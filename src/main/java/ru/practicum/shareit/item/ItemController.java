package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

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
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.save(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("id") Long itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping()
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemByItemId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable("itemId") Long itemId) {
        return itemService.findItemByItemId(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(name = "text") String text) {
        return itemService.findItemByText(userId, text);
    }
}
