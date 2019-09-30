package java_dz7.dto;


import java_dz7.doctor.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ScheduleDtoConverter {
    @Mapping(target = "id", ignore = true)
    Schedule toModel(ScheduleInputDto scheduleInputDto);
}