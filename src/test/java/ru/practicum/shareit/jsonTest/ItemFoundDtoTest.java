package ru.practicum.shareit.jsonTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.ItemFoundDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemFoundDtoTest {

    @Autowired
    JacksonTester<ItemFoundDto> json;

    @Test
    void testItemFoundDto() throws Exception {
        // Given
        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setBookerId(2L);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBookerId(3L);

        ItemFoundDto itemFoundDto = new ItemFoundDto();
        itemFoundDto.setId(1L);
        itemFoundDto.setName("test");
        itemFoundDto.setDescription("test description");
        itemFoundDto.setAvailable(true);
        itemFoundDto.setLastBooking(new ItemFoundDto.LastBooking(lastBooking));
        itemFoundDto.setNextBooking(new ItemFoundDto.NextBooking(nextBooking));

        // When
        JsonContent<ItemFoundDto> result = json.write(itemFoundDto);
        // Then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(3);
    }
}
