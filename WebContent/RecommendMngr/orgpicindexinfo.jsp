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
			<input type="hidden" id="hidtype" name="hidtype" value="${TypeID}" />
			<input type="hidden" id="picindexid" name="picindexid" value="${orgPicIndexInfo.picindexid}" />
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
						<h1 class="journalname"><span id="spjn">${orgPicIndexInfo.picindextheme}</span></h1>
						<span class="jourcombtn"><a href="javascript:void(0);" style="display:${IsRecomand?'bolck':'none'}" onclick="disRecomandWholeJournal();" id="disrmdbtn" title="取消推荐" class="delfine">取消推荐</a><a href="javascript:void(0);" style="display:${IsRecomand?'none':'block'}" id="rmdbtn" onclick="RecomandWholeJournal();" title="精品推荐" class="fineque">精品推荐</a>
						</span>
						<div class="clear"></div>
					</div>
					<div class="journal" style="display: block;">
						<div class="journalpic">
							<img id="picId" src=""></img>
						</div>
						<div class="journalinfo">
							<p>专题名称：${orgPicIndexInfo.subjectname}</p>
							<p>挂图主题名称： ${orgPicIndexInfo.picindextheme}</p>
							<p>图像尺寸：${orgPicIndexInfo.picsize }</p>
							<p>分辨率： ${orgPicIndexInfo.resolution }</p>
							<p>图片数量： ${orgPicIndexInfo.picindexnum }</p>
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
		$(function(){
			var array = new Array();
			jQuery.ajax({
				url : "OrgInfo.do",
				type : "post",
				dataType : "json",
				data : "picindexid="+$("#picindexid").val(),
				success : function(data) {
					$.each(data,function(k,v){
						array.push(v.maxUrl);
					});
					var i=0;
					$("#picId").attr("src",array[0]);
					setInterval(function(){
						if(i<array.length){
							i++;
						}else{
							i=0;
						}
						$("#picId").attr("src",array[i]);
					},1500);
				},
				error : function(XMLHttpRequest, textStatus,
						errorThrown) {
					var t="";
					alert("服务器错误");
				}
			});
		})
	</script>
</body>
</html>