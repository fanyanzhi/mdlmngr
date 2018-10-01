<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc" %>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html" %>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html" %>
		<div id="mdlright">
		<div class="mdlrignav"> <a href="ModuleList.do" class="return fleft">返回</a></div>
			<div class="mdlrigcontent" id="mdlrigcon">
			<div id="divdata">
			</div>
			</div>
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}"/>
			
		</div>
	</div>
	<!--   main  end  -->
<script type="text/javascript">
	$(function(){
		getRecycleModuleList();
	});
</script>
</body>
</html>