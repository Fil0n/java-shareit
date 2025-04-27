package ru.practicum.shareit.comment.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDto {
    @NotBlank(message = "Текст отзыва не может быть пустым")
    String text;
}
