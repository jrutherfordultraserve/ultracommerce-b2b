$(document).ready(function() {

    var $body = $('body');
    var $quickOrderField = $('.js-quickOrderField');
    var $allQuickOrderSuggestions = $('.js-quickOrderSuggestions');
    var $quickOrderForm = $('.js-quickOrderForm');
    var $allQuickOrderFormRows = $('.js-quickOrderFormRow');
    var $quickOrderPasteForm = $('.js-quickOrderPasteForm');
    var $quickOrderPasteArea = $('.js-quickOrderPasteArea');
    var $quickQuoteFormButton = $('.js-quickQuoteFormButton');
    var $quickQuotePasteButton = $('.js-quickQuotePasteButton');

// =========== ACTION HANDLERS ================ //

    $body.on('input', '.js-quickOrderField', function() {
        var $field = $(this);
        var query = $field.val().trim();
        var $quickOrderRow = $field.closest('.js-quickOrderFormRow');
        var $quickOrderSuggest = $quickOrderRow.find('.js-quickOrderSuggestions');
        var $quickOrderSuggestList = $quickOrderSuggest.find('.js-quickOrderSuggestionsList');

        // Clear timer because a new key has been pressed
        clearTimeout(QuickOrder.searchTimer);

        // Set a new timer and get results if they haven't typed in certain amount of time
        QuickOrder.searchTimer = setTimeout(function() {

            if (query.length <= 1) {
                $quickOrderSuggest.hide();
                return;
            }

            QuickOrder.handleQuickOrderSuggest(query, $quickOrderSuggest, $quickOrderSuggestList);
        }, QuickOrder.TYPE_TIMEOUT);
    });

    $body.on('click', '.js-quickOrderItemSuggestion > .js-quickOrderItemLink', function(e) {
        e.preventDefault();
        var $link = $(this);
        var productId = $link.data('id');

        var $quickOrderSuggest = $link.closest('.js-quickOrderSuggestions');
        var $quickOrderRow = $quickOrderSuggest.closest('.js-quickOrderFormRow');

        // check for hidden input named `expandProductOptions` to verify if we should select multi or single
        var $expandProductOptions = $quickOrderForm.find('input[name="expandProductOptions"]');
        var shouldExpandProductOptions = $expandProductOptions.val() === "true";

        if (!shouldExpandProductOptions) {
            QuickOrder.handleQuickOrderSelect(productId, $quickOrderRow, $quickOrderSuggest);
        } else {
            QuickOrder.handleQuickOrderSelectMulti(productId, $quickOrderRow, $quickOrderSuggest);
        }
    });

    $body.on('click', '.js-quickOrderUndo', function(e) {
        var $quickOrderRow = $(this).closest('.js-quickOrderFormRow');

        QuickOrder.handleQuickOrderUndoSelect($quickOrderRow);
    });

// =========== END ACTION HANDLERS ================ //

// =========== MOVEMENT HANDLERS ================ //

    /**
     * Handle various keydown events for the QuickOrder functionality
     */
    $quickOrderField.keydown(function(e) {
        var $field = $(this);
        var $quickOrderRow = $field.closest('.js-quickOrderFormRow');
        var $quickOrderSuggest = $quickOrderRow.find('.js-quickOrderSuggestions');

        // Handle backspace press
        if (e.which === 8) {
            clearTimeout(QuickOrder.searchTimer);
        }

        var query = $field.val().trim();
        if (query.length <= 1) {
            $quickOrderSuggest.hide();
            return;
        }

        var $current = $quickOrderSuggest.find('li.qo-item.is-focused');
        if($current.length === 0 &&
            ($quickOrderSuggest.find('li.qo-item.js-isFocused').length > 0
            || $quickOrderSuggest.find('li.qo-item:hover').length > 0))
        {
            //Do nothing while hovering over menu
            return;
        }

        // Handle down arrow press by highlighting the next item in the dropdown
        if (e.which === 40) {
            e.preventDefault();
            if ($quickOrderSuggest.css('display') === 'none') {
                QuickOrder.handleQuickOrderSuggest(query);
            }

            if ($current.length === 0) {
                var first = $quickOrderSuggest.find('li.qo-item').first();

                first.addClass("is-focused");
            } else {
                $current.removeClass("is-focused");

                var next = getNext($current, $quickOrderSuggest);

                next.addClass("is-focused");
                next.focus();
            }
        }

        // Handle down arrow press by highlighting the previous item in the dropdown
        if (e.which === 38) {
            e.preventDefault();
            if ($quickOrderSuggest.css('display') === 'none') {
                QuickOrder.handleQuickOrderSuggest(query);
            }

            if ($current.length === 0) {
                var last = $quickOrderSuggest.find('li.qo-item').last();

                last.addClass("is-focused");
            } else {
                $current.removeClass("is-focused");

                var prev = getPrev($current, $quickOrderSuggest);

                prev.addClass("is-focused");
                prev.focus();
            }
        }

        // Handle enter press by selecting the highlighted dropdown item
        if (e.which === 13) {
            if ($current.length !== 0) {
                e.preventDefault();
                $current.find('a')[0].click();
            } else {
                e.preventDefault();
                $quickOrderSuggest.find('li.qo-item').first().find('a')[0].click();
            }
        }
    });


    function getNext($current, $quickOrderSuggest) {
        var $items = $quickOrderSuggest.find('li.qo-item');
        var currentIndex = $items.index($current);
        return $($items[currentIndex + 1]);
    }

    function getPrev($current, $quickOrderSuggest) {
        var $items = $quickOrderSuggest.find('li.qo-item');
        var currentIndex = $items.index($current);
        return $($items[currentIndex - 1]);
    }

    /**
     * Hides the currently focused list item when the user hovers over menu, and restores after
     */
    $allQuickOrderSuggestions.hover(function() {
        $(this).find('.is-focused').addClass('js-isFocused').removeClass('is-focused');
    }, function() {
        $(this).find('.js-isFocused').removeClass('js-isFocused').addClass('is-focused');
    });

    /**
     * Hide the typeahead dropdown when we unfocus from the input, e.g. click on the page, focus the input when we click on the typeahead.
     */
    $quickOrderField.on('focus', function(e) {
        addQuickOrderBlurClose($(this));
    });

    $allQuickOrderSuggestions.on('mousedown', function() {
        removeQuickOrderBlurClose($(this));
    });

    function addQuickOrderBlurClose($field) {
        $field.off('blur');
        $field.on('blur', function(e) {
            e.preventDefault();
            $field.closest('.js-quickOrderFormRow').find('.js-quickOrderSuggestions').removeClass('open');
        });
    }

    function removeQuickOrderBlurClose($dropdown) {
        var $field = $dropdown.closest('.js-quickOrderFormRow').find('.js-quickOrderField');
        $field.off('blur');
        $field.on('blur', function(e) {
            e.stopImmediatePropagation();
            e.preventDefault();
            $field.focus();
        });
    }

// =========== END MOVEMENT HANDLERS ================ //

    $quickOrderForm.on('submit', function(e) {
        e.preventDefault();

        var $quickOrderRows = $quickOrderForm.find('.js-quickOrderFormRow');

        if ($quickOrderRows.length) {

            var quickOrderItems = $quickOrderRows.filter(function() {
                var $row = $(this);
                var quantity = +$row.find('[name="itemQuantity"]').val();
                return !!quantity; // filter out zero quantity entries
            }).map(function() {
                var $row = $(this);
                var quantity = +$row.find('[name="itemQuantity"]').val();
                var skuId = +$row.find('[name="skuId"]').val();
                var productId = +$row.find('[name="productId"]').val();
                var itemCode = $row.find('[name="itemCode"]').val();

                var quickOrderItem = {
                    skuId : skuId,
                    productId : productId,
                    quantity : quantity,
                    itemCode : itemCode,
                };

                QuickOrder.buildProductOptionsForRequest(quickOrderItem, $row);

                return quickOrderItem
            }).get();

            $.ajax({
                url : '/quick-order?csrfToken=' + $quickOrderForm.find('[name="csrfToken"]').val(),
                type : 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data : JSON.stringify(quickOrderItems),
                success : function(data) {
                    if (data.error) {
                        // handle error
                        Cart.handleAddToCartError(data, $quickOrderForm);
                    } else {
                        // handle success
                        Cart.updateMiniCart(true);

                        // Update the CSR header in the edge case where the CSR created the cart for the Customer
                        if ($('body').hasClass('csr-mode')) {
                            BLC.csr.refreshCsrSelector();
                        }

                        // first we trim the extra entries beyond the initialFormRowCount
                        trimToInitialRowCount();

                        // clear fields for next quick order
                        $quickOrderRows.each(function() {
                            $(this).find('[name="itemCode"]').val(undefined);
                            $(this).find('.js-quickOrderQuantityField').prop('disabled', false).val(undefined).change();
                            $(this).find('.js-quickOrderItem').html("");
                            $(this).find('.js-quickOrderField').val(undefined).change().closest('.form-group').show();
                        });
                    }
                }
            });
        }

        return false;
    });

    $quickQuoteFormButton.on('click', function (e) {
        e.preventDefault();

        var $quickOrderRows = $quickOrderForm.find('.js-quickOrderFormRow');

        if ($quickOrderRows.length) {

            var quickOrderItems = $quickOrderRows.filter(function() {
                var $row = $(this);
                var quantity = +$row.find('[name="itemQuantity"]').val();
                return !!quantity; // filter out zero quantity entries
            }).map(function() {
                var $row = $(this);
                var quantity = +$row.find('[name="itemQuantity"]').val();
                var skuId = +$row.find('[name="skuId"]').val();
                var productId = +$row.find('[name="productId"]').val();
                var itemCode = $row.find('[name="itemCode"]').val();

                var quickOrderItem = {
                    skuId : skuId,
                    productId : productId,
                    quantity : quantity,
                    itemCode : itemCode,
                };

                QuickOrder.buildProductOptionsForRequest(quickOrderItem, $row);

                return quickOrderItem
            }).get();

            $.ajax({
                url : '/quick-quote?csrfToken=' + $quickOrderForm.find('[name="csrfToken"]').val(),
                type : 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data : JSON.stringify(quickOrderItems),
                success : function(data) {
                    if (data.error) {
                        // handle error
                        Quote.handleAddToQuoteError(data, $quickOrderForm);
                    } else {
                        // handle success
                        Quote.updateMiniQuotes(true);

                        // Update the CSR header in the edge case where the CSR created the quote for the Customer
                        if ($('body').hasClass('csr-mode')) {
                            BLC.csr.refreshCsrSelector();
                        }

                        // first we trim the extra entries beyond the initialFormRowCount
                        trimToInitialRowCount();

                        // clear fields for next quick order
                        $quickOrderRows.each(function() {
                            $(this).find('[name="itemCode"]').val(undefined);
                            $(this).find('.js-quickOrderQuantityField').prop('disabled', false).val(undefined).change();
                            $(this).find('.js-quickOrderItem').html("");
                            $(this).find('.js-quickOrderField').val(undefined).change().closest('.form-group').show();
                        });
                    }
                }
            });
        }

        return false;
    });

    function trimToInitialRowCount() {
        var initialFormRowCount = +$quickOrderForm.find('input[name="initialFormRowCount"]').val();
        $quickOrderForm.find('.js-quickOrderFormRow:gt(' + ( initialFormRowCount - 1 ) + ')').remove();
    }

    $quickOrderPasteForm.on('submit', function(e) {
        e.preventDefault();

        var pasteText = $quickOrderPasteArea.val();
        var quickOrderPasteForm = {
            quickOrderPasteText : pasteText
        };

        $.ajax({
            url : '/quick-order-paste?csrfToken=' + $quickOrderForm.find('[name="csrfToken"]').val(),
            type : 'POST',
            dataType : 'json',
            contentType: 'application/json',
            data : JSON.stringify(quickOrderPasteForm),
            success: function(data) {
                if (data.error) {
                    // handle error
                    Cart.handleAddToCartError(data, $quickOrderForm);
                } else {
                    // handle success
                    Cart.updateMiniCart(true);

                    // Update the CSR header in the edge case where the CSR created the cart for the Customer
                    if ($('body').hasClass('csr-mode')) {
                        BLC.csr.refreshCsrSelector();
                    }

                    // clear fields for next quick order
                    $quickOrderPasteArea.val("");
                }
            }
        });
    });

    $quickQuotePasteButton.on('click', function(e) {
        e.preventDefault();

        var pasteText = $quickOrderPasteArea.val();
        var quickOrderPasteForm = {
            quickOrderPasteText : pasteText
        };

        $.ajax({
            url : '/quick-quote-paste?csrfToken=' + $quickOrderForm.find('[name="csrfToken"]').val(),
            type : 'POST',
            dataType : 'json',
            contentType: 'application/json',
            data : JSON.stringify(quickOrderPasteForm),
            success: function(data) {
                if (data.error) {
                    // handle error
                    Quote.handleAddToQuoteError(data, $quickOrderForm);
                } else {
                    // handle success
                    Quote.updateMiniQuotes(true);

                    // Update the CSR header in the edge case where the CSR created the cart for the Customer
                    if ($('body').hasClass('csr-mode')) {
                        BLC.csr.refreshCsrSelector();
                    }

                    // clear fields for next quick order
                    $quickOrderPasteArea.val("");
                }
            }
        });
    });
});
