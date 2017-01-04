(function($) {
    var pluginName = "speeddial";

    var defaultOptions = {};

    function plugin(el, options) {
        var self = this;
        var $win = $(window);
        var $doc = $(document);

        var $sd = $(el);
        var $sdPrev = $sd.find(".prev");
        var $sdNext = $sd.find(".next");
        var $sdPages = $sd.find(".page");
        var $subCat = $("#sub-category");

        var sdIndex = 0;
        var sdTotal = $sdPages.length;

        var sdOffset;
        var winWidth;

        var isDragging = false;
        var $cat;
        var $placeholder;
        var startDragPageIndex;
        var startDragCatIndex;

        var CAT_BORDER = 3;
        var CAT_WIDTH = 300;
        var CAT_HEIGHT = 130;

        var ROWS = 4;
        var COLS = 3;
        var WIDTH = CAT_WIDTH * COLS;
        var HEIGHT = CAT_HEIGHT * ROWS;

        function getCSSPositionByIndex(index) {
            return {
                top: Math.floor(index / COLS) * CAT_HEIGHT,
                left: index % COLS * CAT_WIDTH
            };
        };

        function getIndexByPosition(left, top) {
            var col = Math.ceil((left - sdOffset.left) / CAT_WIDTH);
            var row = Math.ceil((top - sdOffset.top) / CAT_HEIGHT);

            if (row < 1 || row > ROWS) return false;
            if (col < 1 || col > COLS) return false;

            return (row - 1) * COLS + col;
        };

        function sdChange(next) {
            var targetIndex = next ? sdIndex + 1 : sdIndex - 1;

            if (targetIndex < 0 || targetIndex > sdTotal - 1) return false;

            $sdPages.eq(sdIndex).animate({
                "left": winWidth * (next ? -1 : 1)
            });

            sdIndex = targetIndex;

            $sdPages.eq(sdIndex).animate({
                "left": 0
            });

            $sdPrev.toggle(sdIndex != 0);
            $sdNext.toggle(sdIndex < sdTotal - 1);

            return true;
        };

        function handleMouseout() {
            var position = $placeholder.data("position");
            $cat.removeClass("dragging").css(getCSSPositionByIndex(position)).data("position", position);
            $placeholder.replaceWith($cat);

            if (startDragPageIndex != sdIndex) {
                $sdPages.each(function() {
                    var $cats = $(this).find(".cat");
                    $cats.slice(0, 9).each(function(index) {
                        $(this).css(getCSSPositionByIndex(index)).data("position", index);
                    });

                    if ($cats.length > 9) {
                        var $nextPage = $(this).next(".page");
                        if (!$nextPage.length) {
                            $nextPage = $("<div class='page' />").css("left", winWidth);
                            $sdPages.last().after($nextPage);
                            $sdPages = $sd.find(".page");
                            sdTotal++;
                            if (sdIndex + 1 == sdTotal - 1) $sdNext.show();
                        };
                        $cats.slice(9).each(function(index) {
                            $(this).css(getCSSPositionByIndex(index)).data("position", index);
                        });
                        $nextPage.append($cats.slice(9));
                    } else if (!$cats.length) {
                        $(this).remove();
                        sdTotal--;
                    };
                });
            };

            if (options.onChange && (startDragPageIndex != sdIndex || startDragCatIndex != $cat.data("position"))) {
                var data = [];
                $sdPages.each(function() {
                    var page = [];
                    $(this).find(".cat").each(function(index) {
                        page.push($(this).data("category"));
                    });
                    data.push(page);
                });
                options.onChange(data);
            };
        };

        $sdPages.each(function() {
            $(this).find(".cat").each(function(index) {
                $(this).css(getCSSPositionByIndex(index)).data("position", index);
            });
        });

        $sdPrev.click(function() {
            sdChange(0);
        });

        $sdNext.click(function() {
            sdChange(1);
        });

        $sdPages.find(".cat").click(function() {
            $(this).off(".cat");
            $subCat.find(".title").text($(this).find("span").text());
            $subCat.find(".bd").html($("#subcategory-" + $(this).data("category")).html());
            $subCat.show();
        }).mousedown(function() {
            if (isDragging) return false;

            $(this).one("mousemove.cat", function(event) {
                isDragging = true;
                $cat = $(this);
                $placeholder = $("<div class='cat placeholder' />").data("position", $cat.data("position"));

                startDragPageIndex = sdIndex;
                startDragCatIndex = $cat.data("position");

                var offset = $cat.offset();
                var mouse = {
                    left: event.pageX,
                    top: event.pageY,
                };
                var $page = $sdPages.eq(sdIndex);
                var overPrevNext = false;
                var lastEvent;
                var mousemoveTimer;
                var changePageTimer;

                function goPrevPage() {
                    if (sdChange(0)) $page = $sdPages.eq(sdIndex);
                    if (overPrevNext == -1) changePageTimer = setTimeout(goPrevPage, 1e3);
                };

                function goNextPage() {
                    if (sdChange(1)) $page = $sdPages.eq(sdIndex);
                    if (overPrevNext == 1) changePageTimer = setTimeout(goNextPage, 1e3);
                };

                function updateLayout() {
                    var idx = getIndexByPosition(lastEvent.pageX, lastEvent.pageY);
                    if (idx) {
                        clearTimeout(changePageTimer);
                        overPrevNext = false;

                        var $curr = $page.children().eq(idx - 1);
                        if ($curr.length) {
                            if (!$curr.is($placeholder)) {
                                $placeholder.data("position") > $curr.data("position") ? $curr.before($placeholder) : $curr.after($placeholder);
                                $page.find(".cat").each(function(index) {
                                    $(this).stop().animate(getCSSPositionByIndex(index)).data("position", index);
                                });
                            };
                        } else {
                            $page.append($placeholder);
                            $page.find(".cat").each(function(index) {
                                $(this).stop().animate(getCSSPositionByIndex(index)).data("position", index);
                            });
                        };
                    } else {
                        var delta = Math.abs(sdOffset.left - lastEvent.pageX);
                        if (sdOffset.left > lastEvent.pageX && delta < 50) {
                            overPrevNext = -1;
                            clearTimeout(changePageTimer);
                            changePageTimer = setTimeout(goPrevPage, 1e3);
                        } else if (sdOffset.left + WIDTH < lastEvent.pageX && delta - WIDTH < 50) {
                            overPrevNext = 1;
                            clearTimeout(changePageTimer);
                            changePageTimer = setTimeout(goNextPage, 1e3);
                        };
                    };
                };

                $doc.on("mousemove.cat", function(event) {
                    $cat.css({
                        top: offset.top + CAT_BORDER + event.pageY - mouse.top,
                        left: offset.left + CAT_BORDER + event.pageX - mouse.left
                    });

                    lastEvent = event;
                    clearTimeout(mousemoveTimer);
                    mousemoveTimer = setTimeout(updateLayout, 30);
                }).on("mouseup.cat", function() {
                    isDragging = false;
                    $doc.off(".cat");
                    setTimeout(handleMouseout, 50);
                });

                $cat.before($placeholder).addClass("dragging").css({
                    top: offset.top + CAT_BORDER,
                    left: offset.left + CAT_BORDER
                }).appendTo("body");

                return false;
            });
        }).on("dragstart", function() {
            return false;
        });

        $subCat.find(".bd").on("click", "a", function(event) {
            event.stopPropagation();
            var $ul = $(this).siblings("ul").toggle();
            if ($ul.is(":visible")) {
                $ul.css("margin-left", -$ul.outerWidth() / 2);
                $subCat.find(".types ul").not($ul).hide();
                $doc.one("click", function() {
                    $ul.hide();
                });
            };
            if ($ul.length) return false;
        });

        $("#sub-category .dimmer, #sub-category .close").click(function() {
            $("#sub-category").hide();
        });

        $win.resize(function() {
            $sd.css("margin-top", Math.max(10, ($win.height() - $sd.height()) / 2));
            sdOffset = $sd.offset();
            winWidth = $win.width();
        }).triggerHandler("resize");

        $sdPages.filter(":gt(0)").css("left", winWidth);
    };

    $.fn[pluginName] = function(options) {
        var isMethodCall = typeof options === "string";
        var args = Array.prototype.slice.call(arguments, 1);
        if (isMethodCall) {
            this.each(function() {
                var instance = $.data(this, pluginName);
                if (instance) instance[options].apply(instance, args);
            });
        } else {
            options = $.extend(defaultOptions, options);
            this.each(function() {
                var instance = $.data(this, pluginName);
                if (!instance) $.data(this, pluginName, new plugin(this, options));
            });
        };
        return this;
    };
})(jQuery);