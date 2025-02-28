package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer>{
	public Page<Review> findByStore(Store store, Pageable pageable);
	public List<Review> findTop6ByStoreOrderByCreatedAtDesc(Store store);
	public Review findByStoreAndUser(Store store, User user);
}
