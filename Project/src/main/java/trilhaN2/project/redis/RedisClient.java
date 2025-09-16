package trilhaN2.project.redis;

import redis.clients.jedis.JedisPooled;


//Conexão Unica e simples
public final class RedisClient {
    private static JedisPooled INSTANCE;

    public static synchronized JedisPooled get() {
        if (INSTANCE == null) {
            // Permite configurar via env vars ou defaults locais
            String host = System.getenv().getOrDefault("REDIS_HOST", "localhost");
            int port = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));
            String pass = System.getenv().getOrDefault("REDIS_PASS", "");
            if (pass.isBlank()) {
                INSTANCE = new JedisPooled(host, port);
            } else {
                INSTANCE = new JedisPooled(host, port, Boolean.parseBoolean(pass));
            }
        }
        return INSTANCE;
    }

    private RedisClient() {}
}
