package com.example.nagoyameshi.entity;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "stores")
@Data
public class Store {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "image_name")
	private String imageName;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "price")
	private Integer price;
	
	@Column(name = "openinghoures_start")
	private LocalTime openingHouresStart;
	
	@Column(name = "openinghoures_end")
	private LocalTime openingHouresEnd;
	
	@Column(name = "holiday")
	private Integer holiday;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreCategories> storeCategories = new ArrayList<>();
}
