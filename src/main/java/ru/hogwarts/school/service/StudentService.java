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

    /**
     *  Метод для получения списка всех студентов
     *
     * @return Возвращает список студентов
     */
    public Collection<StudentRecord> getAllStudents() {
        logger.info("Was invoked method for get all students");
        return studentRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    /**
     * Метод создает студента
     *
     * @param studentRecord студент
     * @return возвращает студента
     */
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

    /**
     * Метод для поиска студента по id
     *
     * @param id id студента
     * @return возвращает студента
     */
    public StudentRecord findStudent(long id) {
        logger.info("Was invoked method  for find student");
        return recordMapper.toRecord(studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not student with id = {}", id);
                    return new StudentNotFoundException(id);
                }));
    }

    /**
     * Метод для обновления студента
     *
     * @param id id студента
     * @param studentRecord студент
     * @return возвращает студента
     */
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

    /**
     * Метод для удаления студента
     *
     * @param id id студента
     * @return возвращает студента
     */
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

    /**
     * Метод для поиска студентов по возрасту
     *
     * @param age возраст студента
     * @return возвращает список студентов
     */
    public Collection<StudentRecord> findByAge(int age) {
        logger.info("Was invoked method for find student by age");
        return studentRepository.findByAge(age).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    /**
     * Метод для поиска студентов, возраст которых находится в промежутке, пришедшем в запросе
     *
     * @param min минимальный возраст студента
     * @param max максимальный возраст студента
     * @return возвращает список студентов
     */
    public Collection<StudentRecord> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find student by age between " + min + " and " + max);
        return studentRepository.findByAgeBetween(min, max).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    /**
     * Метод для получения факультета где учится студент
     *
     * @param id id студента
     * @return возвращает факультет
     */
    public FacultyRecord findFacultyByStudent(long id) {
        logger.info("Was invoked method for find faculty by student");
        return findStudent(id).getFaculty();
    }

    /**
     * Метод для добавления студенту аватара (фотография студента)
     *
     * @param id id студента
     * @param avatarId id аватара
     * @return возвращает студента
     */
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

    /**
     * Метод для получения количества всех студентов в школе
     *
     * @return возвращает количество студентов
     */
    public NumberOfStudents getNumberOfStudents() {
        logger.info("Was invoked method for get numbers of students");
        return studentRepository.getCountStudents();
    }

    /**
     * Метод для получения среднего возраста студентов
     *
     * @return возвращает средний возраст студентов
     */
    public AverageAgeOfStudents getAverageAgeOfStudents() {
        logger.info("Was invoked method for get average age of students");
        return studentRepository.getAverageAgeOfStudents();
    }

    /**
     * Метод для получения count последних студентов
     *
     * @param count количество студентов, которых надо вернуть последними
     * @return возвращает список студентов
     */
    public List<StudentRecord> getLastStudents(int count) {
        logger.info("Was invoked method for get last five students");
        return studentRepository.getLastStudents(count).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    /**
     * Метод для получения студенов по имени
     *
     * @param name имя студента
     * @return возвращает список сдентов
     */
    public List<StudentRecord> getStudentsByName(String name) {
        logger.info("Was invoked method for get students by name");
        return studentRepository.findStudentsByName(name).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    /**
     * Метод для получения всех имен всех студентов, чье имя начинается с буквы А
     *
     * @return возвращает список имен студентов
     */
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

    /**
     * Метод для возвращения среднего возраста всех студентов
     *
     * @return возвращает средний возраст студентов
     */
    @TrackExecutionTime
    public double getAverageAgeOfStudentsViaStream() {
        return studentRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .mapToInt(StudentRecord::getAge)
                .average()
                .orElse(0);
    }

    /**
     * Создать эндпоинт для студентов (с любым url), который запускает метод сервиса (с любым названием).
     * В методе сервиса необходимо получить список всех студентов и вывести их имена в консоль используя команду System.out.println().
     * При этом первые два имени вывести в основном потоке, второе и третье в параллельном потоке.
     * А пятое и шестое во втором параллельном потоке.
     * В итоге в консоле должен появиться список из шести имен в порядке, отличном от порядка в коллекции.
     */
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


    /**
     * Создать еще один эндпоинт и метод в сервисе. Но теперь вывод имени в консоль вынести в отдельный синхронизированный метод.
     * И так же запустить печать в консоль первых двух имен в основном потоке, третьего и четвертого в параллельном потоке, четвертого и пятого во втором параллельном потоке.
     * В итоге в консоли должен находиться список из имен в том же порядке, что и в коллекции.
     */
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
