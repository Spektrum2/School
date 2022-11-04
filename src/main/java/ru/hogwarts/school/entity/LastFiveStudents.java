package ru.hogwarts.school.entity;

import org.springframework.beans.factory.annotation.Value;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;

public interface LastFiveStudents {
    Long getId();

    String getName();

    Integer getAge();

    Faculty getFaculty();

    Avatar getAvatar();
}
