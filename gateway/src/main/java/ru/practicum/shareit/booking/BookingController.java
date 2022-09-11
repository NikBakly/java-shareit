package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderKey;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HeaderKey.USER_KEY) long userId,
                                                @RequestBody @Valid BookingDto bookingDto) {
        log.info("Successful creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setStatusBooking(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                   @PathVariable Long bookingId,
                                                   @RequestParam(value = "approved") Boolean approved) {
        log.info("Successful set status for  bookingId={}, userId={}", bookingId, userId);
        return bookingClient.setStatusBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable Long bookingId) {
        log.info("Successful get bookingId={}, userId={}", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForUser(@RequestHeader(HeaderKey.USER_KEY) long userId,
                                                 @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (BookingState.from(stateParam).isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "Unknown state: " + stateParam), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        BookingState state = BookingState.from(stateParam).get();
        log.info("Successful getting booking for user with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllForUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllForOwner(@RequestHeader(HeaderKey.USER_KEY) long userId,
                                                  @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (BookingState.from(stateParam).isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "Unknown state: " + stateParam), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        BookingState state = BookingState.from(stateParam).get();
        log.info("Successful getting booking for owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllForOwner(userId, state, from, size);
    }

}
