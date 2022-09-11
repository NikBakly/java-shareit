package ru.practicum.shareit.request;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Setter(onMethod_ = @Autowired)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;


    @Transactional
    @Override
    public ResponseEntity<Object> create(ItemRequestDto itemRequestDto, Long userId) {
        ResponseEntity<Object> resultValidateWhenCreateRequest = validateWhenCreateRequest(itemRequestDto, userId);
        if (resultValidateWhenCreateRequest != null) {
            return resultValidateWhenCreateRequest;
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, LocalDateTime.now(), userId);
        ItemRequest answer = itemRequestRepository.save(itemRequest);
        log.info("Запрос успешно сохранен");
        return ResponseEntity.ok(ItemRequestMapper.toItemRequestDto(answer));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Object> findAllRequestsByOwnerId(Long userId) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        log.info("Успешный вывод списка запросов по id = {} владельца", userId);
        return ResponseEntity.ok(getItemRequestsDtoWhenFindAllRequestsByOwnerId(userId));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        //если пагинация выходит за список, то изменим ее размер
        int sizeItemRequests = itemRequestRepository.findAll().size();
        if (sizeItemRequests < from + size && sizeItemRequests > 0) {
            size = sizeItemRequests - from;
        }
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAll(PageRequest.of(from, size, Sort.by(Sort.Order.asc("created"))))
                .getContent();
        log.info("Успешный вывод всех запросов с учетом пагинации");
        return ResponseEntity.ok(getItemRequestDtoWhenGetAllRequests(userId, itemRequests));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Object> findRequestById(Long requestId, Long userId) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        if (checkRequestById(requestId)) {
            return new ResponseEntity<>(getMapBody("Запрос с id = " + requestId + " не найден"), HttpStatus.NOT_FOUND);
        }
        log.info("Успешный вывод запроса по его id = {}", requestId);
        return ResponseEntity.ok(getItemRequestDto(requestId));
    }


    // Метод для проверки запроса при его создании
    private ResponseEntity<Object> validateWhenCreateRequest(ItemRequestDto itemRequestDto, Long userId) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        if (itemRequestDto.getDescription() == null) {
            log.warn("Описание запроса должно быть определенно");
            return new ResponseEntity<>(getMapBody("Описание запроса должно быть определенно"), HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    // Метод для проверки существования пользователя
    private Boolean checkUserById(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Пользователь с id = {} не найден", userId);
            return true;
        }
        return false;
    }

    // Метод для проверки существования запроса
    private Boolean checkRequestById(Long requestId) {
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            log.warn("Запрос с id = {} не найден", requestId);
            return true;
        }
        return false;
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

    Map<String, String> getMapBody(String error) {
        return Map.of("error", error);
    }

}
