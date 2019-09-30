package java_dz7.doctor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "pet-clinic.working-hours")
public class WorkingHours {
    private Integer start;
    private Integer end;
}
