package in.beze.pilot.repository;

import in.beze.pilot.domain.SprintTaskHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SprintTaskHistory entity.
 */
@Repository
public interface SprintTaskHistoryRepository extends JpaRepository<SprintTaskHistory, Long>, JpaSpecificationExecutor<SprintTaskHistory> {
    default Optional<SprintTaskHistory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SprintTaskHistory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SprintTaskHistory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select sprintTaskHistory from SprintTaskHistory sprintTaskHistory left join fetch sprintTaskHistory.fromStatus left join fetch sprintTaskHistory.toStatus",
        countQuery = "select count(sprintTaskHistory) from SprintTaskHistory sprintTaskHistory"
    )
    Page<SprintTaskHistory> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select sprintTaskHistory from SprintTaskHistory sprintTaskHistory left join fetch sprintTaskHistory.fromStatus left join fetch sprintTaskHistory.toStatus"
    )
    List<SprintTaskHistory> findAllWithToOneRelationships();

    @Query(
        "select sprintTaskHistory from SprintTaskHistory sprintTaskHistory left join fetch sprintTaskHistory.fromStatus left join fetch sprintTaskHistory.toStatus where sprintTaskHistory.id =:id"
    )
    Optional<SprintTaskHistory> findOneWithToOneRelationships(@Param("id") Long id);
}
