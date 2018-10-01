<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
</head>
<body onkeydown="if(event.keyCode==13) {getRecommendList();return false;}">
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1" method="post" action="" onsubmit="if($.trim($('#keyword').val()).length>300){alert('过滤文字不要超过300字');$('#keyword').focus();return false;}">
			<input type="hidden" name="hidid" id="hidid" value="${WordsID}" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="CommentList.do" class="return">返回</a>
					<div class="rigname">
						<h2>
							<em>过滤设置</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="editcomform">
						<ul>
							<li>输入过滤的文字，以“；”号隔开</li>

							<li><textarea class="flitertarea" name="keyword" id="keyword" cols="" rows="">${FilterWords }</textarea></li>
							<li class="passave"><input name="" type="submit" value="确定" class="mdlbtn" /><input name="" type="reset" value="取消" class="mdlbtn" /></li>
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