package trilhaN2.project.service;

import org.springframework.stereotype.Service;
import trilhaN2.project.model.ConfigEvent;
import trilhaN2.project.producer.ConfigEventProducer;

import java.time.Instant;

@Service
public class ConfigDomainService {

    private final ConfigEventProducer producer;

    public ConfigDomainService(ConfigEventProducer producer) {
        this.producer = producer;
    }

    public void upsert(String ns, String env, String key, String value, String by) {
        ConfigEvent evt = new ConfigEvent();
        evt.setType(ConfigEvent.Type.UPSERT);
        evt.setNamespace(ns);
        evt.setEnv(env);
        evt.setKey(key);
        evt.setValue(value);
        evt.setVersion(1); // quando ligar o JPA, incrementamos direitinho
        evt.setAt(Instant.now());
        evt.setBy(by == null ? "api" : by);
        producer.publish(evt);
    }

    public void delete(String ns, String env, String key, String by) {
        ConfigEvent evt = new ConfigEvent();
        evt.setType(ConfigEvent.Type.DELETE);
        evt.setNamespace(ns);
        evt.setEnv(env);
        evt.setKey(key);
        evt.setValue(null);
        evt.setVersion(1);
        evt.setAt(Instant.now());
        evt.setBy(by == null ? "api" : by);
        producer.publish(evt);
    }
}