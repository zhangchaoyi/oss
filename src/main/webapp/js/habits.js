$("#btn-explain-up").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) + 144;
        if(value>=0){
            $("#btn-explain-up").addClass("disabled");
        }
        if($("#btn-explain-down").hasClass("disabled")){ 
            $("#btn-explain-down ").removeClass("disabled");
        }
        return value;
    });       
});

$("#btn-explain-down").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) - 144;
        if(value <= -288){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});

//player-tag
$("div.nav-tab.habits > ul.nav.nav-pills > li").click(function(){
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
});
//detail-tag
$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills > li").click(function(){
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
});
$("ul.nav.nav-tabs.game-details > li").click(function(){
	var info = $(this).children("a").attr("data-info");
	switch(info){
		case "frequency":
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").text("");
		var str = "<li class='active'><a><span data-info='day-times'>日游戏次数</span></a></li>";
		str += "<li><a><span data-info='week-times'>周游戏次数</span></a></li>";
		str += "<li><a><span data-info='week-days'>周游戏天数</span></a></li>";
		str += "<li><a><span data-info='month-days'>月游戏天数</span></a></li>";
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").append(str);
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").show();
		break;
		case "time":
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").text("");
		var str = "<li class='active'><a><span data-info='day-time'>日游戏时长</span></a></li>";
		str += "<li><a><span data-info='week-time'>周游戏时长</span></a></li>";
		str += "<li><a><span data-info='single-time'>单次游戏时长</span></a></li>";
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").append(str);
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").css("width","123px");
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").show();
		break;
		case "period":
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").hide();
		break;
	}
});