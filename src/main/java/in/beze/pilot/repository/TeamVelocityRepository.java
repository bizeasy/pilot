package in.beze.pilot.repository;

import in.beze.pilot.domain.TeamVelocity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TeamVelocity entity.
 */
@Repository
public interface TeamVelocityRepository extends JpaRepository<TeamVelocity, Long>, JpaSpecificationExecutor<TeamVelocity> {
    default Optional<TeamVelocity> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TeamVelocity> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TeamVelocity> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select teamVelocity from TeamVelocity teamVelocity left join fetch teamVelocity.sprint",
        countQuery = "select count(teamVelocity) from TeamVelocity teamVelocity"
    )
    Page<TeamVelocity> findAllWithToOneRelationships(Pageable pageable);

    @Query("select teamVelocity from TeamVelocity teamVelocity left join fetch teamVelocity.sprint")
    List<TeamVelocity> findAllWithToOneRelationships();

    @Query("select teamVelocity from TeamVelocity teamVelocity left join fetch teamVelocity.sprint where teamVelocity.id =:id")
    Optional<TeamVelocity> findOneWithToOneRelationships(@Param("id") Long id);
}
