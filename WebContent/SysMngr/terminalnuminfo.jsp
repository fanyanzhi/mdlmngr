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
		<form id="form1" name="form1" method="post" action="" onsubmit="return chkTerminalForm();">
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="TerminalNumList.do" class="return">返回</a>
					<div class="rigname">
						<h2>
							<em>系统终端数设置</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="addform">
						<ul>
							<li><label class="leb">系统类别： </label>
							<input type="text" value="${Client}" name="txtclient" id="txtclient" class="iptstyle"/>
							<input type="hidden" name="hidternimalid" id="hidternimalid" value="${TerminalID}" />
							</li>
							<li><label class="leb"> 终端数：</label><input type="text" name="txtTenimalNum" id="txtTenimalNum" value="${TeminalNum}" class="iptstyle"/></li>
							<li class="passave"><input name="" type="submit" value="确定" class="mdlbtn" /><input type="reset" value="重置" class="mdlbtn" /></li>
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