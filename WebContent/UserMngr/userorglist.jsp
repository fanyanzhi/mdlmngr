<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/userpagetag.js"></script>
<script type="text/javascript" src="./js/simplepagetag.js"></script>
</head>
<body  onkeydown="if(event.keyCode==13) {LoginLogSearch();return false;}">
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" />
			<input type="hidden" name="hidparam" id="hidparam" value="" /> 
			<input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> 
			<input type="hidden" name="hidcount" id="hidcount" value="" />
			<input type="hidden" name="hidcurpage" id="hidcurpage" value="" />
			<div id="mdlright">
				<div class="mdlrignav">
					<div class="longsearch">
					用户名：<input type="text" id="txtUserName" name="txtUserName" class="keyword" />
						&nbsp;&nbsp;&nbsp;机构名称：<input id="txtUnitName" name="txtUnitName" type="text" class="searchkey" />
						&nbsp;&nbsp;&nbsp;机构账号：<input type="text" id="txtOrgName" name="txtOrgName" class="searchkey" style="width:90px" />
						<input name="" class="searchbtn" type="button" onclick="getuserorglist();" value="查询" />
					</div>
					<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple"/>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
				<div id="divdata">
				</div>
				<div class="botopt">
				<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
				</div>
				</div>
				
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function() {
		getuserorglist();
	});
	function getuserorglist(){
		var varcon = "";
		if ($.trim($("#txtUserName").val()).length > 0) {
			varcon = varcon + "uname=" + $.trim($("#txtUserName").val()) +"&";;
		}
		if ($.trim($("#txtUnitName").val()).length > 0) {
			varcon = varcon + "unitname=" + encodeURI($.trim($("#txtUnitName").val())) +"&";;
		}
		if ($.trim($("#txtOrgName").val()).length > 0) {
			varcon = varcon + "org=" + $.trim($("#txtOrgName").val()) +"&";;
		}
		$("#hidparam").val(varcon);
		getFormDataCount(1);
	}
	
	function delUserOrg(recid) {
		if (logid.length == 0) {
			return false;
		}
		if (!confirm("你确定要删除该记录吗")) {
			return false;
		}
		var curpage = $("#hidcurpage").val();
		if (curpage == undefined) {
			curpage = 1;
		} else {
			curpage = parseInt(curpage, 10);
		}
		$.ajax({
			type : "get",
			url : $("#hidurl").val(),
			async : true,
			cache : false,
			data : "do=delorg&recid=" + recid,
			success : function(data) {
				if (data == "1") {
					alert("删除成功");
					getFormDataCount(curpage);
				} else {
					alert("删除失败");
				}
			},
			error : function() {
				alert("加载失败");
			}
		});
	}
	function delMultiOrg() {
		var vLogID = "";
		$("input[name='chklogid']").each(function() {
			if ($(this).prop("checked") == true)
				vLogID = vLogID + $(this).val() + ",";
		});
		if (vLogID == "") {
			alert("请选择要删除的日志记录！");
			return false;
		}
		delUserOrg(vLogID);
	}
	</script>
</body>
</html>