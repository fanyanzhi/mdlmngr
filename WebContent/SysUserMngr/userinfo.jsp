<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
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
		<form id="form1" name="form1" action="UserInfo.do" method="post" onsubmit="return chkUserInfo()">
				<input type="hidden" id="hiduid" name="hiduid" value="${UserBean.id}" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="UserList.do" class="return">返回</a>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div class="addform">
						<ul>
							<li><label class="leb"><em class="redword">*</em>用户名：</label> <input type="text" id="txtUser" name="txtUser" onblur="return (chkInputType(this,'tipUser','用户名')&&chkInputLength(this,'tipUser',30,'用户名'))" class="iptstyle" value="${UserBean.userName}" ${UserBean==null?'':UserBean.id==0?'':'readonly'} /><em id="tipUser" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li><label class="leb"><em class="redword">*</em>密码：</label> <input type="password" id="txtPwd" name="txtPwd" onblur="return chkInputLength(this,'tipPwd',32,'密码长度');" class="iptstyle" value="${UserBean.password}" /><em id="tipPwd" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li><label class="leb"><em class="redword">*</em>确认密码：</label> <input type="password" id="txtConfirmPwd" name="txtConfirmPwd" class="iptstyle" value="${UserBean.password}" onblur="if($.trim($('#txtConfirmPwd').val()).length>0){if ($.trim($('#txtPwd').val()) != $.trim($('#txtConfirmPwd').val())) {$('#tipConfirmPwd').html('确认密码和密码不相等');$('#tipConfirmPwd').attr('class', 'wronginfo');$('#tipConfirmPwd').show();}else{$('#tipConfirmPwd').attr('class', 'riginfo');$('#tipConfirmPwd').show();}}"/><em id="tipConfirmPwd" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li><label class="leb"><em class="redword">*</em>用户角色：</label> <input name="isAdmin" id="isAdmin2" type="radio" value="2" ${UserBean==null?'checked':UserBean.role==2?'checked':''} /> <label class="lebrig">普通用户 </label> <input name="isAdmin" id="isAdmin3" type="radio" value="3" ${UserBean==null?'':UserBean.role==3?'checked':''} /> <label class="lebrig">机构馆用户 </label><input name="isAdmin" id="isAdmin1" type="radio" value="1" ${UserBean==null?'':UserBean.role==1?'checked':''} /> <label class="lebrig">管理员 </label></li>
							<li><label class="leb"><em class="redword">*</em>AppID：</label>
							<select id="selappid" name="selappid">
							<c:if test="${drpappid!=null}">
								<c:forEach items="${drpappid}" var="typeitem">
									<option value="${typeitem.key}" ${typeitem.key==UserBean.appid?'selected':''}>${typeitem.value}</option>
								</c:forEach>						
							</c:if>	
							</select></li>
							<li><label class="leb"><em class="redword"></em>备注：</label> <textarea class="comtextarea" name="txtComment" id="txtComment" cols="" rows="">${UserBean.comment}</textarea></li>
							<li class="passave"><input type="submit" value="确定" class="mdlbtn" /><input type="button" onclick="document.getElementById('form1').reset();$('.wronginfo').css('display','none');$('.riginfo').css('display','none');" value="重置" class="mdlbtn" /></li>
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