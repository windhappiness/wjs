$(function() {
    var $win = $(window),
        $doc = $(document),
        $hb = $("html, body"),
        $body = $("body"),
        $main = $("#main");

    if ($body.attr("id") == "page-timeline") {
        var $timelineNav = $("#timeline-nav");
        var $timelineSectionsReversed = $($("#timeline").children().get().reverse());
        var timelineNavStickyPoint = $timelineNav.offset().top - 20;
        var winCenterY;
        var scrolling = false;

        $win.resize(function(){
        	winCenterY = $win.height() / 2;
        }).triggerHandler("resize");

        $win.scroll(function() {
        	if (scrolling) return false;
        	var scrollTop = $(window).scrollTop();
        	$timelineNav.toggleClass("fixed", scrollTop > timelineNavStickyPoint);

        	$timelineSectionsReversed.each(function(){
        		if ($(this).data("y") - scrollTop < winCenterY) {
        			$(this).data("nav").addClass("current").siblings(".current").removeClass("current");
                    return false;
                };
        	});
        });

        $timelineNav.find("a").click(function(){
        	var $link = $(this);
        	if ($link.parent().hasClass("current")) return false;
        	scrolling = true;
        	$hb.stop().animate({
        		scrollTop: $($link.attr("href")).offset().top - 20
        	}, function(){
        		scrolling = false;
        	});
        	$link.parent().addClass("current").siblings(".current").removeClass("current");
        	return false;
        });

        $timelineSectionsReversed.each(function(){
        	$(this).data("y", $(this).offset().top).data("nav", $timelineNav.find("[href='#"+this.id+"']").parent());
        });
    };

    $(".section-detail .more").click(function(){
        $(this).toggleClass("expand", $(".section-detail .info-more").toggle().is(":visible"));
        return false;
    });

    $(".timeline-filter span").click(function(){
        $(this).toggleClass("selected").siblings().removeClass("selected");
        var type = $(this).data("type");

        $("#timeline .event").hide();
        if (type == -1) {
            $("#timeline .event").show();
        } else {
            $("#timeline .event").filter("[data-type='"+type+"']").show();
        };
    });
});