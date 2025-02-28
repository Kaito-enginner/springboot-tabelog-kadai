package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.param.SubscriptionCancelParams;

@Controller
public class SubscriptionController {
	private final StripeService stripeService;
	private final UserService userservice;
	
	@Value("${stripe.api-key}")
    private String stripeApiKey;
	
	public SubscriptionController(StripeService stripeService, UserService userservice) {
		this.stripeService = stripeService;
		this.userservice = userservice;
	}
	
	@GetMapping("/subscription")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						HttpServletRequest httpServletRequest,
						Model model) 
	{
        String sessionId = stripeService.createStripeSession(userDetailsImpl.getUser(), httpServletRequest);
		
		model.addAttribute("sessionId", sessionId);
		
		return "subscription/index";
	}
	
	
	@GetMapping("/premium/cancel")
	public String cancel(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userDetailsImpl.getUser();
		
		model.addAttribute("user", user);
		return "subscription/cancel";
	}
	
	
	@PostMapping("/premium/delete")
    public String delete(@RequestParam(name = "subscriptionId") String subscriptionId,
    					 @RequestParam(name = "userId") Integer userId, HttpSession httpsession,
    					 RedirectAttributes redirectAttributes) 
	{
		
        try {
            Stripe.apiKey = stripeApiKey;

            Subscription subscription = Subscription.retrieve(subscriptionId);
            SubscriptionCancelParams params = SubscriptionCancelParams.builder().build();
            subscription = subscription.cancel(params);
            
            userservice.downgrade(userId);
            
            httpsession.invalidate();
            
    		redirectAttributes.addFlashAttribute("successMessage", "有料会員を解約しました。");

            return "redirect:/";
        } catch (StripeException e) {
        	redirectAttributes.addFlashAttribute("failedMessage", "有料会員の解約に失敗しました。");

            return "redirect:/";        
        }
    }
	
	@GetMapping("/premium/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model, HttpServletRequest httpServletRequest) {
		User user = userDetailsImpl.getUser();
		
		String sessionId = stripeService.StripeSessionChangePaymentMethod(user.getCustomerId(), user.getSubscriptionId(), httpServletRequest);
		
		model.addAttribute("sessionId", sessionId);
		
		return "subscription/edit";
	}
}



























