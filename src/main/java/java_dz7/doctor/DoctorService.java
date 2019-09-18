package java_dz7.doctor;


import java_dz7.pet.Pet;
import java_dz7.pet.PetNotFoundException;
import java_dz7.pet.PetService;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DoctorService {
    private DoctorRepo doctorRepo;
    private PetService petService;

    public List<Doctor> findAll(Optional<List<String>> specializations, Optional<String> name) {
        if (specializations.isPresent() && name.isPresent()) {
            return doctorRepo.findDistinctBySpecializationsInAndNameIgnoreCase(specializations.get(), name.get());
        }
        if (specializations.isPresent()) {
            return doctorRepo.findDistinctBySpecializationsIn(specializations.get());
        }
        if (name.isPresent()) {
            return doctorRepo.findByNameIgnoreCase(name.get());
        }
        return doctorRepo.findAll();
    }

    public Optional<Doctor> findById(Integer id) {
        return doctorRepo.findById(id);
    }

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepo.save(doctor);
    }

    public void updateDoctor(Doctor doctor) {
        if (doctorRepo.findById(doctor.getId()).isPresent()) {
            doctorRepo.save(doctor);
        } else {
            throw new DoctorNotFoundException();
        }
    }

    public void deleteDoctor(Integer id) {
        try {
            doctorRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DoctorNotFoundException();
        }
    }

    public void isPetExist(Integer id) {
        Optional<Pet> mayBePet = petService.findById(id);
        if (!mayBePet.isPresent()) {
            throw new PetNotFoundException();
        }
    }
}