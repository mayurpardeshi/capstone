**User Action	            Endpoint	            Order State	                Payment State**
Clicks "Checkout"	    /place-order	        ORDER_PLACED	            N/A
Confirms Address	    /checkout-order	        ORDER_CHECKED_OUT	        N/A
Clicks "Pay"	        /initiate-payment	    ORDER_PAYMENT_INITIATED	    INITIATED
Payment Success	        Webhook	                ORDER_PAID	                SUCCESS
Payment Fails	        Webhook	                ORDER_PAYMENT_FAILED	     FAILED
Payment Cancelled	    Webhook	                ORDER_PAYMENT_CANCELLED	     FAILED