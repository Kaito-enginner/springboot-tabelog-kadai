package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.StoreCategories;

public interface StoreCategoriesRepository extends JpaRepository<StoreCategories, Integer> {
	List<StoreCategories> findByStoreId(Integer storeId);
	List<StoreCategories> findByCategoryId(Integer categoryId);

}
