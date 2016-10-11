//调整指标说明的高度
$(function(){
	// $("div#explain-panel").css("height", "inherit");
	configTable(null);
})

//充值玩家选择栏
$("ul.nav.nav-tabs.rank-payment-tab > li").click(function(){
	var info = $(this).children("a").attr("data-info");
	var txt = '';	
	switch(info){
        case "whaleRegion":
    	txt += '<li><a href="#">全部</a></li><li><a href="#">区服1</a></li><li><a href="#">区服2</a></li><li><a href="#">区服3</a></li>';
    	$("#rank-menu").text("");
       	$("#rank-menu").append(txt);
        break;
        case "whaleVersion":
        txt += '<li><a href="#">全部</a></li><li><a href="#">版本1</a></li><li><a href="#">版本2</a></li><li><a href="#">版本3</a></li>';
    	$("#rank-menu").text("");
       	$("#rank-menu").append(txt);
        break;
        case "whaleChannel":
        txt += '<li><a href="#">全部</a></li><li><a href="#">渠道1</a></li><li><a href="#">渠道2</a></li><li><a href="#">渠道3</a></li>';
    	$("#rank-menu").text("");
       	$("#rank-menu").append(txt);
        break;
    }
});

function configTable(data) {
    $('#data-table-rank-paymentBehavior').dataTable().fnClearTable();
    $('#data-table-rank-paymentBehavior').dataTable({
        "destroy": true,
        "data": data==null?null:data.device,
        "dom": '',
        'language': {
            'emptyTable': '没有数据',
            'loadingRecords': '加载中...',
            'processing': '查询中...',
            'search': '查询:',
            'lengthMenu': '每页显示 _MENU_ 条记录',
            'zeroRecords': '没有数据',
            "sInfo": "(共 _TOTAL_ 条记录)",
            'infoEmpty': '没有数据',
            'infoFiltered': '(过滤总件数 _MAX_ 条)'
        }
    });
}

//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$('input').iCheck('disable');
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");