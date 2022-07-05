package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Класс, с которым будет работать пользователь
 */
@Data
@Builder
public class UserDto {
    private String name;
    private String email;
}