package hit.final_project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobCreateDTO {
    private String jobName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String jobType;
    private String sensitiveData;
}
