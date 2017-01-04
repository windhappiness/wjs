$(function() {
    var $win = $(window),
        $doc = $(document),
        $body = $("body"),
        isWidescreen = true;

    $win.resize(function(event) {
        isWidescreen = $win.width() >= 1316;
        $body.toggleClass("widescreen", isWidescreen).toggleClass("no-widescreen", !isWidescreen);
    }).triggerHandler("resize");

    // 返回顶部
    var $backtop = $("#backtop");
    $win.scroll(function() {
        $backtop.toggle($win.scrollTop() > $win.height());
    });
    $backtop.click(function() {
        $body.animate({
            scrollTop: 0
        });
        return false;
    });

    // Tab 切换
    $("[data-tab]").click(function() {
        $(this).addClass("active").siblings().removeClass("active");
        $("#" + $(this).data("tab")).addClass("active").siblings().removeClass("active");
    });

    // 待办任务
    $(".list-action .action-toggle").click(function(event) {
        $(this).parent().toggleClass("action-show").siblings().removeClass("action-show");
    });
    $(".list tbody tr").click(function(event) {
        if (event.target.tagName == "INPUT") return;
        $(this).addClass("selected").siblings(".selected").removeClass("selected");
        $(".progress .steps").html($(".progress .steps").html());
    }).dblclick(function() {
        window.open("list-detail" + ($(this).data("id") || "") + ".html");
    });
    $(".list-action .action-item-check .dropdown-toggle").click(function() {
        var $inputs = $(".list .col-check input");
        var allchecked = true;
        $inputs.each(function() {
            if (!this.checked) {
                allchecked = false;
                return false;
            };
        });
        $inputs.prop("checked", !allchecked);
    });

    if ($("#page-detail").length) {
        var $main = $("#page-detail #main");
        $main.TOC().data("toc").hide();
        $win.scroll(function() {
            $main.data("toc").toggle($win.scrollTop() > $win.height());
        });
    };

    $("[data-popover]").click(function() {
        var $popover = $("#popover-" + $(this).data("popover"));
        $(".container > .popover").not($popover).trigger("close");
        $popover.trigger($popover.is(":hidden") ? "show" : "close");
        return false;
    });
    $(".container > .popover").on("show", function() {
        $(this).css({
            bottom: $win.height() - ($("[data-popover='" + $(this).attr("id").replace("popover-", "") + "']").offset().top - $win.scrollTop()) - 63,
            right: -400
        }).show().stop().animate({
            right: 90
        }, 400);
        $body.addClass("open-popover");
    }).on("close", function() {
        $(this).hide();
        $body.removeClass("open-popover");
    }).find(".close").click(function() {
        $(this).parent().trigger("close");
        return false;
    });

    $("[data-action='checkall']").click(function() {
        $(this).parent().parent().find(":checkbox").prop("checked", true);
    });
    $("[data-action='uncheckall']").click(function() {
        $(this).parent().parent().find(":checkbox").prop("checked", false);
    });
    $("[data-action='reverseall']").click(function() {
        $(this).parent().parent().find(":checkbox").prop("checked", function() {
            return !this.checked;
        });
    });

    // 新办任务
    $("#page-new .category-list div").click(function() {
        $(this).parent().addClass("selected").siblings(".selected").removeClass("selected");
        $(".category-detail").show().find(".category-name").text($(this).text());
    });

    // 上传文件
    $(".section-upload .select").click(function() {
        $(this).parent().find("input").trigger("click");
    });
    $(".section-upload input").change(function onSelectFile() {
        var fullPath = this.value;
        if (fullPath) {
            var startIndex = (fullPath.indexOf('\\') >= 0 ? fullPath.lastIndexOf('\\') : fullPath.lastIndexOf('/'));
            var filename = fullPath.substring(startIndex);
            if (filename.indexOf('\\') === 0 || filename.indexOf('/') === 0) {
                filename = filename.substring(1);
            };
            $(this).closest("li").addClass("done").find(".filename").text(filename);
        };
    });
    $(".section-upload .del").click(function() {
        $(this).closest("li").removeClass("done").find("input").val("");
    });

    $(".section-upload .icon").click(function() {
        if (!$(this).closest("li").hasClass("done")) $(this).closest("li").remove();
    });

    /*
    $(".table123").on("selectstart", function(event){
        event.preventDefault();
        return false;
    });
    */
});