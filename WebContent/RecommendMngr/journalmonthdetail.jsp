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
<script type="text/javascript" src="./js/json.js"></script>
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
			<input type="hidden" id="hidcurpage" name="hidcurpage" value="0" /> 
			<input type="hidden" id="hidjpy" name="hidjpy" value="${JournalPY}" /> 
			<input type="hidden" name="hidyearinfo" id="hidyearinfo" value="${YearInfo}" /> 
			<input type="hidden" name="hidissueinfo" id="hidissueinfo" value="${IssueInfo}" /> 
			<input type="hidden" id="hidtype" name="hidtype" value="${TypeID }" /> 
			<input type="hidden" name="hidfunction" id="hidfunction" value="setInssurArticle" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="javascript:history.go(-1);" class="return fleft" >返回</a>
					<div class="rigname">
						<h2>
							<em>精品推荐</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="detailbtnbox journalbtn">
						<h1 class="journalname">
							<span id="spjn">${JournalName}</span>
						</h1>
						<span class="jourcombtn"> <a href="javascript:void(0);" style="display:${IsRecomand?'bolck':'none'}" onclick="disRecomandWholeJournal();" id="disrmdbtn" title="取消推荐" class="delfine">取消推荐</a><a href="javascript:void(0);" style="display:${IsRecomand?'none':'block'}" id="rmdbtn" onclick="RecomandWholeJournal();" title="精品推荐" class="fineque">精品推荐</a>
						</span>
						<div class="clear"></div>
					</div>

					<div class="catalogbox">
						<div class="catalogtit">
							<b>目录</b><em id="emcurinssue" class="gray">2013年12期</em>
						</div>
						<div class="catalogyear">
							<div class="year">
								<p>
									<a href="javascript:void(0);" class="current"><span id="curyear">${LatestYear}</span>年</a>
								</p>
								<span class="moreyear"><a href="javascript:void(0);" onclick="$('#divmonth').hide();$('#divyear').show();">更多...</a></span>
							</div>
							<p class="month" id="divmonth"></p>
							<p class="month yearother" id="divyear" style="display: none;">${YearsInfo}</p>
						</div>
						<div class="cataloglist" id="cataloglist" style="height: 500px;">
							<div id="divdata"></div>
							
						</div>
						<div class="botopt">
								<div class="botleftopt">
									<input name="selectAll" id="selectAll" onclick="checkall('tabarticle',this)" type="checkbox" value="" /><a href="javascript:void(0)" onclick="RemmondAllJournalArticle()" class="delall">推荐</a>
								</div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		$(function() {
			//$(document).ready(function(){
			$("#divyear").find("a").each(function(){
				if($(this).html()==$("#curyear").html()+"年"){
					$(this).attr("class","current");
				}
			});
			
			
			getIssueInfo($("#hidjpy").val(), $("#curyear").html(),
					$("#hidtype").val(), $("#hidissueinfo").val());
			getJournalArticles();
			setCurInssueEM();

			var nScrollHight = 0; //滚动距离总长(注意不是滚动条的长度)
			var nScrollTop = 0; //滚动到的当前位置
			var reset = 0;
			/*var nDivHight = $("#divdata").height();*/

			/*var x=0;
			$(".cataloglist").scroll(function() {
			    $("#span1").text(x+=1);
			  });*/
			$(".cataloglist").scroll(function() {
					if (reset == 0) {
						var nDivHight = $(".cataloglist").height();
						nScrollHight = $(this)[0].scrollHeight;
						nScrollTop = $(this)[0].scrollTop;
						if (nScrollTop + nDivHight + 20 > nScrollHight) {
							if (document.getElementById("hidpagecount") != undefined) {
								if ($("#hidcurpage").val() < $("#hidpagecount").val()) {
									var vcurpage = parseInt($("#hidcurpage").val(),10);
									getFormData(vcurpage + 1);
									reset = 1;
									/*setTimeout(function(){getFormData(vcurpage+1);}, 1000);*/
								};
							}
						}
					} else {
						setTimeout(function() {
							reset = 0;
						}, 1200);
					}

					//alert("滚动条到底部了");
				});
		});
		//function test(){

		//}
	</script>
</body>
</html>