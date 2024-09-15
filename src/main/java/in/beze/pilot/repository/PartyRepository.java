package in.beze.pilot.repository;

import in.beze.pilot.domain.Party;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Party entity.
 */
@Repository
public interface PartyRepository extends JpaRepository<Party, Long>, JpaSpecificationExecutor<Party> {
    default Optional<Party> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Party> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Party> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select party from Party party left join fetch party.user left join fetch party.status left join fetch party.partyType",
        countQuery = "select count(party) from Party party"
    )
    Page<Party> findAllWithToOneRelationships(Pageable pageable);

    @Query("select party from Party party left join fetch party.user left join fetch party.status left join fetch party.partyType")
    List<Party> findAllWithToOneRelationships();

    @Query(
        "select party from Party party left join fetch party.user left join fetch party.status left join fetch party.partyType where party.id =:id"
    )
    Optional<Party> findOneWithToOneRelationships(@Param("id") Long id);
}
