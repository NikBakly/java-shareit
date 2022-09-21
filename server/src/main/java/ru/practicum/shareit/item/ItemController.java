package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderKey;
import ru.practicum.shareit.item.comment.Comment;

/**
 * Класс-контроллер, который предназначен для обработки запросов и возвращение результата.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<Object> createNewItem(@RequestBody ItemDto itemDto,
                                                @RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @RequestHeader(HeaderKey.USER_KEY) Long userId,
                                             @PathVariable("itemId") Long itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItems(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                              @RequestParam(name = "from", required = false) Integer from,
                                              @RequestParam(name = "size", required = false) Integer size) {
        return itemService.findAllItemsByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemByItemId(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                   @PathVariable("itemId") Long itemId) {
        return itemService.findByUserIdAndItemId(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByText(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                 @RequestParam(name = "text") String text,
                                                 @RequestParam(name = "from", required = false) Integer from,
                                                 @RequestParam(name = "size", required = false) Integer size) {
        return itemService.findItemByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody Comment comment,
                                                @RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                @PathVariable("itemId") Long itemId) {
        return itemService.addComment(userId, itemId, comment);
    }
}
