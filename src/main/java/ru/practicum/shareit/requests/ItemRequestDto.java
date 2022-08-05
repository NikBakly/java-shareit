package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * Класс, с которым будет работать пользователь
 */
@Data
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
    private static User requestor;
    private LocalDate created;
}
