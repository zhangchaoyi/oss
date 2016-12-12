$("#menu-account-name > li").click(function(){
	var txt = $(this).children("a").text();
	$("#btn-account-name").html(txt+"<span class='caret'><span>");
});