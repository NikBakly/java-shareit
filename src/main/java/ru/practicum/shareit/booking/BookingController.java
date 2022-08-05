package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.State.State;

import java.util.List;

/**
 * Класс-контроллер, который предназначен для обработки запросов и возвращение результата.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingCreateDto createNewBooking(@RequestBody BookingCreateDto BookingCreateDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.create(BookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setStatusBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable("bookingId") Long bookingId,
                                       @RequestParam(value = "approved", required = false) Boolean approved) {
        return bookingService.setStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable("bookingId") Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> findAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(value = "state", required = false) State state) {
        return bookingService.findAllForUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(value = "state", required = false) State state) {
        return bookingService.findAllForOwner(userId, state);
    }
}
