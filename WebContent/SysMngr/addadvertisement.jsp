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
<script type="text/javascript" src="./js/imgupload.js"></script>
<script type="text/javascript" src="./js/ajaxfileupload.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1" method="post" action="" onsubmit="return chkAdvInfoForm();">
			<input type="hidden" id="hidaid" name="hidaid" value="${AdvertisementInfo.id }" />
			<div id="mdlright">
			<script language="javascript" type="text/javascript" src="js/PopupCalendar.js"></script>
				<div class="mdlrignav">
					<a href="AdvertisementList.do" class="return">返回</a>
					<div class="rigname">
						<h2>
							<em>广告发布</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="editcomform">
						<ul>
							<li><label class="leb">广告类别：</label> <input name="strType" id="strType0" type="radio" value="0" ${AdvertisementInfo==null?'checked':AdvertisementInfo.type==0?'checked':''} /> <label class="lebrig">链接 </label> <input  id="strType1" name="strType" type="radio" value="1" ${AdvertisementInfo==null?'':AdvertisementInfo.type==1?'checked':''} /> <label class="lebrig">文本 </label></li>
							<li><label class="leb">广告详细：</label> <textarea class="comtextarea" name="txtContent" id="txtContent" cols="" rows="">${AdvertisementInfo.content}</textarea></li>
							
							<li class="compicup"><label class="leb"> 上传图片：</label> <span id="uppicspan" class="comuppicbox"> </span> <input name="" class="uptpicbtn" type="button" value="选择图片" /> <input name="" class="uptpicdo" onclick="ajaxImageUpload();" type="button" value="立即上传" /> <span id="schecked"></span> <input type="hidden" id="moduleid" name="moduleid" value='${ModuleID}' /> <input type="hidden" id="imgid" name="imgid" value='${AdvertisementInfo.imageId}' /> 
								<div class="compic">
									<p class="commopic">
										<span class="comuppicbox">暂无预览 </span>
									</p>
									<div id="imgdiv">
										<c:if test="${AdvertisementInfo !=null && AdvertisementInfo.imageId != 0}">
											<img src="ImgSrcHandler?${AdvertisementInfo.imageId}" />
											<p class="compicupdel">
												<span onclick="delImportPic(${AdvertisementInfo.imageId});">删除</span>
											</p>
											<!-- 没有图片时，隐藏此-->
										</c:if>
									</div>
									<!-- 没有图片时，隐藏此-->
								</div></li>
							<li><label class="leb">有效日期：</label> <span>开始时间：<input id="txtStartDate" name="txtStartDate" type="text" value="${AdvertisementInfo.startDate }" class="timetstyle" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" />&nbsp;&nbsp;结束时间：<input id="txtEndDate" name="txtEndDate" type="text" value="${AdvertisementInfo.endDate }" onclick="window.parent.getDateString(this,window.parent.oCalendarChs,0);" autocomplete="off" class="timetstyle" /></span></li>
							<li class="passave"><input name="" type="submit" value="确定" class="mdlbtn" /><input name="" type="button" onclick="$('#txtDescription').val('');" value="重置" class="mdlbtn" /></li>
						</ul>
						<p id="prompt" class="prompt">${errmsg}</p>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		//var vmid = $("#moduleid").val();
		//var f = document.createElement("input");
		var f=createElement("input","uploadpic");
		f.setAttribute("id", "uploadpic");
		f.setAttribute("type", "file");
		f.setAttribute("hidefocus", "true");
		f.setAttribute("class", "uploadpic");
		f.onchange = function() {
			ImportRemdImgChanged();
		};
		document.getElementById("uppicspan").appendChild(f);
		function createElement(type, name) {     
			var element = null;     
			try {        
			    element = document.createElement('<'+type+' name="'+name+'" class="uploadpic"'+'>');     
			} catch (e) {
			}     
			if (element==null) {   
			element = document.createElement(type);     
			element.name = name;     
			}     
			return element;     
			}   
		/*$(function() {
			//getImageHtml(vmid,);
		/*});*/
	</script>
</body>
</html>