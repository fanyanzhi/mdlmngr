<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>CAJViewer云阅读管理系统</title>
<link href="./css/cnkimdl.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript" src="./js/jquery-1.3.2.min.js"></script>
<script language="JavaScript" type="text/javascript" src="./js/mdl.js"></script>
</head>

<body>
	<div id="loginhead">
	<c:if test="${flag==0}">
						<img src="images/jigoubanner.jpg?${flag}" />
					</c:if>
					<c:if test="${flag==1 || flag==null}">
						<img src="images/loginbanner.jpg" />
					</c:if>
		
	</div>
	<div id="loginmain">
		<form name="form1" id="form1" action="Login" method="post" onsubmit="return checkLogin()">
			<div class="logincon">
				<div class="loginleft">
					<c:if test="${flag==0}">
						<img src="images/jglefimg.jpg?${flag}" />
					</c:if>
					<c:if test="${flag==1 || flag==null}">
						<img src="images/loginlefimg.jpg?${flag}" />
					</c:if>
				</div>
				<div class="lgoinright">

					<p>
						<label>用户名：</label><input type="text" name="txtUser" id="txtUser" />
					</p>
					<p>
						<label>密&nbsp;&nbsp;&nbsp;&nbsp;码：</label><input type="password" id="txtPwd" name="txtPwd" />
					</p>
					<p>
						<label>验证码：</label><input type="text" id="txtValidate" name="txtValidate" class="validateipt" /><img src="ValidateImg" id="imgValidate" name="imgValidate" onclick="ChangeValidataImga(this);" alt="验证码" title="看不清，请点击图片"  class="validatepic" />
					</p>
					<span><input type="submit" value="" class="logbtn" /></span>
					<p class="wrong">${errmsg}</p>

				</div>

				<div class="clear"></div>
			</div>
		</form>
	</div>
	<div id="loginfoot">同方知网提供技术服务</div>
	<script type="text/javascript">
	$(function() {
		$("#txtUser").focus();
	});
	function checkLogin(){
		if($.trim($("#txtUser").val()).length==0){
			alert("请填写用户名");
			return false;
		}
		if($.trim($("#txtPwd").val()).length==0){
			alert("请填写密码");
			return false;
		}
		if($.trim($("#txtValidate").val()).length==0){
			alert("请填写验证码");
			return false;
		}
		return true;
	}
	
	function ChangeValidataImga(obj) {
		var num = Math.random();
		obj.src = "ValidateImg?" + num;
	}
	/* function openurl(){
		window.location.href ="/mdlmngr/LoadTestHandler?"+RndNum(10);
	}
	function RndNum(n) {
		var rnd = "";
		for ( var i = 0; i < n; i++)
			rnd += Math.floor(Math.random() * 10);
		var bound1 = 0;
		var bound2 = 10;
		rnd += parseInt(Math.random() * (bound1 - bound2) + bound2);
		return rnd;
	} */
	</script>
</body>
</html>