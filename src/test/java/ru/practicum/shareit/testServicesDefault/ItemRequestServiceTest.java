package ru.practicum.shareit.testServicesDefault;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class ItemRequestServiceTest {
    static ItemRequest itemRequestForTest;
    static Item itemForTest;

    private final UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    private final ItemRequestRepository mockItemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    private final ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);

    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void initItemRequestService() {
        itemRequestService = new ItemRequestServiceImpl();
    }

    @BeforeAll
    static void initItemRequest() {
        itemRequestForTest = new ItemRequest();
        itemRequestForTest.setId(1L);
        itemRequestForTest.setDescription("Любая отвертка");
        itemRequestForTest.setCreated(LocalDateTime.now());
        itemRequestForTest.setRequesterId(1L);
    }

    @BeforeAll
    static void initItem() {
        itemForTest = new Item();
        itemForTest.setId(1L);
        itemForTest.setName("Магнитная отвертка");
        itemForTest.setDescription("Отвертка имеет магнитный наконечник");
        itemForTest.setOwnerId(2L);
        itemForTest.setAvailable(Boolean.TRUE);
        itemForTest.setRequestId(1L);
    }

    @Test
    void test1_createRequest() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequestForTest);
        itemRequestService.setUserRepository(mockUserRepository);
        itemRequestService.setItemRequestRepository(mockItemRequestRepository);
        ItemRequestDto itemRequestDtoForTest = ItemRequestDto.builder().description("test").build();

        // When
        ItemRequestDto itemRequestDtoAnswer = itemRequestService.create(itemRequestDtoForTest, 1L);

        // Then
        Assertions.assertTrue(itemRequestDtoAnswer.getId() == 1L
                && itemRequestDtoAnswer.getDescription().equals("Любая отвертка")
                && itemRequestDtoAnswer.getCreated() != null
                && itemRequestDtoAnswer.getItems().size() == 0);

    }

    @Test
    void test2_createRequest_whenDescriptionIsNull() {
        // Given
        ItemRequestDto itemRequestDtoForTest = ItemRequestDto.builder().description(null).build();
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemRequestService.setUserRepository(mockUserRepository);
        // When
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> itemRequestService.create(itemRequestDtoForTest, 1L));
        // Then
        Assertions.assertEquals("Описание запроса должно быть определенно", thrown.getMessage());
    }

    @Test
    void test3_createRequest_whenFoundUserIsWrong() {
        // Given
        ItemRequestDto itemRequestDtoForTest = ItemRequestDto.builder().description(null).build();
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        itemRequestService.setUserRepository(mockUserRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> itemRequestService.create(itemRequestDtoForTest, 101L));
        // Then
        Assertions.assertEquals("Пользователь с id = 101 не найден", thrown.getMessage());
    }

    @Test
    void test4_findAllRequestsByOwnerId() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestForTest));
        Mockito
                .when(mockItemRepository.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(itemForTest));
        itemRequestService.setUserRepository(mockUserRepository);
        itemRequestService.setItemRequestRepository(mockItemRequestRepository);
        itemRequestService.setItemRepository(mockItemRepository);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name(itemForTest.getName())
                .description(itemForTest.getDescription())
                .available(itemForTest.getAvailable())
                .requestId(itemForTest.getRequestId())
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(itemRequestForTest.getDescription())
                .created(itemRequestForTest.getCreated())
                .items(List.of(itemDto))
                .build();
        List<ItemRequestDto> itemRequestsDtoExpected = List.of(itemRequestDto);
        // When
        List<ItemRequestDto> itemRequestsDtoActual = itemRequestService.findAllRequestsByOwnerId(1L);
        // Then
        Assertions.assertArrayEquals(itemRequestsDtoExpected.toArray(), itemRequestsDtoActual.toArray());
    }

    @Test
    void test5_findAllRequestsByOwnerIdIsWrong() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        itemRequestService.setUserRepository(mockUserRepository);

        // When
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.findAllRequestsByOwnerId(101L));
        // Then
        Assertions.assertEquals("Пользователь с id = 101 не найден", thrown.getMessage());
    }

    @Test
    void test6_findAllRequestsByNoOwnerId() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestForTest));
        Mockito
                .when(mockItemRepository.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(itemForTest));
        itemRequestService.setUserRepository(mockUserRepository);
        itemRequestService.setItemRequestRepository(mockItemRequestRepository);
        itemRequestService.setItemRepository(mockItemRepository);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(itemRequestForTest.getDescription())
                .created(itemRequestForTest.getCreated())
                .items(List.of())
                .build();
        List<ItemRequestDto> itemRequestsDtoExpected = List.of(itemRequestDto);
        // When
        List<ItemRequestDto> itemRequestsDtoActual = itemRequestService.findAllRequestsByOwnerId(2L);
        // Then
        Assertions.assertArrayEquals(itemRequestsDtoExpected.toArray(), itemRequestsDtoActual.toArray());
    }

    @Test
    void test7_getAllRequests_whenFromIsNullAndSizeIsNull() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        ItemRequest itemRequestForTest2 = new ItemRequest();
        itemRequestForTest2.setId(2L);
        itemRequestForTest2.setDescription("Любой молоток");
        itemRequestForTest2.setCreated(LocalDateTime.now());
        itemRequestForTest2.setRequesterId(2L);
        Mockito
                .when(mockItemRequestRepository.findAllByOrderByCreatedAsc())
                .thenReturn(List.of(itemRequestForTest2, itemRequestForTest));
        Mockito
                .when(mockItemRepository.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(itemForTest));
        itemRequestService.setUserRepository(mockUserRepository);
        itemRequestService.setItemRequestRepository(mockItemRequestRepository);
        itemRequestService.setItemRepository(mockItemRepository);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name(itemForTest.getName())
                .description(itemForTest.getDescription())
                .available(itemForTest.getAvailable())
                .requestId(itemForTest.getRequestId())
                .build();

        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .id(itemRequestForTest.getId())
                .description(itemRequestForTest.getDescription())
                .created(itemRequestForTest.getCreated())
                .items(List.of(itemDto))
                .build();

        List<ItemRequestDto> itemRequestsDtoExpected = List.of(itemRequestDto1);
        // When
        List<ItemRequestDto> itemRequestsDtoActual = itemRequestService.getAllRequests(2L, null, null);
        // Then
        Assertions.assertArrayEquals(itemRequestsDtoExpected.toArray(), itemRequestsDtoActual.toArray());
    }

    @Test
    void test8_getAllRequests() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRequestRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequestForTest)));
        Mockito
                .when(mockItemRequestRepository.findAll())
                .thenReturn(List.of(itemRequestForTest));
        Mockito
                .when(mockItemRepository.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(itemForTest));
        itemRequestService.setUserRepository(mockUserRepository);
        itemRequestService.setItemRequestRepository(mockItemRequestRepository);
        itemRequestService.setItemRepository(mockItemRepository);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name(itemForTest.getName())
                .description(itemForTest.getDescription())
                .available(itemForTest.getAvailable())
                .requestId(itemForTest.getRequestId())
                .build();

        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .id(itemRequestForTest.getId())
                .description(itemRequestForTest.getDescription())
                .created(itemRequestForTest.getCreated())
                .items(List.of(itemDto))
                .build();

        List<ItemRequestDto> itemRequestsDtoExpected = List.of(itemRequestDto1);
        // When
        List<ItemRequestDto> itemRequestsDtoActual = itemRequestService.getAllRequests(2L, 0, 1);
        // Then
        Assertions.assertArrayEquals(itemRequestsDtoExpected.toArray(), itemRequestsDtoActual.toArray());
    }

    @Test
    void test9_getAllRequests_whenFromIsNegative() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemRequestService.setUserRepository(mockUserRepository);

        // When
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemRequestService.getAllRequests(1L, -1, 2));
        // Then
        Assertions.assertEquals("Страница не может начинаться с -1", thrown.getMessage());
    }

    @Test
    void test10_getAllRequests_whenSizeIsZero() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        itemRequestService.setUserRepository(mockUserRepository);

        // When
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> itemRequestService.getAllRequests(1L, 0, 0));
        // Then
        Assertions.assertEquals("Страница не может быть равна размеру 0", thrown.getMessage());
    }

    @Test
    void test11_findRequestById() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequestForTest));
        Mockito
                .when(mockItemRepository.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(itemForTest));
        itemRequestService.setUserRepository(mockUserRepository);
        itemRequestService.setItemRequestRepository(mockItemRequestRepository);
        itemRequestService.setItemRepository(mockItemRepository);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name(itemForTest.getName())
                .description(itemForTest.getDescription())
                .available(itemForTest.getAvailable())
                .requestId(itemForTest.getRequestId())
                .build();

        ItemRequestDto itemRequestDtoExpected = ItemRequestDto.builder()
                .id(itemRequestForTest.getId())
                .description(itemRequestForTest.getDescription())
                .created(itemRequestForTest.getCreated())
                .items(List.of(itemDto))
                .build();

        // When
        ItemRequestDto itemRequestDtoActual = itemRequestService.findRequestById(1L, 1L);
        // Then
        Assertions.assertEquals(itemRequestDtoExpected, itemRequestDtoActual);
    }


    @Test
    void test12_findRequestById_whenItemRequestIsNotFound() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        itemRequestService.setUserRepository(mockUserRepository);
        itemRequestService.setItemRequestRepository(mockItemRequestRepository);

        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> itemRequestService.findRequestById(99L, 1L));
        // Then
        Assertions.assertEquals("Запрос с id = 99 не найден", thrown.getMessage());
    }
}
