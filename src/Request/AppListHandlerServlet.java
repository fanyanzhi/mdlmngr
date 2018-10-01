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

import BLL.AppInfoMngr;

/**
 * Servlet implementation class AppListHandlerServlet
 */
@WebServlet("/AppListHandler.do")
public class AppListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppListHandlerServlet() {
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
			strResult = getAppCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getAppList(request);
		} else if ("del".equals(request.getParameter("do"))) {
			strResult = delAppInfo(request);
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

	private String getAppCount(HttpServletRequest request) {
		String appId = request.getParameter("appid");
		return String.valueOf(AppInfoMngr.getAppInfoCount(appId));
	}

	private String getAppList(HttpServletRequest request) {
		StringBuilder sbHtml = new StringBuilder();
		String appId = request.getParameter("appid");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstAppInfo = null;
		lstAppInfo = AppInfoMngr.getAppInfoList(appId, iStart, iLength);
		if (lstAppInfo == null) {
			return "";
		}
		Iterator<Map<String, Object>> iAppInfo = lstAppInfo.iterator();
		Map<String, Object> iMapAppInfo = null;

		sbHtml.append("<table width=\"100%\" id=\"tabusers\" name=\"tabusers\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40px\"  class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"15%\">AppId</th>");
		sbHtml.append("<th>AppKey</th>");
		sbHtml.append("<th>是否付费</th>");
		sbHtml.append("<th>是否同步</th>");
		sbHtml.append("<th>是否验证</th>");
		sbHtml.append("<th>参与活动</th>");
		sbHtml.append("<th>是否有效</th>");
		sbHtml.append("<th width=\"15%\">备注</th>");
		sbHtml.append("<th width=\"10%\">修改日期</th>");
		sbHtml.append("<th width=\"10%\">创建时间</th>");
		sbHtml.append("<th width=\"10%\">操作</th>");
		sbHtml.append("</tr>");
		int iNum = iStart;
		while (iAppInfo.hasNext()) {
			iMapAppInfo = iAppInfo.next();
			String comment = iMapAppInfo.get("comment") == null ? "" : String.valueOf(iMapAppInfo.get("comment"));
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMapAppInfo.get("appid")).append("</td>");
			sbHtml.append("<td>").append(iMapAppInfo.get("appkey")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(iMapAppInfo.get("isfee")).equals("1") ? "付费" : "免费").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(iMapAppInfo.get("sync")).equals("1") ? "同步" : "不同步").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(iMapAppInfo.get("auth")).equals("1") ? "验证" : "不验证").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(iMapAppInfo.get("activity")).equals("1") ? "参与" : "不参与").append("</td>");
			
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(iMapAppInfo.get("status")).equals("1") ? "启用" : "禁用").append("</td>");
			sbHtml.append("<td class=\"tabcent\"><span title='").append(comment).append("'>").append(comment.length() > 20 ? comment.substring(0, 19) : comment).append("</span></td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMapAppInfo.get("updatetime")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMapAppInfo.get("time")).append("</td>");
			sbHtml.append("<td class=\"tabopt bordblack\"><a href=\"AppInfo.do?aid=").append(String.valueOf(iMapAppInfo.get("id"))).append("\" class=\"edit\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delappinfo('").append(String.valueOf(iMapAppInfo.get("id"))).append("')\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private String delAppInfo(HttpServletRequest request) {
		String result = "";
		String appId = request.getParameter("aid");
		if (appId == null || Common.Trim(appId, " ").length() == 0) {
			return "0";
		}
		if (appId.endsWith(",")) {
			appId = Common.Trim(appId, ",");
		}

		if (AppInfoMngr.delAppInfo(appId)) {
			result = "1";
		} else {
			result = "0";
		}
		return result;
	}

}
