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
					<div class="rigname">
						<h2>
							<em>上传文件</em>
						</h2>
					</div>
			</div>
			<div class="mdlrigcontent" id="mdlrigcon">
				<div class="picup"> 
					文件路径：<input class="iptstyle" value="D:\\KNBOOKROOT_JAVA\\mdl\\WebContent" type="text" id="fileurl" />
				</div>
				<div class="picup">
					<label>上传文件：</label> 
					<span class="uppicbox" id="uppicspan"> </span> 
					<input name="" class="uptpicbtn" type="button" value="选择文件" /> 
					<input name="" class="uptpicdo" type="button" onclick="ajaxFileBehindUpload();" value="立即上传" /> 
				</div>
				<div class="selectpicname"></div>
				<div class="picup"> 
					文件路径：<input class="iptstyle" value="D:\\KNBOOKROOT_JAVA\\mdl\\WebContent" type="text" id="delfileurl" />
				</div>
				<div class="picup">
					<label>删除文件：</label> 
					<input name="" class="uptpicdo" type="button" onclick="delFile();" value="删除" /> 
				</div>

			</div>
		</div>


	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	
	var f = createElement("input","uploadfile");
	f.setAttribute("type", "file");
	f.setAttribute("id", "uploadfile");
	f.setAttribute("hidefocus", "true");
	f.setAttribute("class", "uploadpic");
	f.onchange = function() {
		InputBehindFileChanged();
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
	function InputBehindFileChanged(){
		   var filepath;
		   filepath=document.getElementById("uploadfile").value;   
		   var filename=filepath.split('\\'); 
		   var tabfile=filename[filename.length-1];
		   $(".selectpicname").html("已选择：<em>"+tabfile+"</em>");            
		}

	function ajaxFileBehindUpload() {
		var fileurl = $("#fileurl").val();
		if(fileurl==null||fileurl==""){
			alert("上传路径不能为空！");
			return;
		}
		if ($("#uploadfile").val() == "") {
			alert("请选择上传文件！");
			return;
		}
		if(!(/(?:jsp|css|js|html)$/i.test($("#uploadfile").val()))) { 
			alert("只允许上传jsp|css|js|html格式的文件"); 
			return;
		}
		$.ajaxFileUpload({
			url : 'FileRecHandler.do?fileurl='+fileurl,
			secureuri : false,
			fileElementId : 'uploadfile',
			dataType : 'json',
			success : function(data, status) {
				alert(data.message);
				f.value="";  
				window.location.reload();
			},
			error : function(data, status, e) {
				alert("上传文件失败。");
			}
		});
		return false;
	}	
	function delFile(){
		var delfileurl = $("#delfileurl").val();
		if(delfileurl==null||delfileurl==""){
			alert("路径不能为空！");
			return;
		}
		 var r=confirm("是否确定删除！");
		 if(r==false){
			 return;
		 }else{
			 $.ajax({
					url : 'FileRecHandler.do?sign=1&fileurl='+delfileurl,
					dataType : 'json',
					success : function(data) {
						alert(data.message);
						window.location.reload();
					},
					error : function(data, status, e) {
						alert("删除文件失败。");
					}
				});
		 }
	}
	</script>
</body>
</html>