///设置cookie
function setCookie(NameOfCookie, value) {
    var expiredays = 1; //此 cookie 将被保存 1 天
    var ExpireDate = new Date();
    ExpireDate.setTime(ExpireDate.getTime() + (expiredays * 24 * 3600 * 1000));
    document.cookie = NameOfCookie + "=" + escape(value) +
    "; expires=" + ExpireDate.toGMTString();
}

///获取cookie值 
function getCookie(NameOfCookie) {
    var re = new RegExp("\\b" + NameOfCookie + "=([^;]*)\\b");
    var arr = re.exec(document.cookie);
    return arr ? arr[1] : "";
}

///删除cookie 
function delCookie(NameOfCookie) {
    if (getCookie(NameOfCookie)) {
        document.cookie = NameOfCookie + "=" +
"; expires=Thu, 01-Jan-70 00:00:01 GMT";
    }
}
