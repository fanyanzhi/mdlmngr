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

import BLL.VersionMngr;
import Util.Common;

/**
 * Servlet implementation class VersionListHandlerServlet
 */
@WebServlet("/VersionListHandler.do")
public class VersionListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VersionListHandlerServlet() {
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
			strResult = getVersionList(request);
		} else if ("del".equals(request.getParameter("do"))) {
			strResult = delVersion(request);
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

	private String getVersionList(HttpServletRequest request) {

		List<Map<String, Object>> lstVersionInfo = VersionMngr.getForceVersionList();
		if (lstVersionInfo == null) {
			return "";
		}
		Iterator<Map<String, Object>> iMap = lstVersionInfo.iterator();
		Map<String, Object> mapData = null;
		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<table width=\"100%\" id=\"tabsysinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"100\">系统类别</th>");
		sbHtml.append("<th width=\"100\">最低版本</th>");
		sbHtml.append("<th width=\"100\">版本名称</th>");
		sbHtml.append("<th>下载地址</th>");
		sbHtml.append("<th>修改时间</th>");
		sbHtml.append("<th width=\"100\">操作</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		while (iMap.hasNext()) {
			mapData = iMap.next();
			String strVerID = String.valueOf(mapData.get("id"));
			String strClient=String.valueOf(mapData.get("client"));
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"window.location.href='VersionInfo.do?client=").append(strClient).append("'\" title=\"编辑\">").append(strClient).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(mapData.get("version"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("versionname")==null?"":String.valueOf(mapData.get("versionname"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("apkurl")==null?"":String.valueOf(mapData.get("apkurl"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='VersionInfo.do?client=").append(strClient).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delVersionInfo(").append(strVerID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private String delVersion(HttpServletRequest request) {
		String vid = request.getParameter("vid");
		if (Common.IsNullOrEmpty(vid)) {
			return "0";
		}
		if (VersionMngr.deleteForceVersion(Integer.parseInt(vid))) {
			return "1";
		} else {
			return "0";
		}
	}
}
