package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.model.ItemRequest;

/**
 * POJO класс, описывающий поля объекта "товар"
 */
@Data
@Builder
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available; // статус о том, доступна или нет вещь для аренды
    private Long ownerId; // владелец
    private ItemRequest request;
}
