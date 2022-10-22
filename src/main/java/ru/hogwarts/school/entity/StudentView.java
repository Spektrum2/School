package ru.hogwarts.school.entity;

import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;

public interface StudentView {
    Long getStudentId();

    String getStudentName();

    Integer getAge();

    FacultyView getFaculty();

    AvatarView getAvatar();
}
