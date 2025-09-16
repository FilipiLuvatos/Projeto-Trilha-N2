package trilhaN2.project.controller;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trilhaN2.project.model.UpsertRequest;
import trilhaN2.project.service.ConfigDomainService;
import trilhaN2.project.service.ConfigQueryService;

import java.security.Principal;

@RestController
@RequestMapping("/configs")
public class ConfigController {

    private final ConfigDomainService configService;  // <-- service de negócio
    private final ConfigQueryService queryService;    // <-- leitura no Redis (GET)

    public ConfigController(ConfigDomainService configService,
                            ConfigQueryService queryService) {
        this.configService = configService;
        this.queryService = queryService;
    }

    // UPSERT (cria/atualiza)
    @PostMapping
    public ResponseEntity<Void> upsert(@Valid @RequestBody UpsertRequest req, Principal principal) {
        String by = principal != null ? principal.getName()
                : (req.getBy() != null ? req.getBy() : "api");
        configService.upsert(req.getNamespace(), req.getEnv(), req.getKey(), req.getValue(), by);
        return ResponseEntity.accepted().build();
    }

    // DELETE
    @DeleteMapping("/{namespace}/{env}/{key}")
    public ResponseEntity<Void> delete(@PathVariable String namespace,
                                       @PathVariable String env,
                                       @PathVariable String key,
                                       Principal principal,
                                       @RequestParam(required = false, defaultValue = "api") String by) {
        String actor = principal != null ? principal.getName() : by;
        configService.delete(namespace, env, key, actor);
        return ResponseEntity.accepted().build();
    }

    // GET por key (lendo do Redis)
    @GetMapping("/{namespace}/{env}/{key}")
    public ResponseEntity<String> getConfig(@PathVariable String namespace,
                                            @PathVariable String env,
                                            @PathVariable String key) {
        String value = queryService.getValue(namespace, env, key);
        return (value == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(value);
    }
}