package java_dz7;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Data
@ConfigurationProperties(prefix = "pet-clinic.doctors")
public class SpecializationsList {
    final List<String> specializations;
}
