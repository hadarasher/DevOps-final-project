package hit.final_project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(String status);
    List<Job> findByJobType(String jobType);
    @Query("SELECT j FROM Job j WHERE j.createdAt BETWEEN :startDate AND :endDate")
    List<Job> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
