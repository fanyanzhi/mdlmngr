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
		<form id="form1" name="form1" method="post" action="VersionInfo.do" onsubmit="return chkVersionForm();">
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="VersionList.do" class="return">返回</a>
					<div class="rigname">
						<h2>
							<em>版本设置</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="editcomform">
						<ul>
							<li><label class="leb">系统类别： </label> <c:if test="${!isAdd}">
								${VersionBean.client}
								<input type="hidden" value="${VersionBean.client}" name="txtclient" id="txtclient" />
								</c:if> <c:if test="${isAdd}">
									<input type="text" value="" name="txtclient" id="txtclient" class="iptstyle" />
								</c:if> <input type="hidden" name="hidadd" id="hidadd" value="${isAdd?'1':'0'}" /></li>
							<li><label class="leb"> 版本号：</label><input type="text" name="txtversion" id="txtversion" value="${VersionBean.version}" class="iptstyle" /></li>
							<li><label class="leb"> 版本名称：</label><input type="text" name="txtvername" id="txtvername" value="${VersionBean.versionName}" class="iptstyle" /></li>
							<li><label class="leb"> 下载地址：</label>
							<textarea class="comtextarea" name="txtApkUrl" id="txtApkUrl" cols="" rows="">${VersionBean.apkUrl}</textarea></li>
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