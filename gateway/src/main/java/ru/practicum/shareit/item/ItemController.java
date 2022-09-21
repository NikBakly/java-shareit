package ru.practicum.shareit.item;

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
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createNewItem(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                @RequestBody @Valid ItemDto itemDto) {
        log.info("Successful creating item {}, userId={}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable("itemId") Long itemId) {
        log.info("Successful updating itemId {}, userId={}", itemId, userId);
        return itemClient.update(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemByItemId(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                   @PathVariable("itemId") Long itemId) {
        log.info("Successful find item by itemId {}, userId={}", itemId, userId);
        return itemClient.findById(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItems(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Successful get all items by userId={}", userId);
        return itemClient.findAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByText(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                 @RequestParam(name = "text", defaultValue = "") String text,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Successful find item by text={}, userId={}", text, userId);
        return itemClient.findItemByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentDto commentDto,
                                                @RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                @PathVariable("itemId") Long itemId) {
        log.info("Successful create comment for itemId={}, userId={}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
