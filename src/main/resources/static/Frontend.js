let currentOrderId = null;

async function placeOrder() {
    const res = await fetch("http://localhost:8081/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ /* cart info */ })
    });

    const result = await res.json();
    currentOrderId = result.data.id;

    initiatePayment(currentOrderId);
}

async function initiatePayment(orderId) {
    const response = await fetch("http://localhost:8081/payments/initiate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ orderId: orderId })
    });

    const result = await response.json();
    const data = result.data;

    const options = {
        key: data.apiKey,
        amount: data.amount,
        currency: data.currency,
        order_id: data.orderId,
        name: "DesiCart",
        prefill: {
            name: data.customerName,
            email: data.customerEmail
        },
        handler: function (response) {
            verifyPayment(response);
        }
    };

    new Razorpay(options).open();
}
