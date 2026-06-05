package ru.practicum.ewm.comment.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentRequest;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.common.OffsetPageRequest;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
	private static final Sort COMMENTS_SORT = Sort.by(Sort.Direction.DESC, "createdOn");

	private final CommentRepository commentRepository;
	private final UserService userService;
	private final EventService eventService;

	public CommentService(CommentRepository commentRepository, UserService userService, EventService eventService) {
		this.commentRepository = commentRepository;
		this.userService = userService;
		this.eventService = eventService;
	}

	@Transactional
	public CommentDto create(Long userId, Long eventId, NewCommentDto dto) {
		User user = userService.getById(userId);
		Event event = eventService.getEventEntity(eventId);
		if (event.getState() != EventState.PUBLISHED) {
			throw new ConflictException("Only published events can be commented");
		}
		Comment comment = new Comment();
		comment.setText(dto.getText());
		comment.setAuthor(user);
		comment.setEvent(event);
		comment.setCreatedOn(LocalDateTime.now());
		return CommentMapper.toDto(commentRepository.save(comment));
	}

	@Transactional(readOnly = true)
	public List<CommentDto> getEventComments(Long eventId, int from, int size) {
		Event event = eventService.getEventEntity(eventId);
		if (event.getState() != EventState.PUBLISHED) {
			throw new NotFoundException("Event with id=" + eventId + " was not found");
		}
		return commentRepository.findAllByEvent(event, new OffsetPageRequest(from, size, COMMENTS_SORT))
				.stream()
				.map(CommentMapper::toDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<CommentDto> getUserComments(Long userId, int from, int size) {
		User user = userService.getById(userId);
		return commentRepository.findAllByAuthor(user, new OffsetPageRequest(from, size, COMMENTS_SORT))
				.stream()
				.map(CommentMapper::toDto)
				.toList();
	}

	@Transactional
	public CommentDto update(Long userId, Long commentId, UpdateCommentRequest request) {
		Comment comment = getUserComment(userId, commentId);
		comment.setText(request.getText());
		comment.setUpdatedOn(LocalDateTime.now());
		return CommentMapper.toDto(commentRepository.save(comment));
	}

	@Transactional
	public void deleteByUser(Long userId, Long commentId) {
		commentRepository.delete(getUserComment(userId, commentId));
	}

	@Transactional
	public void deleteByAdmin(Long commentId) {
		if (!commentRepository.existsById(commentId)) {
			throw new NotFoundException("Comment with id=" + commentId + " was not found");
		}
		commentRepository.deleteById(commentId);
	}

	private Comment getUserComment(Long userId, Long commentId) {
		User user = userService.getById(userId);
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
		if (!comment.getAuthor().getId().equals(user.getId())) {
			throw new NotFoundException("Comment with id=" + commentId + " was not found");
		}
		return comment;
	}
}
