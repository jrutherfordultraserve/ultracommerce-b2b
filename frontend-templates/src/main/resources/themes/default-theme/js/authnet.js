$(document).ready(function() {
    $('#anet_checkout_form_button').on("click", function(e) {
        e.preventDefault();
        sendPaymentDataToAnet();
    });
});