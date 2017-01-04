$(function() {
    // 实时时钟
    var $navClock = $("#nav .clock");
    (function clockTick() {
        var now = new Date(),
            hours = now.getHours(),
            minutes = now.getMinutes(),
            seconds = now.getSeconds();
        $navClock.find(".cal").text(now.getDate()).end().find(".time span").text((hours >= 10 ? hours : "0" + hours) + ":" + (minutes >= 10 ? minutes : "0" + minutes) + ":" + (seconds >= 10 ? seconds : "0" + seconds));
        setTimeout(clockTick, 1e3);
    })();

    // 切换颜色
    var $redStyle = $("#style-red");
    $("#nav .change-style li").click(function() {
        $redStyle.prop("disabled", !$(this).hasClass("red"));
        $(this).hasClass("red") ? Cookies.set("style-red", true) : Cookies.remove("style-red");
    });
    if (Cookies.get("style-red")) $redStyle.prop("disabled", false);
});