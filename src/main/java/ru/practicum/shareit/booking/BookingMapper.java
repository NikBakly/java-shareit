package ru.practicum.shareit.booking;


import java.util.ArrayList;
import java.util.List;

/**
 * Класс, который преобразовывает объект Item в DTO-объекты
 */
public class BookingMapper {

    public static BookingDtoForCreate toBookingDtoForCreate(Booking booking) {
        BookingDtoForCreate bookingDtoForCreate = new BookingDtoForCreate();
        bookingDtoForCreate.setId(booking.getId());
        bookingDtoForCreate.setItemId(booking.getItemId());
        bookingDtoForCreate.setStart(booking.getStart());
        bookingDtoForCreate.setEnd(booking.getEnd());
        return bookingDtoForCreate;
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static List<BookingDto> toBookingsDtoWithItemAndBooker(List<Booking> bookings) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        bookings.forEach(booking -> bookingsDto.add(toBookingDto(booking)));
        return bookingsDto;
    }

    public static Booking toBooking(BookingDtoForCreate bookingDtoForCreate, Long userId) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoForCreate.getStart());
        booking.setEnd(bookingDtoForCreate.getEnd());
        booking.setItemId(bookingDtoForCreate.getItemId());
        booking.setBookerId(userId);
        return booking;
    }

}