package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Service
public class StudentService {
    Logger logger = LoggerFactory.getLogger(StudentService.class);
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

    public List<StudentRecord> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        return studentRepository.getLastFiveStudents().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<StudentRecord> getStudentsByName(String name) {
        logger.info("Was invoked method for get students by name");
        return studentRepository.findStudentsByName(name).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

}
