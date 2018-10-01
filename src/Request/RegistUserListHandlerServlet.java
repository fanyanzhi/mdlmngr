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

import BLL.UserInfoMngr;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class OnlineUserHandlerServlet
 */
@WebServlet("/RegistUserListHandler.do")
public class RegistUserListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegistUserListHandlerServlet() {
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
			strResult = getRegistUserCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getRegistUserList(request);
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

	private String getRegistUserCount(HttpServletRequest request) throws ServletException, IOException {
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}else{
		 appid = request.getParameter("keyword");
		}
		String phone = request.getParameter("phone");
		String username= request.getParameter("username");
		String startDate = request.getParameter("startdate");
		String endDate = request.getParameter("enddate");
		int count = UserInfoMngr.getRegistCount(appid, phone, username, startDate, endDate);
		return String.valueOf(count);
	}

	private String getRegistUserList(HttpServletRequest request) throws ServletException, IOException {
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}else{
		 appid = request.getParameter("keyword");
		}
		String phone = request.getParameter("phone");
		String username= request.getParameter("username");
		String startDate = request.getParameter("startdate");
		String endDate = request.getParameter("enddate");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstRegistUsers = null;
		lstRegistUsers = UserInfoMngr.getRegistUserList(appid, phone, username, startDate, endDate,iStart,iLength);
		if (lstRegistUsers == null) {
			return "";
		}
		
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<table width=\"100%\" id=\"tabusers\" name=\"tabusers\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40px\"  class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"15%\">用户名</th>");
		sbHtml.append("<th width=\"12%\">手机号</th>");
		sbHtml.append("<th>邮箱</th>");
		sbHtml.append("<th width=\"15%\">注册平台</th>");
		sbHtml.append("<th width=\"100\">地址</th>");
		sbHtml.append("<th width=\"15%\">客户端</th>");
		sbHtml.append("<th width=\"10%\">时间</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iUserInfo = lstRegistUsers.iterator();
		Map<String, Object> iMapUserInfo = null;
		int iNum = iStart;
		while (iUserInfo.hasNext()) {
			iMapUserInfo = iUserInfo.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMapUserInfo.get("username")).append("</td>");
			sbHtml.append("<td>").append(iMapUserInfo.get("mobile")==null?"&nbsp":String.valueOf(iMapUserInfo.get("mobile"))).append("</td>");
			sbHtml.append("<td>").append(iMapUserInfo.get("email")==null?"":String.valueOf(iMapUserInfo.get("email"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMapUserInfo.get("appid")==null?"&nbsp":String.valueOf(iMapUserInfo.get("appid"))).append("</td>");
			String ip =iMapUserInfo.get("address")==null?"":String.valueOf(Common.longToIP(Long.parseLong(String.valueOf(iMapUserInfo.get("address")).trim())));
			sbHtml.append("<td class=\"tabcent\">").append(ip).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMapUserInfo.get("platform")==null?"":String.valueOf(iMapUserInfo.get("platform"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMapUserInfo.get("time") == null ? "" : String.valueOf(iMapUserInfo.get("time"))).append("</td>");
			sbHtml.append("</tr>");
		}
		return sbHtml.toString();
	}

}
