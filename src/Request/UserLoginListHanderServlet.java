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
 * Servlet implementation class UserLoginListHanderServlet
 */
@WebServlet("/UserLoginListHander.do")
public class UserLoginListHanderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLoginListHanderServlet() {
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
			strResult = getUserLoginCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getUserLoginList(request);
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
		doGet(request, response);
	}
	
	private String getUserLoginCount(HttpServletRequest request) throws ServletException, IOException {
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}else{
			appid = request.getParameter("appid");
		}
		String strUserName = request.getParameter("uname");
		return String.valueOf(UserInfoMngr.getUserLoginCount(appid, strUserName, strStartDate, strEndDate));
	}
	
	private String getUserLoginList(HttpServletRequest request) throws ServletException, IOException {
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}else{
			appid = request.getParameter("appid");
		}
		StringBuilder sbHtml = new StringBuilder();
		String strUserName = request.getParameter("uname");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstUserLoginLog = null;
		lstUserLoginLog = UserInfoMngr.getUserLoginList(appid, strUserName, strStartDate, strEndDate, iStart, iLength);
		if (lstUserLoginLog == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" id=\"tablogs\" name=\"tablogs\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th  width=\"18%\">用户名</th>");
		sbHtml.append("<th>设备型号</th>");
		sbHtml.append("<th width=\"100\">AppId</th>");
		sbHtml.append("<th width=\"80\">App版本</th>");
		sbHtml.append("<th width=\"15%\">IP地址</th>");
		sbHtml.append("<th width=\"14%\">登录时间</th>");
		sbHtml.append("<th width=\"12%\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iUser = lstUserLoginLog.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;
		while (iUser.hasNext()) {
			iMap = iUser.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chklogid\" value=\"").append(iMap.get("id")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td>").append(iMap.get("client") == null ? "&nbsp" : String.valueOf(iMap.get("client"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("appid")==null?"&nbsp":String.valueOf(iMap.get("appid"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("version") == null ? "&nbsp" : String.valueOf(iMap.get("version"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("address") == null ? "&nbsp" : String.valueOf(iMap.get("address"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delSingleLog(").append(iMap.get("id")).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
	
	

}
