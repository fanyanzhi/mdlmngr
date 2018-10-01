<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/userpagetag.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidtid" name="hidtid" value="${TypeID }" /> <input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL }" /> 
			<input type="hidden" id="hidseacount" name="hidseacount" value="0" /> 
			<input type="hidden" id="hiddisplaycount" name="hiddisplaycount" value="0" /> 
			<input type="hidden" id="hidordercount" name="hidordercount" value="0" /> 
			<input type="hidden" id="hidparam" name="hidparam" value="" />
			<input type="hidden" id="hidfunction" name="hidfunction" value="setSourceFields" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="SourceTypeList.do" class="return fleft">返回</a>
					<div class="rigname">
						<h2>
							<em>资源管理</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div class="sourseadd">
						<ul>
							<li><label>中文名称：</label><input id="txtFieldNameCH" name="txtFieldNameCH" type="text" class="nameip" value="${FieldNameCH}" /><label>英文名称：</label><input name="txtFieldNameEN" id="txtFieldNameEN" type="text" class="nameip" value="${FieldNameEN}" /><label>OData2.0映射：</label><input name="txtNewName" id="txtNewName" type="text" class="nameip" value="${RelationOData}" /></li>
							<li class="valueadd"><a id="aseafield" href="javascript:void(0);" onclick="$('#tabseafield').show();$('#aseafield').attr('class','current');$('#tabdisplayfield').hide();$('#adisplayfield').attr('class','');$('#taborderfield').hide();$('#aorderfield').attr('class','');" class="current">搜素属性</a> <a id="adisplayfield" href="javascript:void(0)" onclick="$('#tabdisplayfield').show();$('#adisplayfield').attr('class','current');$('#tabseafield').hide();$('#aseafield').attr('class','');$('#taborderfield').hide();$('#aorderfield').attr('class','');">显示属性</a>
												<a id="aorderfield" href="javascript:void(0);" onclick="$('#taborderfield').show();$('#aorderfield').attr('class','current');$('#tabdisplayfield').hide();$('#adisplayfield').attr('class','');$('#tabseafield').hide();$('#aseafield').attr('class','');">排序字段</a></li>
						</ul>
						<div class="clear"></div>
					</div>
						<div id="divdata">
						<table id="tabseafield" width="100%" border="0" cellspacing="0" cellpadding="0" class="tabone">
							<tr>
								<th width="60">&nbsp;</th>
								<th width="40" class="num">&nbsp;</th>
								<th width="30%">中文名称</th>
								<th width="30%">英文名称</th>
								<th>操作</th>
							</tr>
							<tr class="tabtotopt">
								<td></td>
								<td class="num ">+</td>
								<td colspan="3"><a href="javascript:void(0);" onclick="addtabrow('tabseafield');" class="addnewtd">新增</a></td>
							</tr>
						</table>
						
						<table id="tabdisplayfield" style="display: none" width="100%" border="0" cellspacing="0" cellpadding="0" class="tabone">
							<tr>
								<th width="60">&nbsp;</th>
								<th width="40" class="num">&nbsp;</th>
								<th width="30%">中文名称</th>
								<th width="30%">英文名称</th>
								<th>操作</th>
							</tr>
							<tr class="tabtotopt">
								<td></td>
								<td class="num ">+</td>
								<td colspan="3"><a href="javascript:void(0);" onclick="addtabrow('tabdisplayfield');" class="addnewtd">新增</a></td>
							</tr>
						</table>
						
						<table id="taborderfield" style="display: none" width="100%" border="0" cellspacing="0" cellpadding="0" class="tabone">
							<tr>
								<th width="60">&nbsp;</th>
								<th width="40" class="num">&nbsp;</th>
								<th width="30%">中文名称</th>
								<th width="30%">英文名称</th>
								<th>操作</th>
							</tr>
							<tr class="tabtotopt">
								<td></td>
								<td class="num ">+</td>
								<td colspan="3"><a href="javascript:void(0);" onclick="addtabrow('taborderfield');" class="addnewtd">新增</a></td>
							</tr>
						</table>
						</div>
						<div class="addsure">
							<input name="" type="button" value="完  成" onclick="saveSourceFields()" class="addsurebtn" />
						</div>
				</div>

			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		$(function() {
			if ($.trim($("#hidtid").val()).length > 0) {
				getSourceFields();
			}
		});
		
	</script>
</body>
</html>