package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>(); // словарь:userId-items
    private final UserRepository userRepository;

    private Long nextItemId = 1L;

    @Override
    public Item save(ItemDto itemDto, Long userId) {
        validateForSaveItem(itemDto, userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(nextItemId++);
        item.setOwnerId(userId);
        items.compute(userId, (aLong, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        log.info("Вещь id = {} успешно сохранена у пользователя id = {}", item.getId(), userId);
        return item;
    }

    @Override
    public Item update(ItemDto itemDto, Long userId, Long itemId) {
        validateForUpdateItem(userId, itemId);
        Item item = items.get(userId).stream()
                .filter(item1 -> item1.getId().equals(itemId))
                .findFirst()
                .get();
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Вещь id = {} успешно обновлена у пользователя id = {}", itemId, userId);
        return item;
    }

    @Override
    public Item findItemByItemId(Long userId, Long itemId) {
        Optional<Item> foundItem = items.get(userId).stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
        if (foundItem.isEmpty()) {
            log.warn("Вещь id = {} не найдена", itemId);
            throw new NotFoundException("Вещь id = " + itemId + " не найдена");
        }
        log.info("Вещь id = {} успешно найдена у пользователя id = {}", itemId, userId);
        return foundItem.get();
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        checkUserById(userId);
        log.info("Все вещи успешно найдены у пользователя id = {}", userId);
        return new ArrayList<>(items.get(userId));
    }

    @Override
    public List<Item> findItemByText(Long userId, String text) {
        List<Item> foundItems = new ArrayList<>();
        if (!text.isBlank()) {
            String regex = text.toLowerCase();
            //поиск по названию
            foundItems = getAllItems().stream()
                    .filter(item -> item.getAvailable().equals(Boolean.TRUE)
                            && (item.getName().toLowerCase().contains(regex)
                                || item.getDescription().toLowerCase().contains(regex)))
                    .collect(Collectors.toList());

            log.info("Все вещи успешно найдены по text = {} для пользователя id = {}", text, userId);
        }
        return foundItems;
    }

    private void validateForSaveItem(ItemDto itemDto, Long userId) {
        checkUserById(userId);
        if (itemDto.getAvailable() == null) {
            log.warn("У вещи нету статуса аренды");
            throw new BadRequestException("У вещи нету статуса аренды");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.warn("У вещи нету названия");
            throw new BadRequestException("У вещи нету названия");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.warn("У вещи нету описания");
            throw new BadRequestException("У вещи нету описания");
        }
    }

    private void validateForUpdateItem(Long userId, Long itemId) {
        checkUserById(userId);
        if (items.get(userId) == null) {
            log.warn("У пользователя id = {} нету вещей для аренды", userId);
            throw new NotFoundException("У пользователя id = " + userId + " нету вещей для аренды");
        }
        if (items.get(userId).stream().noneMatch(item -> item.getId().equals(itemId))) {
            log.warn("У пользователя id = {} нету прав на вещь id = {}", userId, itemId);
            throw new ForbiddenException("У пользователя id = " + userId + " нету прав на вещь id = " + itemId);
        }
    }

    private void checkUserById(Long userId) {
        if (userRepository.findUserById(userId) == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    private List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();
        items.values().forEach(allItems::addAll);
        return allItems;
    }
}
