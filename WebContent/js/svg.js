function circleover(type, obj, count, date) {
	 $("#gMain circle").animate({
			svgR : 5
		}, 5);
	 $("#gMain circle").attr("stroke", "none");
	 $("#gMain circle").attr("stroke-width", "0");
	$(obj).animate({
		svgR : 8
	}, 200);
	obj.setAttribute("stroke", "#ffffff");
	obj.setAttribute("stroke-width", "2");
	var iX = parseInt(obj.getAttribute("cx"), 10);
	var iY = parseInt(obj.getAttribute("cy"), 10);
	var iSvgWidth = parseInt(document.getElementById("gTip").parentNode.parentNode.getAttribute("width"), 10);
	var iSvgHeight = parseInt(document.getElementById("gTip").parentNode.parentNode.getAttribute("height"), 10);
	var iRectWidth = parseInt(document.getElementById("rectTip").getAttribute("width"), 10);
	var iRectHeight = parseInt(document.getElementById("rectTip").getAttribute("height"), 10);
	var txtTip2 = "";
	switch (type) {
	case 1:
		txtTip2 = "最大在线终端数";
		break;
	case 2:
		txtTip2 = "最大在线用户数";
		break;
	case 3:
		txtTip2 = "登录用户数";
		break;
	default:
		txtTip2 = "";
		break;
	}

	iX = iX + 20;
	iY = iY - 10;

	if (iX > iSvgWidth - iRectWidth - 100) {
		iX = iX - iRectWidth - 50;
	}

	if (iY > iSvgHeight - iRectHeight - 50) {
		iY = iY - iRectHeight;
	}
	if ($("#gTip").css("display") == "none") {
		$("#gTip").attr("transform", "translate(" + iX + "," + iY + ")");
		$("#gTip").show("fast");
	} else {
		$("#gTip").animate({
			svgTransform : "translate(" + iX + "," + iY + ")"
		}, 300);
	}
	if (obj.getAttribute("fill") == "#f98002") {
		document.getElementById("rectTip").setAttribute("stroke", "#fcc184");
		document.getElementById("rectTip").setAttribute("fill", "#fceddd");
	} else {
		document.getElementById("rectTip").setAttribute("stroke", "#73cbec");
		document.getElementById("rectTip").setAttribute("fill", "#e5f7fe");
	}
	document.getElementById("txtTip1").textContent = "日期：" + date;
	document.getElementById("txtTip2").textContent = txtTip2 + "：" + count;

}

function gmainout(e) {
	var targetName = e.relatedTarget==null?null:e.relatedTarget.toString();
	var targetType = targetName==null?"":targetName.substr(8, 3);
	if (targetType.toLowerCase() != "svg") {
		setTimeout("hideTip()", 500);
	}
}

function hideTip() {
	if ($("#gTip").css("display") != "none") {
		$("#gTip").hide(300);
		$("#gMain circle").attr("r", "5");
		$("#gMain circle").attr("stroke", "none");
		$("#gMain circle").attr("stroke-width", "0");
	}
}

function BottomLineOver(flg) {
	var strName = "path" + flg;
	var iWidth = parseInt($("#" + strName).attr("stroke-width"), 10);
	$("#" + strName).attr("stroke-width", iWidth + 1);
}

function BottomLineOut(flg) {
	var strName = "path" + flg;
	var iWidth = parseInt($("#" + strName).attr("stroke-width"), 10);
	$("#" + strName).attr("stroke-width", iWidth - 1);
}

function getLoginPicByType(){
	
}

