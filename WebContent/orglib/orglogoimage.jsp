<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>机构馆</title>
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
			<a href="OrgImageList.do" class="return">返回</a>
				<div class="rigname">
					<h2>
						<em>动态图片</em>
					</h2>
				</div>
			</div>
			<div class="mdlrigcontent" id="mdlrigcon">
				<input type="hidden" id="hidid" name="hidid" value="${id}" /> 
				<div class="editcomform">
						<!-- <div style="margin: 50px auto -30px 50px;position: relative;"> -->
						<ul>
							<li><label class="leb">活动类别：</label> <input name="strType" class="lebrig" type="radio" value="0" ${type==null?'checked':type==0?'checked':''} /> <label class="lebrig">链接 </label> <input  id="strType1" name="strType" type="radio" value="1" ${type==null?'':type==1?'checked':''} /> <label class="lebrig">文本 </label><br /></li>
							<li><label class="leb">活动标题：</label> <input class="iptstyle" id="title" name="title" class="keyword" type="text" value="${title}"/><br /></li>
							<li><label class="leb">活动详情：</label><textarea class="comtextarea" name="txtContent" id="txtContent" cols="" rows="" >${content}</textarea><br/></li>
						
					<!-- </div> -->
				
				<li class="compicup"><label class="leb">上传图片：</label> <span class="uppicbox" id="uppicspan"> </span> <input name="" class="uptpicbtn" type="button" value="选择图片" /> <input name="" class="uptpicdo" type="button" onclick="ajaxOrgImageUpload();" value="发布" />
				<div class="selectpicname"></div>
				<div class="picuplist">
				</div>
					<p id="prompt" class="prompt">${errmsg}</p>
				</li>
				</ul>
				</div>
			</div>
		</div>


	</div>
	<!--   main  end  -->
	<script type="text/javascript">
	
	$(function(){
		var id = $("#hidid").val();
		if(id!=null&&id!=""){
			$(".picuplist").html("<img style='margin:30px 100px' src='OrgImgSrcHandler?id="+id+"'></img>");
		}
	});
	
	
	var f = createElement("input","uploadpic");
	f.setAttribute("type", "file");
	f.setAttribute("id", "uploadpic");
	f.setAttribute("hidefocus", "true");
	f.setAttribute("class", "uploadpic");
	f.onchange = function() {
		InputFileChanged();
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
	function ajaxOrgImageUpload() {
		var appid=$("#appid").val();
		if (appid == "") {
			alert("请填写appid！");
			return;
		}
		if ($("#uploadpic").val() == "") {
			alert("请选择上传图片！");
			return;
		}
		
		if(!(/(?:jpg|gif|png|jpeg|bmp)$/i.test($("#uploadpic").val()))) { 
			alert("只允许上传jpg|gif|png|jpeg|bmp格式的图片"); 
			return;
		}
		function RndNum(n) {
			var rnd = "";
			for ( var i = 0; i < n; i++)
				rnd += Math.floor(Math.random() * 10);
			var bound1 = 0;
			var bound2 = 10;
			rnd += parseInt(Math.random() * (bound1 - bound2) + bound2);
			return rnd;
		}

		$.ajaxFileUpload({
			url : 'OrgImageHandler.do?appid='+appid+"&id="+$("#hidid").val()+"&title="+$("#title").val()+"&content="+$("#txtContent").val()+"&type="+$("input[name='strType']:checked").val(),
			secureuri : false,
			fileElementId : 'uploadpic',
			dataType : 'json',
			success : function(data, status) {
				alert(data.message);
				$(".selectpicname").hide();
				$(".picuplist").html("<img id ='imgsrc' style='margin:30px 100px' src='OrgImgSrcHandler?id="+data.id+"&rnd="+RndNum(9)+"'></img>");
			},
			error : function(data, status, e) {
				alert("上传文件失败。");
			}
		});
		return false;
	}
	</script>
</body>
</html>