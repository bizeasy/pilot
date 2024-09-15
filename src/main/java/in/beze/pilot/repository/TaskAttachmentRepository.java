package in.beze.pilot.repository;

import in.beze.pilot.domain.TaskAttachment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TaskAttachment entity.
 */
@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long>, JpaSpecificationExecutor<TaskAttachment> {
    default Optional<TaskAttachment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TaskAttachment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TaskAttachment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select taskAttachment from TaskAttachment taskAttachment left join fetch taskAttachment.task",
        countQuery = "select count(taskAttachment) from TaskAttachment taskAttachment"
    )
    Page<TaskAttachment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select taskAttachment from TaskAttachment taskAttachment left join fetch taskAttachment.task")
    List<TaskAttachment> findAllWithToOneRelationships();

    @Query("select taskAttachment from TaskAttachment taskAttachment left join fetch taskAttachment.task where taskAttachment.id =:id")
    Optional<TaskAttachment> findOneWithToOneRelationships(@Param("id") Long id);
}
