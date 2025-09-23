package trilhaN2.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trilhaN2.project.model.ConfigEvent;
import trilhaN2.project.model.ConfigHistory;
import trilhaN2.project.model.ConfigItem;
import trilhaN2.project.producer.ConfigEventProducer;
import trilhaN2.project.repository.ConfigHistoryRepository;
import trilhaN2.project.repository.ConfigItemRepository;

import java.time.Instant;

@Service
public class ConfigDomainService {

    private final ConfigItemRepository items;
    private final ConfigHistoryRepository history;
    private final ConfigEventProducer producer;

    public ConfigDomainService(ConfigItemRepository items,
                               ConfigHistoryRepository history,
                               ConfigEventProducer producer) {
        this.items = items;
        this.history = history;
        this.producer = producer;
    }

    @Transactional
    public void upsert(String ns, String env, String key, String value, String by) {
        var existing = items.findByNamespaceAndEnvAndKey(ns, env, key);
        ConfigItem item = existing.orElseGet(() -> {
            ConfigItem ci = new ConfigItem();
            ci.setNamespace(ns);
            ci.setEnv(env);
            ci.setKey(key);
            // base de versão: pega a maior do histórico, caso já tenha existido
            int base = history.findMaxVersion(ns, env, key);
            ci.setVersion(base); // somaremos +1 abaixo
            return ci;
        });

        // No-op se o valor não mudou
        if (existing.isPresent()
                && ((item.getValue() == null && value == null)
                || (item.getValue() != null && item.getValue().equals(value)))) {
            return;
        }

        String old = item.getValue();
        int next = (item.getVersion() == null ? 0 : item.getVersion()) + 1;

        item.setValue(value);
        item.setVersion(next);
        item.setUpdatedBy(by == null ? "api" : by);
        item.setUpdatedAt(Instant.now());
        items.save(item);

        // histórico
        ConfigHistory h = new ConfigHistory();
        h.setItemId(item.getId());
        h.setNamespace(ns);
        h.setEnv(env);
        h.setKeyName(key);
        h.setOldValue(old);
        h.setNewValue(value);
        h.setVersion(next);
        h.setChangedBy(by == null ? "api" : by);
        h.setChangedAt(Instant.now());
        h.setAction("UPSERT");
        history.save(h);

        // evento para o consumidor/Redis
        ConfigEvent evt = new ConfigEvent();
        evt.setType(ConfigEvent.Type.UPSERT);
        evt.setNamespace(ns);
        evt.setEnv(env);
        evt.setKey(key);
        evt.setValue(value);
        evt.setVersion(next);
        evt.setAt(Instant.now());
        evt.setBy(by == null ? "api" : by);
        producer.publish(evt);
    }

    @Transactional
    public void delete(String ns, String env, String key, String by) {
        var opt = items.findByNamespaceAndEnvAndKey(ns, env, key);
        if (opt.isEmpty()) return;

        ConfigItem item = opt.get();
        int next = (item.getVersion() == null ? 0 : item.getVersion()) + 1;

        // histórico
        ConfigHistory h = new ConfigHistory();
        h.setItemId(item.getId());
        h.setNamespace(ns);
        h.setEnv(env);
        h.setKeyName(key);
        h.setOldValue(item.getValue());
        h.setNewValue(null);
        h.setVersion(next);
        h.setChangedBy(by == null ? "api" : by);
        h.setChangedAt(Instant.now());
        h.setAction("DELETE");
        history.save(h);

        // remove do atual
        items.delete(item);

        // evento
        ConfigEvent evt = new ConfigEvent();
        evt.setType(ConfigEvent.Type.DELETE);
        evt.setNamespace(ns);
        evt.setEnv(env);
        evt.setKey(key);
        evt.setValue(null);
        evt.setVersion(next);
        evt.setAt(Instant.now());
        evt.setBy(by == null ? "api" : by);
        producer.publish(evt);
    }

    // útil para GET /configs/{ns}/{env} vindo do DB
    public java.util.List<ConfigItem> list(String ns, String env) {
        return items.findByNamespaceAndEnv(ns, env);
    }
}
