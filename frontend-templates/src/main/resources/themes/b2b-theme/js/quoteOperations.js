/// Dependent on HeatClinic's cartOperations.js

(function(Quote, $, Cart, undefined) {

    /// Public Properties

    /**
     * Initialize the quote on page load
     */
    Quote.initialize = function() {
        if ($('#quote-checkout').length) {
            interceptQuoteCheckoutAjaxCalls();
            interceptQuoteCheckoutForms();
            disableQuoteCheckoutEditButtons();
        }

        if ($('input[name=quoteToLoad]').length) {
            var quoteToLoad = $('input[name=quoteToLoad]').val();
            $('a[data-quoteid=' + quoteToLoad + ']').click();
        }
    };

    /// Public Methods
    
    /**
     * Delegates to Cart.buildProductOptionsForRequest
     * @param {object} itemRequest
     * @param {element} $container
     */
    Quote.buildProductOptionsForRequest = function(itemRequest, $container) {
        Cart.buildProductOptionsForRequest(itemRequest, $container);
    };

    /**
     * Set quote specific parameters on the item request
     * @param {object} itemRequest
     * @param {element} $el
     */
    Quote.buildQuoteConfigForRequest = function (itemRequest, $el)  {
        if (itemRequest['quoteId'] == undefined) {
            itemRequest['quoteId'] = $el.data('value');
        }

        if (itemRequest['priceMethod'] == "ext") {
            //if pricing is given as a line total instead of each
            itemRequest['overrideRetailPrice'] /= itemRequest['quantity'];
            itemRequest['priceMethod'] = "each";
        }  
    };

    /**
     * Delegate to Cart.handleAddToCartError
     * @param {object} data
     * @param {object} $container
     */
    Quote.handleAddToQuoteError = function(data, $container) {
        Cart.handleAddToCartError(date, $container);
    };

    Quote.updateMiniQuotes = function(shouldOpen) {
        BLC.ajax({
            url: '/quote/mini',
            type: "GET"
        }, function(data) {
            $('.js-miniQuotes').replaceWith(data);

            if (shouldOpen) {
                $('.js-miniQuotes > a').trigger('click');
            }
        });
    };

    Quote.addToQuoteSuccess = function(data, itemRequest) {

        Quote.updateMiniQuotes(true);

        // Update the CSR header in the edge case where the CSR created the cart for the Customer
        if ($('body').hasClass('csr-mode')) {
            BLC.csr.refreshCsrSelector();
        }

        if ($('#quote-view').length) {
            $('#quote-view').load(
                data.quoteSummaryViewUrl,
                {
                    quoteId: itemRequest['quoteId'],
                    csrfToken: itemRequest['csrfToken']
                }
            );
        }

        if (data.newQuoteId) {
            $('select[name=quoteId]').append(
                $(
                    '<option>',
                    {
                        value: data.newQuoteId,
                        text: data.newQuoteName
                    }
                )
            )
        }

    };

    /**
     * Responsible for updating the quantity for an order item.  This method only submits the form and is not responsible for changing
     * quantity field.
     * @param {object} element
     * @returns {boolean}
     */
    Quote.updateItemQuantity = function(element) {
        var $button = $(element),
            $form = $button.closest('form'),
            formElements = BLC.serializeObject($form);

        $('#quote-view').load(
            $form.attr('action'),
            formElements
        );
        return false;
    };

    Quote.modifyUrlForQuoteCheckout = function(url) {
        return modifyURLForQuote(url);
    };

    /// Private functions
    function getItemIdFromData(data) {
        if (data.skuId != null) {
            return data.skuId;
        }
        return data.productId;
    }

    /*
     appends quoteId as url parameter
     */
    function modifyURLForQuote(url) {
        if (url) {
            var params = "";

            if (url.indexOf('quoteId') == -1) {
                params = (url.indexOf('?') < 0 ? '?' : '&') + $.param({quoteId: $('#quoteId').val()});
            }

            return url + params;
        }
    }

    function interceptQuoteCheckoutAjaxCalls() {
        $.ajaxSetup({
            beforeSend: function(jqXHR, settings) {
                settings.url = Quote.modifyUrlForQuoteCheckout(settings.url);
                console.log(settings.url);
            }
        });
    }

    function interceptQuoteCheckoutForms() {
        $('form').each(function() {
            var $form = $(this),
                formActionUrl = $form.attr('action');
            var url = modifyURLForQuote(formActionUrl);

            $form.attr('action', url);
        });
    }

    function disableQuoteCheckoutEditButtons() {
        $('.cart-item-summary-title a').hide();
    }

})(window.Quote = window.Quote || {}, jQuery, window.Cart = window.Cart || {});