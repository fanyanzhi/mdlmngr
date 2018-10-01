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
<script type="text/javascript" src="./js/simplepagetag.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" /> <input type="hidden" name="hidparam" id="hidparam" value="" /> <input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> <input type="hidden" name="hidcount" id="hidcount" value="" /> <input type="hidden" name="hidcurpage" id="hidcurpage" value="" />
			<div id="mdlright">
				<div class="mdlrignav">
				<div class="longpadrig">
					<div class="longsearch">
					<a href="AddUserData.do" class="addblock" style="float:left">信息补录</a>
						用户名：<input type="text" id="txtUserName" name="txtUserName" class="searchkey" />
							&nbsp;&nbsp;&nbsp;
						文件ID：<input id="txtFileId" name ="txtFileId"  class="searchkey"/> 
							&nbsp;&nbsp;&nbsp;
							
						操作：<select id="operation" name="operation" class="comselt">
								<option value="">全部</option>
								<option value="1">getdownload</option>
								<option value="2">hfmsdownload</option>
								<option value="3">backstage</option>
								<option value="4">hfmsupload</option>
								<option value="5">sourcefile</option>
								<option value="6">pdf2epub</option>
								<option value="7">chkuserauthority</option>
								<option value="8">redowncnkifile</option>
								<option value="9">feefile</option>
							</select>
						&nbsp;&nbsp;&nbsp;
						执行结果：<select id="opstatus" name="opstatus" class="comselt">
								<option value="">全部</option>
								<option value="1">成功</option>
								<option value="0">失败</option>
							</select>
							
						<input name="" class="searchbtn" type="button" onclick="getDownloadTraceFiles();" value="查询" />
					</div>
					<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple" />
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata"></div>
					<div class="botopt">
						<div class="botleftopt">
							<input name="selectAll" id="selectAll" onclick="checkall('tabfiles',this)" type="checkbox" value="" /><a href="javascript:void(0);" onclick="delDownloadTraceSelectAll()" class="addblock">删除</a><a href="javascript:void(0);" onclick="delDownloadTraceAll()" class="addblock">删除全部</a>
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
			getDownloadTraceFiles();
		});
	</script>
</body>
</html>