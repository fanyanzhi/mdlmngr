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
import BLL.CnkiMngr;

/**
 * Servlet implementation class ErrorCnkiFileHandlerServlet
 */
@WebServlet("/ErrorCnkiFileHandler.do")
public class ErrorCnkiFileHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ErrorCnkiFileHandlerServlet() {
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
			strResult = getErrorFileCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getErrorFileList(request);
		} else if ("delerrorfile".equals(request.getParameter("do"))) {
			strResult = delErrorFile(request);
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

	private String getErrorFileCount(HttpServletRequest request) {
		String strTypeID = request.getParameter("tid");
		String strFileID = request.getParameter("kw");
		String strErrorType = request.getParameter("et");
		return String.valueOf(CnkiMngr.getErrorFileCount(strTypeID, strFileID, strErrorType));
	}

	private String getErrorFileList(HttpServletRequest request) {
		StringBuilder sbHtml = new StringBuilder();
		String strTypeID = request.getParameter("tid");
		String strFileID = request.getParameter("kw");
		String strErrorType = request.getParameter("et");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));
		List<Map<String, Object>> lstCnkiErrorFile = CnkiMngr.getErrorFileList(strTypeID, strFileID, strErrorType, iStart, iLength);
		if (lstCnkiErrorFile == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" id=\"taberrorfile\" name=\"taberrorfile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"20%\">用户名</th>");
		sbHtml.append("<th>文件ID</th>");
		sbHtml.append("<th width=\"15%\">文件类型</th>");
		sbHtml.append("<th width=\"10%\">下载时间</th>");
		sbHtml.append("<th width=\"10%\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iErrorFile = lstCnkiErrorFile.iterator();
		Map<String, Object> iMap = null;
		int iNum = 1;// id, username, typeid, fileid, time
		while (iErrorFile.hasNext()) {
			iMap = iErrorFile.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chkerrorid\" value=\"").append(iMap.get("id")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("fileid")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(iMap.get("typeid"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delErrorFile(").append(iMap.get("id")).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private String delErrorFile(HttpServletRequest request) {
		String strID = request.getParameter("efid");
		if (strID == null) {
			return "0";
		}
		strID = Common.Trim(strID, ",");
		if (CnkiMngr.delErrorFile(strID)) {
			return "1";
		} else {
			return "0";
		}
	}
}
