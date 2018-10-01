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

import BLL.SourceMngr;

/**
 * Servlet implementation class SourceTypeListHandlerServlet
 */
@WebServlet("/SourceTypeListHandler.do")
public class SourceTypeListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SourceTypeListHandlerServlet() {
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
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";

		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSourceTypeList(request);
		} else if ("deltype".equals(request.getParameter("do"))) {
			strResult = delSourceType(request);
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

	protected String getSourceTypeList(HttpServletRequest request) throws ServletException, IOException {

		List<Map<String, Object>> lstSourceType = SourceMngr.getSourceType();
		if (lstSourceType == null) {
			return "";
		}
		Iterator<Map<String, Object>> iMap = lstSourceType.iterator();
		Map<String, Object> mapData = null;
		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"100\">分类名称</th>");
		sbHtml.append("<th width=\"34%\">搜索属性</th>");
		sbHtml.append("<th>显示属性</th>");
		sbHtml.append("<th width=\"100\"> 操作</th>");
		sbHtml.append(" </tr>");
		int iNum = 1;

		while (iMap.hasNext()) {
			mapData = iMap.next();
			String strTypeID = String.valueOf(mapData.get("id"));
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(mapData.get("name_ch"))).append("</td>");
			sbHtml.append("<td> ").append(getSearchFieldHtml(strTypeID)).append("</td>");
			sbHtml.append("<td> ").append(getDisplayFieldHtml(strTypeID)).append("</td>");
			sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='SourceTypeInfo.do?stid=").append(strTypeID).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delSourceType(").append(strTypeID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	protected String delSourceType(HttpServletRequest request) throws ServletException, IOException {
		String strTypeID = request.getParameter("tid");
		if (Common.IsNullOrEmpty(strTypeID)) {
			return "0";
		}
		if (SourceMngr.delSourceType(strTypeID)) {
			return "1";
		} else {
			return "0";
		}
	}

	private String getSearchFieldHtml(String TypeID) {
		List<Map<String, Object>> lstSearchField = SourceMngr.getSearchField(TypeID);
		if (lstSearchField == null) {
			return "&nbsp;";
		}
		StringBuilder sbSechFieldHtml = new StringBuilder();
		Iterator<Map<String, Object>> iterator = lstSearchField.iterator();
		Map<String, Object> imap = null;
		while (iterator.hasNext()) {
			imap = iterator.next();
			sbSechFieldHtml.append("<dfn>");
			sbSechFieldHtml.append(String.valueOf(imap.get("name_ch")));
			sbSechFieldHtml.append("</dfn>");
		}
		return sbSechFieldHtml.toString();
	}

	private String getDisplayFieldHtml(String TypeID) {
		List<Map<String, Object>> lstDisplayField = SourceMngr.getDisplayField(TypeID);
		if (lstDisplayField == null) {
			return "&nbsp;";
		}
		StringBuilder sbDisplayFieldHtml = new StringBuilder();
		Iterator<Map<String, Object>> iterator = lstDisplayField.iterator();
		Map<String, Object> imap = null;
		while (iterator.hasNext()) {
			imap = iterator.next();
			sbDisplayFieldHtml.append("<dfn>");
			sbDisplayFieldHtml.append(String.valueOf(imap.get("name_ch")));
			sbDisplayFieldHtml.append("</dfn>");
		}
		return sbDisplayFieldHtml.toString();
	}

}
