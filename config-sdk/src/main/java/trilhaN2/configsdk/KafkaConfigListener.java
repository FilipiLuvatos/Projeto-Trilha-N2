package trilhaN2.configsdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;

public class KafkaConfigListener {

    // ObjectMapper "tolerante" a campos extras (at, by, etc.)
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final ConfigClient client;
    private final ApplicationEventPublisher publisher;

    public KafkaConfigListener(ConfigClient client, ApplicationEventPublisher publisher, ConfigSdkProperties props) {
        this.client = client; this.publisher = publisher;
    }

    // DTO tolerante a desconhecidos
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class EventDTO {
        public String type, namespace, env, key, value;
        public Integer version;
        // se no futuro quiser usar at/by, adicione aqui:
        // public String at, by;
    }

    @KafkaListener(topics = "${config.sdk.topic}", containerFactory = "configKafkaFactory")
    public void onMessage(String payload) {
        try {
            EventDTO ev = mapper.readValue(payload, EventDTO.class);
            if ("UPSERT".equalsIgnoreCase(ev.type)) {
                client.applyUpsert(ev.namespace, ev.env, ev.key, ev.value);
                publisher.publishEvent(new ConfigUpdateEvent(this,
                        ConfigUpdateEvent.Type.UPSERT, ev.namespace, ev.env, ev.key, ev.value));
            } else if ("DELETE".equalsIgnoreCase(ev.type)) {
                client.applyDelete(ev.namespace, ev.env, ev.key);
                publisher.publishEvent(new ConfigUpdateEvent(this,
                        ConfigUpdateEvent.Type.DELETE, ev.namespace, ev.env, ev.key, null));
            } else {
                System.out.println("[config-sdk] Ignorando evento type=" + ev.type + " payload=" + payload);
            }
        } catch (Exception e) {
            System.err.println("[config-sdk] Falha ao parsear evento: " + e.getMessage() + " payload=" + payload);
        }
    }
}
