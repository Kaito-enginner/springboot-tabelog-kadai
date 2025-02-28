package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer> {
	public Page<Store> findByNameLike(String keyword, Pageable pageable);
	public Page<Store> findByPriceLessThanEqual(Integer price, Pageable pageable);
	
	 @Query("""
		        SELECT s FROM Store s 
		        JOIN StoreCategories sc ON s.id = sc.store.id 
		        JOIN Category c ON sc.category.id = c.id 
		        WHERE c.id = :categoryId
		    """)
	 public Page<Store> findByCategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);
	 
}
