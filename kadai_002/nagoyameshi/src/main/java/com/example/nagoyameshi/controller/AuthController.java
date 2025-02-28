package com.example.nagoyameshi.controller;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.event.SignupEventPublisher;
import com.example.nagoyameshi.form.PasswordResetForm;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.service.PasswordResetTokenService;
import com.example.nagoyameshi.service.UserService;
import com.example.nagoyameshi.service.VerificationTokenService;

@Controller
public class AuthController {
	private final UserService userService;
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;
	private final UserRepository userRepository;
	private final PasswordResetTokenService passwordResetTokenService;
	
	public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService, UserRepository userRepository, PasswordResetTokenService passwordResetTokenService) {
		this.userService = userService;
		this.signupEventPublisher = signupEventPublisher;
		this.verificationTokenService = verificationTokenService;
		this.userRepository = userRepository;
		this.passwordResetTokenService = passwordResetTokenService;
	}
	
	@GetMapping("/login")
    public String login() {
        return "auth/login";
    }
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("signupForm", new SignupForm());
		
		return "auth/signup";
	}
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute @Validated SignupForm signupForm,  BindingResult bindingResult,
						 RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) 
	{
		if(userService.isEmailRegistered(signupForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}
		
		if(!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
			bindingResult.addError(fieldError);
		}
		
		if(bindingResult.hasErrors()) {
			return "auth/signup";
		}
		
		User user = userService.create(signupForm);
		String requestUrl = new String(httpServletRequest.getRequestURL());
		signupEventPublisher.publishSignupEvent(user, requestUrl);
		redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
		
		return "redirect:/";
	}
	
	@GetMapping("/signup/verify")
	public String verify(@RequestParam(name = "token") String token, Model model) {
		VerificationToken verificationToken =verificationTokenService.getVerificationToken(token);
		
		if(verificationToken != null) {
			User user = verificationToken.getUser();
			userService.enableUser(user);
			String successMessage  = "会員登録が完了しました。";
			model.addAttribute("successMessage", successMessage);
		}else {
			String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
		}
		
		return "auth/verify";
	}
	
	@GetMapping("/forget")
	public String forget() {
		return "auth/request";
	}
	
	@PostMapping("/request")
	public String request(@RequestParam(name = "email") String email, Model model,
						RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) 
	{
		User user = userRepository.findByEmail(email);
		String requestUrl = new String(httpServletRequest.getRequestURL());
		
		if(user != null) {
			String token = UUID.randomUUID().toString();
			passwordResetTokenService.create(user, token);
			passwordResetTokenService.sendEmail(email, token, requestUrl);
		} else {
			String errorMessage =  "ご入力いただいたメールアドレスは登録されておりません。";
			model.addAttribute("errorMessage", errorMessage);
			return "auth/request";
		}
		
		redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに再設定用メールを送信しました。メールに記載されているリンクをクリックし、パスワードを再設定してください。");
		
		return "redirect:/";
	}
	
	@GetMapping("/request/verify")
	public String change(@RequestParam(name = "token") String token, Model model) {
		PasswordResetToken passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);
		
		if(passwordResetToken != null) {
			User user = passwordResetToken.getUser();
			PasswordResetForm passwordResetForm = new PasswordResetForm(user.getId(), null, null);
			model.addAttribute("passwordResetForm", passwordResetForm);
			
			return "auth/reset";
		} else {
			String errorMessage = "トークンが無効です。";
			model.addAttribute("errorMessage", errorMessage);
			
			return "/request";
		}

	}
	
	@PostMapping("/request/verify/reset")
	public String reset(@ModelAttribute @Validated PasswordResetForm passwordResetForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		
		if(!passwordResetTokenService.isSamePassword(passwordResetForm.getPassword(), passwordResetForm.getPasswordConfirmation())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
		}
		
		if(bindingResult.hasErrors()) {
			
			return "auth/reset";
		}
		
		passwordResetTokenService.reset(passwordResetForm);
		redirectAttributes.addFlashAttribute("successMessage", "パスワードを変更しました。");
		
		return "redirect:/login";
	}
	
}













