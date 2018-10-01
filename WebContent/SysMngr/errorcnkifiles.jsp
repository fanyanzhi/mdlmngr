<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
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
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" /> <input type="hidden" name="hidparam" id="hidparam" value="" /> 
			<input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" />
			 <input type="hidden" name="hidcount" id="hidcount" value="" /> <input type="hidden" name="hidcurpage" id="hidcurpage" value="" />
			<div id="mdlright">
				<div class="mdlrignav conmendnav">
					<a href="javascript:void(0)" onclick="doErrorFile();" class="addblock fleft">手动处理</a>

					<div class="commendsearch errorsearch">
						<div class="bigsearchblock searchblockdetail">
							<usertag:DropDownList width="70" value="${drpSeaType}" id="ddlTypeName" />
							<input name="txtKeyWord" id="txtKeyWord" type="text" class="keynoborder2" />
							
						</div>
						<span class="fleft"><label>筛选范围：</label>
<select id="ddlErrorType" class="comselt"><option value="">全部</option><option value="1">原文错误</option><option value="2">OData错误</option></select>
</span> 
						<input name="" class="searchbtn" type="button" value="查询" onclick="getErrorFileList()" />
					</div>
					<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple" />
					<div class="rigname">
						<h2>
							<em>出错文件列表</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata"></div>
					<div class="botopt">
						<div class="botleftopt">
							<input name="selectAll" id="selectAll" onclick="checkall('taberrorfile',this)" type="checkbox" value="" /><a href="javascript:void(0);" onclick="delMultiErrorFile()" class="delall">删除</a>
						</div>
						<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
					</div>
				</div>

			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		$(function() {
			getErrorFileList();
		});
		function doErrorFile() {
			showLoading();
			$.ajax({
				type : "POST",
				url : "BackgroundProcess/examineerrorfile",
				cache : false,
				async : true,
				success : function(data) {
					closeLoading();
					alert("处理完成");
					window.location.reload();
				},
				error : function() {
					closeLoading();
					alert("数据加载失败！");
				}
			});
		}
		
		
	</script>
</body>
</html>