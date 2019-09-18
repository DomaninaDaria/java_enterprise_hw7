package java_dz7.dto;


import java_dz7.doctor.Schedule;
import lombok.Data;

import java.util.List;

@Data
public class DoctorInputOutputDto {
    private String name;
    private List<String> specializations;
    private List<Schedule> schedules;

}