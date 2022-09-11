package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс, с которым будет работать пользователь
 */
@Data
@Builder
@ToString
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    @FutureOrPresent
    private LocalDateTime created;
    private List<ItemDto> items;
}
