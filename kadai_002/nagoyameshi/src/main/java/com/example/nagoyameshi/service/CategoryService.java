package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	// カテゴリ情報を登録
	@Transactional
	public void create(CategoryRegisterForm categoryRegisterForm) {
		Category category = new Category();
		
		category.setName(categoryRegisterForm.getName());
		
		categoryRepository.save(category);
	}
	
	// カテゴリ情報を更新
	@Transactional
	public void update(CategoryEditForm categoryEditForm) {
		Integer categoryId = categoryEditForm.getId();
		Category category = categoryRepository.getReferenceById(categoryId);
		
		category.setName(categoryEditForm.getName());
		categoryRepository.save(category);
	}
}
