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

import Model.UserLoginBean;
import Util.Common;
//import BLL.ModuleMngr;
import BLL.UserInfoMngr;

/**
 * Servlet implementation class LoginLogDetailHandlerServlet
 */
@WebServlet("/LoginLogDetailHandler.do")
public class LoginLogDetailHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginLogDetailHandlerServlet() {
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
			strResult = getUserLoginLogCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getUserLoginLog(request);
		} else if ("dellog".equals(request.getParameter("do"))) {
			strResult = delUserLoginLog(request);
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

	private String getUserLoginLogCount(HttpServletRequest request) throws ServletException, IOException {
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}
		String strUname = request.getParameter("uname");
		if (Common.IsNullOrEmpty(strUname)) {
			return "-1";
		}
//		String strTerminalID = request.getParameter("tmal");
//		String strTerminal = null;
//		if (strTerminalID != null) {
//			if (strTerminalID.endsWith(",")) {
//				strTerminalID = Common.Trim(strTerminalID, ",");
//			}
//			//strTerminal = getTerminalName(strTerminalID);
//		}
		String strTerminal = request.getParameter("tmal");
		if (strTerminal != null && strTerminal.endsWith(",")) {
			strTerminal = Common.Trim(strTerminal, ",");
		}
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		return String.valueOf(UserInfoMngr.getUserLoginCountByUserID(appid, strUname, strTerminal, strStartDate, strEndDate));
	}

	private String getUserLoginLog(HttpServletRequest request) throws ServletException, IOException {
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}
		StringBuilder sbHtml = new StringBuilder();
		String strUname = request.getParameter("uname");
		if (Common.IsNullOrEmpty(strUname)) {
			return "-1";
		}
//		String strTerminalID = request.getParameter("tmal");
//		String strTerminal = null;
//		if (strTerminalID != null) {
//			if (strTerminalID.endsWith(",")) {
//				strTerminalID = Common.Trim(strTerminalID, ",");
//			}
//			//strTerminal = getTerminalName(strTerminalID);
//		}

		String strTerminal = request.getParameter("tmal");
		if (strTerminal != null && strTerminal.endsWith(",")) {
			strTerminal = Common.Trim(strTerminal, ",");
		}
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstUserLoginLog = null;
		lstUserLoginLog = UserInfoMngr.getUserLoginListByUserID(appid, strUname, strTerminal, strStartDate, strEndDate, iStart, iLength);
		if (lstUserLoginLog == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" id=\"tablogs\" name=\"tablogs\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th  width=\"18%\">用户名</th>");
		sbHtml.append("<th>设备型号</th>");
		sbHtml.append("<th width=\"100\">AppId</th>");
		sbHtml.append("<th width=\"80\">App版本</th>");
		sbHtml.append("<th width=\"15%\">IP地址</th>");
		sbHtml.append("<th width=\"14%\">登录时间</th>");
		sbHtml.append("<th width=\"12%\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iUser = lstUserLoginLog.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;
		while (iUser.hasNext()) {
			iMap = iUser.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chklogid\" value=\"").append(iMap.get("id")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td>").append(iMap.get("client") == null ? "&nbsp" : String.valueOf(iMap.get("client"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("appid")==null?"&nbsp":String.valueOf(iMap.get("appid"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("version") == null ? "&nbsp" : String.valueOf(iMap.get("version"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("address") == null ? "&nbsp" : String.valueOf(iMap.get("address"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delSingleLog(").append(iMap.get("id")).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		// sbHtml.append("<tr class=\"tabtotopt\">");
		// sbHtml.append("<td><input name=\"selectAll\" onclick=\"checkall('tablogs',this)\" type=\"checkbox\" value=\"\" /></td>");
		// sbHtml.append("<td colspan=\"5\"><a href=\"javascript:void(0);\" onclick=\"delMultiLog()\" class=\"fleft delall\">删除</a></td>");
		// sbHtml.append("</tr>");
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private String delUserLoginLog(HttpServletRequest request) throws ServletException, IOException {
		String strResult = "";
		String strLogID = request.getParameter("lid");
		String strUserName = request.getParameter("uname");
		if (strLogID == null || Common.Trim(strLogID, " ").length() == 0) {
			return "0";
		}
		if (strLogID.endsWith(",")) {
			strLogID = Common.Trim(strLogID, ",");
		}

		if (UserInfoMngr.delUserLoginLog(strLogID, strUserName)) {
			strResult = "1";
		} else {
			strResult = "0";
		}
		return strResult;
	}

//	private String getTerminalName(String TerminalID) {
//		List<Map<String, Object>> lstTerminal = ModuleMngr.getOperatorSystemName(TerminalID);
//		if (lstTerminal == null) {
//			return null;
//		}
//		StringBuilder sbTerminal = new StringBuilder();
//		for (Map<String, Object> map : lstTerminal) {
//			sbTerminal.append(String.valueOf(map.get("osname"))).append(",");
//		}
//		if (sbTerminal.length() > 0) {
//			sbTerminal.delete(sbTerminal.length() - 1, sbTerminal.length());
//		}
//		return sbTerminal.toString();
//	}

}
