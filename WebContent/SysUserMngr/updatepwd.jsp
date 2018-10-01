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
		<form id="form1" name="form1" action="UpdatePwd.do" method="post" onsubmit="return chkPassword()">
				<input type="hidden" id="hiduid" name="hiduid" value="${UserBean.id}" />
			<div id="mdlright">
				<div class="mdlrignav">
				<div class="rigname">
						<h2>
							<em>密码修改</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div class="addform">
						<ul>
							<li><label class="leb"><em class="redword">*</em>原始密码：</label> <input type="password" id="txtOldPwd" name="txtOldPwd" class="iptstyle" value="${oldPwd}" /><em id="tipOldPwd" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li><label class="leb"><em class="redword">*</em>新密码：</label> <input type="password" id="txtNewPwd" name="txtNewPwd" class="iptstyle" value="${newPwd}" onblur="return chkInputLength(this,'tipNewPwd',32,'新密码长度');" /><em id="tipNewPwd" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li><label class="leb"><em class="redword">*</em>确认新密码：</label> <input type="password" id="txtConfirmNewPwd" name="txtConfirmNewPwd" class="iptstyle" value="${newPwd}"  onblur="if($.trim($('#txtConfirmNewPwd').val()).length>0){if ($.trim($('#txtNewPwd').val()) != $.trim($('#txtConfirmNewPwd').val())) {$('#tipConfirmNewPwd').html('确认新密码和新密码不相等');$('#tipConfirmNewPwd').attr('class', 'wronginfo');$('#tipConfirmNewPwd').show();}else{$('#tipConfirmNewPwd').attr('class', 'riginfo');$('#tipConfirmNewPwd').show();}}" /><em id="tipConfirmNewPwd" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li class="passave"><input type="submit" value="确定" class="mdlbtn" /><input type="button" onclick="$('#txtOldPwd').val('');$('#txtNewPwd').val('');$('#txtConfirmNewPwd').val('');$('#tipOldPwd').css('display','none');$('#tipNewPwd').css('display','none');$('#tipConfirmNewPwd').css('display','none');" value="重置" class="mdlbtn" /></li>
						</ul>
						<p class="prompt">${errmsg}</p>
					</div>

				</div>

			</div>
		</form>

	</div>
	<!--   main  end  -->
</body>
</html>