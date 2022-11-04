package ru.hogwarts.school.record;

import javax.validation.constraints.NotBlank;

public class FacultyRecord {
    private long id;
    @NotBlank(message = "Название факультета должно быть заполнено!")
    private String name;
    @NotBlank(message = "Цвет факультета должно быть заполнен!")
    private String color;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
