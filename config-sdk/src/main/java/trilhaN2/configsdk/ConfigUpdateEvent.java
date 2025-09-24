package trilhaN2.configsdk;

import org.springframework.context.ApplicationEvent;

public class ConfigUpdateEvent extends ApplicationEvent {
    public enum Type { UPSERT, DELETE }
    public final String namespace, env, key, value;
    public final Type type;

    public ConfigUpdateEvent(Object src, Type type, String ns, String env, String key, String value) {
        super(src);
        this.type = type; this.namespace = ns; this.env = env; this.key = key; this.value = value;
    }
}
