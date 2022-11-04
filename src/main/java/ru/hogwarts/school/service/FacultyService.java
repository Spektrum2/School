package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.aop.TrackExecutionTime;
import ru.hogwarts.school.component.RecordMapper;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.record.FacultyRecord;
import ru.hogwarts.school.record.StudentRecord;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;


@Service
public class FacultyService {
    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;
    private final RecordMapper recordMapper;

    public FacultyService(FacultyRepository facultyRepository, RecordMapper recordMapper) {
        this.facultyRepository = facultyRepository;
        this.recordMapper = recordMapper;
    }

    public Collection<FacultyRecord> getAllFaculty() {
        logger.info("Was invoked method for get all faculties");
        return facultyRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public FacultyRecord createFaculty(FacultyRecord facultyRecord) {
        logger.info("Was invoked method for create faculty");
        return recordMapper.toRecord(facultyRepository.save(recordMapper.toEntity(facultyRecord)));
    }

    public FacultyRecord findFaculty(long id) {
        logger.info("Was invoked method  for find faculty");
        return recordMapper.toRecord(facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not faculty with id = {}", id);
                    return new FacultyNotFoundException(id);
                }));
    }

    public FacultyRecord editFaculty(long id, FacultyRecord facultyRecord) {
        logger.info("Was invoked method for edit faculty");
        Faculty oldFaculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not faculty with id = {}", id);
                    return new FacultyNotFoundException(id);
                });
        oldFaculty.setName(facultyRecord.getName());
        oldFaculty.setColor(facultyRecord.getColor());
        return recordMapper.toRecord(facultyRepository.save(oldFaculty));
    }

    public FacultyRecord deleteFaculty(long id) {
        logger.info("Was invoked method for delete faculty");
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not faculty with id = {}", id);
                    return new FacultyNotFoundException(id);
                });
        facultyRepository.delete(faculty);
        return recordMapper.toRecord(faculty);
    }

    public Collection<FacultyRecord> findByColor(String color) {
        logger.info("Was invoked method for find faculty by color");
        return facultyRepository.findByColor(color).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Collection<FacultyRecord> findByNameOrColor(String nameOrColor) {
        logger.info("Was invoked method for find faculty by name or color ");
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Collection<StudentRecord> findStudentsByFaculty(long id) {
        logger.info("Was invoked method for get students by faculty");
        return facultyRepository.findById(id)
                .map(Faculty::getStudents)
                .map(students ->
                        students.stream()
                                .map(recordMapper::toRecord)
                                .collect(Collectors.toList())
                )
                .orElseThrow(() -> {
                    logger.error("There is not faculty with id = {}", id);
                    return new FacultyNotFoundException(id);
                });
    }
    @TrackExecutionTime
    public String getTheLongestNameOfFaculty() {
        return facultyRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .map(FacultyRecord::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }
}
