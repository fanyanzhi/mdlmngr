<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/imgupload.js"></script>
<script type="text/javascript" src="./js/ajaxfileupload.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<div id="mdlright">
			<div class="mdlrignav conmendnav">
				<c:if test="${ModuleID==1}">
					<a href="RecommendList.do" class="return fleft">返回</a>
					<div class="rigname">
						<h2>
							<em>精品推荐</em>
						</h2>
					</div>
				</c:if>
				<c:if test="${ModuleID==2}">
					<div class="rigname">
						<h2>
							<em>图片公告</em>
						</h2>
					</div>
				</c:if>
			</div>
			<div class="mdlrigcontent" id="mdlrigcon">
				<div class="picup">
					<label>上传图片：</label> <span class="uppicbox" id="uppicspan"> </span> <input name="" class="uptpicbtn" type="button" value="选择图片" /> <input name="" class="uptpicdo" type="button" onclick="ajaxFileUpload();" value="立即上传" /> <input type="hidden" id="moduleid" name="moduleid" value='${ModuleID}' />
				</div>
				<div class="selectpicname"></div>
				<ul class="picuplist">
				</ul>


			</div>
		</div>


	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		var vmid = $("#moduleid").val();
		var f = createElement("input","uploadpic");
		f.setAttribute("type", "file");
		f.setAttribute("id", "uploadpic");
		f.setAttribute("hidefocus", "true");
		f.setAttribute("class", "uploadpic");
		f.onchange = function() {
			InputFileChanged();
		};
		document.getElementById("uppicspan").appendChild(f);
		$(function() {
			getImageHtml(vmid);

		});
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
	</script>
</body>
</html>