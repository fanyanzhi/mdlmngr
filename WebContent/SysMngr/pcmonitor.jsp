<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
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
			<div id="mdlright">
				<div class="mdlrigcontent" id="mdlrigcon">
					<div class="addform">
						<ul>
							<li><label class="leb">操作系统：</label> ${OSInfo}</li>
							<li><label class="leb">cpu使用：</label> ${CPUInfo}</li>
							<li><label class="leb">物理内存：</label> ${TotalMemory }</li>
							<li><label class="leb">已用物理内存：</label> ${UsedMemory }</li>
							<li><label class="leb">可用物理内存：</label> ${FreeMemory }</li>
							${DeskHtml}
						</ul>
						<p class="prompt">${errmsg}</p>
					</div>

				</div>

			</div>

	</div>
	<!--   main  end  -->
</body>
</html>