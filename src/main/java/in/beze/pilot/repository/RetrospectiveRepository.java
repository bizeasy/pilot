package in.beze.pilot.repository;

import in.beze.pilot.domain.Retrospective;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Retrospective entity.
 */
@Repository
public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long>, JpaSpecificationExecutor<Retrospective> {
    default Optional<Retrospective> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Retrospective> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Retrospective> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select retrospective from Retrospective retrospective left join fetch retrospective.sprint",
        countQuery = "select count(retrospective) from Retrospective retrospective"
    )
    Page<Retrospective> findAllWithToOneRelationships(Pageable pageable);

    @Query("select retrospective from Retrospective retrospective left join fetch retrospective.sprint")
    List<Retrospective> findAllWithToOneRelationships();

    @Query("select retrospective from Retrospective retrospective left join fetch retrospective.sprint where retrospective.id =:id")
    Optional<Retrospective> findOneWithToOneRelationships(@Param("id") Long id);
}
