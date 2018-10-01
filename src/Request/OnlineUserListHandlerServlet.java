package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.UserInfoMngr;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class OnlineUserHandlerServlet
 */
@WebServlet("/OnlineUserListHandler.do")
public class OnlineUserListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OnlineUserListHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getOnLineUserCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getOnLineUserList(request);
		} else if ("onlinecount".equals(request.getParameter("do"))) {
			strResult = getOnlineCount(request);
		} else if ("halfHourUsers".equals(request.getParameter("do"))) {
			strResult = getHalfHourUsers(request);
		}
		out.write(strResult);
		out.flush();
		out.close();

	}

	private String getHalfHourUsers(HttpServletRequest request) {
		 
		Calendar instance = Calendar.getInstance();

		long timeInMillis = instance.getTimeInMillis();

		Date now = new Date(timeInMillis);
		long endMillis = timeInMillis - (1000 * 1800);
		Date endDate = new Date(endMillis);

		//instance.set(Calendar.HOUR_OF_DAY, 0);
		//instance.set(Calendar.MINUTE, 0);
		//instance.set(Calendar.SECOND, 0);
		//instance.set(Calendar.MILLISECOND, 0);
		//long zero = instance.getTimeInMillis();
		//Date startDate = new Date(zero);
		int count=UserInfoMngr.getHalfHourUsers(endDate);
		return String.valueOf(count);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private String getOnLineUserCount(HttpServletRequest request) throws ServletException, IOException {
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}
		String strUserName = request.getParameter("uname");
		return String.valueOf(UserInfoMngr.getOnlineUserCount(appid, strUserName));
	}

	private String getOnLineUserList(HttpServletRequest request) throws ServletException, IOException {
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}
		StringBuilder sbHtml = new StringBuilder();
		String strUserName = request.getParameter("uname");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstOnlineUserName = null;
		lstOnlineUserName = UserInfoMngr.getOnlineUserNameList(appid, strUserName, iStart, iLength);
		if (lstOnlineUserName == null) {
			return "";
		}
		Iterator<Map<String, Object>> iUserName = lstOnlineUserName.iterator();
		Map<String, Object> iMapUserName = null;
		StringBuilder sbUserName = new StringBuilder();
		while (iUserName.hasNext()) {
			iMapUserName = iUserName.next();
			sbUserName.append("'").append(iMapUserName.get("username")).append("',");
		}
		List<Map<String, Object>> lstOnlineUserInfo = null;
		if (sbUserName.length() > 0) {
			sbUserName.delete(sbUserName.length() - 1, sbUserName.length());
		}
		lstOnlineUserInfo = UserInfoMngr.getOnlineUserInfoList(sbUserName.toString());

		sbHtml.append(
				"<table width=\"100%\" id=\"tabusers\" name=\"tabusers\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40px\"  class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"15%\">用户名</th>");
		sbHtml.append("<th>设备型号</th>");
		sbHtml.append("<th width=\"15%\">IP地址</th>");
		sbHtml.append("<th width=\"100\">AppId</th>");
		sbHtml.append("<th width=\"15%\">最新登录时间</th>");
		sbHtml.append("<th width=\"10%\">在线数</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iUserInfo = lstOnlineUserInfo.iterator();
		Map<String, Object> iMapUserInfo = null;
		int iNum = iStart;
		while (iUserInfo.hasNext()) {
			iMapUserInfo = iUserInfo.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMapUserInfo.get("username")).append("</td>");
			sbHtml.append("<td>")
					.append(iMapUserInfo.get("client") == null ? "" : String.valueOf(iMapUserInfo.get("client")))
					.append("</td>");
			sbHtml.append("<td class=\"tabcent\">")
					.append(iMapUserInfo.get("address") == null ? "" : String.valueOf(iMapUserInfo.get("address")))
					.append("</td>");
			sbHtml.append("<td class=\"tabcent\">")
					.append(iMapUserInfo.get("appid") == null ? "&nbsp" : String.valueOf(iMapUserInfo.get("appid")))
					.append("</td>");
			sbHtml.append("<td class=\"tabcent\">")
					.append(iMapUserInfo.get("time") == null ? "" : String.valueOf(iMapUserInfo.get("time")))
					.append("</td>");
			sbHtml.append("<td class=\"tabcent\"><a title='查看在线详情' href='OnlineUserDetail.do?uname=")
					.append(iMapUserInfo.get("username")).append("'><span id=\"span")
					.append(iMapUserInfo.get("username")).append("\"></span></a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table><input type=\"hidden\" id=\"hidusers\" name=\"hidusers\" value=\"")
				.append(sbUserName.toString()).append("\" />");
		return sbHtml.toString();
	}

	private String getOnlineCount(HttpServletRequest request) throws ServletException, IOException {
		String strUserName = request.getParameter("vusers");
		if (Common.IsNullOrEmpty(strUserName)) {
			return "";
		}
		List<Map<String, Object>> lstOnlineUserCount = UserInfoMngr.getOnlineUserLoginCount(strUserName);
		if (lstOnlineUserCount == null) {
			return "";
		}
		StringBuilder sbResult = new StringBuilder();
		Iterator<Map<String, Object>> iUserCount = lstOnlineUserCount.iterator();
		Map<String, Object> iMap = null;
		sbResult.append("{");
		while (iUserCount.hasNext()) {
			iMap = iUserCount.next();
			sbResult.append("\"").append(String.valueOf(iMap.get("username")).toLowerCase()).append("\":\"")
					.append(iMap.get("onlinecount")).append("\",");
		}
		if (sbResult.length() > 1) {
			sbResult.delete(sbResult.length() - 1, sbResult.length());
		}
		sbResult.append("}");
		return sbResult.toString();
	}

}
