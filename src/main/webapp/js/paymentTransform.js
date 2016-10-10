//explain up and down button 
$("#btn-explain-up").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) + 144;
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
        value = parseFloat(value) - 144;
        if(value <= -288){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});

//付费类选择区
$("ul.nav.nav-tabs.rate-paymentTransform-tab > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    switch(info){
        case "dpr":
        case "wpr":
        case "mpr":
        $("div#avg-pt-rate").show();
        break;
        case "apr":
        $("div#avg-pt-rate").hide();
        break;
    }
});