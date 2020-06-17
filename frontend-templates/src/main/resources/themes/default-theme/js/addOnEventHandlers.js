var $body = $('body');

$(function() {
    /**
     * Intercepts a request to 'Configure Items' in a bundle when invoked from the cart page.
     */
    $body.on('click', '.js-update-child-items', function () {
        // close the current modal
        $.modal.close();

        var $container = $(this).closest('tr');
        var orderItemId = $container.attr('id');

        // clean the order item to be just the id
        orderItemId = orderItemId.replace("productRow", "");

        BLC.ajax({
            url: '/cart/reconfigure?orderItemId=' + orderItemId,
            type: "GET"
        }, function (data, extraData) {
            var getRequests = [];
            $(data).find('.js-configure-product-choices').each(function (i, el) {
                getRequests.push(AddOn.getProductOptionsForChooseOne($(el)));
            });

            $.when.apply(null, getRequests).done(function () {
                $(data).find('.configure-product-options').each(function (i, el) {
                    var $container = $(el).closest('.js-configureRow');
                    HC.setExistingProductOptions($container);
                });
                // show the modal after all options are set
                $.modal(data, AddOn.modalProductAddOnReconfigureOptions);
            });
        });

        return false;
    });

    /**
     * When a 'Choose One' product add-on type is selected from the dropdown, update the product's image and look for
     * any product options associated.
     */
    $body.on('change', '.js-configure-product-choices', function () {
        AddOn.getProductOptionsForChooseOne($(this));
        return false;
    });

    /**
     * Trigger a 'change' event on page load for any 'Choose One' selector to populate the correct product options, etc.
     */
    $(document).ready(function () {
        $('.selectpicker.js-configure-product-choices').trigger('change');
    });
});