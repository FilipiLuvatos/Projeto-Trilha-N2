package trilhhaN2.project.redis;

public class ReadConfigCli {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: ReadConfigCli <namespace> <env> <key>");
            System.exit(1);
        }
        ConfigCache cache = new ConfigCache();
        String val = cache.get(args[0], args[1], args[2]);
        System.out.println(val == null ? "(null)" : val);
    }
}