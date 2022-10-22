package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.component.RecordMapper;
import ru.hogwarts.school.entity.AverageAgeOfStudents;
import ru.hogwarts.school.entity.LastFiveStudents;
import ru.hogwarts.school.entity.NumberOfStudents;
import ru.hogwarts.school.entity.StudentView;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.record.FacultyRecord;
import ru.hogwarts.school.record.StudentRecord;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
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
        return studentRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public StudentRecord createStudent(StudentRecord studentRecord) {
        Student student = recordMapper.toEntity(studentRecord);
        student.setFaculty(
                Optional.ofNullable(student.getFaculty())
                        .map(Faculty::getId)
                        .flatMap(facultyRepository::findById)
                        .orElse(null)
        );
        return recordMapper.toRecord(studentRepository.save(student));
    }

    public StudentRecord findStudent(long id) {
        return recordMapper.toRecord(studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id)));
    }

    public StudentRecord editStudent(long id,
                                     StudentRecord studentRecord) {
        Student oldStudent = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
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
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.delete(student);
        return recordMapper.toRecord(student);
    }

    public Collection<StudentRecord> findByAge(int age) {
        return studentRepository.findByAge(age).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Collection<StudentRecord> findByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public FacultyRecord findFacultyByStudent(long id) {
    return findStudent(id).getFaculty();
    }

    public StudentRecord patchStudentAvatar(long id, long avatarId) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        Optional<Avatar> optionalAvatar = avatarRepository.findById(avatarId);
        if (optionalStudent.isEmpty()) {
            throw new StudentNotFoundException(id);
        }
        if (optionalAvatar.isEmpty()) {
            throw new AvatarNotFoundException(avatarId);
        }
        Student student = optionalStudent.get();
        student.setAvatar(optionalAvatar.get());
        return recordMapper.toRecord(studentRepository.save(studentRepository.save(student)));
    }

    public NumberOfStudents getNumberOfStudents() {
        return studentRepository.getCountStudents();
    }

    public AverageAgeOfStudents getAverageAgeOfStudents() {
        return studentRepository.getAverageAgeOfStudents();
    }

    public Collection<StudentView> getLastFiveStudents() {
        return studentRepository.getLastFiveStudents();
    }

}
