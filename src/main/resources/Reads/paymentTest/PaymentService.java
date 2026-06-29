package Reads.paymentTest;

import com.mayur.DesiCart.shop.order.models.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentService {
    private Map<String, PaymentProcessor> paymentProcessorMap;


    public PaymentService(List<PaymentProcessor> paymentProcessors){
        paymentProcessorMap = paymentProcessors.stream()
                .collect(Collectors.toMap(PaymentProcessor::getGateway, Function.identity()));
    }

    public void processPayment(Order order, String gateway){
        PaymentProcessor paymentProcessor = paymentProcessorMap.get(gateway);
        if (paymentProcessor == null){
            throw new IllegalArgumentException("Unsupported gateway");
        }
        // 1. Initiate payment through the specific gateway
        PaymentInitializationResponse response = paymentProcessor.initiatePayment(order);
        // 2. Save details back to the order
        order.setGatewayOrderId(response.getOrderId()); // Stores the external ID
        order.setPaymentGateway(gateway);              // Stores "RAZOR_PAY"
        orderRepository.save(order);
        return response;
    }

}
