package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * Класс, который предназначил для работы с пользователем
 */
@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private static Item item;
    private static User booker;
    private Status status;
}
