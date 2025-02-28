package com.example.nagoyameshi.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.PasswordResetForm;
import com.example.nagoyameshi.repository.PasswordResetTokenRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class PasswordResetTokenService {
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final JavaMailSender javaMailSender;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository, JavaMailSender javaMailSender, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.javaMailSender = javaMailSender;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public void create(User user, String token) {
		PasswordResetToken passwordResetToken = new PasswordResetToken();
		
		passwordResetToken.setUser(user);
		passwordResetToken.setToken(token);
		
		passwordResetTokenRepository.save(passwordResetToken);
	}
	
	
	// 入力されたメールアドレスに再設定用のメールを送信する
	public void sendEmail(String email, String token, String requestUrl) {
		String recipientAddress = email;
        String subject = "メール認証";
        String resetUrl = requestUrl + "/verify?token=" + token;
        String message = "以下のリンクをクリックしてパスワードの再設定を完了してください。";
        
        SimpleMailMessage mailMessage = new SimpleMailMessage(); 
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message + "\n" + resetUrl);
        javaMailSender.send(mailMessage);
	}
	
	// トークンの文字列で検索した結果を返す
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }
    
    @Transactional
	public void reset(PasswordResetForm passwordResetForm) {
    	Integer id = passwordResetForm.getId();
    	User user = userRepository.getReferenceById(id);
    	
    	user.setPassword(passwordEncoder.encode(passwordResetForm.getPassword()));
    	
    	userRepository.save(user);
	}
    
    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    } 
}
