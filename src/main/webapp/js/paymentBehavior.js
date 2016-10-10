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

//首付选择区
$("ul.nav.nav-tabs.paid-details > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    switch(info){
        case "fp-cycle":
        $("div.nav-tab.paid-detail-subtab").show();
        break;
        case "fp-rank":
        case "fp-money":
        $("div.nav-tab.paid-detail-subtab").hide();
        break;
    }
});

//首付选择区域的子选择栏
$("div.nav-tab.paid-detail-subtab > ul > li").click(function(){
    $(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
});
