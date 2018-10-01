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
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1">
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL }" /> <input type="hidden" name="hidpageparam" id="hidpageparam" value="${PageParam}" /> <input type="hidden" name="hidparam" id="hidparam" value="${PageParam}" />
			<div id="mdlright">
				<script language="javascript" type="text/javascript" src="js/PopupCalendar.js"></script>
				<div class="mdlrignav">
				<a href="StatisticAnalysis.do" class="fleft return">返回</a>
					<div class="search" style="width: 600px; margin-left: -300px;">

						<!-- <span>开始时间：<input id="txtStartDate" name="txtStartDate" type="text" class="timetstyle" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" />&nbsp;&nbsp;结束时间：<input id="txtEndDate" name="txtEndDate" type="text" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" autocomplete="off" class="timetstyle" /><input name="" class="searchbtn" type="button" onclick="getOnlineAnalysisPic();" value="查询" /></span> -->
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div id="divdata" class="datapic" style="margin-top:30px;"></div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	$(function() {
		/* $("#txtStartDate").val(new Date().format("yyyy-MM-dd"));
		$("#txtEndDate").val(getLastMonthYestdy(new Date())); */
		//window.setInterval(getAliveDetailPic, 30000); 
		
		getAliveDetailPic();
	});
	window.setInterval(getAliveDetailPic,3600000); 
	function getAliveDetailPic() {
		showLoading();
		var vwidth = window.screen.width - 240 - 150;
		var varcon = "";
		if ($.trim($("#txtStartDate").val()).length > 0) {
			if (!IsDate($.trim($("#txtStartDate").val()))) {
				alert("开始日期格式不正确");
				return false;
			}
		}
		if ($.trim($("#txtEndDate").val()).length > 0) {
			if (!IsDate($.trim($("#txtEndDate").val()))) {
				alert("结束日期格式不正确");
				return false;
			}
		}

		if ($.trim($("#txtStartDate").val()).length > 0
				&& $.trim($("#txtEndDate").val()).length > 0) {
			if ($("#txtStartDate").val() > $("#txtEndDate").val()) {
				var tempTime = $("#txtStartDate").val();
				$("#txtStartDate").val($("#txtEndDate").val());
				$("#txtEndDate").val(tempTime);
			}
		}

		if ($.trim($("#txtStartDate").val()).length > 0) {
			varcon = varcon + "startdate=" + $.trim($("#txtStartDate").val()) + "&";
		}
		if ($.trim($("#txtEndDate").val()).length > 0) {
			varcon = varcon + "enddate=" + $.trim($("#txtEndDate").val()) + "&";
		}
		if ($.trim($("#hidpageparam").val()).length > 0) {
			varcon = varcon + $.trim($("#hidpageparam").val()) + "&";
		}

		$("#hidparam").val(varcon);

		$.ajax({
			type : "GET",
			url : $("#hidurl").val(),
			async : true,
			cache : false,
			data : "do=getpic&" + $("#hidparam").val() + "&picw=" + vwidth,
			success : function(data) {
				closeLoading();
				if (data.length > 0) {
					var jsonarray= $.parseJSON(data);
					var dateArray = new Array();
					var countArray = new Array();
					$.each(jsonarray,function(i, n){
					    var pointdata=[n.date];
					    var pointcount=[n.count];
					    dateArray[i]=pointdata;
					    countArray[i]=pointcount;
					});
					//alert(dateArray);
					//alert(countArray);
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
					        tooltip: {
						            pointFormat: '<b>登录用户数:{point.y}</b>',
						            headerFormat: '<b>日期：{point.x}</b><br />'
					        },
					        legend: {
					        	enabled: false
					        },
					        series: [{
					            name: '登录用户',
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
	}
	</script>

</body>
</html>