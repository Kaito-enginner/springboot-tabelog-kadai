package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	public Favorite findByUserAndStore(User user, Store store);
	Page<Favorite> findByUser(User user, Pageable pageable);

}
