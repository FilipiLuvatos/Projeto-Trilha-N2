package trilhaN2.project.model;

import java.time.Instant;

public class ConfigEvent {
    //Evento para transportar
    //eventos de configuração
    public enum Type { UPSERT, DELETE }

    private Type type;
    private String namespace;
    private String env;
    private String key;
    private String value;     // null se DELETE
    private Integer version;
    private Instant at;
    private String by;

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public String getEnv() { return env; }
    public void setEnv(String env) { this.env = env; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Instant getAt() { return at; }
    public void setAt(Instant at) { this.at = at; }
    public String getBy() { return by; }
    public void setBy(String by) { this.by = by; }

    @Override public String toString() {
        return "ConfigEvent{" +
                "type=" + type +
                ", namespace='" + namespace + '\'' +
                ", env='" + env + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", version=" + version +
                ", at=" + at +
                ", by='" + by + '\'' +
                '}';
    }
}