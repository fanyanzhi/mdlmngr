<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>机构馆</title>
<%@ include file="/WEB-INF/header.inc"%>
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
				<a href="OrgUploadImage.do" class="addblock fleft">添加图片</a>
					<div class="rigname">
						<h2>
							<em>动态图片</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
						<div id="divdata">
						</div>
				</div>

			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function(){
		getOrgImageList();
	});
	function getOrgImageList(){
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
	function delLogoInfo(id){
		var r=confirm("确认删除？");
		  if (r==true){
				$.ajax({
					type : "GET",
					url : $("#hidurl").val(),
					cache : false,
					async : true,
					data : "do=del&id="+id,
					success : function(data,status) {
						alert(data);
						window.location.reload();
					},
					error : function() {
						alert("加载失败！");
					}
				});
		    }
		
	}
	</script>
</body>
</html>