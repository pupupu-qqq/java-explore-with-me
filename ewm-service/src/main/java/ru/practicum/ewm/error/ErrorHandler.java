package ru.practicum.ewm.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleNotFound(NotFoundException exception) {
		return buildError(HttpStatus.NOT_FOUND, "The required object was not found.", exception.getMessage(), List.of());
	}

	@ExceptionHandler({
			ConflictException.class
	})
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiError handleConflict(Exception exception) {
		return buildError(HttpStatus.CONFLICT, "For the requested operation the conditions are not met.",
				exception.getMessage(), List.of());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiError handleDataIntegrity(DataIntegrityViolationException exception) {
		return buildError(HttpStatus.CONFLICT, "Integrity constraint has been violated.",
				exception.getMessage(), List.of());
	}

	@ExceptionHandler({
			ValidationException.class,
			ConstraintViolationException.class,
			HttpMessageNotReadableException.class,
			MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class,
			IllegalArgumentException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleBadRequest(Exception exception) {
		return buildError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", exception.getMessage(), List.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleValidation(MethodArgumentNotValidException exception) {
		List<String> errors = new ArrayList<>();
		for (FieldError error : exception.getBindingResult().getFieldErrors()) {
			errors.add("Field: " + error.getField() + ". Error: " + error.getDefaultMessage()
					+ ". Value: " + error.getRejectedValue());
		}
		return buildError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", String.join("; ", errors), errors);
	}

	private ApiError buildError(HttpStatus status, String reason, String message, List<String> errors) {
		return new ApiError(errors, message, reason, status.name(), LocalDateTime.now());
	}
}
