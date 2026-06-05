package ru.practicum.ewm.comment.mapper;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.user.mapper.UserMapper;

public class CommentMapper {
	private CommentMapper() {
	}

	public static CommentDto toDto(Comment comment) {
		CommentDto dto = new CommentDto();
		dto.setId(comment.getId());
		dto.setText(comment.getText());
		dto.setAuthor(UserMapper.toShortDto(comment.getAuthor()));
		dto.setEvent(comment.getEvent().getId());
		dto.setCreatedOn(comment.getCreatedOn());
		dto.setUpdatedOn(comment.getUpdatedOn());
		return dto;
	}
}
