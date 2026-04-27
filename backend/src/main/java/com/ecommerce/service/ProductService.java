package com.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductRequest;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	public List<ProductDto> findAll(Long categoryId) {
		List<Product> list = categoryId != null
				? productRepository.findByCategoryId(categoryId)
				: productRepository.findAll();
		return list.stream().map(this::toDto).toList();
	}

	public ProductDto findById(Long id) {
		return productRepository.findById(id).map(this::toDto)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
	}

	@Transactional
	public ProductDto create(ProductRequest req) {
		Category category = categoryRepository.findById(req.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));
		Product p = new Product();
		apply(req, p, category);
		p = productRepository.save(p);
		return toDto(productRepository.findById(p.getId()).orElse(p));
	}

	@Transactional
	public ProductDto update(Long id, ProductRequest req) {
		Product p = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
		Category category = categoryRepository.findById(req.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));
		apply(req, p, category);
		p = productRepository.save(p);
		return toDto(productRepository.findById(p.getId()).orElse(p));
	}

	@Transactional
	public void delete(Long id) {
		if (!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Product not found: " + id);
		}
		productRepository.deleteById(id);
	}

	private void apply(ProductRequest req, Product p, Category category) {
		p.setName(req.getName().trim());
		p.setDescription(req.getDescription());
		p.setPrice(req.getPrice());
		p.setImageUrl(req.getImageUrl());
		p.setCategory(category);
	}

	private ProductDto toDto(Product p) {
		CategoryDto cat = new CategoryDto(p.getCategory().getId(), p.getCategory().getName());
		return new ProductDto(
				p.getId(),
				p.getName(),
				p.getDescription(),
				p.getPrice(),
				p.getImageUrl(),
				cat);
	}
}
