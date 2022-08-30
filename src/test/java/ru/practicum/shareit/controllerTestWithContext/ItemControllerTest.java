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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemFoundDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.CommentDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mvc;

    ItemDto itemDto;

    ItemFoundDto itemFoundDto = new ItemFoundDto();


    @BeforeEach
    void setUp() {
        Long id = 1L;
        String name = "test";
        String description = "test description";
        itemDto = ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .requestId(1L)
                .available(true)
                .build();

        itemFoundDto.setId(id);
        itemFoundDto.setName(name);
        itemFoundDto.setDescription(description);
        itemFoundDto.setAvailable(true);
        itemFoundDto.setLastBooking(null);
        itemFoundDto.setNextBooking(null);
        itemFoundDto.setComments(List.of());
    }

    @Test
    void test1_createNewItem() throws Exception {
        Mockito
                .when(itemService.create(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void test2_updateItem() throws Exception {
        itemDto.setDescription("update description");
        Mockito
                .when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void test3_getAllItems() throws Exception {
        Mockito
                .when(itemService.findAllItemsByUserId(any(), any(), any()))
                .thenReturn(List.of(itemFoundDto));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemFoundDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemFoundDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemFoundDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemFoundDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemFoundDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].lastBooking", is(itemFoundDto.getLastBooking()), ItemFoundDto.LastBooking.class))
                .andExpect(jsonPath("$[0].nextBooking", is(itemFoundDto.getNextBooking()), ItemFoundDto.NextBooking.class))
                .andExpect(jsonPath("$[0].comments", is(itemFoundDto.getComments()), List.class));
    }

    @Test
    void test4_findItemByItemId() throws Exception {
        Mockito
                .when(itemService.findByUserIdAndItemId(anyLong(), anyLong()))
                .thenReturn(itemFoundDto);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemFoundDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemFoundDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemFoundDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemFoundDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemFoundDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.lastBooking", is(itemFoundDto.getLastBooking()), ItemFoundDto.LastBooking.class))
                .andExpect(jsonPath("$.nextBooking", is(itemFoundDto.getNextBooking()), ItemFoundDto.NextBooking.class))
                .andExpect(jsonPath("$.comments", is(itemFoundDto.getComments()), List.class));
    }

    @Test
    void test5_findItemByText() throws Exception {
        Mockito
                .when(itemService.findItemByText(anyLong(), any(), any(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 1L)
                        .queryParam("text", "test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void test5_createComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("test comment");
        commentDto.setAuthorName("userName");
        commentDto.setCreated(LocalDateTime.now());

        Mockito
                .when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderKey.USER_KEY, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));
    }
}
