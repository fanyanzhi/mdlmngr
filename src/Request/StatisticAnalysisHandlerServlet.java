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
 * Servlet implementation class StatisticAnalysisHandlerServlet
 */
@WebServlet("/StatisticAnalysisHandler.do")
public class StatisticAnalysisHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatisticAnalysisHandlerServlet() {
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
			strResult = getUserCount(request);
		}else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getUserList(request);
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
	private String getUserCount(HttpServletRequest request) throws ServletException, IOException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strUserName=request.getParameter("uname");
		return String.valueOf(UserInfoMngr.getLastLoginLogCount(appid,strUserName));
	}
	
	private String getUserList(HttpServletRequest request) throws ServletException, IOException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		StringBuilder sbHtml = new StringBuilder();
		String strUserName=request.getParameter("uname");
		int iStart=Integer.parseInt(request.getParameter("start"));
		int iLength=Integer.parseInt(request.getParameter("len"));
		
		List<Map<String, Object>> lstLoginLog = null;
		lstLoginLog = UserInfoMngr.getLastLoginLogList(appid,strUserName, iStart, iLength);
		if (lstLoginLog == null) {
			return "";
		}
		sbHtml.append("<table id=\"selusers\" name=\"selusers\"  width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		Iterator<Map<String, Object>> iUser = lstLoginLog.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;// id,username,address,client,logincount,time
		while (iUser.hasNext()) {
			iMap = iUser.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td width=\"30\"><input name=\"seluserid\" name=\"seluserid\" type=\"checkbox\" ckval=\"").append(iMap.get("username")).append("\" onclick=\"selectUser(this,'").append(iMap.get("username")).append("')\" value=\"").append(iMap.get("username")).append("\" /></td>");
			sbHtml.append("<td width=\"50\" class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td width=\"100\">").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

}
