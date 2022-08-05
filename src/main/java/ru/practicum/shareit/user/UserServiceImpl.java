package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;

/**
 * Класс-сервис, который предназначен для реализации основной бизнес-логики.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public User save(User user) {
        validate(user);
        log.debug("Пользователь под id = {} успешно сохранен.", user.getId());
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User update(Long userId, User updatedUser) {
        validateForUpdateUser(userId, updatedUser);
        User user = userRepository.findById(userId).get();
        if (updatedUser.getName() != null)
            user.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null)
            user.setEmail(updatedUser.getEmail());
        log.debug("Пользователь под id = {} успешно обновлен.", userId);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User findUserById(Long userId) {
        checkUserId(userId);
        log.debug("Пользователь под id = {} успешно найден.", userId);
        return userRepository.findById(userId).get();
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        checkUserId(userId);
        log.debug("Пользователь под id = {} успешно удален.", userId);
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Метод для проверок при создании пользователя
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
    }

    // Метод для проверок при обновлении пользователя
    private void validateForUpdateUser(Long userId, User user) {
        checkUserId(userId);
        if (user.getEmail() != null) {
            checkEmailUser(user.getEmail());
        }
    }

    // Метод для проверки email пользователя
    private void checkEmailUser(String email) {
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(email))) {
            log.warn("Такой email занят другим пользователем");
            throw new ValidationException("Такой email занят другим пользователем");
        }
    }

    // Метод проверка существования пользователя
    private void checkUserId(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}
