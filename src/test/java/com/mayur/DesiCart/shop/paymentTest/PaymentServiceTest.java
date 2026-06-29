//package com.mayur.DesiCart.shop.paymentTest;
//
//import com.mayur.DesiCart.shop.paymentTest.adapters.GpayServiceAdapter;
//import com.mayur.DesiCart.shop.paymentTest.adapters.PaypalServiceAdapter;
//import com.mayur.DesiCart.shop.paymentTest.adapters.StripeServiceAdapter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PaymentServiceTest {
//    @Mock
//    private GpayServiceAdapter gpayService;
//
//    @Mock
//    private PaypalServiceAdapter paypalService;
//
//    @Mock
//    private StripeServiceAdapter stripeService;
//
//
//    private PaymentService paymentService;
//
//    @BeforeEach
//    void setup(){
//        when(gpayService.getGateway()).thenReturn("Gpay");
//        when(paypalService.getGateway()).thenReturn("Paypal");
//        when(stripeService.getGateway()).thenReturn("Stripe");
//        List<PaymentProcessor> paymentProcessors = List.of(gpayService, paypalService, stripeService);
//        paymentService = new PaymentService(paymentProcessors);
//        clearInvocations(gpayService, stripeService, paypalService);
//    }
//
//
//
//    @Test
//    void processPaymentWithPaypal() {
//        paymentService.processPayment(1000L, "Paypal");
//        verify(paypalService).makePayment(1000L);
//        verifyNoInteractions(gpayService, stripeService);
//    }
//
//    @Test
//    void processPaymentWithGpay() {
//        paymentService.processPayment(1000L, "Gpay");
//        verify(gpayService).makePayment(1000L);
//        verifyNoInteractions(stripeService, paypalService);
//    }
//
//    @Test
//    void processPaymentWithStripe() {
//        paymentService.processPayment(1000L, "Stripe");
//        verify(stripeService).makePayment(1000L);
//        verifyNoInteractions(paypalService, gpayService);
//    }
//}