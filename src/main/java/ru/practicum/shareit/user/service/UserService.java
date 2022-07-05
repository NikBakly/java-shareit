package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User save(UserDto userDto);

    User update(Long userId, UserDto userDto);

    User findUserById(Long userId);

    void deleteUserById(Long userId);

    List<User> getAllUsers();
}
