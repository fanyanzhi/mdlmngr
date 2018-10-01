package Request;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.Logger;
import Util.Common;

/**
 * Servlet implementation class ExceptionHandlerServlet
 */
@WebServlet("/ExceptionHandler.do")
public class ExceptionHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExceptionHandlerServlet() {
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
		response.setContentType("text/html utf-8");
		String strOperation = request.getParameter("do");
		String strOut = "";
		if (strOperation.equals("getlist")) {
			strOut = GetDataList(request);
		} else if (strOperation.equals("getcount")) {
			strOut = GetDataCount(request);
		} else if (strOperation.equals("delete")) {
			strOut = DeleteError(request);
		}
		// System.out.println(strOut);
		response.getWriter().print(strOut);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private String GetDataList(HttpServletRequest request) {
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		String strStartTime = request.getParameter("starttime");
		String strEndTime = request.getParameter("endtime");
		if (Common.IsNullOrEmpty(strStartTime)) {
			strStartTime = "00:00:00";
		}
		if (Common.IsNullOrEmpty(strEndTime)) {
			strEndTime = "24:00:00";
		}
		if (!Common.IsNullOrEmpty(strStartDate)) {
			strStartDate = strStartDate.concat(" ").concat(strStartTime);
		}
		if (!Common.IsNullOrEmpty(strEndDate)) {
			strEndDate = strEndDate.concat(" ").concat(strEndTime);
		}
		String strStart = request.getParameter("start");
		String strLength = request.getParameter("len");
		int iStart = Integer.valueOf(strStart);
		int iLength = Integer.valueOf(strLength);
		List<Map<String, Object>> lstData = Logger.GetExceptionList(strStartDate, strEndDate, iStart, iLength);
		if (lstData == null) {
			return "";
		}
		StringBuilder sbList = new StringBuilder();
		Map<String, Object> mapData = null;
		Iterator<Map<String, Object>> iMap = lstData.iterator();
		// sbList.append("<table id=\"tblErrorList\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"tabB\">\r\n<tr>\r\n<th width=\"15\">&nbsp;</th>\r\n<th width=\"280\">信息</th>\r\n<th width=\" \">跟踪信息</th>\r\n<th width=\"65\">时间</th>\r\n<th width=\"40\">操作</th>\r\n</tr>\r\n");
		sbList.append("<table id=\"tblErrorList\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone tabvery\">");
		sbList.append("<tr>");
		sbList.append("<th width=\"40\" ></th>");
		sbList.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbList.append("<th  width=\"280\">信息</th>");
		sbList.append("<th>异常信息</th>");
		sbList.append("<th width=\"100\">IP</th>");
		sbList.append("<th width=\"80\">时间</th>");
		sbList.append("<th width=\"40\"> 操作</th>");
		sbList.append("</tr>");
		while (iMap.hasNext()) {
			mapData = iMap.next();
			sbList.append("<tr>\r\n").append("<td><input name=\"chkErrorList\" type=\"checkbox\" value=\"").append(mapData.get("id")).append("\"></td>\r\n");
			sbList.append("<td  class=\"num\">").append(iStart++).append("</td>\r\n");
			sbList.append("<td>").append(mapData.get("message")).append("</td>\r\n");
			sbList.append("<td>").append(mapData.get("stacktrace")==null?"":String.valueOf(mapData.get("stacktrace")).replace("\r\n", "<br />")).append("</td>\r\n");
			sbList.append("<td>").append(mapData.get("address")==null?"":mapData.get("address")).append("</td>\r\n");
			sbList.append("<td align=\"center\">").append(Common.ConvertToDateTime(mapData.get("time").toString())).append("</td>\r\n");
			sbList.append("<td><a href=\"javascript:void(0);\"class=\"del\" title=\"删除\" onclick='deleteError(\"").append(mapData.get("id")).append("\");' class=\"inneropt\"></a></td>\r\n");
			sbList.append("</tr>\r\n");
		}
		sbList.append("</table>\r\n");
		return sbList.toString();
	}

	private String GetDataCount(HttpServletRequest request) {
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		String strStartTime = request.getParameter("starttime");
		String strEndTime = request.getParameter("endtime");
		if (Common.IsNullOrEmpty(strStartTime)) {
			strStartTime = "00:00:00";
		}
		if (Common.IsNullOrEmpty(strEndTime)) {
			strEndTime = "24:00:00";
		}
		if (!Common.IsNullOrEmpty(strStartDate)) {
			strStartDate = strStartDate.concat(" ").concat(strStartTime);
		}
		if (!Common.IsNullOrEmpty(strEndDate)) {
			strEndDate = strEndDate.concat(" ").concat(strEndTime);
		}
		return String.valueOf(Logger.GetExceptionCount(strStartDate, strEndDate));
	}

	private String DeleteError(HttpServletRequest request) {
		String strIDs = request.getParameter("ids");
		if (!Common.IsNullOrEmpty(strIDs) && strIDs.equals("all")) {
			strIDs = null;
		}
		return String.valueOf(Logger.DeleteException(strIDs));
	}

}
