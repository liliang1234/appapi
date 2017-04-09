<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>选座</title>
<meta name="keywords" content="票之家电影，电影">
<meta name="description" content="票之家电影，电影">
<meta name="viewport"
	content="width=device-width,initial-scale=1, maximum-scale=1, user-scalable=0">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<meta name="format-detection" content="address=no">
<script type="text/javascript" src="<%=path %>/html/css/mf.jquery.js"></script>
    <script>
        (function (doc, win) {
        var docEl = doc.documentElement,
        resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
        recalc = function () {
        var clientWidth = docEl.clientWidth;
        if (!clientWidth) return;
        if(clientWidth>=750){
        docEl.style.fontSize = '100px';
        }else{
        docEl.style.fontSize = 100 * (clientWidth / 750) + 'px';
        }
        };
        if (!doc.addEventListener) return;
        win.addEventListener(resizeEvt, recalc, false);
        doc.addEventListener('DOMContentLoaded', recalc, false);
        })(document, window);
    </script>
<script type="text/javascript" src="<%=path%>/html/css/fastclick.js"></script>
<meta http-equiv="cleartype" content="on">

<link rel="stylesheet" href="<%=path%>/html/css/base2.css">
<style type="text/css">
	body,html{
	height:100%;
	}
.seats span {
	display: inline-block;
	border: 1px #59b7f7 solid;
	margin: 2px 2px;
	width: 50px;
	height: 25px;
}

.side {
	transition-timing-function: cubic-bezier(0.1, 0.57, 0.1, 1);
	-webkit-transition-timing-function: cubic-bezier(0.1, 0.57, 0.1, 1);
	transition-duration: 590ms;
	-webkit-transition-duration: 590ms;
}

.all {
	background-color: #e54847
}

.loadin {
	position: absolute;
	bottom: 0;
	top: 0;
	width: 100%;
	z-index: 998;
	display: none;
}
</style>
</head>
    <%--几个高度修改    1，seats 2.body 3.main-big 4.wrapper--%>
<body onload="loaded()" style="height:100%;">
	<section id="seats" class="hidden" style="display: block;height:100%;">
		<!--  <ol id="crumbs">
				<li>　</li>
				<li data-evt="showcrumbs/2" class="selected">2.在线选座</li>
				<li>3.支付</li>
				<li>　</li>
			</ol>  -->

		<div class="mp-loading" style="visibility: visible; display: block">
			<span class="spin"></span>正在为您获取座位图…
		</div>
		<div class="main main-small main-big" data-user=""
			style="height:auto; margin-bottom: 0.2rem; visibility: hidden; display: block">
			<h3>
				<c:choose>
					<c:when test="${isMultiArea}">
						<span id="chooseSeat">
							<input type="radio" name="seat" checked value="single" onclick="loadSeat(this.value)">${seatArea }单 </input> 
							<input type="radio" name="seat" value="double" onclick="loadSeat(this.value)">${seatArea }双 </input> 
						 </span>
					</c:when>
					<c:otherwise>
						${areaDesc} <!-- <a href="javascript:void(0)" onclick="show();">显示区域图</a>  -->
					</c:otherwise> 
				</c:choose>
			</h3>
			<div class="wrapper" id="wrapper"
				style="min-height:3rem; overflow: hidden;">
				<div class="scroller" id="scroller"
					style="transform-origin: 0px 0px 0px; position: absolute; top: 0px; left: 10px;">
					<div class="item" data-sectionid="0000000000000001"
						data-sectionname="普通区">
						<div class="c-tips">
							<span>舞台</span>
						</div>
						<div id="seatt"></div>
						<p class="c-tips"></p>
					</div>
				</div>
				<div class="side">
					<ol style="position: absolute; top: 0px; left: 0px;">

					</ol>
				</div>
			</div>

		</div>
		<div class="seats"></div>
		<div class="loadin" id="loadin">
			<div class="pay-loading" style="display: block">
				<span class="spin"></span>正在为您预订座位，请稍后…
			</div>
		</div>
		<div class="loadin" id="showImage" onclick="hide(this)">
			<div class="pay-loading" style="display: block; width: 280px;">
				<%--  <c:if test="${seatArea=='b'}">
					 	<div id="single">
							<img alt="" src="http://fhgc.piaozhijia.cn/images_new/seat/seat_b_single.jpg" type="single">
					 	</div>
					 	<div id="double" style="display: none">
							<img alt="" src="http://fhgc.piaozhijia.cn/images_new/seat/seat_b_double.jpg" type="double">
					 	</div>
					</c:if> --%>
				<img alt=""
					src="http://fhgc.piaozhijia.cn/images_new/seat/seat_${seatArea}.jpg">
			</div>
		</div>


	</section>
</body>
<script type="text/javascript" src="<%=path%>/html/css/iscroll-zoom.js"></script>
<script type="text/javascript">
FastClick.attach(document.body);
    <%--修改--%>
    var seatsheight = $("#seats").css("height");
    $("#wrapper").css("height",seatsheight);
    <%--修改--%>
function show(){
	$("#showImage").css('display','block');
}
function hide(a){
	$(a).css('display','none');
}
checkedNum ='${checkedNum}';
supplierId ='${supplierId}';
var myScroll;
seatArea ='${seatArea}';
areaDesc ='${areaDesc}';
screeningId ='${screeningId}';
showStartTime ='${showStartTime}';
sort ='${sort}';
scienicId ='${scienicId}';
token ='${token}';
productId='${productId}';
playId ='${playId}';
var data1 ={token:token,scienicId:scienicId,supplierId:supplierId,seatArea:seatArea,screeningId:screeningId,showStartTime:showStartTime,sort:sort,areaDesc:areaDesc,productId:productId};
var data ={token:token,scienicId:scienicId,supplierId:supplierId,seatArea:seatArea,screeningId:screeningId,showStartTime:showStartTime,sort:sort,areaDesc:areaDesc,productId:productId};
function loadSeat(oddOrEven){
	$(".mp-loading").css('display','block');
	$("#seatt").empty();
	$(".side ol").empty();
    <%--修改visible--%>
	$(".main").css('visibility','visible');
	if(oddOrEven!= 'undefined'&& oddOrEven=='double'){
		$("#double").css('display','block');
		$("#single").css('display','none');
	}else if(oddOrEven!= 'undefined'&& oddOrEven=='single'){
		$("#single").css('display','block');
		$("#double").css('display','none');
	}
	$.ajax({
		type : "POST",
		url : "<%=path%>/seat/product?oddOrEven="+oddOrEven,
		dataType : "json",
		data:data1,
		success : function(data) {
			playId =data.playId;
			$(".side ol").append('<li class="cs"></li>');
			$.each(data.rowSeatList,function(i,n){
				$(".side ol").append('<li>'+(i+1)+'</li>');
				var $p = $("<p></p>");
				$.each(n.list,function(j,o){
						if(o.seatNum.indexOf("__")!=-1){
							$p.append('<span class="seat"></span>');
						}else{
							$p.append('<a href="javascript:;" class="seat '+o.style+'" data-no="'+o.seatNum+'"></a>');
						}
				});
				$("#seatt").append($p);
			});
			$(".side ol").append('<li class="cs"></li>');
			myScroll.refresh();
			$(".mp-loading").css('display','none');
			$(".main").css('visibility','visible');
		},error:function(){
			alert("加载失败，请稍候重试");
		
		}
	});
}
function loaded () {
    <%--修改--%>
	loadSeat();
    <%--修改--%>
	 myScroll = new IScroll('#wrapper', {
			zoom: true,
			scrollX: true,
			scrollY: true,
			mouseWheel: true,
			wheelAction: 'zoom',
			click: true 
		}); 

	 var lisenter = false;
	 myScroll.on("scrollStart", function(){
		 lisenter = true;
		}) ;
	 myScroll.on("zoomStart", function(){
		 lisenter = true;
		}) ;
	 myScroll.on("scrollCancel", function(){
		 lisenter = false;
		}) ;
	 myScroll.on("scrollEnd", function(){
		 lisenter = false;
		}) ;
	 myScroll.on("zoomEnd", function(){
		 lisenter = false;
		}) ;
		
	 function abcd(){
		 if(lisenter){
			 if(myScroll.directionY!=0){
				$(".side").css("transform","translate(0px, "+myScroll.y+"px) scale("+myScroll.scale+") translateZ(0px)");
			}
			if(myScroll.scale>=1){
				$(".side").css("transform","translate(0px, "+myScroll.y+"px) scale("+myScroll.scale+") translateZ(0px)");
				}
		 }
		};
		var currentNum =${currentNum};
		var num = ${num};
		
		setInterval(abcd,1);
	$("#seatt").click(function(e){

		 var e = e || window.event;
		 var target = e.target || e.srcElement;

		 if(target.nodeName.toLowerCase() == "a"){
			if(!$(target).hasClass("disabled")&&!$(target).hasClass("love")){
				seatNo = $(target).attr('data-no');
				data ={playId:playId,token:token,scienicId:scienicId,supplierId:supplierId,seatArea:seatArea,seatNumber:seatNo,showStartTime:showStartTime,sort:sort};
				if($(target).hasClass("selected")){
					$(target).addClass("active");
					$(target).removeClass ("selected");
					currentNum--;
					location.href="/del/"+seatNo;
					$.ajax({
						type : "POST",
						url : "<%=path%>/seat/delCacheSeat",
						dataType : "json",
						data:data,
						success : function(data) {

							},
						error:function(){

						}
					})
				}else{
					if(currentNum<num){
						
					$.ajax({
						type : "POST",
						url : "<%=path%>/seat/addCacheSeat",
						dataType : "json",
						data:data,
						async:false,
						success : function(data) {
								if(data.code=="10000"){
									$(target).addClass("selected");
									$(target).removeClass("active");
									currentNum++;
									location.href="/add/"+seatNo;
								}else{
									$(target).addClass("disabled");
									$(target).removeClass("active");
								}
						},
						error:function(){

						}
					})
				  }else{
						alert("不可以选择超过"+num+"个座位")
					}
				}
			}
		  }
	})
}
document.addEventListener('touchmove', function (e) { e.preventDefault(); }, false);

</script>
<script type="text/javascript">
function  lockSeat(data){
	$.ajax({
		type : "POST",
		url : "<%=path%>/seat/addCacheSeat",
		dataType : "json",
		data:data,
		success : function(data) {
				if(data.code=="10000"){
					alert(data.message);
				}else{
					alert(data.message);
				}
		}
	});
};
</script>
</html>