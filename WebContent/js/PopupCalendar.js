// Powered by: amo
// Improved by Chris.J

var oCalendarEn = new PopupCalendar("oCalendarEn"); //初始化控件时,请给出实例名称如:oCalendarEn
oCalendarEn.weekDaySting = new Array("S", "M", "T", "W", "T", "F", "S");
oCalendarEn.monthSting = new Array("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
oCalendarEn.oBtnTodayTitle = "Today";
oCalendarEn.oBtnCancelTitle = "Cancel"; 
oCalendarEn.Init();
var oCalendarChs=new PopupCalendar("oCalendarChs");	//初始化控件时,请给出实例名称:oCalendarChs
oCalendarChs.weekDaySting=new Array("日","一","二","三","四","五","六");
oCalendarChs.monthSting=new Array("一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月");
oCalendarChs.oBtnTodayTitle="今天";
oCalendarChs.oBtnCancelTitle="取消"; 
oCalendarChs.Init();

//delete by liupei 20091201 删除对触发控件的验证
//document.onclick=DocumentClick;

var IS_IE= (navigator.userAgent.toLowerCase().indexOf("msie") != -1 && document.all) ;
if(!IS_IE && typeof Event == "function")
{
	Window.prototype.__defineGetter__("event", GetEvent);
	//window.event.__defineGetter__("srcElement", GetEventSrcElement);
	Event.prototype.__defineGetter__("srcElement", GetEventSrcElement);
}

function PopupCalendar(InstanceName)
{
	///Global Tag
	this.instanceName=InstanceName;
	///Properties
	this.separator="-";
	this.oBtnTodayTitle="Today";
	this.oBtnCancelTitle="Cancel";
	this.weekDaySting=new Array("S","M","T","W","T","F","S");
	this.monthSting=new Array("January","February","March","April","May","June","July","August","September","October","November","December");
	this.Width=240;
	this.currDate = new Date();
	this.today=new Date();
	this.startYear=1970;
	this.endYear=new Date().getFullYear() + 5;
	///Css
	this.normalfontColor="#666666";
	this.selectedfontColor="#666";
	this.divBorderCss="3px solid #888";
	this.titleTableBgColor="#666666";
	this.titleBorderColor="#000000";
	this.tableBorderColor="#cccccc";
	///Method
	this.Init=CalendarInit;
	this.Fill=CalendarFill;
	this.Refresh=CalendarRefresh;
	this.Restore=CalendarRestore;
	///HTMLObject
	this.oTaget=null;
	this.oPreviousCell=null;
	this.sDIVID=InstanceName+"_Div";
	this.sTABLEID=InstanceName+"_Table";
	this.sMONTHID=InstanceName+"_Month";
	this.sYEARID=InstanceName+"_Year";
	this.sDAYTDID=InstanceName+"_DayTD";
	this.sTODAYBTNID=InstanceName+"_TODAYBTN";
	this.sCANCELID = InstanceName + "_Cancel";
	this.bgIframeID = InstanceName + "_Iframe";
}

function CalendarInit()				///Create panel
{

	//var sMonth = this.currDate.getMonth();
	//var sYear = this.currDate.getFullYear();
	var htmlAll = "<div id='" + this.sDIVID + "' style='display:none;Z-INDEX: 999;width:240px;position:absolute;width:" + this.Width + ";border:" + this.divBorderCss + ";padding:5px; background-color:#f1f1f1'>";
	htmlAll += "<div align='center' style=\"margin-bottom:5px;\">";
	/// Month
	htmloMonth="<select id='" + this.sMONTHID + "' onchange=CalendarMonthChange(" + this.instanceName + ") style='width:45%; border:1px solid #ccc;  margin:0px 5px;'>";
	for(var i=0; i<12; i++)
	{			
		htmloMonth += "<option value='" + i + "'>" + this.monthSting[i] + "</option>";
	}
	htmloMonth += "</select>";
	/// Year
	htmloYear = "<select id='" + this.sYEARID + "' onchange=CalendarYearChange(" + this.instanceName + ") style='width:45%; border:1px solid #ccc; margin:0px 5px;'>";
	for(i=this.startYear; i<=this.endYear; i++)
	{
		htmloYear += "<option value='" + i + "'>" + i + "</option>";
	}
	htmloYear += "</select></div>";
	/// Day
	htmloDayTable = "<table id='" + this.sTABLEID + "' width='100%' border=0 cellpadding=0 cellspacing=1 bgcolor='" + this.tableBorderColor + "' style=\" font-family: arial;\">";
	htmloDayTable += "<tbody>";
	for(i=0;i<=6;i++)
	{
		if(i==0)
		{
			htmloDayTable += "<tr bgcolor='" + this.titleTableBgColor + "' bgcolor='" + this.titleBorderColor + "' style='color:#eee; ' >";
		}
		else
		{
			htmloDayTable += "<tr>";
		}
		for(var j=0;j<7;j++)
		{

			if(i==0)
			{
				htmloDayTable += "<td height='24' align='center' valign='middle' style='cursor: pointer;'>";
				htmloDayTable += this.weekDaySting[j] + "</td>";
			}
			else
			{
				var DayID = this.sDAYTDID + i + j;
				htmloDayTable += "<td id=" + DayID + " height='26' align='center' valign='middle' style='cursor: pointer; line-height:26px; '";
				htmloDayTable += " onmouseover=CalendarCellsMsOver(" + this.instanceName + ")";
				htmloDayTable += " onmouseout=CalendarCellsMsOut(" + this.instanceName + ")";
				htmloDayTable += " onclick=CalendarCellsClick(this," + this.instanceName + ")>";
				htmloDayTable += "&nbsp;</td>";
				
				//Add Event Handler for Day_TD (Mainly for FireFox)
				//AddElementEvent(DayID, 'onmouseover', function(){CalendarCellsMsOver(this.instanceName)});
				//AddElementEvent(DayID, 'onmouseout', function(){CalendarCellsMsOut(this.instanceName)});
				//AddElementEvent(DayID, 'onclick', function(){CalendarCellsClick(this.instanceName)});
			}
		}
		htmloDayTable+="</tr>";	
	}
	htmloDayTable+="</tbody></table>";
	
	/// Today Button
	htmloButton="<div align='center' style='padding:5px'>";
	htmloButton+="<input type='button' id='" + this.sTODAYBTNID + "' style='width:100px; margin: 0px 5px; color:#fff; border:1px solid #fff; background-color:#666;cursor: pointer;  padding:3px 0px 0px;  font-size:12px'";
	htmloButton+=" onclick=CalendarTodayClick("+this.instanceName+") value='" + this.oBtnTodayTitle + "'>&nbsp;";
	htmloButton+="<input type='button' id='" + this.sCANCELID + "' style='width:100px ; margin: 0px 5px; border:1px solid #fff; color:#fff; background-color:#666;cursor: pointer; padding:3px 0px 0px; font-size:12px'";
	htmloButton+=" onclick=CalendarCancel("+this.instanceName+") value='" + this.oBtnCancelTitle + "'>";
	htmloButton+="</div>";
	
	/// All
	htmlAll=htmlAll+htmloMonth+htmloYear+htmloDayTable+htmloButton+"</div>";
	htmlAll += "<iframe id=" + this.bgIframeID + " scrolling='no' frameborder='0' style='position:absolute; top:0px; left:0px; Z-INDEX: 998; display:none;filter=progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0);'></iframe>";
	document.write(htmlAll);
	
	this.Fill();	
	
	//添加事件处理(Mainly for FireFox, 也可以去掉Html中inline写法，直接使用此方法以使用IE)
	//AddElementEvent(this.sTODAYBTNID, 'onclick', function(){CalendarTodayClick(this.instanceName)});
	//AddElementEvent(this.sCANCELID, 'onclick', function(){CalendarCancel(this.instanceName)});
	//AddElementEvent(this.sMONTHID, 'onchange', function(){CalendarMonthChange(this.instanceName)});
	//AddElementEvent(this.sYEARID, 'onchange', function(){CalendarYearChange(this.instanceName)});
}

function CalendarFill()			///
{
	var sMonth,sYear,sWeekDay,sToday,oTable,currRow,MaxDay,iDaySn,rowIndex,cellIndex,oSelectMonth,oSelectYear;//sIndex
	sMonth=this.currDate.getMonth();
	sYear=this.currDate.getFullYear();
	sWeekDay=(new Date(sYear,sMonth,1)).getDay();
	sToday=this.currDate.getDate();
	iDaySn=1;
	oTable=document.getElementById(this.sTABLEID);
	currRow=oTable.rows[1];
	MaxDay=CalendarGetMaxDay(sYear,sMonth);
	
	oSelectMonth=document.getElementById(this.sMONTHID);
	oSelectMonth.selectedIndex=sMonth;
	oSelectYear=document.getElementById(this.sYEARID);
	for(var i=0;i<oSelectYear.length;i++)
	{
		if(parseInt(oSelectYear.options[i].value)==sYear)oSelectYear.selectedIndex=i;
	}
	////
	for(rowIndex=1;rowIndex<=6;rowIndex++)
	{
		if(iDaySn>MaxDay)break;
		currRow = oTable.rows[rowIndex];
		cellIndex = 0;
		if(rowIndex==1)cellIndex = sWeekDay;
		for(;cellIndex<currRow.cells.length;cellIndex++)
		{
			if(iDaySn==sToday)
			{
				currRow.cells[cellIndex].innerHTML="<span color='"+this.selectedfontColor+"' style='background:#bd1b1b;font-weight:bold; color:#fff; display:block'>"+iDaySn+"</span>";
				this.oPreviousCell=currRow.cells[cellIndex];
			}
			else
			{
				currRow.cells[cellIndex].innerHTML=iDaySn;	
				currRow.cells[cellIndex].style.color=this.normalfontColor;
			}
			CalendarCellSetCss(0,currRow.cells[cellIndex]);
			iDaySn++;
			if(iDaySn>MaxDay) break;	
		}
	}
}

function CalendarRestore()					/// Clear Data
{	
	var i,j,oTable;
	oTable=document.getElementById(this.sTABLEID);
	for(i=1;i<oTable.rows.length;i++)
	{
		for(j=0;j<oTable.rows[i].cells.length;j++)
		{
			CalendarCellSetCss(0,oTable.rows[i].cells[j]);
			oTable.rows[i].cells[j].innerHTML="&nbsp;";
		}
	}	
}

function CalendarRefresh(newDate)					///
{
	this.currDate=newDate;
	this.Restore();	
	this.Fill();	
}

function CalendarCellsMsOver(oInstance)				/// Cell MouseOver
{
	var myCell = GetEventSrcElement();
	CalendarCellSetCss(0,oInstance.oPreviousCell);
	if(myCell)
	{
		CalendarCellSetCss(1,myCell);
		oInstance.oPreviousCell=myCell;
	}
}

function CalendarCellsMsOut(oInstance)				////// Cell MouseOut
{
	var myCell = GetEventSrcElement();
	CalendarCellSetCss(0,myCell);	
}

function CalendarYearChange(oInstance)				/// Year Change
{
	var sDay,sMonth,sYear,newDate;
	sDay=oInstance.currDate.getDate();
	sMonth=oInstance.currDate.getMonth();
	sYear=document.getElementById(oInstance.sYEARID).value;
	newDate=new Date(sYear,sMonth,sDay);
	oInstance.Refresh(newDate);
}

function CalendarMonthChange(oInstance)				/// Month Change
{
	var sDay,sMonth,sYear,newDate;
	sDay=oInstance.currDate.getDate();
	sMonth=document.getElementById(oInstance.sMONTHID).value;
	sYear = oInstance.currDate.getFullYear();
	newDate=new Date(sYear,sMonth,sDay);
	oInstance.Refresh(newDate);	
}

function CalendarCellsClick(oCell,oInstance)
{
	var sDay,sMonth,sYear,newDate;
	sYear=oInstance.currDate.getFullYear();
	sMonth=oInstance.currDate.getMonth();
	sDay = oInstance.currDate.getDate();
	
	var InnerText = oCell.innerText || oCell.textContent;
	if(InnerText.length > 0 && InnerText != " " && InnerText.charCodeAt(0) != 32 && InnerText.charCodeAt(0) != 160)
	{
		sDay=parseInt(InnerText);
		if(sDay!=oInstance.currDate.getDate())
		{
			newDate=new Date(sYear,sMonth,sDay);
			oInstance.Refresh(newDate);
		}
	}
	sDateString=sYear+oInstance.separator+CalendarDblNum(sMonth+1)+oInstance.separator+CalendarDblNum(sDay);		///return sDateString
	if(oInstance.oTaget.tagName.toLowerCase()=="input")
	{
		oInstance.oTaget.value=sDateString;
	}
	CalendarCancel(oInstance);
	return sDateString;
}

function CalendarTodayClick(oInstance)				/// "Today" button Change
{
    var today = new Date();
    if (oInstance.oTaget.tagName.toLowerCase() == "input") {
        var sDateString = today.getFullYear() + oInstance.separator + CalendarDblNum(today.getMonth() + 1) + oInstance.separator + CalendarDblNum(today.getDate());
        oInstance.oTaget.value = sDateString;
    }
    oInstance.Refresh(today);
    CalendarCancel(oInstance);
}

var ActiveInstance;	//当前活动的Calendar
function getDateString(oInputSrc,oInstance,offsetX,offsetY)
{
	if(oInputSrc && oInstance) {
	    //fresh data
	    var newDate = new Date(oInputSrc.value);
	    
	    if (newDate == "NaN") {
	        var arrDate = oInputSrc.value.split(oInstance.separator);
	        if (arrDate.length == 3) {
	            newDate.setFullYear(arrDate[0], arrDate[1] - 1, arrDate[2]);
	        }
	    }
	    
	    if (newDate != "Invalid Date"&&newDate!="NaN") {
	        oInstance.Refresh(newDate);
	    }
	    else {
	        oInstance.Refresh(new Date());
        }
	    oInputSrc.value = "";
		var CalendarDiv = document.getElementById(oInstance.sDIVID);
		var bgIframe = document.getElementById(oInstance.bgIframeID);
		oInstance.oTaget = oInputSrc;
		ActiveInstance = oInstance;
		
		var divLeft = CalendargetPos(oInputSrc, "Left");
		var divTop = CalendargetPos(oInputSrc, "Top") + oInputSrc.offsetHeight;
		if (offsetX != undefined) {
		    divLeft = divLeft + offsetX;
		}

		if (offsetY != undefined) {
		    divTop = divTop + offsetY;
        }
		if(document.getElementById("head")!=undefined){
			bgIframe.style.left = bgIframe.style.pixelLeft = CalendarDiv.style.left = CalendarDiv.style.pixelLeft = divLeft-240 + "px";
			bgIframe.style.top = bgIframe.style.pixelTop = CalendarDiv.style.top = CalendarDiv.style.pixelTop = divTop-80 + "px";
		}else{
			bgIframe.style.left = bgIframe.style.pixelLeft = CalendarDiv.style.left = CalendarDiv.style.pixelLeft = divLeft+ "px";
			bgIframe.style.top = bgIframe.style.pixelTop = CalendarDiv.style.top = CalendarDiv.style.pixelTop = divTop + "px";
		}
		
//		CalendarDiv.style.left = divLeft + "px";
//		CalendarDiv.style.top = divTop + "px";
//		bgIframe.style.pixelLeft = divLeft + "px";
//		bgIframe.style.pixelTop = divTop + "px";
//		bgIframe.style.left = divLeft + "px";
//		bgIframe.style.top = divTop + "px";
		try{
			bgIframe.style.display = CalendarDiv.style.display = (CalendarDiv.style.display == "none") ? "block" : "none";	
			bgIframe.style.visibility = CalendarDiv.style.visibility = (CalendarDiv.style.visibility == "hide") ? "visible" : "hide";	
			
		}catch(err){}
		bgIframe.width = CalendarDiv.scrollWidth;
		bgIframe.height = CalendarDiv.scrollHeight;
	}	
}

function CalendarCellSetCss(sMode,oCell)			/// Set Cell Css
{
	if(sMode)
	{
		oCell.style.color = "#fff";
		oCell.style.FontWeight = "900";
		oCell.style.backgroundColor = "#bd1b1b";
	}
	else
	{
		oCell.style.color = "#333";
		//oCell.style.border = "1px solid #f1f1f1";
		oCell.style.backgroundColor = "#f1f1f1";
		
	}	
}

function CalendarGetMaxDay(nowYear,nowMonth)			/// Get MaxDay of current month
{
	var nextMonth,nextYear,currDate,nextDate,theMaxDay;
	nextMonth=nowMonth+1;
	if(nextMonth>11)
	{
		nextYear=nowYear+1;
		nextMonth=0;
	}
	else	
	{
		nextYear=nowYear;	
	}
	currDate=new Date(nowYear,nowMonth,1);
	nextDate=new Date(nextYear,nextMonth,1);
	theMaxDay=(nextDate-currDate)/(24*60*60*1000);
	return theMaxDay;
}

function CalendargetPos(el,ePro)				/// Get Absolute Position
{
	var ePos=0;
	while(el!=null)
	{		
		ePos+=el["offset"+ePro];
		el=el.offsetParent;
	}
	return ePos;
}

function CalendarDblNum(num)
{
	if(num < 10) 
	{
		return "0" + num;
	}
	else
	{
		return num;
	}
}

function CalendarCancel(oInstance)			///Cancel
{
	var CalendarDiv = document.getElementById(oInstance.sDIVID);
	var bgIframe = document.getElementById(oInstance.bgIframeID);
	try{		
		CalendarDiv.style.display = "none";		
		bgIframe.style.display = "none";
		CalendarDiv.style.visibility = "hide";
		bgIframe.style.visibility = "hide";
		oInstance.oTaget.document.body.focus();     //enterkeypress get focus
	}catch(err){}
}

//Click Blank Area and Close
function DocumentClick()
{
	if(ActiveInstance)
	{
		var tEventElement = GetEventSrcElement();
		var CalendarDiv=document.getElementById(ActiveInstance.sDIVID);
		if(oCalendarChs.oTaget != tEventElement && !Contains(CalendarDiv, tEventElement))
		{
			CalendarCancel(oCalendarChs);
		}
	}
}

//获得当前事件 (IE & FireFox)
function GetEvent()
{
	//var i = 0;
	if(IS_IE) 
	{
		return window.event;  
	}
	func = GetEventSrcElement.caller;	
	while(func != null)	
	{	
		var arg0 = func.arguments[0];	
		if(arg0)	
		{	
			//if(arg0.constructor == Event)
			if(arg0.target != null)
			{	
				return arg0;	
			}	
		}	
		func = func.caller;	
	}	
	return null;
}

//获得当前事件的触发对象 (IE & FireFox)
function GetEventSrcElement()
{
	if(IS_IE) 
	{
		return window.event.srcElement;  
	}
	else
	{
		var evt = GetEvent();
		if(evt != null)
		{
			return evt.target;
		}
		else
		{
			return null;
		}
	}
}

//DOM中前者是否包含后者 (IE & FireFox)
function Contains(parentNode, childNode)
{
	if(parentNode != null && childNode != null)
	{
		if(typeof parentNode.contains != "undefined")
		{
			return parentNode.contains(childNode);
		}
		else
		{
			//For FireFox
			while(childNode != null)
			{
				childNode = childNode.parentNode;
				if(childNode == parentNode)
				{
					return true;
				}
			}
			return false;
		}
	}
	else
	{
		return false;
	}
}

//为对象添加一个事件处理器 (IE & FireFox)
function AddElementEvent(CtrlID, eventStr, functionCall)
{
	if(eventStr != null && CtrlID != null)
	{
		var el = document.getElementById(CtrlID);
		if(el)
		{
			if (window.attachEvent)
			{
				if(eventStr.substr(0, 2).toLowerCase() != "on")
				{
					eventStr = "on" + eventStr;
				}
				if(typeof el[eventStr] != "function")
				{
					el.attachEvent(eventStr, functionCall);
				}
			}
			if (window.addEventListener) 
			{
				if(eventStr.substr(0, 2).toLowerCase() == "on")
				{
					eventStr = eventStr.substr(2);
				}
				//事件的阶段通过设置addEventListener的最后一个参数为false(指示冒泡)或true(指示捕获)来切换。

				//false表示添加到队列，true表示正在捕获(?)，通常为false
				el.addEventListener(eventStr, functionCall, false);
			}
		}
    }
}