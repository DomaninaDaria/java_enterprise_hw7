package java_dz7.pet;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PetService {
    private PetRepo petRepo;

    public List<Pet> findAll() {
        return petRepo.findAll();
    }

    public Optional<Pet> findById(Integer id) {
        return petRepo.findById(id);
    }

    public void save(Pet pet) {
        petRepo.save(pet);
    }

    public void updatePet(Pet pet) {
        if (petRepo.findById(pet.getId()).isPresent()) {
            petRepo.save(pet);
        } else {
            throw new PetNotFoundException();
        }
    }

    public void deletePet(Integer id) {
        try {
            petRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new PetNotFoundException();
        }
    }
}