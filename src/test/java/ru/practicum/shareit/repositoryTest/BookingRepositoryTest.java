package ru.practicum.shareit.repositoryTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Transactional
public class BookingRepositoryTest {
    @Autowired
    TestEntityManager em;

    @Autowired
    BookingRepository bookingRepository;

    private final User user = new User();
    private final Item item = new Item();
    private final Booking booking = new Booking();

    @BeforeEach
    void setUp() {
        user.setName("petr");
        user.setEmail("petr@gm.com");
        em.persist(user);

        item.setName("test");
        item.setDescription("test description");
        item.setAvailable(true);
        item.setOwnerId(user.getId());
        em.persist(item);

        //Инициализация пользователя как арендодателя
        User otherUser = new User();
        otherUser.setName("max");
        otherUser.setEmail("max@gm.com");
        em.persist(otherUser);

        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItemId(item.getId());
        booking.setBookerId(otherUser.getId());
        booking.setStatus(Status.APPROVED);
        em.persist(booking);
    }

    @Test
    void test1_findAllByOwnerIdOrderByEndDesc() {
        //When
        List<Booking> bookingsActual = bookingRepository.findAllByOwnerIdOrderByEndDesc(user.getId());

        // Then
        Assertions.assertEquals(1, bookingsActual.size());
        Assertions.assertEquals(booking, bookingsActual.get(0));
    }

    @Test
    void test2_findAllByOwnerIdAndStatusOrderByEndDesc() {

        // When
        List<Booking> bookingsActual = bookingRepository.findAllByOwnerIdAndStatusOrderByEndDesc(user.getId(), Status.APPROVED);
        // Then
        Assertions.assertEquals(1, bookingsActual.size());
        Assertions.assertEquals(booking, bookingsActual.get(0));
    }

    @Test
    void test3_findTwoBookingByOwnerIdOrderByEndAsc() {

        // When
        List<Booking> bookingsActual = bookingRepository.findTwoBookingByOwnerIdOrderByEndAsc(user.getId(), item.getId());
        // Then
        Assertions.assertEquals(1, bookingsActual.size());
        Assertions.assertEquals(booking, bookingsActual.get(0));
    }

}
