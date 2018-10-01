function SetSimPageTagStyle(id, curPage, callback) {
			var pre = document.getElementById(id + "pre");
			var next = document.getElementById(id + "next");
			var totalPage = parseInt(document.getElementById(id + "totalpage").innerHTML, 10);

			if (totalPage == 0) {
				curPage = 0;
				pre.className = "nosimpre";
				pre.onclick = "";
				next.className = "nosimnext";
				next.onclick = "";
				document.getElementById(id + "page").innerHTML = curPage;
				return false;
			}
			if (curPage == 1) {
				pre.className = "nosimpre";
				pre.onclick = "";
			} else {
				pre.className = "simpre";
				pre.onclick = function() {
					SimPageTagClick(id, callback, 2);
				};
			}
			if (curPage == totalPage) {
				next.className = "nosimnext";
				next.onclick = "";
			} else {
				next.className = "simnext";
				next.onclick = function() {
					SimPageTagClick(id, callback, 3);
				};
				document.getElementById(id + "page").innerHTML = curPage;
			}
		}

		function SetSimPageTagData(id, curPage, totalCount, pageSize, callback) {
			if (document.getElementById(id) == undefined) {
				return;
			}
			var totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : parseInt(totalCount / pageSize) + 1;
			document.getElementById(id + "totalpage").innerHTML = totalPage;
			document.getElementById(id + "page").innerHTML = curPage;
			SetSimPageTagStyle(id, curPage, callback);
		}

		function SimPageTagClick(id, callback, src) {
			var curPage = parseInt(document.getElementById(id + "page").innerHTML, 10);
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
			SetSimPageTagStyle(id, curPage, callback);
			callback(curPage);
		}