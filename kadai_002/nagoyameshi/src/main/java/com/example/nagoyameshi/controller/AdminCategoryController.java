package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.service.CategoryService;


@Controller
public class AdminCategoryController {
	private final CategoryRepository categoryRepository;
	private final CategoryService categoryService;
	
	public AdminCategoryController(CategoryRepository categoryRepository, CategoryService categoryService) {
		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;
	}
	
	@GetMapping("/admin/category")
	public String index(@PageableDefault(page = 0, size = 20, sort = "id", direction = Direction.ASC) Pageable pageable,
						Model model, @RequestParam(name = "keyword", required = false) String keyword) 
	{
		CategoryRegisterForm categoryRegisterForm = new CategoryRegisterForm();
		Page<Category> categoryPage;
		
		if(keyword != null && !keyword.isEmpty()) {
			categoryPage = categoryRepository.findByNameLike("%" + keyword + "%", pageable);
		}else {
			categoryPage = categoryRepository.findAll(pageable);
		}
		
		model.addAttribute("categoryPage", categoryPage);
		model.addAttribute("categoryRegisterForm", categoryRegisterForm);
		model.addAttribute("keyword", keyword);
		
		return "admin/category/index";
	}
	
	@PostMapping("/admin/category/create")
	public String create(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "admin/category/index";
		}
		
		categoryService.create(categoryRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました。");
		
		return "redirect:/admin/category";
	}
	
	@GetMapping("/admin/category/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id,  Model model) {
		Category category = categoryRepository.getReferenceById(id);
		
		CategoryEditForm categoryEditForm = new CategoryEditForm(category.getId(), category.getName());
		
		model.addAttribute("categoryEditForm", categoryEditForm);
		
		return "admin/category/edit";
	}
	
	@PostMapping("/admin/category/update")
	public String update(@ModelAttribute @Validated CategoryEditForm categoryEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		
		if(bindingResult.hasErrors()) {
			return "admin/category/edit";
		}
		
		categoryService.update(categoryEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "カテゴリを編集しました。");
		
		return "redirect:/admin/category";
	}
	
	@PostMapping("/admin/category/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		categoryRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");
		
		return "redirect:/admin/category";
	}
}











