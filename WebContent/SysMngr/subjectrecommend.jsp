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
		 <form id="form1" name="form1" method="post" action="SubjectRecommendHandler.do" onsubmit="return chkSubjectInfoForm();">
			<input type="hidden" id="sign" name="sign" value="${sign}" />
			<input type="hidden" id="subjectId" name="id" value="${id}" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="SubjectList.do" class="return">返回</a><c:if test="${subject.openclass ==1}"><a href="javascript:void(0);" onclick="window.location.href='OrgSubject.do?id=${subject.subjectid}&sn='+encodeURI('${subject.title}');" class="return">关联机构</a></c:if>
					<div class="rigname">
						<h2>
							<em>主题推荐</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="editcomform">
						<ul>
							<li><label class="leb">主题名称：</label> <input type="text" class="iptstyle" id="txtTitle" name="txtTitle" value="${subject.title}"  onblur="chkInputLength(this,'tipTitle',30,'主题标题长度')" /></li>
							<li><label class="leb">主题词：</label> <input type="text" class="iptstyle" id="txtWord" name="txtWord" value="${subject.keyword}"  onblur="chkInputLength(this,'tipTitle',30,'通知标题长度')" /></li>
							<li><label class="leb">学科类别：</label> <input type="text" class="iptstyle" id="txtType" name="txtType" value="${subject.type}"  onblur="chkInputLength(this,'tipTitle',30,'通知标题长度')" /></li>
							<li><label class="leb">是否置顶：</label> <input name="strTop" id="strTop0" type="radio" value="0" ${subject.istop==null?'checked':subject.istop==0?'checked':''} /> <label class="lebrig">否 </label> 
							                                         <input name="strTop" id="strTop1" type="radio" value="1" ${subject.istop==null?'':subject.istop==1?'checked':''}/> <label class="lebrig">是</label></li>
							<li><label class="leb">是否推荐：</label> <input name="strRecomd" id="strRecomd0" type="radio" value="0" ${subject.isrecomd==null?'checked':subject.isrecomd==0?'checked':''} /> <label class="lebrig">否 </label> 
							                                         <input name="strRecomd" id="strRecomd1" type="radio" value="1" ${subject.isrecomd==null?'':subject.isrecomd==1?'checked':''}/> <label class="lebrig">是</label></li>
							<li><label class="leb">公开级别：</label> <input name="strOpen" id="strOpen0" type="radio" value="0" ${subject.openclass==null?'checked':subject.openclass==0?'checked':''} /> <label class="lebrig">所有机构 </label> 
							                                         <input name="strOpen" id="strOpen1" type="radio" value="1" ${subject.openclass==null?'':subject.openclass==1?'checked':''}/> <label class="lebrig">部分机构</label></li>
							<li><label class="leb">主题类别：</label> <input name="strLinkType" id="strLinkType0" type="radio" value="0" ${subject.linktype==null?'checked':subject.linktype==0?'checked':''} /> <label class="lebrig">默认主题 </label> 
							                                         <input name="strLinkType" id="strLinkType1" type="radio" value="1" ${subject.linktype==null?'':subject.linktype==1?'checked':''}/> <label class="lebrig">链接</label>
							                                         <input name="strLinkType" id="strLinkType2" type="radio" value="2" ${subject.linktype==null?'':subject.linktype==2?'checked':''}/> <label class="lebrig">文本</label>
							                                         <input name="strLinkType" id="strLinkType3" type="radio" value="3" ${subject.linktype==null?'':subject.linktype==3?'checked':''}/> <label class="lebrig">整刊</label></li>
							<li><label class="leb">置为广告：</label> <input name="isadv" id="isadv0" type="radio" value="0" ${subject.isadv==null?'checked':subject.isadv==0?'checked':''} /> <label class="lebrig">否 </label> 
							                                         <input name="isadv" id="isadv1" type="radio" value="1" ${subject.isadv==null?'':subject.isadv==1?'checked':''}/> <label class="lebrig">是</label></li>
							
							<li><label class="leb">主题摘要：</label> <textarea class="comtextarea" name="txtSummary" id="txtSummary" cols="" rows="">${subject.summary}</textarea></li>
							
							<li class="compicup">
								<label class="leb"> 上传小图片：</label> 
								<span id="uppicspan1" class="comuppicbox"> </span> 
								<input name="" class="uptpicbtn" type="button" value="选择图片" /> 
								<input name="" class="uptpicdo" onclick="imageUpload(1);" type="button" value="立即上传" /> 
								<span id="schecked1"></span> <input type="hidden" id="moduleid1" name="moduleid1" value='${ModuleID}' /> 
								<input type="hidden" id="imgid1" name="simageid" value='${subject.simageid}' /> 
								<div class="compic  smallcompic">
									<p class="commopic">
										<span class="comuppicbox">暂无预览 </span>
									</p>
									<div id="imgdiv1">
										<c:if test="${subject !=null && subject.simageid != 0}">
											<img src="ImgSrcHandler?${subject.simageid}" />
											<p class="compicupdel">
												<span onclick="delThisPic(${subject.simageid},1);">删除</span>
											</p>
											<!-- 没有图片时，隐藏此-->
										</c:if>
									</div>
									<!-- 没有图片时，隐藏此-->
								</div></li>
							<li class="compicup">
								<label class="leb"> 上传大图片：</label> 
								<span id="uppicspan2" class="comuppicbox"> </span> 
								<input name="" class="uptpicbtn" type="button" value="选择图片" /> 
								<input name="" class="uptpicdo" onclick="imageUpload(2);" type="button" value="立即上传" /> 
								<span id="schecked2"></span> <input type="hidden" id="moduleid2" name="moduleid2" value='${ModuleID}' /> 
								<input type="hidden" id="imgid2" name="bimageid" value='${subject.bimageid}' /> 
								<div class="compic">
									<p class="commopic">
										<span class="comuppicbox">暂无预览 </span>
									</p>
									<div id="imgdiv2">
										<c:if test="${subject !=null && subject.bimageid != 0}">
											<img src="ImgSrcHandler?${subject.bimageid}" />
											<p class="compicupdel">
												<span onclick="delThisPic(${subject.simageid},2);">删除</span>
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
		//var vmid = $("#moduleid").val();
		//var f = document.createElement("input");
		var f=createElement("input","uploadpic1");
		f.setAttribute("id", "uploadpic1");
		f.setAttribute("type", "file");
		f.setAttribute("hidefocus", "true");
		f.setAttribute("class", "uploadpic");
		f.onchange = function() {
			remdImgChanged(1);
		};
		var f2=createElement("input","uploadpic2");
		f2.setAttribute("id", "uploadpic2");
		f2.setAttribute("type", "file");
		f2.setAttribute("hidefocus", "true");
		f2.setAttribute("class", "uploadpic");
		f2.onchange = function() {
			remdImgChanged(2);
		};
		document.getElementById("uppicspan1").appendChild(f);
		document.getElementById("uppicspan2").appendChild(f2);
		
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
		 function remdImgChanged(num){
		    var filepath;
		    filepath=document.getElementById("uploadpic"+num).value;     
		    var filename=filepath.split('\\'); 
		    var tabfile=filename[filename.length-1]; 
		    $("#schecked"+num).html("已选择：<em>"+tabfile+"</em>");            
		 }
		 function chkSubjectInfoForm(){
				if($.trim($("#txtTitle").val()).length==0){
					alert("主题名称不能为空！");
					return false;
				}
				if($.trim($("#txtWord").val()).length==0){
					alert("主题词不能为空！");
					return false;
				}
				/*if($.trim($("#txtType").val()).length==0){
					alert("主题类别不能为空！");
					return false;
				}*/
				
				if($.trim($("#txtSummary").val()).length>500){
					alert("主题详情不要超过500字");
					return false;
				}
				return true;
			}
		function imageUpload(num) {
			if ($("#uploadpic"+num).val() == "") {
				alert("请选择上传图片！");
				return;
			}
			if(!(/(?:jpg|gif|png|jpeg|bmp)$/i.test($("#uploadpic"+num).val()))) { 
				alert("只允许上传jpg|gif|png|jpeg|bmp格式的图片"); 
				return;
			}
			$.ajaxFileUpload({
				url : 'ImgRecHandler.do?moduleid=4',
				secureuri : false,
				fileElementId : 'uploadpic'+num,
				dataType : 'json',
				success : function(data, status) {
					if(data.message>0){
						$("#imgdiv"+num).html("<img src=\"ImgSrcHandler?"+data.message+"\" /><p class=\"compicupdel\"><span onclick=\"delThisPic("+data.message+","+num+");\">删除</span></p>");
						$("#imgid"+num).val(data.message);
					}else{
						alert("图片上传失败");
					}
				},
				error : function(data, status, e) {
					alert("上传文件失败。");
				}
			});
			return false;
		}
		function delThisPic(imageid,num){
			$.ajax({
				type : "get",
				url : "ImgRecHandler.do",
				async : true,
				cache : false,
				data : "do=del&pid="+imageid,
				success : function(data) {
					alert(data);
					if(data=="1"){
						$("#imgdiv"+num).html("");
						$("#imgid"+num).val("");
					}else{
						alert("删除失败");
					}
				},
				error : function() {
					alert("服务器错误");
				}
			});
		}
	</script>
</body>
</html>