package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.comment.Comment;

public interface ItemService {
    // методы для создания предмета
    ResponseEntity<Object> create(ItemDto itemDto, Long userId);

    // метод для обновления предмета
    ResponseEntity<Object> update(ItemDto itemDto, Long userId, Long itemId);

    // метод для поиска предмета по id пользователя и по id предмета
    ResponseEntity<Object> findByUserIdAndItemId(Long userId, Long itemId);

    // метод для поиска предметов по id пользователя
    ResponseEntity<Object> findAllItemsByUserId(Long userId, Integer from, Integer size);

    // метод для создания отзыва к предмету
    ResponseEntity<Object> addComment(Long userId, Long itemId, Comment comment);

    // метод для поиска предмета по тексту
    ResponseEntity<Object> findItemByText(Long userId, String text, Integer from, Integer size);


}
