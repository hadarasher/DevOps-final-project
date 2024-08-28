package hit.final_project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Job Entity
 * This controller handles HTTP requests for job interaction
 * and operations
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private static final Logger logger = LoggerFactory.getLogger(JobController.class);
    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Gets all the jobs in the Job table using the JobService
     * @return an HTTP Response including a JSON with an Array of JSONs,
     * representing all the jobs in our database.
     * Successful requests results in status code 200
     */
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody JobCreateDTO jobCreateDTO) {
        Job savedJob = new Job(
                jobCreateDTO.getJobName(),
                jobCreateDTO.getStatus(),
                jobCreateDTO.getCreatedAt(),
                jobCreateDTO.getUpdatedAt(),
                jobCreateDTO.getJobType(),
                jobCreateDTO.getSensitiveData());
        jobService.createJob(savedJob);
        logger.debug(savedJob.toString());
        String location = "/jobs/" + savedJob.getId();
        return ResponseEntity.created(URI.create(location)).body(savedJob);
    }

    // Job performs HTTP GET request in the form of api/jobs/id
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable long id) {
        return jobService.findJobById(id)
                .map(job -> {
                    // Mask sensitive data before sending response
                    job.setSensitiveData(null);
                    return ResponseEntity.ok(job);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable long id, @RequestBody JobCreateDTO jobUpdateDTO) {
        Job job = new Job(
                jobUpdateDTO.getJobName(),
                jobUpdateDTO.getStatus(),
                jobUpdateDTO.getCreatedAt(),
                jobUpdateDTO.getUpdatedAt(),
                jobUpdateDTO.getJobType(),
                jobUpdateDTO.getSensitiveData()
        );
        Job updatedJob = jobService.updateJob(id, job);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable long id) {
        jobService.deleteJob(id);
        // return HTTP Response with empty body and status code 204.
        // add no-content  header to the response
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Job>> getJobsByStatus(@PathVariable String status) {
        List<Job> jobs = jobService.getJobsByStatus(status);
        // Mask sensitive data before sending response
        jobs.forEach(job -> job.setSensitiveData(null));
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobType/{jobType}")
    public ResponseEntity<List<Job>> getJobsByJobType(@PathVariable String jobType){
        List<Job> jobs = jobService.getJobsByJobType(jobType);
        // Mask sensitive data before sending response
        jobs.forEach(job -> job.setSensitiveData(null));
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Job>> getJobsByDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Job> jobs = jobService.getJobsByDateRange(startDate, endDate);
        // Mask sensitive data before sending response
        jobs.forEach(job -> job.setSensitiveData(null));
        return ResponseEntity.ok(jobs);
    }
}
