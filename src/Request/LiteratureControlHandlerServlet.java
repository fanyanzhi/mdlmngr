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

import BLL.CommentMngr;
import BLL.NoticeMngr;
import Util.Common;

/**
 * Servlet implementation class LiteratureControlHandlerServlet
 */
@WebServlet("/LiteratureControlHandler.do")
public class LiteratureControlHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LiteratureControlHandlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("getcount".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = getClosedCommentCount(request);
		} else if ("getlist".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = getClosedCommentList(request);
		} else if ("open".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = openComment(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected String getClosedCommentCount(HttpServletRequest request) throws ServletException, IOException {
		return String.valueOf(CommentMngr.getArticlesCommentCount());
	}

	protected String getClosedCommentList(HttpServletRequest request) throws ServletException, IOException {
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstComment = CommentMngr.getArticlesCommentList(iStart, iLength);
		if (lstComment == null) {
			return "";
		}
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<table width=\"100%\" border=\"0\" name=\"docutab\" id=\"docutab\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\">&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>篇名</th>");
		sbHtml.append("<th width=\"20%\">来源</th>");
		sbHtml.append("<th width=\"15%\">时间</th>");
		sbHtml.append("<th width=\"50\">操作</th>");
		sbHtml.append("</tr>");
		
		Iterator<Map<String, Object>> iMap = lstComment.iterator();
		Map<String, Object> mapData = null;
		int iNum = iStart;
		while (iMap.hasNext()) {
			mapData = iMap.next();
			String strID=String.valueOf(mapData.get("id"));
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chkdocuid\" type=\"checkbox\" value=\"").append(strID).append("\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(String.valueOf(mapData.get("title"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(mapData.get("name_ch"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(mapData.get("time"))).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"openComment('").append(strID).append("')\" class=\"turnon\" title=\"开启\"></a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
	private String openComment(HttpServletRequest request) throws ServletException {
		String strCommentID = request.getParameter("cid");
		if (Common.IsNullOrEmpty(strCommentID)) {
			return "0";
		}
		if(strCommentID==null){
			return "0";
		}
		strCommentID=Common.Trim(strCommentID, ",");
		if(CommentMngr.openArticlesComment(strCommentID)){
			NoticeMngr.delNoticeRelationship(strCommentID);
			return "1";	
		}else{
			return "0";
		}
	}
}
