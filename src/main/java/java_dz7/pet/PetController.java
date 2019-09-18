package java_dz7.pet;


import java_dz7.dto.PetDtoConverter;
import java_dz7.dto.PetInputOutputDto;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class PetController {
    private final PetService petService;
    private final PetDtoConverter petDtoConverter = Mappers.getMapper(PetDtoConverter.class);
    private final UriComponentsBuilder uriComponentsBuilder =
            UriComponentsBuilder.newInstance().scheme("http")
                    .host("localhost")
                    .path("/pets/{id}");

    @GetMapping("/pets/{id}")
    public PetInputOutputDto findById(@PathVariable Integer id) {
        return petService.findById(id)
                .map(petDtoConverter::toDto)
                .orElseThrow(PetNotFoundException::new);
    }


    @GetMapping("/pets")
    public List<PetInputOutputDto> findAll() {
        List<PetInputOutputDto> pets = petService.findAll().stream()
                .map(petDtoConverter::toDto)
                .collect(Collectors.toList());
        return pets;

    }


    @PostMapping("/pets")
    public ResponseEntity<?> createPet(@RequestBody PetInputOutputDto dto) {
        Pet pet = petDtoConverter.toModel(dto);
        petService.save(pet);
        URI uri = uriComponentsBuilder.build(pet.getId());
        return ResponseEntity.created(uri).build();

    }


    @PutMapping("/pets/{id}")
    public ResponseEntity<?> updatePet(@RequestBody PetInputOutputDto dto,
                                       @PathVariable Integer id) {

        Pet pet = petDtoConverter.toModel(dto, id);
        petService.updatePet(pet);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/pets/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer id) {

        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noSuchDoctorOrSpecialization(PetNotFoundException e1) {
    }
}
