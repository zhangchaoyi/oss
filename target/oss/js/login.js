function login(){
	if($("#username").val() == "" || $("#password").val() == ""){
		alert("请输入用户名或密码！");
		return;
	}
	$.post("/oss/login", $("form").serialize(), function(data){
		if(data.message == "success"){
			location.href = location.protocol + "//" + location.host + "/oss/main.html";
		}else{
			alert("用户名或密码错误");
		}
	})
}