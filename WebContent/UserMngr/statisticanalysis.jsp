<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/simplepagetag.js"></script>
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
			 <input type="hidden" name="hidcount" id="hidcount" value="" /> 
			 <input type="hidden" name="hidfunction" id="hidfunction" value="setHidUser" />
			 <input type="hidden" name="hidfunempty" id="hidfunempty" value="setUserEmpty" />
			 <input type="hidden" name="hidcurpage" id="hidcurpage" value="" />
			<div id="mdlright">
			<script language="javascript" type="text/javascript" src="js/PopupCalendar.js"></script>
				<div class="mdlrignav">
				<a href="UserAlivePicAnalysis.do" class="addblock fleft">实时终端</a>
				<a href="UserAliveList.do" class="addblock fleft">在线终端</a>
				
				<a href="HeartBeatDetail.do" class="addblock fleft">心跳数据</a>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div class="selectform" id="leftcon">
						<ul>
							<li><label class="statleb"> 用户名： </label><span id="spanuser"></span> <a href="javascript:void(0);" onclick="showusertab();" class="statsmore" title="重新选择">选择</a></li>
							<li><label class="statleb">时间段： </label> <input id="txtStartDate" name="txtStartDate" type="text" class="timetstyle" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);"/>&nbsp;&nbsp;至&nbsp;&nbsp;<input id="txtEndDate" name="txtEndDate" type="text" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" autocomplete="true" class="timetstyle" /></li>
								<li><label class="statleb">设备：</label>
								<input id="singleTerminal" name="singleTerminal" class="iptstyle" type="text"/>  
							</li>
							<c:if test="${Terminal!=null}">
							<li><label class="statleb">使用设备：</label> 
							<c:forEach items="${Terminal}" var="item">  
							<input name="terminal" type="checkbox" value="${item.key }" /><label class="device">${item.value }</label>
							</c:forEach>
							</li>
							</c:if>
							<li class="statsbtnbox"><input name="" type="button" onclick="beginTongJi(0)" value="开始统计" class="beginstats  statsbtn2" />&nbsp;&nbsp;<input name="" type="button" onclick="beginTongJi(1)" value="图表分析" class="beginstats" /></li>
						</ul>
					</div>
					<div class="statsuser" id="divusertab" style="display: none">
						<div class="statsusertit">
						<a class="statsclose" href="javascript:void(0);" onclick="closediv()" title="关闭选择">关闭</a>
							<h2>选择用户</h2>
							<div class="searchuser">
								<input name="txtUserName" id="txtUserName" type="text" class="keyword" /><input name="" class="searchbtn" type="button" onclick="getSelUser()" value="查询" />
							</div>
						</div>
						<table width="100%" border="0" cellspacing="0" cellpadding="0" class="tabone">
							<tr>
								<th width="30">&nbsp;</th>
								<th width="50" class="num">&nbsp;</th>
								<th width="100">用户名</th>
								<th>上次登录时间</th>
							</tr>
						</table>
						<div class="statusertab" id="statuser">
						
						</div>
						<div class="useropt">
							<div class="selectopt"><input name="selectAll" id="selectAll" type="checkbox" value="" onclick="checkallUsers('selusers',this);" /> 全选 <a href="javascript:void(0);" onclick="clearuser()" class="delall">清空</a> </div>
							<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple"/>
						</div>
					</div>

				</div>
			</div>
		</form>
		<div id="userpopalert" class="popalert" style="display:none;"> <a href="javascript:void(0);" onclick="$('#userpopalert').css('display','none');" class="closealert" title="关闭">关闭</a> 
			<p class="popnote">只能统计最近登录的前20名用户信息！</p>
			<p class="popbtn"><input name="" type="button" value="确定" class="mdlbtn"  onclick="$('#userpopalert').css('display','none');"/></p>
		</div>
	</div>
	<!--   main  end  -->
	
	<script type="text/javascript">
		var arrUser = new Array();
		var objUser = {};
	</script>
</body>
</html>