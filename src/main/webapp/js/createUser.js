$("#role-select > li").click(function(){
	var role = $(this).children("a").text();
	var info = $(this).children("a").attr("data-info");
	$("#role-show").text(role);
	$("#role-show").attr("data-info", info);
});