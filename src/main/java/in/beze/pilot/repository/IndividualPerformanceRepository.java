package in.beze.pilot.repository;

import in.beze.pilot.domain.IndividualPerformance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the IndividualPerformance entity.
 */
@Repository
public interface IndividualPerformanceRepository
    extends JpaRepository<IndividualPerformance, Long>, JpaSpecificationExecutor<IndividualPerformance> {
    default Optional<IndividualPerformance> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<IndividualPerformance> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<IndividualPerformance> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select individualPerformance from IndividualPerformance individualPerformance left join fetch individualPerformance.party left join fetch individualPerformance.sprint",
        countQuery = "select count(individualPerformance) from IndividualPerformance individualPerformance"
    )
    Page<IndividualPerformance> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select individualPerformance from IndividualPerformance individualPerformance left join fetch individualPerformance.party left join fetch individualPerformance.sprint"
    )
    List<IndividualPerformance> findAllWithToOneRelationships();

    @Query(
        "select individualPerformance from IndividualPerformance individualPerformance left join fetch individualPerformance.party left join fetch individualPerformance.sprint where individualPerformance.id =:id"
    )
    Optional<IndividualPerformance> findOneWithToOneRelationships(@Param("id") Long id);
}
