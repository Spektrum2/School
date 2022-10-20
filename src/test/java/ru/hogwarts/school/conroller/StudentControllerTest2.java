package ru.hogwarts.school.conroller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.record.FacultyRecord;
import ru.hogwarts.school.record.StudentRecord;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StudentControllerTest2 {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

//    @BeforeEach
////    public void SetUp() {
//        Faculty faculty = new Faculty();
//        faculty.setColor("Красный");
//        faculty.setName("Гриффиндор");
//        facultyRepository.save(faculty);
//
//        Student harry = new Student();
//        harry.setAge(22);
//        harry.setName("Гарри Поттер");
//        harry.setFaculty(faculty);
//
//        Student ron = new Student();
//        ron.setAge(23);
//        ron.setName("Рон Уизли");
//        ron.setFaculty(faculty);
//
//        Student hermione = new Student();
//        hermione.setAge(23);
//        hermione.setName("Гермиона Грейнджер");
//        hermione.setFaculty(faculty);
//
//        studentRepository.save(harry);
//        studentRepository.save(ron);
//        studentRepository.save(hermione);
//
//
//    }
//
//    @Test
//    public void createStudentTest() {
////        Faculty faculty = new Faculty();
////        faculty.setId(1);
////        faculty.setColor("Красный");
////        faculty.setName("Гриффиндор");
////
////        Student harry = new Student();
////        harry.setId(1);
////        harry.setAge(22);
////        harry.setName("Гарри Поттер");
////        harry.setFaculty(faculty);
////
////        when(facultyRepository.findById(any())).thenReturn(Optional.of(faculty));
////        when(studentRepository.save(any())).thenReturn(harry);
//
//        FacultyRecord facultyRecord = new FacultyRecord();
//        facultyRecord.setColor("Черный");
//        facultyRecord.setName("Пуффендуй");
//
//        StudentRecord studentRecord = new StudentRecord();
//        studentRecord.setAge(19);
//        studentRecord.setName("Драко Блэк");
//        studentRecord.setFaculty(facultyRecord);
//        ResponseEntity<StudentRecord> recordResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/student", studentRecord, StudentRecord.class);
//        Faculty faculty = facultyRepository.findAll().stream()
//                .filter(f -> f.getName().equals("Пуффендуй") && f.getColor().equals("Черный"))
//                .findFirst()
//                .orElseThrow();
//        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(recordResponseEntity.getBody()).isNotNull();
//        assertThat(recordResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("id", "faculty_id").isEqualTo(studentRecord);
//        assertThat(recordResponseEntity.getBody().getFaculty().getId()).isEqualTo(faculty.getId());
//
//        ResponseEntity<StudentRecord> recordDelete = testRestTemplate.exchange("http://localhost:" + port + "/student", studentRecord, StudentRecord.class);

//    }

}