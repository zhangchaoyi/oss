//鼠标覆盖按钮自动下拉菜单
$("#dropdownMenu1").on("mouseover", function() {
    if ($(this).parent().is(".open")) {
        return
    }

    $(this).dropdown("toggle")
});

//正则表达式获取url参数
function GetQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}
//图标显示
function iconsView() {
    var icons = $('.btn-icons');
    if(!$("ul.dropdown-menu.iconBar").find("div").hasClass("checked")){
    	$("#platform-selected").css("display", "block");
    	setTimeout('$("#platform-selected").css("display", "none")', 5000);
    	$(".dropdown.open").toggleClass("open");
    	return;
    }
    for(var i=0;i<icons.length;i++){
    	showIcon(icons[i]);    	
    }
    loadData();
    $(".dropdown.open").toggleClass("open");
}

//点击菜单tab 页面跳转时添加icon参数 添加时间参数
$("#main-menu > li a").click(function(){
    var href = $(this).attr("href");
    if(href=="#")return;
    $(this).attr("href",href + "?icon=" + getIcons() + "&startDate=" + $("input#startDate").attr("value") + "&endDate=" + $("input#endDate").attr("value"));  
});

function showIcon(icon){
	var value = $(icon).attr("data-value");
	var className = "span.fa.fa-" + value + ".icons";
	
	if($(icon).find("div").hasClass("checked")) {
		//whether element is null
		if($(className).length == 0){
			$("#btn-dropdownIcon").prepend("<span class='fa fa-"+ value +" icons icons-view' data-info="+value+" aria-hidden='true'></span>");
		}
	}else{
		$(className).remove();
	}
}

$("button.btn.btn-default.btn-off").click(function(){
    $.post("/oss/api/logout", {},
    function(data, status) {
        location.href = location.protocol + "//" + location.host + "/oss/login";
    });
});

//获取当前所有显示的图标
function getIcons(){
    var list = [];
    var str = "";
    var icon = $('button#btn-dropdownIcon > span.fa');
    for(var i=0;i<icon.length;i++){
        if($(icon[i]).attr("data-info")=='apple'){
            list.push("iOS");
            continue;
        }
        list.push($(icon[i]).attr("data-info"));

    }
    return list;
}

// 复选框选择/取消
$("li.btn-icons").click(function(){
	$(this).iCheck('toggle');
});

//select more than one icon
$('.dropdown-menu.iconBar').click(function(e) {
    e.stopPropagation();
});
//清除按钮
$("button.btn.btn-default.btn-circle").click(function(){
    location.reload();
})

//用户显示栏
$(function(){
    $.post("/oss/api/cookie/info", {

    },
    function(data, status) {
         if(data.message=="true"){
            $("#dropdownMenu1").text("");
            $("#dropdownMenu1").append(data.username + "<span class='caret'></span>");
         }
    }); 
   
});