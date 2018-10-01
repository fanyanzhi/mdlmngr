function autoheight(){

var bodyfrm = ( document.compatMode.toLowerCase()=="css1compat" ) ? document.documentElement : document.body;
   var height_t1 = document.getElementById("mdlleft");
   var height_t2 = document.getElementById("mdlright");
   var height_t3 = document.getElementById("mdlrigcon");
   var height_t4 = document.getElementById("leftcon");
   var height_t5 = document.getElementById("statuser");
   var height_t6 = document.getElementById("cataloglist");
   var height_t7 = document.getElementById("commentcon");
   var docWidth = bodyfrm.clientWidth-240+"px";
   var docHeight = bodyfrm.clientHeight-80+"px";
   var rightconHeight = bodyfrm.clientHeight-80-58+"px";
   if(height_t1!=undefined){
   height_t1.style.height = docHeight;
   }
   if(height_t2!=undefined){
   height_t2.style.height = docHeight;
   }
   if(height_t3!=undefined){
   height_t3.style.height = rightconHeight;
   }
   height_t2.style.width = docWidth;
   if(height_t4!=undefined){
   height_t4.style.width=height_t2.clientWidth-450+"px";
   }
   if(height_t5!=undefined){
   height_t5.style.height=bodyfrm.clientHeight-80-165+"px";
   }
   if(height_t6!=undefined){
   height_t6.style.height=bodyfrm.clientHeight-400+"px";
   }
   if(height_t7!=undefined){
	   height_t7.style.height=bodyfrm.clientHeight-115-58+"px";
	}
}
window.onresize =autoheight;
window.onload = autoheight;
 