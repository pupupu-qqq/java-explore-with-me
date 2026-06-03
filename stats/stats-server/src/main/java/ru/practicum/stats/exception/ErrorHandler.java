package ru.practicum.stats.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
	@ExceptionHandler({
			IncorrectDateException.class,
			DateTimeParseException.class,
			HttpMessageNotReadableException.class,
			MethodArgumentNotValidException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleBadRequest(Exception exception) {
		String message = exception.getMessage();
		if (message == null) {
			message = "Incorrect request";
		}
		return Map.of("error", message);
	}
}
