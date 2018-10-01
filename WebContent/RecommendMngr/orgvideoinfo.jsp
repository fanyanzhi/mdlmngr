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
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" />
			<input type="hidden" id="hidjpy" name="hidjpy" value="${orgPY}" />
			<input type="hidden" id="hidtype" name="hidtype" value="${TypeID }" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="javascript:history.go(-1);" class="return fleft">返回</a>
					<div class="rigname">
						<h2>
							<em>精品推荐</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="detailbtnbox journalbtn">
						<h1 class="journalname"><span id="spjn">${orgVideoInfo.columnname}</span></h1>
						<span class="jourcombtn"><a href="javascript:void(0);" style="display:${IsRecomand?'bolck':'none'}" onclick="disRecomandWholeJournal();" id="disrmdbtn" title="取消推荐" class="delfine">取消推荐</a><a href="javascript:void(0);" style="display:${IsRecomand?'none':'block'}" id="rmdbtn" onclick="RecomandWholeJournal();" title="精品推荐" class="fineque">精品推荐</a>
						</span>
						<div class="clear"></div>
					</div>
					<div class="journal" style="display: block;">
						<div class="journalpic">
							<img src=${ImageURL}></img>
						</div>
						<div class="journalinfo">
							<p>栏目： ${orgVideoInfo.columnname}</p>
							<p>节目：${orgVideoInfo.showname}</p>
							<p>日期：${orgVideoInfo.broadcastdate}</p>
							<p>编导：${orgVideoInfo.director }</p>
							<p>时长： ${orgVideoInfo.duration}</p>
							<p>视频大小： ${orgVideoInfo.filesize }</p>
							<p>节目简介： ${orgVideoInfo.showinfo }</p>
						</div>
					</div>
					<div class="catalogbox">
						<%-- <div class="catalogtit">
							<b>本刊出版汇总:</b><em class="gray"></em>
						</div>
						<div class="catalogyear">
							<div class="year">
								<p>
									<a  href="#" class="current"><span id="curyear">${LatestYear}</span>年</a>
								</p>
								<span class="moreyear"><a href="javascript:void(0);" onclick="$('#divmonth').hide();$('#divyear').show();">更多...</a></span>
							</div>
							<p class="month" id="divmonth" style="display: block;">
							</p>
							<p class="month yearother" id="divyear" style="display: none;">
								${YearsInfo } 
							</p>
						</div> --%>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	/* $(function(){
		$("#divyear").find("a").each(function(){
			if($(this).html()==$("#curyear").html()+"年"){
				$(this).attr("class","current");
			}
		});
		getIssueInfo($("#hidjpy").val(),$("#curyear").html(),$("#hidtype").val());
	}); */
	</script>
</body>
</html>