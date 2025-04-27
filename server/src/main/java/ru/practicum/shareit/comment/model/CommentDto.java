package ru.practicum.shareit.comment.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    Long id;
    String text;
    Long itemId;
    String authorName;
    LocalDateTime created;
}
