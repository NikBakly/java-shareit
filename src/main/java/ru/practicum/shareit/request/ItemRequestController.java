package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderKey;

import java.util.List;

/**
 * Класс находиться в разработке.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createNewRequest(@RequestBody ItemRequestDto itemRequestDto,
                                           @RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByOwnerId(@RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return itemRequestService.findAllRequestsByOwnerId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                               @RequestParam(name = "from", required = false) Integer from,
                                               @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findRequestInfoById(@PathVariable("requestId") Long requestId,
                                              @RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return itemRequestService.findRequestById(requestId, userId);
    }

}
