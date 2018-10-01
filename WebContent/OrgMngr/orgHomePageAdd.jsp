<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/userpagetag.js"></script>
<script type="text/javascript" src="./js/imgupload.js"></script>
<script type="text/javascript" src="./js/ajaxfileupload.js"></script>
<script type="text/javascript">
	$(function() {
		$("#returnBtn").click(function() {
			window.location.href="OrgHomePageList.do";
		});
		$("#passBtn").click(function() {
			$("#form1").submit();
		});
	});
	function check(){
		if($.trim($("#unitname").val())==""||$.trim($("#unitname").val())==null){
			alert("机构名称不能为空");
			return false;
		}
		if($.trim($("#orgname").val())==""||$.trim($("#orgname").val())==null){
			alert("机构用户名不能为空");
			return false;
		}
		if($.trim($("#weburl").val())==""||$.trim($("#weburl").val())==null){
			alert("首页地址不能为空");
			return false;
		}
	}
</script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1" method="post" action="" onsubmit="return check();">
			<div id="mdlright" style="position: fixed;">
				<div class="mdlrignav">
					<a href="OrgHomePageList.do" class="return">返回</a>
					<div class="rigname">
						<h2>
							<em>添加详情信息</em>
						</h2>
					</div>
				</div>
				<div id="mdlrigcon" class="mdlrigcontent">
					<div class="editcomform">
						<ul>
							<li><label class="leb">机构名称：</label><input style="width: 300px;" type="text" name="unitname" id="unitname" /></li>
							<li><label class="leb">机构用户名：</label><input style="width: 300px;" type="text" name="orgname" id="orgname" /></li>
							<li><label class="leb">首页网址：</label><input style="width: 300px;" type="text" name="weburl" id="weburl" /></li>
							
							<li class="passave" style="margin-top: 50px;">
								<input type="button" id="returnBtn" value="返回" class="mdlbtn" />
								<input type="button" id="passBtn" value="确定" class="mdlbtn" />
							</li>
						</ul>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	
</body>
</html>