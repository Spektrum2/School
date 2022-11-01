package ru.hogwarts.school.conroller;

import org.springframework.web.bind.annotation.*;

import ru.hogwarts.school.record.FacultyRecord;
import ru.hogwarts.school.record.StudentRecord;
import ru.hogwarts.school.service.FacultyService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public FacultyRecord createFaculty(@RequestBody @Valid FacultyRecord facultyRecord) {
        return facultyService.createFaculty(facultyRecord);
    }

    @GetMapping
    public Collection<FacultyRecord> getAllFaculty() {
        return facultyService.getAllFaculty();
    }

    @GetMapping("{id}")
    public FacultyRecord findFaculty(@PathVariable Long id) {
       return facultyService.findFaculty(id);
    }

    @GetMapping(params = "color")
    public Collection<FacultyRecord> findByColor(@RequestParam String color) {
        return facultyService.findByColor(color);
    }

    @GetMapping("/{id}/students")
    public Collection<StudentRecord> findStudentsByFaculty(@PathVariable Long id) {
        return facultyService.findStudentsByFaculty(id);
    }

    @GetMapping(params = {"nameOrColor"})
    public Collection<FacultyRecord> findByNameOrColor(@RequestParam String nameOrColor) {
        return facultyService.findByNameOrColor(nameOrColor);
    }

    @GetMapping("theLongestName")
    public String getTheLongestNameOfFaculty() {
        return facultyService.getTheLongestNameOfFaculty();
    }

    @PutMapping({"{id}"})
    public FacultyRecord editFaculty(@PathVariable Long id,
                                     @RequestBody @Valid FacultyRecord facultyRecord) {
        return facultyService.editFaculty(id, facultyRecord);
    }

    @DeleteMapping("{id}")
    public FacultyRecord deleteFaculty(@PathVariable Long id) {
        return facultyService.deleteFaculty(id);
    }
}
