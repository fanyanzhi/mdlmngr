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
<body onkeydown="if(event.keyCode==13) {getRecommendList();return false;}">
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" /> <input type="hidden" name="hidparam" id="hidparam" value="" /> <input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> <input type="hidden" name="hidcount" id="hidcount" value="" /> <input type="hidden" name="hidtype" id="hidtype" value="" /> <input type="hidden" name="hidcurpage" id="hidcurpage" value="" />
			<div id="mdlright">
				<div class="mdlrignav">

					<div class="controltit">
						<span style="cursor:pointer;" onclick="window.location.href='CommentSortControl.do';">分类控制</span><em class="shu">1</em><span class="current">文献控制</span>
					</div>
					<a href="CommentSearch.do" class="addblock fright20">评论控制</a>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata">
	 				</div>
					<div class="botopt">
						<div class="botleftopt">
							<input name="selectAll" id="selectAll" onclick="checkall('docutab',this)" type="checkbox" value="" /><a href="javascript:void(0)" onclick="openMultiComment();" class="delall">开启</a>
						</div>
						<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function(){
		getFormDataCount(1);
	});
	
	</script>
</body>
</html>