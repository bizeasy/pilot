package in.beze.pilot.repository;

import in.beze.pilot.domain.TaskLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TaskLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskLogRepository extends JpaRepository<TaskLog, Long>, JpaSpecificationExecutor<TaskLog> {}
