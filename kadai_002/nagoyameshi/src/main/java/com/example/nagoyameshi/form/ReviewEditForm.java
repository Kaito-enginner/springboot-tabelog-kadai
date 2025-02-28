package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewEditForm {
	@NotNull
	private Integer id;
	
	@NotNull
	private Integer storeId;
	
	@NotNull
	private Integer userId;
	
	@NotNull(message = "評価を入力してください。")
	private Integer evaluation;
	
	@NotBlank(message = "コメントを入力してください。")
	private String comment;

}
