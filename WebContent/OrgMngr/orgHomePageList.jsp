<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/userpagetag.js"></script>
<script type="text/javascript" src="./js/simplepagetag.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" />
			<input type="hidden" id="hidpagesize" name="hidpagesize"
				value="${PageSize}" /> <input type="hidden" name="hidparam"
				id="hidparam" value="" /> <input type="hidden" name="hidcount"
				id="hidcount" value="" /> <input type="hidden" name="hidcurpage"
				id="hidcurpage" value="" />
			<div id="mdlright">
				<script language="javascript" type="text/javascript"
					src="js/PopupCalendar.js"></script>
				<div class="mdlrignav">
					<div class="longpadrig">
					<a href="OrgHomePageEdit.do"
							class="addblock fleft">添加</a>
						<div class="longsearch">
							<span style="margin-left: 150px">机构用户名：<input
								id="txtOrg" name="txtOrg" class="searchkey"
								type="text" value="" /></span> <span>开始时间：<input
								id="txtStartDate" name="txtStartDate" type="text"
								class="timetstyle"
								onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" />&nbsp;&nbsp;
								结束时间：<input id="txtEndDate" name="txtEndDate" type="text"
								onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);"
								autocomplete="off" class="timetstyle" /> <input name=""
								class="searchbtn" type="button" onclick="getOrgHomePageList();"
								value="查询" />
							</span>
						</div>

						<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1"
							totalCount="100" onClick="" pageType="simple" />
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata"></div>
					<div class="botopt">
						<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1"
							totalCount="100" onClick="" />
					</div>
				</div>
			</div>
		</form>
	</div>
	<script type="text/javascript">
		$(function() {
			getOrgHomePageList();
		});
		function getOrgHomePageList(){
			var varcon = "";
			if ($.trim($("#txtOrg").val()).length > 0) {
				varcon = varcon + "txtOrg=" + $("#txtOrg").val() +"&";
			}
			if ($.trim($("#txtStartDate").val()).length > 0) {
				varcon = varcon + "txtStartDate=" + $.trim($("#txtStartDate").val()) +"&";
			}
			if ($.trim($("#txtEndDate").val()).length > 0) {
				varcon = varcon + "txtEndDate=" + $.trim($("#txtEndDate").val()) +"&";
			}
			$("#hidparam").val(varcon);
			getFormDataCount(1);
		}
		function deleteOpt(id) {
			if (!confirm("确认要删除？")) {
				return false;
			}
			var curpage = $("#hidcurpage").val();
			if (curpage == undefined) {
				curpage = 1;
			} else {
				curpage = parseInt(curpage, 10);
			}
			$.ajax({
				url : $("#hidurl").val(),
				type : "GET",
				data : "do=deleteOpt&id=" + id,
				async : false,
				cache : false,
				success : function(data) {
					if (data == "1") {
						alert("删除成功");
						getFormDataCount(curpage);
					} else {
						alert("删除失败");
					}
				},
				error : function() {
					alert("服务器错误");
				}
			});
		}
	</script>
</body>
</html>