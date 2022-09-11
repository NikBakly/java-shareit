package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderKey;

/**
 * Класс-контроллер, который предназначен для обработки запросов и возвращение результата
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<Object> createNewRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                   @RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByOwnerId(@RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return itemRequestService.findAllRequestsByOwnerId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                 @RequestParam(name = "from", required = false) Integer from,
                                                 @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestInfoById(@PathVariable("requestId") Long requestId,
                                                      @RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return itemRequestService.findRequestById(requestId, userId);
    }

}
