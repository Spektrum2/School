package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.entity.AverageAgeOfStudents;
import ru.hogwarts.school.entity.LastFiveStudents;
import ru.hogwarts.school.entity.NumberOfStudents;
import ru.hogwarts.school.entity.StudentView;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int min, int max);

    @Query(value = "select count(*) as numberOfStudents from student", nativeQuery = true)
    NumberOfStudents getCountStudents();

    @Query(value = "select round(avg(age)) as averageAgeOfStudents from student", nativeQuery = true)
    AverageAgeOfStudents getAverageAgeOfStudents();

    @Query(value = "select s.id as studentId, s.name as studentName, s.age, f.id as facultyId, f.name, f.color, a.id from student s INNER JOIN faculty f on s.faculty_id = f.id INNER JOIN avatar a on s.avatar_id = a.id order by s.id desc limit 5", nativeQuery = true)
    Collection<StudentView> getLastFiveStudents();

}
