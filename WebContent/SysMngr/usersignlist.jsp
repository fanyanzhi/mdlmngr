<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
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
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" />
			<input type="hidden" id="hidpagesize" name="hidpagesize"
				value="${PageSize}" /> <input type="hidden" name="hidparam"
				id="hidparam" value="" /> <input type="hidden" name="hidcount"
				id="hidcount" value="" /> <input type="hidden" name="hidcurpage"
				id="hidcurpage" value="" />
			<div id="mdlright">
				<script language="javascript" type="text/javascript"
					src="js/PopupCalendar.js"></script>
				<div class="mdlrignav">
					<div class="longpadrig">
						<a href="javascript:void(0);" onclick="exportExcel();"
							class="addblock fleft">导数到Excel</a>
						<div class="longsearch">
							<span style="margin-left: 200px">用户名：<input
								id="txtUserName" name="txtUserName" class="searchkey"
								type="text" value="" /></span> <span>开始时间：<input
								id="txtStartDate" name="txtStartDate" type="text"
								class="timetstyle"
								onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" />&nbsp;&nbsp;
								结束时间：<input id="txtEndDate" name="txtEndDate" type="text"
								onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);"
								autocomplete="off" class="timetstyle" /> 连续签到天数大于：<input
								type="text" id="txtscount" name="txtscount" class="searchkey"
								style="width: 60px" />&nbsp;&nbsp; 累计签到天数大于：<input type="text"
								id="txtssum" name="txtssum" class="searchkey"
								style="width: 60px" />&nbsp;&nbsp; <input name=""
								class="searchbtn" type="button" onclick="getUserSignContext();"
								value="查询" />
							</span>
						</div>

						<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1"
							totalCount="100" onClick="" pageType="simple" />
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata"></div>
					<div class="botopt">
						<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1"
							totalCount="100" onClick="" />
					</div>
				</div>
			</div>
		</form>
	</div>
	<script type="text/javascript">
		$(function() {
			getUserSignContext();

		});
		function getUserSignContext() {
			var varcon = "";
			varcon += "txtUserName=" + $("#txtUserName").val() + "&"
					+ "txtStartDate=" + $("#txtStartDate").val() + "&"
					+ "txtEndDate=" + $("#txtEndDate").val() + "&txtssum="
					+ $("#txtssum").val() + "&txtscount="
					+ $("#txtscount").val();
			$("#hidparam").val(varcon);
			getFormDataCount(1);
		}

		function exportExcel() {
			var varcon = "";
			varcon += "txtUserName=" + $("#txtUserName").val() + "&"
					+ "txtStartDate=" + $("#txtStartDate").val() + "&"
					+ "txtEndDate=" + $("#txtEndDate").val() + "&txtssum="
					+ $("#txtssum").val() + "&txtscount="
					+ $("#txtscount").val();
			window.open($("#hidurl").val() + "?do=excel&"
					+ $("#hidparam").val());
		}
	</script>
</body>
</html>