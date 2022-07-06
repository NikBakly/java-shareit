package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class UserDAOImpl implements UserDAO {
    private final HashMap<Long, User> users = new HashMap<>(); // словарь:userId-user
    private Long nextId = 1L;

    @Override
    public User save(User user) {
        validate(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.debug("Пользователь под id = {} успешно сохранен.", user.getId());
        return user;
    }

    @Override
    public User update(Long userId, User UpdatedUser) {
        validateForUpdateUser(userId, UpdatedUser);
        User user = users.get(userId);
        if (UpdatedUser.getName() != null) {
            user.setName(UpdatedUser.getName());
        }
        if (UpdatedUser.getEmail() != null) {
            user.setEmail(UpdatedUser.getEmail());
        }
        log.debug("Пользователь под id = {} успешно обновлен.", userId);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        checkUserId(userId);
        log.debug("Пользователь под id = {} успешно найден.", userId);
        return users.get(userId);
    }

    @Override
    public void deleteUserById(Long userId) {
        checkUserId(userId);
        log.debug("Пользователь под id = {} успешно удален.", userId);
        users.remove(userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Все пользователи успешно найдены");
        return new ArrayList<>(users.values());
    }

    private void validate(@Valid User user) {
        if (user.getName().isBlank()) {
            log.warn("Имя у пользователя id = {} не может быть пустым", user.getId());
            throw new ValidationException("Имя у пользователя не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Email у пользователя id = {} не может быть пустым", user.getId());
            throw new BadRequestException("Email у пользователя не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Email у пользователя id = {} не имеет @", user.getId());
            throw new BadRequestException("Email у пользователя не имеет @");
        }
        checkEmailUser(user.getEmail());
    }

    private void validateForUpdateUser(Long userId, User user) {
        checkUserId(userId);
        if (user.getEmail() != null) {
            checkEmailUser(user.getEmail());
        }
    }

    private void checkEmailUser(String email) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            log.warn("Такой email занят другим пользователем");
            throw new ValidationException("Такой email занят другим пользователем");
        }
    }

    private void checkUserId(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}
