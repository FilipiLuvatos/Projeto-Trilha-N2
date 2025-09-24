package trilhaN2.configsdk;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;

public class CacheService {
    private final Cache<String, String> cache;

    public CacheService(ConfigSdkProperties props) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(props.getCacheTtlSeconds()))
                .maximumSize(10_000)
                .build();
    }
    public String get(String fullKey) { return cache.getIfPresent(fullKey); }
    public void put(String fullKey, String value) { cache.put(fullKey, value); }
    public void invalidate(String fullKey) { cache.invalidate(fullKey); }
}
