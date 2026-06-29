package Reads.paymentTest.adapters;

import com.mayur.DesiCart.shop.paymentTest.PaymentProcessor;
import org.springframework.stereotype.Component;

@Component
public class PaypalServiceAdapter implements PaymentProcessor {
    public void makePayment(Long amount){
        System.out.println("Payment made by Paypal: "+amount);
    }

    public String getGateway(){
        return "Paypal";
    }
}
