package java_dz7.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleInputDto {
    private LocalDate localDate;
    private Map<Integer, Integer> scheduleOnDay;
}