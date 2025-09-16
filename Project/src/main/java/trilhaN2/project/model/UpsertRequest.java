package trilhaN2.project.model;

import jakarta.validation.constraints.NotBlank;

public class UpsertRequest {
    @NotBlank private String namespace;
    @NotBlank private String env;
    @NotBlank private String key;
    @NotBlank private String value;
    private String by; // opcional

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public String getEnv() { return env; }
    public void setEnv(String env) { this.env = env; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getBy() { return by; }
    public void setBy(String by) { this.by = by; }
}