var retdata;

function ajaxFileUpload() {
	if ($("#uploadpic").val() == "") {
		alert("请选择上传图片！");
		return;
	}
	if(!(/(?:jpg|gif|png|jpeg|bmp)$/i.test($("#uploadpic").val()))) { 
		alert("只允许上传jpg|gif|png|jpeg|bmp格式的图片"); 
		return;
	}
	$.ajaxFileUpload({
		url : 'ImgRecHandler.do?moduleid='+vmid,
		secureuri : false,
		fileElementId : 'uploadpic',
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


function ajaxImageUpload() {
	var vmid=$("#moduleid").val();
	var foreignid=$("#foreignid").val();
	if ($("#uploadpic").val() == "") {
		alert("请选择上传图片！");
		return;
	}
	if(!(/(?:jpg|gif|png|jpeg|bmp)$/i.test($("#uploadpic").val()))) { 
		alert("只允许上传jpg|gif|png|jpeg|bmp格式的图片"); 
		return;
	}
	$.ajaxFileUpload({
		url : 'ImgRecHandler.do?moduleid='+vmid+'&fid='+foreignid,
		secureuri : false,
		fileElementId : 'uploadpic',
		dataType : 'json',
		success : function(data, status) {
			if(data.message>0){
				$("#imgdiv").html("<img src=\"ImgSrcHandler?"+data.message+"\" /><p class=\"compicupdel\"><span onclick=\"delImportPic("+data.message+");\">删除</span></p>");
				$("#imgid").val(data.message);
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

function InputFileChanged()
{
   var filepath;
   filepath=document.getElementById("uploadpic").value;     
   var filename=filepath.split('\\'); 
   var tabfile=filename[filename.length-1]; 
   $(".selectpicname").html("已选择：<em>"+tabfile+"</em>");            
}
function getImageHtml(mid){
	$.ajax({
		type : "get",
		url : "ImgRecHandler.do",
		async : true,
		cache : false,
		data : "do=get&mid="+mid,
		success : function(data) {
			if(data.length>0){
				$(".picuplist").html(data);
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
} 
function delPic(imageid){
	$.ajax({
		type : "get",
		url : "ImgRecHandler.do",
		async : true,
		cache : false,
		data : "do=del&pid="+imageid,
		success : function(data) {
			if(data=="1"){
				alert("删除成功");
				delPicli(imageid);
			}else{
				alert("删除失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function delImportPic(imageid){
	$.ajax({
		type : "get",
		url : "ImgRecHandler.do",
		async : true,
		cache : false,
		data : "do=del&pid="+imageid,
		success : function(data) {
			if(data=="1"){
				$("#imgdiv").html("");
				$("#imgid").val("");
			}else{
				alert("删除失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function delPicli(id){
	$("ul")[0].removeChild($("#"+id)[0]); 
}

function addPicli(id){
	var ul=$("ul")[0];    
	var varli="<img src='ImgSrcHandler?"+id+"' /><p><span class='picupdel' onclick=\"delPic("+id+");\">删除</span></p>";
	var li= document.createElement("li");
	li.id=id;
	li.innerHTML=varli; 
	ul.appendChild(li);    
}


function checkImport(obj) {
	if ($(obj).val() == "1") {
		//$(".compicup").css("display", "");
		$(".compicup").show(200);
	} else {
		//$(".compicup").css("display", "none");
		$(".compicup").hide(200);
	}
}

function ImportRemdImgChanged()
{
   var filepath;
   filepath=document.getElementById("uploadpic").value;     
   var filename=filepath.split('\\'); 
   var tabfile=filename[filename.length-1]; 
   $("#schecked").html("已选择：<em>"+tabfile+"</em>");            
}

