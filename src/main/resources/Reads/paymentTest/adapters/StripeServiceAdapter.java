package Reads.paymentTest.adapters;

import com.mayur.DesiCart.shop.paymentTest.PaymentProcessor;
import org.springframework.stereotype.Component;

@Component
public class StripeServiceAdapter implements PaymentProcessor {
    public void makePayment(Long amount){
        System.out.println("Payment made by Stripe: "+amount);
    }

    public String getGateway(){
        return "Stripe";
    }
}
