package com.example.nagoyameshi.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.StoreRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReviewService;

@Controller
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final StoreRepository storeRepository;
	private final ReviewService reviewService;
	
	public ReviewController(ReviewRepository reviewRepository, StoreRepository storeRepository, ReviewService reviewService) {
		this.reviewRepository = reviewRepository;
		this.storeRepository = storeRepository;
		this.reviewService = reviewService;
	}
	
	@GetMapping("/review/{id}")
	public String index(@PathVariable(name = "id") Integer id,
						@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
						@PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
						Model model) {
		Store store = storeRepository.getReferenceById(id);
		
		Page<Review> reviewPage = reviewRepository.findByStore(store, pageable);
		
		// 同じユーザーと店舗でレビューが投稿されていないかをチェックする
		Review review = reviewRepository.findByStoreAndUser(store, userDetailsImpl.getUser());
		
		if(userDetailsImpl != null) {
			model.addAttribute("user", userDetailsImpl.getUser());
		}
		
		model.addAttribute("reviewbool", review);
		model.addAttribute("store", store);
		model.addAttribute("reviewPage", reviewPage);
		
		return "freeMembership/review/index";
	}
	
	
	@GetMapping("/premium/review/{id}/register")
	public String register(@PathVariable(name = "id") Integer id,
						   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
						   Model model) 
	{
		model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
		model.addAttribute("storeId", id);
		model.addAttribute("userId", userDetailsImpl.getUser().getId());
		
		return "paidMembership/review/register";
	}
	
	@PostMapping("/premium/review/create")
	public String create(@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm, 
						 BindingResult bindingResult, RedirectAttributes redirectAttributes, 
						 UriComponentsBuilder builder) {
		
		if(bindingResult.hasErrors()) {
			return "paidMembership/review/register";
		}
		
		reviewService.create(reviewRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
		
		//リダイレクト先を指定
		URI location = builder.path("/review/" + reviewRegisterForm.getStoreId()).build().toUri();
		
		return "redirect:" + location.toString();
	}
	

	@GetMapping("/premium/review/{storeid}/{reviewid}/edit")
	public String edit(@PathVariable(name = "storeid") Integer storeId,
					   @PathVariable(name = "reviewid") Integer reviewId,
					   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
					   Model model) 
	{
		Review review = reviewRepository.getReferenceById(reviewId);
		
		ReviewEditForm reviewEditForm = new ReviewEditForm(reviewId, storeId, userDetailsImpl.getUser().getId(), review.getEvaluation(), review.getComment());
		
		model.addAttribute("reviewEditForm", reviewEditForm);
		
		return "paidMembership/review/edit";
	}
	
	
	@PostMapping("/premium/review/update")
	public String update(@ModelAttribute @Validated ReviewEditForm reviewEditForm, 
						 BindingResult bindingResult, RedirectAttributes redirectAttributes, 
						 UriComponentsBuilder builder) 
	{
		
		if(bindingResult.hasErrors()) {
			return "paidMembership/review/edit";
		}
		
		reviewService.update(reviewEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		
		//リダイレクト先を指定
		URI location = builder.path("/review/" + reviewEditForm.getStoreId()).build().toUri();
		
		return "redirect:" + location.toString();
	}
	
	
	@PostMapping("/premium/review/{storeid}/{reviewid}/delete")
	public String delete(@PathVariable(name = "storeid") Integer storeId,
						 @PathVariable(name = "reviewid") Integer reviewId, 
						 RedirectAttributes redirectAttributes, UriComponentsBuilder builder) 
	{
		reviewRepository.deleteById(reviewId);
		
		redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
		
		//リダイレクト先を指定
		URI location = builder.path("/review/" + storeId).build().toUri();
				
		return "redirect:" + location.toString();
	}

}



















