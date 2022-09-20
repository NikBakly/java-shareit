package ru.practicum.shareit.repositoryTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

@DataJpaTest()
public class ItemRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void test1_findItemsByText() {
        User user = createUser();
        em.persist(user);
        Item item = createItem(user.getId());
        em.persist(item);

        List<Item> itemsActual = itemRepository.findItemsByText("test");

        Assertions.assertEquals(1, itemsActual.size());
        Assertions.assertEquals(item, itemsActual.get(0));
    }

    private User createUser() {
        User user = new User();
        user.setName("petr");
        user.setEmail("petr@gm.com");
        return user;
    }

    private Item createItem(Long ownerId) {
        Item item = new Item();
        item.setName("test");
        item.setDescription("test description");
        item.setAvailable(Boolean.TRUE);
        item.setOwnerId(ownerId);
        return item;
    }


}
