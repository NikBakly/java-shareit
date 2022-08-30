package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderKey;
import ru.practicum.shareit.booking.state.State;

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
    public BookingCreateDto createNewBooking(@RequestBody BookingCreateDto bookingCreateDto,
                                             @RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return bookingService.create(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setStatusBooking(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                       @PathVariable("bookingId") Long bookingId,
                                       @RequestParam(value = "approved", required = false) Boolean approved) {
        return bookingService.setStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                               @PathVariable("bookingId") Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> findAllForUser(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                           @RequestParam(value = "state", required = false) State state,
                                           @RequestParam(name = "from", required = false) Integer from,
                                           @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.findAllForUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllForOwner(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                            @RequestParam(value = "state", required = false) State state,
                                            @RequestParam(name = "from", required = false) Integer from,
                                            @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.findAllForOwner(userId, state, from, size);
    }
}
