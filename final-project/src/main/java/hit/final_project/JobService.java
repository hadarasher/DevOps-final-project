package hit.final_project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.prefs.Preferences.MAX_NAME_LENGTH;

@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);
    private final JobRepository jobRepository;
    private final BCryptPasswordEncoder sensitiveDataEncoder;

    /**
     * constructor-based Dependency Injection (DI)
     * @param jobRepository
     */
    @Autowired
    public JobService(JobRepository jobRepository, BCryptPasswordEncoder sensitiveDataEncoder) {
        this.jobRepository = jobRepository;
        this.sensitiveDataEncoder = sensitiveDataEncoder;
    }

    /**
     * Retrieves all jobs
     * @return list of jobs
     */
    public List<Job> getAllJobs() {
        logger.debug("Fetching all jobs");
        List<Job> allJobs = jobRepository.findAll();
        logger.debug("Found {} jobs", allJobs.size());
        logger.debug(allJobs.toString());
//        return jobRepository.findAll();
        return allJobs;
    }

    /**
     * Saves a new job
     * @param job
     * @return saved job if successful
     */
    public Job createJob(Job job) {

        if (job.getJobName() == null || job.getStatus() == null || job.getCreatedAt() == null || job.getUpdatedAt() == null || job.getJobType() == null ||job.getSensitiveData() == null) {
            throw new IllegalArgumentException("Job details cannot be null");
        }

        if (job.getJobName().trim().isEmpty()) {
            throw new RuntimeException("Job name cannot be empty");
        }

        if (job.getCreatedAt().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Dates cannot be in the future");
        }

        logger.debug("sensitive data: {}", job.getSensitiveData());
        String encryptedData = sensitiveDataEncoder.encode(job.getSensitiveData());
        job.setSensitiveData(encryptedData);
        return jobRepository.save(job);
    }



    public Optional<Job> findJobById(Long id){
        logger.debug("Finding job with ID: {}", id);
        return jobRepository.findById(id);
    }

    /**
     * Update a possibly existing Job
     * @param id Id of the job to update
     * @param jobDetails all details from JSON in the request
     * @return updated job Entity
     * @throws RuntimeException id id is not in database
     */
    public Job updateJob(Long id, Job jobDetails) {
        logger.debug("updating job with id: {}",id);
        Job job = jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        logger.debug("Found job: {}", job.toString());
        StringBuilder errorMessages = new StringBuilder();

        if (jobDetails.getJobName() == null || jobDetails.getJobName().trim().isEmpty()) {
            errorMessages.append("Job name cannot be empty");
        }
        if (jobDetails.getStatus() == null) {
            if (errorMessages.length() > 0) {
                errorMessages.append(" and ");
            }
            errorMessages.append("Status cannot be null");
        }
        logger.debug(jobDetails.getJobName());
        if (jobDetails.getJobName().length() > MAX_NAME_LENGTH) {
            throw new RuntimeException("Job name exceeds maximum length");
        }

        if (errorMessages.length() > 0) {
            throw new RuntimeException(errorMessages.toString());
        }

        job.setJobName(jobDetails.getJobName());
        job.setStatus(jobDetails.getStatus());
        job.setCreatedAt(jobDetails.getCreatedAt());
        job.setUpdatedAt(jobDetails.getUpdatedAt());

        return jobRepository.save(job);
    }

    public void deleteJob(Long id){
        logger.debug("Deleting job with ID: {}", id);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        jobRepository.delete(job);
        logger.info("Job deleted with ID: {}", id);
    }

    public List<Job> getJobsByStatus(String status) {
        logger.debug("Fetching jobs with status: {}", status);
        return jobRepository.findByStatus(status);
    }

    public List<Job> getJobsByJobType(String jobType) {
        logger.debug("Fetching jobs with job type: {}", jobType);
        return jobRepository.findByJobType(jobType);
    }

    public List<Job> getJobsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Fetching jobs between {} and {}", startDate, endDate);
        return jobRepository.findByDateRange(startDate, endDate);
    }

}
