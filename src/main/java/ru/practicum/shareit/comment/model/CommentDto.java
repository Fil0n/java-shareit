package ru.practicum.shareit.comment.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    Long id;
    @NotBlank(message = "Текст отзыва не может быть пустым")
    String text;

    Long itemId;

    String authorName;

    LocalDateTime created;
}
