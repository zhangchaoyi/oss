$(function(){
	loadData();
	withoutIcon();
});

function loadData(){
}

$("#btn-execute").click(function(){
	var account = $("#input-recover-account").val();
	var serialNumber = $("#serial-number").val();
	var isPro = $("#is-pro").val();
	if(account==null||account==""){
		alert("帐号不能为空");
		return;
	}
	if(serialNumber==null||serialNumber==""){
		alert("订单号不能为空");
		return;
	}
	if(isPro==null||isPro==""){
		alert("是否正式订单不能为空");
		return;
	}
	var param = [];
	param.push(account);
	param.push(serialNumber);
	param.push(isPro);
	var payloadData = {
	"cmd":"recover_charge",
	"parms":param,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	$.ajax({
        type: "POST",
        url: getAddressFromIcon(getCookie("server")),
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payloadData),
        crossDomain: true,
        dataType: "json",
        success: function (data) {
        	if(data.result=='1'){
        		alert("处理成功");
        	}else{
        		alert("处理失败");
        	}
        }
    });
});


//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});

