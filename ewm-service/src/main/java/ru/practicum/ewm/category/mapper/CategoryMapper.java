package ru.practicum.ewm.category.mapper;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

public class CategoryMapper {
	private CategoryMapper() {
	}

	public static Category toEntity(NewCategoryDto dto) {
		return new Category(null, dto.getName());
	}

	public static CategoryDto toDto(Category category) {
		return new CategoryDto(category.getId(), category.getName());
	}
}
