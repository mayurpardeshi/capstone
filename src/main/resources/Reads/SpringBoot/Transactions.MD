In this project we have used @Transaction at 4 places:
1. placeOrder(userId) - Most critical - it creates the order, create multiple OrderItems, updates productInventory and clears the cart. If any of the 4 steps fail whole transaction will have to roll back
2. addCartItem - updates the cartItem quantity and updates the CartTotal amount, if the total isn't updated, UI will show the wrong price
3. removeCartItem - deletes a cartItem and updates the Cart total amount
4. clearCart(cartId) - deletes multiple rows from the cart_items table and resets the cart table

The reason to make @transactional is a safety wrapper around business logic. Spring actually ensures that all database method follows **ACID** properties


## FLOW ORDER PLACEMENT:
Here is exactly what happens inside the placeOrder transaction from start to finish:

**_Phase A: Preparation (Read)_**
1. Fetch Cart: The app retrieves the Cart and its User.
2. Validate: It checks if the cart is empty. If empty, the transaction stops immediately (no changes made).
3. Initialize Order: A new Order object is created in memory (Status: PENDING).

**_Phase B: Processing (Business Logic)_**
4. Create OrderItems:
 * The app loops through cart.getCartItems().
 * Inventory Check: For each item, it checks if stock >= quantity. If null or 0, it throws your ProductOutOfStockException.
 * Stock Deduction: It subtracts the quantity from the Product object. Note: The database isn't updated yet; only the object in memory is "dirty."
 * Price Snapshot: The current price is copied into the OrderItem.
 * Calculate Total: All OrderItem prices are summed to set order.setTotalAmount().

**_Phase C: Completion (Write & Cleanup)_**
5. Save Order: orderRepository.save(order) is called. Because of CascadeType.ALL, this automatically saves all the OrderItems we created.
6. Clear Cart: The items are removed from the cart.
8. The "Commit": The method finishes. Spring "flushes" the transaction.

    SQL 1: INSERT into orders
    SQL 2: INSERT into order_items (multiple rows)
    SQL 3: UPDATE products (setting new inventory levels)
    SQL 4: DELETE from cart_items
    SQL 5: UPDATE carts (setting total to 0)