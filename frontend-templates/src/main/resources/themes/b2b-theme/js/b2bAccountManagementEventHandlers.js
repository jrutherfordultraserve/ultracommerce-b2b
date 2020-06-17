/**
 * Event handlers that deal with the B2B customers accounts
 */

$('body').on('click', 'a.js-viewMemberDetails', function() {
    var url = $(this).attr('href');
    var $memberDetails = $(this).parents('.js-member').find('.js-memberDetails');

    // if this is the currently opened member details, collapse it
    if ($memberDetails.is(':visible')) {
        $memberDetails.slideToggle();
    } else {
        $memberDetails.load(url, function() {
            $visibleSections = $('.js-memberDetails:visible');
            $memberDetails.slideToggle();
            $visibleSections.slideToggle();
        });
    }
    return false;
});

$('body').on('click', 'a.js-viewOrderApprovalDetails', function() {
    var url = $(this).attr('href');
    var $orderDetails = $(this).parents('.js-order-approvals-table').next('div');

    // if this is the currently opened order details, collapse it
    if ($orderDetails.is(':visible')) {
        $orderDetails.slideToggle();
    } else {
        $orderDetails.load(url, function() {
            var $visibleSections = $('.js-orderDetails:visible');
            $orderDetails.slideToggle();
            $visibleSections.slideToggle();
        });
    }
    return false;
});