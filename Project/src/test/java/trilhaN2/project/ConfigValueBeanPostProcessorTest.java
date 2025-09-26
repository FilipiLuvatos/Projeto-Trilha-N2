package trilhaN2.project;

import org.junit.jupiter.api.Test;
import trilhaN2.configsdk.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigValueBeanPostProcessorTest {

    static class BeanDemo {
        @ConfigValue("greeting.message")
        String greeting;
    }

    @Test
    void injectsInitialValueFromClientGet() {
        var cache = mock(CacheService.class);
        var http  = mock(HttpFallbackClient.class);
        var props = new ConfigSdkProperties(); props.setNamespace("billing"); props.setEnv("dev");
        var client = new ConfigClient(cache, http, props);

        when(http.get("billing","dev","greeting.message")).thenReturn("Olá v1!");

        var bpp = new ConfigValueBeanPostProcessor(client, props);
        var bean = new BeanDemo();
        bpp.postProcessBeforeInitialization(bean, "beanDemo");

        assertEquals("Olá v1!", bean.greeting);
    }
}
