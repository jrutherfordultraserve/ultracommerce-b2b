(function(AddOn, $, undefined) {

    /// Public Properties

    /// Public Methods

    /**
     * This performs an ajax call to retrieve product options for a given add-on
     * @param {element} $selector - the add-on to lookup
     * @returns {promise}
     */
    AddOn.getProductOptionsForChooseOne = function($selector) {
        var productId = $selector.val();
        var $configureRow = $selector.closest('.js-configureRow');
        var addOnId = $configureRow.find('input[name*=addOnXrefId]').val();
        if (productId === null) {
            return false;
        }

        var def = $.Deferred();

        BLC.get({
            url: '/addon/details/' + addOnId + '/' + productId,
            dataType: "json"
        }, function (data) {
            var $row = $selector.closest('.row');
            var $productImage = $row.find('img');
            $productImage.attr('src', data.productImage.url);

            var $productPrice = $configureRow.find('.js-itemPrice');
            var $productPriceSpan = $productPrice.find('span.item-price-span');
            if ($productPrice.data('baseprice') !== '0.00') {
                $productPriceSpan.html(data.productPrice);
            }

            checkForOutOfStock(productId, $configureRow);

            if (data.hasProductOptions) {
                var orderItemIndex = $selector.closest('.js-itemWrapper').data('item-index');
                var url = '/cart/product/options/' + productId + '?orderItemIndex=' + orderItemIndex + '&addOnId=' + addOnId;
                BLC.get({
                    url: url
                }, function (data) {
                    var $container = $selector.parent();
                    $container.nextAll().remove();

                    // Activate bootstrap-select
                    if($(data).find('.selectpicker').length !== 0){
                        var $picker = $(data).find('.selectpicker').selectpicker({ container: 'body'});
                        var $menu = $picker.siblings('.dropdown-menu').css('display', 'none');
                        $picker.siblings('.dropdown-toggle').on('click', function() {
                            $menu.css('display', 'inherit');
                        });
                    }

                    $container.after($(data));
                    HC.setExistingProductOptions($container.closest('.js-configureRow'));
                    def.resolve();
                });
            } else {
                def.resolve();
            }
        });

        return def.promise();
    };

    /// Private Methods

    /**
     * Checks whether or not a given product is out of stock
     * @param {number} productId
     * @param {element} $configureRow
     */
    function checkForOutOfStock(productId, $configureRow) {
        var $quantityInput = $configureRow.find('.js-quantityInput');

        if (params.outOfStockProducts.indexOf(parseInt(productId)) != -1) {
            $configureRow.addClass('out-of-stock');
            $configureRow.find('.js-outOfStock').removeClass('is-hidden');

            $quantityInput.val(0);
            $quantityInput.parent().hide();
        } else {
            $configureRow.removeClass('out-of-stock');
            $configureRow.find('.js-outOfStock').addClass('is-hidden');

            $quantityInput.val($quantityInput.data('orig-min'));
            $quantityInput.parent().show();
        }
    }

})(window.AddOn = window.AddOn || {}, jQuery);