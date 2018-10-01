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
					<div class="search" style="width: 600px; margin-left: -300px;">

						<span>统计日期：
						<input id="txtDate" name="txtDate" type="text" class="timetstyle" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" />
						<input name="" class="searchbtn" type="button" onclick="getOnlinePic();" value="查询" />
						</span>
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
			$("#txtDate").val(new Date().format("yyyy-MM-dd"));
			$("#txtEndDate").val(getLastMonthYestdy(new Date()));
			getOnlinePic();
		});
		function getOnlinePic() {
			showLoading();
			var vwidth = window.screen.width - 240 - 150;
			var varcon = "";
			if ($.trim($("#txtDate").val()).length > 0) {
				if (!IsDate($.trim($("#txtDate").val()))) {
					alert("统计日期格式不正确");
					return false;
				}
			}

			if ($.trim($("#txtDate").val()).length > 0) {
				varcon = varcon + "startdate=" + $.trim($("#txtDate").val()) + "&";
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
				data : "do=getStatistics&" + $("#hidparam").val() + "&picw=" + vwidth,
				success : function(data) {
					closeLoading();
					if (data.length > 0) {
						var jsonarray= $.parseJSON(data);
						var dateArray = new Array();
						var onlineCountArray = new Array();
						$.each(jsonarray,function(i, n){
							console.info("i:"+i);
							console.info("n:"+n);
						    var pointdata=[n.spottime];
						    var onlinecount=[n.count];
						    dateArray[i]=pointdata;
						    onlineCountArray[i]=onlinecount;
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
						            name: '最大在线用户数',
						            data: onlineCountArray,
						            color:'#07ABE8'
						        }
						        ]
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