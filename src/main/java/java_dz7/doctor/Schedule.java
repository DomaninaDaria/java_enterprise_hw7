package java_dz7.doctor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Map;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate localDate;
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "time")
    @Column(name = "petId")
    private Map<Integer, Integer> scheduleOnDay;

    public void addScheduleOnDay(Integer localTime, Integer id) {
        scheduleOnDay.put(localTime, id);
    }
}