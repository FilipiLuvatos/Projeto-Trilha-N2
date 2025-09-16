package trilhaN2.project.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "config_history")
public class ConfigHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long itemId;

    @Column(nullable = false) private String namespace;
    @Column(nullable = false) private String env;
    @Column(nullable = false) private String keyName;

    @Column(columnDefinition = "TEXT") private String oldValue;
    @Column(columnDefinition = "TEXT") private String newValue;

    @Column(nullable = false) private Integer version;
    private String changedBy;
    @Column(nullable = false) private Instant changedAt;
    @Column(nullable = false) private String action; // "UPSERT" / "DELETE"

    // getters/setters
    public Long getId() { return id; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public String getEnv() { return env; }
    public void setEnv(String env) { this.env = env; }
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public Instant getChangedAt() { return changedAt; }
    public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}