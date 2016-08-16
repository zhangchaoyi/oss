$("#dropdownMenu1").on("mouseover", function() {
    if ($(this).parent().is(".open")) {
        return
    }

    $(this).dropdown("toggle")
});

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
    $(".dropdown.open").toggleClass("open");
}

function showIcon(icon){
	var value = $(icon).attr("data-value");
	var className = "span.fa.fa-" + value + ".icons";
	
	if($(icon).find("div").hasClass("checked")) {
		//whether element is null
		if($(className).length == 0){
			$("#btn-dropdownIcon").prepend("<span class='fa fa-"+ value +" icons' id='icons-view' aria-hidden='true'></span>");
		}		
	}else{
		$(className).remove();
	}
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

