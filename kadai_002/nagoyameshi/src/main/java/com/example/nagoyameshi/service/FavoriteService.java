package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.StoreRepository;
import com.example.nagoyameshi.repository.UserRepository;


@Service
public class FavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final StoreRepository storeRepository;

	
	public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, StoreRepository storeRepository) {
		this.favoriteRepository = favoriteRepository;
		this.userRepository = userRepository;
		this.storeRepository = storeRepository;
	}
	
	// お気に入りに登録
	@Transactional
	public void create(Integer userId, Integer storeId) {
		Favorite favorite = new Favorite();
		User user = userRepository.getReferenceById(userId);
		Store store = storeRepository.getReferenceById(storeId);
		
		favorite.setUser(user);
		favorite.setStore(store);
		
		favoriteRepository.save(favorite);
	}
}
