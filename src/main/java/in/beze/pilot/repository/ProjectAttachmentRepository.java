package in.beze.pilot.repository;

import in.beze.pilot.domain.ProjectAttachment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProjectAttachment entity.
 */
@Repository
public interface ProjectAttachmentRepository extends JpaRepository<ProjectAttachment, Long>, JpaSpecificationExecutor<ProjectAttachment> {
    default Optional<ProjectAttachment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProjectAttachment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProjectAttachment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select projectAttachment from ProjectAttachment projectAttachment left join fetch projectAttachment.facility",
        countQuery = "select count(projectAttachment) from ProjectAttachment projectAttachment"
    )
    Page<ProjectAttachment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select projectAttachment from ProjectAttachment projectAttachment left join fetch projectAttachment.facility")
    List<ProjectAttachment> findAllWithToOneRelationships();

    @Query(
        "select projectAttachment from ProjectAttachment projectAttachment left join fetch projectAttachment.facility where projectAttachment.id =:id"
    )
    Optional<ProjectAttachment> findOneWithToOneRelationships(@Param("id") Long id);
}
