<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
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
					<div class="commendsearch errorsearch">
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
					<span class="fleft">epub状态：<select id="epubstatus" name="epubstatus" class="comselt">
								<option value="">全部</option>
								<option value="1">有</option>
								<option value="0">无</option>
								<option value="2">null</option>
							</select>
							</span>
					<input name="" class="searchbtn" type="button" value="查询" onclick="searchFileList()" />
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
					<a href="javascript:void(0);" onclick="doFile(0);" class="addblock fleft">epub转换</a>&nbsp;<a href="javascript:void(0);" onclick="doFile(1);" class="addblock fleft">重新下载</a><!--&nbsp;<a href="javascript:void(0);" style="display:none" onclick="getFileDetail();" class="addblock fleft">获取数据</a>  -->
					</div>
					<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" />
				</div>
			</div>
		</div>

	</div>
	<!--   main  end  -->
	<script type="text/javascript">

		$(function() {
			searchFileList();
			getSearchFileMenu();
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
		
		
		function getParam(){
			var varcon = "";
			if ($.trim($("#hidTypeid").val()).length > 0) {
				varcon += "typeid=" + $.trim($("#hidTypeid").val()) + "&";
			}
			if ($.trim($("#txtKeyWord").val()).length > 0) {
				varcon += "keyword=" + encodeURI($.trim($("#txtKeyWord").val())) + "&";
			}
			if ($.trim($("#SeachField_SelectValue").val()).length > 0) {
				varcon += "filter=" + $.trim($("#SeachField_SelectValue").val())
						+ "&";
			}
			if ($.trim($("#epubstatus").val()).length > 0) {
				varcon += "es=" + encodeURI($.trim($("#epubstatus").val())) + "&";
			}
			return varcon;
		}

		function searchFileList() {
			var varcon = getParam();
			$("#hidparam").val(varcon);
			getFormDataCount(1);
		}
		
		function getSearchFileMenu(){
			$.ajax({
				type : "GET",
				url : "EpubTransSearchHandler.do",
				async : false,
				cache : false,
				data : "do=setsearchmenu&deftype=" + $("#hidTypeid").val(),
				success : function(data) {
					$("#rdftype").html("<em class=\"classclose\" title=\"关闭\" onclick=\"$('#rdftype').css('display','none');\"></em>"+ data);
					getEpubSearchField($("#hidTypeid").val(), 0);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器错误");
				}
			});
		}
		
		
		function doFile(vtype){
			var vdo = "";
			if(vtype==0){
				if (!confirm("谨慎操作，确认要重新转换符合查询条件的文章的epub数据？")) {
					return false;
				}
				vdo = "tranepub";
			}else{
				if (!confirm("谨慎操作，确认要重新符合查询条件的数据？")) {
					return false;
				}
				vdo = "redown";
			}
			showLoading();
			var varcon = getParam();
			$.ajax({
				url : $("#hidurl").val(),
				type : "GET",
				data : "do="+vdo+"&" + varcon,
				async : false,
				cache : false,
				success : function(data) {
					closeLoading();
					alert("处理"+data+"条数据");
				},
				error : function() {
					closeLoading();
					alert("服务器错误");
				}
			});
		}
		
		function getFileDetail(){
			showLoading();
			var varcon = getParam();
			$.ajax({
				url : $("#hidurl").val(),
				type : "GET",
				data : "do=vdetail&" + varcon,
				async : false,
				cache : false,
				success : function(data) {
					closeLoading();
					alert("处理"+data+"条数据");
				},
				error : function() {
					closeLoading();
					alert("服务器错误");
				}
			});
		}
		
	</script>
</body>
</html>