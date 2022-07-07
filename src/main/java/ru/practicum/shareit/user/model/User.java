package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

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
