$(function(){
	loadData();
});

function loadData(){
	$.post("/oss/api/dashboard", {
    },
    function(data, status) {
       configTable(data);   
    });
}

function configTable(data){
	$("#equipTotal").text(data["eT"]);
	$("#playerTotal").text(data["pT"]);
	$("#activePlayerTotal").text(data["apT"]);
	$("#payNumTotal").text(data["pPT"]);
	$("#payTimesTotal").text(data["pTT"]);
	$("#incomeTotal").text('$' + data["rT"]);
	$("#arpuTotal").text('$' + data["arpuTotal"]);
	$("#gameTimesAvg").text(data["aGTTotal"]);
	$("#gameTimeTotal").text(data["aGPTotal"]);
	$("#equip").text(data["eY"]);
	$("#player").text(data["pY"]);
	$("#activePlayer").text(data["apY"]);
	$("#payNum").text(data["pPY"]);
	$("#payTimes").text(data["pTY"]);
	$("#income").text('$' + data["rY"]);
	$("#arpu").text('$' + data["arpuY"]);
	$("#gameTimes").text(data["aGTY"]);
	$("#gameTime").text(data["aGPY"]);
	
	$("#iOS-eT").text(data["iOSET"]);
	$("#iOS-eN").text(data["iOSEN"]);
	$("#iOS-pT").text(data["iOSPT"]);
	$("#iOS-pN").text(data["iOSPN"]);
	$("#iOS-gT").text(data["iOSGT"]);
	$("#iOS-gN").text(data["iOSGN"]);
	$("#iOS-rT").text('$' + data["iOSRT"]);
	$("#iOS-rN").text('$' + data["iOSRN"]);
	$("#and-eT").text(data["androidET"]);
	$("#and-eN").text(data["androidEN"]);
	$("#and-pT").text(data["androidPT"]);
	$("#and-pN").text(data["androidPN"]);
	$("#and-gT").text(data["androidGT"]);
	$("#and-gN").text(data["androidGN"]);
	$("#and-rT").text('$' + data["androidRT"]);
	$("#and-rN").text('$' + data["androidRN"]);
	$("#wp-eT").text(data["windowsET"]);
	$("#wp-eN").text(data["windowsEN"]);
	$("#wp-pT").text(data["windowsPT"]);
	$("#wp-pN").text(data["windowsPN"]);
	$("#wp-gT").text(data["windowsGT"]);
	$("#wp-gN").text(data["windowsGN"]);
	$("#wp-rT").text('$' + data["windowsRT"]);
	$("#wp-rN").text('$' + data["windowsRN"]);
	$("#eT-sum").text(data["deviceTSum"]);
	$("#eN-sum").text(data["deviceNSum"]);
	$("#pT-sum").text(data["playersTSum"]);
	$("#pN-sum").text(data["playersNSum"]);
	$("#gT-sum").text(data["gameTimesTSum"]);
	$("#gN-sum").text(data["gameTimesNSum"]);
	$("#rT-sum").text('$' + data["revenueTSum"]);
	$("#rN-sum").text('$' + data["revenueNSum"]);
	$("#eT-sum-total").text(data["deviceTSum"]);
	$("#eN-sum-total").text(data["deviceNSum"]);
	$("#pT-sum-total").text(data["playersTSum"]);
	$("#pN-sum-total").text(data["playersNSum"]);
	$("#gT-sum-total").text(data["gameTimesTSum"]);
	$("#gN-sum-total").text(data["gameTimesNSum"]);
	$("#rT-sum-total").text('$' + data["revenueTSum"]);
	$("#rN-sum-total").text('$' + data["revenueNSum"]);
};

$(".set-icons").click(function(){
	var icon = $(this).attr("data-info");
	var list = [];
	list.push(icon);
	setCookie("icons", list);
})

//设置cookie 作用域是/
function setCookie(c_name,value)
{
    document.cookie=c_name+ "=" +escape(value)+";path=/";
}