package trilhaN2.configsdk;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

public class HttpFallbackClient {
    private final RestClient rest;

    public HttpFallbackClient(ConfigSdkProperties props) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) props.getHttpTimeoutMs());
        factory.setReadTimeout((int) props.getHttpTimeoutMs());

        this.rest = RestClient.builder()
                .baseUrl(props.getApiBaseUrl())
                .defaultHeaders(h -> h.setBasicAuth(props.getApiUsername(), props.getApiPassword()))
                .requestFactory(factory)
                .build();
    }

    public String get(String namespace, String env, String key) {
        return rest.get()
                .uri("/configs/{ns}/{env}/{key}", namespace, env, key)
                .retrieve()
                .body(String.class);
    }
}
