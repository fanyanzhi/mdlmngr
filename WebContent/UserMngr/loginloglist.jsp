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
<body  onkeydown="if(event.keyCode==13) {LoginLogSearch();return false;}">
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
			<input type="hidden" name="hidcurpage" id="hidcurpage" value="" />
			<div id="mdlright">
				<div class="mdlrignav">
				<c:if test="${LoginObj.role!='3'}">
					<a href="UserAnalysis.do" class="addblock fleft">用户统计</a><a href="UserLoginDayLog.do" class="addblock fleft">每日登陆</a>
					</c:if>
					<div class="search"  style="width: 700px; margin-left: -350px;">
						AppId:<c:if test="${drpappid!=null}">
						<select id="selappid" name="selappid">
							
							<option value="" selected="selected">所有</option>
								<c:forEach items="${drpappid}" var="typeitem">
									<option value="${typeitem.key}" ${typeitem.key==UserBean.appid?'selected':''}>${typeitem.value}</option>
								</c:forEach>						
							</select>
							</c:if>	&nbsp;&nbsp;&nbsp;<input type="text" id="txtUserName" name="txtUserName" class="keyword" />
						<input name="" class="searchbtn" type="button" onclick="LoginLogSearch();" value="查询" />
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
		LoginLogSearch();
	});
	</script>
</body>
</html>