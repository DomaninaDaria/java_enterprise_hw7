package java_dz7.dto;

import java_dz7.pet.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PetDtoConverter {
    @Mapping(target = "id", ignore = true)
    Pet toModel(PetInputOutputDto petInputDto);

    Pet toModel(PetInputOutputDto dto, Integer id);

    @Mapping(target = "name")
    @Mapping(target = "age")
    @Mapping(target = "owner")
    PetInputOutputDto toDto(Pet pet);
}