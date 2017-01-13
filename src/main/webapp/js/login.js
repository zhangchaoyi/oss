function login(){
	var username = $("#username").val();
	var password = $("#password").val();

	if(username == "" || password == ""){
		alert("请输入用户名或密码！");
		return;
	}
	var key = randomWord(false,16,16);
	$.post("/oss/api/login", {
		username:Encrypt(username, key),
		password:Encrypt(password, key),
		key:key,
    },
    function(data, status) {
    	if(data.message == "success"){
    		$.get("/oss/api/prop/channels", {},
		    function(data, status) {
		    	for(var key in data){
		    		var value = data[key];
		    		localStorage.setItem(key+"Channels", JSON.stringify(value));
		    		//当前选择渠道去除测试
		    		if(value["0"]!=undefined){
		    			delete value["0"];
		    		}
		    		localStorage.setItem(key+"SelectChannels", JSON.stringify(value));
		    	}
		    	var href = window.location.href;
				if(href.indexOf("from=") != -1) {
					var from = href.split("from=")[1];
					location.href = location.protocol + "//" + location.host + from;
					return;
				}
				location.href = location.protocol + "//" + location.host + "/oss/dashboard";
		    });
		}else{
			alert("用户名或密码错误");
		}
    });
}

function getKey(e) {
	if(event.keyCode==13){  
		login(); 
	}  
}
$(function(){

	$("#username").focus();

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

