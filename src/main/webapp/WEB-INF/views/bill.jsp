<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%
String path = request.getContextPath();
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width,height=device-height,inital-scale=1.0,maximum-scale=1.0,user-scalable=no;">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<meta charset="utf-8"/>
<title>对账单</title>
<link href="<%=path %>/html/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=path %>/html/css/mf.jquery.js"></script>
<style type="text/css">
body{
	font-size:15px;
}
table{border:1px solid #999;overflow: auto;width="100%";font-size:30px;}
table td{word-break: keep-all;white-space:nowrap;}
.admintime{
    box-sizing: border-box;
	padding-bottom:20px;
	padding-top:20px;
	padding-left:20px;
	    overflow: auto;
}
.comfirm{
	width: 50%;
    font-size: 20px;
    text-align: center;
    background-color: blue;
    color: wheat;
    line-height: 50px;
    border-radius: 5px;
	margin: 0 auto;
}
.problem {
    color: #666;
    font-size: 1.5em;
    padding-top: 3%;
    padding-bottom: 3%;
    padding-left: 3%;
    border-top: 1px solid transparent;
    color: #333;
    background-color: #f5f5f5;
    border-color: #ddd;
}
.clear{
	height:50px;
	 font-size: 1.5em;
	   text-align: center;
	line-height: 50px;   
}
</style>
</head>

<body>
<div class="clear">对账单</div>
<div class="problem">账期：2015-08-08 ~ 2015-09-09</div>
<div class="problem">采购金额：<span id ="realAmount">${realAmount }</span></div>
<div class="problem">返利金额：<span id ="rebateAmount">${rebateAmount }</span></div>
<div class="admintime wenti">
	
	<table border="1" cellspacing="0" cellpadding="0">	
				<tr class="tdbg txtweight">
					<td width="20%">订单号</td>
					<td width="20%">供应商名称</td>
					<td width="20%">产品类型</td>
					<td width="10%">团/散</td>
					<td width="10%">单价/元</td>
					<td width="10%">数量</td>
					<td width="20%">采购金额</td>
					<td width="20%">返利金额</td>
					<td width="20%">操作</td>
				</tr>
				<tbody id ="table">
				<c:forEach items="${list}" var="o">
					<tr>
					<td>${o.orderId }</td>
					<td>${o.supplierName }</td>
					<td>${o.standardName }</td>
					<c:if test="${o.isGroup==0 }">
						<td>散</td>
					</c:if>
					<c:if test="${o.isGroup==1 }">
						<td>团</td>
					</c:if>
					<td>${o.unitPrice }</td>
					<td>${o.totalNum }</td>
					<td>${o.realAmount }</td>
					<td>${o.rebateAmount }</td>
					<td onclick="test('${o.orderId }')">详情</td>
					</tr>
					</c:forEach>
				</tbody>
				</table>
<script>
function test(id){
	location.href=id;
}
</script>
</body>
</html>
