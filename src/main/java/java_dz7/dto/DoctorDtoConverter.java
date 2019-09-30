package java_dz7.dto;

import java_dz7.doctor.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface DoctorDtoConverter {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    Doctor toModel(DoctorInputOutputDto doctorInputDto);

    Doctor toModel(DoctorInputOutputDto dto, Integer id);


    @Mapping(target = "specializations")
    @Mapping(target = "name")
    @Mapping(target = "schedules")
    DoctorInputOutputDto toDto(Doctor doctor);
}