package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.State.State;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс-сервис, который предназначен для реализации основной бизнес-логики.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDtoForCreate create(BookingDtoForCreate bookingDtoForCreate, Long userId) {
        validateForCreate(bookingDtoForCreate, userId);
        Booking booking = BookingMapper.toBooking(bookingDtoForCreate, userId);
        bookingRepository.save(booking);
        log.info("Бронь id = {} успешно запрошена пользователем id = {}", booking.getId(), userId);
        return BookingMapper.toBookingDtoForCreate(booking);
    }

    @Transactional
    @Override
    public BookingDto setStatus(Long userId, Long bookingId, Boolean approved) {
        validateForSetStatus(userId, bookingId, approved);
        Booking booking = bookingRepository.findById(bookingId).get();

        //назначение статического класса для BookingDto класса
        BookingDto.itemInitialization = new BookingDto.Item(booking.getItemId(),
                itemRepository.findById(booking.getItemId()).get().getName());
        BookingDto.bookerInitialization = new BookingDto.Booker(booking.getBookerId());

        //Изменение сущности бронирования с учетом подтверждения владельцем вещи для аренды
        if (approved.equals(Boolean.TRUE)) {
            //Изменение статуса у бронирования и сохранение в БД
            booking.setStatus(Status.APPROVED);
            bookingRepository.save(booking);
            log.info("Бронь id = {} успешно подтверждена владельцем id = {}", bookingId, userId);
            return BookingMapper.toBookingDto(booking);
        } else {
            booking.setStatus(Status.REJECTED);
            bookingRepository.save(booking);
            log.info("Бронь id = {} успешно отклонена владельцем id = {}", bookingId, userId);
            return BookingMapper.toBookingDto(booking);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        checkBookerAndOwner(userId, bookingId);
        return BookingMapper.toBookingDto(bookingRepository.findById(bookingId).get());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllForUser(Long userId, State state) {
        checkUserById(userId);
        if (state == null) {
            state = State.ALL;
        }
        List<Booking> bookings;
        //Другие состояния находятся в разработке
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByEndDesc(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByEndDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByEndDesc(userId, Status.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }

        return BookingMapper.toBookingsDtoWithItemAndBooker(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllForOwner(Long userId, State state) {
        checkUserById(userId);
        if (state == null) {
            state = State.ALL;
        }
        List<Booking> bookings;
        //Другие состояния находятся в разработке
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerIdOrderByEndDesc(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatusOrderByEndAsc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatusOrderByEndAsc(userId, Status.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }

        return BookingMapper.toBookingsDtoWithItemAndBooker(bookings);
    }

    // метод для проверки возможности создания бронирования
    private void validateForCreate(BookingDtoForCreate bookingDtoForCreate, Long userId) {
        checkUserById(userId);
        checkItemById(bookingDtoForCreate.getItemId());

        //проверка на доступность предмета для аренды
        if (itemRepository.findById(bookingDtoForCreate.getItemId()).get().getAvailable().equals(Boolean.FALSE)) {
            log.warn("Этот предмет не доступен для аренды");
            throw new BadRequestException("Этот предмет не доступен для аренды");
        }

        //проверка на адекватность срока бронирования
        if (bookingDtoForCreate.getEnd().isBefore(bookingDtoForCreate.getStart())
                || bookingDtoForCreate.getStart().isBefore(LocalDateTime.now())
                || bookingDtoForCreate.getEnd().isBefore(LocalDateTime.now())) {
            log.warn("Продолжительность аренды не верно указано");
            throw new BadRequestException("Продолжительность аренды не верно указано");
        }


        Optional<Item> item = itemRepository.findById(bookingDtoForCreate.getItemId());
        //проверка на возможность бронирования владельца собственного предмета
        if (item.get().getOwnerId().equals(userId)) {
            log.warn("Пользователь id = {} является владельцем предмета id = {}", userId, bookingDtoForCreate.getItemId());
            throw new NotFoundException("Пользователь id = "
                    + userId + " является владельцем предмета id = " + bookingDtoForCreate.getItemId());
        }
    }

    private void validateForSetStatus(Long userId, Long bookingId, Boolean approved) {
        checkUserById(userId);
        checkBookingById(bookingId);
        //Проверка на существование подтверждение
        if (approved == null) {
            log.warn("Параметр approved не был передан");
            throw new BadRequestException("Параметр approved не был передан");
        }

        //проверка на повторное запрос подтверждения
        if (bookingRepository.findById(bookingId).get().getStatus().equals(Status.APPROVED)
                && approved.equals(Boolean.TRUE)) {
            log.warn("Повторное подтверждение бронирование не допустимо");
            throw new BadRequestException("Повторное подтверждение бронирование не допустимо");
        }
        Long ownerId = itemRepository
                .findById(bookingRepository
                        .findById(bookingId)
                        .get()
                        .getItemId())
                .get()
                .getOwnerId();

        //Проверка, что пользователь является владельцем предмета
        if (!ownerId.equals(userId)) {
            log.warn("Пользователь id = {} не является владельцем предмета", userId);
            throw new NotFoundException("Пользователь id = " + userId + " не является владельцем предмета");
        }
    }

    private void checkBookerAndOwner(Long userId, Long bookingId) {
        checkUserById(userId);
        checkBookingById(bookingId);
        Long bookerId = bookingRepository
                .findById(bookingId)
                .get()
                .getBookerId();
        Long ownerId = itemRepository
                .findById(bookingRepository
                        .findById(bookingId)
                        .get().getItemId())
                .get().getOwnerId();
        //проверка, что пользователь является владельцем вещи или автором бронирования
        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            log.warn("Пользователь id = {} не является владельцем предмета или автором бронирования", userId);
            throw new NotFoundException("Пользователь id = " + userId + " не является владельцем предмета или автором бронирования");
        }
    }

    private void checkUserById(Long userId) {
        //проверка существования пользователя
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    private void checkItemById(Long itemId) {
        //проверка существования предмета
        if (itemRepository.findById(itemId).isEmpty()) {
            log.warn("Предмет с id = {} не найден", itemId);
            throw new NotFoundException("Предмет с id = " + itemId + " не найден");
        }
    }

    private void checkBookingById(Long bookingId) {
        // проверка существование бронирование по id
        if (bookingRepository.findById(bookingId).isEmpty()) {
            log.warn("Бронирование id = {} не найден", bookingId);
            throw new NotFoundException("Бронирование id = " + bookingId + " не найден");
        }
    }
}
