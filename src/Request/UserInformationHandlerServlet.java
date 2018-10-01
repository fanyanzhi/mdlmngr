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

import BLL.UserInformationMngr;
import Util.Common;

/**
 * 
 * 用户资料
 *
 */
@WebServlet("/UserInformationHandler.do")
public class UserInformationHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UserInformationHandlerServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getUserInformationCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSearchData(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	protected String getUserInformationCount(HttpServletRequest request) {
		String txtUserName = request.getParameter("txtUserName");
		String txtMobile = request.getParameter("txtMobile");
		int count = UserInformationMngr.getUserInformationCount(txtUserName, txtMobile);
		return String.valueOf(count);
	}

	protected String getSearchData(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String txtUserName = request.getParameter("txtUserName");
		String txtMobile = request.getParameter("txtMobile");
		String start = request.getParameter("start");
		String len = request.getParameter("len");
		List<Map<String, Object>> rechargeList = UserInformationMngr.getUserInformationList(txtUserName, txtMobile,
				Integer.parseInt(start), Integer.parseInt(len));
		Iterator<Map<String, Object>> iMap = rechargeList.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append(
				"<table width=\"100%\" id=\"tabserinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>用户名</th>");
		sbHtml.append("<th>姓名</th>");
		sbHtml.append("<th>手机</th>");
		sbHtml.append("<th>Email</th>");
		sbHtml.append("<th>时间</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		String strServerID = "";
		String usernamestr = null;
		String namestr = null;
		String mobilestr = null;
		String emailstr = null;

		StringBuilder sbHostID = new StringBuilder();
		while (iMap.hasNext()) {
			mapData = iMap.next();
			usernamestr = String.valueOf(mapData.get("username") == null ? "" : mapData.get("username"));
			namestr = String.valueOf(mapData.get("name") == null ? "" : mapData.get("name"));
			mobilestr = String.valueOf(mapData.get("mobile") == null ? "" : mapData.get("mobile"));
			emailstr = String.valueOf(mapData.get("email") == null ? "" : mapData.get("email"));

			sbHtml.append("<tr id=\"tr").append(strServerID).append("\">");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(usernamestr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(namestr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(mobilestr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(emailstr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(mapData.get("updatetime") == null ? ""
					: Common.ConvertToDateTime(mapData.get("updatetime").toString()))).append("</td>");
			sbHtml.append("</tr> ");
		}
		if (!Common.IsNullOrEmpty(sbHostID.toString())) {
			sbHostID.delete(sbHostID.length() - 1, sbHostID.length());
		}
		sbHtml.append("</table><input type=\"hidden\" name=\"hidtrs\" id=\"hidtrs\" value=\"")
				.append(sbHostID.toString()).append("\" />");
		return sbHtml.toString();
	}

}
