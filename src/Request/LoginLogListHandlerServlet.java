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

import Model.UserLoginBean;
import Util.Common;

import BLL.UserInfoMngr;

/**
 * Servlet implementation class LoginLogListHandlerServlet
 */
@WebServlet("/LoginLogListHandler.do")
public class LoginLogListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginLogListHandlerServlet() {
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
			strResult = getLoginLogCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getLoginLogs(request);
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

	private String getLoginLogCount(HttpServletRequest request) throws ServletException, IOException {
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}else{
			appid = request.getParameter("appid");
		}
		String strUserName = request.getParameter("uname");
		return String.valueOf(UserInfoMngr.getLastLoginLogCount(appid, strUserName));
	}

	private String getLoginLogs(HttpServletRequest request) throws ServletException, IOException {
		//System.out.println("getLoginLogs");
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}else{
			appid = request.getParameter("appid");
		}
		StringBuilder sbHtml = new StringBuilder();
		String strUserName = request.getParameter("uname");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstLoginLog = null;
		//System.out.println("getLoginLogs"+appid);
		lstLoginLog = UserInfoMngr.getLastLoginLogList(appid, strUserName, iStart, iLength);
		//System.out.println("getLoginLogs"+lstLoginLog.toString());
		if (lstLoginLog == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" name=\"tablogs\" id=\"tablogs\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40px\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>用户名</th>");
		sbHtml.append("<th width=\"15%\">上次登录时间</th>");
		if(Common.IsNullOrEmpty(appid)){
			sbHtml.append("<th width=\"15%\">AppId</th>");
		}
		sbHtml.append("<th width=\"15%\">首次登录时间</th>");
		sbHtml.append("<th width=\"10%\">登录次数</th>");
		sbHtml.append("<th width=\"10%\">查看详细</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iUser = lstLoginLog.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;// id,username,address,client,logincount,time
		while (iUser.hasNext()) {
			iMap = iUser.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("updatetime")))).append("</td>");
			if(Common.IsNullOrEmpty(appid)){
				sbHtml.append("<td class=\"tabcent\">").append(iMap.get("appid") == null ? "&nbsp" : String.valueOf(iMap.get("appid"))).append("</td>");
			}
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabcent\"><a href=\"LoginLogDetail.do?uname=").append(iMap.get("username")).append("\" title=\"查看\">").append(UserInfoMngr.getUserLoginCount(appid, iMap.get("username").toString())).append("</a></td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"LoginLogDetail.do?uname=").append(iMap.get("username")).append("\" class=\"view\" title=\"查看\"></a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
}
