package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

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
