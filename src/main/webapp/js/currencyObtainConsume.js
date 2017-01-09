var goldSingleTable = "#table-gold-single-player";
var goldServerTable = "#table-gold-server";
var rmbSingleTable = "#table-rmb-single-player";
var rmbServerTable = "#table-rmb-server";

$(function(){
	withoutIcon();
	loadData();
})

function loadData(){
	var goldInfo = $(".nav-tab.sub-op-gold > ul > li.active > a > span").attr("data-info");
	var rmbInfo = $(".nav-tab.sub-op-rmb > ul > li.active > a > span").attr("data-info");

	if(goldInfo=="all-server"){
		loadAllCurrency("gold",goldServerTable);
	}else{
		var account = $("#input-gold-account-name").val();
		if(account==""){
			configTable(null, goldSingleTable);
		}else{
			loadSingleCurrency("gold",account,goldSingleTable);
		}
	}

	if(rmbInfo=="all-server"){
		loadAllCurrency("rmb",rmbServerTable);
	}else{
		var account = $("#input-rmb-account-name").val();
		if(account==""){
			configTable(null, rmbSingleTable);
		}else{
			loadSingleCurrency("rmb",account,rmbSingleTable);
		}
	}

}

function loadAllCurrency(currency, dataTable){
	$(dataTable).dataTable().fnClearTable();
	$(dataTable).dataTable({
	    "destroy": true,
	    "searching": false,
    	"ordering":  false,
	    serverSide: true,
	    ajax: {
			url: '/oss/api/operation/currency/all',
			type: 'POST',
			"data": function ( d ) {
				d.startDate = $("input#startDate").attr("value");
				d.endDate = $("input#endDate").attr("value");
				d.currency = currency;	    
			}
	   	},
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

function loadSingleCurrency(currency, account, dataTable){
	$.post("/oss/api/operation/currency/player", {
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value"),
        currency:currency,
        account:account
    },
    function(data, status) {
        configTable(data, dataTable);
    });
}

function configTable(data,dataTable) {
    $(dataTable).dataTable().fnClearTable();  
    $(dataTable).dataTable({
        "destroy": true,
        // retrive:true,
        "data": data,
        "order": [[ 1, 'desc' ]],
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




$(".nav-tab.sub-op-gold > ul > li").click(function(){
	var goldInfo = $(this).children("a").children("span").attr("data-info");
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
	if(goldInfo=="single-player"){
		$(".tab-content.gold-single-player").show();
		$(".tab-content.gold-all-server").hide();
		$(".tab-content.gold-single-player").css("float","left");
		configTable(null, goldSingleTable);
	}else{
		$(".tab-content.gold-single-player").hide();
		$(".tab-content.gold-all-server").show();
		$(".tab-content.gold-all-server").css("float","initial");
		$("#input-gold-account-name").val("");
		loadAllCurrency("gold",goldServerTable);
	}
});

$(".nav-tab.sub-op-rmb > ul > li").click(function(){
	var rmbInfo = $(this).children("a").children("span").attr("data-info");
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
	if(rmbInfo=="single-player"){
		$(".tab-content.rmb-single-player").show();
		$(".tab-content.rmb-all-server").hide();
		$(".tab-content.rmb-single-player").css("float","left");
		configTable(null, rmbSingleTable);
	}else{
		$(".tab-content.rmb-single-player").hide();
		$(".tab-content.rmb-all-server").show();
		$(".tab-content.rmb-all-server").css("float","initial");
		$("#input-rmb-account-name").val("");
		loadAllCurrency("rmb", rmbServerTable);
	}
});

$("#btn-gold-single-gold").click(function(){
	var account = $("#input-gold-account-name").val();
	if(account==""){
		return;
	}
	loadSingleCurrency("gold",account, goldSingleTable);
});

$("#btn-rmb-single-rmb").click(function(){
	var account = $("#input-rmb-account-name").val();
	if(account==""){
		return;
	}
	loadSingleCurrency("rmb",account, rmbSingleTable);
});


//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});