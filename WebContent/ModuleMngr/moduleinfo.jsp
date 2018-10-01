<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc" %>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html" %>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html" %>
		<form action="ModuleInfo.do" method="post" id="form1" onsubmit=" return chkModuleInfoInput();">
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="ModuleList.do" class="return">返回</a>
				</div>
				<div class="mdlrigcontent">
					<div class="addform">
						<input type="hidden" id="hidid" name="hidid" value="${ModuleID}" />
						<ul>
							<li><label class="leb"><em class="redword">*</em>模块名称： </label> <input name="txtModuleName" id="txtModuleName" value="${ModuleInfo.tableName}" ${ModuleID!=null?'readonly':''} onblur="return (chkInputType(this,'tipModuleName','模块名称')&&chkInputLength(this,'tipModuleName',20,'模块名称长度'))" type="text" class="iptstyle" /><em id="tipModuleName" class="riginfo" style="display: none">输入正确</em></li>
							<li><label class="leb"><em class="redword">*</em>英文名称：</label> <input name="txtModuleNameEN" id="txtModuleNameEN" value="${ModuleInfo.tableName_EN}" type="text" onblur="return (chkInputType(this,'tipModuleName_EN','英文名称')&&chkInputLength(this,'tipModuleName_EN',30,'英文名称长度'))" class="iptstyle" /><em id="tipModuleName_EN" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li><label class="leb"><em class="redword">*</em>中文名称：</label> <input name="txtModuleNameCH" id="txtModuleNameCH" value="${ModuleInfo.tableName_CH}" type="text" onblur="chkInputLength(this,'tipModuleName_CH',25,'中文名称长度')" class="iptstyle" /><em id="tipModuleName_CH" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li><label class="leb"> 是否有效：</label> <input name="isValid" id="isValid0" type="radio" value="1" ${ModuleInfo==null?'checked':ModuleInfo.status=='1'?'checked':''} /> <label class="lebrig">是 </label> <input name="isValid" id="isValid1" type="radio" value="0" ${ModuleInfo==null?'':ModuleInfo.status=='0'?'checked':''} /> <label class="lebrig">否 </label></li>
							<li><label class="leb"> 是否显示：</label> <input name="isDisplay" id="isDisplay0" type="radio" value="true" ${ModuleInfo==null?'checked':ModuleInfo.isDisplay?'checked':''} /> <label class="lebrig">是 </label> <input name="isDisplay" id="isDisplay1" type="radio" value="false" ${ModuleInfo==null?'':ModuleInfo.isDisplay=='0'?'checked':''} /> <label class="lebrig">否 </label></li>
							<li><label class="leb"> 描述：</label> <textarea id="txtDescription" name="txtDescription" class="textrig" name="" onblur="chkInputLength(this,'tipDescription',200,'描述信息长度')" cols="" rows="">${ModuleInfo.description }</textarea><em id="tipDescription" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li class="passave"><input type="submit" name="subbtn" id="subbtn" type="button" value="确定" class="mdlbtn" /><input type="button" onclick="document.getElementById('form1').reset();$('.wronginfo').css('display','none');$('.riginfo').css('display','none');" value="重置" class="mdlbtn" /></li>
						</ul>
						<p id="prompt" class="prompt">${errmsg}</p>
					</div>
					<c:if test="${ModuleID!=null}">
					<span onclick="window.location.href='ModuleContentInfo.do?tabid=${ModuleID}&tabname=${ModuleInfo.tableName_CH}';" class="adddatabtn">属性</span>
					</c:if>
				</div>
			</div>
		</form>

	</div>
	<!--   main  end  -->
</body>
</html>