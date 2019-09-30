package java_dz7.doctor;


import java_dz7.pet.Pet;
import java_dz7.SpecializationNotFoundException;
import java_dz7.SpecializationsList;
import java_dz7.dto.DoctorDtoConverter;
import java_dz7.dto.DoctorInputOutputDto;
import java_dz7.dto.ScheduleDtoConverter;
import java_dz7.dto.ScheduleInputDto;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
public class DoctorController {
    private final SpecializationsList specializationsList;
    private final DoctorService doctorService;
    private final WorkingHours workingHours;
    private final DoctorDtoConverter doctorDtoConverter = Mappers.getMapper(DoctorDtoConverter.class);
    private final UriComponentsBuilder uriComponentsBuilder;
    private final ScheduleDtoConverter scheduleDtoConverter = Mappers.getMapper(ScheduleDtoConverter.class);

    public DoctorController(DoctorService doctorService, WorkingHours workingHours,
                            @Value("${pet-clinic.doctors.host-name:localhost}") String hostName,
                            SpecializationsList specializationsList) {
        this.doctorService = doctorService;
        this.workingHours = workingHours;
        this.specializationsList = specializationsList;

        uriComponentsBuilder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(hostName)
                .path("/doctors/{id}");
    }

    @GetMapping("/doctors/{id}")
    public DoctorInputOutputDto findById(@PathVariable Integer id) {
        return doctorService.findById(id)
                .map(doctorDtoConverter::toDto)
                .orElseThrow(DoctorNotFoundException::new);
    }


    @GetMapping("/doctors")
    public List<DoctorInputOutputDto> findAll(@RequestParam Optional<List<String>> specializations,
                                              @RequestParam Optional<String> name) {
        List<DoctorInputOutputDto> doctors = doctorService.findAll(specializations, name)
                .stream()
                .map(doctorDtoConverter::toDto)
                .collect(Collectors.toList());
        return doctors;

    }


    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctor(@RequestBody DoctorInputOutputDto dto) {
        Doctor doctor = doctorDtoConverter.toModel(dto);
        doctor.setSchedules(new ArrayList<>());
        boolean checkSp;

        for (int i = 0; i < doctor.getSpecializations().size(); i++) {
            checkSp = specializationsList.getSpecializations().contains(doctor.getSpecializations().get(i));
            if (!checkSp) {
                throw new SpecializationNotFoundException((specializationsList.getSpecializations().toString()));
            }
        }
        doctorService.createDoctor(doctor);
        URI uri = uriComponentsBuilder.build(doctor.getId());
        return ResponseEntity.created(uri).build();
    }


    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctor(@RequestBody DoctorInputOutputDto dto,
                                          @PathVariable Integer id) {

        Doctor doctor = doctorDtoConverter.toModel(dto, id);
        boolean checkSp;

        for (int i = 0; i < doctor.getSpecializations().size(); i++) {
            checkSp = specializationsList.getSpecializations().contains(doctor.getSpecializations().get(i));
            if (!checkSp) {
                throw new SpecializationNotFoundException((specializationsList.getSpecializations().toString()));
            }
        }
        doctorService.updateDoctor(doctor);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer id) {

        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/doctors/{id}/schedule/{date}/{time}")
    public ResponseEntity<?> createSchedule(@RequestBody Pet pet, @PathVariable Integer id,
                                            @PathVariable String date, @PathVariable Integer time) {
        Optional<Doctor> doctor = doctorService.findById(id);
        if (doctor.isPresent()) {

            doctorService.isPetExist(pet.getId());
            LocalDate newDate = LocalDate.parse(date);

            if (workingHours.getStart() > time || time > workingHours.getEnd()) {
                return ResponseEntity.badRequest().body("wrong time, doctors are working from "
                        + workingHours.getStart() + "AM to " + workingHours.getEnd() + "PM");
            }
            if (newDate.isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body("wrong date");
            }
            if (newDate.isEqual(LocalDate.now()) && time < LocalTime.now().getHour()) {
                return ResponseEntity.badRequest().body("wrong time");
            }


            List<Schedule> schedules = doctor.get().getSchedules();

            Optional<Schedule> mayBeSchedule = schedules
                    .stream()
                    .filter(sch -> sch.getLocalDate().equals(newDate)).findFirst();

            if (mayBeSchedule.isEmpty()) {
                Map<Integer, Integer> timePetId = new HashMap<>();
                timePetId.put(time, pet.getId());
                Schedule newSchedule = scheduleDtoConverter.toModel(new ScheduleInputDto(newDate, timePetId));
                schedules.add(newSchedule);
                Doctor updatedDoctor = new Doctor(id, doctor.get().getName(), doctor.get().getSpecializations(), schedules);
                doctorService.updateDoctor(updatedDoctor);
            } else {
                Schedule schedule = mayBeSchedule.get();
                int i = schedules.indexOf(schedule);
                boolean isTimeBusy = schedule.getScheduleOnDay().containsKey(time);
                if (isTimeBusy) {
                    return ResponseEntity.badRequest().body("this time is busy");
                } else {
                    schedule.addScheduleOnDay(time, pet.getId());
                    schedules.set(i, schedule);
                    Doctor updatedDoctor = new Doctor(id, doctor.get().getName(), doctor.get().getSpecializations(), schedules);
                    doctorService.updateDoctor(updatedDoctor);
                }
            }
        } else {
            throw new DoctorNotFoundException();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctors/{id}/schedule/{date}")
    public Map<Integer, Integer> findScheduleOfDoctorOnDay(@PathVariable Integer id, @PathVariable String date) {

        LocalDate newDate = LocalDate.parse(date);
        Optional<Doctor> mayBeDoctor = doctorService.findById(id);
        if (mayBeDoctor.isPresent()) {
            Optional<Schedule> mayBeSchedule = mayBeDoctor.get().getSchedules()
                    .stream()
                    .filter(sch -> sch.getLocalDate().equals(newDate)).findFirst();
            if (mayBeSchedule.isPresent()) {
                return mayBeSchedule.get().getScheduleOnDay();
            } else {
                return new HashMap<>();
            }
        } else {
            throw new DoctorNotFoundException();
        }
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noSuchDoctorOrSpecialization(DoctorNotFoundException e1, SpecializationNotFoundException e2) {
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void parseException(DateTimeParseException e3) {
    }
}