package ru.practicum.shareit.item;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Класс-сервис, который предназначен для реализации основной бизнес-логики.
 */
@Service
@Setter(onMethod_ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    @Transactional
    @Override
    public ResponseEntity<Object> create(ItemDto itemDto, Long userId) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        Item answerItem = itemRepository.save(item);
        log.info("Вещь id = {} успешно сохранена у пользователя id = {}", answerItem.getId(), userId);
        return ResponseEntity.ok(ItemMapper.toItemDto(answerItem));
    }

    @Transactional
    @Override
    public ResponseEntity<Object> update(ItemDto itemDto, Long userId, Long itemId) {
        ResponseEntity<Object> resultValidateWhenUpdateItem = validateWhenUpdateItem(userId, itemId);
        if (resultValidateWhenUpdateItem != null) {
            return resultValidateWhenUpdateItem;
        }
        Item item = itemRepository.findById(itemId).get();
        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());
        Item answerItem = itemRepository.save(item);
        log.info("Вещь id = {} успешно обновлена у пользователя id = {}", itemId, userId);
        return ResponseEntity.ok(ItemMapper.toItemDto(answerItem));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Object> findByUserIdAndItemId(Long userId, Long itemId) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        if (checkItemById(itemId)) {
            return new ResponseEntity<>(getMapBody("Вещь id = " + itemId + " не найдена"), HttpStatus.NOT_FOUND);
        }
        Item foundItem = itemRepository.findById(itemId).get();
        log.info("Вещь id = {} успешно найдена", itemId);
        List<CommentDto> commentsDto = new ArrayList<>();
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        //Перебор всех комментариев по предмету и преобразование их в DTO класс
        comments.forEach(comment -> commentsDto.add(CommentMapper.toCommentDto(
                comment,
                userRepository.findById(comment.getUserId()).get().getName())
        ));
        ItemFoundDto result;
        // Если пользователь является владельцем предмета
        if (foundItem.getOwnerId().equals(userId)) {
            result = getItemFoundDto(foundItem, userId, commentsDto);
        } else {
            result = ItemMapper.toItemFoundDto(foundItem,
                    null,
                    null,
                    commentsDto);
        }
        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Object> findAllItemsByUserId(Long userId, Integer from, Integer size) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        //если пагинация выходит за список, то изменим ее размер
        int sizeItems = itemRepository.findAllByOwnerId(userId).size();
        if (sizeItems < from + size && sizeItems > 0) {
            size = sizeItems - from;
        }
        List<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size, Sort.by("id"))).getContent();

        List<ItemFoundDto> itemsDto = new ArrayList<>();
        items.forEach(item -> itemsDto.add(getItemFoundDto(item, userId)));
        log.info("Все вещи успешно найдены у пользователя id = {} с учетом пагинации", userId);
        return ResponseEntity.ok(itemsDto);
    }

    @Transactional()
    @Override
    public ResponseEntity<Object> addComment(Long userId, Long itemId, Comment comment) {
        ResponseEntity<Object> resultValidateWhenAddComment = validateWhenAddComment(userId, itemId);
        if (resultValidateWhenAddComment != null) {
            return resultValidateWhenAddComment;
        }
        comment.setItemId(itemId);
        comment.setUserId(userId);
        comment.setCreated(LocalDateTime.now());
        Comment answerComment = commentRepository.save(comment);
        log.info("Комментарий id = {} успешно добавлен к предмету id = {}", answerComment.getId(), itemId);
        CommentDto result = CommentMapper.toCommentDto(answerComment, userRepository.findById(userId).get().getName());
        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Object> findItemByText(Long userId, String text, Integer from, Integer size) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        if (text.isBlank())
            return ResponseEntity.ok(new ArrayList<>());
        //если пагинация выходит за список, то изменим ее размер
        int sizeItems = itemRepository.findItemsByText(text).size();
        if (sizeItems < from + size && sizeItems > 0) {
            size = sizeItems - from;
        }
        List<Item> items = itemRepository.findItemsByText(text, PageRequest.of(from, size)).getContent();

        log.info("Все вещи успешно найдены по text = '{}' для пользователя id = {}", text, userId);
        return ResponseEntity.ok(ItemMapper.toItemsDto(items));
    }

    // Метод для обработки и возврата инициализированного класса ItemFoundDto
    private ItemFoundDto getItemFoundDto(Item foundItem, Long userId) {
        // Найдем две ближайшие аренды
        List<Booking> bookings = bookingRepository.findTwoBookingByOwnerIdOrderByEndAsc(userId, foundItem.getId());
        // если получено две аренды: предыдущая и следующая.
        if (bookings.size() >= 2) {
            return ItemMapper.toItemFoundDto(foundItem,
                    new ItemFoundDto.LastBooking(bookings.get(0)),
                    new ItemFoundDto.NextBooking(bookings.get(1)));
            // если получена только аренда
        } else if (bookings.size() == 1) {
            // если аренда является предыдущей
            if (bookings.get(0).getStart().isBefore(LocalDateTime.now())) {
                return ItemMapper.toItemFoundDto(foundItem,
                        new ItemFoundDto.LastBooking(bookings.get(0)),
                        null);
                // если аренда является следующей
            } else {
                return ItemMapper.toItemFoundDto(foundItem,
                        null,
                        new ItemFoundDto.NextBooking(bookings.get(0)));
            }
        } else {
            return ItemMapper.toItemFoundDto(foundItem,
                    null,
                    null);
        }
    }

    private ItemFoundDto getItemFoundDto(Item foundItem, Long userId, List<CommentDto> commentsDto) {
        // Найдем две ближайшие аренды
        List<Booking> bookings = bookingRepository.findTwoBookingByOwnerIdOrderByEndAsc(userId, foundItem.getId());
        // если получено две аренды: предыдущая и следующая.
        if (bookings.size() == 2) {
            return ItemMapper.toItemFoundDto(foundItem,
                    new ItemFoundDto.LastBooking(bookings.get(0)),
                    new ItemFoundDto.NextBooking(bookings.get(1)),
                    commentsDto);
            // если получена только аренда
        } else if (bookings.size() == 1) {
            // если аренда является предыдущей
            if (bookings.get(0).getStart().isBefore(LocalDateTime.now())) {
                return ItemMapper.toItemFoundDto(foundItem,
                        new ItemFoundDto.LastBooking(bookings.get(0)),
                        null,
                        commentsDto);
                // если аренда является следующей
            } else {
                return ItemMapper.toItemFoundDto(foundItem,
                        null,
                        new ItemFoundDto.NextBooking(bookings.get(0)),
                        commentsDto);
            }
        } else {
            return ItemMapper.toItemFoundDto(foundItem,
                    null,
                    null,
                    commentsDto);
        }
    }

    // Метод для проверок при создании комментария
    private ResponseEntity<Object> validateWhenAddComment(Long userId, Long itemId) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        if (checkItemById(itemId)) {
            return new ResponseEntity<>(getMapBody("Вещь id = " + itemId + " не найдена"), HttpStatus.NOT_FOUND);
        }

        if (bookingRepository.findByBookerIdAndItemId(userId, itemId) == null) {
            log.warn("Пользователь id = {} не арендовывал предмет id = {}", userId, itemId);
            return new ResponseEntity<>(getMapBody("Пользователь id = " + userId + " не арендовывал предмет id = " + itemId), HttpStatus.BAD_REQUEST);
        }
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemId(userId, itemId);
        //Аренда по которой можно будет сделать отзыв
        Booking checkBooking = null;
        for (Booking booking : bookings) {
            if (booking.getStatus().equals(Status.APPROVED) && booking.getEnd().isBefore(LocalDateTime.now())) {
                checkBooking = booking;
                break;
            }
        }
        if (checkBooking == null) {
            log.warn("Бронирование не подтверждено или не закончился срок аренды");
            return new ResponseEntity<>(getMapBody("Бронирование не подтверждено или не закончился срок аренды"), HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    // Метод для проверок при обновлении предмета
    private ResponseEntity<Object> validateWhenUpdateItem(Long userId, Long itemId) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(getMapBody("Пользователь с id = " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }
        if (itemRepository.findAllByOwnerId(userId).stream().noneMatch(item -> item.getId().equals(itemId))) {
            log.warn("У пользователя id = {} нету прав на вещь id = {}", userId, itemId);
            return new ResponseEntity<>(getMapBody("У пользователя id = " + userId + " нету прав на вещь id = " + itemId), HttpStatus.FORBIDDEN);
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

    private Boolean checkItemById(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.warn("Вещь id = {} не найдена", itemId);
            return true;
        }
        return false;
    }

    Map<String, String> getMapBody(String error) {
        return Map.of("error", error);
    }
}
