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
import Model.ModuleContentInfoBean;
import Util.Common;

/**
 * Servlet implementation class UserModuleContentListHandlerServlet
 */
@WebServlet("/UserModuleContentListHandler.do")
public class UserModuleContentListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserModuleContentListHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getUserModuleContentCount(request);
		}else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getUserModuleContentList(request);
		}else if("delinfo".equals(request.getParameter("do"))){
			strResult=delModuleMessage(request);
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

	private String getUserModuleContentCount(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String strModuleID = request.getParameter("mid");
		String strModuleTypeID = request.getParameter("tid");
		String username = request.getParameter("username");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		String strTableName = ModuleMngr.getTableName(Integer.parseInt(strModuleID));
		if (!ModuleMngr.existsTable(strTableName)) {
			return "-1";
		}
		return String.valueOf(ModuleMngr.getUserModuleContentCount(strModuleID, strModuleTypeID,username, strStartDate, strEndDate));

	}

	private String getUserModuleContentList(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String strModuleID = request.getParameter("mid");
		String strModuleTypeID = request.getParameter("tid");
		String username = request.getParameter("username");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");

		List<ModuleContentInfoBean> lstField = ModuleMngr.getDisplayModuleContentByTabID(strModuleID);
		if (lstField == null) {
			return "";
		}

		//int allLength = ModuleMngr.getSumFieldLength(strModuleID);
		StringBuilder sbField = new StringBuilder();

		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<table width=\"100%\" name=\"tabUserContent\" id=\"tabUserContent\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		
		sbField.append("id,");
		for (ModuleContentInfoBean moduleContentInfo : lstField) {
			sbField.append(moduleContentInfo.getFieldName()).append(",");
			if ("time".equals(moduleContentInfo.getFieldName())) {
				continue;
				//sbHtml.append("<th width=\"155px\">").append(moduleContentInfo.getFieldName_CH()).append("</th>");
			} else {
				sbHtml.append("<th>").append(moduleContentInfo.getFieldName_CH()).append("</th>");
			}
		}
		sbHtml.append("<th width=\"155px\">更新时间</th>");
		sbHtml.append("<th width=\"95px\">操作</th>");
		sbHtml.append("</tr>");
		if (sbField.length() > 0) {
			sbField.delete(sbField.length() - 1, sbField.length());
		}
		String strField = sbField.toString();
		String[] arrFields = strField.split(",");
		List<Map<String, Object>> lstUserModuleContent = ModuleMngr.getUserModuleContentList(strField, strModuleID, strModuleTypeID,username, strStartDate, strEndDate, iStart, iLength);
		if (lstUserModuleContent == null) {
			sbHtml.append("<tr>");
			sbHtml.append("<td colspan=\" class=\"tabcent\"").append(arrFields.length+3).append("\">暂无数据</td>");
			sbHtml.append("</tr>");
			sbHtml.append("</table>");
			return sbHtml.toString();
		}
		Iterator<Map<String, Object>> iUserModule = lstUserModuleContent.iterator();
		Map<String, Object> mapUserModule = null;

		while (iUserModule.hasNext()) {
			mapUserModule = iUserModule.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chkucid\" value=\"").append(String.valueOf(mapUserModule.get("id"))).append(":").append(mapUserModule.get("tablename")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iStart++).append("</td>");
			for (int i = 0; i < arrFields.length; i++) {
				String strTempField=arrFields[i];
				if("id".equals(strTempField)||"time".equals(strTempField)){
					continue;
				}
				sbHtml.append("<td class=\"tabcent\">");
				//if ("time".equals(strTempField)) {
					//sbHtml.append(Common.ConvertToDateTime(String.valueOf(mapUserModule.get(strTempField))));
				//} else {
					String strTempVal=mapUserModule.get(strTempField)==null?"":String.valueOf(mapUserModule.get(strTempField));
					sbHtml.append("<span title='").append(strTempVal).append("'>").append(strTempVal.length()>30?strTempVal.substring(0, 30).concat("..."):strTempVal).append("</span>");
				//}
				sbHtml.append("</td>");
			}
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapUserModule.get("time")))).append("</td>");
			if(strModuleTypeID==null){
				sbHtml.append("<td><a href=\"javascript:void(0);\" onclick=\"delModuleMessage(").append(String.valueOf(mapUserModule.get("id"))).append(",").append("'").append(mapUserModule.get("tablename")).append("'").append(");\" class=\"del\" title=\"删除\"> </a><a href=\"UserModuleContentDetail.do?rid=").append(mapUserModule.get("id")).append("&tablename=").append(mapUserModule.get("tablename")).append("&mid=").append(strModuleID).append("\" class=\"view\" title=\"查看\"></a></td>");
			}else{
				sbHtml.append("<td><a href=\"javascript:void(0);\" onclick=\"delModuleMessage(").append(String.valueOf(mapUserModule.get("id"))).append(",").append("'").append(mapUserModule.get("tablename")).append("'").append(");\" class=\"del\" title=\"删除\"> </a><a href=\"UserModuleContentDetail.do?rid=").append(mapUserModule.get("id")).append("&tablename=").append(mapUserModule.get("tablename")).append("&mid=").append(strModuleID).append("&tid=").append(strModuleTypeID).append("\" class=\"view\" title=\"查看\"></a></td>");
			}
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();

	}
	
	public static String delModuleMessage(HttpServletRequest request) throws ServletException, IOException {
//		String strModuleID = request.getParameter("mid");
//		String strModuleTypeID = request.getParameter("tid");
		String strRecdId=request.getParameter("rid");
		String tablename = request.getParameter("tablename");
		if(Common.IsNullOrEmpty(strRecdId)||Common.IsNullOrEmpty(tablename)){
			return "0";
		}
//		strRecdId=Common.Trim(strRecdId, ",");
		if(ModuleMngr.delModuleMessage(strRecdId, tablename)){
			return "1";
		}else{
			return "0";
		}
	}

}
