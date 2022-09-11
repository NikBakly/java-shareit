package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;

public interface ItemRequestService {
    // Метод для создания запроса
    ResponseEntity<Object> create(ItemRequestDto itemRequestDto, Long userId);

    // Метод для вывода полного списка запросов по id юзера
    ResponseEntity<Object> findAllRequestsByOwnerId(Long userId);

    // Метод для вывода полного списка запросов с пагинацией
    ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size);

    // Метод для вывода запроса по его id
    ResponseEntity<Object> findRequestById(Long requestId, Long userId);
}
