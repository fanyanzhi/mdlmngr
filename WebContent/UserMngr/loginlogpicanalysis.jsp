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
  	<script type="text/javascript" src="./js/highcharts.js"></script>
<style>
	.spanIcon{
	float:left;
	background-color:#08ACE9;
	width:50px;
	height:26px;
	display:inline-table;
	text-align:center;
	border-left:2px solid #E7E7E7;
	cursor:pointer;
	}
</style>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" />
			<input type="hidden" name="hidtheparam" id="hidparam" value="${PageParam}" />

			<div id="mdlright">
				<script language="javascript" type="text/javascript" src="js/PopupCalendar.js"></script>
				<div class="mdlrignav">
				<a href="StatisticAnalysis.do" class="fleft return">返回</a>
					<div class="search" style="width: 600px; margin-left: -300px;">
						<!-- <span>开始时间：<input id="txtStartDate" name="txtStartDate" type="text" class="timetstyle" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" />&nbsp;&nbsp;结束时间：<input id="txtEndDate" name="txtEndDate" type="text" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" autocomplete="off" class="timetstyle" /><input name="" class="searchbtn" type="button" onclick="getAnalysisPic();" value="查询" /></span> -->
					</div>
				</div>
				<div id="selectIcon" style="margin-top:30px;width: 156px;height:26px;float:right;margin-right: 150px;border-top:2px solid #E7E7E7;border-right:2px solid #E7E7E7;border-bottom:2px solid #E7E7E7;">
						<span id="dayIcon" class="spanIcon" onclick="getLoginLogAnalysisPic('d');">天</span>
						<span id="monthIcon" class="spanIcon" onclick="getLoginLogAnalysisPic('m');">月</span>
						<span id="yearIcon" class="spanIcon" onclick="getLoginLogAnalysisPic('y');">年</span>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon" style="clear:both">
					<div id="divdata" class="datapic" style="margin-top:30px;"></div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		$(function() {
			//getLoginLogAnalysisPic('d');
			showLoading();
			var vwidth = window.screen.width - 240 - 150;
			$.ajax({
				type : "GET",
				url : $("#hidurl").val(),
				async : true,
				cache : false,
				data : "do=getpic&" + $("#hidparam").val() + "&picw=" + vwidth + "&vtype=d",
				success : function(data) {
					closeLoading();
					 if (data.length > 0) {
						var jsonarray= $.parseJSON(data);
						var dateArray = new Array();
						var countArray = new Array();
						$.each(jsonarray,function(i, n){
						    dateArray[i]=[n.date];
						    countArray[i]=[n.count];
						});
						 $('#divdata').highcharts({
								 credits:{
								     enabled:false // 禁用版权信息
								},
						        title: {
						            text: '',
						            x: -20 //center
						        },
						        subtitle: {
						            text: '',
						            x: -20
						        },
						        xAxis: {
						            categories: dateArray
						        },
						        yAxis: {
						        	allowDecimals:false,
						        	min: 0,
						        	lineWidth:1,
						            title: {
						                text: ''
						            },
						            plotLines: [{
						                value: 0,
						                width: 1,
						                color: '#808080'
						            }]
						        },
						        legend: {
						        	enabled: false
						        },
						        series: [{
						            name: '最大登录数',
						            data: countArray,
						            color:'#07ABE8'
						        }]
						    });
					} else {
						$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
					} 
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					closeLoading();
					alert("服务器错误");
				}
			});
		});
	</script>

</body>
</html>