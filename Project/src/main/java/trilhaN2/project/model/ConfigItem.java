package trilhaN2.project.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "config_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"namespace","env","key_name"}))
public class ConfigItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String namespace;
    private String env;

    @Column(name = "key_name")
    private String key;

    @Column(columnDefinition = "TEXT")
    private String value;

    private Integer version;

    private String updatedBy;
    private Instant updatedAt;

    // getters/setters
    public Long getId() { return id; }
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
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
