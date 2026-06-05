package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

public class CompilationMapper {
	private CompilationMapper() {
	}

	public static CompilationDto toDto(Compilation compilation, List<EventShortDto> events) {
		CompilationDto dto = new CompilationDto();
		dto.setEvents(events);
		dto.setId(compilation.getId());
		dto.setPinned(compilation.getPinned());
		dto.setTitle(compilation.getTitle());
		return dto;
	}
}
