package com.example.nagoyameshi.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.SetupIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

@Service
public class StripeService {
	private final UserService userService;
	
	@Value("${stripe.api-key}")
    private String stripeApiKey;
	
	public StripeService(UserService userService) {
		this.userService = userService;
	}
	
	
	
	// セッションを作成し、Stripeに必要な情報を返す
	public String createStripeSession(User user, HttpServletRequest httpServletRequest) {
		Stripe.apiKey = stripeApiKey;
        String requestUrl = new String(httpServletRequest.getRequestURL());

		String priceId = "price_1QsgrJJy1wLqoo0tKsKy2A1x";
        
		SessionCreateParams params = new SessionCreateParams.Builder()
		  .setSuccessUrl(requestUrl.replaceAll("/subscription", "/?successfulContract"))
		  .setCancelUrl(requestUrl.replaceAll("/subscription", ""))
		  .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
		  .addLineItem(new SessionCreateParams.LineItem.Builder()
		    .setQuantity(1L)
		    .setPrice(priceId)
		    .build()
		  )
		  .setSubscriptionData(
	          new SessionCreateParams.SubscriptionData.Builder()
                  .putMetadata("userId", user.getId().toString())
                  .build()
          )
		  .build();

		try {
			Session session = Session.create(params);
			return session.getId();
		} catch (StripeException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
    // modeがsubscriptionならroleをpremiumに更新し、setupなら支払い方法を設定する。
	public void processSessionCompleted(Event event) {
        Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
        optionalStripeObject.ifPresentOrElse(stripeObject -> {
            Session session = (Session)stripeObject;
            
            if(session.getMode().equals("subscription")) {
                SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("subscription").build();
                
                try {
                    session = Session.retrieve(session.getId(), params, null);
                    Map<String, String> subscriptionObject = session.getSubscriptionObject().getMetadata();
                    String subscriptionId = session.getSubscriptionObject().getId();
                    String customerId = session.getCustomer();
                    userService.upgrade(subscriptionObject, subscriptionId, customerId);
                } catch (StripeException e) {
                    e.printStackTrace();
                }
                System.out.println("アップグレードに成功しました。");
                System.out.println("Stripe API Version: " + event.getApiVersion());
                System.out.println("stripe-java Version: " + Stripe.VERSION);
            }
            else if(session.getMode().equals("setup")) {
                try {
                	String setupIntentId = session.getSetupIntent();
                	if(setupIntentId != null) {
                		SetupIntent setupIntent = SetupIntent.retrieve(setupIntentId);
                        Map<String, String> setupIntentMetadata  = setupIntent.getMetadata();
                        String paymentMethod = setupIntent.getPaymentMethod();
                        changeInvoicePaymentMethod(setupIntentMetadata, paymentMethod);
                        changeSubscriptionPaymentMethod(setupIntentMetadata, paymentMethod);
                	} else {
                        System.out.println("セットアップインテントIDが見つかりません。");
                    }
                } catch (StripeException e) {
                    e.printStackTrace();
                }
                System.out.println("支払い方法を更新しました。");
                System.out.println("Stripe API Version: " + event.getApiVersion());
                System.out.println("stripe-java Version: " + Stripe.VERSION);
            }},
        () -> {
            System.out.println("Stripeとの連携に失敗しました。");
            System.out.println("Stripe API Version: " + event.getApiVersion());
            System.out.println("stripe-java Version: " + Stripe.VERSION);
        });
		
	}
	
	// セッションを作成し、Stripeに必要な情報を返す
	public String StripeSessionChangePaymentMethod(String customerId, String subscriptionId, HttpServletRequest httpServletRequest) {
        Stripe.apiKey = stripeApiKey;
        String requestUrl = new String(httpServletRequest.getRequestURL());
        
		SessionCreateParams params =
		  SessionCreateParams.builder()
		    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
		    .setMode(SessionCreateParams.Mode.SETUP)
		    .setCustomer(customerId)
		    .setSetupIntentData(
		      SessionCreateParams.SetupIntentData.builder()
		        .putMetadata("customer_id", customerId)
		        .putMetadata("subscription_id", subscriptionId)
		        .build())
		    .setSuccessUrl(requestUrl.replaceAll("/premium/edit", "/?successfulChange"))
		    .setCancelUrl(requestUrl.replaceAll("/premium/edit", ""))
		    .build();
		
		try {
			Session session = Session.create(params);
			return session.getId();
		} catch (StripeException e) {
			e.printStackTrace();
			return "";
		}

    }
	
	// 請求書の支払い方法を設定する
	public void changeInvoicePaymentMethod ( Map<String, String> setupIntentMetadata, String paymentMethod) throws StripeException {
	    Stripe.apiKey = stripeApiKey;
	    String customerId = setupIntentMetadata.get("customer_id");
		Customer resource = Customer.retrieve(customerId);
		
		CustomerUpdateParams params =
		  CustomerUpdateParams.builder()
		    .setInvoiceSettings(
		      CustomerUpdateParams.InvoiceSettings.builder()
		        .setDefaultPaymentMethod(paymentMethod)
		        .build()
		    )
		    .build();
		
		resource.update(params);
	}
	
	// サブスクリプションの支払い方法を設定する
	public void changeSubscriptionPaymentMethod( Map<String, String> setupIntentMetadata, String paymentMethod) throws StripeException {
	    Stripe.apiKey = stripeApiKey;
	    String subscriptionId = setupIntentMetadata.get("subscription_id");
	    System.out.println(subscriptionId);
		Subscription subscription = Subscription.retrieve(subscriptionId);
		Map<String, Object> params = new HashMap<>();
		params.put("default_payment_method", paymentMethod);
		subscription.update(params);
	}
	
}
