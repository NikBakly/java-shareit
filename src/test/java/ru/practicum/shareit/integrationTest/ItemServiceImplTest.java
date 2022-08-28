package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void findItemByTextTest() {
        // Given
        User user = createUser("Petr", "Petr@ml.ru");
        User otherUser = createUser("Max", "max@gm.com");

        ItemDto item = createItem("Отвертка электрическая", "заряжается через провод", user.getId());
        ItemDto otherItem = createItem("Отвертка стандартная", "нужен каждому рабочему", user.getId());
        // When
        List<ItemDto> itemsDtoActual = itemService.findItemByText(otherUser.getId(), "отвертка", 0, 2);
        // Then
        Assertions.assertEquals(2, itemsDtoActual.size());
        assertThat(itemsDtoActual.get(0), equalTo(item));
        assertThat(itemsDtoActual.get(1), equalTo(otherItem));
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
}
