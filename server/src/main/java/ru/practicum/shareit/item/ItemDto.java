package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

/**
 * Класс, с которым будет работать пользователь
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available; // статус, определяющий доступность вещи для аренды
    private Long requestId;
}

