function drpselect(id) {
	if (document.getElementById(id + "_ddlul").style.display == "block") {
		drphide(id);
	} else {
		// <%=mBeforeShowListScript %>;
		drpshow(id);	
		// <%=mAfterShowListScript %>;
	}
}

function drpItemSelected(id, text, value) {
	document.getElementById(id + "_ddl").innerHTML = text;
	document.getElementById(id + "_ddl").title = text;
	document.getElementById(id + "_SelectText").value = text;
	document.getElementById(id + "_SelectValue").value = value;
	drphide(id);
}

function drpshow(id) {
	document.getElementById(id + "_ddlul").style.display = "block";
	document.getElementById(id + "_ddlul").focus();

}

function drphide(id) { // alert(event.srcElement.id);
	document.getElementById(id + "_ddlul").style.display = "none";
}

function DrpTagInit(id, text, value) {
	if (text != "null") {
		document.getElementById(id + "_ddl").innerHTML = text;
		document.getElementById(id + "_ddl").title = text;
		document.getElementById(id + "_SelectText").value = text;
	}
	if (value != "null") {
		document.getElementById(id + "_SelectValue").value = value;
	}

	document.getElementById(id + "_ddl").onclick = function() {
		drpselect(id);
	};
	document.getElementById(id + "_ddl").onmousedown = function() {
		document.getElementById(id + "_ddlul").onblur = null;
	};
	document.getElementById(id + "_ddl").onmouseup = function() {
		document.getElementById(id + "_ddlul").onblur = function() {
			drphide(id);
		};
	};
}