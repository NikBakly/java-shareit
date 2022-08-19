package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingCreateDto;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class BookingServiceImplTest {
    BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);

    private final String nameForItem = "Отвертка магнитная";
    private final String descriptionForItem = "Отвертка с магнитным наконечником";
    private final Boolean aBooleanForItem = Boolean.TRUE;


    private BookingServiceImpl bookingService;
    private Item itemForTest;
    private ItemDto itemDtoForTest;
    private User userForTest;


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
        itemForTest.setName(nameForItem);
        itemForTest.setDescription(descriptionForItem);
        itemForTest.setAvailable(aBooleanForItem);
        itemForTest.setOwnerId(1L);
    }

    @BeforeEach
    void initItemDto() {
        itemDtoForTest = ItemDto.builder()
                .id(1L)
                .name(nameForItem)
                .description(descriptionForItem)
                .available(Boolean.TRUE)
                .build();
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
        bookingForTest.setStatus(Status.APPROVED);
        bookingForTest.setStart(LocalDateTime.now().plusDays(1));
        bookingForTest.setEnd(LocalDateTime.now().plusDays(2));
        bookingForTest.setItemId(1L);
        bookingForTest.setBookerId(1L);
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
        Booking bookingForTest = new Booking();
        bookingForTest.setId(1L);
        bookingForTest.setStatus(Status.APPROVED);
        bookingForTest.setStart(LocalDateTime.now().plusDays(1));
        bookingForTest.setEnd(LocalDateTime.now().plusDays(2));
        bookingForTest.setItemId(1L);
        bookingForTest.setBookerId(1L);
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
        Booking bookingForTest = new Booking();
        bookingForTest.setId(1L);
        bookingForTest.setStatus(Status.APPROVED);
        bookingForTest.setStart(LocalDateTime.now().minusDays(1));
        bookingForTest.setEnd(LocalDateTime.now().plusDays(2));
        bookingForTest.setItemId(1L);
        bookingForTest.setBookerId(1L);
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
        Booking bookingForTest = new Booking();
        bookingForTest.setId(1L);
        bookingForTest.setStatus(Status.APPROVED);
        bookingForTest.setStart(LocalDateTime.now().minusDays(1));
        bookingForTest.setEnd(LocalDateTime.now().plusDays(2));
        bookingForTest.setItemId(1L);
        bookingForTest.setBookerId(1L);
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
}
