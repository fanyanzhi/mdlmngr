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
				<a href="VersionInfo.do" class="addblock fleft">添加信息</a>
					<div class="rigname">
						<h2>
							<em>版本号管理</em>
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
		getVersionList();
	});
	</script>
</body>
</html>