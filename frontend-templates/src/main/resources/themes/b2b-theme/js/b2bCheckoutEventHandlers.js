var $body = $('body');

/**
 * Show the details of the personal credit card payment method
 */
$body.on('click', '.js-paymentMethodPersonalCreditCard', function() {
    Checkout.hideAllPaymentOptionDescriptions();
    Checkout.showPaymentOptionDetails($(this));
});

/**
 * Show the details of the "charge to account" payment method
 */
$body.on('click', '.js-paymentMethodChargeToAccount', function() {
    Checkout.hideAllPaymentOptionDescriptions();
    Checkout.showPaymentOptionDetails($(this));
});

$(function() {
    Checkout.initialize();

    $('.js-paymentMethodChargeToAccount').click();
});