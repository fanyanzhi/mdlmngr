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
<body >
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1" method="post" onsubmit="return submitWord();">
			<div id="mdlright">
				<div class="mdlrignav">
					<div class="rigname">
						<h2>
							<em>热词设置</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
				<input type="hidden" id="hidid" name="hidid" value="${hid}"/>
					<div class="editcomform">
						<ul>
							<li>类型：<input type="text" class="iptstyle" id="txtType" name="txtType" value="${type}" /></li>
							<li>输入热词，以“；”号隔开</li>

							<li><textarea class="flitertarea" name="keyword" id="keyword" cols="" rows="">${hotword}</textarea></li>
							<li style="color:red;font-size:12px;">${errmsg }</li>
							<li class="passave"><input name="" type="submit" value="确定" class="mdlbtn" /><input name="" id="resetword" type="button" onclick="resetWord()" value="取消" class="mdlbtn" /></li>
						</ul>
					</div>
				</div>

			</div>
			</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		function resetWord(){
			$("#keyword").val("");
		}
		function submitWord(){
			 var hidid = $("#hidid").val();
			 var keyword = $("#keyword").val();
			 if(keyword==null||keyword==""){
				 alert('请填写热词！');
				 return false;
			 }
			 if($.trim($('#keyword').val()).length>100){
				 alert('热词文字不要超过100字');
				 $('#keyword').focus();
				 return false;
			}
			 return true;
		}
	</script>
	
</body>
</html>