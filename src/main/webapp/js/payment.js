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
        if(value <= -720){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});

//付费分析选择tab
$("ul.nav.nav-tabs.payment-tab > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    if(info=="analyze-payment-times"){
        $("div.nav-tab.paid-analyze-tab").hide();
        $("div.arpu-block").show();
        return;
    }
    $("div.nav-tab.paid-analyze-tab").show();
    $("div.arpu-block").hide();
});

//detail tag
$("ul.nav.nav-tabs.paid-details > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    switch(info){
        case "area":
        case "country":
        case "channel":
        $("div.nav-tab.paid-detail-subtab").show();
        $("div.nav-tab.paid-detail-consumepackage").hide();
        break;
        case "mobileoperator":
        case "paid-way":
        case "currency-type":
        $("div.nav-tab.paid-detail-subtab").hide();
        $("div.nav-tab.paid-detail-consumepackage").hide();
        break;
        case "comsume-package":
        $("div.nav-tab.paid-detail-subtab").hide();
        $("div.nav-tab.paid-detail-consumepackage").show();
        break;
    }
});


//付费分析时间tab
// $("div.nav-tab.paid-analyze-tab > ul > li").click(function(){
//     $("div.nav-tab.paid-analyze-tab > ul > li.active").toggleClass("active");
//     $(this).addClass("active");
// });
// $("div.nav-tab.paid-analyze-arp-tab > ul > li").click(function(){
//     $("div.nav-tab.paid-analyze-arp-tab > ul > li.active").toggleClass("active");
//     $(this).addClass("active");
// });

//details sub tag
$("div.nav-tab.paid-detail-subtab > ul > li, div.nav-tab.paid-detail-consumepackage > ul > li, div.nav-tab.paid-analyze-arp-tab > ul > li, div.nav-tab.paid-analyze-tab > ul > li").click(function(){
    $(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
});

