var $body = $('body');

$body.on('click', '.js-addToQuote', function () {

    if (BLC.theme.vars.ajaxCart == "false") {
        return true;
    }

    var $selectedQuote = $(this);
    var $container = $selectedQuote.closest('.js-productContainer');
    var $addToCartButton = $container.find('.js-addToCart');
    var $form = $addToCartButton.closest('form');

    var $errorSpan = $container.find('.error');
    var $productOptionsSpan = $container.find('span.js-productOptionsSpan');
    var itemRequest = BLC.serializeObject($form);

    // Add any product add-ons to the request
    itemRequest = Object.assign({}, itemRequest, BLC.serializeObject($('.js-productAddOns')));

    Quote.buildProductOptionsForRequest(itemRequest, $container);
    Quote.buildQuoteConfigForRequest(itemRequest, $selectedQuote);

    var actionUrl = $form.attr('action');
    if ($selectedQuote.closest('.js-add-to-quote').length) {
        actionUrl = $('.js-add-to-quote').data('url');
    }

    BLC.ajax({
        url: actionUrl,
        type: "POST",
        dataType: "json",
        data: itemRequest
    }, function(data, extraData) {
        if (data.error) {
            Quote.handleAddToQuoteError(data, $container);
        } else {
            $errorSpan.hide();
            $productOptionsSpan.hide();
            Quote.addToQuoteSuccess(data, itemRequest);
        }
    });
    return false;
});

$body.on('click','.js-removeQuoteItem', function () {
    var $button = $(this),
        quoteId = $button.data('quoteid'),
        quoteItemId = $button.data('itemid');

    $('#quote-view').load(
        $button.data('url'),
        {
            quoteId: quoteId,
            quoteItemId: quoteItemId,
            csrfToken: $('input[name=csrfToken]').val()
        }
    )
});

//Get the quoteTable template in an AJAX call
$body.on('click', '.js-viewQuoteDetails', function () {
    var $button = $(this),
        quoteId = $button.data('quoteid'),
        $orderDetails = $button.parents('table').next('div');

    if ($orderDetails.is(':visible')) {
        $orderDetails.slideToggle();
    } else {
        $orderDetails.load("/account/quote/getQuoteTableView", {quoteId: quoteId, csrfToken: params.csrfToken}, function () {
            $visibleSections = $('.js-quote-details-container:visible');
            $orderDetails.slideToggle();
            $visibleSections.slideToggle();
        });
    }
    return false;
});

/**
 * Intercept update quantity operations and perform them via AJAX instead
 * This will trigger on any input with class "js-updateQuantity"
 */
$body.on('change', '.js-updateQuoteQuantity', function() {
    return Quote.updateItemQuantity(this);
});


$body.on('click', '.js-addCustomQuoteItem', function() {
    var $button = $(this),
        $form = $button.closest('form'),
        formElements = BLC.serializeObject($form);

    $('#quote-view').load(
        $form.data('url'),
        formElements
    );
});

/*
 Intercepts forms from the checkout page
 Adds quoteId as parameter
 */
$(function() {
    Quote.initialize();
});