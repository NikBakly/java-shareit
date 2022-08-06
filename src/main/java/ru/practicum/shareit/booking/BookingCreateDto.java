package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс, который предназначил для работы с пользователем при создании Booking
 */
@Getter
@Setter
public class BookingCreateDto {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
