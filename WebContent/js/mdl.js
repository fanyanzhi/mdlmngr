//public
function isNum(val) {
	var res = /^\d*[0-9]\d*$/;
	return res.test(val);
}

function isLetter(val) {
	var res = /^[a-zA-Z]{1}[A-Za-z_0-9]*$/;
	return res.test(val);
}

// 1判断是否为大于0的整数 2判断是否为字母
function inputOnBlur(flag, obj) {
	if (flag == 1) {
		if ($.trim($(obj).val()).length > 0) {
			if (!isNum(obj.value)) {
				obj.focus();
				alert("该文本框只能为整数");
				return false;
			}
		}
	} else if (flag == 2) {
		if ($.trim($(obj).val()).length > 0) {
			if (!isLetter(obj.value)) {
				obj.focus();
				alert("该文本框只能为字母");
				return false;
			}
		}
	}
}

function chkInputType(obj, tipobj, msg) {
	if ($.trim($(obj).val()).length > 0) {
		if (!isLetter(obj.value)) {
			$("#" + tipobj).attr("class", "wronginfo");
			$("#" + tipobj).css("display", "inline-block");
			$("#" + tipobj).html(msg + "必须以字母开头，可以包含数字或_");
			return false;
		} else {
			$("#" + tipobj).attr("class", "riginfo");
			$("#" + tipobj).css("display", "inline-block");
			return true;
		}

	} else {
		$("#" + tipobj).css("display", "none");
	}
}
function chkInputLength(obj, tipobj, len, msg) {
	if ($.trim($(obj).val()).length > 0) {
		if ($.trim($(obj).val()).length > len) {
			$("#" + tipobj).attr("class", "wronginfo");
			$("#" + tipobj).css("display", "inline-block");
			$("#" + tipobj).html(msg + "不得超过" + len + "个字符");
		} else {
			$("#" + tipobj).attr("class", "riginfo");
			$("#" + tipobj).css("display", "inline-block");
		}
	} else {
		$("#" + tipobj).css("display", "none");
	}
}

function showLoading() {
	$("#divShow").show();
}
function closeLoading() {
	$("#divShow").hide();
}
Array.prototype.in_array = function(e) {
	for ( var i = 0; i < this.length; i++) {
		if (this[i] == e)
			return true;
	}
	return false;
};

function checkall(parent, obj) {
	var ckelems = $("#" + parent + " input[type='checkbox'][name!='selectAll']");
	ckelems.prop("checked", obj.checked);
	$("input[type='checkbox'][name='selectAll']").prop("checked", obj.checked);
	return true;
}

function showPopAlert(flag, msg) {
	if (flag == 1) {
		$("#popalert p").attr("class", "susess");
		$("#popalert p").html(msg);
		$("#popalert").css("display", "block");
	} else {
		$("#popalert p").attr("class", "fail");
		$("#popalert p").html(msg);
		$("#popalert").css("display", "block");
	}
}

// 短日期(2008-03-29)
function IsDate(str) {
	var r = str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
	if (r == null) {
		return false;
	} else {
		var d = new Date(r[1], r[3] - 1, r[4]);
		return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d
				.getDate() == r[4]);
	}
}

function getFormDataCount(curpage) {
	showLoading();
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=getcount&" + $("#hidparam").val(),
		success : function(data) {
			closeLoading();
			if (data > 0) {
				// $("#hidcount").val(data);
				if (document.getElementById("hidpagesize") != undefined) {
					var pagesize = parseInt($("#hidpagesize").val(), 10);
					var totalcount = parseInt(data, 10);
					$("#hidcount").val(totalcount);
					var totalpage = totalcount % pagesize == 0 ? totalcount
							/ pagesize : parseInt(totalcount / pagesize) + 1;
					if (curpage > totalpage) {
						curpage = totalpage;
					}
				}
				curpage = curpage < 1 ? 1 : curpage;
				getFormData(curpage);
			} else {
				if ($("#hidfunempty").length > 0
						&& $.trim($("#hidfunempty").val()).length > 0) {
					eval($.trim($("#hidfunempty").val()))();
				} else {
					setEmpty();
				}
				$("#feeCountSpan").html("0");
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			closeLoading();
			alert("服务器错误");
		}
	});
}

function getActionFormDataCount(curpage) {
	showLoading();
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=getcount&" + $("#hidparam").val(),
		success : function(data) {
			closeLoading();
			if (data > 0) {
				// $("#hidcount").val(data);
				if (document.getElementById("hidpagesize") != undefined) {
					var pagesize = parseInt($("#hidpagesize").val(), 10);
					var totalcount = parseInt(data, 10);
					$("#hidcount").val(totalcount);
					var totalpage = totalcount % pagesize == 0 ? totalcount
							/ pagesize : parseInt(totalcount / pagesize) + 1;
					if (curpage > totalpage) {
						curpage = totalpage;
					}
				}
				curpage = curpage < 1 ? 1 : curpage;
				getFormData(curpage);
				//$("#recordCount").html("记录条数：<em style=\"color:red\">"+totalcount+"</em>");
			} else {
				//$("#recordCount").html("记录条数：<em style=\"color:red\">0</em>");
				if ($("#hidfunempty").length > 0
						&& $.trim($("#hidfunempty").val()).length > 0) {
					eval($.trim($("#hidfunempty").val()))();
				} else {
					setEmpty();
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			closeLoading();
			alert("服务器错误");
		}
	});
}

function setEmpty() {
	$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
	if (document.getElementById("ucPageBottom") != undefined) {
		$("#ucPageBottom").css("display", "none");
		$("#ucPageBottom").parent().css("display", "none");
	}
	if (document.getElementById("ucPageTop") != undefined) {
		$("#ucPageTop").css("visibility", "hidden");
	}
}

function getFormData(curpage) {
	if (document.getElementById("hidcurpage") != undefined) {
		$("#hidcurpage").val(curpage);
	}
	showLoading();
	var varParam = "";
	if (document.getElementById("hidpagesize") != undefined) {
		var start = (curpage - 1) * $("#hidpagesize").val() + 1;
		varParam = "start=" + start + "&len=" + $("#hidpagesize").val() + "&";
	}
	varParam = varParam + $("#hidparam").val();
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=getlist&" + varParam,
		success : function(data) {
			closeLoading();
			if ($("#hidfunction").length > 0
					&& $.trim($("#hidfunction").val()).length > 0) {
				eval($.trim($("#hidfunction").val()))(data, curpage);
			} else {
				setFormData(data, curpage);
			}

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			closeLoading();
			alert("服务器错误");
		}
	});

}


function getEpubTransFormData(curpage) {
	if (document.getElementById("hidcurpage") != undefined) {
		$("#hidcurpage").val(curpage);
	}
	showLoading();
	var varParam = "";
	if (document.getElementById("hidpagesize") != undefined) {
		var start = (curpage - 1) * $("#hidpagesize").val() + 1;
		varParam = "start=" + start + "&len=" + $("#hidpagesize").val() + "&";
	}
	varParam = varParam + $("#hidparam").val();
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=getlist&" + varParam,
		success : function(data) {
			closeLoading();
			if ($("#hidfunction").length > 0
					&& $.trim($("#hidfunction").val()).length > 0) {
				eval($.trim($("#hidfunction").val()))(data, curpage);
			} else {
				setFormData(data, curpage);
			}

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			closeLoading();
			alert("服务器错误");
		}
	});

}


function setFormData(vdata, curpage) {
	$("#divdata").html(vdata);
	if (document.getElementById("ucPageBottom") != undefined) {
		$("#ucPageBottom").css("display", "block");
		$("#ucPageBottom").parent().css("display", "block");
		SetPageTagData("ucPageBottom", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}

	if (document.getElementById("ucPageTop") != undefined) {
		$("#ucPageTop").css("visibility", "visible");
		SetSimPageTagData("ucPageTop", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}
	if (document.getElementsByName("selectAll") != undefined) {
		$("#selectAll").prop("checked", false);
	}
	var count = $("#feeCount").val();
	$("#feeCountSpan").html(count);
	
}

// onlineuserlist.jsp
function UserOnlineSearch() {
	var varcon = "";
	if ($.trim($("#txtUserName").val()).length > 0) {
		varcon = "uname=" + $.trim($("#txtUserName").val());
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function setOnlineUserInfo(vdata, curpage) {
	$("#divdata").html(vdata);
	if (document.getElementById("ucPageBottom") != undefined) {
		$("#ucPageBottom").css("display", "block");
		$("#ucPageBottom").parent().css("display", "block");
		SetPageTagData("ucPageBottom", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}

	if (document.getElementById("ucPageTop") != undefined) {
		$("#ucPageTop").css("visibility", "visible");
		SetSimPageTagData("ucPageTop", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}
	getUserOnlineCount();
}

function getUserOnlineCount() {
	if ($.trim($("#hidusers").val()).length > 0) {
		var uid = $.trim($("#hidusers").val());
		$.ajax({
			type : "get",
			url : $("#hidurl").val(),
			async : true,
			cache : false,
			data : "do=onlinecount&vusers=" + uid,
			success : function(data) {
				if (data.length > 0) {
					var obj = JSON.parse(data);
					for ( var item in obj) {
						$("#span" + item).html(obj[item]);
					}
				}
			},
			error : function() {
				alert("服务器错误");
			}
		});
	}
}

// onlineuserdetail.jsp
function delOnlineUser(uid) {
	if (uid.length == 0) {
		return false;
	}
	if (!confirm("你确定要删除该记录吗")) {
		return false;
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=deluser&uid=" + uid,
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
function delMultiUsers() {
	var vUserID = "";
	$("input[name='chkuserid']").each(function() {
		if ($(this).prop("checked") == true)
			vUserID = vUserID + $(this).val() + ",";
	});
	if (vUserID == "") {
		alert("请选择要删除的用户！");
		return false;
	}
	delOnlineUser(vUserID);
}

function setOnlineUserDetail(vdata, curpage) {
	if (vdata.length == 0) {
		$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
		$("#divbotopt").hide();
	} else {
		$("#divdata").html(vdata);
	}
}

// function getOnLineUser() {
// $.ajax({
// type : "GET",
// url : $("#hidurl").val(),
// cache : false,
// async : true,
// data : "do=getlist",
// success : function(data) {
// $("#mdlrigcon").html(data);
// },
// error : function(XMLHttpRequest, textStatus, errorThrown) {
// alert(XMLHttpRequest.status);
// alert(XMLHttpRequest.readyState);
// alert(textStatus);
// },
//
// });
// }

// loginloglist.jsp
function LoginLogSearch() {
	var varcon = "";
	if ($.trim($("#selappid").val()).length > 0) {
		varcon = "appid=" + $.trim($("#selappid").val()) +"&";
	}
	if ($.trim($("#txtUserName").val()).length > 0) {
		varcon = varcon + "uname=" + $.trim($("#txtUserName").val());
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

// loginlogdetail.jsp
function getUserLoginLog() {
	var varcon = "";
	if ($.trim($("#hiduname").val()).length > 0) {
		varcon = "uname=" + $.trim($("#hiduname").val()) + "&";
	}
	if ($.trim($("#hidtheparam").val()).length > 0) {
		varcon = varcon + $.trim($("#hidtheparam").val());
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
	// $.ajax({
	// type : "GET",
	// url : $("#hidurl").val(),
	// cache : false,
	// async : true,
	// data : "do=getlist&uid=" + $("#hiduserid").val(),
	// success : function(data) {
	// $("#mdlrigcon").html(data);
	// },
	// error : function(XMLHttpRequest, textStatus, errorThrown) {
	// alert(XMLHttpRequest.status);
	// alert(XMLHttpRequest.readyState);
	// alert(textStatus);
	// },
	//
	// });
}

function delSingleLog(logid) {
	if (logid.length == 0) {
		return false;
	}
	if (!confirm("你确定要删除该记录吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=dellog&lid=" + logid + "&uname="
				+ $.trim($("#hiduname").val()),
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败");
		}
	});
}
function delMultiLog() {
	var vLogID = "";
	$("input[name='chklogid']").each(function() {
		if ($(this).prop("checked") == true)
			vLogID = vLogID + $(this).val() + ",";
	});
	if (vLogID == "") {
		alert("请选择要删除的日志记录！");
		return false;
	}
	delSingleLog(vLogID);
}

// statisticanalysis.jsp
function getSelUser() {
	var varcon = "";
	if ($.trim($("#txtUserName").val()).length > 0) {
		varcon = "uname=" + $.trim($("#txtUserName").val());
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}
function setUserEmpty() {
	$("#statuser").html("<div class=\"nodata\">还没有数据。</div>");
	if (document.getElementById("ucPageBottom") != undefined) {
		$("#ucPageBottom").css("display", "none");
		$("#ucPageBottom").parent().css("display", "none");
	}
	if (document.getElementById("ucPageTop") != undefined) {
		$("#ucPageTop").css("visibility", "hidden");
	}
}
function setHidUser(vdata, curpage) {
	$("#statuser").html(vdata);
	if (document.getElementById("ucPageBottom") != undefined) {
		$("#ucPageBottom").css("display", "block");
		$("#ucPageBottom").parent().css("display", "block");
		SetSimPageTagData("ucPageBottom", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}
	if (document.getElementById("ucPageTop") != undefined) {
		$("#ucPageTop").css("visibility", "visible");
		SetSimPageTagData("ucPageTop", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}
	setUserCheck();
	$("#selectAll").prop("checked", false);
}
function showusertab() {
	if ($("#divusertab").css("display") == "none") {
		// if($("#statuser").html().length==0){
		getSelUser();
		// }

		$("#leftcon").css("float", "left");
		$("#divusertab").show();
		setUserCheck();
	}
	// else {
	// $("#leftcon").css("float", "");
	// $("#divusertab").hide(500);
	// }
}
function selectUserback(obj, tt) {
	if (obj.checked) {
		if (!arrUser.in_array($(obj).val())) {
			arrUser.push($(obj).val());
		}
	} else {
		arrUser.pop($(obj).val());
	}
	for ( var i = 0; i < arrUser.length; i++) {
		alert(arrUser[i]);
	}
}

function selectUser(obj, val) {
	var vid = $(obj).val();
	var vobj = {
		id : vid,
		username : val
	};
	if (obj.checked) {
		if (!checkArrUser()) {
			$(obj).prop("checked", false);
			return;
		} else {
			arrUser.push(vobj);
		}
	} else {
		for ( var i = 0; i < arrUser.length; i++) {
			if (arrUser[i].id == vobj.id) {
				arrUser.splice(i, 1);
			}
		}
	}
	setUserSpan();
}
function delSeledUser(uid) {
	for ( var i = 0; i < arrUser.length; i++) {
		if (arrUser[i].id == uid) {
			arrUser.splice(i, 1);
		}
	}
	calUserCheck(uid);
	setUserSpan();

}
function setUserSpan() {
	var vhtml = "";
	for ( var i = 0; i < arrUser.length; i++) {
		vhtml += "<a href=\"javascrpt:void(0);\" class=\"statsname\" onclick=\"delSeledUser("
				+ arrUser[i].id
				+ ");return false;\" title=\"点击删除\">"
				+ arrUser[i].username + "</a>";
	}
	$("#spanuser").html(vhtml);
}
function setUserCheck() {
	if (arrUser.length > 0) {
		for ( var i = 0; i < arrUser.length; i++) {
			$("input[name='seluserid']").each(function() {
				if ($(this).attr("value") == arrUser[i].id) {
					$(this).prop("checked", true);
				}
			});
		}
	}
}
function calUserCheck(uid) {
	$("input[name='seluserid']").each(function() {
		if ($(this).attr("value") == uid) {
			$(this).prop("checked", false);
		}
	});

}
function checkallUsers(parent, obj) {
	// var ckelems = $("#" + parent + "
	// input[type='checkbox'][name!='selectAll']");
	// ckelems.attr("checked", obj.checked);

	$("#" + parent + " input[type='checkbox'][name!='selectAll']").each(
			function() {
				$(this).prop("checked", obj.checked);
				if ($(this).prop("checked")) {
					if (!arrUser.objin_array($(this).attr("value"))) {
						var vobj = {
							id : $(this).attr("value"),
							username : $(this).attr("ckval")
						};
						if (!checkArrUser()) {
							$(obj).prop("checked", false);
							$(this).prop("checked", false);
							return;
						} else {
							arrUser.push(vobj);
						}
					}
				} else {
					for ( var i = 0; i < arrUser.length; i++) {
						if (arrUser[i].id == $(this).attr("value")) {
							arrUser.splice(i, 1);
						}
					}
				}
			});
	setUserSpan();
	return true;
}
Array.prototype.objin_array = function(uid) {
	for ( var i = 0; i < this.length; i++) {
		if (this[i].id == uid) {
			return true;
		}
	}
	return false;
};
function checkArrUser() {
	if (arrUser.length > 19) {
		$("#userpopalert").show();
		return false;
	}
	return true;
}

function clearuser() {
	arrUser.length = 0;
	$("#divusertab input[type='checkbox']").prop("checked", false);
	setUserSpan();
}

function closediv() {
	$("#leftcon").css("float", "");
	$("#divusertab").hide(500);
}

function beginTongJi(flag) {
	var vParam = "";
	// if (arrUser.length == 0) {
	// alert("请选择统计用户"); // 人数最多30人
	// return false;
	// }
	var vUid = "";
	for ( var i = 0; i < arrUser.length; i++) {
		vUid = vUid + arrUser[i].id + ",";
	}
	if (vUid.length > 0) {
		vParam = "uname=" + vUid + "&";
	}
	var vStartDate = $.trim($("#txtStartDate").val());
	var vEndDate = $.trim($("#txtEndDate").val());
	if (vStartDate != "" && !IsDate(vStartDate)) {
		alert('输入日期格式不正确');
		return false;
	}
	if (vEndDate != "" && !IsDate(vEndDate)) {
		alert('输入日期格式不正确');
		return false;
	}
	if ($.trim($("#txtStartDate").val()).length > 0
			&& $.trim($("#txtEndDate").val()).length > 0) {
		if ($("#txtStartDate").val() > $("#txtEndDate").val()) {
			var tempTime = $("#txtStartDate").val();
			$("#txtStartDate").val($("#txtEndDate").val());
			$("#txtEndDate").val(tempTime);
		}
	}

	var teminal = "";
	$("input[type='checkbox'][name='terminal']").each(function() {
		if ($(this).prop("checked")) {
			teminal += $(this).val() + ",";
		}
	});
	if (teminal.length > 0) {
		vParam = vParam + "tmal=" + teminal + "&";
	}
	if ($.trim($("#txtStartDate").val()).length > 0) {
		vParam = vParam + "startdate=" + $.trim($("#txtStartDate").val()) + "&";
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		vParam = vParam + "enddate=" + $.trim($("#txtEndDate").val()) + "&";
	}
	if($("#singleTerminal")!=null&&$.trim($("#singleTerminal").val()).length>0){
		vParam = vParam + "singleTer=" + $.trim($("#singleTerminal").val()) + "&";
	}
	if (flag == 0) {
		window.location.href = "StatisticanList.do?" + vParam;
	} else {
		window.location.href = "LoginLogPicAnalysis.do?" + vParam;
	}

}

// statisticanlist.jsp
function getstatistican(ordertype, ordernum) {
	if (ordertype == 0) {
		if (ordernum == 0) { // 0升序
			$("#logintime0").unbind("click").bind("click", function() {
				getstatistican(0, 1);
			});
			$("#logintime0").attr("class", "orderbtnBdoit");
		} else { // 1倒序
			$("#logintime0").unbind("click").bind("click", function() {
				getstatistican(0, 0);
			});
			$("#logintime0").attr("class", "orderbtndoit");
		}
		$("#logincount0").attr("class", "orderbtn");
		$("#logincount0").unbind("click").bind("click", function() {
			getstatistican(1, 1);
		});
	} else if (ordertype == 1) {
		if (ordernum == 0) { // 0升序
			$("#logincount0").unbind("click").bind("click", function() {
				getstatistican(1, 1);
			});
			$("#logincount0").attr("class", "orderbtnBdoit");
		} else { // 1倒序
			$("#logincount0").unbind("click").bind("click", function() {
				getstatistican(1, 0);
			});
			$("#logincount0").attr("class", "orderbtndoit");
		}
		$("#logintime0").attr("class", "orderbtn");
		$("#logintime0").unbind("click").bind("click", function() {
			getstatistican(0, 1);
		});
	}
	var varcon = $("#hidtheparam").val();

	varcon = varcon + "ordtype=" + ordertype + "&order=" + ordernum;

	$("#hidparam").val(varcon);
	getFormDataCount(1);
	// getFormData(1);
}
function setAnalysisData(vdata, curpage) {
	$("#divdata").html(vdata);
	if (document.getElementById("ucPageBottom") != undefined) {
		$("#ucPageBottom").css("display", "block");
		$("#ucPageBottom").parent().css("display", "block");
		SetPageTagData("ucPageBottom", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}
	if (document.getElementById("ucPageTop") != undefined) {
		$("#ucPageTop").css("visibility", "visible");
		SetSimPageTagData("ucPageTop", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}
	$("#emUcount").html($("#hidcount").val());
	$("#emLcount").html($("#alllogincount").val());
}
function setAnalysisEmpty() {
	$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
	if (document.getElementById("ucPageBottom") != undefined) {
		$("#ucPageBottom").css("display", "none");
		$("#ucPageBottom").parent().css("display", "none");
	}
	if (document.getElementById("ucPageTop") != undefined) {
		$("#ucPageTop").css("visibility", "hidden");
	}
	$("#tiptotal").html('');
}

// loginlogpicanalysis.jsp
function getLoginLogAnalysisPic(vtype) {
	showLoading();
	var vwidth = window.screen.width - 240 - 150;
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=getpic&" + $("#hidparam").val() + "&picw=" + vwidth + "&vtype=" + vtype,
		success : function(data) {
			closeLoading();
			if (data.length > 0) {
				$("#divdata").html(data);
			} else {
				$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			closeLoading();
			alert("服务器错误");
		}
	});
}

// onlinepicanalysis.jsp
function getOnlineAnalysisPic() {
	showLoading();
	var vwidth = window.screen.width - 240 - 150;
	var varcon = "";
	if ($.trim($("#txtStartDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtStartDate").val()))) {
			alert("开始日期格式不正确");
			return false;
		}
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtEndDate").val()))) {
			alert("结束日期格式不正确");
			return false;
		}
	}

	if ($.trim($("#txtStartDate").val()).length > 0
			&& $.trim($("#txtEndDate").val()).length > 0) {
		if ($("#txtStartDate").val() > $("#txtEndDate").val()) {
			var tempTime = $("#txtStartDate").val();
			$("#txtStartDate").val($("#txtEndDate").val());
			$("#txtEndDate").val(tempTime);
		}
	}

	if ($.trim($("#txtStartDate").val()).length > 0) {
		varcon = varcon + "startdate=" + $.trim($("#txtStartDate").val()) + "&";
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		varcon = varcon + "enddate=" + $.trim($("#txtEndDate").val()) + "&";
	}
	if ($.trim($("#hidpageparam").val()).length > 0) {
		varcon = varcon + $.trim($("#hidpageparam").val()) + "&";
	}

	$("#hidparam").val(varcon);

	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=getpic&" + $("#hidparam").val() + "&picw=" + vwidth,
		success : function(data) {
			closeLoading();
			if (data.length > 0) {
				$("#divdata").html(data);
			} else {
				$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			closeLoading();
			alert("服务器错误");
		}
	});
}

Date.prototype.format = function(format) {
	/*
	 * format="yyyy-MM-dd hh:mm:ss";
	 */
	var o = {
		"M+" : this.getMonth() + 1,
		"d+" : this.getDate(),
		"h+" : this.getHours(),
		"m+" : this.getMinutes(),
		"s+" : this.getSeconds(),
		"q+" : Math.floor((this.getMonth() + 3) / 3),
		"S" : this.getMilliseconds()
	};

	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	}

	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
};
function getLastMonthYestdy(date) {
	var daysInMonth = new Array([ 0 ], [ 31 ], [ 28 ], [ 31 ], [ 30 ], [ 31 ],
			[ 30 ], [ 31 ], [ 31 ], [ 30 ], [ 31 ], [ 30 ], [ 31 ]);
	var strYear = date.getFullYear();
	var strDay = date.getDate();
	var strMonth = date.getMonth() + 1;
	if (strYear % 4 == 0 && strYear % 100 != 0) {
		daysInMonth[2] = 29;
	}
	if (strMonth - 1 == 0) {
		strYear -= 1;
		strMonth = 12;
	} else {
		strMonth -= 1;
	}
	strDay = daysInMonth[strMonth] >= strDay ? strDay : daysInMonth[strMonth];
	if (strMonth < 10) {
		strMonth = "0" + strMonth;
	}
	if (strDay < 10) {
		strDay = "0" + strDay;
	}
	datastr = strYear + "-" + strMonth + "-" + strDay;
	return datastr;
}

// moduleinfo.jsp
function chkModuleInfoInput() {
	$("#prompt").html('');
	var varModuleName = $.trim($("#txtModuleName").val());
	var varModuleNameEN = $.trim($("#txtModuleNameEN").val());
	var varModuleNameCH = $.trim($("#txtModuleNameCH").val());
	var bResult = true;
	var arrtip = {
		"tipModuleName" : "",
		"tipModuleName_EN" : "",
		"tipModuleName_CH" : "",
		"tipDescription" : ""
	};
	if (varModuleName.length > 0) {
		if (!isLetter(varModuleName)) {
			arrtip.tipModuleName = "模块名称必须以字母开头，可以包含数字或_";
		}
		if (varModuleName.length > 20 && arrtip.tipModuleName.length == 0) {
			arrtip.tipModuleName = "模块名称长度不得超过20个字符";
		}
	} else {
		arrtip.tipModuleName = "模块名称不能为空";
	}
	if (varModuleNameEN.length > 0) {
		if (!isLetter(varModuleNameEN)) {
			arrtip.tipModuleName_EN = "英文名称必须以字母开头，可以包含数字或_";
		}
		if (varModuleNameEN.length > 30 && arrtip.tipModuleName_EN.length == 0) {
			arrtip.tipModuleName_EN = "英文名称长度不得超过30个字符";
		}
	} else {
		arrtip.tipModuleName_EN = "英文名称不能为空";
	}
	if (varModuleNameCH.length > 0) {
		if (varModuleNameCH.length > 25) {
			arrtip.tipModuleName_CH = "中文名称长度不得超过25个字符";
		}
	} else {
		arrtip.tipModuleName_CH = "中文名称不能为空";
	}
	if ($.trim($("#txtDescription").val()).length > 200) {
		arrtip.tipDescription = "描述信息长度不得超过200个字符";
	}
	for ( var p in arrtip) {
		if (arrtip[p].length > 0) {
			$("#" + p).attr("class", "wronginfo");
			// $("#" + p).show();
			$("#" + p).css("display", "inline-block");
			$("#" + p).html(arrtip[p]);
			bResult = false;
		} else {
			$("#" + p).attr("class", "riginfo");
			// $("#" + p).show();
			$("#" + p).css("display", "inline-block");
		}
	}
	return bResult;
}

// moduletypeinfo.jsp
function chkModuleTypeInput() {
	$("#prompt").html('');
	var varTypeName = $.trim($("#txtTypeName").val());
	var varTypeNameEN = $.trim($("#txtTypeNameEN").val());
	var varTypeNameCH = $.trim($("#txtTypeNameCH").val());
	var bResult = true;
	var arrtip = {
		"tipTypeName" : "",
		"tipTypeName_EN" : "",
		"tipTypeName_CH" : "",
		"tipDescription" : ""
	};
	if (varTypeName.length > 0) {
		if (!isLetter(varTypeName)) {
			arrtip.tipTypeName = "类别名称必须以字母开头，可以包含数字或_";
		}
		if (varTypeName.length > 20 && arrtip.tipTypeName.length == 0) {
			arrtip.tipTypeName = "类别名称长度不得超过20个字符";
		}
	} else {
		arrtip.tipTypeName = "类别名称不能为空";
	}
	if (varTypeNameEN.length > 0) {
		if (!isLetter(varTypeNameEN)) {
			arrtip.tipTypeName_EN = "类别英文名称必须以字母开头，可以包含数字或_";

		}
		if (varTypeNameEN.length > 30 && arrtip.tipTypeName_EN.length == 0) {
			arrtip.tipTypeName_EN = "类别英文名称长度不得超过30个字符";
		}
	} else {
		arrtip.tipTypeName_EN = "类别英文名称不能为空";
	}
	if (varTypeNameCH.length > 0) {
		if (varTypeNameCH.length > 25) {
			arrtip.tipTypeName_CH = "类别中文名称长度不得超过25个字符";
		}
	} else {
		arrtip.tipTypeName_CH = "类别中文名称不能为空";
	}

	// if ($.trim($("#ddlModuleInfo_SelectValue").val()).length ==0) {
	// arrtip.tipModuleInfo = "请选择一个模块";
	// }

	if ($.trim($("#txtDescription").val()).length > 200) {
		arrtip.tipDescription = "描述信息长度不得超过200个字符";
	}
	for ( var p in arrtip) {
		if (arrtip[p].length > 0) {
			$("#" + p).attr("class", "wronginfo");
			// $("#" + p).show();
			$("#" + p).css("display", "inline-block");

			$("#" + p).html(arrtip[p]);
			bResult = false;
		} else {
			$("#" + p).attr("class", "riginfo");
			// $("#" + p).show();
			$("#" + p).css("display", "inline-block");
		}
	}
	return bResult;
}

// modulelist.jsp
function getModuleList() {
	getFormDataCount(1);
};

function delModuleInfo(tabid) {
	checkData('chkmoduledata', tabid);

	if (!confirm("确认删除该条数据吗？")) {
		return false;
	}
	if (chkdata == 1) {
		if (!confirm("表中存在数据，是否继续删除？")) {
			return false;
		}
	}
	showLoading();
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "tabid=" + tabid + "&do=delmodule",
		success : function(data) {
			closeLoading();
			if (data == 1) {
				alert("删除成功");
				window.location.reload();
			} else if (data == -1) {
				alert("删除失败");
			} else if (data == 2) {
				alert("参数错误");
			}
		},
		error : function() {
			closeLoading();
			alert("删除失败！");
		}
	});
}
function delModuleTypeInfo(typeid, tabid) {
	checkData('chktypedata', tabid, typeid);
	if (!confirm("确认删除该条数据吗？")) {
		return false;
	}
	if (chkdata == 1) {
		if (!confirm("表中存在数据，是否继续删除？")) {
			return;
		}
	}
	showLoading();
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "typeid=" + typeid + "&do=delmoduletype",
		success : function(data) {
			closeLoading();
			if (data == 1) {
				alert("删除 成功");
				window.location.reload();
			} else if (data == -1) {
				alert("删除失败");
			} else if (data == 2) {
				alert("参数错误");
			}
		},
		error : function() {
			closeLoading();
			alert("删除失败！");
		}
	});
}
function checkData(type, tableid, typeid) {
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : false,
		data : "typeid=" + typeid + "&tabid=" + tableid + "&do=" + type,
		success : function(data) {
			chkdata = data;
		},
		error : function() {
			chkdata = data;
		}
	});
}

// modulerecyclelist.jsp
function getRecycleModuleList() {
	getFormDataCount(1);
}
function recycleModule(type, id, status) {
	if (status == "1") {
		alert("请先恢复父级模块");
		return false;
	}
	showLoading();
	$.ajax({
		type : "GET",
		url : $("#hidurl").val() + "?do=recycle&type=" + type + "&id=" + id,
		cache : false,
		async : true,
		success : function(data) {
			closeLoading();
			if (data == "1") {
				alert("恢复成功");
				window.location.reload();
			} else {
				showPopAlert(1, "恢复失败");
			}
		},
		error : function() {
			closeLoading();
			alert("数据加载失败！");
		}
	});
}

// modulecontentinfo.jsp
function getModuleContentList() {
	showLoading();
	$.ajax({
		type : "GET",
		url : $("#hidurl").val() + "?do=getlist&hidtabid="
				+ $.trim($("#hidtabid").val()),
		cache : false,
		async : true,
		success : function(data) {
			closeLoading();
			$("#mdlrigcon").html(data);
			var vtab = document.getElementById("fieldtab");
			var i = vtab.rows.length - 2;
			$("#hidCount").val(i);
			$("#hidFlagCount").val(i);
		},
		error : function() {
			closeLoading();
			alert("服务器错误！");
		}
	});
};

function setModuleColumn(vdata, curpage) {
	$("#divdata").html(vdata);
	if (document.getElementById("ucPageBottom") != undefined) {
		$("#ucPageBottom").css("display", "block");
		$("#ucPageBottom").parent().css("display", "block");
		SetPageTagData("ucPageBottom", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}
	if (document.getElementById("ucPageTop") != undefined) {
		$("#ucPageTop").css("visibility", "visible");
		SetSimPageTagData("ucPageTop", curpage, $("#hidcount").val(), $(
				"#hidpagesize").val(), getFormData);
	}
	var vtab = document.getElementById("fieldtab");
	var i = vtab.rows.length - 3;
	$("#hidCount").val(i);
}

function addRow() {
	var vtab = document.getElementById("fieldtab");
	var vCount = $("#hidCount").val();
	var j = ++vCount;
	$("#hidCount").val(j);

	var i = vtab.rows.length - 1;
	var row = vtab.insertRow(i); // 行对象
	row.className = "addnewline";
	var col; // 列对象
	col = row.insertCell(0);
	col.className = "num";
	col.innerHTML = i;
	col = row.insertCell(1);
	col.className = "tabcent";
	col.innerHTML = "<input type='text' name='filedname" + j
			+ "' id='filedname" + j + "' class=\"addvalue\"/>";
	col = row.insertCell(2);
	col.className = "tabcent";
	col.innerHTML = "<select name='filedtype"
			+ j
			+ "' id='isdisplay"
			+ j
			+ "' onchange='changeinput(this)'><option value='varchar'>string</option><option value='int'>int</option><option value='datetime'>datetime</option><option value='text'>text</option></select>";
	col = row.insertCell(3);
	col.className = "tabcent";
	col.innerHTML = "<input type='text' checked='yes' name='filedlen" + j
			+ "' id='filedlen" + j + "' class=\"addvalue\"/>";
	col = row.insertCell(4);
	col.className = "tabcent";
	col.innerHTML = "<input type='text' name='fileden" + j + "' id='fileden"
			+ j + "' class=\"addvalue\"/>";
	col = row.insertCell(5);
	col.className = "tabcent";
	col.innerHTML = "<input type='text' name='filedch" + j + "' id='filedch"
			+ j + "' class=\"addvalue\"/>";
	col = row.insertCell(6);
	col.className = "tabcent";
	col.innerHTML = "<select name='isdisplay"
			+ j
			+ "' id='isdisplay"
			+ j
			+ "'><option value='true'>是</option><option value='false'>否</option></select>";
	col = row.insertCell(7);
	col.className = "tabcent";
	col.innerHTML = "<select name='isindex"
			+ j
			+ "' id='isindex"
			+ j
			+ "'><option value='false'>否</option><option value='true'>是</option></select>";
	col = row.insertCell(8);
	col.className = "tabcent";
	col.innerHTML = "<select name='isnull"
			+ j
			+ "' id='isnull"
			+ j
			+ "'><option value='true'>是</option><option value='false'>否</option></select>";
	col = row.insertCell(9);
	col.className = "tabcent";
	col.innerHTML = "<select name='isprimkey"
			+ j
			+ "' id='isprimkey"
			+ j
			+ "'><option value='false'>否</option><option value='true'>是</option></select>";
	col = row.insertCell(10);
	col.className = "tabopt tableft";
	col.innerHTML = "<a href=\"javascript:void(0);\" class=\"del\" title=\"删除\"  onclick='deleteRow("
			+ i + ")'></a>";
}

function changeinput(obj) {
	$(obj).attr("id").substr(9);
	if ($(obj).val() == "text") {
		$("#filedlen" + $(obj).attr("id").substr(9)).val('');
		$("#filedlen" + $(obj).attr("id").substr(9)).prop("disabled", "true");
		$("#filedlen" + $(obj).attr("id").substr(9)).attr("check", "no");
		$("#isindex" + $(obj).attr("id").substr(9)).prop("disabled", "true");
		$("#isprimkey" + $(obj).attr("id").substr(9)).prop("disabled", "true");
	} else if ($(obj).val() == "datetime") {
		$("#filedlen" + $(obj).attr("id").substr(9)).val('');
		$("#filedlen" + $(obj).attr("id").substr(9)).prop("disabled", "true");
		$("#filedlen" + $(obj).attr("id").substr(9)).attr("check", "no");
	} else {
		$("#filedlen" + $(obj).attr("id").substr(9)).prop("disabled", "");
		$("#isprimkey" + $(obj).attr("id").substr(9)).prop("disabled", "");
		$("#isindex" + $(obj).attr("id").substr(9)).prop("disabled", "");
		$("#filedlen" + $(obj).attr("id").substr(9)).attr("check", "yes");
	}
}

function deleteRow(rowIndex) {

	var vtab = document.getElementById("fieldtab");
	vtab.deleteRow(rowIndex);
	setTabTdNum();

	// newRowIndex--;//维护全局变量
}

function checkTable(tabname) {
	var vtab = document.getElementById(tabname);
	var rows = vtab.rows;
	for ( var i = 0; i < rows.length; i++) {
		var e = $(rows[i]).find("input[type='text']");
		for ( var j = 0; j < e.length; j++) {
			if ($(e[j]).attr("check") != "no") {
				if ($.trim($(e[j]).val()).length == 0) {
					$(e[j]).focus();
					$(e[j]).css("border", "1px solid red");
					return false;
				}
			}
		}
	}
	return true;
}

function setTabTdNum() {
	var vtab = document.getElementById("fieldtab");
	var rows = vtab.rows;
	var tdlength;
	for ( var i = 1; i < rows.length - 1; i++) {
		tdlength = rows[i].cells.length;
		rows[i].cells[0].innerHTML = i;
		rows[i].cells[tdlength - 1].innerHTML = "<a title=\"删除\" class=\"del\" onclick=\"deleteRow("
				+ i + ")\" href=\"javascript:void(0);\"/>";
	}
}
function saveFields() {
	if ($("#hidFlagCount").val() == 0) {
		if ($("#hidCount").val() == $("#hidFlagCount").val()) {
			alert("请添加数据后保存");
			return false;
		}
	}
	if (!saveContentCheck()) {
		return false;
	}
	$.ajax({
		type : "POST",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : $("#form1").serialize(),
		success : function(data) {
			closeLoading();
			// $("#tipspan").html(data);
			showPopAlert(1, data);
			// var vtab = document.getElementById("fieldtab");
			// var i = vtab.rows.length - 3;
			// $("#hidCount").val(i);
		},
		error : function() {
			closeLoading();
			alert("数据加载失败！");
		}
	});
}

// 判断是否为空和，是否有重复
function saveContentCheck() {
	if (!checkTable("fieldtab")) {
		alert("请补充完整后再提交");
		return false;
	}
	;
	var lstField = new Array();
	var lstFielden = new Array();
	var lstFieldch = new Array();
	var vtab = document.getElementById("fieldtab");
	var rows = vtab.rows;
	for ( var i = 0; i < rows.length; i++) {
		var e = $(rows[i]).find("input[type='text']");
		if (e.length == 0) {
			continue;
		}
		var vField = $.trim($(e[0]).val()).toLowerCase();
		if (vField == "time") {
			alert("time为保留字段，请更换");
			$(e[0]).focus();
			return false;
		}
		if (vField == "id") {
			alert("id为保留字段，请更换");
			$(e[0]).focus();
			return false;
		}
		if (vField.length > 20) {
			alert("字段名称控制在20个字母以内");
			$(e[0]).focus();
			return false;
		}
		if (!isLetter(vField)) {
			alert("字段名称必须为字母");
			$(e[0]).focus();
			return false;
		}
		if (!lstField.in_array(vField)) {
			lstField.push(vField);
		} else {
			alert("存在相同的字段名称");
			$(e[0]).focus();
			return false;
		}
		if ($(e[1]).attr("check") != "no") {
			var vFieldlen = $.trim($(e[1]).val()).toLowerCase();
			if (!isNum(vFieldlen)) {
				alert("字段长度必须为大于0的整数");
				$(e[1]).focus();
				return false;
			}
		}

		var vFielden = $.trim($(e[2]).val()).toLowerCase();
		if (vFielden == "time") {
			alert("time为保留英文名称，请更换");
			$(e[2]).focus();
			return false;
		}
		if (vFielden.length > 50) {
			alert("英文名称控制在50个字母以内");
			$(e[2]).focus();
			return false;
		}
		if (!isLetter(vFielden)) {
			alert("英文名称必须以字母开头，可以包含数字或_");
			$(e[2]).focus();
			return false;
		}

		if (!lstFielden.in_array(vFielden)) {
			lstFielden.push(vFielden);
		} else {
			alert("存在相同的英文名称");
			$(e[2]).focus();
			return false;
		}

		var vFieldch = $.trim($(e[3]).val()).toLowerCase();
		if (vFieldch == "更新时间") {
			alert("更新时间为保留中文名称，请更换");
			$(e[3]).focus();
			return false;
		}
		if (vFieldch.length > 50) {
			alert("中文名称控制在50个字以内");
			$(e[3]).focus();
			return false;
		}
		if (!lstFieldch.in_array(vFieldch)) {
			lstFieldch.push(vFieldch);
		} else {
			alert("存在相同的中文名称");
			$(e[3]).focus();
			return false;
		}
	}
	return true;
}
// userlist.jsp
function getUserList() {
	getFormDataCount(1);
}
function delUser(uid) {
	if ($.trim(uid).length == 0) {
		alert("参数错误");
		return false;
	}
	if (!confirm("确定要删除")) {
		return false;
	}
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "do=deluser&uid=" + uid,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(1);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("请求失败");
		}
	});
}

// userinfo.jsp
function chkUserInfo() {
	$("#prompt").html('');
	if ($.trim($("#txtUser").val()).length == 0) {
		$("#tipUser").html("用户名不能为空");
		$("#tipUser").attr("class", "wronginfo");
		$("#tipUser").show();
		return false;
	} else {
		if (!isLetter($.trim($("#txtUser").val()))) {
			$("#tipUser").html("用户名必须以字母开头，可以包含数字或_");
			$("#tipUser").attr("class", "wronginfo");
			$("#tipUser").show();
			return false;
		}
		if ($.trim($("#txtUser").val()).length > 30) {
			$("#tipUser").html("用户名不得超过30个字符");
			$("#tipUser").attr("class", "wronginfo");
			$("#tipUser").show();
			return false;
		}
	}
	if ($.trim($("#txtPwd").val()).length == 0) {
		$("#tipPwd").html("密码不能为空");
		$("#tipPwd").attr("class", "wronginfo");
		$("#tipPwd").show();
		return false;
	} else {
		if ($.trim($("#txtPwd").val()).length > 32) {
			$("#tipPwd").html("密码长度不得超过32个字符");
			$("#tipPwd").attr("class", "wronginfo");
			$("#tipPwd").show();
		}
	}
	if ($.trim($("#txtPwd").val()) != $.trim($("#txtConfirmPwd").val())) {
		$("#tipConfirmPwd").html("确认密码和密码不相等");
		$("#tipConfirmPwd").attr("class", "wronginfo");
		$("#tipConfirmPwd").show();
		return false;
	}
	return true;
};

// updatepwd.jsp
function chkPassword() {
	if ($.trim($("#txtOldPwd").val()).length == 0) {
		$("#tipOldPwd").html("原始密码不能为空");
		$("#tipOldPwd").attr("class", "wronginfo");
		$("#tipOldPwd").show();
		return false;
	}
	if ($.trim($("#txtNewPwd").val()).length == 0) {
		$("#tipNewPwd").html("新密码不能为空");
		$("#tipNewPwd").attr("class", "wronginfo");
		$("#tipNewPwd").show();
		return false;
	} else {
		if ($.trim($("#txtNewPwd").val()).length > 32) {
			$("#tipNewPwd").html("新密码长度不得超过32个字符");
			$("#tipNewPwd").attr("class", "wronginfo");
			$("#tipNewPwd").show();
		}
	}
	if ($.trim($("#txtNewPwd").val()) != $.trim($("#txtConfirmNewPwd").val())) {
		$("#tipConfirmNewPwd").html("确认新密码和新密码不相等");
		$("#tipConfirmNewPwd").attr("class", "wronginfo");
		$("#tipConfirmNewPwd").show();
		return false;
	}
	return true;
}

// usermodulecontentlist.jsp
function getUserModuleContent() {
	var varcon = "";
	if ($.trim($("#txtStartDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtStartDate").val()))) {
			alert("开始日期格式不正确");
			return false;
		}
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtEndDate").val()))) {
			alert("结束日期格式不正确");
			return false;
		}
	}

	if ($.trim($("#txtStartDate").val()).length > 0
			&& $.trim($("#txtEndDate").val()).length > 0) {
		if ($("#txtStartDate").val() > $("#txtEndDate").val()) {
			var tempTime = $("#txtStartDate").val();
			$("#txtStartDate").val($("#txtEndDate").val());
			$("#txtEndDate").val(tempTime);
		}
	}
	if ($.trim($("#txtUserName").val()).length > 0) {
		varcon = varcon + "username=" + $.trim($("#txtUserName").val()) + "&";
	}
	if ($.trim($("#txtStartDate").val()).length > 0) {
		varcon = varcon + "startdate=" + $.trim($("#txtStartDate").val()) + "&";
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		varcon = varcon + "enddate=" + $.trim($("#txtEndDate").val()) + "&";
	}
	if ($.trim($("#hidpageparam").val()).length > 0) {
		varcon = varcon + $.trim($("#hidpageparam").val()) + "&";
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function delModuleMessage(rid,tablename) {
	if (!confirm("确认删除该记录吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
				url : $("#hidurl").val(),
				type : "GET",
				async : true,
				cache : false,
				data : "do=delinfo&rid=" + rid + "&"+"tablename="+tablename+"&"
						+ $.trim($("#hidpageparam").val()),
				success : function(data) {
					if (data == "1") {
						alert("删除成功");
						getFormDataCount(curpage);
					} else {
						alert("删除失败");
					}
				},
				error : function() {
					alert("加载失败");
				}
			});
}
function delMultiUserCon() {
	var vUserCon = "";
	$("input[name='chkucid']").each(function() {
		if ($(this).prop("checked") == true)
			vUserCon = vUserCon + $(this).val() + ",";
	});
	if (vUserCon == "") {
		alert("请选择要删除的信息！");
		return false;
	}
	delModuleMessage(vUserCon);
}

// noticelist.jsp
function getNoticeList() {
	getFormDataCount(1);
}
function delNotice(nid) {
	if (nid.length == 0) {
		return false;
	}
	if (!confirm("你确定要删除该通知吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=delnotice&nid=" + nid,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败");
		}
	});
}

function delMultiShip() {
	var vNoticeID = "";
	$("input[name='chknoteid']").each(function() {
		if ($(this).prop("checked") == true)
			vNoticeID = vNoticeID + $(this).val() + ",";
	});
	if (vNoticeID == "") {
		alert("请选择要删除的通告！");
		return false;
	}
	delNotice(vNoticeID);
}

// publishnotice.jsp
function subNotice() {
	var vUid = "";
	for ( var i = 0; i < arrUser.length; i++) {
		vUid = vUid + arrUser[i].id + ",";
	}
	$("#hiduname").val(vUid);

	if ($.trim($("#txtTitle").val()).length == 0) {
		$("#tipTitle").html("通知标题不能为空");
		$("#tipTitle").attr("class", "wronginfo");
		$("#tipTitle").show();
		return false;
	} else if ($.trim($("#txtTitle").val()).length > 30) {
		$("#tipTitle").html("通知标题长度不得超过30个字符");
		$("#tipTitle").attr("class", "wronginfo");
		$("#tipTitle").show();
		return false;
	}
	if ($.trim($("#txtNotice").val()).length == 0) {
		$("#tipNotice").html("通知长度不能为空");
		$("#tipNotice").attr("class", "wronginfo");
		$("#tipNotice").show();
		return false;
	} else if ($.trim($("#txtNotice").val()).length > 500) {
		$("#tipNotice").html("通知长度不得超过500个字符");
		$("#tipNotice").attr("class", "wronginfo");
		$("#tipNotice").show();
		return false;
	}

	$.ajax({
		type : "POST",
		url : "PublishNotice.do",
		cache : false,
		async : true,
		data : $("#form1").serialize(),
		success : function(data) {
			closeLoading();
			if (data == "1") {
				window.location.href = "NoticeList.do";
			} else {
				alert("发布失败");
				return false;
			}
		},
		error : function() {
			closeLoading();
			alert("数据加载失败！");
		}
	});
}

// RecommendSearch.jsp //
function CheckDBTag(obj, tagType) {
	$("#hidTypeid").val(tagType);
	if (document.getElementById("spantypename") != undefined) {
		$("#spantypename").html($(obj).html());
		$("#rdftype").hide();
	}
	$(obj).parent().children().each(function() {
		if ($(this).attr("class") != "classclose") {
			$(this).attr("class", "");
		}
	});

	$(obj).attr("class", "current");
	getSearchField(tagType, 1);
}
function CheckEpubDBTag(obj, tagType) {
	$("#hidTypeid").val(tagType);
	if (document.getElementById("spantypename") != undefined) {
		$("#spantypename").html($(obj).html());
		$("#rdftype").hide();
	}
	$(obj).parent().children().each(function() {
		if ($(this).attr("class") != "classclose") {
			$(this).attr("class", "");
		}
	});

	$(obj).attr("class", "current");
	getEpubSearchField(tagType, 1);
}
// RecommendSearch.jsp //RecommendSearchList.jsp

function getSearchMenu() {
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		async : false,
		cache : false,
		data : "do=setsearchmenu",
		success : function(data) {
			$("#rdftype").html(data);
			if ($("#rdftype").css("height") > "40px") {
				$("#rdftype").attr("class", "foldtext");
				$("#rdftype").next().attr("class", "fold");
				$("#rdftype").next().bind("click", function() {
					if ($("#rdftype").attr("class") == "foldtext") {
						$("#rdftype").attr("class", "unfoldtext");
						$("#rdftype").next().attr("class", "unfold");
					} else {
						$("#rdftype").attr("class", "foldtext");
						$("#rdftype").next().attr("class", "fold");
					}
				});
			} else {
				$("#rdftype").attr("class", "unfoldtext");
				$("#rdftype").next().attr("class", "");
			}
			getSearchField($("#hidTypeid").val());
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("服务器错误");
		}
	});
}

// function CheckDBTag(obj, tagType) {
// $("#hidTypeid").val(tagType);
// $(obj).parent().children().each(function() {
// $(this).attr("class", "");
// });
// $(obj).attr("class", "current");
// getSearchField(tagType);
// if(tagType=="journals"){
// $("#fullissue").show();
// }else{
// $("#fullissue").hide();
// }
// }

function getSearchData() {
	var vseaField = $("#SeachField_SelectValue").val();
	window.location.href = "RecommendSearchList.do?vtypeid="
			+ $("#hidTypeid").val() + "&seafiled=" + vseaField + "&searchval="
			+ encodeURI($.trim($("#seachText").val()));
}

function getSearchField(typeid) {
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		async : false,
		cache : false,
		data : "do=getsearchfield&typeid=" + typeid,
		success : function(data) {
			$("#ddlSearchField").html(data);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("服务器错误");
		}
	});
};
//function getEpubSearchField(typeid) {
//	$.ajax({
//		type : "GET",
//		url : "EpubTransSearchHandler.do",
//		async : false,
//		cache : false,
//		data : "do=getsearchfield&typeid=" + typeid,
//		success : function(data) {
//			$("#ddlSearchField").html(data);
//		},
//		error : function(XMLHttpRequest, textStatus, errorThrown) {
//			alert("服务器错误333");
//		}
//	});
//};

// RecommendSearchList.jsp

function getODataSearchMenu() {

	$.ajax({
			type : "GET",
			url : "RecommendSearchHandler.do",
			async : false,
			cache : false,
			data : "do=setsearchmenu&deftype=" + $("#hidTypeid").val(),
			success : function(data) {
				$("#rdftype").html("<em class=\"classclose\" title=\"关闭\" onclick=\"$('#rdftype').css('display','none');\"></em>"+ data);
				getSearchField($("#hidTypeid").val(), 0);
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("服务器错误222");
			}
		});
}
//epubtransserverlist.jsp
function getEpubTransDataSearchMenu(){
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
function getEpubSearchField(typeid, vdefiled) {
	var param = "";
	if (vdefiled == 0) {
		param = "do=getsearchfield&typeid=" + typeid + "&seafield="
				+ $("#hidSeaField").val();
	} else {
		param = "do=getsearchfield&typeid=" + typeid + "&seafield="
				+ $("#SeachField_SelectValue").val();
	}
	$.ajax({
		type : "GET",
		url : "EpubTransSearchHandler.do",
		async : false,
		cache : false,
		data : param,
		success : function(data) {
			$("#ddlSearchField").html(data);
			$("#hidSeaField").val($("#SeachField_SelectValue").val());
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("服务器错误");
		}
	});
};
function getSearchField(typeid, vdefiled) {
	var param = "";
	if (vdefiled == 0) {
		param = "do=getsearchfield&typeid=" + typeid + "&seafield="
				+ $("#hidSeaField").val();
	} else {
		param = "do=getsearchfield&typeid=" + typeid + "&seafield="
				+ $("#SeachField_SelectValue").val();
	}
	$.ajax({
		type : "GET",
		url : "RecommendSearchHandler.do",
		async : false,
		cache : false,
		data : param,
		success : function(data) {
			$("#ddlSearchField").html(data);
			$("#hidSeaField").val($("#SeachField_SelectValue").val());
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("服务器错误");
		}
	});
};
function searchOData(vdefiled) {
	var varcon = "";

	/*
	 * if ($.trim($("#hidType").val()).length > 0) { varcon += "type=" +
	 * $.trim($("#hidType").val()) + "&"; }
	 */
	if ($.trim($("#hidTypeid").val()).length > 0) {
		varcon += "typeid=" + $.trim($("#hidTypeid").val()) + "&";
	}
	if ($.trim($("#txtKeyWord").val()).length > 0) {
		varcon += "keyword=" + encodeURI($.trim($("#txtKeyWord").val())) + "&";
	}
	if (vdefiled == 0) {
		if ($.trim($("#hidSeaField").val()).length > 0) {
			varcon += "filter=" + $.trim($("#hidSeaField").val()) + "&";
		}
	} else {
		if ($.trim($("#SeachField_SelectValue").val()).length > 0) {
			varcon += "filter=" + $.trim($("#SeachField_SelectValue").val())
					+ "&";
		}
	}
	$("#hidparam").val(varcon);
	getFormData(1);
}

function searchEpubTransData(vdefiled) {
	var varcon = "";

	/*
	 * if ($.trim($("#hidType").val()).length > 0) { varcon += "type=" +
	 * $.trim($("#hidType").val()) + "&"; }
	 */
	if ($.trim($("#hidTypeid").val()).length > 0) {
		varcon += "typeid=" + $.trim($("#hidTypeid").val()) + "&";
	}
	if ($.trim($("#txtKeyWord").val()).length > 0) {
		varcon += "keyword=" + encodeURI($.trim($("#txtKeyWord").val())) + "&";
	}
	if (vdefiled == 0) {
		if ($.trim($("#hidSeaField").val()).length > 0) {
			varcon += "filter=" + $.trim($("#hidSeaField").val()) + "&";
		}
	} else {
		if ($.trim($("#SeachField_SelectValue").val()).length > 0) {
			varcon += "filter=" + $.trim($("#SeachField_SelectValue").val())
					+ "&";
		}
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function setSearchOData(vdata, curpage) {
	$("#divdata").html(vdata);
	// $("#spantypename").html($("#hidtypech").val());
	if ($("#hidcount").val() == 0) {
		$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
		if (document.getElementById("ucPageBottom") != undefined) {
			$("#ucPageBottom").css("display", "none");
			$("#ucPageBottom").parent().css("display", "none");
		}
		if (document.getElementById("ucPageTop") != undefined) {
			$("#ucPageTop").css("visibility", "hidden");
		}
	} else {
		if (document.getElementById("ucPageBottom") != undefined) {
			$("#ucPageBottom").css("display", "block");
			$("#ucPageBottom").parent().css("display", "block");
			$("#divbotopt").css("display", "block");
			SetPageTagData("ucPageBottom", curpage, $("#hidcount").val(), $(
					"#hidpagesize").val(), getFormData);
		}
		if (document.getElementById("ucPageTop") != undefined) {
			$("#ucPageTop").css("visibility", "visible");
			// $("#divbotopt").css("display", "block");
			SetSimPageTagData("ucPageTop", curpage, $("#hidcount").val(), $(
					"#hidpagesize").val(), getFormData);
		}
	}
}

function RemdArticle(obj, type, artid, flag) {
	var jsonArticle = {
		"jsondata" : ""
	};
	var arrArticle = new Array();
	var param = "";
	if (flag == 0) {
		var vobj = {
			rid : "",
			rval : "",
			rtab : ""
		};
		vobj.rid = artid;
		vobj.rval = encodeURI($(obj).parent().parent().find(
				"span[name='title']").html().replace(
				new RegExp('(["\"])', 'g'), "\\\""));
		vobj.rtab = encodeURI($(obj).parent().parent().find(
				"input[name='hidtablename']").val().replace(
				new RegExp('(["\"])', 'g'), "\\\""));
		arrArticle.push(vobj);
		jsonArticle.jsondata = arrArticle;
		param = "do=commend&type=" + type + "&fileid="
				+ JSON.stringify(jsonArticle);
	} else {
		param = "do=commend&type=" + type + "&fileid=" + JSON.stringify(obj);
	}

	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : param,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("推荐成功");
				$("#selectAll").attr("checked", false);
				addArticleTitleFlag(obj, type, artid, flag);
				// searchOData();
			} else {
				alert("推荐失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function DisRemdArticle(obj, type, artid, flag) {
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=discommend&type=" + type + "&fileid=" + artid,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("取消成功");
				cancelArticleTitleFlag(obj, type, artid, flag);
			} else {
				alert("取消失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function addArticleTitleFlag(obj, type, artid, flag) {
	if (flag == 0) {
		var vcon = $(obj).parent().parent().find("span[name='title']").parent()
				.html();
		vcon = vcon + "<em class=\"fine\">已推荐</em>";
		$(obj).parent().parent().find("span[name='title']").parent().html(vcon);
		$(obj).parent().html(
				"<a title=\"取消推荐\" class=\"discommend\" onclick=\"DisRemdArticle(this,'"
						+ type + "','" + artid + "'," + flag
						+ ")\" href=\"javascript:void(0);\"/>");
	} else {
		$("input[name='chkfileid']")
				.each(
						function() {
							if ($(this).prop("checked") == true) {
								var rid = $(this).val();
								$(this).parent().parent().find(
										"span[name='title']").parent().find(
										".fine").remove();
								var vcon = $(this).parent().parent().find(
										"span[name='title']").parent().html();
								vcon = vcon + "<em class=\"fine\">已推荐</em>";
								$(this).parent().parent().find(
										"span[name='title']").parent().html(
										vcon);
								$(this)
										.parent()
										.parent()
										.find("td:last")
										.html(
												"<a title=\"取消推荐\" class=\"discommend\" onclick=\"DisRemdArticle(this,'"
														+ type
														+ "','"
														+ rid
														+ "',0)\" href=\"javascript:void(0);\"/>");
								$(this).prop("checked", false);
							}
						});
	}
	;
}
function cancelArticleTitleFlag(obj, type, artid, flag) {
	if (flag == 0) {
		$(obj).parent().parent().find("span[name='title']").parent().find(
				".fine").remove();
		$(obj)
				.parent()
				.html(
						"<a title=\"推荐\" class=\"commend\" href=\"javascript:void(0);\" onclick=\"RemdArticle(this,'"
								+ type
								+ "','"
								+ artid
								+ "',"
								+ flag
								+ ")\"></a>");
	}
	;
}

function RemmondAll() {
	var vFild = "";
	var jsonArticle = {
		"jsondata" : ""
	};
	var arrArticle = new Array();
	$("input[name='chkfileid']").each(
			function() {
				if ($(this).prop("checked") == true) {
					// vFild = vFild + $(this).val() + ",";
					var obj = {
						rid : "",
						rval : ""
					};
					obj.rid = $(this).val();
					obj.rval = encodeURI($(this).parent().parent().find(
							"span[name='title']").html());
					arrArticle.push(obj);
				}
			});
	if (arrArticle.length == 0) {
		alert("请选择要推荐的文章！");
		return false;
	}
	jsonArticle.jsondata = arrArticle;
	RemdArticle(jsonArticle, $("#hidTypeid").val(), vFild, 1);
}

// RecommendList.jsp
function getRecommendList() {
	var varcon = "";

	if ($.trim($("#txtKeyWord").val()).length > 0) {
		varcon += "kw=" + encodeURI($.trim($("#txtKeyWord").val())) + "&";
	}
	if ($.trim($("#ddlTypeName_SelectValue").val()).length > 0) {
		varcon += "tid="
				+ encodeURI($.trim($("#ddlTypeName_SelectValue").val())) + "&";
	}

	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function DisRemdSingleArticle(artids) {

	if (!confirm("确认取消推荐")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=discommend&fileid=" + artids,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("取消成功");
				getFormDataCount(curpage);
			} else {
				alert("取消失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function disRemmondAll() {
	var vFild = "";
	$("input[name='recmdid']").each(function() {
		if ($(this).prop("checked") == true)
			vFild = vFild + $(this).val() + ",";
	});
	if (vFild == "") {
		alert("请选择要取消的文章！");
		return false;
	}
	DisRemdSingleArticle(vFild);
}

// recommendedit.jsp
function chkRecomdForm() {
	if ($.trim($("#txtDescription").val()).length > 200) {
		alert("描述信息过长");
		return false;
	}
	return true;
}

// sourcetypelist.jsp
function getSourceTypeList() {
	getFormData(0);
}

function setSourceTypeList(vdata, curpage) {
	if (vdata.length == 0) {
		$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
	} else {
		$("#divdata").html(vdata);
	}
}

function delSourceType(stid) {
	if (!confirm("确认删除该种类型")) {
		return false;
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=deltype&tid=" + stid,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				window.location.reload();
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

// sourcetypeinfo.jsp
function getSourceFields() {
	var varcon = "";
	if ($.trim($("#hidtid").val()).length > 0) {
		varcon = "tid=" + $.trim($("#hidtid").val()) + "&";
	}
	$("#hidparam").val(varcon);
	getFormData(1);
}
function setSourceFields(vdata, curpage) {
	$("#divdata").html(vdata);
	$("#hidseacount").val($("#seacount").val());
	$("#hiddisplaycount").val($("#displaycount").val());
	$("#hidordercount").val($("#ordercount").val());
}
function saveSourceFieldsCheck() {
	if ($.trim($("#txtFieldNameCH").val()).length == 0) {
		alert("中文名称不能为空");
		return false;
	}
	if ($.trim($("#txtFieldNameEN").val()).length == 0) {
		alert("英文名称不能为空");
		return false;
	}
	if (!checkTable("tabseafield") || !checkTable("tabdisplayfield")) {
		alert("请补充完整后再提交");
		return false;
	}

	return true;
}
function saveSourceFields() {
	if (!confirm("提交前请确保信息的正确性，是否继续？")) {
		return false;
	}
	if (!saveSourceFieldsCheck()) {
		return false;
	}
	$.ajax({
		type : "POST",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : $("#form1").serialize(),
		success : function(data) {
			closeLoading();
			if (data == "1") {
				showPopAlert(1, "保存成功");
			} else if (data == "0") {
				showPopAlert(1, "保存失败");
			}
		},
		error : function() {
			closeLoading();
			alert("服务器错误！");
		}
	});
}
function addtabrow(tabname) {
	var vtab = document.getElementById(tabname);
	var vcolname1 = "";
	var vcolname2 = "";
	var vcount = "";
	if (tabname == "tabseafield") {
		vcount = "hidseacount";
		vcolname1 = "snamech";
		vcolname2 = "snameen";
	} else if (tabname == "tabdisplayfield") {
		vcount = "hiddisplaycount";
		vcolname1 = "dnamech";
		vcolname2 = "dnameen";
	} else if (tabname == "taborderfield") {
		vcount = "hidordercount";
		vcolname1 = "onamech";
		vcolname2 = "onameen";
	}
	var vCount = $("#" + vcount).val();
	var j = ++vCount;
	$("#" + vcount).val(j);
	var i = vtab.rows.length - 1;
	var row = vtab.insertRow(i); // 行对象
	var col; // 列对象
	col = row.insertCell(0);
	col.innerHTML = "<a href=\"javascript:void(0)\" class=\"downbtn\" onclick=\"goup(this,'"
			+ tabname
			+ "');\" title=\"向上\"></a> <a href=\"javascript:void(0)\" onclick=\"godown(this,'"
			+ tabname + "');\" class=\"upbtn\" title=\"向下\"></a>";
	col = row.insertCell(1);
	col.className = "num";
	col.innerHTML = "<span name='tdnum'>" + i + "</span>";
	col = row.insertCell(2);
	col.className = "tabcent";
	col.innerHTML = "<input name=\"" + vcolname1 + i + "\" id=\"" + vcolname1
			+ i + "\" type=\"text\" class=\"addvalue\" /> ";
	col = row.insertCell(3);
	col.className = "tabcent";
	col.innerHTML = "<input name=\"" + vcolname2 + i + "\" id=\"" + vcolname2
			+ i + "\" type=\"text\" class=\"addvalue\" /> ";
	col = row.insertCell(4);
	col.className = "tabopt tableft";
	col.innerHTML = "<a href=\"javascript:void(0)\" onclick=\"deltabrow(this,'"
			+ tabname + "');\" class=\"del\" title=\"删除\"></a>";

}

function deltabrow(obj, tabname) {
	var vtab = document.getElementById(tabname);
	var tdnum = parseInt($(obj).parent().parent().find("span[name='tdnum']")
			.html(), 10);
	var vcount = "";
	if (tabname == "tabseafield") {
		vcount = "hidseacount";
		vcolname1 = "snamech";
		vcolname2 = "snameen";
	} else if (tabname == "tabdisplayfield") {
		vcount = "hiddisplaycount";
		vcolname1 = "dnamech";
		vcolname2 = "dnameen";
	} else if (tabname == "taborderfield") {
		vcount = "hidordercount";
		vcolname1 = "onamech";
		vcolname2 = "onameen";
	}
	var tdcount = parseInt($("#" + vcount).val(), 10);
	var tdnextnum;
	while (tdnum < tdcount) {
		tdnextnum = tdnum + 1;
		var oldval1 = $("#" + vcolname1 + tdnextnum).val();
		var oldval2 = $("#" + vcolname2 + tdnextnum).val();
		$("#" + vcolname1 + tdnextnum).val($("#" + vcolname1 + tdnum).val());
		$("#" + vcolname2 + tdnextnum).val($("#" + vcolname2 + tdnum).val());
		$("#" + vcolname1 + tdnum).val(oldval1);
		$("#" + vcolname2 + tdnum).val(oldval2);
		tdnum = tdnextnum;
	}
	vtab.deleteRow(tdcount);
	tdcount = tdcount - 1;
	$("#" + vcount).val(tdcount);
}
function godown(obj, tabname) {
	var tdnum = parseInt($(obj).parent().parent().find("span[name='tdnum']")
			.html(), 10);
	var vcount = "";
	if (tabname == "tabseafield") {
		vcount = "hidseacount";
		vcolname1 = "snamech";
		vcolname2 = "snameen";
	} else if (tabname == "tabdisplayfield") {
		vcount = "hiddisplaycount";
		vcolname1 = "dnamech";
		vcolname2 = "dnameen";
	} else if (tabname == "taborderfield") {
		vcount = "hidordercount";
		vcolname1 = "onamech";
		vcolname2 = "onameen";
	}
	var tdcount = parseInt($("#" + vcount).val(), 10);
	if (tdnum == tdcount) {
		alert("已到最后一行");
		return false;
	} else {
		var tdnextnum = tdnum + 1;
		var oldval1 = $("#" + vcolname1 + tdnextnum).val();
		var oldval2 = $("#" + vcolname2 + tdnextnum).val();
		$("#" + vcolname1 + tdnextnum).val($("#" + vcolname1 + tdnum).val());
		$("#" + vcolname2 + tdnextnum).val($("#" + vcolname2 + tdnum).val());
		$("#" + vcolname1 + tdnum).val(oldval1);
		$("#" + vcolname2 + tdnum).val(oldval2);
	}
}
function goup(obj, tabname) {
	var tdnum = parseInt($(obj).parent().parent().find("span[name='tdnum']")
			.html(), 10);
	if (tabname == "tabseafield") {
		vcolname1 = "snamech";
		vcolname2 = "snameen";
	} else if (tabname == "tabdisplayfield") {
		vcolname1 = "dnamech";
		vcolname2 = "dnameen";
	} else if (tabname == "taborderfield") {
		vcolname1 = "onamech";
		vcolname2 = "onameen";
	}
	if (tdnum == 1) {
		alert("已到最前一行");
		return false;
	} else {
		var tdnprenum = tdnum - 1;
		var oldval1 = $("#" + vcolname1 + tdnprenum).val();
		var oldval2 = $("#" + vcolname2 + tdnprenum).val();
		$("#" + vcolname1 + tdnprenum).val($("#" + vcolname1 + tdnum).val());
		$("#" + vcolname2 + tdnprenum).val($("#" + vcolname2 + tdnum).val());
		$("#" + vcolname1 + tdnum).val(oldval1);
		$("#" + vcolname2 + tdnum).val(oldval2);
	}
}

// recommendtypelist.jsp

function getRemSourceTypeList() {
	var varcon = "";
	if ($.trim($("#selappid").val()).length > 0) {
		varcon += "appid=" + $.trim($("#selappid").val()) + "&";
	}
	if ($.trim($("#hidjpy").val()).length > 0) {
		varcon += "journalname=" + $.trim($("#hidjpy").val()) + "&";
	}
	$("#hidparam").val(varcon);
	getFormData(1);
}

function setRemSourceTypeList(vdata, curpage) {
	if (vdata.length == 0) {
		$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
	} else {
		$("#divdata").html(vdata);
		$("#divaddsure").show();
	}
}

function addRecomdSourceType() {
	var stid = "";
	$("input[name='stypechk']").each(function() {
		if ($(this).prop("checked") == true) {
			stid += $(this).val() + ",";
		}
	});
	var pid =$("#selappid").val();
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=addremmendtype&tid=" + stid +"&pid="+pid,
		success : function(data) {
			if (data == "1") {
				alert("保存成功");
				window.location.reload();
			} else {
				alert("保存失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

// recommendtypeorder.jsp
function getRemSourceTypeOrder() {
	var varcon = "";
	if ($.trim($("#selappid").val()).length > 0) {
		varcon += "appid=" + $.trim($("#selappid").val()) + "&";
	}
	if ($.trim($("#hidjpy").val()).length > 0) {
		varcon += "journalname=" + $.trim($("#hidjpy").val()) + "&";
	}
	$("#hidparam").val(varcon);
	getFormData(0);
}

function setRemSourceTypeOrder(vdata, curpage) {
	if (vdata.length == 0) {
		$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
	} else {
		$("#divdata").html(vdata);
		$("#divaddsure").show();
	}
}
function saveRecomdSourceType() {
	$.ajax({
		type : "POST",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : $("#form1").serialize(),
		success : function(data) {
			closeLoading();
			if (data == "1") {
				showPopAlert(1, "保存成功");
			} else if (data == "0") {
				showPopAlert(1, "保存失败");
			}
		},
		error : function() {
			closeLoading();
			alert("服务器错误！");
		}
	});
}
function ordergoup(obj, tabname) {
	var tdnum = parseInt($(obj).parent().parent().find("span[name='tdnum']")
			.html(), 10);
	if (tdnum == 1) {
		return false;
	}
	var curtr = $(obj).parent().parent();
	var pretr = $(obj).parent().parent().prev();
	for ( var i = 2; i < curtr.find("td").length; i++) {
		var temp = curtr.find("td:eq(" + i + ")").html();
		curtr.find("td:eq(" + i + ")").html(
				pretr.find("td:eq(" + i + ")").html());
		pretr.find("td:eq(" + i + ")").html(temp);
	}
	var prenum = tdnum - 1;
	var vhidid = $("#hidid" + tdnum).val();
	$("#hidid" + tdnum).val($("#hidid" + prenum).val());
	$("#hidid" + prenum).val(vhidid);
}

function ordergodown(obj) {
	var tdnum = parseInt($(obj).parent().parent().find("span[name='tdnum']")
			.html(), 10);
	var vcount = $("#hidcount").val();
	if (tdnum == vcount) {
		return false;
	}
	var curtr = $(obj).parent().parent();
	var nexttr = $(obj).parent().parent().next();
	for ( var i = 2; i < curtr.find("td").length; i++) {
		var temp = curtr.find("td:eq(" + i + ")").html();
		curtr.find("td:eq(" + i + ")").html(
				nexttr.find("td:eq(" + i + ")").html());
		nexttr.find("td:eq(" + i + ")").html(temp);
	}
	var nextnum = tdnum + 1;
	var vhidid = $("#hidid" + tdnum).val();
	$("#hidid" + tdnum).val($("#hidid" + nextnum).val());
	$("#hidid" + nextnum).val(vhidid);
}
function delorderrow(obj, tabname) {
	var vtab = document.getElementById(tabname);
	var tdnum = parseInt($(obj).parent().parent().find("span[name='tdnum']")
			.html(), 10);
	var tdcount = parseInt($("#hidcount").val(), 10);
	var curtr = $(obj).parent().parent();
	var nexttr = $(obj).parent().parent().next();
	while (tdnum < tdcount) {
		for ( var i = 2; i < curtr.find("td").length; i++) {
			var temp = curtr.find("td:eq(" + i + ")").html();
			curtr.find("td:eq(" + i + ")").html(
					nexttr.find("td:eq(" + i + ")").html());
			nexttr.find("td:eq(" + i + ")").html(temp);
		}
		var nextnum = tdnum + 1;
		var vhidid = $("#hidid" + tdnum).val();
		$("#hidid" + tdnum).val($("#hidid" + nextnum).val());
		$("#hidid" + nextnum).val(vhidid);

		curtr = nexttr;
		nexttr = curtr.next();
		tdnum = nextnum;
	}
	vtab.deleteRow(tdcount);
	tdcount = tdcount - 1;
	$("#hidcount").val(tdcount);
}

// sysconfiginfo.jsp
function chkSysInfoForm() {
	if ($.trim($("#txtProName").val()).length == 0) {
		alert("请填写属性名称");
		return false;
	} else {
		if ($.trim($("#txtProName").val()).length > 128) {
			alert("属性名称过长");
			return false;
		}
	}
	if ($.trim($("#txtProVal").val()).length == 0) {
		alert("请填写属性值");
		return false;
	}
	return true;
}

// journalinfo.jsp;journalmonthdetail.jsp
function getJournalArticles() {
	var varcon = "";
	if ($.trim($("#hidjpy").val()).length > 0) {
		varcon += "journalname=" + $.trim($("#hidjpy").val()) + "&";
	}
	if ($.trim($("#hidyearinfo").val()).length > 0) {
		varcon += "yearinfo=" + $.trim($("#hidyearinfo").val()) + "&";
	}
	if ($.trim($("#hidissueinfo").val()).length > 0) {
		varcon += "issueinfo=" + $.trim($("#hidissueinfo").val()) + "&";
	}
	$("#hidparam").val(varcon);
	getFormData(1);
}

function getInssueandData(journalpy, yearinfo, typeid) {
	getIssueInfo(journalpy, yearinfo, typeid);
	$("#hidyearinfo").val($("#curyear").html());

	var issurinfo = $("#divmonth>a:eq(0)").html();
	$("#divmonth>a:eq(0)").prop("class", "current");
	$("#hidissueinfo").val(issurinfo.substring(1, issurinfo.length - 2));
	getJournalArticles();
	setCurInssueEM();
	$("#hidcurpage").val(0);
	$(".cataloglist").scrollTop = 0;
}

function getIssueInfo(journalpy, yearinfo, typeid, curissue) {
	$("#divyear").find("a").each(function() {
		if ($(this).html() == yearinfo + "年") {
			$(this).attr("class", "current");
		} else {
			$(this).attr("class", "");
		}
	});
	var vParam = "";
	if (curissue == undefined) {
		vParam = "do=getissueinfo&jpy=" + journalpy + "&year=" + yearinfo
				+ "&vtypeid=" + typeid + "&journalname="
				+ encodeURI($("#spjn").html());
	} else {
		vParam = "do=getissueinfo&jpy=" + journalpy + "&year=" + yearinfo
				+ "&vtypeid=" + typeid + "&curissue=" + curissue
				+ "&journalname=" + encodeURI($("#spjn").html());
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : false,
		cache : false,
		data : vParam,
		success : function(data) {
			$("#curyear").html(yearinfo);
			$("#divmonth").html(data);
			$("#divyear").hide();
			$("#divmonth").show();
		},
		error : function() {
			alert("服务器错误");
		}
	});
}
function RecomandWholeJournal() {
	var journalpy = $("#hidjpy").val();
	var typeid = $("#hidtype").val();
	var journalname = $("#spjn").html();
	$.ajax({
		type : "get",
		url : "JournalInfoHandler.do",
		async : true,
		cache : false,
		data : "do=remdwjournal&jpy=" + journalpy + "&vtypeid=" + typeid
				+ "&journalname=" + encodeURI(journalname),
		success : function(data) {
			if (data == "1") {
				alert("推荐成功");
				$("#rmdbtn").hide();
				$("#disrmdbtn").show();
			} else {
				alert("推荐失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function disRecomandWholeJournal() {
	var journalpy = $("#hidjpy").val();
	var typeid = $("#hidtype").val();
	var journalname = $("#spjn").html();
	$.ajax({
		type : "get",
		url : "JournalInfoHandler.do",
		async : true,
		cache : false,
		data : "do=disremdwjournal&jpy=" + journalpy + "&vtypeid=" + typeid
				+ "&journalname=" + encodeURI(journalname),
		success : function(data) {
			if (data == "1") {
				alert("取消成功");
				$("#rmdbtn").show();
				$("#disrmdbtn").hide();
			} else {
				alert("取消失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}
function setInssueInfo(obj, curissue) {
	$(obj).siblings().attr("class", "");
	$(obj).attr("class", "current");
	$("#hidissueinfo").val(curissue);
	getJournalArticles();
	setCurInssueEM();
	$("#hidcurpage").val(0);
	$(".cataloglist").scrollTop = 0;
}
function setCurInssueEM() {
	var vemcon = $("#hidyearinfo").val() + "年" + $("#hidissueinfo").val() + "期";
	$("#emcurinssue").html(vemcon);
}

function setInssurArticle(vdata, curpage) {
	// alert(vdata);
	if (curpage == 1) {
		$("#divdata").html(vdata);
	} else {
		$("#tableBody").html($("#tableBody").html() + vdata);
	}
	if ($("#hidcount").val() == 0) {
		$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
	} else {
		var vcurpage = parseInt($("#hidcurpage").val(), 10);
		$("#hidcurpage").val(vcurpage + 1);
	}

}

function RemdJournalArticle(obj, artid, flag) {
	var jsonArticle = {
		"jsondata" : ""
	};
	var arrArticle = new Array();
	var param = "";
	if (flag == 0) {
		var vobj = {
			rid : "",
			rval : "",
			rtab : ""
		};
		vobj.rid = artid;
		vobj.rval = encodeURI($(obj).parent().parent().find(
				"span[name='title']").html().replace(
				new RegExp('(["\"])', 'g'), "\\\""));
		vobj.rtab = encodeURI($(obj).parent().parent().find(
				"input[name='hidtablename']").val().replace(
				new RegExp('(["\"])', 'g'), "\\\""));
		arrArticle.push(vobj);
		jsonArticle.jsondata = arrArticle;
		param = "do=commend&fileid=" + JSON.stringify(jsonArticle);
	} else {
		param = "do=commend&fileid=" + JSON.stringify(obj);
	}

	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : param,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("推荐成功");
				$("#selectAll").prop("checked", false);
				addTitleFlag(obj, artid, flag);
			} else {
				alert("推荐失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function DisRemdJournalArticle(obj, artid, flag) {
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=discommend&fileid=" + artid,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("取消成功");
				cancelTitleFlag(obj, artid, flag);
			} else {
				alert("取消失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}
function RemmondAllJournalArticle() {
	var vFild = "";
	var jsonArticle = {
		"jsondata" : ""
	};
	var arrArticle = new Array();
	$("input[name='chkfileid']").each(
			function() {
				if ($(this).prop("checked") == true) {
					// vFild = vFild + $(this).val() + ",";
					var obj = {
						rid : "",
						rval : "",
						rtab : ""
					};
					obj.rid = $(this).val();
					obj.rval = encodeURI($(this).parent().parent().find(
							"span[name='title']").html());
					obj.rtab = encodeURI($(this).parent().parent().find(
					"input[name='hidtablename']").val().replace(
					new RegExp('(["\"])', 'g'), "\\\""));
					arrArticle.push(obj);
				}
			});
	if (arrArticle.length == 0) {
		alert("请选择要推荐的文章！");
		return false;
	}
	jsonArticle.jsondata = arrArticle;
	RemdJournalArticle(jsonArticle, vFild, 1);
}
function addTitleFlag(obj, artid, flag) {
	if (flag == 0) {
		var vcon = $(obj).parent().parent().find("span[name='title']").parent()
				.html();
		vcon = vcon + "<em class=\"fine\">已推荐</em>";
		$(obj).parent().parent().find("span[name='title']").parent().html(vcon);
		$(obj).parent().html(
				"<a title=\"取消推荐\" class=\"discommend\" onclick=\"DisRemdJournalArticle(this,'"
						+ artid + "'," + flag
						+ ")\" href=\"javascript:void(0);\"/>");
	} else {
		$("input[name='chkfileid']")
				.each(
						function() {
							if ($(this).prop("checked") == true) {
								var rid = $(this).val();
								$(this).parent().parent().find(
										"span[name='title']").parent().find(
										".fine").remove();
								var vcon = $(this).parent().parent().find(
										"span[name='title']").parent().html();
								vcon = vcon + "<em class=\"fine\">已推荐</em>";
								$(this).parent().parent().find(
										"span[name='title']").parent().html(
										vcon);
								$(this)
										.parent()
										.parent()
										.find("td:last")
										.html(
												"<a title=\"取消推荐\" class=\"discommend\" onclick=\"DisRemdJournalArticle(this,'"
														+ rid
														+ "',0)\" href=\"javascript:void(0);\"/>");
								$(this).prop("checked", false);
							}
						});
	}
	;
}
function cancelTitleFlag(obj, artid, flag) {
	if (flag == 0) {
		$(obj).parent().parent().find("span[name='title']").parent().find(
				".fine").remove();
		$(obj)
				.parent()
				.html(
						"<a title=\"推荐\" class=\"commend\" href=\"javascript:void(0);\" onclick=\"RemdJournalArticle(this,'"
								+ artid + "'," + flag + ")\"></a>");
	}
	;
}
// uploadfilelist.jsp
function getUploadFiles() {
	var varcon = "";
	if ($.trim($("#txtFileName").val()).length > 0) {
		varcon += "fn=" + encodeURI($.trim($("#txtFileName").val())) + "&";
	}
	if ($.trim($("#txtUserName").val()).length > 0) {
		varcon += "un=" + encodeURI($.trim($("#txtUserName").val())) + "&";
	}
	if($.trim($("#selIsDelete").val()).length>0){
		varcon += "isdel=" + encodeURI($.trim($("#selIsDelete").val())) + "&";
	}
	if($.trim($("#txtFileType").val()).length>0){
		varcon += "ft=" + encodeURI($.trim($("#txtFileType").val())) + "&";
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}
function delUploadFile(fileid,username) {
	if (!confirm("确认要删除？")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=delfile&fid=" + fileid + "&uname="+username,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}
function delUpFileAll() {
	var vFileID = "";
	$("input[name='chkfileid']").each(function() {
		if ($(this).prop("checked") == true)
			vFileID = vFileID + $(this).val() + ",";
	});
	if (vFileID == "") {
		alert("请选择要删除的文件！");
		return false;
	}
	delUploadFile(vFileID);
}
// downloadfilelist.jsp
function getDownloadFiles() {
	var varcon = "";
	if($.trim($("#selappid").val()).length > 0) {
		varcon += "ad=" + encodeURI($.trim($("#selappid").val())) + "&";
	}
	if ($.trim($("#txtFileName").val()).length > 0) {
		varcon += "fn=" + encodeURI($.trim($("#txtFileName").val())) + "&";
	}
	if ($.trim($("#txtUserName").val()).length > 0) {
		varcon += "un=" + encodeURI($.trim($("#txtUserName").val())) + "&";
	}
	if ($.trim($("#txtOrgName").val()).length > 0) {
		varcon += "on=" + encodeURI($.trim($("#txtOrgName").val())) + "&";
	}
	if($.trim($("#selIsOrg").val()).length>0){
		varcon += "isorg=" + encodeURI($.trim($("#selIsOrg").val())) + "&";
	}
	if ($.trim($("#txtStartDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtStartDate").val()))) {
			alert("开始日期格式不正确");
			return false;
		}
		varcon += "startdate=" + encodeURI($.trim($("#txtStartDate").val())) + "&";
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtEndDate").val()))) {
			alert("结束日期格式不正确");
			return false;
		}
		varcon += "enddate=" + encodeURI($.trim($("#txtEndDate").val())) + "&";
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function getDownloadTraceFiles(){
	var varcon = "";
	if ($.trim($("#txtUserName").val()).length > 0) {
		varcon += "username=" + encodeURI($.trim($("#txtUserName").val())) + "&";
	}
	if ($.trim($("#txtFileId").val()).length > 0) {
		varcon += "fileId=" + encodeURI($.trim($("#txtFileId").val())) + "&";
	}
	if ($.trim($("#operation").val()).length > 0) {
		varcon += "operation=" + encodeURI($.trim($("#operation").val())) + "&";
	}
	if ($.trim($("#opstatus").val()).length > 0) {
		varcon += "opstatus=" + encodeURI($.trim($("#opstatus").val())) + "&";
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function delDownloadFile(fileid,username) {
	if (!confirm("确认要删除？")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=delfile&fid=" + fileid+"&uname="+username,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}
function delDownFileAll() {
	var vFileID = "";
	$("input[name='chkfileid']").each(function() {
		if ($(this).prop("checked") == true)
			vFileID = vFileID + $(this).val() + ",";
	});
	if (vFileID == "") {
		alert("请选择要删除的文件！");
		return false;
	}
	delDownloadFile(vFileID);
}

// error.jsp
function searchErrorList() {

	if ($.trim($("#txtStartTime").val()).length > 0
			&& $.trim($("#txtStartDate").val()).length == 0) {
		alert("请选择开始日期");
		return false;
	}
	if ($.trim($("#txtEndTime").val()).length > 0
			&& $.trim($("#txtEndDate").val()).length == 0) {
		alert("请选择结束日期");
		return false;
	}

	var pat1 = /^(\d{1}|\d{1}:|\d{2}|\d{2}:)$/;
	var StartTime = $.trim($("#txtStartTime").val());
	var EndTime = $.trim($("#txtEndTime").val());

	if (pat1.test(StartTime)) {
		$("#txtStartTime").val("0" + StartTime + ":00:00");
	}
	if (pat1.test(EndTime)) {
		$("#txtEndTime").val("0" + EndTime + ":00:00");
	}

	var pat2 = /^((\d{1}|\d{2}):((\d{1}|\d{1}:|\d{2}|\d{2}:)))$/;

	if (pat2.test(StartTime)) {
		var arrStartTime = StartTime.split(':');

		if (arrStartTime[0].length == "1") {
			$("#txtStartTime").val("0" + arrStartTime[0] + ":");
		} else {
			$("#txtStartTime").val(arrStartTime[0] + ":");
		}

		if (arrStartTime[1].length == "1") {
			$("#txtStartTime").val(
					$("#txtStartTime").val() + "0" + arrStartTime[1] + ":00");
		} else {
			$("#txtStartTime").val(
					$("#txtStartTime").val() + arrStartTime[1] + ":00");
		}
	}

	if (pat2.test(EndTime)) {
		var arrEndTime = EndTime.split(':');

		if (arrEndTime[0].length == "1") {
			$("#txtEndTime").val("0" + arrEndTime[0] + ":");
		} else {
			$("#txtEndTime").val(arrEndTime[0] + ":");
		}

		if (arrEndTime[1].length == "1") {
			$("#txtEndTime").val(
					$("#txtEndTime").val() + "0" + arrEndTime[1] + ":00");
		} else {
			$("#txtEndTime")
					.val($("#txtEndTime").val() + arrEndTime[1] + ":00");
		}
	}
	pat = /^(([01]?\d)|([2][0-3])):[0-5]?\d:[0-5]?\d$/;
	if ($("#txtStartTime").val() != "" && !pat.test($("#txtStartTime").val())) {
		alert("输入的开始时间有误");
		return false;
	}
	if ($("#txtEndTime").val() != "" && !pat.test($("#txtEndTime").val())) {
		alert("输入的结束时间有误");
		return false;
	}

	if ($.trim($("#txtStartDate").val()).length > 0
			&& $.trim($("#txtEndDate").val()).length > 0) {
		if ($("#txtStartDate").val() > $("#txtEndDate").val()) {
			var tempTime = $("#txtStartDate").val();
			$("#txtStartDate").val($("#txtEndDate").val());
			$("#txtEndDate").val(tempTime);
		}
	}

	if ($("#txtStartDate").val() == $("#txtEndDate").val()) {
		if ($.trim($("#txtStartTime").val()).length > 0
				&& $("#txtEndTime").val() > 0) {
			if ($("#txtStartTime").val() > $("#txtEndTime").val()) {
				var tempTime = $("#txtStartTime").val();
				$("#txtStartTime").val($("#txtEndTime").val());
				$("#txtEndTime").val(tempTime);
			}
		}
	}
	var varcon = "startdate=" + encodeURI($.trim($("#txtStartDate").val()))
			+ "&enddate=" + encodeURI($.trim($("#txtEndDate").val()))
			+ "&starttime=" + encodeURI($.trim($("#txtStartTime").val()))
			+ "&endtime=" + encodeURI($.trim($("#txtEndTime").val()));
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function deleteError(id) {
	if (!confirm("你确定要删除异常信息吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	if (id == undefined) {
		id = "";
		$("input[name='chkErrorList']").each(function() {
			if ($(this).prop("checked") == true) {
				id = id + $(this).val() + ",";
			}
		});
	}
	if (id == undefined || id.length == 0) {
		alert("请选择要删除的记录！");
		return false;
	}
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "do=delete&ids=" + id,
		success : function(msg) {
			if (msg == "true") {
				getFormDataCount(curpage);
				alert("删除成功！");
			} else {
				alert("删除失败！");
			}
		},
		error : function() {
			alert("删除失败！");
		}
	});
}

// sysconfiglist.jsp
function delSysInfo(SysID) {
	if (!confirm("你确定要删除该记录吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=delsysinfo&sid=" + SysID,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}
function delMultiSysInfo() {
	var vSysID = "";
	$("input[name='chksysid']").each(function() {
		if ($(this).prop("checked") == true)
			vSysID = vSysID + $(this).val() + ",";
	});
	if (vSysID == "") {
		alert("请选择要删除的记录！");
		return false;
	}
	delSysInfo(vSysID);
}

//versioninfo.jsp
function chkVersionForm(){
	if($.trim($("#txtclient").val()).length==0){
		alert("请填写系统类别");
		return false;
	}
	if($.trim($("#txtversion").val()).length==0){
		alert("请填写版本号");
		return false;
	}
	if($.trim($("#txtclient").val()).length>50){
		alert("系统类别不要超过50个字符");
		return false;
	}
	
	if($.trim($("#txtversion").val()).length>10){
		alert("版本号不要超过10个字符");
		return false;
	}
	
	if($.trim($("#txtvername").val()).length>30){
		alert("版本名称不要超过30个字符");
		return false;
	}
	if($.trim($("#txtApkUrl").val()).length>0){
		if($("#txtApkUrl").val().substring(0,7)!="http://"){
			alert("下载地址必须以http://开头");
			return false;
		}
	}
	if($.trim($("#txtApkUrl").val()).length>300){
		alert("下载地址不要超过300个字符");
		return false;
	}
	return true;
}

//versionlist.jsp
function getVersionList(){
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "do=getlist",
		success : function(data) {
			if(data.length==0){
				$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
			}else{
				$("#divdata").html(data);
			}
		},
		error : function() {
			alert("加载失败！");
		}
	});
}

function delVersionInfo(vid){
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "do=del&vid="+vid,
		success : function(data) {
			if(data == "1"){
				alert("删除成功");
				window.location.reload();
			}else{
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败！");
		}
	});
}

function getTerminalNumList(){
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "do=getlist",
		success : function(data) {
			if(data.length==0){
				$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
			}else{
				$("#divdata").html(data);
			}
		},
		error : function() {
			alert("加载失败！");
		}
	});
}

function delTerminalNum(vid){
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "do=del&tid="+vid,
		success : function(data) {
			if(data == "1"){
				alert("删除成功");
				window.location.reload();
			}else{
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败！");
		}
	});
}
function chkTerminalForm(){
	if($.trim($("#txtclient").val()).length==0){
		alert("请填写系统类别");
		return false;
	}
	if($.trim($("#txtTenimalNum").val()).length==0){
		alert("请填写系统终端数");
		return false;
	}
	if($.trim($("#txtversion").val()).length>50){
		alert("系统类别不要超过50个字符");
		return false;
	}
	
	if(!/^[0-9]+$/.test($.trim($("#txtTenimalNum").val()))){
		alert("系统终端数必须为非负数");
		return false;
	}
	return true;
}
//epubserverlist.jsp
function getEpubServerList(){
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : false,
		data : "do=getlist",
		success : function(data) {
			if(data.length==0){
				$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
			}else{
				$("#divdata").html(data);
				//getEpubServerStatus();
			}
		},
		error : function() {
			alert("加载失败！");
		}
	});
}
function delEpubServer(vid){
	if(!confirm("确定删除该服务器？")){
		return true;
	}
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "do=del&sid="+vid,
		success : function(data) {
			if(data == "1"){
				alert("删除成功");
				window.location.reload();
			}else{
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败！");
		}
	});
}


function getEpubServerStatus(){
	if(document.getElementById("hidtrs")== undefined){
		return;
	}
 	var vtrs=$.trim($("#hidtrs").val());
 	if(vtrs.length==0){
 		return;
 	}
 	var arrtr=vtrs.split(";");
 	for(var i=0;i<arrtr.length;i++){
 		var host = $("#"+arrtr[i]).children("td").eq(1).children("a").html();
 		var port = $("#"+arrtr[i]).children("td").eq(4).html();
 		$.ajax({
 			type : "GET",
 			url : $("#hidurl").val(),
 			cache : false,
 			async : false,
 			data : "do=getstatus&hostid="+host+"&portid="+port,
 			success : function(data) {
 					var vdata=data.split(";");
 					$("#"+arrtr[i]).children("td").eq(5).html(vdata[0]);
 					$("#"+arrtr[i]).children("td").eq(6).html(vdata[1]);
 					$("#"+arrtr[i]).children("td").eq(7).html(vdata[2]);
 					if((parseInt(vdata[0])-parseInt(vdata[1]))<2){
 						$("#"+arrtr[i]).children("td").eq(1).addClass("red");
 					}
 			},
 			error : function() {
 				alert("加载失败！");
 			}
 		});
 	}
}
//epubserverinfo.jsp
function chkEpubServerForm(){
	if($.trim($("#txtServerAdd").val()).length==0){
		alert("请填写服务器地址");
		return false;
	}else{
		if(!isIpAddress($("#txtServerAdd").val())){
			alert("服务器地址格式错误");
			return false;
		}
	}
	if($.trim($("#txtStatPort").val()).length==0){
		alert("请填写状态端口");
		return false;
	}
	if($.trim($("#txtCmdPort").val()).length==0){
		alert("请填写命令端口");
		return false;
	}	
	if($.trim($("#txtStatPort").val()).length>0){
		if(!/^[0-9]+$/.test($.trim($("#txtStatPort").val()))){
			alert("状态端口必须为非负数");
			return false;
		}
	}
	if($.trim($("#txtCmdPort").val()).length>0){
		if(!/^[0-9]+$/.test($.trim($("#txtCmdPort").val()))){
			alert("命令端口必须为非负数");
			return false;
		}
	}
	return true;
}

function isIpAddress(addr)
{
	var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])){3}$/;
	if (addr.match(reg)) {
		return true;
	} else {
		return false;
	}
}
//commentlist.jsp
function getCommnetList(){  
	var varcon = "";

	if ($.trim($("#UserName").val()).length > 0) {
		varcon += "un=" + encodeURI($.trim($("#UserName").val())) + "&";
	}
	if ($.trim($("#txtStartDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtStartDate").val()))) {
			alert("开始日期格式不正确");
			return false;
		}
		varcon += "startdate=" + encodeURI($.trim($("#txtStartDate").val())) + "&";
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtEndDate").val()))) {
			alert("结束日期格式不正确");
			return false;
		}
		varcon += "enddate=" + encodeURI($.trim($("#txtEndDate").val())) + "&";
	}
	if ($.trim($("#DocuName").val()).length > 0) {
		varcon += "dn=" + encodeURI($.trim($("#DocuName").val())) + "&";
	}
	if ($.trim($("#txtContent").val()).length > 0) {
		varcon += "tc=" + encodeURI($.trim($("#txtContent").val())) + "&";
	}
	if($.trim($("#ddlIsPassed").val()).length>0){
		varcon += "ip=" + encodeURI($.trim($("#ddlIsPassed").val())) + "&";
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function delComment(vid,vtab){
	if (vid.length == 0) {
		return false;
	}
	if (!confirm("你确定要删除该评论吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=delcomment&vid=" + vid +"&vtab="+vtab,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败");
		}
	});
}

function delMultiComment(){
	var vid = "";
	var vtab = "";
	$("input[name='chkcommentid']").each(function() {
		if ($(this).prop("checked") == true)
			vid = vid + $(this).val().split(",")[0] + ",";
			vtab = vtab + $(this).val().split(",")[1] + ",";
	});
	if (vid == "") {
		alert("请选择要删除的评论记录！");
		return false;
	}
	delComment(vid,vtab);
}

function serchODataCommentArticle(vdefiled) {
	var varcon = "";

	if ($.trim($("#hidTypeid").val()).length > 0) {
		varcon += "typeid=" + $.trim($("#hidTypeid").val()) + "&";
	}
	if ($.trim($("#txtKeyWord").val()).length > 0) {
		varcon += "keyword=" + encodeURI($.trim($("#txtKeyWord").val())) + "&";
	}
	if (vdefiled == 0) {
		if ($.trim($("#hidSeaField").val()).length > 0) {
			varcon += "filter=" + $.trim($("#hidSeaField").val()) + "&";
		}
	} else {
		if ($.trim($("#SeachField_SelectValue").val()).length > 0) {
			varcon += "filter=" + $.trim($("#SeachField_SelectValue").val())
					+ "&";
		}
	}
	$("#hidparam").val(varcon);
	getFormData(1);
}

function CloseArticleComment(obj, type, artid, flag) {
	var jsonArticle = {
			"jsondata" : ""
		};
		var arrArticle = new Array();
		var param = "";
		if (flag == 0) {
			var vobj = {
				rid : "",
				rval : "",
				rtab : ""
			};
			vobj.rid = artid;
			vobj.rval = encodeURI($(obj).parent().parent().find(
					"span[name='title']").html().replace(
					new RegExp('(["\"])', 'g'), "\\\""));
			vobj.rtab = encodeURI($(obj).parent().parent().find(
					"input[name='hidtablename']").val().replace(
					new RegExp('(["\"])', 'g'), "\\\""));
			arrArticle.push(vobj);
			jsonArticle.jsondata = arrArticle;
			param = "do=close&type=" + type + "&fileid="
					+ JSON.stringify(jsonArticle);
		} else {
			param = "do=close&type=" + type + "&fileid=" + JSON.stringify(obj);
		}

		$.ajax({
			url : $("#hidurl").val(),
			type : "GET",
			data : param,
			async : false,
			cache : false,
			success : function(data) {
				if (data == "1") {
					alert("关闭成功");
					$("#selectAll").attr("checked", false);
					addCloseArticleFlag(obj, type, artid, flag);
				} else {
					alert("关闭失败");
				}
			},
			error : function() {
				alert("服务器错误");
			}
		});
	}


function OpenArticleComment(obj, type, artid, flag) {
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=open&type=" + type + "&fileid=" + artid,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("打开成功");
				cancelCloseArticleFlag(obj, type, artid, flag);
			} else {
				alert("打开失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function addCloseArticleFlag(obj, type, artid, flag) {
	if (flag == 0) {
		var vcon = $(obj).parent().parent().find("span[name='title']").parent()
				.html();
		vcon = vcon + "<em class=\"fine\">已关闭</em>";
		$(obj).parent().parent().find("span[name='title']").parent().html(vcon);
		$(obj).parent().html(
				"<a title=\"打开评论\" class=\"turnon\" onclick=\"OpenArticleComment(this,'"
						+ type + "','" + artid + "'," + flag
						+ ")\" href=\"javascript:void(0);\"/>");
	} else {
		$("input[name='chkfileid']")
				.each(
						function() {
							if ($(this).prop("checked") == true) {
								var rid = $(this).val();
								$(this).parent().parent().find(
										"span[name='title']").parent().find(
										".fine").remove();
								var vcon = $(this).parent().parent().find(
										"span[name='title']").parent().html();
								vcon = vcon + "<em class=\"fine\">已关闭</em>";
								$(this).parent().parent().find(
										"span[name='title']").parent().html(
										vcon);
								$(this)
										.parent()
										.parent()
										.find("td:last")
										.html(
												"<a title=\"打开评论\" class=\"turnon\" onclick=\"OpenArticleComment(this,'"
														+ type
														+ "','"
														+ rid
														+ "',0)\" href=\"javascript:void(0);\"/>");
								$(this).prop("checked", false);
							}
						});
	}
	;
}

function cancelCloseArticleFlag(obj, type, artid, flag) {
	if (flag == 0) {
		$(obj).parent().parent().find("span[name='title']").parent().find(
				".fine").remove();
		$(obj)
				.parent()
				.html(
						"<a title=\"关闭评论\" class=\"turnoff\" href=\"javascript:void(0);\" onclick=\"CloseArticleComment(this,'"
								+ type
								+ "','"
								+ artid
								+ "',"
								+ flag
								+ ")\"></a>");
	}
	;
}

function closeArticleCommentAll() {
	var vFild = "";
	var jsonArticle = {
		"jsondata" : ""
	};
	var arrArticle = new Array();
	$("input[name='chkfileid']").each(
			function() {
				if ($(this).prop("checked") == true) {
					// vFild = vFild + $(this).val() + ",";
					var obj = {
						rid : "",
						rval : "",
						rtab : ""
					};
					obj.rid = $(this).val();
					obj.rval = encodeURI($(this).parent().parent().find(
							"span[name='title']").html());
					obj.rtab = encodeURI($(this).parent().parent().find(
					"input[name='hidtablename']").val().replace(
					new RegExp('(["\"])', 'g'), "\\\""));

					arrArticle.push(obj);
				}
			});
	if (arrArticle.length == 0) {
		alert("请选择要关闭评论的文章！");
		return false;
	}
	jsonArticle.jsondata = arrArticle;
	CloseArticleComment(jsonArticle, $("#hidTypeEn").val(), vFild, 1);
}


function getCommentODataSearchMenu() {

	$.ajax({
				type : "GET",
				url : "CommentSearchHandler.do",
				async : false,
				cache : false,
				data : "do=setsearchmenu&deftype=" + $("#hidTypeid").val(),
				success : function(data) {
					$("#rdftype")
							.html(
									"<em class=\"classclose\" title=\"关闭\" onclick=\"$('#rdftype').css('display','none');\"></em>"
											+ data);
					getCommentSearchField($("#hidTypeid").val(), 0);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器错误");
				}
			});
}

function getCommentSearchField(typeid, vdefiled) {
	var param = "";
	if (vdefiled == 0) {
		param = "do=getsearchfield&typeid=" + typeid + "&seafield="
				+ $("#hidSeaField").val();
	} else {
		param = "do=getsearchfield&typeid=" + typeid + "&seafield="
				+ $("#SeachField_SelectValue").val();
	}
	$.ajax({
		type : "GET",
		url : "CommentSearchHandler.do",
		async : false,
		cache : false,
		data : param,
		success : function(data) {
			$("#ddlSearchField").html(data);
			$("#hidSeaField").val($("#SeachField_SelectValue").val());
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("服务器错误");
		}
	});
};


//commentsortcontrol.jsp
function openAllComment() {
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=openall",
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				window.location.reload();
			} else {
				alert("打开失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}
function closeAllComment() {
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=closenall",
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				window.location.reload();
			} else {
				alert("关闭失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function openSortComment(obj,typeid) {
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=opensort&typeid="+typeid,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				obj.onclick=null;
				$(obj).unbind("click").bind("click",function() {
					closeSortComment(obj,typeid);
				});
				$(obj).attr("title","关闭评论");
				$(obj).removeClass("closeclass").addClass("openclass");
			} else {
				alert("打开失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function closeSortComment(obj,typeid) {
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=closesort&typeid="+typeid,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				obj.onclick=null;
				$(obj).unbind("click").bind("click",function(){
					openSortComment(obj,typeid);
				});
				$(obj).attr("title","打开评论");
				$(obj).removeClass("openclass").addClass("closeclass");
			} else {
				alert("关闭失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

//commentsearch.jsp
function getCommentSearchMenu() {
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		async : false,
		cache : false,
		data : "do=setsearchmenu",
		success : function(data) {
			$("#rdftype").html(data);
			if ($("#rdftype").css("height") > "40px") {
				$("#rdftype").attr("class", "foldtext");
				$("#rdftype").next().attr("class", "fold");
				$("#rdftype").next().bind("click", function() {
					if ($("#rdftype").attr("class") == "foldtext") {
						$("#rdftype").attr("class", "unfoldtext");
						$("#rdftype").next().attr("class", "unfold");
					} else {
						$("#rdftype").attr("class", "foldtext");
						$("#rdftype").next().attr("class", "fold");
					}
				});
			} else {
				$("#rdftype").attr("class", "unfoldtext");
				$("#rdftype").next().attr("class", "");
			}
			getSearchField($("#hidTypeid").val());
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("服务器错误");
		}
	});
}

function getCommentArticleData(){
	var vseaField = $("#SeachField_SelectValue").val();
	window.location.href = "CommentSearchList.do?vtypeid="
			+ $("#hidTypeid").val() + "&seafiled=" + vseaField + "&searchval="
			+ encodeURI($.trim($("#seachText").val()));
}

//literaturecontrol.jsp
function openMultiComment(){
	var vComID = "";
	$("input[name='chkdocuid']").each(function() {
		if ($(this).prop("checked") == true)
			vComID = vComID + $(this).val() + ",";
	});
	if (vComID == "") {
		alert("请选择要删除的日志记录！");
		return false;
	}
	openComment(vComID);
}
function openComment(cid){
	if (cid.length == 0) {
		return false;
	}
	if (!confirm("你确定打开该文献的评论吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=open&cid=" + cid,
		success : function(data) {
			if (data == "1") {
				alert("打开成功");
				getFormDataCount(curpage);
			} else {
				alert("打开失败");
			}
		},
		error : function() {
			alert("加载失败");
		}
	});
}

//articlepraise.jsp
function getPraiseList(){
	var varcon = "";

	if ($.trim($("#txtTitle").val()).length > 0) {
		varcon += "tl=" + encodeURI($.trim($("#txtTitle").val())) + "&";
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

//userfreelist.jsp
function getUserFreeContext(){
	var varcon = "";
	varcon += "txtUserName=" + $("#txtUserName").val()+"&"+"txtStartDate="+$("#txtStartDate").val()+"&"+"txtEndDate="+$("#txtEndDate").val();
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}
//rechargeList.jsp
function getRechargeContext(){
	var varcon = "";
	varcon += "txtUserName=" + $("#txtUserName").val()+"&"+"txtStartDate="+$("#txtStartDate").val()+"&"+"txtEndDate="+$("#txtEndDate").val()+"&"+"platform="+$("#platform").val()+"&"+"issus="+$("#issus").val();
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}
//orgInfomation.jsp
function getOrgInfomationContext(){
	var varcon = "";
	varcon += "txtOrg=" + $("#txtOrg").val()+"&"+"txtStartDate="+$("#txtStartDate").val()+"&"+"txtEndDate="+$("#txtEndDate").val();
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}
//用户资料
function getUserInfomationContext(){
	var varcon = "";
	varcon += "txtUserName=" + $("#txtUserName").val()+"&"+"txtMobile="+$("#txtMobile").val();
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function delPraise(vid,vtab){
	if (vid.length == 0) {
		return false;
	}
	if (!confirm("你确定要删除该条记录吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=delpraise&vid=" + vid +"&vtab="+vtab,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败");
		}
	});
}
function delMultiPraise(){
	var vid = "";
	var vtab = "";
	$("input[name='chkpras']").each(function() {
		if ($(this).prop("checked") == true)
			vid = vid + $(this).val().split(",")[0] + ",";
			vtab = vtab + $(this).val().split(",")[1] + ",";
	});
	if (vid == "") {
		alert("请选择要删除的记录！");
		return false;
	}
	delPraise(vid,vtab);
}

function getAdvertisementList(){
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "do=getlist",
		success : function(data) {
			if(data.length==0){
				$("#divdata").html("<div class=\"nodata\">还没有数据。</div>");
			}else{
				$("#divdata").html(data);
				$("#divaddsure").show();
				
			}
		},
		error : function() {
			alert("加载失败！");
		}
	});
}

function delAdvertisement(aid){
	if (!confirm("你确定要删除该条广告吗")) {
		return false;
	}
	$.ajax({
		type : "GET",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : "do=del&advid="+aid,
		success : function(data) {
			if(data == "1"){
				alert("删除成功");
				window.location.reload();
			}else{
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败！");
		}
	});
}

function chkAdvInfoForm(){
	if($.trim($("#txtContent").val()).length==0){
		alert("广告详细不能为空！");
		return false;
	}
	if($("input[name='strType']").get(0).checked){
		if($("#txtContent").val().substring(0,7)!="http://"){
			alert("链接必须以http://开头");
			return false;
		}
	}else{
		if($.trim($("#txtContent").val()).length>500){
			alert("广告详情不要超过500字");
			return false;
		}
	}
	if ($.trim($("#txtStartDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtStartDate").val()))) {
			alert("开始日期格式不正确");
			return false;
		}
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtEndDate").val()))) {
			alert("结束日期格式不正确");
			return false;
		}
	}
	return true;
}

//errorfilelist.jsp
function getErrorFileList() {
	var varcon = "";

	if ($.trim($("#txtKeyWord").val()).length > 0) {
		varcon += "kw=" + encodeURI($.trim($("#txtKeyWord").val())) + "&";
	}
	if ($.trim($("#ddlTypeName_SelectValue").val()).length > 0) {
		varcon += "tid="
				+ encodeURI($.trim($("#ddlTypeName_SelectValue").val())) + "&";
	}

	if($.trim($("#ddlErrorType").val()).length>0){
		varcon += "et=" + encodeURI($.trim($("#ddlErrorType").val())) + "&";
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function delMultiErrorFile() {
	var vNoticeID = "";
	$("input[name='chkerrorid']").each(function() {
		if ($(this).prop("checked") == true)
			vNoticeID = vNoticeID + $(this).val() + ",";
	});
	if (vNoticeID == "") {
		alert("请选择要删除的数据！");
		return false;
	}
	delErrorFile(vNoticeID);
}

function delErrorFile(efid) {
	if (efid.length == 0) {
		return false;
	}
	if (!confirm("你确定要删除该条数据吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=delerrorfile&efid=" + efid,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败");
		}
	});
}

function delDownloadTraceAll(){
	if (!confirm("确认删除？")) {
		return false;
	}
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=delfileAll",
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(1);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

function delDownloadTraceSelectAll(){
	var vFild = "";
	$("input[name='chktraceid']").each(function() {
		if ($(this).prop("checked") == true)
			vFild = vFild + $(this).val() + ",";
	});
	if (vFild == "") {
		alert("请选择要删除的文件！");
		return false;
	}
	DisDownlodaTrace(vFild);
}

function DisDownlodaTrace(artids) {

	if (!confirm("确认删除？")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		url : $("#hidurl").val(),
		type : "GET",
		data : "do=delfile&id=" + artids,
		async : false,
		cache : false,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("服务器错误");
		}
	});
}

//crash.jsp
function getAppCrashContent() {
	var varcon = "";
	if ($.trim($("#txtStartDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtStartDate").val()))) {
			alert("开始日期格式不正确");
			return false;
		}
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		if (!IsDate($.trim($("#txtEndDate").val()))) {
			alert("结束日期格式不正确");
			return false;
		}
	}

	if ($.trim($("#txtStartDate").val()).length > 0
			&& $.trim($("#txtEndDate").val()).length > 0) {
		if ($("#txtStartDate").val() > $("#txtEndDate").val()) {
			var tempTime = $("#txtStartDate").val();
			$("#txtStartDate").val($("#txtEndDate").val());
			$("#txtEndDate").val(tempTime);
		}
	}
	if ($.trim($("#txtStartDate").val()).length > 0) {
		varcon = varcon + "startdate=" + $.trim($("#txtStartDate").val()) + "&";
	}
	if ($.trim($("#txtEndDate").val()).length > 0) {
		varcon = varcon + "enddate=" + $.trim($("#txtEndDate").val()) + "&";
	}
	if ($.trim($("#hidpageparam").val()).length > 0) {
		varcon = varcon + $.trim($("#hidpageparam").val()) + "&";
	}
	$("#hidparam").val(varcon);
	getFormDataCount(1);
}

function delCrashRec(acid) {
	if (acid.length == 0) {
		return false;
	}
	if (!confirm("你确定要删除该条数据吗")) {
		return false;
	}
	var curpage = $("#hidcurpage").val();
	if (curpage == undefined) {
		curpage = 1;
	} else {
		curpage = parseInt(curpage, 10);
	}
	$.ajax({
		type : "get",
		url : $("#hidurl").val(),
		async : true,
		cache : false,
		data : "do=del&acid=" + acid,
		success : function(data) {
			if (data == "1") {
				alert("删除成功");
				getFormDataCount(curpage);
			} else {
				alert("删除失败");
			}
		},
		error : function() {
			alert("加载失败");
		}
	});
}

function delMultiCrashRec(){
	var vSysID = "";
	$("input[name='appcrashid']").each(function() {
		if ($(this).prop("checked") == true)
			vSysID = vSysID + $(this).val() + ",";
	});
	if (vSysID == "") {
		alert("请选择要删除的记录！");
		return false;
	}
	delCrashRec(vSysID);
}

function saveAdvertisementList() {
	$.ajax({
		type : "POST",
		url : $("#hidurl").val(),
		cache : false,
		async : true,
		data : $("#form1").serialize(),
		success : function(data) {
			closeLoading();
			if (data == "1") {
				showPopAlert(1, "保存成功");
			} else if (data == "0") {
				showPopAlert(1, "保存失败");
			}
		},
		error : function() {
			closeLoading();
			alert("服务器错误！");
		}
	});
}