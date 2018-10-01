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
<script type="text/javascript" src="./js/json.js"></script>
</head>
<body onkeydown="if(event.keyCode==13) {searchEpubTransData();return false;}" >
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" /> <input type="hidden" name="hidparam" id="hidparam" value="" /> 
		<input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> 
		<input type="hidden" id="hidSeaField" name="hidSeaField" value="${SeaFiled}" />
		<input type="hidden" name="hidTypeid" id="hidTypeid" value="${TypeID}" /> 
 		<input type="hidden" name="hidfunction" id="hidfunction" value="setSearchOData" />
 		<input type="hidden" name="hidcount" id="hidcount" value="" />
 		
		<div id="mdlright">
			<div class="mdlrignav conmendnav">
				<div class="commendsearch">
					<div class="classstore">
						<span id="spantypename" class="setclass">${TypeNameCH}</span>
						<p class="hideclass" id="rdftype"  tabindex="-1">							
						</p>
					</div>

					<div class="bigsearchblock searchblockdetail">
						<p id="ddlSearchField" class="selectsimi">
							<a id="ddlRightName_ddl" class="select" title="文件名"> 文件名</a> <span class="hideoption" style="display: block;"> <em title="文件名">文件名</em> <em title="文件ID">文件ID</em>
							</span>
						</p>
						<input name="txtKeyWord" id="txtKeyWord" value="${KeyWord}" type="text" class="keynoborder2" />
					</div>
					<input name="" class="searchbtn" type="button" value="查询" onclick="searchEpubTransData(1)" />
				</div>
				<usertag:UserPage id="ucPageTop" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple"/>
				<div class="rigname">
					<h2>
						<em>Epub文件转换</em>
					</h2>
				</div>
			</div>
			<div class="mdlrigcontent" id="mdlrigcon">
				<div id="divdata"></div>
				<div class="botopt" id="divbotopt" style="display:none">
					<div class="botleftopt">
					</div>
					<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
				</div>
			</div>
		</div>

	</div>
	<!--   main  end  -->
	<script type="text/javascript">

		$(function() {
			searchEpubTransData(0);
			getEpubTransDataSearchMenu();
		});
		$("#spantypename").bind("click", function() {
			if ($("#rdftype").is(":hidden")) {
				$("#rdftype").show();
				$("#rdftype").focus();
			} else {
				$("#rdftype").hide();
			}
		});
		$("#spantypename").bind("mousedown", function() {
			$("#rdftype").unbind("blur");
		});
		$("#spantypename").bind("mouseup", function() {
			$("#rdftype").bind("blur", function() {
				$("#rdftype").hide();
			});
		});
		
		function delODataFile(otype,fileid){
			if (!confirm("你确定要删除该记录吗")) {
				return false;
			}
			$.ajax({
				type : "get",
				url : $("#hidurl").val(),
				async : true,
				cache : false,
				data : "do=delrec&otype=" + otype+"&fileid=" + fileid,
				success : function(data) {
					if (data == "1") {
						alert("删除成功");
						window.location.reload();
					} else {
						alert("删除失败");
					}
				},
				error : function() {
					alert("加载失败");
				}
			});
		}

		/*$(".classstore").bind("mouseout",function(){
			setTimeout(function(){if(vtemp==0){$("#rdftype").hide();}}, 1000);
		});
		$("#rdftype").bind("mouseover",function(){
			vtemp = 1 ;
		});
		$("#rdftype").bind("mouseout",function(){
			vtemp = 0;
			$("#rdftype").hide();
		});*/
	</script>
</body>
</html>