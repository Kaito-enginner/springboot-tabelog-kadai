package com.example.nagoyameshi.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public User create(SignupForm signupForm) {
		User user = new User();
        Role role = roleRepository.findByName("ROLE_GENERAL");
		
		user.setName(signupForm.getName());
		user.setFurigana(signupForm.getFurigana());
		user.setPostalCode(signupForm.getPostalCode());
		user.setAddress(signupForm.getAddress());
		user.setPhoneNumber(signupForm.getPhoneNumber());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setRole(role);
		user.setEnabled(false);
		
		return userRepository.save(user);
	}
	
	@Transactional
	public void update(UserEditForm userEditForm) {
		User user = userRepository.getReferenceById(userEditForm.getId());
		
		user.setName(userEditForm.getName());
		user.setFurigana(userEditForm.getFurigana());
		user.setPostalCode(userEditForm.getPostalCode());
		user.setAddress(userEditForm.getAddress());
		user.setPhoneNumber(userEditForm.getPhoneNumber());
		user.setEmail(userEditForm.getEmail());
		
		userRepository.save(user);
	}
	
	// 有料会員へアップグレード
	@Transactional
	public void upgrade(Map<String, String> subscriptionObject, String subscriptionId, String customerId) {
		User user = userRepository.getReferenceById(Integer.valueOf(subscriptionObject.get("userId")));
		Role role = roleRepository.getReferenceById(3);
		
		user.setRole(role);
		user.setSubscriptionId(subscriptionId);
		user.setCustomerId(customerId);
		userRepository.save(user);
	}
	
	// 無料会員へダウングレード
	@Transactional
	public void downgrade(Integer userId) {
		User user = userRepository.getReferenceById(userId);
		Role role = roleRepository.getReferenceById(1);
		
		user.setRole(role);
		user.setSubscriptionId("");
		userRepository.save(user);
	}
	
	
	// アカウントを無効化する(論理削除)
	@Transactional
	public void disable(Integer userId) {
		User user = userRepository.getReferenceById(userId);
		
		user.setEnabled(false);
		userRepository.save(user);
	}
	
	//メールアドレスが変更されたかどうかをチェックする
	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return userEditForm.getEmail().equals(currentUser.getEmail());
	}
	
	
	// メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		
		if(user != null) {
			return true;
		}
		return false;
	}
	
	// パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
	public boolean isSamePassword(String password, String passwordConfirmation) {
		if(password.equals(passwordConfirmation)) {
			return true;
		}
		return false;
	}
	
	// ユーザーを有効にする
	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}
	
}
