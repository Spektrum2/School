package ru.hogwarts.school.conroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.entity.AverageAgeOfStudents;
import ru.hogwarts.school.entity.LastFiveStudents;
import ru.hogwarts.school.entity.NumberOfStudents;
import ru.hogwarts.school.entity.StudentView;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
public class InfoByStudentsController {
    private final StudentService studentService;

    public InfoByStudentsController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/number-of-students")
    public NumberOfStudents getNumberOfStudents() {
        return studentService.getNumberOfStudents();
    }

    @GetMapping("/average-age-of-students")
    public AverageAgeOfStudents getAverageAgeOfStudents() {
        return studentService.getAverageAgeOfStudents();
    }

    @GetMapping("last-five-students")
    public Collection<StudentView> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }
}
