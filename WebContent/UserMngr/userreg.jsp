<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h1>欢迎你，代号为 " + ${openid} + " 的用户!</h1>
<br />
<br />
<!-- infor_input -->
<LABEL for=email>你的电子邮箱</LABEL> <INPUT onkeydown=RegKeyDown() id=txtEmail name=txtEmail> <SPAN style="COLOR: red" id=err_email></SPAN><BR>
<LABEL for=password>设置中国知网密码</LABEL> <INPUT onkeydown=RegKeyDown() id=txtPassword name=txtPassword value="" type=password> <SPAN style="COLOR: red" id=err_password></SPAN><BR>
<LABEL for=password_again>再输入一遍密码</LABEL> <INPUT onkeydown=RegKeyDown() id="txtPasswordAgain" name="txtPasswordAgain" value="" type="password"> <SPAN style="COLOR: red" id="err_passwordagain"></SPAN><BR>
<INPUT id="btnReg" language="javascript" class="infor_button" onclick=" return checkReg();" name=btnReg type=submit> 
<DIV id=suggestion class="suggestion"><SPAN class="red">重要</SPAN>！请填写<SPAN class="red">有效的邮箱地址</SPAN>此邮箱地址即是你的中国知网用户名</DIV>

<br />
<br />
<hr>
<!-- k_main -->
<H3>已有知网会员账号？直接绑定吧：</H3><LABEL for=username>用户名：</LABEL> <INPUT onkeydown=BindKeyDown() id=txtUserName name=txtUserName><BR><LABEL for=k_password>密&nbsp;&nbsp;码：</LABEL> <INPUT onkeydown=BindKeyDown() id=txtLoginPassword name=txtLoginPassword value="" type=password><BR><INPUT id=btnBind language=javascript class=k_botton onclick="return checkbind();this.disabled=true;__doPostBack('btnBind','');" name=btnBind type=submit> 
<H4>绑定后您的新浪微博或知网账号均可登录中国知网</H4>

<!-- 可以直接登录 -->

</body>
</html>