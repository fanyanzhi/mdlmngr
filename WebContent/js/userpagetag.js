function PageTagInit(id, callback) {
	document.getElementById(id + "first").onclick = function() {
		PageTagClick(id, callback, 1);
	};
	document.getElementById(id + "pre").onclick = function() {
		PageTagClick(id, callback, 2);
	};
	document.getElementById(id + "next").onclick = function() {
		PageTagClick(id, callback, 3);
	};
	document.getElementById(id + "last").onclick = function() {
		PageTagClick(id, callback, 4);
	};
	document.getElementById(id + "turn").onclick = function() {
		PageTagClick(id, callback, 5);
	};
	document.getElementById(id + "page").onkeypress = function(event) {
		CheckEnterKey(id, event);
	};
	SetPageTagStyle(id, 1, callback);
}

function SetPageTagStyle(id, curPage, callback) {
	var first = document.getElementById(id + "first");
	var pre = document.getElementById(id + "pre");
	var next = document.getElementById(id + "next");
	var last = document.getElementById(id + "last");
	var totalPage = parseInt(document.getElementById(id + "totalpage").innerHTML, 10);

	if (totalPage == 0) {
		curPage = 0;
		first.className = "hui";
		first.onclick = "";
		pre.className = "hui";
		pre.onclick = "";
		next.className = "hui";
		next.onclick = "";
		last.className = "hui";
		last.onclick = "";
		document.getElementById(id + "page").value = curPage;
		return false;
	}

	if (curPage == 1) {
		first.className = "hui";
		first.onclick = "";
		pre.className = "hui";
		pre.onclick = "";
	} else {
		first.className = "";
		first.onclick = function() {
			PageTagClick(id, callback, 1);
		};
		pre.className = "";
		pre.onclick = function() {
			PageTagClick(id, callback, 2);
		};
	}
	if (curPage == totalPage) {
		next.className = "hui";
		next.onclick = "";
		last.className = "hui";
		last.onclick = "";
	} else {
		next.className = "";
		next.onclick = function() {
			PageTagClick(id, callback, 3);
		};
		last.className = "";
		last.onclick = function() {
			PageTagClick(id, callback, 4);
		};

		document.getElementById(id + "page").value = curPage;
	}
}

function SetPageTagData(id, curPage, totalCount, pageSize, callback) {
	if (document.getElementById(id) == undefined) {
		return;
	}
	document.getElementById(id + "turn").onclick = function() {
		PageTagClick(id, callback, 5);
	};
	document.getElementById(id + "page").onkeypress = function(event) {
		CheckEnterKey(id, event);
	};
	var totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : parseInt(totalCount / pageSize) + 1;
	document.getElementById(id + "totalcount").innerHTML = totalCount;
	document.getElementById(id + "totalpage").innerHTML = totalPage;
	document.getElementById(id + "page").value = curPage;
	SetPageTagStyle(id, curPage, callback);
}

function PageTagClick(id, callback, src) {
	var curPage = parseInt(document.getElementById(id + "page").value, 10);
	var totalCount = parseInt(document.getElementById(id + "totalpage").innerHTML, 10);
	if (isNaN(curPage)) {
		curPage = 1;
	}
	if (curPage < 1) {
		curPage = 1;
	}
	if (curPage > totalCount) {
		curPage = totalCount;
	}
	if (src == 1) {
		curPage = 1;
	} else if (src == 2) {
		curPage = curPage - 1;
	} else if (src == 3) {
		curPage = curPage + 1;
	} else if (src == 4) {
		curPage = totalCount;
	}
	SetPageTagStyle(id, curPage, callback);
	callback(curPage);
}

function CheckEnterKey(id, e) {
	var varEvent = e;
	if (!varEvent) {
		varEvent = event;
	}
	if (varEvent && (varEvent.keyCode == 13 || varEvent.which == 13)) {
		try {
			document.getElementById(id + "turn").click();
		} catch (e) {
			document.getElementById(id + "turn").onclick();
		}
		return false;
	}

}