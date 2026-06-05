package ru.practicum.ewm.user.mapper;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

public class UserMapper {
	private UserMapper() {
	}

	public static User toEntity(NewUserRequest request) {
		return new User(null, request.getEmail(), request.getName());
	}

	public static UserDto toDto(User user) {
		return new UserDto(user.getId(), user.getEmail(), user.getName());
	}

	public static UserShortDto toShortDto(User user) {
		return new UserShortDto(user.getId(), user.getName());
	}
}
