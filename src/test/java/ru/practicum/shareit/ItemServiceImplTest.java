package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ItemServiceImplTest {
    private final ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    private final UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    private final CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);
    private final BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);

    private final String nameForItem = "Отвертка магнитная";
    private final String descriptionForItem = "Отвертка с магнитным наконечником";
    private final Boolean aBooleanForItem = Boolean.TRUE;

    private ItemServiceImpl itemService;
    private Item itemForTest;
    private ItemDto itemDtoForTest;
    private User userForTest;


    @BeforeEach
    void initUser() {
        userForTest = new User();
        userForTest.setId(1L);
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
    void initItemService() {
        itemService = new ItemServiceImpl();
    }

    @Test
    void test1_createItem() {
        // Given
        Mockito
                .when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(itemForTest);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemService.setItemRepository(mockItemRepository);
        itemService.setUserRepository(mockUserRepository);
        // When
        ItemDto itemDtoActual = itemService.create(itemDtoForTest, 1L);
        // Then
        Assertions.assertTrue(itemDtoActual.getId().equals(itemForTest.getId())
                && itemDtoActual.getName().equals(itemForTest.getName())
                && itemDtoActual.getDescription().equals(itemForTest.getDescription())
                && itemDtoActual.getAvailable().equals(itemForTest.getAvailable()));
    }

    @Test
    void test2_createItem_whenUserIdIsWrong() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        itemService.setUserRepository(mockUserRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> itemService.create(itemDtoForTest, 99L));
        // Then
        Assertions.assertEquals("Пользователь с id = 99 не найден", thrown.getMessage());
    }

    @Test
    void test3_createItem_whenAvailableIsNull() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemService.setUserRepository(mockUserRepository);
        // When
        itemDtoForTest.setAvailable(null);
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemService.create(itemDtoForTest, 1L));
        // Then
        Assertions.assertEquals("У вещи нету статуса аренды", thrown.getMessage());
    }

    @Test
    void test4_createItem_whenNameIsNull() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemService.setUserRepository(mockUserRepository);
        // When
        itemDtoForTest.setName(null);
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemService.create(itemDtoForTest, 1L));
        // Then
        Assertions.assertEquals("У вещи нету названия", thrown.getMessage());
    }

    @Test
    void test5_createItem_whenNameIsBlank() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemService.setUserRepository(mockUserRepository);
        // When
        itemDtoForTest.setName(" ");
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemService.create(itemDtoForTest, 1L));
        // Then
        Assertions.assertEquals("У вещи нету названия", thrown.getMessage());
    }

    @Test
    void test6_createItem_whenDescriptionIsNull() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemService.setUserRepository(mockUserRepository);
        // When
        itemDtoForTest.setDescription(null);
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemService.create(itemDtoForTest, 1L));
        // Then
        Assertions.assertEquals("У вещи нету описания", thrown.getMessage());
    }

    @Test
    void test7_createItem_whenDescriptionIsBlank() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemService.setUserRepository(mockUserRepository);
        // When
        itemDtoForTest.setDescription(" ");
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemService.create(itemDtoForTest, 1L));
        // Then
        Assertions.assertEquals("У вещи нету описания", thrown.getMessage());
    }

    @Test
    void test8_updateItem() {
        // Given
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        itemDtoForTest.setName("Отвертка обычная");
        itemDtoForTest.setDescription("Крестовая отвертка");
        itemDtoForTest.setAvailable(Boolean.FALSE);
        Mockito
                .when(mockItemRepository.findAllByOwnerId(Mockito.anyLong()))
                .thenReturn(List.of(itemForTest));
        Mockito
                .when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(itemForTest);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemService.setItemRepository(mockItemRepository);
        itemService.setUserRepository(mockUserRepository);
        // When
        ItemDto itemDtoActual = itemService.update(itemDtoForTest, 1L, 1L);
        // Then
        Assertions.assertTrue(itemDtoActual.getId().equals(itemForTest.getId())
                && itemDtoActual.getName().equals(itemDtoForTest.getName())
                && itemDtoActual.getDescription().equals(itemDtoForTest.getDescription())
                && itemDtoActual.getAvailable().equals(itemDtoForTest.getAvailable()));
    }

    @Test
    void test9_updateItem_whenUserIdIsWrong() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        itemService.setUserRepository(mockUserRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> itemService.update(itemDtoForTest, 99L, 1L));
        // Then
        Assertions.assertEquals("Пользователь с id = 99 не найден", thrown.getMessage());
    }

    @Test
    void test10_updateItem_whenItemOwnerIdIsWrong() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findAllByOwnerId(Mockito.anyLong()))
                .thenReturn(null);
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> itemService.update(itemDtoForTest, 99L, 1L));
        // Then
        Assertions.assertEquals("У пользователя id = 99 нету вещей для аренды", thrown.getMessage());
    }

    @Test
    void test11_updateItem_whenItemIdIsWrong() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Item otherItem = new Item();
        otherItem.setId(2L);
        Mockito
                .when(mockItemRepository.findAllByOwnerId(Mockito.anyLong()))
                .thenReturn(List.of(otherItem, itemForTest));
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        // When
        ForbiddenException thrown = Assertions
                .assertThrows(ForbiddenException.class, () -> itemService.update(itemDtoForTest, 99L, 58L));
        // Then
        Assertions.assertEquals("У пользователя id = 99 нету прав на вещь id = 58", thrown.getMessage());
    }

    @Test
    void test12_findByUserIdAndItemId_whenItemWithoutComments_andUserIsNotOwner() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockCommentRepository.findAllByItemId(itemDtoForTest.getId()))
                .thenReturn(List.of());
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        itemService.setCommentRepository(mockCommentRepository);
        // When
        ItemFoundDto itemFoundDtoActual = itemService.findByUserIdAndItemId(2L, 1L);
        // Then
        Assertions.assertTrue(itemFoundDtoActual.getId().equals(itemForTest.getId())
                && itemFoundDtoActual.getName().equals(nameForItem)
                && itemFoundDtoActual.getDescription().equals(descriptionForItem)
                && itemFoundDtoActual.getAvailable().equals(aBooleanForItem)
                && itemFoundDtoActual.getComments().size() == 0
                && itemFoundDtoActual.getLastBooking() == null
                && itemFoundDtoActual.getNextBooking() == null);
    }

    @Test
    void test13_findByUserIdAndItemId_whenItemWithoutComments_andUserIsOwner() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockCommentRepository.findAllByItemId(itemDtoForTest.getId()))
                .thenReturn(List.of());
        Mockito
                .when(mockBookingRepository.findTwoBookingByOwnerIdOrderByEndAsc(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of());
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        itemService.setCommentRepository(mockCommentRepository);
        itemService.setBookingRepository(mockBookingRepository);
        // When
        ItemFoundDto itemFoundDtoActual = itemService.findByUserIdAndItemId(itemForTest.getOwnerId(), 1L);
        // Then
        Assertions.assertTrue(itemFoundDtoActual.getId().equals(itemForTest.getId())
                && itemFoundDtoActual.getName().equals(nameForItem)
                && itemFoundDtoActual.getDescription().equals(descriptionForItem)
                && itemFoundDtoActual.getAvailable().equals(aBooleanForItem)
                && itemFoundDtoActual.getComments().size() == 0
                && itemFoundDtoActual.getLastBooking() == null
                && itemFoundDtoActual.getNextBooking() == null);
    }

    @Test
    void test14_findByUserIdAndItemId_whenUserIdIsWrong() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> itemService.findByUserIdAndItemId(99L, 1L));
        // Then
        Assertions.assertEquals("Пользователь с id = 99 не найден", thrown.getMessage());
    }

    @Test
    void test15_addComment() {
        // Given
        userForTest.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Comment commentForTest = new Comment();
        commentForTest.setId(1L);
        commentForTest.setText("test");
        commentForTest.setCreated(LocalDateTime.now());
        Mockito
                .when(mockCommentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(commentForTest);
        Booking bookingForTest = new Booking();
        bookingForTest.setStatus(Status.APPROVED);
        bookingForTest.setEnd(LocalDateTime.now().minusDays(3));
        Mockito
                .when(mockBookingRepository.findByBookerIdAndItemId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(bookingForTest));
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        itemService.setCommentRepository(mockCommentRepository);
        itemService.setBookingRepository(mockBookingRepository);
        // When
        CommentDto commentDtoActual = itemService.addComment(2L, 1L, commentForTest);
        // Then
        Assertions.assertTrue(commentDtoActual.getText().equals(commentForTest.getText())
                && commentDtoActual.getCreated() != null
                && commentDtoActual.getAuthorName().equals(userForTest.getName()));
    }

    @Test
    void test16_addComment_whenBookingsIsNotApproved() {
        // Given
        userForTest.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Booking bookingForTest = new Booking();
        bookingForTest.setStatus(Status.REJECTED);
        bookingForTest.setEnd(LocalDateTime.now().minusDays(3));
        Mockito
                .when(mockBookingRepository.findByBookerIdAndItemId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(bookingForTest));
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        itemService.setBookingRepository(mockBookingRepository);
        // When
        Comment commentForTest = new Comment();
        commentForTest.setId(1L);
        commentForTest.setText("test");
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemService.addComment(2L, 1L, commentForTest));
        // Then
        Assertions.assertEquals("Бронирование не подтверждено или не закончился срок аренды ", thrown.getMessage());
    }

    @Test
    void test17_addComment_whenBookingsIsNull() {
        // Given
        userForTest.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        Mockito
                .when(mockBookingRepository.findByBookerIdAndItemId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(null);
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        itemService.setBookingRepository(mockBookingRepository);
        // When
        Comment commentForTest = new Comment();
        commentForTest.setId(1L);
        commentForTest.setText("test");
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemService.addComment(2L, 1L, commentForTest));
        // Then
        Assertions.assertEquals("Пользователь id = 2 не арендовывал предмет id = 1", thrown.getMessage());
    }

    @Test
    void test18_addComment_whenCommentTextIsBlank() {
        // Given
        userForTest.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForTest));
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        // When
        Comment commentForTest = new Comment();
        commentForTest.setId(1L);
        commentForTest.setText(" ");
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemService.addComment(2L, 1L, commentForTest));
        // Then
        Assertions.assertEquals("Комментарий не может быть пустым", thrown.getMessage());
    }

    @Test
    void test18_addComment_whenItemIdIWrong() {
        // Given
        userForTest.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> itemService.addComment(2L, 99L, new Comment()));
        // Then
        Assertions.assertEquals("Вещь id = 99 не найдена", thrown.getMessage());
    }

    @Test
    void test19_addComment_whenUserIdIsWrong() {
        // Given
        userForTest.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        itemService.setUserRepository(mockUserRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> itemService.addComment(99L, 1L, new Comment()));
        // Then
        Assertions.assertEquals("Пользователь с id = 99 не найден", thrown.getMessage());
    }

    @Test
    void test20_findItemByText() {
        // Given
        userForTest.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        String text = "Отвертка";
        Mockito
                .when(mockItemRepository.findItemsByText(text))
                .thenReturn(List.of(itemForTest));
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        // When
        List<ItemDto> itemsDtoExpected = List.of(itemDtoForTest);
        List<ItemDto> itemsDtoActual = itemService.findItemByText(1L, text, null, null);
        // Then
        Assertions.assertArrayEquals(itemsDtoExpected.toArray(), itemsDtoActual.toArray());
    }

    @Test
    void test21_findItemByText_whenTextIsBlank() {
        // Given
        userForTest.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        String text = " ";
        Mockito
                .when(mockItemRepository.findItemsByText(text))
                .thenReturn(List.of(itemForTest));
        itemService.setUserRepository(mockUserRepository);
        itemService.setItemRepository(mockItemRepository);
        // When
        List<ItemDto> itemsDtoActual = itemService.findItemByText(1L, text, null, null);
        // Then
        Assertions.assertEquals(0, itemsDtoActual.size());
    }

    @Test
    void test22_findItemByText_whenUserIdIsWrong() {
        // Given
        userForTest.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        String text = " ";
        itemService.setUserRepository(mockUserRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> itemService.findItemByText(99L, text, null, null));
        // Then
        Assertions.assertEquals("Пользователь с id = 99 не найден", thrown.getMessage());
    }

}
