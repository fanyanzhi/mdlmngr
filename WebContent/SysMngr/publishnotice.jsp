<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@taglib uri="http://usertaglib.mdlmngr.cnki.net" prefix="usertag"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
<script type="text/javascript" src="./js/simplepagetag.js"></script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1" action="PublishNotice.do" method="post">
		<input type="hidden" id="hidnid" name="hidnid" value="${NoticeInfo.id}" />
			<input type="hidden" id="hidurl" name="hidurl" value="${HandlerURL}" /> 
			<input type="hidden" name="hidparam" id="hidparam" value="" />
			 <input type="hidden" id="hidpagesize" name="hidpagesize" value="${PageSize}" /> 
			 <input type="hidden" name="hidcount" id="hidcount" value="" /> 
			 <input type="hidden" name="hidfunction" id="hidfunction" value="setHidUser" />
			 <input type="hidden" name="hidfunempty" id="hidfunempty" value="setUserEmpty" />
			 <input type="hidden" name="hiduname" id="hiduname" value="${UserID}" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="NoticeList.do" class="return fleft">返回</a>
					<c:if test="${NoticeInfo.isPublic ==1}">
						<a href="javascript:void(0);" onclick="window.location.href='OrgNotice.do?id=${NoticeInfo.noticeid}&sn='+encodeURI('${NoticeInfo.title}');" class="return">关联机构</a>
					</c:if>
					<div class="rigname">
						<h2>
							<em>通知发布</em>
						</h2>
					</div>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">

					<div class="selectform"  id="leftcon">
						<ul>
							<!-- <li><label class="statleb"> 用户名： </label><span id="spanuser"></span> <a href="javascript:void(0);" onclick="showusertab();" class="statsmore" title="重新选择">选择</a></li> -->
							<li><label class="statleb">通知标题：</label> 
							<input type="text" class="iptstyle" id="txtTitle" name="txtTitle" value="${NoticeInfo.title}"  onblur="chkInputLength(this,'tipTitle',30,'通知标题长度')" /><em id="tipTitle" class="wronginfo" style="display: none">输入格式错误</em>
							</li>
							<li><label class="statleb">通知类别：</label> <input name="strNoticeType" id="strNoticeType0" type="radio" value="0" ${NoticeInfo.type==null?'checked':NoticeInfo.type==0?'checked':''} /> <label class="lebrig">文本 </label> 
							                                         <input name="strNoticeType" id="strNoticeType1" type="radio" value="1" ${NoticeInfo.type==null?'':NoticeInfo.type==1?'checked':''}/> <label class="lebrig">链接</label>
							                                         <input name="strNoticeType" id="strNoticeType2" type="radio" value="2" ${NoticeInfo.type==null?'':NoticeInfo.type==2?'checked':''}/> <label class="lebrig">整刊</label></li>
							<li>
								<label class="statleb">公开级别：</label>
								<input name="strIsPublic" id="strIsPublic0" type="radio" value="0" ${NoticeInfo.isPublic==null?'checked':NoticeInfo.isPublic==0?'checked':''} />
								<label class="lebrig">所有机构</label> 
							    <input name="strIsPublic" id="strIsPublic1" type="radio" value="1" ${NoticeInfo.isPublic==null?'':NoticeInfo.isPublic==1?'checked':''}/>
							    <label class="lebrig">部分机构</label>
							</li>
							<li><label class="statleb">通知内容：</label> 
							<textarea id="txtNotice" name="txtNotice" class="notice" name="" onblur="chkInputLength(this,'tipNotice',500,'通知长度')" cols="" rows="">${NoticeInfo.content}</textarea><em id="tipNotice" class="wronginfo" style="display: none">输入格式错误</em>
							</li>
							<li class="passave"><input type="button" onclick="subNotice();" name="subbtn" id="subbtn" type="button" value="发布" class="mdlbtn" /><input type="button" onclick="document.getElementById('form1').reset();$('.wronginfo').css('display','none');$('.riginfo').css('display','none')" type="button"  value="重置" class="mdlbtn" /></li>
						</ul>
					</div>
					<div class="statsuser" id="divusertab" style="display: none">
						<div class="statsusertit">
						<a class="statsclose" href="javascript:void(0);" onclick="closediv()" title="关闭选择">关闭</a>
							<h2>选择用户</h2>
							<div class="searchuser">
								<input name="txtUserName" id="txtUserName" type="text" class="keyword" /><input name="" class="searchbtn" type="button" onclick="getSelUser()" value="查询" />
							</div>
						</div>
						<table width="100%" border="0" cellspacing="0" cellpadding="0" class="tabone">
							<tr>
								<th width="30">&nbsp;</th>
								<th width="50" class="num">&nbsp;</th>
								<th width="100">用户名</th>
								<th>上次登录时间</th>
							</tr>
						</table>
						<div class="statusertab" id="statuser">
						
						</div>
						<div class="useropt">
							<div class="selectopt"><input name="selectAll" id="selectAll" type="checkbox" value="" onclick="checkallUsers('selusers',this);" /> 全选 <a href="javascript:void(0);" onclick="clearuser()" class="delall">清空</a> </div>
							<usertag:UserPage id="ucPageBottom" pageSize="25" curPage="1" totalCount="100" onClick="" pageType="simple"/>
						</div>
					</div>

				</div>


			</div>
		</form>
		<div id="userpopalert" class="popalert" style="display:none;"> <a href="javascript:void(0);" onclick="$('#userpopalert').css('display','none');" class="closealert" title="关闭">关闭</a> 
			<p class="popnote">只能统计最近登录的前20名用户信息！</p>
			<p class="popbtn"><input name="" type="button" value="确定" class="mdlbtn"  onclick="$('#userpopalert').css('display','none');"/></p>
		</div>
	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		var arrUser = new Array();
		var objUser = {};
		$(function(){
			if($.trim($("#hiduname").val()).length>0){
				var vhiduid=$.trim($("#hiduname").val()).split(",");
				for( var i = 0; i < vhiduid.length; i++) {
					var tuid=vhiduid[i].split(":");
					var vobj = {
						id : tuid[0],
						username : tuid[1]
					};
					arrUser.push(vobj);
				}
				setUserSpan();
			}
		});
	</script>
</body>
</html>