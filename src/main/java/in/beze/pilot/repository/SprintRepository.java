package in.beze.pilot.repository;

import in.beze.pilot.domain.Sprint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Sprint entity.
 */
@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long>, JpaSpecificationExecutor<Sprint> {
    default Optional<Sprint> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Sprint> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Sprint> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select sprint from Sprint sprint left join fetch sprint.project left join fetch sprint.status",
        countQuery = "select count(sprint) from Sprint sprint"
    )
    Page<Sprint> findAllWithToOneRelationships(Pageable pageable);

    @Query("select sprint from Sprint sprint left join fetch sprint.project left join fetch sprint.status")
    List<Sprint> findAllWithToOneRelationships();

    @Query("select sprint from Sprint sprint left join fetch sprint.project left join fetch sprint.status where sprint.id =:id")
    Optional<Sprint> findOneWithToOneRelationships(@Param("id") Long id);
}
