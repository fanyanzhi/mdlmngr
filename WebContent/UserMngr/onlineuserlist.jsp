<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/userpagetag.js"></script>
<script type="text/javascript" src="./js/simplepagetag.js"></script>
<script type="text/javascript" src="./js/json.js"></script>
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
			<input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> 
			<input type="hidden" name="hidcount" id="hidcount" value="" />
			<input type="hidden" name="hidfunction" id="hidfunction" value="setOnlineUserInfo" />
			<div id="mdlright">
				<div class="mdlrignav conmendnav">
					<a href="OnlinePicAnalysis.do" class="addblock fleft">图表分析</a>
					<a href="OnlineStatistics.do" class="addblock fleft" title="实时统计">实时统计</a>
					<div class="longsearch">
						<input type="text" id="txtUserName" name="txtUserName" class="keyword" /> <input name="" class="searchbtn" type="button" onclick="UserOnlineSearch();" value="查询" />
						<span style="margin-left: 10px;">在线用户：</span>
						<span id="totalUser" style="color:#C00">0</span>
					</div>
					<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple"/>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
				<div id="divdata">
				</div>
				<div class="botopt">
				<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
				</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		$(function() {
			UserOnlineSearch();
			/* 统计在线用户，凌晨到半小时前 */
		$.ajax({
				type : "GET",
				url : $("#hidurl").val(),
				async : true,
				cache : false,
				data : "do=halfHourUsers",
				success : function(data) {
					$("#totalUser").html(data);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					closeLoading();
					alert("服务器错误");
				}
			});
		});
	</script>
</body>
</html>