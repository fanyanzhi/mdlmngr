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
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" />
			
			<input type="hidden" name="hidtheparam" id="hidtheparam" value="${StrParam}" />

			<input type="hidden" name="hidparam" id="hidparam" value="" /> 
			<input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> 
			<input type="hidden" name="hidcount" id="hidcount" value="" />
			<input type="hidden" name="hidfunction" id="hidfunction" value="setAnalysisData" />
			 <input type="hidden" name="hidfunempty" id="hidfunempty" value="setAnalysisEmpty" />
			 <input type="hidden" name="hidcurpage" id="hidcurpage" value="" />

			<div id="mdlright">
				<div class="mdlrignav">
					<a href="StatisticAnalysis.do" class="fleft return">返回</a>
					<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple"/>
					<span class="rigorder">
					<h2 id="tiptotal" class="total">${TipMsg}</h2>
					 <a href="javascript:void(0);" id="logintime0" class="orderbtndoit">登录时间</a> <a href="javascript:void(0);" id="logincount0" class="orderbtn">登录次数</a>
					</span>
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
	<script type="text/javascript">
	$(function(){
		$("#logintime0").bind("click",function(){getstatistican(0,1);});
		$("#logincount0").bind("click",function(){getstatistican(1,1);});
		getstatistican(0,1);
	});
	
	</script>
</body>
</html>