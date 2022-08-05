package ru.practicum.shareit.booking;


/**
 * Класс, который преобразовывает объект Item в DTO-объекты
 */
public class BookingMapper {

    public static BookingCreateDto toBookingCreateDto(Booking booking) {
        BookingCreateDto BookingCreateDto = new BookingCreateDto();
        BookingCreateDto.setId(booking.getId());
        BookingCreateDto.setItemId(booking.getItemId());
        BookingCreateDto.setStart(booking.getStart());
        BookingCreateDto.setEnd(booking.getEnd());
        return BookingCreateDto;
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

    public static Booking toBooking(BookingCreateDto BookingCreateDto, Long userId) {
        Booking booking = new Booking();
        booking.setStart(BookingCreateDto.getStart());
        booking.setEnd(BookingCreateDto.getEnd());
        booking.setItemId(BookingCreateDto.getItemId());
        booking.setBookerId(userId);
        return booking;
    }

}
