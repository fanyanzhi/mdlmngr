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
<script type="text/javascript" src="./js/ajaxfileupload.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" />
			<input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> 
			<input type="hidden" id="hidsubjectid" name="hidsubjectid" value="${subjectid}" /> 
			<input type="hidden" id="hidsubjectname" name="hidsubjectname" value="${subjectname}" /> 
			<input type="hidden" name="hidparam" id="hidparam" value="" /> 
			<input type="hidden" name="hidcount" id="hidcount" value="" />
			<input type="hidden" name="hidcurpage" id="hidcurpage" value="" />
			<div id="mdlright">
			<script language="javascript" type="text/javascript" src="js/PopupCalendar.js"></script>
				<div class="mdlrignav">
					<div class="longpadrig">
						<div class="longsearch">
							<label class="leb">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="file" id="orgfile" name="orgfile"/> </label><input type="button" onclick="importCsv();" class="uptpicdo" name="showorg" value="导入用户" />
						</div>
				
						<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple"/>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata">
					</div>
					<div class="botopt">
					<div class="botleftopt"> <input name="selectAll" id="selectAll" onclick="checkall('tabsubjectorg',this)" type="checkbox" value=""/><a href="javascript:void(0);" onclick="delMultiSubjectOrg()"  class="delall">删除</a> </div>
					<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
					</div>
				</div>
			</div>
		</form>
	</div>
	<script type="text/javascript">
	$(function(){
		getOrgSubject();
		
	});
	function getOrgSubject(){
		var varcon = "";
		varcon += "subjectname="+encodeURI($("#hidsubjectname").val())+"&subjectid=" + $("#hidsubjectid").val();
		$("#hidparam").val(varcon);
		getFormDataCount(1);
	}
	
	function importCsv() {
		if ($("#orgfile").val() == "") {
			alert("请选择需要导入的文件！");
			return;
		}
		if(!(/(?:csv)$/i.test($("#orgfile").val()))) { 
			alert("只允许导入cvs的数据"); 
			return;
		}
		$.ajaxFileUpload({
			url : 'OrgSubject.do',
			type: 'post',
            data: { subjectid: $("#hidsubjectid").val()}, 
			secureuri : false,
			fileElementId : 'orgfile',
			dataType : 'json',
			success : function(data, status) {
				alert(data.msg);
				window.location.reload();
			},
			error : function(data, status, e) {
				alert("导入机构用户失败。");
			}
		});
		return false;
	}
	
	function delSubjectOrg(orgname) {
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
			data : "do=delorg&orgname=" + orgname + "&subjectid="+$("#hidsubjectid").val(),
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
	function delMultiSubjectOrg() {
		var vFileID = "";
		$("input[name='chkorgname']").each(function() {
			if ($(this).prop("checked") == true)
				vFileID = vFileID + $(this).val() + ",";
		});
		if (vFileID == "") {
			alert("请选择要删除的机构！");
			return false;
		}
		delSubjectOrg(vFileID);
	}
	
	
	</script>
</body>
</html>