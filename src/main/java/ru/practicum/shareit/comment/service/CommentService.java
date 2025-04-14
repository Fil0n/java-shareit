package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.model.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentDto createComment(Item item, CommentDto commentDto, User user) {
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        return CommentMapper.toCommentDto(commentRepository.saveAndFlush(comment));
    }

    public List<CommentDto> getItemComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }
}
