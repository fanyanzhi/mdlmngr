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
		<form id="form1" name="form1" method="post" action="" onsubmit="return chkRecomdForm();">
			<input type="hidden" id="hidrid" name="hidrid" value="${RecommendationInfo.id }" /> <input type="hidden" id="hidimport" name="hidimport" value="${RecommendationInfo.important}" /> <input type="hidden" id="hidtitle" name="hidtitle" value="${RecommendationInfo.title }" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="RecommendList.do" class="return">返回</a>
					<div class="rigname">
						<h2>
							<em>精品推荐</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="editcomform">
						<ul>
							<li><label class="leb">名称： </label>${RecommendationInfo.title}</li>
							<li><label class="leb"> 描述：</label> <textarea class="comtextarea" name="txtDescription" id="txtDescription" cols="" rows="">${RecommendationInfo.description}</textarea></li>
							<li><label class="leb"> 重点推荐：</label> <input name="keyrecom" onclick="checkImport(this);" type="radio" value="1" ${RecommendationInfo==null?'':RecommendationInfo.important==1?'checked':''} /> <label class="lebrig">是 </label> <input name="keyrecom" onclick="checkImport(this);" type="radio" value="0" ${RecommendationInfo==null?'checked':RecommendationInfo.important==0?'checked':''} /> <label class="lebrig">否 </label></li>
							<li class="compicup" style="display:${RecommendationInfo==null?'none':RecommendationInfo.important==1?'':'none'}"><label class="leb"> 上传图片：</label> <span id="uppicspan" class="comuppicbox"> </span> <input name="" class="uptpicbtn" type="button" value="选择图片" /> <input name="" class="uptpicdo" onclick="ajaxImageUpload();" type="button" value="立即上传" /> <span id="schecked"></span> <input type="hidden" id="moduleid" name="moduleid" value='${ModuleID}' /> <input type="hidden" id="imgid" name="imgid" value='${ImageID}' /> <input type="hidden" id="foreignid" name="foreignid" value="${RecommendationInfo.id }" />
								<div class="compic">
									<p class="commopic">
										<span class="comuppicbox">暂无预览 </span>
									</p>
									<div id="imgdiv">
										<c:if test="${ImageID!=null}">
											<img src="ImgSrcHandler?${ImageID}" />
											<p class="compicupdel">
												<span onclick="delImportPic(${ImageID});">删除</span>
											</p>
											<!-- 没有图片时，隐藏此-->
										</c:if>
									</div>
									<!-- 没有图片时，隐藏此-->
								</div></li>
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
		var f = createElement("input","uploadpic");
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
	</script>
</body>
</html>