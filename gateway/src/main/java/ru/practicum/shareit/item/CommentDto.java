package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    @FutureOrPresent
    private LocalDateTime created;
}
