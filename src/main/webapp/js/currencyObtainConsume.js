$(function(){
	loadData();
})

function loadData(){
	var dataInfo = $(".nav-tab.sub-op-currency > ul > li.active > a > span").attr("data-info");
	if(dataInfo=="all-server"){
		loadAllCurrency();
	}else{
		var account = $("#input-account-name").val();
		if(account==""){
			configTable(null);
			return;
		}
		loadSingleCurrency(account);
	}
}

function loadAllCurrency(){
	$.post("/oss/api/operation/currency/all", {
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configTable(data);
    });
}

function loadSingleCurrency(account){
	$.post("/oss/api/operation/currency/player", {
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value"),
        account:account
    },
    function(data, status) {
        configTable(data);
    });
}

function configTable(data) {
    $("#table-currency").dataTable().fnClearTable();  
    $("#table-currency").dataTable({
        "destroy": true,
        // retrive:true,
        "data": data,
        "order": [[ 1, 'asc' ]],
        "dom": '<"top"f>rt<"left"lip>',
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

$(".nav-tab.sub-op-currency > ul > li").click(function(){
	var dataInfo = $(this).children("a").children("span").attr("data-info");
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
	if(dataInfo=="single-player"){
		$(".input-group.single-players").css("display","table");
		$(".tab-content").css("float","left");
		configTable(null);
	}else{
		$(".input-group.single-players").hide();
		$(".tab-content").css("float","initial");
		$("#input-account-name").val("");
		loadAllCurrency();
	}
	
});

$("#btn-single-currency").click(function(){
	var account = $("#input-account-name").val();
	if(account==""){
		return;
	}
	loadSingleCurrency(account);
});

//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});