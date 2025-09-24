package trilhaN2.configsdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

public class KafkaConfigListener {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ConfigClient client;
    private final ApplicationEventPublisher publisher;
    private final ConfigSdkProperties props;

    public KafkaConfigListener(ConfigClient client, ApplicationEventPublisher publisher, ConfigSdkProperties props) {
        this.client = client; this.publisher = publisher; this.props = props;
    }

    @Bean
    public ConsumerFactory<String, String> configConsumerFactory() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.GROUP_ID_CONFIG, props.getGroupId());
        cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        cfg.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return new DefaultKafkaConsumerFactory<>(cfg);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> configKafkaFactory() {
        var f = new ConcurrentKafkaListenerContainerFactory<String, String>();
        f.setConsumerFactory(configConsumerFactory());
        f.setConcurrency(1);
        return f;
    }

    static class EventDTO {
        public String type, namespace, env, key, value;
        public Integer version;
    }

    @KafkaListener(topics = "#{@configSdkProperties.topic}", containerFactory = "configKafkaFactory")
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
        } catch (Exception ignore) { /* log se quiser */ }
    }

    @Bean
    public ConfigSdkProperties configSdkProperties(ConfigSdkProperties p){ return p; }
}
