package ru.practicum.ewm.category.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.OffsetPageRequest;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final EventRepository eventRepository;

	public CategoryService(CategoryRepository categoryRepository, EventRepository eventRepository) {
		this.categoryRepository = categoryRepository;
		this.eventRepository = eventRepository;
	}

	@Transactional
	public CategoryDto create(NewCategoryDto dto) {
		return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.toEntity(dto)));
	}

	@Transactional
	public CategoryDto update(Long catId, CategoryDto dto) {
		Category category = getById(catId);
		category.setName(dto.getName());
		return CategoryMapper.toDto(categoryRepository.save(category));
	}

	@Transactional
	public void delete(Long catId) {
		if (!categoryRepository.existsById(catId)) {
			throw new NotFoundException("Category with id=" + catId + " was not found");
		}
		if (eventRepository.existsByCategoryId(catId)) {
			throw new ConflictException("The category is not empty");
		}
		categoryRepository.deleteById(catId);
	}

	@Transactional(readOnly = true)
	public List<CategoryDto> getCategories(int from, int size) {
		return categoryRepository.findAll(new OffsetPageRequest(from, size)).getContent().stream()
				.map(CategoryMapper::toDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public CategoryDto getCategoryDto(Long catId) {
		return CategoryMapper.toDto(getById(catId));
	}

	@Transactional(readOnly = true)
	public Category getById(Long catId) {
		return categoryRepository.findById(catId)
				.orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
	}
}
