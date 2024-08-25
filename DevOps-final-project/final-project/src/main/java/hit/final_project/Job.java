package hit.final_project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
public class Job {
    @Id @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private UUID uuid;
    private String jobName;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private String jobType;
    private String sensitiveData;

    public Job(String jobName, String status,LocalDateTime createdAt, LocalDateTime updatedAt, String jobType) {
        this(jobName,status,createdAt,updatedAt,jobType,"");
    }

    public Job(String jobName, String status, LocalDateTime createdAt, LocalDateTime updatedAt, String jobType, String sensitiveData) {
        this.uuid = UUID.randomUUID();
        this.jobName = jobName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.jobType = jobType;
        this.sensitiveData = sensitiveData;
    }
}
