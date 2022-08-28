package ru.practicum.shareit.requests;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Setter(onMethod_ = @Autowired)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;


    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        validateWhenCreateRequest(itemRequestDto, userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, LocalDateTime.now(), userId);
        ItemRequest answer = itemRequestRepository.save(itemRequest);
        log.info("Запрос успешно сохранен");
        return ItemRequestMapper.toItemRequestDto(answer);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> findAllRequestsByOwnerId(Long userId) {
        checkUserById(userId);
        log.info("Успешный вывод списка запросов по id = {} владельца", userId);
        return getItemRequestsDtoWhenFindAllRequestsByOwnerId(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        checkUserById(userId);
        if (from == null || size == null) {
            log.info("Один из параметров не определен, возвращаются все запросы");
            return getItemRequestDtoWhenGetAllRequests(userId,
                    itemRequestRepository.findAllByOrderByCreatedAsc());
        }
        checkValueFromAndSize(from, size);
        //если пагинация выходит за список, то изменим ее размер
        int sizeItemRequests = itemRequestRepository.findAll().size();
        if (sizeItemRequests < from + size && sizeItemRequests > 0) {
            size = sizeItemRequests - from;
        }
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAll(PageRequest.of(from, size, Sort.by(Sort.Order.asc("created"))))
                .getContent();
        log.info("Успешный вывод всех запросов с учетом пагинации");
        return getItemRequestDtoWhenGetAllRequests(userId, itemRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto findRequestById(Long requestId, Long userId) {
        checkUserById(userId);
        checkRequestById(requestId);
        log.info("Успешный вывод запроса по его id = {}", requestId);
        return getItemRequestDto(requestId);
    }


    // Метод для проверки запроса при его создании
    private void validateWhenCreateRequest(ItemRequestDto itemRequestDto, Long userId) {
        checkUserById(userId);
        if (itemRequestDto.getDescription() == null) {
            log.warn("Описание запроса должно быть определенно");
            throw new BadRequestException("Описание запроса должно быть определенно");
        }
    }

    // Метод для проверки параметров для пагинации
    private void checkValueFromAndSize(int from, int size) {
        if (from < 0) {
            log.warn("Страница не может начинаться с {}", from);
            throw new BadRequestException("Страница не может начинаться с " + from);
        }
        if (size <= 0) {
            log.warn("Страница не может быть равна размеру {}", size);
            throw new BadRequestException("Страница не может быть равна размеру " + size);
        }
    }

    // Метод для проверки существования пользователя
    private void checkUserById(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    // Метод для проверки существования запроса
    private void checkRequestById(Long requestId) {
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            log.warn("Запрос с id = {} не найден", requestId);
            throw new NotFoundException("Запрос с id = " + requestId + " не найден");
        }
    }

    // Метод для возврата списка запросов для пользователя
    private List<ItemRequestDto> getItemRequestDtoWhenGetAllRequests(Long userId, List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        itemRequests.forEach(itemRequest -> {
            if (!itemRequest.getRequesterId().equals(userId)) {
                List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
                List<ItemDto> itemsDto = new ArrayList<>();
                items.forEach(item -> itemsDto.add(ItemMapper.toItemDto(item)));
                itemRequestsDto.add(ItemRequestMapper.toItemRequestDto(itemRequest, itemsDto));
            }
        });
        return itemRequestsDto;
    }

    // Метод для возврата списка запросов для его владельца
    private List<ItemRequestDto> getItemRequestsDtoWhenFindAllRequestsByOwnerId(Long userId) {
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(userId);
        itemRequests.forEach(itemRequest -> {
            List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
            if (items.size() == 0 || !itemRequest.getRequesterId().equals(userId)) {
                itemRequestsDto.add(ItemRequestMapper.toItemRequestDto(itemRequest));
            } else {
                List<ItemDto> itemsDto = new ArrayList<>();
                items.forEach(item -> itemsDto.add(ItemMapper.toItemDto(item)));
                itemRequestsDto.add(ItemRequestMapper.toItemRequestDto(itemRequest, itemsDto));
            }
        });
        return itemRequestsDto;
    }

    // Метод для возврата запроса по его id
    private ItemRequestDto getItemRequestDto(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();
        // собираем предметы под запрос
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        List<ItemDto> itemsDto = new ArrayList<>();
        items.forEach(item -> itemsDto.add(ItemMapper.toItemDto(item)));
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsDto);
    }

}
