package hit.final_project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FinalProjectApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(FinalProjectApplicationTests.class);

	@Autowired
	private JobService jobService;

	@Autowired
	private JobRepository jobRepo;

	@BeforeEach
	void setUp() {
		jobRepo.deleteAll();
	}

	@Nested
	class CreateJobTests {

		@Test
		void testCreateJobSuccessfully() {
			logger.info("Running testCreateJobSuccessfully");
			Job job = new Job("Test Job", "PENDING", LocalDateTime.now(), LocalDateTime.now(), "BUILD", "sensitiveData");
			Job savedJob = jobService.createJob(job);

			assertNotNull(savedJob.getId());
			assertEquals("Test Job", savedJob.getJobName());
			assertEquals("PENDING", savedJob.getStatus());
			assertNotNull(savedJob.getCreatedAt());
			assertNotNull(savedJob.getUpdatedAt());
			assertNotNull(savedJob.getSensitiveData());
		}

		@Test
		void testCreateJobWithNullInputs() {
			logger.info("Running testCreateJobWithNullInputs");
			Job job = new Job(null, null, null, null, null);

			// Ensure that creating a job with null inputs does not create a job
			try {
				jobService.createJob(job);
				// If no exception is thrown, fail the test
				fail("Expected IllegalArgumentException to be thrown");
			} catch (IllegalArgumentException e) {
				assertEquals("Job details cannot be null", e.getMessage());
			}
		}


		@ParameterizedTest
		@ValueSource(strings = {"SUCCESS", "FAILED", "PENDING"})
		void testAddJobWithVariousStatuses(String status) {
			logger.info("Running testCreateJobWithVariousStatuses with status: {}", status);
			Job job = new Job("Test Job", status, LocalDateTime.now(), LocalDateTime.now(), "BUILD");
			Job savedJob = jobService.createJob(job);
			assertEquals(status, savedJob.getStatus());
		}

		@ParameterizedTest
		@ValueSource(strings = {"BUILD", "DEPLOY", "TEST"})
		void testCreateJobWithValidJobTypes(String validJobType) {
			logger.info("Running testCreateJobWithValidJobTypes with jobType: {}", validJobType);
			Job job = new Job("Deploy Application", "SUCCESS", LocalDateTime.now(), LocalDateTime.now(), validJobType, "sensitive data");
			Job savedJob = jobService.createJob(job);
			assertEquals(validJobType, savedJob.getJobType());
		}

		@ParameterizedTest
		@ValueSource(strings = {"2023-01-01T00:00:00", "2024-12-31T23:59:59", "2050-01-01T00:00:00"})
		void testCreateJobWithVariousCreatedAtDates(String dateTime) {
			logger.info("Running testCreateJobWithVariousCreatedAtDates with dateTime: {}", dateTime);
			LocalDateTime createdAt = LocalDateTime.parse(dateTime);
			Job job = new Job("Build Pipeline", "SUCCESS", createdAt, LocalDateTime.now(), "BUILD", "sensitive data");

			if (createdAt.isAfter(LocalDateTime.now())) {
				// Expecting an exception if the createdAt date is in the future
				Exception exception = assertThrows(RuntimeException.class, () -> jobService.createJob(job));
				assertEquals("Dates cannot be in the future", exception.getMessage());
			} else {
				// No exception expected if the date is valid
				Job savedJob = jobService.createJob(job);
				assertEquals(createdAt, savedJob.getCreatedAt());
			}
		}

	}

	@Nested
	class UpdateJobTests {

		@Test
		void testUpdateJobSuccessfully() {
			logger.info("Running testUpdateJobSuccessfully");
			Job job = jobService.createJob(new Job("Build Pipeline", "PENDING", LocalDateTime.now(), LocalDateTime.now(), "BUILD"));
			job.setStatus("SUCCESS");
			Job updatedJob = jobService.updateJob(job.getId(), job);
			assertEquals("SUCCESS", updatedJob.getStatus());
		}

		@ParameterizedTest
		@ValueSource(strings = {"", " ", "Valid Job Name", "VeryVeryLongJobNameThatExceedsTheExpectedLengthLimit"})
		void testUpdateJobWithVariousJobNames(String jobName) {
			logger.info("Running testUpdateJobWithVariousJobNames with jobName: {}", jobName);
			Job originalJob = jobService.createJob(new Job("Original Job", "PENDING", LocalDateTime.now(), LocalDateTime.now(), "BUILD", "sensitive data"));

			if (jobName.isBlank()) {
				RuntimeException exception = assertThrows(RuntimeException.class, () -> jobService.updateJob(originalJob.getId(),
						new Job(jobName, "PENDING", LocalDateTime.now(), LocalDateTime.now(), "BUILD", "sensitive data")));
				assertEquals("Job name cannot be empty", exception.getMessage(), "Exception message does not match");
			} else {
				Job updatedJob = jobService.updateJob(originalJob.getId(),
						new Job(jobName, "SUCCESS", LocalDateTime.now(), LocalDateTime.now(), "BUILD", "sensitive data"));

				assertEquals(jobName, updatedJob.getJobName(), "Job name was not updated correctly");
			}
		}


		@Test
		void testUpdateJobWithInvalidData() {
			logger.info("Running testUpdateJobWithInvalidData");
			Job job = jobService.createJob(new Job("Original Job", "PENDING", LocalDateTime.now(), LocalDateTime.now(), "BUILD"));

			// Attempt to update with invalid data
			job.setJobName(""); // Invalid job name
			job.setStatus(null); // Null status

			// Check for exceptions or invalid states
			RuntimeException exception = assertThrows(RuntimeException.class, () -> jobService.updateJob(job.getId(), job));
			assertEquals("Job name cannot be empty and Status cannot be null", exception.getMessage());
		}
	}

	@Nested
	class DeleteJobTests {

		@Test
		void testDeleteJobSuccessfully() {
			logger.info("Running testDeleteJobSuccessfully");
			Job job = jobService.createJob(new Job("Test Job", "PENDING", LocalDateTime.now(), LocalDateTime.now(), "BUILD"));
			jobService.deleteJob(job.getId());
			Optional<Job> foundJob = jobService.findJobById(job.getId());
			assertFalse(foundJob.isPresent());
		}

		@Test
		void testDeleteNonExistentJob() {
			logger.info("Running testDeleteNonExistentJob");

			assertThrows(RuntimeException.class, () -> jobService.deleteJob(-1L),
					"Expected RuntimeException to be thrown when deleting a non-existent job");		}
	}

	@Nested
	class FindJobTests {

		@Test
		void testGetJobById() {
			logger.info("Running testGetJobById");
			Job job = new Job("Test Job", "PENDING", LocalDateTime.now(), LocalDateTime.now(), "BUILD", "sensitiveData");
			Job savedJob = jobService.createJob(job);

			Optional<Job> foundJob = jobService.findJobById(savedJob.getId());

			assertTrue(foundJob.isPresent());
			assertEquals("Test Job", foundJob.get().getJobName());
		}

		@Test
		void testGetJobByInvalidId() {
			logger.info("Running testGetJobByInvalidId");
			Optional<Job> job = jobService.findJobById(-1L);
			assertFalse(job.isPresent(), "Job should not be found for an invalid ID");
		}
	}

	@Nested
	class IntegrationTests {

		@Test
		void testJobServiceIntegration() {
			logger.info("Running testJobServiceIntegration");
			// Create a job and verify the complete interaction from creation to retrieval
			Job job = new Job("Integration Test Job", "PENDING", LocalDateTime.now(), LocalDateTime.now(), "BUILD", "sensitiveData");
			Job savedJob = jobService.createJob(job);

			// Retrieve the job and assert its properties
			Optional<Job> retrievedJob = jobService.findJobById(savedJob.getId());
			assertTrue(retrievedJob.isPresent());
			assertEquals(savedJob.getId(), retrievedJob.get().getId());
			assertEquals("Integration Test Job", retrievedJob.get().getJobName());
		}
	}

}
