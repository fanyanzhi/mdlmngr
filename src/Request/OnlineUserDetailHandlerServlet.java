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
import Util.Common;

/**
 * Servlet implementation class OnlineUserDetailHandlerServlet
 */
@WebServlet("/OnlineUserDetailHandler.do")
public class OnlineUserDetailHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OnlineUserDetailHandlerServlet() {
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
		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getOnLineUserList(request);
		}else if ("deluser".equals(request.getParameter("do"))) {
			strResult = delOnLineUser(request);
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

	private String getOnLineUserList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String strUserName=request.getParameter("uname");

		List<Map<String, Object>> lstOnlineUser = null;
		lstOnlineUser=UserInfoMngr.getOnlineUserDetail(strUserName);
		if (lstOnlineUser == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" id=\"tabusers\" name=\"tabusers\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<td width=\"40px\" >&nbsp;</th>");
		sbHtml.append("<th width=\"50px\"  class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"18%\">用户名</th>");
		sbHtml.append("<th>设备型号</th>");
		sbHtml.append("<th width=\"100\">AppId</th>");
		sbHtml.append("<th width=\"80\">App版本</th>");
		sbHtml.append("<th width=\"15%\">IP地址</th>");
		sbHtml.append("<th width=\"15%\">最近操作时间</th>");
		sbHtml.append("<th width=\"15%\">登录时间</th>");
		sbHtml.append("<th width=\"60\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iUser = lstOnlineUser.iterator();
		Map<String, Object> iMap = null;
		int iNum=1;
		while (iUser.hasNext()) {
			iMap=iUser.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chkuserid\" value=\"").append(iMap.get("id")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td>").append(iMap.get("client")==null?"":String.valueOf(iMap.get("client"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("appid")==null?"&nbsp":String.valueOf(iMap.get("appid"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("version") == null ? "&nbsp" : String.valueOf(iMap.get("version"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("address")==null?"":String.valueOf(iMap.get("address"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("lasttime")==null?"":String.valueOf(iMap.get("lasttime"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delOnlineUser(").append(iMap.get("id")).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
	
	private String delOnLineUser(HttpServletRequest request) throws ServletException, IOException {
		String strResult = "";
		String strUserId = request.getParameter("uid");
		if (strUserId == null || Common.Trim(strUserId, " ").length() == 0) {
			return "0";
		}
		if (strUserId.endsWith(",")) {
			strUserId = Common.Trim(strUserId, ",");
		}

		if (UserInfoMngr.delOnlineUser(strUserId)) {
			strResult = "1";
		} else {
			strResult = "0";
		}

		return strResult;
	}
}
