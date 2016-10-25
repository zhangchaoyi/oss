$("div.nav-tab.loss > ul.nav.nav-pills > li").click(function(){
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
});