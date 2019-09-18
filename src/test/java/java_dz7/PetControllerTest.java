package java_dz7;

import java_dz7.pet.Pet;
import java_dz7.pet.PetRepo;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class PetControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PetRepo petRepo;

    @After
    public void cleanUp() {
        petRepo.deleteAll();
    }

    @Test
    public void shouldFindAllPets() throws Exception {
        petRepo.save(new Pet(null, "Tom", 3, "Daria"));
        petRepo.save(new Pet(null, "Jerry", 2, "Katya"));
        petRepo.save(new Pet(null, "Mike", 5, "Vlad"));
        mockMvc.perform(MockMvcRequestBuilders.get("/pets"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(fromResource("all-pets.json"), false))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Tom")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].owner", Matchers.is("Daria")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is("Jerry")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].owner", Matchers.is("Vlad")));
    }

    @Test
    public void shouldUpdateKirill() throws Exception {
        Integer id = petRepo.save(new Pet(null, "Tom", 2, "Katya")).getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/pets/{id}", id)
                .contentType("application/json")
                .content(fromResource("update-pet.json")))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertThat(petRepo.findById(id).get().getName()).isEqualTo("Tom");
        Assertions.assertThat(petRepo.findById(id).get().getOwner()).isEqualTo("Daria");
        Assertions.assertThat(petRepo.findById(id).get().getAge()).isEqualTo(3);

    }

    @Test
    public void shouldReturnNotFoundForUpdate() throws Exception {
        Integer id = petRepo.save(new Pet(null, "Tom", 2, "Katya")).getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/pets/{id}", id + 1)
                .contentType("application/json")
                .content(fromResource("update-pet.json")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundForDelete() throws Exception {
        petRepo.save(new Pet(null, "Tom", 3, "Daria"));
        petRepo.save(new Pet(null, "Jerry", 2, "Katya"));
        petRepo.save(new Pet(null, "Mike", 5, "Vlad"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/pets/{id}", 4))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldDeleteNikita() throws Exception {
        petRepo.save(new Pet(null, "Tom", 3, "Daria"));
        petRepo.save(new Pet(null, "Jerry", 2, "Katya"));
        Integer id = petRepo.save(new Pet(null, "Mike", 5, "Vlad")).getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/pets/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertThat(petRepo.findById(id)).isEmpty();
    }

    @Test
    public void shouldCreateDoctor() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/pets")
                .contentType("application/json")
                .content(fromResource("create-pet.json")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(header().string("location", containsString("http://localhost/pets/")))
                .andReturn().getResponse();
        Integer id = Integer.parseInt(response.getHeader("location")
                .replace("http://localhost/pets/", ""));
        Assertions.assertThat(petRepo.findById(id)).isPresent();
    }

    public String fromResource(String path) {
        try {
            File file = ResourceUtils.getFile("classpath:" + path);
            return Files.readString(file.toPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}