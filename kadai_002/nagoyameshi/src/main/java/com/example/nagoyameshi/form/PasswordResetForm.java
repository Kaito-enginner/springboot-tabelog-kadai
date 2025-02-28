package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetForm {
	@NotNull
	private Integer id;
	
	@NotBlank(message = "パスワードを入力してください。")
	private String password;
	
	@NotBlank(message = "パスワード（確認用）を入力してください。")
	private String passwordConfirmation;
}
