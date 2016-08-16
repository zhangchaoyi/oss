$("#dropdownMenu1").on("mouseover", function() {
    if ($(this).parent().is(".open")) {
        return
    }

    $(this).dropdown("toggle")
});

function iconsView() {
    var icons = $('.btn-icons');
    var emptyClick = true;
    for(var i=0;i<icons.length;i++){
    	if(!showIcon(icons[i])){
    		emptyClick = false;
    	}
    }
    if(emptyClick){
    	$("#platform-selected").css("display", "block");
    	setTimeout('$("#platform-selected").css("display", "none")', 8000);
    	$(".dropdown.open").toggleClass("open");
    }	
}

function showIcon(icon){
	var emptyClick = true;
	var value = $(icon).attr("data-value");
	var className = "span.fa.fa-" + value + ".icons";
	
	if($(icon).find("div").hasClass("checked")) {
		//whether element is null
		emptyClick = false;
		if($(className).length == 0){
			$("#btn-dropdownIcon").prepend("<span class='fa fa-"+ value +" icons' id='icons-view' aria-hidden='true'></span>");
		}		
	}else{
		$(className).remove();
	}
	return emptyClick;
}

$("li.btn-icons").click(function(){
	$(this).iCheck('toggle');
});

//onload initial
$(document).ready(function(){
	$("#btn-dropdownIcon").prepend("<span class='fa fa-apple icons' id='icons-view' aria-hidden='true'></span>");
});

//select more than one icon
$('.dropdown-menu.iconBar').click(function(e) {
    e.stopPropagation();
});

