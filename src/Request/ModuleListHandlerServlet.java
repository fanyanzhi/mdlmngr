package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ModuleMngr;

/**
 * Servlet implementation class ModuleListHandlerServlet
 */
@WebServlet("/ModuleListHandler.do")
public class ModuleListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ModuleListHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getModuleCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getModuleList(request);
		} else if ("chkmoduledata".equals(request.getParameter("do"))) {
			strResult = checkModuleData(request);
		} else if ("chktypedata".equals(request.getParameter("do"))) {
			strResult = checkModuleTypeData(request);
		} else if ("delmodule".equals(request.getParameter("do"))) {
			strResult = delModuleInfo(request);
		} else if ("delmoduletype".equals(request.getParameter("do"))) {
			strResult = delModuleTypeInfo(request);
		}
		out.write(strResult);
		out.flush();
		out.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private String getModuleCount(HttpServletRequest request) throws ServletException {
		return String.valueOf(ModuleMngr.getModuleCount());
	}
	
	private String getModuleList(HttpServletRequest request) throws ServletException {

		List<Map<String, Object>> lstModuleInfo = ModuleMngr.getModuleList("0");
		if (lstModuleInfo == null) {
			return "";
		}
		StringBuilder sbHtml = new StringBuilder();
		Iterator<Map<String, Object>> iMap = lstModuleInfo.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th class=\"noborrig\">名称</th> ");
		sbHtml.append("<th width=\"80\" class=\"addbox\"></th> ");
		sbHtml.append("<th width=\"15%\">英文名称</th>");
		sbHtml.append("<th width=\"15%\">中文名称</th>");
		sbHtml.append("<th width=\"10%\">是否有效</th>");
		sbHtml.append("<th width=\"10%\">是否显示</th>");
		sbHtml.append("<th width=\"100\">操作</th>");
		sbHtml.append("</tr> ");
		int iNum = 1;
		while (iMap.hasNext()) {
			mapData = iMap.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(String.valueOf(iNum++)).append("</td>");
			sbHtml.append("<td class=\"noborrig\"><a href=\"ModuleInfo.do?md=").append(String.valueOf(mapData.get("id"))).append("\">").append(String.valueOf(mapData.get("tablename"))).append("</a></td>");
			sbHtml.append("<td class=\"addbox\"><a  class=\"adddata\" title=\"添加模块属性\" href=\"javascript:void(0);\" onclick=\"window.location.href='ModuleContentInfo.do?tabid=").append(String.valueOf(mapData.get("id"))).append("&tabname=\'+encodeURI('").append(String.valueOf(mapData.get("tablename_ch"))).append("') \" ></a><a href=\"javascript:void(0)\" onclick=\"window.location.href='ModuleTypeInfo.do?tabid=").append(String.valueOf(mapData.get("id"))).append("&tabname=\'+encodeURI('").append(String.valueOf(mapData.get("tablename_ch"))).append("') \" class=\"addnew\" title=\"添加子模块\"> </a></td>");
			sbHtml.append("<td>").append(String.valueOf(mapData.get("tablename_en"))).append("</td>");
			sbHtml.append("<td>").append(String.valueOf(mapData.get("tablename_ch"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append("1".equals(String.valueOf(mapData.get("status"))) ? "是" : "否").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append("1".equals(String.valueOf(mapData.get("isdisplay"))) ? "是" : "否").append("</td>");
			sbHtml.append("<td class=\"tabopt tableft\"><a href=\"ModuleInfo.do?md=").append(String.valueOf(mapData.get("id"))).append("\" class=\"edit\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delModuleInfo('").append(String.valueOf(mapData.get("id"))).append("')\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
			sbHtml.append(getSubModuleList(String.valueOf(mapData.get("id")), String.valueOf(mapData.get("tablename_ch"))));
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	/*
	 * 获取子模块列表
	 */
	private String getSubModuleList(String TableID, String TableName) {
		List<Map<String, Object>> lstModuleType = ModuleMngr.getModuleTypeList(TableID,"0");
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
			sbSubModule.append("<td class=\"submod\" colspan=\"2\"><a href=\"javascript:void(0)\" onclick=\"window.location.href='ModuleTypeInfo.do?tabid=").append(TableID).append("&tabname=\'+encodeURI('").append(TableName).append("') +'&id=").append(mapData.get("id")).append("'\">").append(mapData.get("typename")).append("</a></td>");
			sbSubModule.append("<td>").append(mapData.get("typename_en")).append("</td>");
			sbSubModule.append("<td>").append(mapData.get("typename_ch")).append("</td>");
			sbSubModule.append("<td class=\"tabcent\">").append("1".equals(String.valueOf(mapData.get("status"))) ? "是" : "否").append("</td>");
			sbSubModule.append("<td class=\"tabcent\">&nbsp;</td>");
			sbSubModule.append("<td class=\"tabopt tableft\"><a href=\"ModuleTypeInfo.do?tabid=").append(TableID).append("&tabname=").append(TableName).append("&id=").append(mapData.get("id")).append("\" class=\"edit\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delModuleTypeInfo(").append(mapData.get("id")).append(",").append(TableID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbSubModule.append("</tr>");
		}
		return sbSubModule.toString();
	}

	private String checkModuleData(HttpServletRequest request) throws ServletException {
		String strTableID = request.getParameter("tabid");
		if (ModuleMngr.IsHasData(strTableID)) {
			return "1";
		} else {
			return "0";
		}

	}

	private String delModuleInfo(HttpServletRequest request) throws ServletException {
		String strTableID = request.getParameter("tabid");
		if (strTableID == null) {
			return "2"; // 参数错误
		}
		if (ModuleMngr.deleteModuleInfo(strTableID)) {
			return "1";
		} else {
			return "-1"; // 删除失败
		}
	}

	private String checkModuleTypeData(HttpServletRequest request) throws ServletException {
		String strTypeID = request.getParameter("typeid");
		String strTableID = request.getParameter("tabid");

		if (ModuleMngr.existsModuleTypeData(strTypeID, strTableID)) {
			return "1";
		} else {
			return "0";
		}
	}

	private String delModuleTypeInfo(HttpServletRequest request) throws ServletException, IOException {
		String strTypeID = request.getParameter("typeid");
		if (strTypeID == null) {
			return "2";
		}
		if (ModuleMngr.deleteModuleTypeInfo(strTypeID)) {
			return "1";
		} else {
			return "-1";
		}

	}
}
