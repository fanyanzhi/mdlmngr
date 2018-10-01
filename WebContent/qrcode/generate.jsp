<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<script language="JavaScript" type="text/javascript" src="./js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="./js/qrcodejs-master/qrcode.js"></script>
</head>
<body>
	<div id="qrcode" align="center"></div>
	<script type="text/javascript">
		new QRCode(document.getElementById("qrcode"), "http://m.cnki.net/mcnki/appdownload.html?vk=${qrcode}&vm=login");
	</script>

	<script type="text/javascript">
	$(function(){
		setInterval(function(){ 
			$.ajax({
	            url: "${ctx}/mdlmngr/users/qrcodelogin",
	            contentType : "application/json",  
	            data: JSON.stringify({"qrcode":"${qrcode}", "clientid":"webtest", "platform":"webtest", "version":"webtest"}),
	            cache: false,
	            type: "POST",
	            success: function(data){
	            	var res = eval("("+data+")");
					if(res.result == false) {
					}else if(res.result == true) {
						//只看validcode
						if(res.validcode==false) {
							alert("二维码过期，需要重新获取二维码");
							window.location.href="http://192.168.103.244:8888/mdlmngr/qrcodegenerate";
						}else if(res.validcode==undefined && res.islogin==true) {
							var usertoken = res.usertoken;
							var username = res.username;
							window.location.href="${ctx}/mdlmngr/qrcodegenerate?sw=success&p1="+usertoken+"&p2="+username;
						}
					}
	            }
			});
		
		},3000)
	});
	</script>
</body>
</html>