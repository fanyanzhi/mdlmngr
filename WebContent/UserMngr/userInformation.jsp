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
						<div class="longsearch">
							<span style="margin-left: 150px">用户名：<input
								id="txtUserName" name="txtUserName" class="searchkey"
								type="text" value="" /></span>
							<span  style="margin-left: 10px;">手机号：<input
								id="txtMobile" name="txtMobile" class="searchkey"
								type="text" value="" />
								<input name=""
								class="searchbtn" type="button" onclick="getUserInfomationContext();"
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
			getUserInfomationContext();

		});
	</script>
</body>
</html>