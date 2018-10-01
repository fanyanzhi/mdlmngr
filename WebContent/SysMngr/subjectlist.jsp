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
<body>
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
				<a href="SubjectRecommend.do" class="addblock fleft">推荐主题</a>
					<div class="rigname">
						<h2>
							<em>主题列表</em>
						</h2>
					</div>
					<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple"/>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata"></div>
					<div class="botopt">
					<!-- <div class="botleftopt"> <input name="selectAll" id="selectAll" onclick="checkall('tabnotice',this)" type="checkbox" value=""/><a href="javascript:void(0);" onclick="delAll()"  class="delall">删除</a> </div> -->
					<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function() {
		getFormDataCount(1);
	});
	
	function delSubjectInfo(id,simageid,bimageid){
		jQuery.ajax({
			url : "/mdlmngr/SubjectListHandler.do",
			type : "get",
			dataType : "json",
			contentType: "application/x-www-form-urlencoded;charset=utf-8", 
			data : "do=del&id="+id+"&simageid="+simageid+"&bimageid="+bimageid,
			success : function(data) {
				if(data==true){
					alert("删除成功");
					location.reload();
				}else{
					alert("删除失败");
				}
			},
			error : function(XMLHttpRequest, textStatus,
					errorThrown) {
				alert("服务器错误");
			}
		});
	}
	/* function delAll() {
		var ids = "";
		$("input[name='chknoteid']").each(function() {
			if ($(this).prop("checked") == true)
				ids = ids + $(this).val() + ",";
		});
		alert(ids);
		if (ids == "") {
			alert("请选择要删除的主题！");
			return false;
		}
		delSubjectList(ids);
	}
	function delSubjectList(ids) {
		if (ids.length == 0) {
			return false;
		}
		if (!confirm("你确定要删除该主题吗")) {
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
			data : "do=delSubs&ids=" + ids,
			success : function(data) {
				if (data == "1") {
					alert("删除成功");
					location.reload();
				} else {
					alert("删除失败");
				}
			},
			error : function() {
				alert("加载失败");
			}
		});
	} */
	</script>
</body>
</html>