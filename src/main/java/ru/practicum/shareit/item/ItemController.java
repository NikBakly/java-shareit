package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

/**
 * Класс-контроллер, который предназначен для обработки запросов и возвращение результата.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemRepository itemRepository;

    @PostMapping
    public Item createNewItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRepository.save(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public Item updateItem(@RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("id") Long itemId) {
        return itemRepository.update(itemDto, userId, itemId);
    }

    @GetMapping()
    public List<Item> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRepository.getAllItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public Item findItemByItemId(@PathVariable("itemId") Long itemId) {
        return itemRepository.findItemByItemId(itemId);
    }

    @GetMapping("/search")
    public List<Item> findItemByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(name = "text") String text) {
        return itemRepository.findItemByText(userId, text);
    }
}
