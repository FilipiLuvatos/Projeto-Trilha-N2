package trilhaN2.project.redis;


import redis.clients.jedis.JedisPooled;

import java.time.Duration;
import java.util.Objects;

public class ConfigCache {

    private final JedisPooled redis;
    private final int ttlSeconds;

    public ConfigCache() {
        this(RedisClient.get(), Duration.ofHours(6));
    }
    public ConfigCache(JedisPooled redis, Duration ttl) {
        this.redis = Objects.requireNonNull(redis);
        this.ttlSeconds = (int) ttl.toSeconds();
    }

    private String key(String ns, String env, String k) {
        return "cfg:" + ns + ":" + env + ":" + k;
    }

    public void upsert(String ns, String env, String k, String v) {
        String redisKey = key(ns, env, k);
        redis.set(redisKey, v);
        if (ttlSeconds > 0) redis.expire(redisKey, ttlSeconds);
    }

    public void delete(String ns, String env, String k) {
        redis.del(key(ns, env, k));
    }

    public String get(String ns, String env, String k) {
        return redis.get(key(ns, env, k));
    }
}
