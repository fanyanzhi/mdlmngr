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
				<script language="javascript" type="text/javascript" src="js/PopupCalendar.js"></script>
				<div class="mdlrignav bighight">
					<div class="comfind">
						 <span class="fleft"><label>评&nbsp;&nbsp;论&nbsp;&nbsp;人：</label><input name="UserName" id="UserName" type="text" class="searchkey" /> </span>
<span class="fleft"><label>开始时间：</label><input name="txtStartDate" id="txtStartDate" type="text" class="timetstyle"  onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);"/>  </span><span class="fleft"><label>结束时间：</label><input name="txtEndDate" id="txtEndDate" type="text" class="timetstyle"  onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);"/></span> 
 <span class="fleft"><label>文献名称：</label><input name="DocuName" id="DocuName" type="text" class="findipt" /></span>
<span class="fleft"><label>评论内容：</label><input name="txtContent" id="txtContent" type="text" class="findipt" /></span>

<span class="fleft"><label>筛选范围：</label>
<select id="ddlIsPassed" class="comselt"><option value="">全部</option><option value="1">已通过</option><option value="0">未通过</option></select>
</span> <div class="clear" ></div>
<input name="" class="searchbtn" type="button" onclick="getCommnetList();" value="查询"/>
 </div>

  						<a href="FilterWord.do" class="fliter" title="设置过滤关键词">过滤设置</a>
  						<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple" />
  					
 				</div>
 				<div class="mdlrigcontent" id="commentcon">
	 				<div id="divdata">
	 				</div>
					<div class="botopt">
		  				<div class="botleftopt"> <input name="selectAll" id="selectAll" onclick="checkall('divcomment',this)" type="checkbox" value="" /><a href="javascript:void(0)" onclick="delMultiComment();" class="delall">刪除</a> </div>
		  				<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
	   				</div>
 				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		$(function() {
			getCommnetList();
		});
	</script>
</body>
</html>