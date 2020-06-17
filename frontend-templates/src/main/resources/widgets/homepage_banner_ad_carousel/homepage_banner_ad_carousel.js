$(function() {
    var indicators = $(".carousel-indicators li");
    for (i = 0; i < indicators.length; i++) {
        var localListItem = indicators[i];
        $(localListItem).attr('data-slide-to', i);
    };
});