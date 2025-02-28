package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.StoreRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	
	public ReviewService(ReviewRepository reviewRepository, StoreRepository storeRepository, UserRepository userRepository) {
		this.reviewRepository= reviewRepository;
		this.storeRepository = storeRepository;
		this.userRepository = userRepository;
	}
	
	// レビューを投稿
	@Transactional
	public void create(ReviewRegisterForm reviewRegisterForm) {
		Review review = new Review();
		
		Store store = storeRepository.getReferenceById(reviewRegisterForm.getStoreId());
		User user = userRepository.getReferenceById(reviewRegisterForm.getUserId());
		
		review.setStore(store);
		review.setUser(user);
		review.setEvaluation(reviewRegisterForm.getEvaluation());
		review.setComment(reviewRegisterForm.getComment());
		
		reviewRepository.save(review);
	}
	
	// レビューを更新
	@Transactional
	public void update(ReviewEditForm reviewEditForm) {
		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
		
		Store store = storeRepository.getReferenceById(reviewEditForm.getStoreId());
		User user = userRepository.getReferenceById(reviewEditForm.getUserId());
		
		review.setStore(store);
		review.setUser(user);
		review.setEvaluation(reviewEditForm.getEvaluation());
		review.setComment(reviewEditForm.getComment());
		
		reviewRepository.save(review);
	}
}
