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

import Util.Common;

//import BLL.ModuleMngr;
import BLL.UserInfoMngr;

/**
 * Servlet implementation class StatisticanListHandlerServlet
 */
@WebServlet("/StatisticanListHandler.do")
public class StatisticanListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StatisticanListHandlerServlet() {
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
			strResult = getStatisticanCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getStatisticanList(request);
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

	protected String getStatisticanCount(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String strUserID = request.getParameter("uname");
		if (strUserID != null) {
			if (strUserID.endsWith(",")) {
				strUserID = Common.Trim(strUserID, ",");
			}
			strUserID = "'".concat(strUserID).concat("'").replace(",", "','");
		}
		String strTerminal = request.getParameter("tmal");
		/*String strTerminal=null;
		if (strTerminalID != null) {
			if (strTerminalID.endsWith(",")) {
				strTerminalID = Common.Trim(strTerminalID, ",");
			}
			strTerminal=getTerminalName(strTerminalID);
		}*/
		String strSingleTer = request.getParameter("singleTer");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		return String.valueOf(UserInfoMngr.getLastLoginCount(strUserID, strTerminal, strStartDate, strEndDate,strSingleTer));
	}

	protected String getStatisticanList(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		StringBuilder sbHtml = new StringBuilder();
		List<Map<String, Object>> lst = null;
		String strUserID = request.getParameter("uname");
		if (strUserID != null) {
			if (strUserID.endsWith(",")) {
				strUserID = Common.Trim(strUserID, ",");
			}
			strUserID = "'".concat(strUserID).concat("'").replace(",", "','");
		}
		String strTerminal = request.getParameter("tmal");
		/*String strTerminal=null;
		if (strTerminalID != null) {
			if (strTerminalID.endsWith(",")) {
				strTerminalID = Common.Trim(strTerminalID, ",");
			}
			strTerminal=getTerminalName(strTerminalID);
		}*/

		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		String strSingleTer = request.getParameter("singleTer");
		String strOrderType = request.getParameter("ordtype");
		String strOrder = request.getParameter("order");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));
		// if (strUserID == null) {
		// return "-1";
		// }
		if (strOrderType == null) {
			strOrderType = "0"; // 0为时间,1为
		}
		if (strOrder == null) {
			strOrder = "0"; // 0为正序，1为倒序
		}
		StringBuilder sbParam = new StringBuilder();
		if (strStartDate != null) {
			sbParam.append("&startdate=").append(strStartDate);
		}
		if (strEndDate != null) {
			sbParam.append("&enddate=").append(strEndDate);
		}
		if (strTerminal != null) {
			sbParam.append("&tmal=").append(strTerminal);
		}
		if ("0".equals(strOrderType)) {
			lst = UserInfoMngr.getLastLoginListOrderByTime(strUserID, strTerminal, strStartDate, strEndDate,strSingleTer, Integer.parseInt(strOrder), iStart, iLength);
		} else {
			lst = UserInfoMngr.getLastLoginListOrderByCount(strUserID, strTerminal, strStartDate, strEndDate,strSingleTer, Integer.parseInt(strOrder), iStart, iLength);
		}
		if (lst == null) {
			return "-1";
		}
		int allLgCount = UserInfoMngr.getSumLoginCount(strUserID, strTerminal, strStartDate, strEndDate,strSingleTer);
		Iterator<Map<String, Object>> iList = lst.iterator();
		Map<String, Object> iMap = null;
		sbHtml.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40px\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"15%\">用户名</th>");
		sbHtml.append("<th width=\"15%\">上次登录时间</th>");
		sbHtml.append("<th>使用设备</th>");
		sbHtml.append("<th width=\"15%\">IP地址</th>");
		sbHtml.append("<th width=\"10%\">登录次数</th>");
		sbHtml.append("<th width=\"10%\">查看详细</th>");
		sbHtml.append("</tr>");
		// int iNum = 1;// username,`client`,`address`,`time`,`userid`,COUNT(*)
		// as
		// logincount
		while (iList.hasNext()) {
			iMap = iList.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iStart++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("time")).append("</td>");
			sbHtml.append("<td>").append(iMap.get("client")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("address")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("logincount")).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"LoginLogDetail.do?uid=").append(iMap.get("userid")).append("&uname=").append(iMap.get("username")).append(sbParam.toString()).append("\" class=\"view\" title=\"查看\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		sbHtml.append("<input type=\"hidden\" id=\"alllogincount\" name=\"alllogincount\" value=\"").append(String.valueOf(allLgCount)).append("\" />");
		return sbHtml.toString();

	}

	/*private String getTerminalName(String TerminalID) {
		List<Map<String, Object>> lstTerminal = ModuleMngr.getOperatorSystemName(TerminalID);
		if (lstTerminal == null) {
			return null;
		}
		StringBuilder sbTerminal = new StringBuilder();
		for (Map<String, Object> map : lstTerminal) {
			sbTerminal.append(String.valueOf(map.get("osname"))).append(",");
		}
		if (sbTerminal.length() > 0) {
			sbTerminal.delete(sbTerminal.length() - 1, sbTerminal.length());
		}
		return sbTerminal.toString();
	}*/

}
