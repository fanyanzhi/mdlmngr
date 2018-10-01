<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>CAJViewer云阅读管理系统</title>
<%@ include file="/WEB-INF/header.inc"%>
</head>
<body>
	<!--  head begin -->
	<%@ include file="../htm/head.html"%>
	<!--  maio begin -->
	<div id="main">
		<%@ include file="../htm/menu.html"%>
		<form id="form1" name="form1" action="AppInfo.do" method="post">
			<input type="hidden" name="hidid" id="hidid" value="${id }" />
			<div id="mdlright">
				<div class="mdlrignav">
					<a href="AppList.do" class="return">返回</a>
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div class="addform">
						<ul>
							<li><label class="leb"><em class="redword">*</em>AppId：</label> <input type="text" id="txtAppId" name="txtAppId" class="iptstyle" value="${appid}" ${appid==null?'':'readonly' }/></li>
							<li><label class="leb"><em class="redword">*</em>AppKey：</label> <input type="text" id="txtAppKey" name="txtAppKey" class="iptstyle" value="${appkey}" /><a href="javascript:createKey();">随机</a></li>
							<li><label class="leb"><em class="redword">*</em>是否付费：</label> <input name="isfee" id="isfee1" type="radio" value="1" ${fee==null?'checked':fee==1?'checked':''} /> <label class="lebrig">付费</label> <input name="isfee" id="isfee0" type="radio" value="0" ${fee==null?'':fee==0?'checked':''} /> <label class="lebrig">免费 </label></li>
							<li><label class="leb"><em class="redword">*</em>是否同步：</label> <input name="issync" id="issync1" type="radio" value="1" ${sync==null?'sync':sync==1?'checked':''} /> <label class="lebrig">是</label> <input name="issync" id="issync0" type="radio" value="0" ${sync==null?'':sync==0?'checked':''} /> <label class="lebrig">否 </label></li>
							<li><label class="leb"><em class="redword">*</em>是否启用：</label> <input name="txtStatus" id="txtStatus1" type="radio" value="1" ${status==null?'checked':status==1?'checked':''} /> <label class="lebrig">启用</label> <input name="txtStatus" id="txtStatus0" type="radio" value="0" ${status==null?'':status==0?'checked':''} /> <label class="lebrig">禁用 </label></li>
							
							
							<li><label class="leb"><em class="redword">*</em>验证用户：</label> <input name="isauth" id="isauth1" type="radio" value="1" ${auth==null?'checked':auth==1?'checked':''} /> <label class="lebrig">是</label> <input name="isauth" id="isauth0" type="radio" value="0" ${auth==null?'':auth==0?'checked':''} /> <label class="lebrig">否 </label></li>
							
							<li><label class="leb"><em class="redword">*</em>参加活动：</label> <input name="isactivity" id="isactivity1" type="radio" value="0" ${activity==null?'checked':activity==0?'checked':''} /> <label class="lebrig">否</label> <input name="isactivity" id="isactivity0" type="radio" value="1" ${activity==null?'':activity==1?'checked':''} /> <label class="lebrig">是 </label></li>
							<li><label class="leb"><em class="redword">*</em>是否漫游：</label> <input name="isroam" id="isroam0" type="radio" value="0" ${roam==null?'checked':roam==0?'checked':''} /> <label class="lebrig">否</label> <input name="isroam" id="isroam1" type="radio" value="1" ${roam==null?'':roam==1?'checked':''} /> <label class="lebrig">1天 </label><input name="isroam" id="isroam2" type="radio" value="1" ${roam==null?'':roam==15?'checked':''} /> <label class="lebrig">15天 </label><input name="isroam" id="isroam2" type="radio" value="1" ${roam==null?'':roam==30?'checked':''} /> <label class="lebrig">30天 </label></li>
							<li><label class="leb"><em class="redword"></em>备注：</label> <textarea class="comtextarea" name="txtComment" id="txtComment" cols="" rows="">${comment}</textarea></li>
							<li class="passave"><input type="submit" value="确定" class="mdlbtn" /><input type="button" onclick="document.getElementById('form1').reset();" value="重置" class="mdlbtn" /></li>
						</ul>
						<p id="prompt" class="prompt">${errmsg}</p>
					</div>

				</div>

			</div>
		</form>

	</div>
	<!--   main  end  -->
	<script type="text/javascript">
		function createKey() {
			var txt = randomTxt(16);
			$('#txtAppKey').val(txt);
		}

		function randomTxt(len) {
			len = len || 16;
			var $chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
			var maxPos = $chars.length;
			var key = '';
			for (i = 0; i < len; i++) {
				key += $chars.charAt(Math.floor(Math.random() * maxPos));
			}
			return key;
		}
	</script>
</body>
</html>