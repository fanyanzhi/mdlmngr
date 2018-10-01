<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>测试页</title>
<script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="js/json.js"></script>
</head>
<body  style="text-align:center">
<form>
<div style="border:thin; width:70%;">
		<p align="left">&nbsp;&nbsp;&nbsp;用户名：<span id="spanuser">ttod</span>		</p>
		<hr />
		<p>&nbsp;</p>
		<table width="80%" border="1" align="center" cellpadding="0" cellspacing="0" style=" border:1px;">
			<tr>
				<td align="right">模块：</td>
				<td align="left"><select id="modulename" name="modulename">
					<option value="sc">收藏</option>
					<option value="sc">订阅</option>
					<option value="sc">浏览</option>
					</select> </td>
			</tr>
			<tr>
			<td align="right">类别：</td>
				<td align="left">
				<select id="moduletype" name="moduletype">
					<option value="qk">期刊</option>
					<option value="zz">杂志</option>
				</select>				</td>
			</tr>
			<tr>
			<td align="right">文件标示：</td>
				<td align="left">
				<input type="text" name="txtsign" id="txtsign" />				</td>
			</tr>
			<tr>
			<td align="right">文件标题：</td>
				<td align="left">
				<input type="text" name="txttitle" id="txttitle" />				</td>
			</tr>
			<tr>
			<td align="right">文件简介：</td>
				<td align="left">
				  <textarea name="txtabstract" id="txtabstract"></textarea>				</td>
			</tr>
			<tr>
			<td colspan="2" align="center"><input type="button" value="提交" onclick="submits()" /></td>
			</tr>
		</table>
		<br />
		<br />
		<div style="text-align:left">
		<span>&nbsp;&nbsp;* 用户登录成功需要提交一次值</span><br />
		<span>&nbsp;&nbsp;* 用户注销需要提交一次值</span> <br />
		<a href="UserThirdLogin?to=qq">请使用你的QQ账号登陆</a><br />
		<a href="UserThirdLogin?to=sina">请使用你的新浪微博登陆</a>
		</div>
  </div>
</form>
</body>
<script type="text/javascript">
function submits(){
	alert($("#spanuser").text());
	var url="ModuleInfoHandler";
	//url="http://localhost:13827/JSON.aspx";
	var strVal={"modulename":$("#modulename").val(),
			"moduletype":$("#moduletype").val(),
			"un":encodeURI($("#spanuser").text()),
			"ts":encodeURI($("#txtsign").val()),
			"tt":encodeURI($("#txttitle").val()),
			"ta":encodeURI($("#txtabstract").val())};
	$.ajax({
		type : "GET",
		url : url,
		data:"param="+JSON.stringify(strVal),
		cache : false,
		async : true,
		success : function(data) {
			var obj=JSON.parse(data); 
			alert(obj.result);
		},
		error : function() {
			alert("失败！");
		}
	});	
}
</script>
</html>