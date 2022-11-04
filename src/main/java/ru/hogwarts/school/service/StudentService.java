package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.aop.TrackExecutionTime;
import ru.hogwarts.school.component.RecordMapper;
import ru.hogwarts.school.entity.AverageAgeOfStudents;
import ru.hogwarts.school.entity.NumberOfStudents;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.record.FacultyRecord;
import ru.hogwarts.school.record.StudentRecord;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;

import java.util.stream.Collectors;

@Service
@EnableAsync
public class StudentService {
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarRepository avatarRepository;
    private final RecordMapper recordMapper;

    public StudentService(StudentRepository studentRepository,
                          FacultyRepository facultyRepository,
                          AvatarRepository avatarRepository,
                          RecordMapper recordMapper) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.avatarRepository = avatarRepository;
        this.recordMapper = recordMapper;
    }

    public Collection<StudentRecord> getAllStudents() {
        logger.info("Was invoked method for get all students");
        return studentRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public StudentRecord createStudent(StudentRecord studentRecord) {
        logger.info("Was invoked method for create student");
        Student student = recordMapper.toEntity(studentRecord);
        student.setFaculty(
                Optional.ofNullable(studentRecord.getFaculty())
                        .map(FacultyRecord::getId)
                        .flatMap(facultyRepository::findById)
                        .orElse(null)
        );
        return recordMapper.toRecord(studentRepository.save(student));
    }

    public StudentRecord findStudent(long id) {
        logger.info("Was invoked method  for find student");
        return recordMapper.toRecord(studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not student with id = {}", id);
                    return new StudentNotFoundException(id);
                }));
    }

    public StudentRecord editStudent(long id,
                                     StudentRecord studentRecord) {
        logger.info("Was invoked method for edit student");
        Student oldStudent = studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not student with id = {}", id);
                    return new StudentNotFoundException(id);
                });
        oldStudent.setName(studentRecord.getName());
        oldStudent.setAge(studentRecord.getAge());
        oldStudent.setFaculty(
                Optional.ofNullable(studentRecord.getFaculty())
                        .map(FacultyRecord::getId)
                        .flatMap(facultyRepository::findById)
                        .orElse(null)
        );
        return recordMapper.toRecord(studentRepository.save(oldStudent));
    }

    public StudentRecord deleteStudent(long id) {
        logger.info("Was invoked method for delete student");
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not student with id = {}", id);
                    return new StudentNotFoundException(id);
                });
        studentRepository.delete(student);
        return recordMapper.toRecord(student);
    }

    public Collection<StudentRecord> findByAge(int age) {
        logger.info("Was invoked method for find student by age");
        return studentRepository.findByAge(age).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Collection<StudentRecord> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find student by age between " + min + " and " + max);
        return studentRepository.findByAgeBetween(min, max).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public FacultyRecord findFacultyByStudent(long id) {
        logger.info("Was invoked method for find faculty by student");
        return findStudent(id).getFaculty();
    }

    public StudentRecord patchStudentAvatar(long id, long avatarId) {
        logger.info("Was invoked method for adding avatar to student");
        Optional<Student> optionalStudent = studentRepository.findById(id);
        Optional<Avatar> optionalAvatar = avatarRepository.findById(avatarId);
        if (optionalStudent.isEmpty()) {
            logger.error("There is not student with id = {}", id);
            throw new StudentNotFoundException(id);
        }
        if (optionalAvatar.isEmpty()) {
            logger.error("There is not avatar with id = {}", avatarId);
            throw new AvatarNotFoundException(avatarId);
        }
        Student student = optionalStudent.get();
        student.setAvatar(optionalAvatar.get());
        return recordMapper.toRecord(studentRepository.save(student));
    }

    public NumberOfStudents getNumberOfStudents() {
        logger.info("Was invoked method for get numbers of students");
        return studentRepository.getCountStudents();
    }

    public AverageAgeOfStudents getAverageAgeOfStudents() {
        logger.info("Was invoked method for get average age of students");
        return studentRepository.getAverageAgeOfStudents();
    }

    public List<StudentRecord> getLastStudents(int count) {
        logger.info("Was invoked method for get last five students");
        return studentRepository.getLastStudents(count).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<StudentRecord> getStudentsByName(String name) {
        logger.info("Was invoked method for get students by name");
        return studentRepository.findStudentsByName(name).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    @TrackExecutionTime
    public Collection<String> getNamesOfStudentsByLatterA() {
        return studentRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .map(StudentRecord::getName)
                .map(String::toUpperCase)
                .filter(s -> s.startsWith("A"))
                .sorted()
                .collect(Collectors.toList());
    }

    @TrackExecutionTime
    public double getAverageAgeOfStudentsViaStream() {
        return studentRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .mapToInt(StudentRecord::getAge)
                .average()
                .orElse(0);
    }

    public void getThreads() {
        logger.info("Threads");
        List<StudentRecord> students = studentRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .toList();
        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());
        new Thread(() -> {
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        }).start();
        new Thread(() -> {
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        }).start();
    }

    public void getSynchronizedThreads() {
        logger.info("SynchronizedThreads");
        List<StudentRecord> students = studentRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .toList();
        operation(students, 0, 1);
        new Thread(() -> {
            operation(students, 2, 3);
        }).start();
        new Thread(() -> {
            operation(students, 4, 5);
        }).start();
    }

    private synchronized void operation(List<StudentRecord> students, int key1, int key2) {
        System.out.println(students.get(key1).getName());
        System.out.println(students.get(key2).getName());
    }
}
