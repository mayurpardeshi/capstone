package Reads.paymentTest.adapters;

import com.mayur.DesiCart.shop.paymentTest.PaymentProcessor;
import org.springframework.stereotype.Component;

@Component
public class GpayServiceAdapter implements PaymentProcessor {
    public void makePayment(Long amount){
        System.out.println("Payment made by Gpay: "+amount);
    }

    public String getGateway(){
        return "Gpay";
    }
}
