package trilhhaN2.project.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import trilhhaN2.project.model.ConfigEvent;

@Component
public class ConfigEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ConfigEventProducer.class);

    private final KafkaTemplate<String, String> template;
    private final ObjectMapper om;
    private final String topic;

    // app.kafka.topic tem default "config.events" caso não esteja no application.yml
    public ConfigEventProducer(KafkaTemplate<String, String> template,
                               ObjectMapper om,
                               @Value("${app.kafka.topic:config.events}") String topic) {
        this.template = template;
        this.om = om;
        this.topic = topic;
    }

    public void publish(ConfigEvent evt) {
        try {
            String key = evt.getNamespace() + ":" + evt.getEnv(); // mantém ordenação por ns/env
            String payload = om.writeValueAsString(evt);

            var record = new ProducerRecord<>(topic, key, payload);

            // Spring Kafka (Boot 3+) retorna CompletableFuture
            template.send(record).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Falha ao publicar evento no Kafka (topic={}, key={}): {}",
                            topic, key, ex.getMessage(), ex);
                } else if (result != null) {
                    var md = result.getRecordMetadata();
                    log.info("Evento publicado: topic={} partition={} offset={} key={}",
                            md.topic(), md.partition(), md.offset(), key);
                } else {
                    log.warn("Envio retornou nulo (topic={}, key={})", topic, key);
                }
            });

        } catch (Exception e) {
            // Erro de serialização normalmente cai aqui
            throw new RuntimeException("Falha ao serializar/publicar ConfigEvent", e);
        }
    }
}
