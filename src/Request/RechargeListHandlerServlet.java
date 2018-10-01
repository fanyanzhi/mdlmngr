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

import BLL.RechargeMngr;
import Util.Common;

/**
 * 
 * 充值记录
 *
 */
@WebServlet("/RechargeListHandler.do")
public class RechargeListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RechargeListHandlerServlet() {
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
			strResult = getRechargeCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSearchData(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	protected String getRechargeCount(HttpServletRequest request) {
		String txtUserName = request.getParameter("txtUserName");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		String txtPlatForm = request.getParameter("platform");
		String txtIsSus = request.getParameter("issus");
		int count = RechargeMngr.getRechargeCount(txtUserName, txtStartDate, txtEndDate,txtPlatForm,txtIsSus);
		return String.valueOf(count);
	}

	protected String getSearchData(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String txtUserName = request.getParameter("txtUserName");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		String txtPlatForm = request.getParameter("platform");
		String txtIsSus = request.getParameter("issus");
		String start = request.getParameter("start");
		String len = request.getParameter("len");
		float rechargeCount = RechargeMngr.getRechargeSum(txtUserName, txtStartDate, txtEndDate,txtPlatForm,txtIsSus);
		sbHtml.append("<input id=\"feeCount\" type=\"hidden\" value= \"").append(rechargeCount).append("\"/>");
		List<Map<String, Object>> rechargeList = RechargeMngr.getRechargeList(txtUserName, txtStartDate, txtEndDate, txtPlatForm, txtIsSus,
				Integer.parseInt(start), Integer.parseInt(len));
		Iterator<Map<String, Object>> iMap = rechargeList.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append(
				"<table width=\"100%\" id=\"tabserinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>用户名</th>");
		sbHtml.append("<th>金额</th>");
		sbHtml.append("<th>平台信息</th>");
		sbHtml.append("<th>充值环境</th>");
		sbHtml.append("<th>状态</th>");
		sbHtml.append("<th>IP</th>");
		sbHtml.append("<th>时间</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		String strServerID = "";
		String usernamestr = null;
		String cashstr = null;
		String platformstr = null;
		String environmentstr = null;
		String statusstr = null;
		String ipstr = null;

		StringBuilder sbHostID = new StringBuilder();
		while (iMap.hasNext()) {
			mapData = iMap.next();
			usernamestr = String.valueOf(mapData.get("username"));
			float cash = (float) mapData.get("cash");
			cashstr = String.valueOf(cash);
			platformstr = String.valueOf(mapData.get("platform"));
			environmentstr = String.valueOf(mapData.get("environment"));
			statusstr = String.valueOf(mapData.get("status"));
			ipstr = String.valueOf(mapData.get("ip"));

			sbHtml.append("<tr id=\"tr").append(strServerID).append("\">");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(usernamestr).append("</td>");

			sbHtml.append("<td class=\"tabcent\">").append(cashstr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(platformstr).append("</td>");

			sbHtml.append("<td class=\"tabcent\">").append(environmentstr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(statusstr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(ipstr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">")
					.append(String.valueOf(
							mapData.get("time") == null ? "" : Common.ConvertToDateTime(mapData.get("time").toString()))).append("</td>");
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
