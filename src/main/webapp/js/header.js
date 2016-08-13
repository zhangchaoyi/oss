$("#dropdownMenu1").on("mouseover", function() {
    if ($(this).parent().is(".open")) {
        return
    }

    $(this).dropdown("toggle")
});

function iconsView() {
    var icons = $("[type='checkbox']");
    if((!icons[0].checked) && (!icons[1].checked) && (!icons[2].checked)) {
    	alert("a");
    	return;
    }
    for(var i = 0;i < $("[type='checkbox']").length;i++) {
    	showIcon(icons[i]);
    }
    $(".dropdown.open").toggleClass("open");
}

function showIcon(icon){
	var className = "span.fa.fa-" + icon.value + ".icons";
	if(icon.checked === true) {
		//whether element is null
		if($(className).length == 0){
			$("#btn-dropdownIcon").prepend("<span class='fa fa-"+icon.value+" icons' id='icons-view' aria-hidden='true'></span>");
		}		
	}else{
		$(className).remove();
	}
}

$("li.btn-icons").click(function(){
	if($(this).find("input").prop("checked")){
		$(this).find("input").prop("checked", false);
	}else{
		$(this).find("input").prop("checked", true);
	}

	
});

//onload initial
$(document).ready(function(){
	$("#btn-dropdownIcon").prepend("<span class='fa fa-apple icons' id='icons-view' aria-hidden='true'></span>");
});

$('.dropdown-menu.iconBar').click(function(e) {
    e.stopPropagation();
});
