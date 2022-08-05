package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User save(User userDto);

    User update(Long userId, User updatedUser);

    User findUserById(Long userId);

    void deleteUserById(Long userId);

    List<User> getAllUsers();
}
