package ru.hogwarts.school.conroller;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.record.FacultyRecord;
import ru.hogwarts.school.record.StudentRecord;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private final Faker faker = new Faker();

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    public void createTest() {
        addStudent(generateStudent(addFaculty(generateFaculty())));
    }

    @Test
    public void putTest() {
        FacultyRecord facultyRecord1 = addFaculty(generateFaculty());
        FacultyRecord facultyRecord2 = addFaculty(generateFaculty());
        StudentRecord studentRecord = addStudent(generateStudent(facultyRecord1));

        ResponseEntity<StudentRecord> getRecordResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + studentRecord.getId(), StudentRecord.class);
        assertThat(getRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRecordResponseEntity.getBody()).isNotNull();
        assertThat(getRecordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(studentRecord);
        assertThat(getRecordResponseEntity.getBody().getFaculty()).usingRecursiveComparison().isEqualTo(facultyRecord1);

        studentRecord.setFaculty(facultyRecord2);

        ResponseEntity<StudentRecord> recordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/student/" + studentRecord.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(studentRecord),
                StudentRecord.class
        );

        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody()).isNotNull();
        assertThat(recordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(studentRecord);
        assertThat(recordResponseEntity.getBody().getFaculty()).usingRecursiveComparison().isEqualTo(facultyRecord2);
    }

    @Test
    public void findTests() {
        List<FacultyRecord> facultyRecords = Stream.generate(this::generateFaculty)
                .limit(4)
                .map(this::addFaculty)
                .toList();
        List<StudentRecord> studentRecords = Stream.generate(() -> generateStudent(facultyRecords.get(faker.random().nextInt(facultyRecords.size()))))
                .limit(50)
                .map(this::addStudent)
                .toList();
        StudentRecord studentRecord = studentRecords.get(0);

        int min = 18;
        int max = 23;
        int age = 20;

        List<StudentRecord> expectedStudents1 = studentRecords.stream()
                .filter(s -> s.getAge() >= min && s.getAge() <= max)
                .toList();
        List<StudentRecord> expectedStudents2 = studentRecords.stream()
                .filter(s -> s.getAge() == age)
                .toList();
        List<StudentRecord> expectedStudents3 = studentRecords.stream()
                .filter(s -> s.getName().equals(studentRecord.getName()))
                .toList();
        ResponseEntity<List<StudentRecord>> getRecordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/student?min={min}&max={max}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                min,
                max);
        assertThat(getRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRecordResponseEntity.getBody())
                .hasSize(expectedStudents1.size())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedStudents1);

        ResponseEntity<List<StudentRecord>> getAllRecordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        assertThat(getAllRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAllRecordResponseEntity.getBody())
                .hasSize(studentRecords.size())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(studentRecords);

        ResponseEntity<List<StudentRecord>> getAgeRecordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/student?age={age}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                age);

        assertThat(getAgeRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAgeRecordResponseEntity.getBody())
                .hasSize(expectedStudents2.size())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedStudents2);

        ResponseEntity<FacultyRecord> getFacultyRecordResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/student/"+ studentRecord.getId() + "/faculty", FacultyRecord.class);

        assertThat(getFacultyRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getFacultyRecordResponseEntity.getBody()).isNotNull();
        assertThat(getFacultyRecordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(studentRecord.getFaculty());

        ResponseEntity<List<StudentRecord>> getNameRecordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/student/name/" + studentRecord.getName(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        assertThat(getNameRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getNameRecordResponseEntity.getBody())
                .hasSize(expectedStudents3.size())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedStudents3);
    }


    private StudentRecord generateStudent(FacultyRecord facultyRecord) {
        StudentRecord studentRecord = new StudentRecord();
        studentRecord.setName(faker.harryPotter().character());
        studentRecord.setAge(faker.random().nextInt(17, 25));
        if (facultyRecord != null) {
            studentRecord.setFaculty(facultyRecord);
        }
        return studentRecord;
    }

    private FacultyRecord generateFaculty() {
        FacultyRecord facultyRecord = new FacultyRecord();
        facultyRecord.setName(faker.harryPotter().house());
        facultyRecord.setColor(faker.color().name());
        return facultyRecord;
    }

    private FacultyRecord addFaculty(FacultyRecord facultyRecord) {
        ResponseEntity<FacultyRecord> facultyRecordResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/faculty", facultyRecord, FacultyRecord.class);
        assertThat(facultyRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyRecordResponseEntity.getBody()).isNotNull();
        assertThat(facultyRecordResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(facultyRecord);
        assertThat(facultyRecordResponseEntity.getBody().getId()).isNotNull();

        return facultyRecordResponseEntity.getBody();
    }

    private StudentRecord addStudent(StudentRecord studentRecord) {
        ResponseEntity<StudentRecord> studentRecordResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/student", studentRecord, StudentRecord.class);
        assertThat(studentRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studentRecordResponseEntity.getBody()).isNotNull();
        assertThat(studentRecordResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(studentRecord);
        assertThat(studentRecordResponseEntity.getBody().getId()).isNotNull();

        return studentRecordResponseEntity.getBody();

    }


}