package ru.practicum.shareit.controllerTestWithContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.HeaderKey;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingCreateDto;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.status.Status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mvc;

    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setItem(new BookingDto.Item(1L, "test"));
        bookingDto.setBooker(new BookingDto.Booker(2L));
    }

    @Test
    void test1_createNewBooking() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setId(1L);
        bookingCreateDto.setStart(bookingDto.getStart());
        bookingCreateDto.setEnd(bookingDto.getEnd());
        bookingCreateDto.setItemId(bookingDto.getItem().id);
        Mockito
                .when(bookingService.create(any(), anyLong()))
                .thenReturn(bookingCreateDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingCreateDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingCreateDto.getItemId()), Long.class));
    }

    @Test
    void test2_setStatusBooking() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        Mockito
                .when(bookingService.setStatus(any(), anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().id), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().name), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().id), Long.class));
    }

    @Test
    void test3_findById() throws Exception {
        Mockito
                .when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().id), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().name), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().id), Long.class));
    }

    @Test
    void test4_findAllForUser() throws Exception {
        Mockito
                .when(bookingService.findAllForUser(anyLong(), any(), any(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 2L)
                        .queryParam("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().id), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().name), String.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().id), Long.class));
    }

    @Test
    void test4_findAllForOwner() throws Exception {
        Mockito
                .when(bookingService.findAllForOwner(anyLong(), any(), any(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 1L)
                        .queryParam("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().id), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().name), String.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().id), Long.class));
    }
}
