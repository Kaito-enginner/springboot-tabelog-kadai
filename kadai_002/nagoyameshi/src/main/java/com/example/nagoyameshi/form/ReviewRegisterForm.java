package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ReviewRegisterForm {
	@NotNull
	private Integer storeId;
	
	@NotNull
	private Integer userId;
	
	@NotNull(message = "評価を入力してください。")
	private Integer evaluation;
	
	@NotBlank(message = "コメントを入力してください。")
	private String comment;
}
