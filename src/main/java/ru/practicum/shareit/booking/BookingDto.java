package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.status.Status;

import java.time.LocalDateTime;

/**
 * Класс, который предназначил для работы с пользователем
 */
@Getter
@Setter
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Item item = itemInitialization;
    private Booker booker = bookerInitialization;

    public static Item itemInitialization;
    public static Booker bookerInitialization;

    public static class Item {
        public Long id;
        public String name;

        public Item(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class Booker {
        public Long id;

        public Booker(Long id) {
            this.id = id;
        }
    }
}
