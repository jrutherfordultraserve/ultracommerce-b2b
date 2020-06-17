$(document).ready(function() {
    var $searchBox = $('.js-search');
    var $typeAhead = $('#typeAhead');

    /**
     * Handle TypeAhead requests once the TypeAhead.TYPE_TIMEOUT time has been reached to improve user interaction
     */
    $searchBox.on('input', function() {
        // Clear timer because a new key has been pressed
        clearTimeout(TypeAhead.searchTimer);

        // Set a new timer and get results if they haven't typed in certain amount of time
        TypeAhead.searchTimer = setTimeout(function() {
            var query = $searchBox.val().trim();
            if (query.length <= 1) {
                $typeAhead.hide();
                return;
            }


            TypeAhead.handleTypeAhead(query);
        }, TypeAhead.TYPE_TIMEOUT);
    });

    /**
     * Handle various keydown events for the TypeAhead functionality
     */
    $searchBox.keydown(function(e) {
        // Handle backspace press
        if (e.which === 8) {
            clearTimeout(TypeAhead.searchTimer);
        }

        var query = $searchBox.val().trim();
        if (query.length <= 1) {
            $typeAhead.hide();
            return;
        }

        var current = $typeAhead.find('li.ta-item.is-focused');
        if(current.length === 0 &&
            ($typeAhead.find('li.ta-item.js-isFocused').length > 0
            || $typeAhead.find('li.ta-item:hover').length > 0))
        {
            //Do nothing while hovering over menu
            return;
        }

        // Handle down arrow press by highlighting the next item in the dropdown
        if (e.which === 40) {
            e.preventDefault();
            if ($typeAhead.css('display') === 'none') {
                TypeAhead.handleTypeAhead(query);
            }

            if (current.length === 0) {
                var first = $typeAhead.find('li.ta-item').first();

                first.addClass("is-focused");
            } else {
                current.removeClass("is-focused");

                var next = getNext(current);

                next.addClass("is-focused");
                next.focus();
            }
        }

        // Handle down arrow press by highlighting the previous item in the dropdown
        if (e.which === 38) {
            e.preventDefault();
            if ($typeAhead.css('display') === 'none') {
                TypeAhead.handleTypeAhead(query);
            }

            if (current.length === 0) {
                var last = $typeAhead.find('li.ta-item').last();

                last.addClass("is-focused");
            } else {
                current.removeClass("is-focused");

                var prev = getPrev(current);

                prev.addClass("is-focused");
                prev.focus();
            }
        }

        // handle left arrow press by highlighting the item to the left in the next column over
        if (e.which == 37) {
            if (current.length !== 0) {
                e.preventDefault();
                current.removeClass("is-focused");

                var left = getLeft(current);

                left.addClass("is-focused");
                left.focus();
            }
        }

        // handle right arrow press by highlighting the item to the right in the next column over
        if (e.which == 39) {
            if (current.length !== 0) {
                e.preventDefault();
                current.removeClass("is-focused");

                var right = getRight(current);

                right.addClass("is-focused");
                right.focus();
            }
        }

        // Handle enter press by selecting the highlighted dropdown item
        if (e.which === 13) {
            if (current.length !== 0) {
                e.preventDefault();
                current.find('a')[0].click();
            } else {
                e.preventDefault();
                var $searchForm = $(this).closest('form');
                window.location.href = $searchForm.attr('action') + '?' + $searchForm.serialize();
            }
        }
    });

    function getNext($current) {
        var $items = $typeAhead.find('li.ta-item');
        var currentIndex = $items.index($current);
        return $($items[currentIndex + 1]);
    }

    function getPrev($current) {
        var $items = $typeAhead.find('li.ta-item');
        var currentIndex = $items.index($current);
        return $($items[currentIndex - 1]);
    }

    function getLeft($current) {
        var $column = $current.closest('.ta-column');
        var $prevColumn = $column.prev().length ? $column.prev() : $typeAhead.find('.ta-column').last();
        return $prevColumn.find('li.ta-item').first();
    }

    function getRight($current) {
        var $column = $current.closest('.ta-column');
        var $nextColumn = $column.next().length ? $column.next() : $typeAhead.find('.ta-column').first();
        return $nextColumn.find('li.ta-item').first();
    }

    /**
     * Hides the currently focused list item when the user hovers over menu, and restores after
     */
    $typeAhead.hover(function() {
        $typeAhead.find('.is-focused').addClass('js-isFocused').removeClass('is-focused');
    }, function() {
        $typeAhead.find('.js-isFocused').removeClass('js-isFocused').addClass('is-focused');
    });

    /**
     * Hide the typeahead dropdown when we unfocus from the input, e.g. click on the page, focus the input when we click on the typeahead.
     */
    $searchBox.on('focus', function(e) {
        addTypeAheadBlurClose();
    });

    $typeAhead.on('mousedown', function() {
        removeTypeAheadBlurClose();
    });

    function addTypeAheadBlurClose() {
        $searchBox.off('blur');
        $searchBox.on('blur', function(e) {
            e.preventDefault();
            $typeAhead.find('.dropdown').removeClass('open');
        });
    }

    function removeTypeAheadBlurClose() {
        $searchBox.off('blur');
        $searchBox.on('blur', function(e) {
            e.stopImmediatePropagation();
            e.preventDefault();
            $searchBox.focus();
        });
    }

    $(function() {
        TypeAhead.initialize();
    });
});

/**
 * Do nothing when empty search submitted. This is not in document ready, otherwise the user can
 * do an empty search faster than the page is ready.
 */
$('.js-searchForm').submit(function(e) {
    if($('.js-search').val().trim().length === 0) {
        e.preventDefault();
        return false;
    } else {
        return true;
    }
});