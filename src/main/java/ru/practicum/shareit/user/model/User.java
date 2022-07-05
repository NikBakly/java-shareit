package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * POJO класс, описывающий поля объекта "Пользователь"
 */
@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}
