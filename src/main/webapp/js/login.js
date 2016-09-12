function login(){
	if($("#username").val() == "" || $("#password").val() == ""){
		alert("请输入用户名或密码！");
		return;
	}

	$.post("/api/login", $("form").serialize(), function(data){
		if(data.message == "success"){
			var href = window.location.href;
			if(href.indexOf("from=") != -1) {
				var from = href.split("from=")[1];
				location.href = location.protocol + "//" + location.host + from;
				return;
			}

			location.href = location.protocol + "//" + location.host + "/dashboard";
		}else{
			alert("用户名或密码错误");
		}
	})
}

function getKey(e) {
	if(event.keyCode==13){  
		login(); 
	}  
}
$(function(){
	$("#username").focus();
});
