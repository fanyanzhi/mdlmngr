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
		<form id="form1" name="form1" action="PushTest.do" method="post">
			<div id="mdlright">
				<div class="mdlrignav">
				</div>
				<div class="mdlrigcontent" id="mdlrigcon">
					<div class="addform">
						<ul>
							<li><label class="leb"><em class="redword">*</em>用户名：</label> <input type="text" id="txtuser" name="txtuser" class="iptstyle" value="" /></li>
							<li><label class="leb"><em class="redword">*</em>设备类型：</label><select id="device"><option value="">全部</option><option value="iphone">iphone</option><option value="huawei">huawei</option><option value="xiaomi">xiaomi</option><option value="other">other</option></select></li>
							<li><label class="leb"><em class="redword">*</em>服务器：</label> <select id="server"><option value="">任意</option><option value="10.1.201.169">10.1.201.169</option><option value="10.1.201.173">10.1.201.173</option><option value="10.1.201.174">10.1.201.174</option><option value="10.1.201.175">175</option><option value="10.1.203.171">171</option></select></li>
							<li><label class="leb"><em class="redword">*</em>推送类型：</label><select id="pushtype"><option value="6">通讯消息</option><option value="1">更新条数</option><option value="2">单篇文献</option><option value="3">整刊信息</option><option value="4">文本消息</option><option value="5">链接消息</option></select></li>
							<li><label class="leb"><em class="redword"></em>内容：</label> <textarea class="comtextarea" name="txtComment" id="txtComment" cols="" rows=""></textarea></li>
							<li class="passave"><input type="submit" value="发送" class="mdlbtn" /></li>
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