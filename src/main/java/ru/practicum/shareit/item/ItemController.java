package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderKey;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

/**
 * Класс-контроллер, который предназначен для обработки запросов и возвращение результата.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createNewItem(@RequestBody ItemDto itemDto,
                                 @RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(HeaderKey.USER_KEY) Long userId,
                              @PathVariable("itemId") Long itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping()
    public List<ItemFoundDto> getAllItems(@RequestHeader(HeaderKey.USER_KEY) Long userId) {
        return itemService.findAllItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemFoundDto findItemByItemId(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                         @PathVariable("itemId") Long itemId) {
        return itemService.findByUserIdAndItemId(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByText(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                        @RequestParam(name = "text") String text) {
        return itemService.findItemByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody Comment comment,
                                    @RequestHeader(HeaderKey.USER_KEY) Long userId,
                                    @PathVariable("itemId") Long itemId) {
        return itemService.addComment(userId, itemId, comment);
    }
}
