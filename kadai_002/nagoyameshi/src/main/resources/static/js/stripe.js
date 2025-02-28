const stripe = Stripe('pk_test_51QfynPJy1wLqoo0t1mAgdvI7zPzprKzc6LGd0hFc7YWSprxMAhZz1bWXzUVwOHfQ32prX6QKhbIa2u5oJAiHL4Aj00B466iEVd');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
 stripe.redirectToCheckout({
   sessionId: sessionId
 })
});