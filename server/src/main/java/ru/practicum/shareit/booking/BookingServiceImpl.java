package ru.practicum.shareit.booking;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.exception.BadRequestException;
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
@Setter(onMethod_ = @Autowired)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Transactional
    @Override
    public ResponseEntity<BookingCreateDto> create(BookingCreateDto bookingCreateDto, Long userId) {
        ResponseEntity<BookingCreateDto> resultValidateForCreate = validateForCreate(bookingCreateDto, userId);
        if (resultValidateForCreate != null) {
            return resultValidateForCreate;
        }
        Booking booking = BookingMapper.toBooking(bookingCreateDto, userId);
        Booking bookingAnswer = bookingRepository.save(booking);
        log.info("Бронь id = {} успешно запрошена пользователем id = {}", bookingAnswer.getId(), userId);
        return ResponseEntity.ok(BookingMapper.toBookingCreateDto(bookingAnswer));
    }

    @Transactional
    @Override
    public ResponseEntity<BookingDto> setStatus(Long userId, Long bookingId, Boolean approved) {
        ResponseEntity<BookingDto> resultValidateForSetStatus = validateForSetStatus(userId, bookingId, approved);
        if (resultValidateForSetStatus != null) {
            return resultValidateForSetStatus;
        }

        Booking booking = bookingRepository.findById(bookingId).get();
        //Изменение сущности бронирования с учетом подтверждения владельцем вещи для аренды
        if (approved.equals(Boolean.TRUE)) {
            //Изменение статуса у бронирования и сохранение в БД
            booking.setStatus(Status.APPROVED);
            bookingRepository.save(booking);
            log.info("Бронь id = {} успешно подтверждена владельцем id = {}", bookingId, userId);
        } else {
            booking.setStatus(Status.REJECTED);
            bookingRepository.save(booking);
            log.info("Бронь id = {} успешно отклонена владельцем id = {}", bookingId, userId);
        }
        BookingDto result = BookingMapper.toBookingDto(booking,
                new BookingDto.Item(booking.getItemId(),
                        itemRepository.findById(booking.getItemId()).get().getName()),
                new BookingDto.Booker(booking.getBookerId()));
        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<BookingDto> findById(Long userId, Long bookingId) {
        ResponseEntity<BookingDto> resultCheckBookerAndOwner = checkBookerAndOwner(userId, bookingId);
        if (resultCheckBookerAndOwner != null) {
            return resultCheckBookerAndOwner;
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        BookingDto result = BookingMapper.toBookingDto(booking,
                new BookingDto.Item(booking.getItemId(),
                        itemRepository.findById(booking.getItemId()).get().getName()),
                new BookingDto.Booker(booking.getBookerId()));
        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<BookingDto>> findAllForUser(Long userId, State state, Integer from, Integer size) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (state == null) {
            state = State.ALL;
        }
        List<Booking> resultBookings = new ArrayList<>();

        //если пагинация выходит за список, то изменим ее размер
        int sizeBookings = bookingRepository.findAllByBookerIdOrderByEndDesc(userId).size();
        if (sizeBookings < from + size) {
            size = sizeBookings - from;
        }
        Pageable pageable = PageRequest.of(from, size);
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByEndDesc(userId, pageable).getContent();
        switch (state) {
            case ALL:
                resultBookings = bookingRepository.findAllByBookerIdOrderByEndDesc(userId, pageable).getContent();
                break;
            case WAITING:
                resultBookings = bookingRepository
                        .findAllByBookerIdAndStatusOrderByEndDesc(userId, Status.WAITING, pageable).getContent();
                break;
            case REJECTED:
                resultBookings = bookingRepository
                        .findAllByBookerIdAndStatusOrderByEndDesc(userId, Status.REJECTED, pageable).getContent();
                break;
            case PAST:
                for (Booking booking : bookings) {
                    if (booking.getEnd().isBefore(LocalDateTime.now())) {
                        resultBookings.add(booking);
                    }
                }
                break;
            case CURRENT:
                for (Booking booking : bookings) {
                    if (booking.getStart().isBefore(LocalDateTime.now())
                            && booking.getEnd().isAfter(LocalDateTime.now())) {
                        resultBookings.add(booking);
                    }
                }
                break;
            case FUTURE:
                for (Booking booking : bookings) {
                    if (booking.getEnd().isAfter(LocalDateTime.now())) {
                        resultBookings.add(booking);
                    }
                }
                break;
            default:
                log.warn("Unknown state: {}", state);
                throw new BadRequestException("Unknown state: " + state);
        }
        return ResponseEntity.ok(getBookingsDto(resultBookings));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<BookingDto>> findAllForOwner(Long userId, State state, Integer from, Integer size) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (state == null) {
            state = State.ALL;
        }
        List<Booking> resultBookings = new ArrayList<>();

        //если пагинация выходит за список, то изменим ее размер
        int sizeBookings = bookingRepository.findAllByOwnerIdOrderByEndDesc(userId).size();
        if (sizeBookings < from + size && sizeBookings > 0) {
            size = sizeBookings - from;
        }
        Pageable pageable = PageRequest.of(from, size);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdOrderByEndDesc(userId, pageable).getContent();
        switch (state) {
            case ALL:
                resultBookings = bookingRepository.findAllByOwnerIdOrderByEndDesc(userId, pageable).getContent();
                break;
            case WAITING:
                resultBookings = bookingRepository
                        .findAllByOwnerIdAndStatusOrderByEndDesc(userId, Status.WAITING, pageable).getContent();
                break;
            case REJECTED:
                resultBookings = bookingRepository
                        .findAllByOwnerIdAndStatusOrderByEndDesc(userId, Status.REJECTED, pageable).getContent();
                break;
            case PAST:
                for (Booking booking : bookings) {
                    if (booking.getEnd().isBefore(LocalDateTime.now())) {
                        resultBookings.add(booking);
                    }
                }
                break;
            case CURRENT:
                for (Booking booking : bookings) {
                    if (booking.getStart().isBefore(LocalDateTime.now())
                            && booking.getEnd().isAfter(LocalDateTime.now())) {
                        resultBookings.add(booking);
                    }
                }
                break;
            case FUTURE:
                for (Booking booking : bookings) {
                    if (booking.getEnd().isAfter(LocalDateTime.now())) {
                        resultBookings.add(booking);
                    }
                }
                break;
            default:
                log.warn("Unknown state: {}", state);
                throw new BadRequestException("Unknown state: " + state);
        }

        return ResponseEntity.ok(getBookingsDto(resultBookings));
    }

    // метод возвращаюсь список из преобразованных классов
    private List<BookingDto> getBookingsDto(List<Booking> bookings) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        bookings.forEach(booking -> bookingsDto.add(BookingMapper.toBookingDto(booking,
                new BookingDto.Item(booking.getItemId(), itemRepository.findById(booking.getItemId()).get().getName()),
                new BookingDto.Booker(booking.getBookerId()))));
        return bookingsDto;
    }

    // метод для проверки возможности создания бронирования
    private ResponseEntity<BookingCreateDto> validateForCreate(BookingCreateDto bookingCreateDto, Long userId) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (checkItemById(bookingCreateDto.getItemId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //проверка на доступность предмета для аренды
        if (itemRepository.findById(bookingCreateDto.getItemId()).get().getAvailable().equals(Boolean.FALSE)) {
            log.warn("Этот предмет не доступен для аренды");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        //проверка на адекватность срока бронирования
        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())
                || bookingCreateDto.getStart().isBefore(LocalDateTime.now())
                || bookingCreateDto.getEnd().isBefore(LocalDateTime.now())) {
            log.warn("Продолжительность аренды не верно указано");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Item> item = itemRepository.findById(bookingCreateDto.getItemId());
        //проверка на возможность бронирования владельца собственного предмета
        if (item.get().getOwnerId().equals(userId)) {
            log.warn("Пользователь id = {} является владельцем предмета id = {}", userId, bookingCreateDto.getItemId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return null;
    }

    private ResponseEntity<BookingDto> validateForSetStatus(Long userId, Long bookingId, Boolean approved) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (checkBookingById(bookingId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //Проверка на существование подтверждение
        if (approved == null) {
            log.warn("Параметр approved не был передан");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //проверка на повторное запрос подтверждения
        if (bookingRepository.findById(bookingId).get().getStatus().equals(Status.APPROVED)
                && approved.equals(Boolean.TRUE)) {
            log.warn("Повторное подтверждение бронирование не допустимо");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return null;
    }

    private ResponseEntity<BookingDto> checkBookerAndOwner(Long userId, Long bookingId) {
        if (checkUserById(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (checkBookingById(bookingId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return null;
    }

    private Boolean checkUserById(Long userId) {
        //проверка существования пользователя
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Пользователь с id = {} не найден", userId);
            return true;
        }
        return false;
    }

    private Boolean checkItemById(Long itemId) {
        //проверка существования предмета
        if (itemRepository.findById(itemId).isEmpty()) {
            log.warn("Предмет с id = {} не найден", itemId);
            return true;
        }
        return false;
    }

    private Boolean checkBookingById(Long bookingId) {
        // проверка существование бронирование по id
        if (bookingRepository.findById(bookingId).isEmpty()) {
            log.warn("Бронирование id = {} не найдено", bookingId);
            return true;
        }
        return false;
    }
}
