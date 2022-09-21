package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<User> save(User user);

    ResponseEntity<User> update(Long userId, User updatedUser);

    ResponseEntity<User> findUserById(Long userId);

    ResponseEntity<Void> deleteUserById(Long userId);

    ResponseEntity<List<User>> getAllUsers();
}
