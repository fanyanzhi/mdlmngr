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
<script type="text/javascript" src="./js/simplepagetag.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" /> <input type="hidden" name="hidparam" id="hidparam" value="" /> <input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> <input type="hidden" name="hidcount" id="hidcount" value="" /> <input type="hidden" name="hidcurpage" id="hidcurpage" value="" />
			<div id="mdlright">
			<script language="javascript" type="text/javascript" src="js/PopupCalendar.js"></script>
				<div class="mdlrignav">
				<div class="longpadrig">
					<div class="longsearch">
							用户名：<input type="text" id="txtUserName" name="txtUserName" class="searchkey" />
							检索式名：<input type="text" id="formal" name="formal" class="searchkey" />
						<!-- 	appid：<input type="text" id="appid" name="appid" class="searchkey" /> -->
							开始时间：<input id="txtStartDate" name="txtStartDate" type="text" class="timetstyle" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);"/>&nbsp;&nbsp;
					                     结束时间：<input id="txtEndDate" name="txtEndDate" type="text" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" autocomplete="off" class="timetstyle" />
							<input name="" class="searchbtn" type="button" onclick="getAtionScholar();" value="查询" />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span id="recordCount"></span>
					</div>
					<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple" />
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata"></div>
					<div class="botopt">
						<!-- <div class="botleftopt">
							<input name="selectAll" id="selectAll" onclick="checkall('tabfiles',this)" type="checkbox" value="" /><a href="javascript:void(0);" onclick="delUpFileAll()" class="addblock">删除</a>
						</div> -->
						<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
					</div>
				</div>

			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function() {
		getAtionScholar();
	});
	function getAtionScholar(){
		var varcon = "";
		if ($.trim($("#txtUserName").val()).length > 0) {
			varcon += "un=" + encodeURI($.trim($("#txtUserName").val())) + "&";
		}
		if ($.trim($("#formal").val()).length > 0) {
			varcon += "formal=" + encodeURI($.trim($("#formal").val())) + "&";
		}
		if ($.trim($("#appid").val()).length > 0) {
			varcon += "appid=" + encodeURI($.trim($("#appid").val())) + "&";
		}
		if ($.trim($("#txtStartDate").val()).length > 0) {
			varcon += "startdate=" + encodeURI($.trim($("#txtStartDate").val())) + "&";
		}
		if ($.trim($("#txtEndDate").val()).length > 0) {
			varcon += "enddate=" + encodeURI($.trim($("#txtEndDate").val())) + "&";
		}
		$("#hidparam").val(varcon);
		getActionFormDataCount(1);
		getKuaiBaoUsers();
	}
	
	function getKuaiBaoUsers(){
		$.ajax({
			type : "GET",
			url : $("#hidurl").val(),
			async : true,
			cache : false,
			data : "do=usercount&" + $("#hidparam").val(),
			success : function(data) {
				$("#recordCount").html("定制总人数：<em style=\"color:red\">"+data+"</em>");
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				closeLoading();
				alert("服务器错误");
			}
		});
	}
	</script>
</body>
</html>