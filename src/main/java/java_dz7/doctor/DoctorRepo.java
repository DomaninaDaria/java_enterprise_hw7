package java_dz7.doctor;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DoctorRepo extends JpaRepository<Doctor, Integer> {

    List<Doctor> findDistinctBySpecializationsInAndNameIgnoreCase(List<String> specializations, String name);

    List<Doctor> findDistinctBySpecializationsIn(List<String> specializations);

    List<Doctor> findByNameIgnoreCase(String name);
}