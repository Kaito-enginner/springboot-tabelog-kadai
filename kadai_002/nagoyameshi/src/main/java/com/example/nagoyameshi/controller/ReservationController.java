package com.example.nagoyameshi.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.StoreCategories;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.StoreCategoriesRepository;
import com.example.nagoyameshi.repository.StoreRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReservationService;
import com.example.nagoyameshi.service.StoreService;

@Controller
public class ReservationController {
	private final ReservationRepository reservationRepository;
	private final StoreCategoriesRepository storeCategoriesRepository;
	private final UserRepository userRepository;
	private final StoreRepository storeRepository;
	private final ReservationService reservationService;
	private final FavoriteRepository favoriteRepository;
	private final ReviewRepository reviewRepository;
	private final StoreService storeService;

	public ReservationController(ReservationRepository reservationRepository, UserRepository userRepository, StoreRepository storeRepository, 
								 ReservationService reservationService, FavoriteRepository favoriteRepository, ReviewRepository reviewRepository,
								 StoreCategoriesRepository storeCategoriesRepository, StoreService storeService) 
	{
		this.reservationRepository = reservationRepository;
		this.userRepository = userRepository;
		this.storeRepository = storeRepository;
		this.reservationService = reservationService;
		this.favoriteRepository = favoriteRepository;
		this.reviewRepository = reviewRepository;
		this.storeService = storeService;
		this.storeCategoriesRepository = storeCategoriesRepository;
	}
	
	@GetMapping("/premium/reservation")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model,
						@PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
		Page<Reservation> reservationPage = reservationRepository.findByUser(userDetailsImpl.getUser(), pageable);
		
		model.addAttribute("reservationPage", reservationPage);
		
		return "paidMembership/reservation/index";
	}
	
	
	@PostMapping("/premium/reservation/register")
	public String register(@ModelAttribute @Validated ReservationRegisterForm reservationRegisterForm, 
						   BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		if(reservationRegisterForm.getReservationDate() != null && reservationRegisterForm.getReservationTime() != null) {
			 if (reservationService.duplicateCheckSecond(reservationRegisterForm)) {
				 FieldError fieldError = new FieldError(bindingResult.getObjectName(), "reservationTime", "2時間以内の予約の重複はできません。");
		         bindingResult.addError(fieldError);
		     }
		}
		 
		 if(bindingResult.hasErrors()) {
			 	User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
			 	Store store = storeRepository.getReferenceById(reservationRegisterForm.getStoreId());
				List<StoreCategories> storeCategories = storeCategoriesRepository.findByStoreId(reservationRegisterForm.getStoreId());
			 	List<Review> reviewList = reviewRepository.findTop6ByStoreOrderByCreatedAtDesc(store);
			 	Favorite favorite = favoriteRepository.findByUserAndStore(user, store);
			 	List<LocalDate> dateList = storeService.createDateList(store);
				List<String> timeList = storeService.createTimeList(store);
			 	
				model.addAttribute("favorite", favorite);
				model.addAttribute("reviewList", reviewList);
				model.addAttribute("dateList", dateList);
				model.addAttribute("timeList", timeList);
				model.addAttribute("storeCategories", storeCategories);
			 	model.addAttribute("store", store);
			 	model.addAttribute("user", user);

				return "freeMembership/store/show";
			}
			
		reservationService.create(reservationRegisterForm);
		
		redirectAttributes.addFlashAttribute("successMessage", "予約が完了しました。");
		return "redirect:/premium/reservation";
	}
	
	
	@PostMapping("/premium/reservation/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		reservationRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("successMessage", "予約をキャンセルしました。");
		return "redirect:/premium/reservation";
		
	}
	
}
