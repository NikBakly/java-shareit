package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.ItemRequestDto;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void getAllRequestsTest() {
        // Given
        User user = createUser("Petr", "petr@ml.ru");
        User otherUser = createUser("Max", "max@gm.com");
        ItemRequestDto itemRequestDto = makeToRequestDto("любая отвертка");
        itemRequestService.create(itemRequestDto, user.getId());
        ItemRequestDto otherItemRequestDto = makeToRequestDto("любой молоток");
        itemRequestService.create(otherItemRequestDto, user.getId());
        // When
        List<ItemRequestDto> itemRequestsDtoActual = itemRequestService.getAllRequests(otherUser.getId(), 0, 2);
        //Then
        Assertions.assertEquals(2, itemRequestsDtoActual.size());
        assertThat(itemRequestsDtoActual.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestsDtoActual.get(1).getDescription(), equalTo(otherItemRequestDto.getDescription()));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userService.save(user);
    }

    private ItemRequestDto makeToRequestDto(String description) {
        return ItemRequestDto.builder()
                .description(description)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

    }
}
