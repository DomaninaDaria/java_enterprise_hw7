package java_dz7.doctor;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> specializations;
    @JoinColumn(name = "doctor_id")
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)

    private List<Schedule> schedules;
}