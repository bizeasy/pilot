package in.beze.pilot.repository;

import in.beze.pilot.domain.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Status entity.
 */
@Repository
public interface StatusRepository extends JpaRepository<Status, Long>, JpaSpecificationExecutor<Status> {
    default Optional<Status> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Status> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Status> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select status from Status status left join fetch status.category",
        countQuery = "select count(status) from Status status"
    )
    Page<Status> findAllWithToOneRelationships(Pageable pageable);

    @Query("select status from Status status left join fetch status.category")
    List<Status> findAllWithToOneRelationships();

    @Query("select status from Status status left join fetch status.category where status.id =:id")
    Optional<Status> findOneWithToOneRelationships(@Param("id") Long id);
}
