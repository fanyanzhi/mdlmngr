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

import BLL.ClaimMngr;
import Util.Common;

/**
 * Servlet implementation class UserLoginListHanderServlet
 */
@WebServlet("/ClaimListHander.do")
public class ClaimListHanderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ClaimListHanderServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getAppealCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getAppealList(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private String getAppealCount(HttpServletRequest request) throws ServletException, IOException {
		String strStartDate = request.getParameter("txtStartDate");
		String strEndDate = request.getParameter("txtEndDate");// yyyy-MM-dd
		String username = request.getParameter("username");
		return String.valueOf(ClaimMngr.getClaimCount(username, strStartDate, strEndDate));
	}

	private String getAppealList(HttpServletRequest request) throws ServletException, IOException {

		StringBuilder sbHtml = new StringBuilder();
		String txtStartTime = request.getParameter("txtStartDate");
		String txtEndTime = request.getParameter("txtEndDate");
		String username = request.getParameter("username");

		int start = Integer.parseInt(request.getParameter("start"));
		int length = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstUserLoginLog = null;
		lstUserLoginLog = ClaimMngr.getClaimList(username, txtStartTime, txtEndTime, start, length);

		sbHtml.append(
				"<table width=\"100%\" id=\"appealList\" name=\"appealList\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th class=\"num\" style=\"width:50px\"></th>");
		sbHtml.append("<th>用户名</th>");
		sbHtml.append("<th>姓名</th>");
		sbHtml.append("<th>工作单位</th>");
		sbHtml.append("<th>联系电话</th>");
		sbHtml.append("<th>联系邮箱</th>");
		sbHtml.append("<th>身份证号</th>");
		sbHtml.append("<th style=\"width:140px\">认领时间</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iUser = lstUserLoginLog.iterator();
		Map<String, Object> iMap = null;
		int iNum = start;
		while (iUser.hasNext()) {
			iMap = iUser.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++);
			sbHtml.append("<input type=\"hidden\" name=\"id\" id=\"id\" value=\"").append(iMap.get("id"));
			sbHtml.append("\" />");
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("username") == null ? "" : iMap.get("username"));
			sbHtml.append("</td>");
			
			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("realname") == null ? "" : iMap.get("realname"));
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("workunit") == null ? "" : iMap.get("workunit"));
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("phone") == null ? "" : iMap.get("phone"));
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("email") == null ? "" : iMap.get("email"));
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("cardnum") == null ? "" : String.valueOf(iMap.get("cardnum")));
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("time") == null ? ""
					: Common.ConvertToDateTime(String.valueOf(iMap.get("time"))));
			sbHtml.append("</td>");

			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

}
