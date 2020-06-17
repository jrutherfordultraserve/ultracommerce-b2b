(function(QuickOrder, $, undefined) {
    var $clonedQuickOrderRow = undefined;

    $(document).ready(function() {
        $clonedQuickOrderRow = $('.js-quickOrderFormRow').last().clone(true, true);
    });

    // Public Properties

    QuickOrder.searchTimer = null;
    QuickOrder.TYPE_TIMEOUT = 200;

    function renderItem(props) {
        return "<li class='js-quickOrderItemSuggestion qo-item" + props.className +"'><a href='#' class='js-quickOrderItemLink' data-id='" + props.id + "' >" + props.content + "</a></li>";
    }

    function renderProductContent(props) {
        var onSale = props.salePrice !== props.retailPrice;
        var extraClass = (onSale) ? 'on-sale' : '';
        var priceNewClass = (onSale) ? 'price-new' : '';

        return "<img src='" + props.mediaUrl + "' /><div class='qo-details'><h5>" + props.name + "</h5><span class='qo-price price " + extraClass + "'><span class='" + priceNewClass + "'>" + props.salePrice + "</span></span><span class='qo-number'>Item Code: " + props.itemCode + "</span><span class='qo-number'>UPC: " + props.upc + "</span></div>";
    }

    /**
     * Delegates to Cart.buildProductOptionsForRequest
     * @param {object} itemRequest
     * @param {element} $container
     */
    QuickOrder.buildProductOptionsForRequest = function(itemRequest, $container) {
        Cart.buildProductOptionsForRequest(itemRequest, $container);

        Object.keys(itemRequest).forEach(function(key) {
            if (key.startsWith("itemAttributes[")) {
                if (!itemRequest.itemAttributes) {
                    itemRequest.itemAttributes = {};
                }

                var attrKey = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
                itemRequest.itemAttributes[attrKey] = itemRequest[key];
                delete itemRequest[key]
            }
        });
    };


    /**
     * Handle QuickOrder requests leveraging Account functionality (BLCQuickOrder)
     */
    QuickOrder.handleQuickOrderSuggest = function(query, $quickOrderSuggest, $quickOrderSuggestList) {
        var options = {
            type: 'GET',
            url: '/type-ahead/search',
            traditional: true,
            data: {
                q: query,
                name: 'Quick Order Suggest',
                contributor: 'quick_order_products',
                'contributor.quick_order_products.limit': 6
            },
        };

        BLC.ajax(options, function(data) {
            if (data.quick_order_products) {
                $quickOrderSuggest.show();
                $quickOrderSuggest.addClass('open');
                $quickOrderSuggestList.html('');
                data.quick_order_products.forEach(function(dto) {
                    var productItem = renderProductContent(dto);

                    var item = renderItem(
                        Object.assign({}, dto, {
                            className: 'card-product',
                            content: productItem
                        }));

                    $quickOrderSuggestList.append(item);
                });
            } else {
                $quickOrderSuggest.removeClass('open');
            }
        });
    };

    QuickOrder.handleQuickOrderSelect = function(productId, $quickOrderRow, $quickOrderSuggest) {
        var options = {
            type: 'GET',
            url: "/quick-order-select/" + productId,
            dataType: 'html',
        };

        BLC.ajax(options, function($response) {
            $quickOrderSuggest.hide();
            renderSelectionForRow($quickOrderRow, $response);

            expandQuickOrderRowsIfNeeded($quickOrderRow);
        });
    };

    QuickOrder.handleQuickOrderSelectMulti = function(productId, $quickOrderRow, $quickOrderSuggest) {
        var options = {
            type: 'GET',
            url: "/quick-order-select-multi/" + productId,
            dataType: 'html',
        };

        BLC.ajax(options, function($response) {
            $quickOrderSuggest.hide();

            renderSelectionForRow($quickOrderRow, $response.get(0));

            for (var i = $response.length - 1; i > 0; i--) {
                var $clonedRow = $clonedQuickOrderRow.clone(true, true);
                renderSelectionForRow($clonedRow, $response.get(i));
                $clonedRow.insertAfter($quickOrderRow);
            }

            expandQuickOrderRowsIfNeeded($quickOrderRow);
        });
    };

    QuickOrder.handleQuickOrderUndoSelect = function($quickOrderRow) {
        $quickOrderRow.replaceWith($clonedQuickOrderRow.clone(true, true));
    };

    function renderSelectionForRow($quickOrderRow, $response) {
        $quickOrderRow.find('.js-quickOrderField').closest('.form-group').hide();
        $quickOrderRow.find('.js-quickOrderItem').append($response);

        var $quickOrderQuantityField = $quickOrderRow.find('.js-quickOrderQuantityField');
        $quickOrderQuantityField.val(1);
        $quickOrderQuantityField.change();
        $quickOrderQuantityField.focus();
        $quickOrderQuantityField.closest('.form-group').removeClass('is-empty');

        var $productOptionGroup = $quickOrderRow.find('.js-productOptionGroup');
        if ($productOptionGroup.length) {
            $productOptionGroup.find('input,textarea,select,a').first().focus();
            $productOptionGroup.find('.selectpicker').selectpicker();
        }
    }

    function expandQuickOrderRowsIfNeeded($current) {
        var $next = $current.next('.js-quickOrderFormRow');
        var $last = $('.js-quickOrderFormRow').last();
        if ($last[0] == $current[0] || !$next.length) {
            $last.after($clonedQuickOrderRow.clone(true, true));
        }
    }
})(window.QuickOrder = window.QuickOrder || {}, jQuery);