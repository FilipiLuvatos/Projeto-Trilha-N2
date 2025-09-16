package trilhaN2.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trilhaN2.project.model.ConfigItem;

import java.util.List;
import java.util.Optional;

public interface ConfigItemRepository extends JpaRepository<ConfigItem, Long> {
    Optional<ConfigItem> findByNamespaceAndEnvAndKey(String namespace, String env, String key);
    List<ConfigItem> findByNamespaceAndEnv(String namespace, String env);
}