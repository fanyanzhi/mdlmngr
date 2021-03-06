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

import BLL.UserInfoMngr;
import Util.Common;

/**
 * Servlet implementation class TerminalNumListHandlerServlet
 */
@WebServlet("/TerminalNumListHandler.do")
public class TerminalNumListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TerminalNumListHandlerServlet() {
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
		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getTerminalNumList(request);
		} else if ("del".equals(request.getParameter("do"))) {
			strResult = delTerminalNum(request);
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

	private String getTerminalNumList(HttpServletRequest request) {

		List<Map<String, Object>> lstTerminalNum = UserInfoMngr.getMaxOnlineCountList();
		if (lstTerminalNum == null) {
			return "";
		}
		Iterator<Map<String, Object>> iMap = lstTerminalNum.iterator();
		Map<String, Object> mapData = null;
		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<table width=\"100%\" id=\"tabsysinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"200\">系统类别</th>");
		sbHtml.append("<th>终端数量</th>");
		sbHtml.append("<th>修改时间</th>");
		sbHtml.append("<th width=\"100\">操作</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		while (iMap.hasNext()) {
			mapData = iMap.next();
			String strTermalID = String.valueOf(mapData.get("id"));
			String strClient = String.valueOf(mapData.get("client"));
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"window.location.href='TerminalNumInfo.do?tid=").append(strTermalID).append("'\" title=\"编辑\">").append(strClient).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(mapData.get("onlinecount"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='TerminalNumInfo.do?tid=").append(strTermalID).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delTerminalNum(").append(strTermalID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private String delTerminalNum(HttpServletRequest request) {
		String strTeminalID = request.getParameter("tid");
		if (Common.IsNullOrEmpty(strTeminalID)) {
			return "0";
		}
		if (UserInfoMngr.deleteMaxOnlineCount(Integer.parseInt(strTeminalID))) {
			return "1";
		} else {
			return "0";
		}
	}

}
