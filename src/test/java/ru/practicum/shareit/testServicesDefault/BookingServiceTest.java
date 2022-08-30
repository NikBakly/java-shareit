package ru.practicum.shareit.testServicesDefault;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BookingServiceTest {
    BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);

    private BookingServiceImpl bookingService;
    private Item itemForTest;
    private User userForTest;
    private Booking bookingForTest;


    @BeforeEach
    void initUser() {
        userForTest = new User();
        userForTest.setId(2L);
        userForTest.setName("Petr");
        userForTest.setEmail("Petr@mail.com");
    }

    @BeforeEach
    void initItem() {
        itemForTest = new Item();
        itemForTest.setId(1L);
        itemForTest.setName("Отвертка магнитная");
        itemForTest.setDescription("Отвертка с магнитным наконечником");
        itemForTest.setAvailable(Boolean.TRUE);
        itemForTest.setOwnerId(1L);
    }

    @BeforeEach
    void initBooking() {
        bookingForTest = new Booking();
        bookingForTest.setId(1L);
        bookingForTest.setStart(LocalDateTime.now().plusDays(1));
        bookingForTest.setEnd(LocalDateTime.now().plusDays(2));
        bookingForTest.setItemId(1L);
        bookingForTest.setBookerId(2L);
    }

    @BeforeEach
    void initBookingService() {
        bookingService = new BookingServiceImpl();
    }

    @Test
    void test1_createBooking() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Booking bookingForTest = new Booking();
        bookingForTest.setId(1L);
        bookingForTest.setStart(LocalDateTime.now().plusDays(1));
        bookingForTest.setEnd(LocalDateTime.now().plusDays(2));
        bookingForTest.setItemId(1L);
        bookingForTest.setBookerId(2L);
        Mockito
                .when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookingForTest);
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        BookingCreateDto bookingCreateDtoForTest = new BookingCreateDto();
        bookingCreateDtoForTest.setItemId(1L);
        bookingCreateDtoForTest.setStart(bookingForTest.getStart());
        bookingCreateDtoForTest.setEnd(bookingForTest.getEnd());

        BookingCreateDto bookingCreateDtoActual = bookingService.create(bookingCreateDtoForTest, 2L);
        //Then
        Assertions.assertTrue(bookingCreateDtoActual.getItemId().equals(itemForTest.getId())
                && bookingCreateDtoActual.getStart() != null
                && bookingCreateDtoActual.getEnd() != null);
    }

    @Test
    void test2_createBooking_whenUserIsOwner() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookingForTest);
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        BookingCreateDto bookingCreateDtoForTest = new BookingCreateDto();
        bookingCreateDtoForTest.setItemId(1L);
        bookingCreateDtoForTest.setStart(bookingForTest.getStart());
        bookingCreateDtoForTest.setEnd(bookingForTest.getEnd());

        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.create(bookingCreateDtoForTest, 1L));
        //Then
        Assertions.assertEquals("Пользователь id = 1 является владельцем предмета id = 1", thrown.getMessage());
    }

    @Test
    void test3_createBooking_whenStartBookingIsPast() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        bookingForTest.setStart(LocalDateTime.now().minusDays(1));
        bookingForTest.setEnd(LocalDateTime.now().plusDays(2));
        Mockito
                .when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookingForTest);
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        BookingCreateDto bookingCreateDtoForTest = new BookingCreateDto();
        bookingCreateDtoForTest.setItemId(1L);
        bookingCreateDtoForTest.setStart(bookingForTest.getStart());
        bookingCreateDtoForTest.setEnd(bookingForTest.getEnd());

        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> bookingService.create(bookingCreateDtoForTest, 2L));
        //Then
        Assertions.assertEquals("Продолжительность аренды не верно указано", thrown.getMessage());
    }

    @Test
    void test4_createBooking_whenItemAvailableIsFalse() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemForTest.setAvailable(Boolean.FALSE);
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookingForTest);
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        BookingCreateDto bookingCreateDtoForTest = new BookingCreateDto();
        bookingCreateDtoForTest.setItemId(1L);
        bookingCreateDtoForTest.setStart(bookingForTest.getStart());
        bookingCreateDtoForTest.setEnd(bookingForTest.getEnd());

        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> bookingService.create(bookingCreateDtoForTest, 2L));
        //Then
        Assertions.assertEquals("Этот предмет не доступен для аренды", thrown.getMessage());
    }

    @Test
    void test5_setStatus_whenApprovedIsTrue() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        BookingDto bookingDtoActual = bookingService.setStatus(1L, 1L, Boolean.TRUE);
        //Then
        Assertions.assertTrue(bookingDtoActual.getItem().id.equals(itemForTest.getId())
                && bookingDtoActual.getStart() != null
                && bookingDtoActual.getEnd() != null
                && bookingDtoActual.getBooker().id.equals(2L)
                && bookingDtoActual.getStatus().equals(Status.APPROVED));
    }

    @Test
    void test6_setStatus_whenApprovedIsFalse() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        BookingDto bookingDtoActual = bookingService.setStatus(1L, 1L, Boolean.FALSE);
        //Then
        Assertions.assertTrue(bookingDtoActual.getItem().id.equals(itemForTest.getId())
                && bookingDtoActual.getStart() != null
                && bookingDtoActual.getEnd() != null
                && bookingDtoActual.getBooker().id.equals(2L)
                && bookingDtoActual.getStatus().equals(Status.REJECTED));
    }

    @Test
    void test7_setStatus_whenUserIsNotOwner() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        Mockito
                .when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookingForTest);
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.setStatus(2L, 1L, Boolean.FALSE));
        //Then
        Assertions.assertEquals("Пользователь id = 2 не является владельцем предмета", thrown.getMessage());
    }

    @Test
    void test8_setStatus_whenBookingIsApproved() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        bookingForTest.setStatus(Status.APPROVED);
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> bookingService.setStatus(1L, 1L, Boolean.TRUE));
        //Then
        Assertions.assertEquals("Повторное подтверждение бронирование не допустимо", thrown.getMessage());
    }

    @Test
    void test9_setStatus_whenApprovedIsNull() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> bookingService.setStatus(1L, 1L, null));
        //Then
        Assertions.assertEquals("Параметр approved не был передан", thrown.getMessage());
    }

    @Test
    void test10_setStatus_whenBookingIdIsWrong() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.setStatus(1L, 99L, Boolean.TRUE));
        //Then
        Assertions.assertEquals("Бронирование id = 99 не найдено", thrown.getMessage());
    }

    @Test
    void test11_setStatus_whenUserIdIsWrong() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        bookingService.setUserRepository(mockUserRepository);
        //When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.setStatus(99L, 1L, Boolean.TRUE));
        //Then
        Assertions.assertEquals("Пользователь с id = 99 не найден", thrown.getMessage());
    }

    @Test
    void test12_findById() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        BookingDto bookingDtoActual = bookingService.findById(1L, 1L);
        //Then
        Assertions.assertTrue(bookingDtoActual.getId().equals(bookingForTest.getId())
                && bookingDtoActual.getStatus().equals(bookingForTest.getStatus())
                && bookingDtoActual.getItem().id.equals(bookingForTest.getItemId())
                && bookingDtoActual.getBooker().id.equals(bookingForTest.getBookerId()));
    }

    @Test
    void test13_findById_whenUserIsNotOwnerOrBooker() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.findById(3L, 1L));
        //Then
        Assertions.assertEquals("Пользователь id = 3 не является владельцем предмета или автором бронирования",
                thrown.getMessage());
    }

    @Test
    void test14_findById_whenBookingIdIsWrong() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.findById(1L, 99L));
        //Then
        Assertions.assertEquals("Бронирование id = 99 не найдено", thrown.getMessage());
    }

    @Test
    void test15_findById_whenUserIdIsWrong() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        bookingService.setUserRepository(mockUserRepository);
        //When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.findById(99L, 1L));
        //Then
        Assertions.assertEquals("Пользователь с id = 99 не найден", thrown.getMessage());
    }

    @Test
    void test15_findAllForUser_whenFromIsNull() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        Mockito
                .when(mockBookingRepository.findAllByBookerIdOrderByEndDesc(Mockito.anyLong()))
                .thenReturn(List.of(bookingForTest));
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        List<BookingDto> bookingsDtoActual = bookingService.findAllForUser(2L, State.ALL, null, 1);
        //Then
        Assertions.assertEquals(1, bookingsDtoActual.size());
        BookingDto bookingDtoActual = bookingsDtoActual.get(0);
        Assertions.assertEquals(bookingForTest.getId(), bookingDtoActual.getId());
        Assertions.assertEquals(bookingForTest.getBookerId(), bookingDtoActual.getBooker().id);
        Assertions.assertEquals(bookingForTest.getItemId(), bookingDtoActual.getItem().id);
    }

    @Test
    void test15_findAllForUser() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        Mockito
                .when(mockBookingRepository.findAllByBookerIdOrderByEndDesc(Mockito.anyLong()))
                .thenReturn(List.of(bookingForTest));
        Mockito
                .when(mockBookingRepository.findAllByBookerIdOrderByEndDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(bookingForTest)));
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        List<BookingDto> bookingsDtoActual = bookingService.findAllForUser(2L, State.ALL, 0, 1);
        //Then
        Assertions.assertEquals(1, bookingsDtoActual.size());
        BookingDto bookingDtoActual = bookingsDtoActual.get(0);
        Assertions.assertEquals(bookingForTest.getId(), bookingDtoActual.getId());
        Assertions.assertEquals(bookingForTest.getBookerId(), bookingDtoActual.getBooker().id);
        Assertions.assertEquals(bookingForTest.getItemId(), bookingDtoActual.getItem().id);
    }

    @Test
    void test15_findAllForOwner() {
        //Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingForTest));
        Mockito
                .when(mockBookingRepository.findAllByOwnerIdOrderByEndDesc(Mockito.anyLong()))
                .thenReturn(List.of(bookingForTest));
        Mockito
                .when(mockBookingRepository.findAllByOwnerIdOrderByEndDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(bookingForTest)));
        bookingService.setUserRepository(mockUserRepository);
        bookingService.setItemRepository(mockItemRepository);
        bookingService.setBookingRepository(mockBookingRepository);
        //When
        List<BookingDto> bookingsDtoActual = bookingService.findAllForOwner(1L, State.ALL, 0, 1);
        //Then
        Assertions.assertEquals(1, bookingsDtoActual.size());
        BookingDto bookingDtoActual = bookingsDtoActual.get(0);
        Assertions.assertEquals(bookingForTest.getId(), bookingDtoActual.getId());
        Assertions.assertEquals(bookingForTest.getBookerId(), bookingDtoActual.getBooker().id);
        Assertions.assertEquals(bookingForTest.getItemId(), bookingDtoActual.getItem().id);
    }


}
