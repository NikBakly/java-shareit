package ru.practicum.shareit.booking;

import ru.practicum.shareit.State.State;

import java.util.List;

public interface BookingService {

    // Метод для создания бронирования по запросу пользователя
    BookingDtoForCreate create(BookingDtoForCreate bookingDtoForCreate, Long userId);

    // Метод для назначения статуса для бронирования
    BookingDto setStatus(Long userId, Long bookingId, Boolean approved);

    // Метод для поиска бронирования по его id
    BookingDto findById(Long userId, Long bookingId);

    // Метод для поиска всей аренды для пользователя, который осуществляет бронирование
    List<BookingDto> findAllForUser(Long userId, State state);

    // Метод для поиска всей аренды для пользователя, который является владельцем вещи
    List<BookingDto> findAllForOwner(Long userId, State state);
}
