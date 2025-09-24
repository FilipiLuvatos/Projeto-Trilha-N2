package controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import trilhaN2.configsdk.ConfigClient;
import trilhaN2.configsdk.ConfigValue;

@RestController
public class DemoController {

    @ConfigValue("greeting.message")
    private String greeting;   // será preenchido no boot e auto-atualiza via Kafka

    private final ConfigClient client;

    public DemoController(ConfigClient client) { this.client = client; }

    @GetMapping("/demo/message")
    public String message() {
        return greeting != null ? greeting : "(sem valor)";
    }

    @GetMapping("/demo/direct")
    public String direct() {
        return client.get("greeting.message"); // fallback HTTP + cache local
    }
}
