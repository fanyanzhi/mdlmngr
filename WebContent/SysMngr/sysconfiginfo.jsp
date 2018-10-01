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
		<form id="form1" name="form1" method="post" action="" onsubmit="return chkSysInfoForm();">
			<input type="hidden" id="hidsid" name="hidsid" value="${HidID }" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="SysConfigList.do" class="return">返回</a>
					<div class="rigname">
						<h2>
							<em>系统设置</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="addform">
						<ul>
							<li><label class="leb">属性名称： </label><input type="text" value="${TxtName }" id="txtProName" name="txtProName"  class="iptstyle"  /></li>
							<li><label class="leb">属性值：</label> <textarea class="textrig" name="txtProVal" id="txtProVal" cols="" rows="">${TxtVal }</textarea></li>
							<li class="passave"><input name="" type="submit" value="确定" class="mdlbtn" /><input name="" type="button" onclick="$('#txtProName').val('');$('#txtProVal').val('');" value="重置" class="mdlbtn" /></li>
						</ul>
						<p id="prompt" class="prompt">${errmsg}</p>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
</body>
</html>