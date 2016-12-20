$("ul.nav.nav-tabs.announcement > li").click(function(){
	var info = $(this).children("a").attr("data-info");
	switch(info){
		case "rolling-content":
		initRollingContentHtml();
		break;
		case "announcement":
		$(".form-group.announcement-tab").html("<label>标题</label><input class='form-control' type='text' id='input-header'>");
		break;
	}
});



function initRollingContentHtml(){
	var htmlStr = "<div class='input-group'><span class='input-group-addon'>次数</span><input type='text' class='form-control' style='width:10%;margin-right:20px' id='input-times'><input type='text' class='form-control' placeholder='请输入间隔' data-info='none' style='width:10%' id='input-period'><div class='input-group-btn' style='float:left'><button type='button' class='btn btn-info dropdown-toggle' data-toggle='dropdown'>间隔<span class='caret'></span></button><ul class='dropdown-menu dropdown-menu' role='menu' id='period-menu'><li><a data-info='hour'>时</a></li><li><a data-info='minute'>分</a></li><li><a data-info='second'>秒</a></li></ul></div></div>";
	$(".form-group.announcement-tab").html(htmlStr);
	$("#period-menu > li").click(function(){
		var period = $(this).children("a").attr("data-info");
		var txt = $(this).text();
		$("#input-period").attr("data-info",period);
		$("#period-menu").siblings("button").html(txt + "<span class='caret'></span>");
	});
}

                      