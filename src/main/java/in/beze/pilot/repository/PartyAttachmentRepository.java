package in.beze.pilot.repository;

import in.beze.pilot.domain.PartyAttachment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartyAttachment entity.
 */
@Repository
public interface PartyAttachmentRepository extends JpaRepository<PartyAttachment, Long>, JpaSpecificationExecutor<PartyAttachment> {
    default Optional<PartyAttachment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PartyAttachment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PartyAttachment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select partyAttachment from PartyAttachment partyAttachment left join fetch partyAttachment.party",
        countQuery = "select count(partyAttachment) from PartyAttachment partyAttachment"
    )
    Page<PartyAttachment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select partyAttachment from PartyAttachment partyAttachment left join fetch partyAttachment.party")
    List<PartyAttachment> findAllWithToOneRelationships();

    @Query(
        "select partyAttachment from PartyAttachment partyAttachment left join fetch partyAttachment.party where partyAttachment.id =:id"
    )
    Optional<PartyAttachment> findOneWithToOneRelationships(@Param("id") Long id);
}
