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

import BLL.BehaviourMngr;
import Util.Common;

/**
 * Servlet implementation class HeartBeatDetailHandlerServlet
 */
@WebServlet("/HeartBeatClientidDetailHandler.do")
public class HeartBeatClientidDetailHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HeartBeatClientidDetailHandlerServlet() {
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
			strResult = getHeartBeatCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getHeartBeatList(request);
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

	private String getHeartBeatCount(HttpServletRequest request) throws ServletException, IOException {
		String strClientID = request.getParameter("ci");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		return String.valueOf(BehaviourMngr.getHeartBeatClientidCount(strClientID, strStartDate, strEndDate));
	}

	private String getHeartBeatList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String strUserName = request.getParameter("un");
		String strClientID = request.getParameter("ci");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstFiles = null;
		lstFiles = BehaviourMngr.getHeartBeatList(strUserName, strClientID, strStartDate, strEndDate, iStart, iLength);
		if (lstFiles == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" name=\"tabfiles\" id=\"tabfiles\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"5%\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th  width=\"18%\">用户名</th>");
		sbHtml.append("<th width=\"300\">设备ID</th>");
		sbHtml.append("<th>设备型号</th>");
		sbHtml.append("<th width=\"80\">App版本</th>");
		sbHtml.append("<th width=\"10%\">IP地址</th>");
		sbHtml.append("<th width=\"12%\">在线时间</th>");
		sbHtml.append("<th width=\"12%\">时间</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iFile = lstFiles.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;
		while (iFile.hasNext()) { // id,username,filename,fileid,client,address,time
			iMap = iFile.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("username") == null ? "" : String.valueOf(iMap.get("username"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("clientid")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("platform") == null ? "" : iMap.get("platform")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("appinfo") == null ? "" : iMap.get("appinfo")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("address") == null ? "" : Common.longToIP(Long.parseLong(String.valueOf(iMap.get("address"))))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("interval") == null ? "" : iMap.get("interval")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
}
