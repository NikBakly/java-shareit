package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDAO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.Item;

import java.util.List;

/**
 * Класс-сервис, который предназначен для реализации основной бизнес-логики.
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDAO itemDAO;

    @Override
    public ItemDto save(ItemDto itemDto, Long userId) {
        return itemDAO.save(itemDto, userId);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        return itemDAO.update(itemDto, userId, itemId);
    }

    @Override
    public ItemDto findItemByItemId(Long userId, Long itemId) {
        return itemDAO.findItemByItemId(userId, itemId);
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemDAO.getAllItemsByUserId(userId);
    }

    @Override
    public List<ItemDto> findItemByText(Long userId, String text) {
        return itemDAO.findItemByText(userId, text);
    }
}
