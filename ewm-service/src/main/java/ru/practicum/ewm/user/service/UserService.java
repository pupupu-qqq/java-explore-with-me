package ru.practicum.ewm.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.OffsetPageRequest;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public UserDto create(NewUserRequest request) {
		return UserMapper.toDto(userRepository.save(UserMapper.toEntity(request)));
	}

	@Transactional(readOnly = true)
	public List<UserDto> getUsers(List<Long> ids, int from, int size) {
		OffsetPageRequest pageRequest = new OffsetPageRequest(from, size);
		List<User> users;
		if (ids == null || ids.isEmpty()) {
			users = userRepository.findAll(pageRequest).getContent();
		} else {
			users = userRepository.findAllByIdIn(ids, pageRequest);
		}
		return users.stream()
				.map(UserMapper::toDto)
				.toList();
	}

	@Transactional
	public void delete(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("User with id=" + userId + " was not found");
		}
		userRepository.deleteById(userId);
	}

	@Transactional(readOnly = true)
	public User getById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
	}
}
