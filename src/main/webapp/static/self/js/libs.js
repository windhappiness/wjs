$.fn.Tab = function(selectedIdx) {
    selectedIdx = selectedIdx || 0;
    return this.each(function() {
        var $tabs = $(this).find(".tabbar").children();
        var $panes = $(this).find(".tab-content").children();
        var $selectedTab = $tabs.eq(selectedIdx).addClass("selected");
        var $selectedPane = $panes.eq(selectedIdx).addClass("selected");

        $tabs.each(function(idx) {
            $(this).click(function() {
                if ($(this).hasClass("selected")) return;
                $selectedTab.removeClass("selected");
                $selectedPane.removeClass("selected");
                $selectedTab = $tabs.eq(idx).addClass("selected");
                $selectedPane = $panes.eq(idx).addClass("selected");
            });
        });
    });
};

$.fn.TOC = function() {
    return this.each(function() {
        var scrolling = false;
        var idPrefix = "toc-" + $.fn.TOC.id + "-";
        var $content = $(this);
        var $hb = $("html,body");
        var $win = $(window);
        var $sections = $content.find(".section");
        var $sectionsReversed = $($sections.get().reverse());
        var $toc = $("<ul class='toc' />");

        var tocHTML = $sections.map(function(idx) {
            var id = idPrefix + idx;
            var title = $(this).attr("id", id).find(".section-title").text();
            return '<li><a href="#' + id + '">' + title + '</a></li>';
        }).get().join("");

        $toc.html(tocHTML).insertBefore($content);

        var $tocLinks = $toc.find("a");

        $tocLinks.click(function() {
            var scrollTop = $($(this).attr("href")).offset().top;
            scrolling = true;
            $(this).parent().addClass("active").siblings(".active").removeClass("active");
            $hb.stop().animate({
                scrollTop: scrollTop
            }, 400, function() {
                scrolling = false;
            });
            return false;
        });

        $win.scroll(function() {
            if (scrolling) return;
            var sTop = $win.scrollTop();
            $sectionsReversed.each(function() {
                if ($(this).offset().top - sTop < 250) {
                    $tocLinks.filter("[href='#" + this.id + "']").parent().addClass("active").siblings(".active").removeClass("active");
                    return false;
                };
            });
        });

        $tocLinks.eq(0).parent().addClass("active");

        $.fn.TOC.id++;

        $(this).data("toc", $toc);
    });
};
$.fn.TOC.id = 0;