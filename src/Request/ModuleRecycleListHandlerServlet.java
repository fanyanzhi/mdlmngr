package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;

import BLL.ModuleMngr;

/**
 * Servlet implementation class ModuleRecycleListHandlerServlet
 */
@WebServlet("/ModuleRecycleListHandler.do")
public class ModuleRecycleListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModuleRecycleListHandlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getDeletedModuleCount(request);
		}else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getModuleRecycleList(request);
		} else if ("recycle".equals(request.getParameter("do"))) {
			strResult = recycleModuleData(request);
		} 
		out.write(strResult);
		out.flush();
		out.close();
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private String getDeletedModuleCount(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		if(ModuleMngr.getDeletedModuleCount()>0){
			return "1";
		}
		else if(ModuleMngr.getDeletedModuleTypeCount()>0){
			return "1";
		}
		return "0";
	}
	
	private String getModuleRecycleList(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		int iDBKeepDays = Integer.parseInt(Common.GetConfig("DBKeepDays"));
		List<Map<String, Object>> lstModuleInfo = ModuleMngr.getModuleList();
		if (lstModuleInfo == null) {
			return "";
		}
		StringBuilder sbHtml = new StringBuilder();
		Iterator<Map<String, Object>> iMap = lstModuleInfo.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>名称</th>");
		sbHtml.append("<th width=\"10%\">英文名称</th>");
		sbHtml.append("<th width=\"15%\">中文名称</th>");
		sbHtml.append("<th width=\"5%\">是否有效</th>");
		sbHtml.append("<th width=\"5%\">是否显示</th>");
		sbHtml.append("<th width=\"10%\">删除日期</th>");
		sbHtml.append("<th width=\"60\">剩余天数</th>");
		sbHtml.append("<th width=\"100\">操作</th>");
		sbHtml.append("</tr> ");
				
		int iNum = 1;
		while (iMap.hasNext()) {
			mapData = iMap.next();
			String strDelete=String.valueOf(mapData.get("isdelete"));
			String strModuleType="";
			if("0".equals(strDelete)){
				strModuleType=getSubModuleList(String.valueOf(mapData.get("id")), String.valueOf(mapData.get("tablename_ch")),"0");	
			}else{
				strModuleType=getSubModuleList(String.valueOf(mapData.get("id")), String.valueOf(mapData.get("tablename_ch")),"1");
			}
			
			if ("0".equals(strDelete)&&strModuleType.length()==0) {
				continue;
			}
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(String.valueOf(iNum++)).append("</td>");
			//sbHtml.append("<td><a href=\"ModuleInfo.do?md=").append(String.valueOf(mapData.get("id"))).append("\">").append(String.valueOf(mapData.get("tablename"))).append("</a></td>");
			sbHtml.append("<td>").append(String.valueOf(mapData.get("tablename"))).append("</td>");
			sbHtml.append("<td>").append(String.valueOf(mapData.get("tablename_en"))).append("</td>");
			sbHtml.append("<td>").append(String.valueOf(mapData.get("tablename_ch"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append("1".equals(String.valueOf(mapData.get("status"))) ? "是" : "否").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append("1".equals(String.valueOf(mapData.get("isdisplay"))) ? "是" : "否").append("</td>");
			if("1".equals(strDelete)){
				sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("deletetime")), "yyyy-MM-dd")).append("</td>");
				Date dateDelete = Common.ConvertToDate(String.valueOf(mapData.get("deletetime")), "yyyy-MM-dd");
				long leaveDay=iDBKeepDays-(new Date().getTime() - dateDelete.getTime())/86400000;
				sbHtml.append("<td class=\"tabcent\"><span title='").append(String.valueOf(leaveDay)).append("天后自动删除'>").append(String.valueOf(leaveDay)).append("</span></td>");
				sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" class=\"cover\" onclick=\"recycleModule(0,").append(String.valueOf(mapData.get("id"))).append(",0)\" title=\"恢复\"></a></td>");	
			}else{
				sbHtml.append("<td class=\"tabcent\">&nbsp;</td>");
				sbHtml.append("<td class=\"tabcent\">&nbsp;</td>");
				sbHtml.append("<td class=\"tabopt\"> </td>");
			}
			
			sbHtml.append("</tr> ");
			sbHtml.append(strModuleType);
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	/*
	 * 获取子模块列表
	 */
	private String getSubModuleList(String TableID, String TableName,String ParentStatus) {
		int iDBKeepDays = Integer.parseInt(Common.GetConfig("DBKeepDays"));
		List<Map<String, Object>> lstModuleType = ModuleMngr.getModuleTypeList(TableID,"1");
		if (lstModuleType == null) {
			return "";
		}
		StringBuilder sbSubModule = new StringBuilder();
		Iterator<Map<String, Object>> iMap = lstModuleType.iterator();
		Map<String, Object> mapData = null;
		while (iMap.hasNext()) {
			mapData = iMap.next();
			sbSubModule.append("<tr class=\"sublist\">");
			sbSubModule.append("<td class=\"num\"></td>");
			//sbSubModule.append("<td class=\"submod\"><a href=\"ModuleTypeInfo.do?tabid=").append(TableID).append("&tabname=").append(TableName).append("&id=").append(mapData.get("id")).append("\">").append(mapData.get("typename")).append("</a></td>");
			sbSubModule.append("<td class=\"submod\">").append(mapData.get("typename")).append("</td>");
			sbSubModule.append("<td>").append(mapData.get("typename_en")).append("</td>");
			sbSubModule.append("<td>").append(mapData.get("typename_ch")).append("</td>");
			sbSubModule.append("<td class=\"tabcent\">").append("0".equals(mapData.get("isdelete")) ? "是" : "否").append("</td>");
			sbSubModule.append("<td class=\"tabcent\">&nbsp;</td>");
			sbSubModule.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("deletetime")), "yyyy-MM-dd")).append("</td>");
			Date dateDelete = Common.ConvertToDate(String.valueOf(mapData.get("deletetime")), "yyyy-MM-dd");
			long leaveDay=iDBKeepDays-(new Date().getTime() - dateDelete.getTime())/86400000;
			sbSubModule.append("<td class=\"tabcent\"><span title='").append(String.valueOf(leaveDay)).append("天后自动删除'>").append(String.valueOf(iDBKeepDays-(new Date().getTime() - dateDelete.getTime())/86400000)).append("</td>");
			sbSubModule.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" class=\"cover\" onclick=\"recycleModule(1,").append(String.valueOf(mapData.get("id"))).append(",").append(ParentStatus).append(")\" title=\"恢复\"></a></td>");
			sbSubModule.append("</tr>");
		}
		return sbSubModule.toString();
	}
	
	
	private String recycleModuleData(HttpServletRequest request) throws ServletException {
		String strResult="";
		String strTypeID=request.getParameter("type");
		String strID=request.getParameter("id");
		
		if("0".equals(strTypeID)){
			if(ModuleMngr.recycleModuleInfo(strID)){
				strResult="1";
			}else{
				strResult="0";
			}
		}else if("1".equals(strTypeID)){
			if(ModuleMngr.recycleModuleTypeInfo(strID)){
				strResult="1";
			}else{
				strResult="0";
			}
		}
		return strResult;
	}

}
