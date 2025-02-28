package com.example.nagoyameshi.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.StoreCategories;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.StoreCategoriesRepository;
import com.example.nagoyameshi.repository.StoreRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StoreService;

@Controller
@RequestMapping("/stores")
public class StoreController {
	private final StoreRepository storeRepository;
	private final StoreCategoriesRepository storeCategoriesRepository;
	private final FavoriteRepository favoriteRepository;
	private final ReviewRepository reviewRepository;
	private final StoreService storeService;
	
	public StoreController(StoreRepository storeRepository, StoreCategoriesRepository storeCategoriesRepository, FavoriteRepository favoriteRepository, ReviewRepository reviewRepository, StoreService storeService) {
		this.storeRepository = storeRepository;
		this.storeCategoriesRepository = storeCategoriesRepository;
		this.favoriteRepository = favoriteRepository;
		this.reviewRepository = reviewRepository;
		this.storeService= storeService; 
	}
	
	@GetMapping
    public String index(@RequestParam(name = "page", defaultValue = "0") int page,
    					@RequestParam(name = "keyword", required = false) String keyword, 
    					@RequestParam(name = "category", required = false) Integer category, 
    					@RequestParam(name = "price", required = false) Integer price,
    					@RequestParam(name = "sort", required = false) String sort,Model model) 
    {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")
									   .and(Sort.by(Sort.Direction.ASC, "price")));
		
		Sort sortParam = Sort.unsorted();
	        if (sort != null) {
	            String[] sortParams = sort.split(",");
	            if (sortParams.length == 2) {
	                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
	                sortParam = Sort.by(direction, sortParams[0]);
	            }
	        }

	    pageable = PageRequest.of(page, 10, sortParam);
	    
    	Page<Store> storePage;
    	
    	if(keyword != null && !keyword.isEmpty()) {
    		storePage = storeRepository.findByNameLike("%" + keyword + "%", pageable);
    	} 
    	else if(category != null) {
    		storePage = storeRepository.findByCategoryId(category, pageable);

    	} 
    	else if(price != null) {
    		storePage = storeRepository.findByPriceLessThanEqual(price, pageable);
    	} 
    	else {
    		storePage = storeRepository.findAll(pageable);
    	}    

    	model.addAttribute("storePage",storePage);
    	model.addAttribute("sort", sort);
    	model.addAttribute("keyword", keyword);
    	model.addAttribute("category", category);
    	model.addAttribute("price", price);
    	
    	return "freeMembership/store/index";
    }
	
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,Model model) {
		Store store = storeRepository.getReferenceById(id);
		List<StoreCategories> storeCategories = storeCategoriesRepository.findByStoreId(id);
		
		
        List<Review> reviewList = reviewRepository.findTop6ByStoreOrderByCreatedAtDesc(store);
		
		if(userDetailsImpl != null) {
			Favorite favorite = favoriteRepository.findByUserAndStore(userDetailsImpl.getUser(), store);
			List<LocalDate> dateList = storeService.createDateList(store);
			List<String> timeList = storeService.createTimeList(store);
			
			// 同じユーザーと店舗でレビューが投稿されていないかをチェックする
	     	Review review = reviewRepository.findByStoreAndUser(store, userDetailsImpl.getUser());
			
			model.addAttribute("favorite", favorite);
			model.addAttribute("dateList", dateList);
			model.addAttribute("timeList", timeList);
			model.addAttribute("reservationRegisterForm", new ReservationRegisterForm());
			model.addAttribute("user", userDetailsImpl.getUser());
			model.addAttribute("reviewbool", review);
		}
		
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("store", store);
		model.addAttribute("storeCategories", storeCategories);
		
		return "freeMembership/store/show";
	}

}
