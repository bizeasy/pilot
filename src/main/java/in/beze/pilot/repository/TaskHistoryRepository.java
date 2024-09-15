package in.beze.pilot.repository;

import in.beze.pilot.domain.TaskHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TaskHistory entity.
 */
@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long>, JpaSpecificationExecutor<TaskHistory> {
    default Optional<TaskHistory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TaskHistory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TaskHistory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select taskHistory from TaskHistory taskHistory left join fetch taskHistory.assignedTo left join fetch taskHistory.sprint left join fetch taskHistory.assignedBy",
        countQuery = "select count(taskHistory) from TaskHistory taskHistory"
    )
    Page<TaskHistory> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select taskHistory from TaskHistory taskHistory left join fetch taskHistory.assignedTo left join fetch taskHistory.sprint left join fetch taskHistory.assignedBy"
    )
    List<TaskHistory> findAllWithToOneRelationships();

    @Query(
        "select taskHistory from TaskHistory taskHistory left join fetch taskHistory.assignedTo left join fetch taskHistory.sprint left join fetch taskHistory.assignedBy where taskHistory.id =:id"
    )
    Optional<TaskHistory> findOneWithToOneRelationships(@Param("id") Long id);
}
