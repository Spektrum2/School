package ru.hogwarts.school.conroller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.entity.AverageAgeOfStudents;
import ru.hogwarts.school.entity.NumberOfStudents;
import ru.hogwarts.school.record.StudentRecord;
import ru.hogwarts.school.service.StudentService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
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
    public List<StudentRecord> getLastFiveStudents(@RequestParam @Min(1) @Max(10) Integer count) {
        return studentService.getLastStudents(count);
    }
}
