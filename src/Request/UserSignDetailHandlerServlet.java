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

import BLL.SignMngr;
import Util.Common;

/**
 * Servlet implementation class UserSignDetailHandlerServlet
 */
@WebServlet("/UserSignDetailHandler.do")
public class UserSignDetailHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserSignDetailHandlerServlet() {
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
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getUserSignDetailCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getUserSignDeatil(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	protected String getUserSignDetailCount(HttpServletRequest request) {
		String txtUserName = request.getParameter("txtUserName");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		int count = SignMngr.getUserSignDetailCount(txtUserName, txtStartDate, txtEndDate);
		return String.valueOf(count);
	}

	protected String getUserSignDeatil(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String txtUserName = request.getParameter("txtUserName");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		String start = request.getParameter("start");
		String len = request.getParameter("len");
		List<Map<String, Object>> userSignList = SignMngr.getUserSignLogList(txtUserName, txtStartDate, txtEndDate,
				Integer.parseInt(start), Integer.parseInt(len));
		Iterator<Map<String, Object>> iMap = userSignList.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append(
				"<table width=\"100%\" id=\"tabserinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>用户名</th>");
		sbHtml.append("<th>签到ip</th>");
		sbHtml.append("<th>app版本</th>");
		sbHtml.append("<th>签到时间</th>");
		sbHtml.append("</tr>");
		int iNum = Integer.parseInt(start);
		while (iMap.hasNext()) {//username,ip,version,time
			mapData = iMap.next();
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("username")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("ip")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("version")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">")
					.append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
}
