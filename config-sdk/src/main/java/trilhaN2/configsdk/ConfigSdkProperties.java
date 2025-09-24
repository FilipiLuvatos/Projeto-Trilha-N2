package trilhaN2.configsdk;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config.sdk")
public class ConfigSdkProperties {
    private String namespace = "default";
    private String env = "dev";

    private String topic = "config.events";
    private String bootstrapServers = "localhost:9092";
    private String groupId = "config-sdk-clients";

    private String apiBaseUrl = "http://localhost:8081";
    private String apiUsername = "admin";
    private String apiPassword = "admin123";
    private long httpTimeoutMs = 2000;

    private long cacheTtlSeconds = 300;

    // getters/setters
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public String getEnv() { return env; }
    public void setEnv(String env) { this.env = env; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getApiBaseUrl() { return apiBaseUrl; }
    public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }
    public String getApiUsername() { return apiUsername; }
    public void setApiUsername(String apiUsername) { this.apiUsername = apiUsername; }
    public String getApiPassword() { return apiPassword; }
    public void setApiPassword(String apiPassword) { this.apiPassword = apiPassword; }
    public long getHttpTimeoutMs() { return httpTimeoutMs; }
    public void setHttpTimeoutMs(long httpTimeoutMs) { this.httpTimeoutMs = httpTimeoutMs; }
    public long getCacheTtlSeconds() { return cacheTtlSeconds; }
    public void setCacheTtlSeconds(long cacheTtlSeconds) { this.cacheTtlSeconds = cacheTtlSeconds; }
}
