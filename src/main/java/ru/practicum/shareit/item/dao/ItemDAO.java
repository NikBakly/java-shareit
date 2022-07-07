package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemDAO {
    ItemDto save(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    ItemDto findItemByItemId(Long userId, Long itemId);

    List<ItemDto> getAllItemsByUserId(Long userId);

    List<ItemDto> findItemByText(Long userId, String text);
}
