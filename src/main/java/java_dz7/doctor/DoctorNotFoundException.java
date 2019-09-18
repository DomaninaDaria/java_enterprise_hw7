package java_dz7.doctor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "no such doctor")
public class DoctorNotFoundException extends RuntimeException {
}
