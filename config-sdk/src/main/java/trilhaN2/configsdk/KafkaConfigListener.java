package trilhaN2.configsdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;

public class KafkaConfigListener {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ConfigClient client;
    private final ApplicationEventPublisher publisher;

    public KafkaConfigListener(ConfigClient client, ApplicationEventPublisher publisher, ConfigSdkProperties props) {
        this.client = client; this.publisher = publisher;
    }

    static class EventDTO { public String type, namespace, env, key, value; public Integer version; }

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
            }
        } catch (Exception e) {
            // log opcional
        }
    }
}
