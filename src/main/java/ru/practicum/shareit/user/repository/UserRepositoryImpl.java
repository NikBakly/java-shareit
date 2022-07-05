package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>(); // словарь:userId-user
    private Long nextId = 1L;

    @Override
    public User save(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validate(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Пользователь под id = {} успешно сохранен.", user.getId());
        return user;
    }

    @Override
    public User update(Long userId, UserDto userDto) {
        validateForUpdateUser(userId, userDto);
        User user = users.get(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        log.info("Пользователь под id = {} успешно обновлен.", user.getId());
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        checkUserId(userId);
        log.info("Пользователь под id = {} успешно найден.", userId);
        return users.get(userId);
    }

    @Override
    public void deleteUserById(Long userId) {
        checkUserId(userId);
        log.info("Пользователь под id = {} успешно удален.", userId);
        users.remove(userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Все пользователи успешно найдены");
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

    private void validateForUpdateUser(Long userId, UserDto userDto) {
        checkUserId(userId);
        if (userDto.getEmail() != null) {
            checkEmailUser(userDto.getEmail());
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
