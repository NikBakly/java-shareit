package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

/**
 * Класс-контроллер, который предназначен для обработки запросов и возвращение результата
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserRepository userRepository;

    @PostMapping
    public User createNewUser(@RequestBody UserDto user) {
        return userRepository.save(user);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable("id") Long userId, @RequestBody UserDto updatedUserDto) {
        return userRepository.update(userId, updatedUserDto);
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") Long userId) {
        return userRepository.findUserById(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") Long userId) {
        userRepository.deleteUserById(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

}
