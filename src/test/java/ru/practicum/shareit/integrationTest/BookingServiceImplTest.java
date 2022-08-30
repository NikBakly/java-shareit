package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingCreateDto;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void findAllForUserTest() {
        // Given
        User user = createUser("Petr", "Petr@ml.ru");
        User otherUser = createUser("Max", "max@gm.com");
        ItemDto item = createItem("Отвертка электрическая", "заряжается через провод", user.getId());
        ItemDto otherItem = createItem("Отвертка стандартная", "нужен каждому рабочему", user.getId());
        createBooking(otherUser.getId(),
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        BookingDto bookingDto = createBooking(otherUser.getId(),
                otherItem.getId(),
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4)
        );

        // When
        List<BookingDto> bookingsDtoActual = bookingService.findAllForUser(otherUser.getId(), State.ALL, 0, 1);
        // Then
        Assertions.assertEquals(1, bookingsDtoActual.size());
        assertThat(bookingsDtoActual.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookingsDtoActual.get(0).getStatus(), equalTo(bookingDto.getStatus()));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userService.save(user);
    }

    private ItemDto createItem(String name, String description, Long userId) {
        ItemDto itemDto = ItemDto.builder()
                .name(name)
                .description(description)
                .available(Boolean.TRUE)
                .build();
        return itemService.create(itemDto, userId);
    }

    private BookingDto createBooking(Long userId, Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(itemId);
        bookingCreateDto.setStart(start);
        bookingCreateDto.setEnd(end);
        bookingCreateDto = bookingService.create(bookingCreateDto, userId);
        return bookingService.findById(userId, bookingCreateDto.getId());
    }
}
