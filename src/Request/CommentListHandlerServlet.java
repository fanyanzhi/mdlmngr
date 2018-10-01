package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.CommentMngr;
import Util.Common;

/**
 * Servlet implementation class CommentListHandlerServlet
 */
@WebServlet("/CommentListHandler.do")
public class CommentListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommentListHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("getcount".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = getCommnetCount(request);
		} else if ("getlist".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = getCommnetList(request);
		} else if ("delcomment".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = delComment(request);
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

	protected String getCommnetCount(HttpServletRequest request) throws ServletException, IOException {
		String strUserName = request.getParameter("un");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		String strDocuName = request.getParameter("dn");
		String strContent = request.getParameter("tc");
		String strIsDisplay = request.getParameter("ip");
		return String.valueOf(CommentMngr.getCommentCount(strUserName, strStartDate, strEndDate, strDocuName, strContent, strIsDisplay));
	}

	protected String getCommnetList(HttpServletRequest request) throws ServletException, IOException {
		String strUserName = request.getParameter("un");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		String strDocuName = request.getParameter("dn");
		String strContent = request.getParameter("tc");
		String strIsDisplay = request.getParameter("ip");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstComment = CommentMngr.getCommentList(strUserName, strStartDate, strEndDate, strDocuName, strContent, strIsDisplay, iStart, iLength);
		if (lstComment == null) {
			return "";
		}
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<div id=\"divcomment\" >");
		Iterator<Map<String, Object>> iMap = lstComment.iterator();

		Map<String, Object> mapData = null;
		int iNum = iStart;
		while (iMap.hasNext()) {
			mapData = iMap.next();
			String strTempContent = Common.transform((String.valueOf(mapData.get("content"))));
			sbHtml.append("<ul class=\"comment\">");
			sbHtml.append("<li class=\"finetit\"><input name=\"chkcommentid\" value=\"").append(mapData.get("id")).append(",").append(mapData.get("tablename")).append("\" type=\"checkbox\" class=\"finecheck\"/> <ins class=\"num\">").append(String.valueOf(iNum++)).append(".</ins>");
			sbHtml.append(String.valueOf(mapData.get("title")));
			sbHtml.append("<span><img src=\"./images/star").append(String.valueOf(mapData.get("score"))).append(".gif\" /></span>");
			sbHtml.append("<em>").append(String.valueOf(mapData.get("time"))).append("</em>  <a href=\"javascript:void(0);\" onclick=\"delComment('").append(mapData.get("id")).append("','").append(mapData.get("tablename")).append("');\" class=\"del\" title=\"删除\"> </a></li>");
			if (strTempContent.length() > 250) {
				sbHtml.append("<li class=\"concotent\"><span class=\"person\">").append(String.valueOf(mapData.get("username"))).append(":</span>").append(strTempContent.substring(0, 250));
				sbHtml.append("<span style=\"display:none\">").append(strTempContent.substring(250)).append("</span>");
				sbHtml.append("<em class=\"expand\"  onclick=\"$(this).hide();$(this).prev().show();$(this).next().show();\">展开</em><em style=\"display:none\" class=\"merger\" onclick=\"$(this).hide();$(this).prev().show();$(this).prev().prev().hide();\">合并</em></li>");
			} else {
				sbHtml.append("<li class=\"concotent\"><span class=\"person\">").append(String.valueOf(mapData.get("username"))).append(":</span>").append(strTempContent).append("</li>");
			}
			sbHtml.append("</ul>");
		}
		sbHtml.append("</div>");
		return sbHtml.toString();
	}

	private String delComment(HttpServletRequest request) throws ServletException {
		String strCommentID = request.getParameter("vid");
		String strTables = request.getParameter("vtab");
		if (Common.IsNullOrEmpty(strCommentID)) {
			return "0";
		}
		Map<String, List<String>> mapComent = new HashMap<String, List<String>>();
		if (!strCommentID.contains(",")) {
			ArrayList<String> lstTab = new ArrayList<String>();
			lstTab.add(strCommentID);
			mapComent.put(strTables, lstTab);
		} else {
			String[] arrID = strCommentID.substring(0, strCommentID.length() - 1).split(",");
			String[] arrTab = strTables.substring(0, strTables.length() - 1).split(",");
			for (int i = 0; i < arrTab.length; i++) {
				if (mapComent.containsKey(arrTab[i])) {
					mapComent.get(arrTab[i]).add(arrID[i]);
				} else {
					ArrayList<String> lstTab = new ArrayList<String>();
					lstTab.add(arrID[i]);
					mapComent.put(arrTab[i], lstTab);
				}
			}
		}
		if (CommentMngr.delComment(mapComent)) {
			return "1";
		} else {
			return "0";
		}
	}

}
