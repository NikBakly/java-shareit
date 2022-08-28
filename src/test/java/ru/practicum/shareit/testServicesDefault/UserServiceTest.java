package ru.practicum.shareit.testServicesDefault;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;
import java.util.Optional;

public class UserServiceTest {
    private final UserRepository mockUserRepository = Mockito.mock(UserRepository.class);

    private UserServiceImpl userService;
    private User userForTest;


    @BeforeEach
    void initUser() {
        userForTest = new User();
        userForTest.setId(1L);
        userForTest.setName("Petr");
        userForTest.setEmail("Petr@mail.com");
    }

    @BeforeEach
    void initUserService() {
        userService = new UserServiceImpl();
    }

    @Test
    void test1_saveUser() {
        // Given
        Mockito
                .when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(userForTest);
        userService.setUserRepository(mockUserRepository);
        // When
        User userActual = userService.save(userForTest);
        // Then
        Assertions.assertTrue(userActual.getId() == 1L
                && userActual.getName().equals("Petr")
                && userActual.getEmail().equals("Petr@mail.com"));
    }

    @Test
    void test2_saveUser_whenNameIsBlank() {
        // Given
        userForTest.setName(" ");
        // When
        ValidationException thrown = Assertions
                .assertThrows(ValidationException.class, () -> userService.save(userForTest));
        // Then
        Assertions.assertEquals("Имя у пользователя не может быть пустым", thrown.getMessage());
    }

    @Test
    void test3_saveUser_whenEmailIsNull() {
        // Given
        userForTest.setEmail(null);
        // When
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> userService.save(userForTest));
        // Then
        Assertions.assertEquals("Email у пользователя не может быть пустым", thrown.getMessage());
    }

    @Test
    void test4_saveUser_whenEmailIsBlank() {
        // Given
        userForTest.setEmail(" ");
        // When
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> userService.save(userForTest));
        // Then
        Assertions.assertEquals("Email у пользователя не может быть пустым", thrown.getMessage());
    }

    @Test
    void test5_saveUser_whenEmailWithoutAt() {
        // Given
        userForTest.setEmail("Petr.mail.com");
        // When
        BadRequestException thrown = Assertions
                .assertThrows(BadRequestException.class, () -> userService.save(userForTest));
        // Then
        Assertions.assertEquals("Email у пользователя не имеет @", thrown.getMessage());
    }

    @Test
    void test6_updateUser_whenUpdateEmail() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        User updatedUser = new User();
        updatedUser.setEmail("Petr@gmail.com");
        Mockito
                .when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(updatedUser);
        userService.setUserRepository(mockUserRepository);
        // When
        User updatedUserActual = userService.update(1L, updatedUser);
        // Then
        Assertions.assertEquals("Petr@gmail.com", updatedUserActual.getEmail());
    }

    @Test
    void test7_updateUser_whenUpdateName() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        User updatedUser = new User();
        updatedUser.setName("Pavel");
        Mockito
                .when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(updatedUser);
        userService.setUserRepository(mockUserRepository);
        // When
        User updatedUserActual = userService.update(1L, updatedUser);
        // Then
        Assertions.assertEquals("Pavel", updatedUserActual.getName());
    }

    @Test
    void test8_updateUser_whenEmailIsEmployed() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        User updatedUser = new User();
        updatedUser.setEmail("Pasha@mail.com");
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Pasha");
        otherUser.setEmail("Pasha@mail.com");
        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(List.of(userForTest, otherUser));
        userService.setUserRepository(mockUserRepository);
        // When
        ValidationException thrown = Assertions
                .assertThrows(ValidationException.class, () -> userService.update(1L, updatedUser));
        // Then
        Assertions.assertEquals("Такой email занят другим пользователем", thrown.getMessage());
    }

    @Test
    void test9_findUserById() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userForTest));
        userService.setUserRepository(mockUserRepository);
        // When
        User userActual = userService.findUserById(1L);

        // Then
        Assertions.assertEquals(userActual, userForTest);
    }

    @Test
    void test10_findUserById_whenUserIdIsWrong() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        userService.setUserRepository(mockUserRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> userService.findUserById(99L));
        // Then
        Assertions.assertEquals("Пользователь с id = 99 не найден", thrown.getMessage());
    }

    @Test
    void test11_deleteUserById_whenUserIdIsWrong() {
        // Given
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        userService.setUserRepository(mockUserRepository);
        // When
        NotFoundException thrown = Assertions
                .assertThrows(NotFoundException.class, () -> userService.deleteUserById(99L));
        // Then
        Assertions.assertEquals("Пользователь с id = 99 не найден", thrown.getMessage());
    }
}
