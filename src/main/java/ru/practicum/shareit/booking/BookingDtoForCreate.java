package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс, который предназначил для работы с пользователем
 */
@Getter
@Setter
public class BookingDtoForCreate {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
