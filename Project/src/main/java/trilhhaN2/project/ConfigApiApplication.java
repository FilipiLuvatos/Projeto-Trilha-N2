package trilhhaN2.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class })
public class ConfigApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApiApplication.class, args);
    }
}
