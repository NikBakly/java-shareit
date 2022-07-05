package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс, с которым будет работать пользователь
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private String name;
    private String description;
    private Boolean available; // статус о том, доступна или нет вещь для аренды
}

