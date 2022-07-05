package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(ItemDto itemDto, Long userId);

    Item update(ItemDto itemDto, Long userId, Long itemId);

    Item findItemByItemId(Long userId, Long itemId);

    List<Item> getAllItemsByUserId(Long userId);

    List<Item> findItemByText(Long userId, String text);
}
