package com.ecommerce.config;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;

/**
 * Seeds a default admin account and demo catalog when the database is empty.
 * Adjust credentials via {@code app.seed.*} in application.properties.
 */
@Component
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.seed.admin-email}")
	private String adminEmail;

	@Value("${app.seed.admin-password}")
	private String adminPassword;

	public DataInitializer(
			UserRepository userRepository,
			CategoryRepository categoryRepository,
			ProductRepository productRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		if (!userRepository.existsByEmail(adminEmail)) {
			User admin = new User();
			admin.setName("Administrator");
			admin.setEmail(adminEmail);
			admin.setPassword(passwordEncoder.encode(adminPassword));
			admin.setRole(Role.ADMIN);
			userRepository.save(admin);
		}

		if (categoryRepository.count() == 0) {
			Category electronics = new Category();
			electronics.setName("Electronics");
			electronics = categoryRepository.save(electronics);

			Category books = new Category();
			books.setName("Books");
			books = categoryRepository.save(books);

			createProduct("Wireless Headphones", "Noise-cancelling over-ear headphones.", new BigDecimal("129.99"),
					"https://picsum.photos/seed/headphones/400/300", electronics);
			createProduct("USB-C Hub", "7-in-1 hub with HDMI and card reader.", new BigDecimal("45.50"),
					"https://picsum.photos/seed/hub/400/300", electronics);
			createProduct("Clean Code", "A handbook of agile software craftsmanship.", new BigDecimal("42.00"),
					"https://picsum.photos/seed/book1/400/300", books);
		}
	}

	private void createProduct(String name, String description, BigDecimal price, String imageUrl, Category category) {
		Product p = new Product();
		p.setName(name);
		p.setDescription(description);
		p.setPrice(price);
		p.setImageUrl(imageUrl);
		p.setCategory(category);
		productRepository.save(p);
	}
}
