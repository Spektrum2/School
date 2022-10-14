package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.conroller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SchoolApplicationForStudentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
        assertThat(studentController).isNotNull();
    }

    @Test
    public void getAllStudentsTest() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student", String.class)).isNotNull();
    }

    @Test
    public void findStudentTest() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student/3", Student.class)).isNotNull();
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student/90", Student.class)).isEqualTo(null);
    }

    @Test
    public void findByAgeTest() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student?age=25", List.class)).isNotNull();
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student?age=100", List.class)).isEmpty();
    }

    @Test
    public void findByAgeBetweenTest() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student?min=10&max=20", List.class)).isNotNull();
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student?min=50&max=100", List.class)).isEmpty();
    }

    @Test
    public void findFacultyByStudentTest() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student/8/faculty", Faculty.class)).isNotNull();
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student/100/faculty", Faculty.class)).isEqualTo(null);
    }

    @Test
    public void createAndDeleteStudentTest() {
        Student student = new Student();
        student.setName("Мария");
        student.setAge(25);
        assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/student", student, Student.class)).isNotNull();
        Student student1 = studentController.getAllStudents().stream()
                .filter(s -> s.getName().equals("Мария") && s.getAge() == 25)
                .findFirst()
                .orElseThrow();
        this.restTemplate.delete("http://localhost:" + port + "/student/" + student1.getId());
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student/" + student1.getId(), Student.class)).isEqualTo(null);
    }

    @Test
    public void editStudentTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Student student1 = new Student();
        student1.setName("Трофим");
        student1.setAge(22);
        student1.setId(3);
        Student student2 = new Student();
        student2.setName("Игорь");
        student2.setAge(8);
        student2.setId(3);
        Student student3 = new Student();
        student3.setName("Сара");
        student3.setAge(27);
        student3.setId(100);
        HttpEntity<Student> httpEntity = new HttpEntity<>(student3, headers);
        this.restTemplate.put("http://localhost:" + port + "/student", student1);
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student/3", Student.class)).isEqualTo(student1);
        this.restTemplate.put("http://localhost:" + port + "/student", student2);
        ResponseEntity<Student> response = restTemplate.exchange("http://localhost:" + port + "/student", HttpMethod.PUT, httpEntity, Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}