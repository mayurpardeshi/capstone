//package com.mayur.DesiCart.shop.cartAndCheckout.controllers;
//
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentWebhookDto;
//import com.mayur.DesiCart.shop.order.service.OrderService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/payments/webhook")
//@RequiredArgsConstructor
//@Slf4j
//public class WebhookController {
//
//    private final OrderService orderService;
//
//    @PostMapping("/razorpay")
//    public ResponseEntity<String> handleRazorpayWebhook(@RequestBody JsonNode root) {
//        try {
//            // Razorpay sends 'event'. We only care about 'payment.captured'
//            String event = root.path("event").asText();
//            if (!"payment.captured".equals(event)) {
//                return ResponseEntity.ok("Event ignored");
//            }
//
//            // Deep Navigation: payload -> payment -> entity
//            JsonNode entity = root.path("payload").path("payment").path("entity");
//
//            if (entity.isMissingNode()) {
//                log.error("Could not find the 'entity' node in the Razorpay payload");
//                return ResponseEntity.badRequest().body("Invalid Payload Structure");
//            }
//
//            // Build the DTO using the values from the 'entity' node
//            PaymentWebhookDto dto = PaymentWebhookDto.builder()
//                    .gatewayOrderId(entity.path("order_id").asText())
//                    .gatewayPaymentId(entity.path("id").asText())
//                    .email(entity.path("email").asText())
//                    .contact(entity.path("contact").asText())
//                    .method(entity.path("method").asText())
//                    // wallet/bank can be null depending on method, asText() handles this gracefully
//                    .wallet(entity.path("wallet").asText(null))
//                    .bank(entity.path("bank").asText(null))
//                    .amount(entity.path("amount").asLong())
//                    .tax(entity.path("tax").asInt())
//                    .fee(entity.path("fee").asInt())
//                    .build();
//
//            log.info("Processing Payment DTO: {}", dto);
//
//            // Pass to Service
//            orderService.completeOrder(dto);
//
//            return ResponseEntity.ok("Webhook Processed");
//        } catch (Exception e) {
//            log.error("Error in Webhook Controller: ", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//}
package com;

