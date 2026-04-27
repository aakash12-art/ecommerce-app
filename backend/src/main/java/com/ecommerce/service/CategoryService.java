package com.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.dto.CategoryRequest;
import com.ecommerce.entity.Category;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<CategoryDto> findAll() {
		return categoryRepository.findAll().stream().map(this::toDto).toList();
	}

	public CategoryDto findById(Long id) {
		return categoryRepository.findById(id).map(this::toDto)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
	}

	@Transactional
	public CategoryDto create(CategoryRequest req) {
		if (categoryRepository.existsByNameIgnoreCase(req.getName().trim())) {
			throw new BadRequestException("Category name already exists");
		}
		Category c = new Category();
		c.setName(req.getName().trim());
		c = categoryRepository.save(c);
		return toDto(c);
	}

	@Transactional
	public CategoryDto update(Long id, CategoryRequest req) {
		Category c = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
		String name = req.getName().trim();
		if (!c.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
			throw new BadRequestException("Category name already exists");
		}
		c.setName(name);
		return toDto(categoryRepository.save(c));
	}

	@Transactional
	public void delete(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new ResourceNotFoundException("Category not found: " + id);
		}
		categoryRepository.deleteById(id);
	}

	private CategoryDto toDto(Category c) {
		return new CategoryDto(c.getId(), c.getName());
	}
}
