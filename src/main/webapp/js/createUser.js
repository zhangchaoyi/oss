$("#role-select > li").click(function(){
	var role = $(this).children("a").text();
	var info = $(this).children("a").attr("data-info");
	$("#role-show").text(role);
	$("#role-show").attr("data-info", info);
});

$("#create-user").click(function(){
	var username = $("#username").val();
	var password = $("#password").val();
	var confirm = $("#confirm-password").val();

	if(username == "" || password == "" || confirm == ""){
		alert("请输入用户名或密码！");
		return;
	}
	if(username.length<5 || password<5){
		alert("帐号或密码长度需要大于五个字符");
		return;
	}
	if(username==password){
		alert("帐号名不能和密码相同");
		return;
	}
	var role = $("#role-show").attr("data-info");
	if(role=="none"){
		alert("请选择角色");
		return;
	}
	if(password != confirm){
		alert("确认密码不一致");
		return;
	}
	var key = randomWord(false,16,16);
	var selectList = {};
	var checkbox = $(".cu-select-data");
	for(var i=0;i<checkbox.length;i++){
		if($(checkbox[i]).prop("checked")){
			var dataInfo = $(checkbox[i]).parent().siblings("a").attr("data-info");
			var groupInfo = $(checkbox[i]).parent().siblings("a").attr("group-info");
			if(selectList.hasOwnProperty(groupInfo)){
				var value = selectList[groupInfo];
				value.push(dataInfo);
				selectList[groupInfo] = value;
			}else{
				var value = [];
				value.push(dataInfo);
				selectList[groupInfo] = value;
			}
		}
	}

	if(isEmptyObject(selectList)){
		alert("请选择服务器");
		return;
	}

	if(selectList.server==undefined){
		alert("请选择服务器");
		return;
	}

	$.post("/oss/api/admin/createUser", {
		username:Encrypt(username, key),
		password:Encrypt(password, key),
		role:Encrypt(role, key),
		selectList:JSON.stringify(selectList),
		key:key
    },
    function(data, status) {
    	var message = data.message;
    	if(message=="successfully"){
    		alert("创建用户成功");
    	}else if(message=="exist"){
    		alert("用户名已存在,请重新输入");
    	}else {
    		alert("创建用户失败");
    	}
    	history.go(0);
    });
});

function Encrypt(word, key){
	var key = CryptoJS.enc.Utf8.parse(key);
	var srcs = CryptoJS.enc.Utf8.parse(word);
	var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
    return encrypted.toString();
}

/*
** randomWord 产生任意长度随机字母数字组合
** randomFlag-是否任意长度 min-任意长度最小位[固定位数] max-任意长度最大位
** xuanfeng 2014-08-28
*/
 
function randomWord(randomFlag, min, max){
    var str = "",
        range = min,
        arr = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
 
    // 随机产生
    if(randomFlag){
        range = Math.round(Math.random() * (max-min)) + min;
    }
    for(var i=0; i<range; i++){
        pos = Math.round(Math.random() * (arr.length-1));
        str += arr[pos];
    }
    return str;
}

function initSelect(){
	$("#cu-select-all").siblings("ins").click(function(){
		var checked = $(this).parent().hasClass("checked");
		if(checked==true) {
			$(".cu-select-options input").iCheck("check");
		}else{
			$(".cu-select-options input").iCheck("uncheck");
		}
	});

	$(".cu-type").siblings("ins").click(function(){
		var checked = $(this).parent().hasClass("checked");
		if(checked==true) {
			$(this).parent().siblings("div").find("input").iCheck("check");
		}else{
			$(this).parent().siblings("div").find("input").iCheck("uncheck");
		}
	});
}

$(function(){
	initSelect();
})

function isEmptyObject(obj) {
  for (var key in obj) {
    return false;
  }
  return true;
}

//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-db").one("click",function(){
    $("#db-menu > li").addClass("disabled");
    $("#db-menu > li").unbind("click");   
});
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});