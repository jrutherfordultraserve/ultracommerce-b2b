(function(TypeAhead, $, undefined) {

    // Private Properties

    var $typeAhead = $('#typeAhead');
    var $typeAheadKeyword = $typeAhead.find('.js-typeahead--keyword');
    var $typeAheadProduct = $typeAhead.find('.js-typeahead--product');
    var $typeAheadCategory = $typeAhead.find('.js-typeahead--category');
    var $typeAheadMfg = $typeAhead.find('.js-typeahead--mfg');

    // Public Properties

    TypeAhead.searchTimer = null;
    TypeAhead.TYPE_TIMEOUT = 200;
    TypeAhead.CONTRIBUTORS = ['categories', 'keywords', 'manufacturers', 'products'];

    // Public Methods

    /**
     * Initialize the TypeAhead section by hiding it until it is needed
     */
    TypeAhead.initialize = function() {
        $typeAhead.hide();
    };

    function getQueryHTML(originalQuery, suggestedQuery) {
        // for each term find the first occurrence of term in rest of suggest
        // add strong brackets, then recall method for the next term against the rest of the query

        return highlightTerms(originalQuery.split(" "), suggestedQuery);
    }

    function highlightTerms(terms, text) {
        var highlighted = recurseHighlightTerms(terms, text);
        while(highlighted.indexOf('***') != -1) {
            highlighted = highlighted.replace('***', '<strong>');
            highlighted = highlighted.replace('***', '</strong>');
        }

        return highlighted;
    }

    function recurseHighlightTerms(terms, text) {
        if (terms.length === 0) {
            return text;
        }

        var lowText = text.toLowerCase();

        var termToHighlight = terms[0].toLowerCase();

        var startIndex = lowText.indexOf(termToHighlight);
        var endIndex = startIndex + termToHighlight.length;

        if (startIndex === -1) {
            return highlightTerms(terms.slice(1), text);
        }

        var beforeText = text.substring(0, startIndex);
        var afterText = text.substring(endIndex);

        var highlightedTerm = '***' + text.substring(startIndex, endIndex) + '***';

        return highlightTerms(terms.slice(1), beforeText) + highlightedTerm + highlightTerms(terms.slice(1), afterText);
    }

    function renderItem(props) {
        return "<li class='ta-item " + props.className + "'><a href='" + props.url + "'>" + props.content + "</a></li>";
    }

    function renderProductContent(props) {
        var onSale = props.salePrice !== props.retailPrice;
        var oldPrice = "";
        if (onSale) {
            oldPrice = "<span class='price-old'>" + props.retailPrice+ "</span>";
        }

        return "<div class='col-sm-3 ta-image'><img src=" + props.mediaUrl + " /></div><div class='col-sm-9'><h5>" + props.suggestion + "</h5><div class='ta-price price text-right" + (onSale ? " on-sale" : "") + "'>" + oldPrice + "<span" + (onSale ? " class='price-new'" : "") + ">" + props.salePrice + "</span></div></div>";
    }

    function hasTypeAheadResults(data) {
        return TypeAhead.CONTRIBUTORS.some(function(contributorKey) {
           return !!data[contributorKey] && !!data[contributorKey].length;
        });
    }

    /**
     * Handle TypeAhead requests leveraging EnterpriseSearch functionality (BLCSearch)
     */
    TypeAhead.handleTypeAhead = function(query) {
        var options = {
            type: 'GET',
            url: '/type-ahead/search',
            traditional: true,
            data: {
                q: query,
                name: 'Demo Type Ahead',
                contributor: TypeAhead.CONTRIBUTORS
            },
        };

        BLC.ajax(options, function(data) {
            if (data !== null && data !== undefined && hasTypeAheadResults(data)) {
                $typeAhead.show();
                $typeAheadKeyword.html('');
                $typeAheadProduct.html('');
                $typeAheadCategory.html('');
                $typeAheadMfg.html('');

                $typeAhead.find('.dropdown').addClass('open');
                if (data.keywords) {
                    data.keywords.forEach(function (dto) {
                        var queryHtml = getQueryHTML(query, dto.suggestion);

                        var item = renderItem(
                            Object.assign({}, dto, {
                                content: queryHtml
                            }));

                        $typeAheadKeyword.append(item);
                    });
                }

                if (data.products) {
                    data.products.forEach(function (dto) {
                        var productItem = renderProductContent(dto);

                        var item = renderItem(
                            Object.assign({}, dto, {
                                className: 'card-product',
                                content: productItem
                            }));

                        $typeAheadProduct.append(item);
                    });
                }

                if (data.categories) {
                    data.categories.forEach(function (dto) {
                        var item = renderItem(
                            Object.assign({}, dto, {
                                content: dto.suggestion + " (" + dto.count + ")"
                            }));

                        $typeAheadCategory.append(item);
                    });
                }

                if (data.manufacturers) {
                    data.manufacturers.forEach(function (dto) {
                        var item = renderItem(
                            Object.assign({}, dto, {
                                content: dto.suggestion + " (" + dto.count + ")"
                            }));

                        $typeAheadMfg.append(item);
                    });
                }
            }
            else {
                $typeAhead.find('.dropdown').removeClass('open');
            }
        });
    };

})(window.TypeAhead = window.TypeAhead || {}, jQuery);