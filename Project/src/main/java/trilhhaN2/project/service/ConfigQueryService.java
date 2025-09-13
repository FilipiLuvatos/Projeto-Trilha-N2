package trilhhaN2.project.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class ConfigQueryService {

    private static final String PREFIX = "cfg:";

    public String getValue(String namespace, String env, String key) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            return jedis.get(PREFIX + namespace + ":" + env + ":" + key);
        }
    }
}
