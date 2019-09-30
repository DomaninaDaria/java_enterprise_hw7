package java_dz7;


import java_dz7.doctor.Doctor;
import java_dz7.doctor.DoctorRepo;
import java_dz7.doctor.Schedule;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    DoctorRepo doctorRepo;

    @Autowired
    PetRepo petRepo;

    @After
    public void cleanUp() {
        doctorRepo.deleteAll();

    }


    @Test
    public void shouldFindAllDoctors() throws Exception {
        doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"),
                Arrays.asList()));
        doctorRepo.save(new Doctor(null, "Vasya", Arrays.asList("doctor2"),
                Arrays.asList()));
        doctorRepo.save(new Doctor(null, "Nikita", Arrays.asList("doctor3"),
                Arrays.asList()));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(fromResource("all-doctors.json"), false))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Kirill")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specializations[0]", Matchers.is("doctor1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is("Vasya")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].specializations[0]", Matchers.is("doctor2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].schedules", Matchers.hasSize(0)));
    }

    @Test
    public void shouldReturnKirillAndVasya() throws Exception {
        doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"),
                Arrays.asList()));
        doctorRepo.save(new Doctor(null, "Vasya", Arrays.asList("doctor2", "doctor1"),
                Arrays.asList()));
        doctorRepo.save(new Doctor(null, "Nikita", Arrays.asList("doctor3"),
                Arrays.asList()));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors")
                .param("specializations", "doctor1", "doctor2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specializations[0]", Matchers.is("doctor1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].specializations[0]", Matchers.is("doctor2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].specializations[1]", Matchers.is("doctor1")));
    }

    @Test
    public void shouldReturnVasya() throws Exception {
        doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"),
                Arrays.asList()));
        doctorRepo.save(new Doctor(null, "Vasya", Arrays.asList("doctor2", "doctor1"),
                Arrays.asList()));
        doctorRepo.save(new Doctor(null, "Nikita", Arrays.asList("doctor3"),
                Arrays.asList()));
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors")
                .param("name", "Vasya")
                .param("specializations", "doctor2", "doctor1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specializations[0]", Matchers.is("doctor2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specializations[1]", Matchers.is("doctor1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Vasya")));
    }

    @Test
    public void shouldUpdateKirill() throws Exception {
        Integer id = doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"), Arrays.asList())).getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/{id}", id)
                .contentType("application/json")
                .content(fromResource("update-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertThat(doctorRepo.findById(id).get().getName()).isEqualTo("Roma");
        Assertions.assertThat(doctorRepo.findById(id).get().getSpecializations().get(0)).isEqualTo("doctor3");
        Assertions.assertThat(doctorRepo.findById(id).get().getSpecializations().get(1)).isEqualTo("doctor1");

    }

    @Test
    public void shouldReturnNotFoundForUpdate() throws Exception {
        Integer id = doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"), Arrays.asList())).getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/{id}", id + 1)
                .contentType("application/json")
                .content(fromResource("update-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    public void shouldReturnNotFoundForDelete() throws Exception {
        Integer id = doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"),
                Arrays.asList())).getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/{id}", id + 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldDeleteNikita() throws Exception {
        doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"),
                Arrays.asList()));
        doctorRepo.save(new Doctor(null, "Vasya", Arrays.asList("doctor2"),
                Arrays.asList()));
        Integer id = doctorRepo.save(new Doctor(null, "Nikita", Arrays.asList("doctor3"),
                Arrays.asList())).getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertThat(doctorRepo.findById(id)).isEmpty();
    }

    @Test
    public void shouldCreateDoctor() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                .contentType("application/json")
                .content(fromResource("create-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(header().string("location", containsString("http://my-doctor.com/doctors/")))
                .andReturn().getResponse();
        Integer id = Integer.parseInt(response.getHeader("location")
                .replace("http://my-doctor.com/doctors/", ""));
        Assertions.assertThat(doctorRepo.findById(id)).isPresent();
    }

    @Test
    public void shouldReturnNotFoundSpecializationForCreateDoctor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                .contentType("application/json")
                .content(fromResource("create-update-wrong-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundSpecializationForUpdateDoctor() throws Exception {
        Integer id = doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"),
                Arrays.asList())).getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/{id}", id)
                .contentType("application/json")
                .content(fromResource("create-update-wrong-doctor.json")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldAddNewSchedule() throws Exception {
        Integer idDoctor = doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"), Arrays.asList())).getId();
        Integer idPet = petRepo.save(new Pet(null, "Tom", 2, "Daria")).getId();
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{id}/schedule/{date}/{sTime}",
                idDoctor, "2020-10-14", "14")
                .contentType("application/json")
                //.content(fromResource("add-schedule.json")))
                .content("{\n" +
                        "  \"id\": " + idPet + "\n" +
                        "}"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertThat(doctorRepo.findById(idDoctor).get()
                .getSchedules().get(0)
                .getLocalDate()).isEqualTo(LocalDate.parse("2020-10-14"));
        Assertions.assertThat(doctorRepo.findById(idDoctor).get()
                .getSchedules().get(0)
                .getScheduleOnDay()).containsEntry(14, idPet);
    }

    @Test
    public void shouldReturnPetNotFoundException() throws Exception {
        Integer idDoctor = doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"), Arrays.asList())).getId();
        Integer idPet = petRepo.save(new Pet(null, "Tom", 2, "Daria")).getId();
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{id}/schedule/{date}/{sTime}",
                idDoctor, "2020-10-14", "14")
                .contentType("application/json")
                //.content(fromResource("add-schedule.json")))
                .content("{\n" +
                        "  \"id\": " + idPet + 1 + "\n" +
                        "}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    public void shouldReturnDoctorNotFoundException() throws Exception {
        Integer idDoctor = doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"), Arrays.asList())).getId();
        Integer idPet = petRepo.save(new Pet(null, "Tom", 2, "Daria")).getId();
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{id}/schedule/{date}/{sTime}",
                idDoctor + 1, "2020-10-14", "14")
                .contentType("application/json")
                //.content(fromResource("add-schedule.json")))
                .content("{\n" +
                        "  \"id\": " + idPet + "\n" +
                        "}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    public void shouldReturnBadRequestForAddingNewScheduleOnDay() throws Exception {
        Integer idPet = petRepo.save(new Pet(null, "Tom", 2, "Daria")).getId();
        Integer idDoctor = doctorRepo.save(new Doctor(null, "Kirill", Arrays.asList("doctor1"),
                Arrays.asList(new Schedule(null, LocalDate.parse("2020-10-14"),
                        new HashMap<>() {{
                            put(14, idPet);
                        }})))).getId();

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{id}/schedule/{date}/{sTime}",
                idDoctor, "2020-10-14", "14")
                .contentType("application/json")
                //.content(fromResource("add-schedule.json")))
                .content("{\n" +
                        "  \"id\": " + idPet + "\n" +
                        "}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
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