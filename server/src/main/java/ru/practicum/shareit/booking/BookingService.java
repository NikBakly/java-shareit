package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.state.State;

import java.util.List;

public interface BookingService {

    // Метод для создания бронирования по запросу пользователя
    ResponseEntity<BookingCreateDto> create(BookingCreateDto bookingCreateDto, Long userId);

    // Метод для назначения статуса для бронирования
    ResponseEntity<BookingDto> setStatus(Long userId, Long bookingId, Boolean approved);

    // Метод для поиска бронирования по его id
    ResponseEntity<BookingDto> findById(Long userId, Long bookingId);

    // Метод для поиска всей аренды для пользователя, который осуществляет бронирование
    ResponseEntity<List<BookingDto>> findAllForUser(Long userId, State state, Integer from, Integer size);

    // Метод для поиска всей аренды для пользователя, который является владельцем вещи
    ResponseEntity<List<BookingDto>> findAllForOwner(Long userId, State state, Integer from, Integer size);
}
