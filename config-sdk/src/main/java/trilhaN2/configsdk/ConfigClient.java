package trilhaN2.configsdk;

public class ConfigClient {

    private final CacheService cache;
    private final HttpFallbackClient http;
    private final ConfigSdkProperties props;

    public ConfigClient(CacheService cache, HttpFallbackClient http, ConfigSdkProperties props) {
        this.cache = cache; this.http = http; this.props = props;
    }

    private String fk(String ns, String env, String key) { return ns + ":" + env + ":" + key; }

    public String get(String key) {
        String ns = props.getNamespace(), env = props.getEnv();
        String fullKey = fk(ns, env, key);
        String v = cache.get(fullKey);
        if (v != null) return v;
        v = http.get(ns, env, key);
        if (v != null) cache.put(fullKey, v);
        return v;
    }

    public void applyUpsert(String ns, String env, String key, String value) {
        if (!ns.equals(props.getNamespace()) || !env.equals(props.getEnv())) return;
        cache.put(fk(ns, env, key), value);
    }

    public void applyDelete(String ns, String env, String key) {
        if (!ns.equals(props.getNamespace()) || !env.equals(props.getEnv())) return;
        cache.invalidate(fk(ns, env, key));
    }
}
