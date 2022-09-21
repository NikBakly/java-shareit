package ru.practicum.shareit.user;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Класс-сервис, который предназначен для реализации основной бизнес-логики.
 */
@Service
@Setter(onMethod_ = @Autowired)
@Slf4j
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Transactional
    @Override
    public ResponseEntity<User> save(User user) {
        log.debug("Пользователь под id = {} успешно сохранен.", user.getId());
        return ResponseEntity.ok(userRepository.save(user));
    }

    @Transactional
    @Override
    public ResponseEntity<User> update(Long userId, User updatedUser) {
        ResponseEntity<User> resultValidation = validateForUpdateUser(userId, updatedUser);
        if (resultValidation != null) {
            return resultValidation;
        }
        User user = userRepository.findById(userId).get();
        if (updatedUser.getName() != null)
            user.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null)
            user.setEmail(updatedUser.getEmail());
        log.debug("Пользователь под id = {} успешно обновлен.", userId);
        return ResponseEntity.ok(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<User> findUserById(Long userId) {
        if (checkUserId(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.debug("Пользователь под id = {} успешно найден.", userId);
        return ResponseEntity.ok(userRepository.findById(userId).get());
    }

    @Transactional
    @Override
    public ResponseEntity<Void> deleteUserById(Long userId) {
        if (!checkUserId(userId)) {
            log.debug("Пользователь под id = {} успешно удален.", userId);
            userRepository.deleteById(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Метод для проверок при обновлении пользователя
    private ResponseEntity<User> validateForUpdateUser(Long userId, User user) {
        Boolean resultCheckUserId = checkUserId(userId);
        if (resultCheckUserId) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (user.getEmail() != null) {
            Boolean resultCheckEmailUser = checkEmailUser(user.getEmail());
            if (resultCheckEmailUser) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
        return null;
    }

    // Метод для проверки email пользователя
    private Boolean checkEmailUser(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Такой email занят другим пользователем");
            return true;
        }
        return false;
    }

    // Метод проверка существования пользователя
    private Boolean checkUserId(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Пользователь с id = {} не найден", userId);
            return true;
        }
        return false;
    }
}
