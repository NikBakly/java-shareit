package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final CommentRepository commentRepository;

    public static CommentDto toCommentDto(Comment comment, String authorName) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(authorName);
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static List<CommentDto> toCommentsDto(List<Comment> comment, String authorName) {
        List<CommentDto> commentsDto = new ArrayList<>();
        comment.forEach(comment1 -> commentsDto.add(toCommentDto(comment1, authorName)));
        return commentsDto;
    }
}
