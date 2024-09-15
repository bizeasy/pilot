package in.beze.pilot.repository;

import in.beze.pilot.domain.PartyType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartyType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartyTypeRepository extends JpaRepository<PartyType, Long>, JpaSpecificationExecutor<PartyType> {}
