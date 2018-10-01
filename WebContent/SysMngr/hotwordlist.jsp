<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/userpagetag.js"></script>
<script type="text/javascript" src="./js/simplepagetag.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" /> 
			<input type="hidden" name="hidparam" id="hidparam" value="" /> 
			<div id="mdlright">
				<div class="mdlrignav">
				<a href="HotWord.do" class="addblock fleft">发布热搜词</a>
					<div class="rigname">
						<h2>
							<em>热搜词管理</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
						<div id="divdata"></div>
				</div>

			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function() {
		getHotWordList();
	});
	function getHotWordList(){
		$.ajax({
			type : "GET",
			url : $("#hidurl").val(),
			cache : false,
			async : true,
			data : "do=getlist",
			success : function(data) {
				if(data.length==0){
					$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
				}else{
					$("#divdata").html(data);
				}
			},
			error : function() {
				alert("加载失败！");
			}
		});
	}
	function delhotword(hid){
		if (!confirm("确定删除该数据吗")) {
			return false;
		}
		$.ajax({
			type : "GET",
			url : $("#hidurl").val(),
			cache : false,
			async : true,
			data : "do=del&hid="+hid,
			success : function(data) {
				if(data == "1"){
					alert("删除成功");
					window.location.reload();
				}else{
					alert("删除失败");
				}
			},
			error : function() {
				alert("加载失败！");
			}
		});
	}
	
	</script>
</body>
</html>