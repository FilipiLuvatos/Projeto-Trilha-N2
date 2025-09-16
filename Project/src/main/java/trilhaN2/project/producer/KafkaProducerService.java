package trilhaN2.project.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import trilhaN2.project.model.ConfigEvent;
import trilhaN2.project.util.Json;

import java.time.Instant;
import java.util.Properties;

public class KafkaProducerService {
    public static void main(String[] args) throws Exception {
        String topic = "config.events";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // boas práticas:
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            // Exemplo: UPSERT de greeting.message
            ConfigEvent evt = new ConfigEvent();
            evt.setType(ConfigEvent.Type.UPSERT);
            evt.setNamespace("billing");
            evt.setEnv("dev");
            evt.setKey("greeting.message");
            evt.setValue("Olá do Kafka via JSON!");
            evt.setVersion(1);
            evt.setAt(Instant.now());
            evt.setBy("admin");

            String partitionKey = evt.getNamespace() + ":" + evt.getEnv(); // chave de partição por ns/env
            String payload = Json.MAPPER.writeValueAsString(evt);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, partitionKey, payload);

            producer.send(record, (metadata, ex) -> {
                if (ex != null) {
                    System.err.println("Falha ao enviar: " + ex.getMessage());
                } else {
                    System.out.printf("Enviado para %s-%d@%d%n",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });

            producer.flush();
            System.out.println("Mensagem JSON enviada para o Kafka.");
        }
    }
}

