<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/userpagetag.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" />
			<input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> 
			<input type="hidden" name="hidparam" id="hidparam" value="" /> 
			<input type="hidden" name="hidcount" id="hidcount" value="" />
			<input type="hidden" name="hidcurpage" id="hidcurpage" value="" />
			<div id="mdlright">			
				<div class="mdlrignav">
					<a href="AppInfo.do" class="addblock fleft">新增AppId</a>
					<div class="search"  style="width: 700px; margin-left: -350px;">
						<input type="text" id="txtAppID" name="txtAppID" class="keyword" />
						<input name="" class="searchbtn" type="button" onclick="getAppInfoList();" value="查询" />
					</div>
					<div class="rigname">
						<h2>
							<em>APP列表</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata"></div>
					<div class="botopt">
				<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
				</div>
				</div>
			</div>

		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function(){
		getAppInfoList();
	});
	function getAppInfoList(){
		var varcon = "";
		if ($.trim($("#txtAppID").val()).length > 0) {
			varcon = "appid=" + $.trim($("#txtAppID").val());
		}
		$("#hidparam").val(varcon);
		getFormDataCount(1);
	}

	function delappinfo(aid){
		if (!confirm("你确定要删除该条数据吗，删除后无法恢复")) {
			return false;
		}
		$.ajax({
			type : "GET",
			url : $("#hidurl").val(),
			cache : false,
			async : true,
			data : "do=del&aid="+aid,
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