package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {
    ItemDto save(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    ItemDto findItemByItemId(Long userId, Long itemId);

    List<ItemDto> getAllItemsByUserId(Long userId);

    List<ItemDto> findItemByText(Long userId, String text);
}
