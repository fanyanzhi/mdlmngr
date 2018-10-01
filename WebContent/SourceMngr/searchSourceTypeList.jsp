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
			<input type="hidden" id="hidfunction" name="hidfunction" value="setRemSourceTypeList" />
			<div id="mdlright">
				<div class="mdlrignav"> <a href="SearchSourceTypeOrder.do" class="return fleft">返回</a>
					<div class="rigname">
						<h2>
							<em>精品推荐</em>
						</h2>
					</div>

				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata"></div>
					<div id="divaddsure" class="addsure" style="display:none"><input name="" type="button" value="保 存" onclick="addRecomdSourceType()" class="addsurebtn"  /></div>
				</div>
			</div>

		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		$(function() {
			getRemSourceTypeList();
		});
		
	</script>
</body>
</html>