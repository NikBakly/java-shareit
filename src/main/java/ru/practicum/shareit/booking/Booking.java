package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * POJO класс, описывающий поля объекта "Бронирование"
 */
@Data
public class Booking {
    private Long id;
    private LocalDate start; // дата начала бронирования
    private LocalDate end; // дата конца бронирования
    private Item item; // вещь, которую пользователь бронирует;
    private User booker; // пользователь, который осуществляет бронирование;
    private Status status; // статус бронирования
}
