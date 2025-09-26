package trilhaN2.project;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import trilhaN2.configsdk.ConfigClient;
import trilhaN2.configsdk.KafkaConfigListener;
import trilhaN2.configsdk.ConfigSdkProperties;

import static org.mockito.Mockito.*;

public class KafkaConfigListenerTest {

    @Test
    void onMessage_withExtraFields_appliesUpsert() {
        var client = mock(ConfigClient.class);
        var pub = mock(ApplicationEventPublisher.class);
        var props = new ConfigSdkProperties();

        var listener = new KafkaConfigListener(client, pub, props);
        String payload = """
          {"type":"UPSERT","namespace":"billing","env":"dev","key":"greeting.message","value":"X","version":2,"at":"2025-01-01T00:00:00Z","by":"admin"}
        """;
        listener.onMessage(payload);

        verify(client).applyUpsert("billing","dev","greeting.message","X");
        verify(pub, atLeastOnce()).publishEvent(any());
    }
}
