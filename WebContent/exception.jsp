<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>异常信息查看</title>
<link href="./css/cnkimdl.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript" src="./js/jquery-2.1.0.min.js"></script>
<script language="JavaScript" type="text/javascript" src="./js/mdl.js"></script>
<script type="text/javascript" src="./js/userpagetag.js"></script>
<script type="text/javascript" src="./js/simplepagetag.js"></script>
</head>
<body>
<!--  head begin -->
<form id="form1">
<div id="headvery">
<img src="./images/verytit.gif" />
</div>

<!--  maio begin -->
 
<div id="main">
 <script language="javascript" type="text/javascript" src="js/PopupCalendar.js"></script>
<div class="mdlrignav">

 <div class="search" style="width:800px;">
 <label>开始时间：</label><span class="timebox"><input id="txtStartDate" name="txtStartDate" type="text" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" /><input id="txtStartTime" name="txtStartTime" type="text" class="time" /></span>  
 <label>结束时间：</label><span class="timebox"><input id="txtEndDate" name="txtEndDate" type="text"  onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);"/><input id="txtEndTime" name="txtEndTime" type="text" class="time" /></span>
 <input name="" class="searchbtn" type="button" value="查询"  onclick="return searchErrorList();"/>
 </div>
 <usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple"/>
 </div>
  <div class="mdlrigcontent" id="mdlrigcon">
 <div id="divdata">
 </div>
 
 <div class="botopt">
  	<div class="botleftopt"> <input name="selectAll" id="selectAll" type="checkbox" value="" onclick="checkall('tblErrorList',this);"/>
		  <a name="del" href="javascript:void(0);" class="delall" onclick="deleteError();">删除</a>
		  <a name="del" href="javascript:void(0);" class="addblock" onclick="deleteError('all');" >全部删除</a> 
	</div>
	  <usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
   </div>
</div>  
 


</div>  
<input type="hidden" id="hidurl" value="${HandlerURL }" />
		<input type="hidden" id="hidparam" value="" />
		<input type="hidden" id="hidpagesize" value="${PageSize}" />
		<input type="hidden" id="hidcount" value="0" />
		<input type="hidden" id="hidcurpage" />
		
	</form>
<!--   main  end  -->
<script language="javascript" type="text/javascript">
	$(document).ready(function(){searchErrorList();});
	
</script>
 
</body>
</html>