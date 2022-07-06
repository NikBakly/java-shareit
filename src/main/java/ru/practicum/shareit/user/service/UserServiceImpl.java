package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDAO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Класс-сервис, который предназначен для реализации основной бизнес-логики.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    @Override
    public User save(User userDto) {
        return userDAO.save(userDto);
    }

    @Override
    public User update(Long userId, User updatedUser) {
        return userDAO.update(userId, updatedUser);
    }

    @Override
    public User findUserById(Long userId) {
        return userDAO.findUserById(userId);
    }

    @Override
    public void deleteUserById(Long userId) {
        userDAO.deleteUserById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
}
