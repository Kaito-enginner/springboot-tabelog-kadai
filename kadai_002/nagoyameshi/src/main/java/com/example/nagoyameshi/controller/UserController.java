package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.UserService;



@Controller
@RequestMapping("/user")
public class UserController {
	private final UserRepository userRepository;
	private final UserService userService;
	
	public UserController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}
	
	
	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		
		model.addAttribute("user", user);
		
		return "freeMembership/user/index";
	}
	
	
	@GetMapping("/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getFurigana(), user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail());
		
		model.addAttribute("userEditForm", userEditForm);
		return "freeMembership/user/edit";
	}
	
	
	@PostMapping("/update")
	public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if(!userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);                       
		}
		
		if(bindingResult.hasErrors()) {
			return "freeMembership/user/edit";
		}
		
		userService.update(userEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
		
		return "redirect:/user";
	}
	
	
	@GetMapping("/withdrawal")
	public String withdrawal(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		
		model.addAttribute("userId", userDetailsImpl.getUser().getId());
		
		return "freeMembership/user/withdrawal";
	}
	
	
	@PostMapping("/{id}/disable") 
	public String disable(@PathVariable(name = "id") Integer id, HttpSession httpsession, RedirectAttributes redirectAttributes) {
		userService.disable(id);
		
		httpsession.invalidate();
		
		redirectAttributes.addFlashAttribute("successMessage", "会員情報を削除しました。");
		
		return "redirect:/";
	}
}
