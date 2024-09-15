package in.beze.pilot.repository;

import in.beze.pilot.domain.SprintAttachment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SprintAttachment entity.
 */
@Repository
public interface SprintAttachmentRepository extends JpaRepository<SprintAttachment, Long> {
    default Optional<SprintAttachment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SprintAttachment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SprintAttachment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select sprintAttachment from SprintAttachment sprintAttachment left join fetch sprintAttachment.sprint",
        countQuery = "select count(sprintAttachment) from SprintAttachment sprintAttachment"
    )
    Page<SprintAttachment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select sprintAttachment from SprintAttachment sprintAttachment left join fetch sprintAttachment.sprint")
    List<SprintAttachment> findAllWithToOneRelationships();

    @Query(
        "select sprintAttachment from SprintAttachment sprintAttachment left join fetch sprintAttachment.sprint where sprintAttachment.id =:id"
    )
    Optional<SprintAttachment> findOneWithToOneRelationships(@Param("id") Long id);
}
