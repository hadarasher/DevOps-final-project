package hit.final_project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;


@Configuration
public class DatabaseSeeder {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    private BCryptPasswordEncoder sensitiveDataEncoder = new BCryptPasswordEncoder();


    @Bean
    CommandLineRunner initDatabase(JobRepository jobRepository) {
        return args -> {
            logger.info("Initializing database in runtime...");
            // Examples taken from ChatGPT
            Job job1 = new Job("Build Pipeline", "SUCCESS", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "BUILD",sensitiveDataEncoder.encode("Build secrets"));
            Job job2 = new Job("Deploy to Staging", "FAILED", LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), "DEPLOY",sensitiveDataEncoder.encode("Deploy secrets"));
            Job job3 = new Job("Unit Tests", "SUCCESS", LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), "TEST"); // with no sensitive data

            jobRepository.save(job1);
            logger.info("Created Job: {}", job1);

            jobRepository.save(job2);
            logger.info("Created Job: {}", job2);

            jobRepository.save(job3);
            logger.info("Created Job: {}", job3);

            logger.info("Database seeding completed.");
        };
    }
}