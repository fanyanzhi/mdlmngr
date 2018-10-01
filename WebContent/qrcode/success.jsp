<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<script language="JavaScript" type="text/javascript" src="./js/jquery-1.9.1.min.js"></script>

</head>
<body>
	<div id="xsx" align="center">
		<table border="1">
			<tr>
			<td>username</td>
			<td>usertoken</td>
			</tr>
			<tr>
			<td>${username}</td>
			<td>${usertoken}</td>
			</tr>
		</table>
	</div>
</body>
</html>