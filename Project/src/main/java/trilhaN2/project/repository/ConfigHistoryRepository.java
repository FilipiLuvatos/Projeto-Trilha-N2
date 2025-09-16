package trilhaN2.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trilhaN2.project.model.ConfigHistory;

import java.util.List;

public interface ConfigHistoryRepository extends JpaRepository<ConfigHistory, Long> {

    List<ConfigHistory> findByItemIdOrderByVersionDesc(Long itemId);

    @Query("select coalesce(max(h.version),0) from ConfigHistory h " +
            "where h.namespace=:ns and h.env=:env and h.keyName=:key")
    int findMaxVersion(@Param("ns") String ns, @Param("env") String env, @Param("key") String key);
}