package trilhaN2.configsdk;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

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

    @Bean
    public KafkaConfigListener kafkaConfigListener(ConfigClient client,
                                                   ApplicationEventPublisher publisher,
                                                   ConfigSdkProperties props) {
        return new KafkaConfigListener(client, publisher, props);
    }
}
