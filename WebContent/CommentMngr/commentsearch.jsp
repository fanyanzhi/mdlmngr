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
<body onkeydown="if(event.keyCode==13) {getSearchData();return false;}" >
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
		<input type="hidden" name="hidurl" id="hidurl" value="${HandlerURL}" />
			<div id="mdlright">
				<div class="mdlrignav conmendnav">
					<a href="LiteratureControl.do" class="addblock">文献评论控制</a>
					<div class="rigname">
						<h2>
							<em>文献搜索</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div class="comsearchbox">

						<div class="comlassli">
							<p class="unfoldtext"  id="rdftype">
								<a href="#" class="current">期刊</a> <a href="#">硕士</a> <a href="#">博士</a> <a href="#">会议</a> <a href="#">报纸</a> <a href="#">外文文献</a> <a href="#">百科</a> <a href="#">专利</a> <a href="#">标准</a> <a href="#">成果</a> <a href="#">古籍</a> <a href="#">年鉴</a> <a href="#">词典</a> <a href="#"> 法律</a> <a href="#">统计数据</a> <a href="#">引文 </a> <a href="#">手册</a> <a href="#">图片</a>
							</p>
							<span class="unfold"></span>
						</div>
						<div class="bigsearch">
							<div class="bigsearchblock ">
								<p id="ddlSearchField" class="selectsimi">
									<a id="ddlRightName_ddl" class="select" title="全文"> 全文</a> <span class="hideoption" style="display: block;"> <em title="全文">全文全文</em> <em title="来源">来源</em>
									</span>
								</p>
								<input name="seachText" id="seachText" type="text" class="keynoborder" />
							</div>
							<input name="" class="searchbtn" type="button" value="查询" onclick="getCommentArticleData()"/>
						</div>

					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function(){
		getCommentSearchMenu();
	});
	</script>
</body>
</html>