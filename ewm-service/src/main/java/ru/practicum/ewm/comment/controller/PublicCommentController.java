package ru.practicum.ewm.comment.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {
	private final CommentService commentService;

	public PublicCommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@GetMapping
	public List<CommentDto> getEventComments(@PathVariable Long eventId,
											 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
											 @RequestParam(defaultValue = "10") @Min(1) int size) {
		return commentService.getEventComments(eventId, from, size);
	}
}
