package java_dz7.pet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "no such pet is registered")
public class PetNotFoundException extends RuntimeException {
}
