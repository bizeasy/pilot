package in.beze.pilot.repository;

import in.beze.pilot.domain.StatusCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StatusCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatusCategoryRepository extends JpaRepository<StatusCategory, Long>, JpaSpecificationExecutor<StatusCategory> {}
