package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.entity.AverageAgeOfStudents;
import ru.hogwarts.school.entity.LastFiveStudents;
import ru.hogwarts.school.entity.NumberOfStudents;
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

    @Query(value = "select name as studentName from student as student order by id desc limit 5", nativeQuery = true)
    Collection<LastFiveStudents> getLastFiveStudents();

}
