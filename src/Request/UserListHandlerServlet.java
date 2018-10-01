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

/**
 * Servlet implementation class UserListHandlerServlet
 */
@WebServlet("/UserListHandler.do")
public class UserListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserListHandlerServlet() {
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
			strResult = getSysUserCount(request);
		}else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSysUserList(request);
		} else if ("deluser".equals(request.getParameter("do"))) {
			strResult = delUserInfo(request);
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
	
	private String getSysUserCount(HttpServletRequest request) throws ServletException, IOException {
		return String.valueOf(UserInfoMngr.getSysUserCount());
	}

	private String getSysUserList(HttpServletRequest request) throws ServletException, IOException {

		List<Map<String, Object>> lstUser = UserInfoMngr.getSysUserList();
		if (lstUser == null) {
			return "";
		}
		StringBuilder sbUserList = new StringBuilder();
		sbUserList.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbUserList.append("<tr>");
		sbUserList.append("<th width=\"5%\" class=\"num\">&nbsp;</th>");
		sbUserList.append("<th width=\"22%\">用户名</th>  ");
		sbUserList.append("<th width=\"8%\">用户角色</th>  ");
		sbUserList.append("<th width=\"10%\" class=\"bordblack\">操作</th>");
		sbUserList.append("<th width=\"5%\" class=\"num\">&nbsp;</th>");
		sbUserList.append("<th width=\"25%\">用户名</th> ");
		sbUserList.append("<th width=\"8%\">用户角色</th>  ");
		sbUserList.append("<th width=\"10%\">操作</th>");
		sbUserList.append("</tr>");

		Iterator<Map<String, Object>> iUsers = lstUser.iterator();
		Map<String, Object> iMap = null;
		int iNum = 1;
		while (iUsers.hasNext()) {
			iMap = iUsers.next();
			String strUserName = String.valueOf(iMap.get("username"));
			if ("root".equalsIgnoreCase(strUserName)) {
				continue;
			}
			if (iNum % 2 == 1) {
				sbUserList.append("<tr>");
			}
			sbUserList.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbUserList.append("<td>").append(strUserName).append("</td>");
			sbUserList.append("<td class=\"tabcent\">").append("1".equals(String.valueOf(iMap.get("role"))) ? "管理员" : "2".equals(String.valueOf(iMap.get("role")))?"普通用户":"机构馆用户").append("</td>");
			sbUserList.append("<td class=\"tabopt bordblack\"><a href=\"UserInfo.do?uid=").append(String.valueOf(iMap.get("id"))).append("\" class=\"edit\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delUser('").append(String.valueOf(iMap.get("id"))).append("')\" class=\"del\" title=\"删除\"> </a></td>");
			if (iNum % 2 == 1) {
				sbUserList.append("</tr>");
			}
		}

		if (!sbUserList.toString().endsWith("</tr>")) {
			sbUserList.append("<td class=\"num\">&nbsp;</td>");
			sbUserList.append("<td>&nbsp;</td>");
			sbUserList.append("<td>&nbsp;</td>");
			sbUserList.append("<td class=\"tabopt\">&nbsp;</td>");
			sbUserList.append("</tr>");
		}
		sbUserList.append("</table>");
		return sbUserList.toString();
	}

	private String delUserInfo(HttpServletRequest request) throws ServletException, IOException {
		String strUserID = request.getParameter("uid");
		if (UserInfoMngr.delSysUserInfo(strUserID)) {
			return "1";
		} else {
			return "0";
		}
	}
}
