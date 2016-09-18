//explain up and down button 
$("#btn-explain-up").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) + 145;
        if(value>=0){
            $("#btn-explain-up").addClass("disabled");
        }
        if($("#btn-explain-down").hasClass("disabled")){ 
            $("#btn-explain-down ").removeClass("disabled");
        }
        return value;
    });       
});
$("#btn-explain-down").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) - 145;
        if(value <= -290){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});s