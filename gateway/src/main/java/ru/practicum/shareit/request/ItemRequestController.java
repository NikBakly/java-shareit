package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderKey;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createNewRequest(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                   @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Successful creating itemRequest {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByOwnerId(@RequestHeader(HeaderKey.USER_KEY) Long userId) {
        log.info("Successful getting itemRequests for ownerId={}", userId);
        return itemRequestClient.findAllByOwnerId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Successful getting all itemRequests with from={}, size={}, userId={}", from, size, userId);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestInfoById(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                      @PathVariable("requestId") Long requestId) {
        log.info("Successful get requestId={}, userId={}", requestId, userId);
        return itemRequestClient.findById(userId, requestId);
    }
}
