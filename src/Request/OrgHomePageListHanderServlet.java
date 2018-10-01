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

import BLL.OrgHomePageMngr;
import Util.Common;

/**
 * Servlet implementation class UserLoginListHanderServlet
 */
@WebServlet("/OrgHomePageListHander.do")
public class OrgHomePageListHanderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrgHomePageListHanderServlet() {
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
			strResult = getOrgHomePageCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getOrgHomePageList(request);
		} else if ("deleteOpt".equals(request.getParameter("do"))) {
			strResult = delete(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	private String delete(HttpServletRequest request) {
		String strResult = "";
		String id = request.getParameter("id");
		if (Common.IsNullOrEmpty(id) || Common.IsNullOrEmpty(id)) {
			return "0";
		}
		if (OrgHomePageMngr.delete(id)) {
			strResult = "1";
		} else {
			strResult = "0";
		}
		return strResult;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private String getOrgHomePageCount(HttpServletRequest request) throws ServletException, IOException {
		String strStartDate = request.getParameter("txtStartDate");
		String strEndDate = request.getParameter("txtEndDate");// yyyy-MM-dd
		String txtOrg = request.getParameter("txtOrg");
		return String.valueOf(OrgHomePageMngr.getOrgHomePageCount(txtOrg, strStartDate, strEndDate));
	}

	private String getOrgHomePageList(HttpServletRequest request) throws ServletException, IOException {

		StringBuilder sbHtml = new StringBuilder();
		String txtStartTime = request.getParameter("txtStartDate");
		String txtEndTime = request.getParameter("txtEndDate");
		String txtOrg = request.getParameter("txtOrg");

		int start = Integer.parseInt(request.getParameter("start"));
		int length = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstUserLoginLog = null;
		lstUserLoginLog = OrgHomePageMngr.getOrgHomePageList(txtOrg, txtStartTime, txtEndTime, start, length);

		sbHtml.append(
				"<table width=\"100%\" id=\"orgHomePageList\" name=\"orgHomePageList\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th class=\"num\" style=\"width:50px\"></th>");
		sbHtml.append("<th>机构名称</th>");
		sbHtml.append("<th>机构用户名</th>");
		sbHtml.append("<th>首页网址</th>");
		sbHtml.append("<th style=\"width:140px\">时间</th>");
		sbHtml.append("<th style=\"width:100px\">操作</th>");
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
			sbHtml.append(iMap.get("unitname") == null ? "" : iMap.get("unitname"));
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("orgname") == null ? "" : iMap.get("orgname"));
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("weburl") == null ? "" : iMap.get("weburl"));
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabcent\">");
			sbHtml.append(iMap.get("time") == null ? "" : Common.ConvertToDateTime(String.valueOf(iMap.get("time"))));
			sbHtml.append("</td>");

			sbHtml.append("<td class=\"tabopt\">");
			sbHtml.append("<a class=\"edit\" title=\"修改\" href=\"javascript:void(0);\" ");
			sbHtml.append("onclick=\"");
			sbHtml.append("window.location.href='OrgHomePageEdit.do?id=").append(iMap.get("id")).append("'");
			sbHtml.append("\"");
			sbHtml.append(">");
			sbHtml.append("</a>");
			// 删除
			sbHtml.append("<a class=\"del\" title=\"删除\" href=\"javascript:void(0);\" ");
			sbHtml.append("onclick=\"deleteOpt('").append(iMap.get("id")).append("')\"");
			sbHtml.append(">");
			sbHtml.append("</a>");
			sbHtml.append("</td>");

			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

}
