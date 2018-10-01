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
			<div id="mdlright">
				<div class="mdlrignav conmendnav">
				<script language="javascript" type="text/javascript" src="js/PopupCalendar.js"></script>
			
					<div class="longsearch">
					<c:if test="${drpappid!=null}">
						<select id="selappid" name="selappid">
							
							<option value="" selected="selected">所有</option>
								<c:forEach items="${drpappid}" var="typeitem">
									<option value="${typeitem.key}" ${typeitem.key==UserBean.appid?'selected':''}>${typeitem.value}</option>
								</c:forEach>						
							
							</select> 
							</c:if>&nbsp;&nbsp;&nbsp;手机号：<input type="text" id="txtPhone" name="txtPhone"  class="searchkey" />&nbsp;&nbsp;用户名：<input type="text" id="txtUserName" name="txtUserName" class="searchkey" />&nbsp;&nbsp;开始时间：<input id="txtStartDate" name="txtStartDate" type="text" class="timetstyle" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);"/>&nbsp;&nbsp;
					               	 结束时间：<input id="txtEndDate" name="txtEndDate" type="text" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" autocomplete="off" class="timetstyle" /><input name="" class="searchbtn" type="button" onclick="UserRegistSearch();" value="查询" />
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
			UserRegistSearch();
		});
		function UserRegistSearch() {
			var keyword = $("#selappid").val();
			var	varcon = "keyword=" + keyword+"&";
			if ($.trim($("#txtPhone").val()).length > 0) {
				varcon = varcon + "phone=" + $.trim($("#txtPhone").val())+"&";
			}
			if ($.trim($("#txtUserName").val()).length > 0) {
				varcon = varcon + "username=" + $.trim($("#txtUserName").val())+"&";
			}
			if ($.trim($("#txtStartDate").val()).length > 0) {
				varcon = varcon + "startdate=" + $.trim($("#txtStartDate").val()) +"&";;
			}
			if ($.trim($("#txtEndDate").val()).length > 0) {
				varcon = varcon + "enddate=" + $.trim($("#txtEndDate").val()) +"&";;
			}
			$("#hidparam").val(varcon);
			getFormDataCount(1);
		}
	</script>
</body>
</html>