package ru.practicum.shareit.booking;


/**
 * Класс, который преобразовывает объект Item в DTO-объекты
 */
public class BookingMapper {

    public static BookingCreateDto toBookingCreateDto(Booking booking) {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setId(booking.getId());
        bookingCreateDto.setItemId(booking.getItemId());
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());
        return bookingCreateDto;
    }

    public static BookingDto toBookingDto(Booking booking, BookingDto.Item item, BookingDto.Booker booker) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);
        return bookingDto;
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto, Long userId) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItemId(bookingCreateDto.getItemId());
        booking.setBookerId(userId);
        return booking;
    }

}
