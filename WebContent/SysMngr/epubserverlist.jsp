<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
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
				<a href="EpubServerInfo.do" class="addblock fleft">添加服务器</a>
					<div class="rigname">
						<h2>
							<em>epub服务器监控</em>
						</h2>
						<span class="refresh" title="刷新" onclick="getEpubServerStatus();"></span>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
						<div id="divdata">
						${ServerHtml }
						</div>
				</div>

			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function(){
		getEpubServerStatus();
	});
	</script>
</body>
</html>