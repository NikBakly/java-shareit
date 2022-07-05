package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDAO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Класс-сервис, который предназначен для реализации основной бизнес-логики.
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDAO itemDAO;

    @Override
    public Item save(ItemDto itemDto, Long userId) {
        return itemDAO.save(itemDto, userId);
    }

    @Override
    public Item update(ItemDto itemDto, Long userId, Long itemId) {
        return itemDAO.update(itemDto, userId, itemId);
    }

    @Override
    public Item findItemByItemId(Long itemId) {
        return itemDAO.findItemByItemId(itemId);
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        return itemDAO.getAllItemsByUserId(userId);
    }

    @Override
    public List<Item> findItemByText(Long userId, String text) {
        return itemDAO.findItemByText(userId, text);
    }
}
