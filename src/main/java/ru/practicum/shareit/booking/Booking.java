package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.status.Status;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * POJO класс, описывающий поля объекта "Бронирование"
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date_time")
    private LocalDateTime start; // дата начала бронирования

    @Column(name = "end_date_time")
    private LocalDateTime end; // дата конца бронирования

    @Column(name = "item_id")
    private Long itemId; // id вещи, которую пользователь бронирует;

    @Column(name = "booker_id")
    private Long bookerId; // id пользователя, который осуществляет бронирование;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.WAITING; // статус бронирования

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id)
                && Objects.equals(start, booking.start)
                && Objects.equals(end, booking.end)
                && Objects.equals(itemId, booking.itemId)
                && Objects.equals(bookerId, booking.bookerId)
                && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, itemId, bookerId, status);
    }
}
