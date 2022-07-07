package ru.practicum.shareit.requests.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * POJO класс, описывающий поля объекта "запрос товара"
 */
@Data
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private LocalDate created;
}
