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
 * Servlet implementation class ArticlePraiseHandlerServlet
 */
@WebServlet("/ArticlePraiseHandler.do")
public class ArticlePraiseHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ArticlePraiseHandlerServlet() {
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
		if ("getcount".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = getPraiseCount(request);
		} else if ("getlist".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = getPraiseList(request);
		} else if ("delpraise".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = delPraise(request);
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

	protected String getPraiseCount(HttpServletRequest request) throws ServletException, IOException {
		String strTitle = request.getParameter("tl");
		return String.valueOf(CommentMngr.getPraiseCount(strTitle));
	}

	protected String getPraiseList(HttpServletRequest request) throws ServletException, IOException {
		String strTitle = request.getParameter("tl");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstPraise = CommentMngr.getPraiseList(strTitle, iStart, iLength);
		if (lstPraise == null) {
			return "";
		}
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<table id=\"tabpraise\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\">&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"15%\">篇名</th>");
		sbHtml.append("<th width=\"70\">推荐数</th>");
		sbHtml.append("<th>推荐人</th>");
		sbHtml.append("<th width=\"50\">操作</th>");
		sbHtml.append("</tr>");

		Iterator<Map<String, Object>> iMap = lstPraise.iterator();

		Map<String, Object> mapData = null;
		int iNum = iStart;
		while (iMap.hasNext()) {
			mapData = iMap.next();
			String strTempTitle = String.valueOf(mapData.get("title"));
			String strUserName = Common.Trim(String.valueOf(mapData.get("username")),";");
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chkpras\" type=\"checkbox\" value=\"").append(mapData.get("id")).append(",").append(mapData.get("tablename")).append("\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td><span title = \"").append(strTempTitle).append("\">").append(strTempTitle.length() > 30 ? strTempTitle.substring(0, 30) : strTempTitle).append("</span></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("count")).append("</td>");
			if (strUserName.length() > 120) {
				sbHtml.append("<td class=\"namef12\">").append(strUserName.substring(0, 120));
				sbHtml.append("<span style=\"display:none\">").append(strUserName.substring(120)).append("</span>");
				sbHtml.append("<em class=\"expand\"  onclick=\"$(this).hide();$(this).prev().show();$(this).next().show();\">展开</em><em style=\"display:none\" class=\"merger\" onclick=\"$(this).hide();$(this).prev().show();$(this).prev().prev().hide();\">合并</em></td>");
			} else {
				sbHtml.append("<td class=\"namef12\">").append(strUserName).append("</td>");
			}
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delPraise('").append(mapData.get("id")).append("','").append(mapData.get("tablename")).append("');\" class=\"del\" title=\"删除\"> </a></td>");
		}										
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private String delPraise(HttpServletRequest request) throws ServletException {
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
		if (CommentMngr.delPraise(mapComent)) {
			return "1";
		} else {
			return "0";
		}
	}

}
