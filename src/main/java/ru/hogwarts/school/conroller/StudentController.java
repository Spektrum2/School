package ru.hogwarts.school.conroller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.record.FacultyRecord;
import ru.hogwarts.school.record.StudentRecord;
import ru.hogwarts.school.service.StudentService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public StudentRecord createStudent(@RequestBody StudentRecord studentRecord) {
        return studentService.createStudent(studentRecord);
    }

    @GetMapping
    public Collection<StudentRecord> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("{id}")
    public StudentRecord findStudent(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @GetMapping(params = "age")
    public Collection<StudentRecord> findByAge(@RequestParam Integer age) {
        return studentService.findByAge(age);
    }

    @GetMapping(params = {"min", "max"})
    public Collection<StudentRecord> findByAgeBetween(@RequestParam Integer min,
                                                     @RequestParam Integer max) {
        return studentService.findByAgeBetween(min, max);
    }

    @GetMapping("{id}/faculty")
    public FacultyRecord findFacultyByStudent(@PathVariable Long id) {
        return studentService.findFacultyByStudent(id);
    }

    @GetMapping("/name/{name}")
    public Collection<StudentRecord> getStudentsByName(@PathVariable String name) {
        return studentService.getStudentsByName(name);
    }

    @GetMapping("name")
    public Collection<String> getNamesOfStudentsByLatterA() {
        return studentService.getNamesOfStudentsByLatterA();
    }

    @GetMapping("averageAge")
    public Double getAverageAgeOfStudentsViaStream() {
        return studentService.getAverageAgeOfStudentsViaStream();
    }

    @PutMapping("{id}")
    public StudentRecord editStudent(@PathVariable Long id,
                                     @RequestBody @Valid StudentRecord studentRecord) {
        return studentService.editStudent(id, studentRecord);

    }

    @DeleteMapping("{id}")
    public StudentRecord deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }

    @PatchMapping("{id}/avatar")
    public StudentRecord patchStudentAvatar(@PathVariable Long id,
                                            @RequestParam("avatarId") Long avatarId) {
        return studentService.patchStudentAvatar(id, avatarId);
    }
}
