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
		<form id="form1" action="ModuleTypeInfo.do" method="post" onsubmit=" return chkModuleTypeInput();">
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="ModuleList.do" class="return">返回</a>
					<div class="rigname">
						<h2>
							<em>${ModuleName}</em>类别添加
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="addform">
						<input type="hidden" id="hidid" name="hidid" value="${ModuleTypeID}" />
						<input type="hidden" id="hidtabid" name="hidtabid" value="${ModuleID}" />
						<input type="hidden" id="hidtabname" name="hidtabname" value="${ModuleName}" />
						<ul>
							<li><label class="leb"><em class="redword">*</em>类别名称： </label> <input name="txtTypeName" id="txtTypeName" value="${ModuleTypeInfo.typeName}" ${ModuleTypeID!=null?'readonly':''} type="text" onblur="return (chkInputType(this,'tipTypeName','类别名称')&&chkInputLength(this,'tipTypeName',20,'类别名称长度'))" class="iptstyle" /><em id="tipTypeName" class="riginfo" style="display: none">输入正确</em></li>
							<li><label class="leb"><em class="redword">*</em>英文名称：</label> <input name="txtTypeNameEN" id="txtTypeNameEN" value="${ModuleTypeInfo.typeName_EN}" type="text" onblur="return (chkInputType(this,'tipTypeName_EN','类别英文名称')&&chkInputLength(this,'tipTypeName_EN',30,'类别英文名称长度'))" class="iptstyle" /><em id="tipTypeName_EN"  class="wronginfo" style="display: none">输入格式错误</em></li>
							<li><label class="leb"><em class="redword">*</em>中文名称：</label> <input name="txtTypeNameCH" id="txtTypeNameCH" value="${ModuleTypeInfo.typeName_CH}" type="text" onblur="chkInputLength(this,'tipTypeName_CH',25,'类别中文名称长度')" class="iptstyle" /><em id="tipTypeName_CH" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li><label class="leb"> 是否有效：</label> <input name="isValid" id="isValid0" type="radio" value="1" ${ModuleTypeInfo==null?'checked':ModuleTypeInfo.status=='1'?'checked':''} /> <label class="lebrig">是 </label> <input name="isValid" id="isValid1" type="radio" value="0" ${ModuleTypeInfo==null?'':ModuleTypeInfo.status=='0'?'checked':''} /> <label class="lebrig">否 </label></li>
							<li><label class="leb"> 描述：</label> <textarea id="txtDescription"  name="txtDescription" onblur="chkInputLength(this,'tipDescription',200,'描述信息长度')" class="textrig" cols="" rows="">${ModuleTypeInfo.description}</textarea><em id="tipDescription" class="wronginfo" style="display: none">输入格式错误</em></li>
							<li class="passave"><input type="submit" name="subbtn" id="subbtn" type="button" value="确定" class="mdlbtn" /><input type="button"  onclick="document.getElementById('form1').reset();$('.wronginfo').hide();$('.riginfo').hide();" value="重置" class="mdlbtn" /></li>
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