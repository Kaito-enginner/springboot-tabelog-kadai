package com.example.nagoyameshi.form;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreEditForm {
	@NotNull
	private Integer id;
	
	@NotBlank(message = "店舗名を入力してください。")
	private String name;
	
    private MultipartFile imageFile;
	
	
	@NotBlank(message = "説明を入力してください。")
	private String description;
	
	@NotNull(message = "予算を入力してください。")
    @Min(value = 1, message = "予算は1円以上に設定してください。")
	private Integer price;
	
	@NotNull(message = "営業開始時間を入力してください。")
	private LocalTime openingHouresStart;
	
	@NotNull(message = "営業終了時間を入力してください。")
	private LocalTime openingHouresEnd;

	@NotNull(message = "定休日を入力してください。")
	private Integer holiday;
	
	@NotBlank(message = "住所を入力してください。")
	private String address;
	
	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;

	@NotNull(message = "カテゴリを選択してください。")
	private List<Integer> categoryId;
	
}
