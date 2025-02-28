package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;


@Controller
public class FavoriteController {
	private final FavoriteRepository favoriteRepository;
	private final FavoriteService favoriteService;
	
	public FavoriteController(FavoriteService favoriteService, FavoriteRepository favoriteRepository) {
		this.favoriteService = favoriteService;
		this.favoriteRepository = favoriteRepository;
	}
	
	@GetMapping("/favorite")
	public String index(@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
						@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						Model model) 
	{
		Page<Favorite> favoritePage = favoriteRepository.findByUser(userDetailsImpl.getUser(), pageable);
		
		model.addAttribute("favoritePage", favoritePage);
		
		return "paidMembership/favorite/index";
	}
	
	@PostMapping("/favorite/register")
	public String register(@RequestParam(name = "storeId", required = false) Integer storeId, 
						 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						 RedirectAttributes redirectAttributes)
	{
		favoriteService.create(userDetailsImpl.getUser().getId(), storeId);
        redirectAttributes.addFlashAttribute("successMessage", "お気に入りに登録しました。");

		return "redirect:/favorite";
	}
	
	@PostMapping("/favorite/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) 
	{
		favoriteRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");

		return "redirect:/favorite";
	}
}
