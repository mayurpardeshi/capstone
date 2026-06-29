package com.mayur.DesiCart.shop.cartAndCheckout.services.PaymentAdaptors;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;
import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentVerificationRequest;
import com.mayur.DesiCart.shop.cartAndCheckout.exceptions.PaymentGatewayException;
import com.mayur.DesiCart.shop.cartAndCheckout.models.CurrencyCode;
import com.mayur.DesiCart.shop.cartAndCheckout.models.PaymentType;
import com.mayur.DesiCart.shop.cartAndCheckout.services.PaymentProcessor;
import com.mayur.DesiCart.shop.cartAndCheckout.services.utils.AdapterUtils;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.PaymentDetails;
import com.mayur.DesiCart.shop.order.orderRepository.PaymentDetailsRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service("RAZOR_PAY")
@RequiredArgsConstructor
public class RazorPayServiceAdapter implements PaymentProcessor {

    private final PaymentDetailsRepository paymentDetailsRepository;
    private final AdapterUtils adapterUtils;
    @Autowired
    private RazorpayClient razorpayClient;

    private static final Logger log = LoggerFactory.getLogger(RazorPayServiceAdapter.class);
    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    public String setUpGateway() {
        return PaymentType.RAZOR_PAY.toString();
    }

    @Override
    public PaymentDetails verifyPayment(String gatewayOrderId) {
        try {
            com.razorpay.Order order = razorpayClient.orders.fetch(gatewayOrderId);
            if ("paid".equals(order.get("status"))) {
                return syncToDb(order);
            }
            return null;
        } catch (RazorpayException e) {
            log.error("API Fetch failed for Razorpay: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private PaymentDetails syncToDb(com.razorpay.Order gatewayOrder) {
        String gatewayOrderId = gatewayOrder.get("id");
        //double check one last time to avoid race conditions
        return paymentDetailsRepository.findByGatewayOrderId(gatewayOrderId)
                .orElseGet(() -> {
                    log.info("Self-healing: Synchronizing missing payment for Order ID: {}", gatewayOrderId);
                    PaymentDetails paymentDetails = new PaymentDetails();
                    paymentDetails.setGatewayOrderId(gatewayOrderId);
                    int amountInPaise = gatewayOrder.get("amount");
                    paymentDetails.setAmount(BigDecimal.valueOf(amountInPaise/100.0));
                    paymentDetails.setStatus(mapStatus(gatewayOrder.get("status")));
                    paymentDetails.setPaymentDate(LocalDateTime.now());
                    try {
                        return paymentDetailsRepository.saveAndFlush(paymentDetails);
                    } catch (DataIntegrityViolationException e){
                        return paymentDetailsRepository.findByGatewayOrderId(gatewayOrderId).orElseThrow(() ->
                            new IllegalStateException("Database consistency error during sync"));
                    }
                });
    }

    private String mapStatus(String gatewayStatus) {
        // Razorpay statuses: created, attempted, paid
        return "paid".equalsIgnoreCase(gatewayStatus) ? "SUCCESS" : "PENDING";
    }

    /**
     * ------------------------------------------------------------
     * PAYMENT INITIALIZATION
     * ------------------------------------------------------------
     * - Creates a Razorpay order for the given Order
     * - Reuses existing gateway order if already created
     * - Persists payment details
     * - Returns data required by frontend for payment checkout
     * ------------------------------------------------------------
     */
    @Override
    public PaymentInitializationResponse initiatePayment(Order order) {
        adapterUtils.validateOrder(order);

        if (adapterUtils.isPaymentAlreadyInitialized(order)){
            return buildResponseFromExisting(order);
        }

        try {
            RazorpayClient razorpayClient = adapterUtils.createRazorpayClient();
            long amountInPaise = adapterUtils.convertToPaise(order.getTotalAmount());
            JSONObject orderRequest = adapterUtils.buildRazorpayOrderRequest(order, amountInPaise);

            log.info("Creating Razorpay order for OrderId={}, AmountPaise={}",
                    order.getId(), amountInPaise);

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            String gatewayOrderId = razorpayOrder.get("id");

            adapterUtils.savePaymentDetails(order, gatewayOrderId);
            return buildPaymentInitializationResponse(order, gatewayOrderId, amountInPaise);

        } catch (RazorpayException ex) {
            log.error("Razorpay order creation failed for OrderId={}", order.getId(), ex);
            throw new PaymentGatewayException("Failed to initiate payment. Please try again later.");
        }

    }

    private PaymentInitializationResponse buildResponseFromExisting(Order order){
        adapterUtils.validateOrderAndPayment(order);
        PaymentDetails existingPaymentDetails = order.getPaymentDetails();
        BigDecimal amount = adapterUtils.resolvePaymentAmount(order, existingPaymentDetails);
        long amountInPaise = adapterUtils.convertToPaise(amount);
        return PaymentInitializationResponse.builder()
                .gatewayName(setUpGateway())
                .orderId(existingPaymentDetails.getGatewayOrderId())
                .amount(amountInPaise)
                .apiKey(keyId)
                .paymentUrl("http://localhost:8081/view/payments/pay/" + order.getId())
                .customerName(order.getUser().getFirstName())
                .customerEmail(order.getUser().getUserId())
                .build();

    }

    public boolean verifyPayment(PaymentVerificationRequest request){
        try{
            String secret = keySecret;
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());
            return Utils.verifyPaymentSignature(options, secret);
        } catch (RazorpayException e) {
            log.error("Payment verification failed", e);
            return false;
        }
    }

    private PaymentInitializationResponse buildPaymentInitializationResponse(
            Order order,
            String gatewayOrderId,
            long amountInPaise) {

        return PaymentInitializationResponse.builder()
                .gatewayName(setUpGateway())
                .orderId(gatewayOrderId)
                .amount(amountInPaise)
                .currency(CurrencyCode.INR.name())
                .paymentUrl("http://localhost:8081/view/payments/pay/" + order.getId())
                .apiKey(keyId)
                .customerName(order.getUser().getFirstName())
                .customerEmail(order.getUser().getUserId())
                .build();
    }
}
