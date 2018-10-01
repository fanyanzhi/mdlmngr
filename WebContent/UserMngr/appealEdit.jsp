<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
<script type="text/javascript">
	$(function() {
		var oldStatus = $("#status").val();
		$("#rejectBtn").click(function() {
			if(oldStatus=="1"){
				alert("已处理不能驳回！");
				return false;
			}
// 			if(oldStatus=="-1"){
// 				alert("已驳回！");
// 				return false;
// 			}
			if(!($.trim($("#remark").val()).length>=5&&$.trim($("#remark").val()).length<=100)){
				alert("请输入5-100字备注信息！");
				return false;
			}
			submitForm(-1);
		});
		$("#passBtn").click(function() {
// 			if(oldStatus=="1"){
// 				alert("已处理！");
// 				return false;
// 			}
			submitForm(1);
		});
	});
	function submitForm(num) {
		$("#status").val(num);
		$("#form1").submit();
	}
</script>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1" method="post" action="">
			<input type="hidden" id="id" name="id" value="${entity.id }" />
			<input type="hidden" id="status" name="status" value="${entity.status}" />
			<div id="mdlright" style="position: fixed;">
				<div class="mdlrignav">
					<a href="AppealList.do" class="return">返回</a>
					<div class="rigname">
						<h2>
							<em>详情信息</em>
						</h2>
					</div>
				</div>
				<div id="mdlrigcon" class="mdlrigcontent">
					<div class="editcomform" >
						<ul>
							<li><label class="leb">申诉状态： </label>${entity.status=="1"?"已处理":entity.status=="0"?"待处理":entity.status=="-1"?"已驳回":""}&nbsp;</li>
							<li><label class="leb">认证人姓名：</label>${entity.realname}&nbsp;</li>
							<li><label class="leb">工作单位：</label>${entity.workunit}&nbsp;</li>
							<li><label class="leb">联系电话：</label>${entity.phone}&nbsp;</li>
							<li><label class="leb">电子邮箱：</label>${entity.email}&nbsp;</li>
							<li><label class="leb">提交账户：</label>${entity.username}&nbsp;</li>
							<li><label class="leb">身份证号：</label>${entity.cardnum}&nbsp;</li>
							
							<li >
								<label class="leb">身份证图片：</label>
								<table>
									<tr>
										<td style="width:90mm;height: 54mm;">
											<c:if test="${entity.front!=null && entity.front!=''}">
												<img style="height: 54mm;width: 85.6mm;" src="auth/?${entity.front}"/>
											</c:if>
											<c:if test="${entity.front==null || entity.front==''}">
												<div style="border: 1px gray solid;width:90mm;height: 54mm;background-color:#eaeaea;color:#999;text-align: center;line-height: 54mm;">暂无预览</div>
											</c:if>
										</td>
										<td style="width:90mm;height: 54mm;">
											<c:if test="${entity.back!=null && entity.back!=''}">
												<img style="height: 54mm;width: 85.6mm;" src="auth/?${entity.back}"/>
											</c:if>
											<c:if test="${entity.back==null || entity.back==''}">
												<div style="border: 1px gray solid;width:90mm;height: 54mm;background-color:#eaeaea;color:#999;text-align: center;line-height: 54mm;">暂无预览</div>
											</c:if>
											<!-- <img style="height: 54mm;width: 85.6mm;" src="auth/?9311ebd628df4939be814c590b4d7ced" /> -->
										</td>
									</tr>
									<tr>
										<td style="text-align: center;">正面</td>
										<td style="text-align: center;">反面</td>
									</tr>
								</table>
							</li>
							
							<li><label class="leb">申诉时间：</label><fmt:formatDate value="${entity.time}" type="both"/>&nbsp;</li>
							<li><label class="leb">申诉理由：</label>
								<textarea class="comtextarea" name="txtDescription" id="txtDescription"  
								 cols="25" rows="4" disabled="disabled">${entity.cause}</textarea>
							</li>
							<c:if test="${entity.updatetime!=null}">
								<li><label class="leb">处理时间：</label><fmt:formatDate value="${entity.updatetime}" type="both"/>&nbsp;</li>
							</c:if>
							<li><label class="leb">更多备注：</label>
								<textarea class="comtextarea" name="remark" id="remark" cols="25" rows="4">${entity.remark}</textarea>
							</li>

							<li class="passave">
								<input type="button" id="rejectBtn" value="驳回" class="mdlbtn" />
								<input type="button" id="passBtn" value="通过" class="mdlbtn" />
							</li>
						</ul>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!--   main  end  -->
	
</body>
</html>