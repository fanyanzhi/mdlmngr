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
		<form id="form1" name="form1">
			<div id="mdlright">
				<input type="hidden" id="hidCount" name="hidCount" value="" />
				<input type="hidden" id="hidFlagCount" name="hidFlagCount" value="" />
				<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" /> 
				<input type="hidden" id="hidtabid" name="hidtabid" value="${TableID}" />
				<!-- <input type="hidden" name="hidparam" id="hidparam" value="hidtabid=${TableID}" />
				<input type="hidden" name="hidfunction" id="hidfunction" value="setModuleColumn" /> -->
				<div class="mdlrignav">
					<a href="ModuleList.do" class="return fleft">返回</a>
					<div class="rigname">
						<h2>
							<em>${ModuleName }</em>属性列表
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					
					<!-- <div class="addsure">
						<span style="float: left" id="tipspan">sdfd</span><input name="btnsumit" type="button" value="完  成" class="addsurebtn" onclick="saveFields()" />
					</div>
 -->
				</div>
				
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		$(function() {
			if ($.trim($("#hidtabid").val()).length > 0) {
				getModuleContentList();
			}
		});
	</script>
</body>
</html>