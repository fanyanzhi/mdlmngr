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
			<input type="hidden" id="hidfunction" name="hidfunction" value="setRemSourceTypeOrder" />
			<input type="hidden" name="hidparam" id="hidparam" value="" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="RecommendTypeList.do" style="float:left" class="addblock">编辑分类</a>
					<div class="search">
						AppID：
							<select style="width: 150px; height: 28px; line-height: 28px; text-indent: 10px;font-size:15px;" id="selappid" name="selappid"  onchange="getRemSourceTypeOrder()">
							<c:if test="${drpappid!=null}">
								<c:forEach items="${drpappid}" var="typeitem">
									<option value="${typeitem.key}" ${typeitem.key==UserBean.appid?'selected':''}>${typeitem.value}</option>
								</c:forEach>						
							</c:if>	
							</select>
					</div>
					<div class="rigname">
						<h2>
							<em>精品推荐</em>
						</h2>
					</div>

				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata"></div>
					<div id="divaddsure" class="addsure" style="display:none">
						<input name="" type="button" value="完 成" onclick="saveRecomdSourceType()" class="addsurebtn" />
					</div>
				</div>
			</div>

		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		$(function() {
			getRemSourceTypeOrder();
		});
		
	</script>
</body>
</html>