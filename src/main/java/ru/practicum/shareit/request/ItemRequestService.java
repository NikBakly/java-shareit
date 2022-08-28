package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    // Метод для создания запроса
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    // Метод для вывода полного списка запросов по id юзера
    List<ItemRequestDto> findAllRequestsByOwnerId(Long userId);

    // Метод для вывода полного списка запросов с пагинацией
    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    // Метод для вывода запроса по его id
    ItemRequestDto findRequestById(Long requestId, Long userId);
}
