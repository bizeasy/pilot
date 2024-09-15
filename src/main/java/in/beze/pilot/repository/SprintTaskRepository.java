package in.beze.pilot.repository;

import in.beze.pilot.domain.SprintTask;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SprintTask entity.
 */
@Repository
public interface SprintTaskRepository extends JpaRepository<SprintTask, Long>, JpaSpecificationExecutor<SprintTask> {
    default Optional<SprintTask> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SprintTask> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SprintTask> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select sprintTask from SprintTask sprintTask left join fetch sprintTask.task left join fetch sprintTask.sprint left join fetch sprintTask.assignedTo left join fetch sprintTask.assignedBy left join fetch sprintTask.qa left join fetch sprintTask.reviewedBy left join fetch sprintTask.status",
        countQuery = "select count(sprintTask) from SprintTask sprintTask"
    )
    Page<SprintTask> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select sprintTask from SprintTask sprintTask left join fetch sprintTask.task left join fetch sprintTask.sprint left join fetch sprintTask.assignedTo left join fetch sprintTask.assignedBy left join fetch sprintTask.qa left join fetch sprintTask.reviewedBy left join fetch sprintTask.status"
    )
    List<SprintTask> findAllWithToOneRelationships();

    @Query(
        "select sprintTask from SprintTask sprintTask left join fetch sprintTask.task left join fetch sprintTask.sprint left join fetch sprintTask.assignedTo left join fetch sprintTask.assignedBy left join fetch sprintTask.qa left join fetch sprintTask.reviewedBy left join fetch sprintTask.status where sprintTask.id =:id"
    )
    Optional<SprintTask> findOneWithToOneRelationships(@Param("id") Long id);
}
