package com.projeto.consumidor.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerService {
    public static void main(String[] args) {
        // Configurações do Kafka Consumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Endereço do broker Kafka
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group"); // Grupo de consumidores
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        try {
            // Inscreve o consumidor no tópico "test-topic"
            consumer.subscribe(Collections.singletonList("test-topic"));

            while (true) {
                // Consome as mensagens
                var records = consumer.poll(1000); // Espera por 1 segundo
                records.forEach(record -> {
                    System.out.println("Consumido: " + record.value());
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
