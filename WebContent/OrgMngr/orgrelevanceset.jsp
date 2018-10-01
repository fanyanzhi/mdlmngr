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
					<div class="rigname">
						<h2>
							<em>审核设置</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent">
					<div class="editcomform">
						<ul>
							<li><label class="leb">审核方式：</label>
							 	<input name="shsort" id="shsort0" type="radio" value="1" ${shset.sort==null?'checked':shset.sort==1?'checked':''} /> <label class="lebrig">自动审核 </label> 
							 	<input name="shsort" id="shsort1" type="radio" value="2" ${shset.sort==null?'':shset.sort==2?'checked':''}/> <label class="lebrig">人工审核</label>
							    <input name="shsort" id="shsort2" type="radio" value="3" ${shset.sort==null?'':shset.sort==3?'checked':''}/> <label class="lebrig">问题审核</label> 
							</li>
							<li><label class="leb">有效期：</label>
							<input name="validtime" id="validtime0" type="radio" value="-1" ${shset.validtime==null?'checked':shset.validtime==-1?'checked':''} /> <label class="lebrig">无限制 </label> 
							 	<input name="validtime" id="validtime1" type="radio" value="7" ${shset.validtime==null?'':shset.validtime==7?'checked':''}/> <label class="lebrig">7天</label>
							    <input name="validtime" id="validtime2" type="radio" value="30" ${shset.validtime==null?'':shset.validtime==30?'checked':''}/> <label class="lebrig">30天</label>  
							</li>
							<li><label class="leb">验证问题：</label> <input type="text" class="iptstyle" id="txtquestion" name="txtquestion" value="${shset.question}"  onblur="chkInputLength(this,'tipTitle',100,'问题长度')" /></li>
							<li><label class="leb">问题答案：</label> <input type="text" class="iptstyle" id="txtanswer" name="txtanswer" value="${shset.answer}"  onblur="chkInputLength(this,'tipTitle',50,'答案长度')" /></li>
							<li class="passave"><input name="" type="submit" value="确定" class="mdlbtn" /><input name="" type="reset" onclick="$('#txtDescription').val('');" value="重置" class="mdlbtn" /></li>
						</ul>
						<p id="prompt" class="prompt">${errmsg}</p>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">		
		 function chkSubjectInfoForm(){
				if($.trim($("#txtTitle").val()).length==0){
					alert("主题名称不能为空！");
					return false;
				}
				if($.trim($("#txtWord").val()).length==0){
					alert("主题词不能为空！");
					return false;
				}
				if($.trim($("#txtType").val()).length==0){
					alert("主题类别不能为空！");
					return false;
				}
				
				if($.trim($("#txtSummary").val()).length>500){
					alert("主题详情不要超过500字");
					return false;
				}
				return true;
		}
	</script>
</body>
</html>