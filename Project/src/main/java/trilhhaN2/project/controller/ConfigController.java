package trilhhaN2.project.controller;



import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trilhhaN2.project.model.ConfigEvent;
import trilhhaN2.project.model.UpsertRequest;
import trilhhaN2.project.producer.ConfigEventProducer;
import trilhhaN2.project.service.ConfigQueryService;

import java.time.Instant;

@RestController
@RequestMapping("/configs")
public class ConfigController {

    private final ConfigEventProducer producer;
    private final ConfigQueryService queryService;

    public ConfigController(ConfigEventProducer producer, ConfigQueryService queryService) {
        this.producer = producer;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<?> upsert(@Valid @RequestBody UpsertRequest req) {
        ConfigEvent evt = new ConfigEvent();
        evt.setType(ConfigEvent.Type.UPSERT);
        evt.setNamespace(req.getNamespace());
        evt.setEnv(req.getEnv());
        evt.setKey(req.getKey());
        evt.setValue(req.getValue());
        evt.setVersion(1); // por ora fixo; depois integramos com DB/Service
        evt.setAt(Instant.now());
        evt.setBy(req.getBy() == null ? "api" : req.getBy());

        producer.publish(evt);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{namespace}/{env}/{key}")
    public ResponseEntity<?> delete(@PathVariable String namespace,
                                    @PathVariable String env,
                                    @PathVariable String key,
                                    @RequestParam(defaultValue = "api") String by) {
        ConfigEvent evt = new ConfigEvent();
        evt.setType(ConfigEvent.Type.DELETE);
        evt.setNamespace(namespace);
        evt.setEnv(env);
        evt.setKey(key);
        evt.setValue(null);
        evt.setVersion(1);
        evt.setAt(Instant.now());
        evt.setBy(by);

        producer.publish(evt);
        return ResponseEntity.accepted().build();
    }


    @GetMapping("/{namespace}/{env}/{key}")
    public ResponseEntity<?> getConfig(@PathVariable String namespace,
                                       @PathVariable String env,
                                       @PathVariable String key) {
        String value = queryService.getValue(namespace, env, key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }
}
