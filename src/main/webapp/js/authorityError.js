$("#btn-goback").click(function(){
	window.history.go(-1);
	return;
});

$("#btn-login").click(function(){
	var href = window.location.href;
	if(href.indexOf("from=") != -1) {
		var from = href.split("from=")[1];
		location.href = location.protocol + "//" + location.host + "/oss/login?from=" +from;
		return;
	}
	location.href = location.protocol + "//" + location.host + "/oss/login";
});