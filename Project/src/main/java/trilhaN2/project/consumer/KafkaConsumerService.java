package trilhaN2.project.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import trilhaN2.project.model.ConfigEvent;
import trilhaN2.project.redis.ConfigCache;
import trilhaN2.project.util.Json;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaConsumerService {
    public static void main(String[] args) {
        String topic = "config.events";
        AtomicBoolean running = new AtomicBoolean(true);
        ConfigCache cache = new ConfigCache();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "config-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                consumer.wakeup();
            }));

            consumer.subscribe(Collections.singletonList(topic));

            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> rec : records) {
                    try {
                        ConfigEvent evt = Json.MAPPER.readValue(rec.value(), ConfigEvent.class);

                        // Log básico
                        System.out.printf("Evento [%s] %s.%s %s=%s (v%s) key=%s part=%d off=%d%n",
                                evt.getType(), evt.getNamespace(), evt.getEnv(),
                                evt.getKey(), evt.getValue(), evt.getVersion(),
                                rec.key(), rec.partition(), rec.offset());

                        // Persistência no Redis
                        switch (evt.getType()) {
                            case UPSERT -> cache.upsert(evt.getNamespace(), evt.getEnv(), evt.getKey(), evt.getValue());
                            case DELETE -> cache.delete(evt.getNamespace(), evt.getEnv(), evt.getKey());
                        }

                    } catch (Exception parseEx) {
                        System.err.println("JSON inválido: " + parseEx.getMessage());
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Consumidor finalizado.");
    }
}
