package trilhaN2.configsdk;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import trilhaN2.configsdk.*;

import java.util.HashMap;
import java.util.Map;

@AutoConfiguration
@EnableConfigurationProperties(ConfigSdkProperties.class)
public class ConfigSdkAutoConfiguration {

    @Bean
    public CacheService cfgCache(ConfigSdkProperties props){ return new CacheService(props); }

    @Bean
    public HttpFallbackClient httpFallback(ConfigSdkProperties props){ return new HttpFallbackClient(props); }

    @Bean
    public ConfigClient configClient(CacheService cache, HttpFallbackClient http, ConfigSdkProperties props){
        return new ConfigClient(cache, http, props);
    }

    @Bean
    public ConfigValueBeanPostProcessor configValueBpp(ConfigClient client, ConfigSdkProperties props){
        return new ConfigValueBeanPostProcessor(client, props);
    }

    // === FACTORIES DO KAFKA (necessárias para o @KafkaListener) ===
    @Bean
    public ConsumerFactory<String, String> configConsumerFactory(ConfigSdkProperties props) {
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
    public ConcurrentKafkaListenerContainerFactory<String, String> configKafkaFactory(
            ConsumerFactory<String, String> configConsumerFactory) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, String>();
        f.setConsumerFactory(configConsumerFactory);
        f.setConcurrency(1);
        return f;
    }

    @Bean
    public KafkaConfigListener kafkaConfigListener(ConfigClient client,
                                                   ApplicationEventPublisher publisher,
                                                   ConfigSdkProperties props) {
        return new KafkaConfigListener(client, publisher, props);
    }
}