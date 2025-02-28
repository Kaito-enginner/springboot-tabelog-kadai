package com.example.nagoyameshi.form;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ReservationRegisterForm {
	@NotNull
	private Integer storeId;
	
	@NotNull
	private Integer userId;
	
	@NotNull(message = "日付を選択してください。")
	private LocalDate reservationDate;
	
	@NotNull(message = "時間を選択してください。")
	private LocalTime reservationTime;
	
	@NotNull(message = "人数を選択してください。")
    @Min(value = 1, message = "予約人数は1人以上に設定してください。")
	private Integer numberOfPeople;
}
