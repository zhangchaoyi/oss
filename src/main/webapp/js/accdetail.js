function checkInput(){
   var result = document.getElementById("account-id").value;
   
   if(result == ""  ){
     alert("帐号ID不能为空");
     return false;
   }
}

function GetQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}

$(function(){
  	var param = GetQueryString("account-id");
  	$("#account-id").attr("value",param);
  	if(param != null && param.toString().length>1){
  		$.post("/api/players/accdetail", {
        accountId:param
        },
	    function(data, status) {
	        configTable(data)
	    });
  	}

})


function configTable(data) {
    $('#data-table-detail-first').dataTable().fnClearTable();
    $('#data-table-detail-second').dataTable().fnClearTable();  
    console.log(data.detail);
    $('#data-table-detail-first').dataTable({
        "destroy": true,
        "data": data.device,
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
    $('#data-table-detail-second').dataTable({
        "destroy": true,
        "data": data.detail,
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

