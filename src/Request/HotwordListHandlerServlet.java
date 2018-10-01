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

import BLL.HotWordMngr;

/**
 * Servlet implementation class HotwordListHandlerServlet
 */
@WebServlet("/HotwordListHandler.do")
public class HotwordListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HotwordListHandlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getHotWordCount();
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getHotWordList(request);
		} else if ("del".equals(request.getParameter("do"))) {
			strResult = delAppInfo(request);
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
	
	private String getHotWordCount(){
		return String.valueOf(HotWordMngr.getHotWordCount());
	}
	
	private String getHotWordList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();

		List<Map<String, Object>> lstHotWord = HotWordMngr.getHotWordList();
		if (lstHotWord == null) {
			return "";
		}
		Iterator<Map<String, Object>> iHotWord = lstHotWord.iterator();
		Map<String, Object> iMap = null;

		sbHtml.append("<table width=\"100%\" id=\"tabusers\" name=\"tabusers\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40px\"  class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"15%\">类型</th>");
		sbHtml.append("<th>关键词</th>");
		sbHtml.append("<th width=\"10%\">创建时间</th>");
		sbHtml.append("<th width=\"10%\">操作</th>");
		sbHtml.append("</tr>");
		int iNum = 1;
		while (iHotWord.hasNext()) {
			iMap = iHotWord.next();
			String keyword = iMap.get("hotword") == null ? "" : String.valueOf(iMap.get("hotword"));
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("type")).append("</td>");
			sbHtml.append("<td class=\"tabcent\"><span title='").append(keyword).append("'>").append(keyword.length() > 20 ? keyword.substring(0, 19) : keyword).append("</span></td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("time")).append("</td>");
			sbHtml.append("<td class=\"tabopt bordblack\"><a href=\"HotWord.do?hwid=").append(String.valueOf(iMap.get("id"))).append("\" class=\"edit\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delhotword('").append(String.valueOf(iMap.get("id"))).append("')\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
	
	
	private String delAppInfo(HttpServletRequest request) {
		String result = "";
		String hwId = request.getParameter("hid");
		if (hwId == null || Common.Trim(hwId, " ").length() == 0) {
			return "0";
		}
		if (hwId.endsWith(",")) {
			hwId = Common.Trim(hwId, ",");
		}

		if (HotWordMngr.delHotWord(hwId)) {
			result = "1";
		} else {
			result = "0";
		}
		return result;
	}


}
