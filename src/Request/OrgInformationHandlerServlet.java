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

import BLL.OrgInformationMngr;
import Util.Common;

/**
 * 
 * 机构信息
 *
 */
@WebServlet("/OrgInformationHandler.do")
public class OrgInformationHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public OrgInformationHandlerServlet() {
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
		String txtOrg = request.getParameter("txtOrg");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		int count = OrgInformationMngr.getRechargeCount(txtOrg, txtStartDate, txtEndDate);
		return String.valueOf(count);
	}

	protected String getSearchData(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String txtOrg = request.getParameter("txtOrg");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		String start = request.getParameter("start");
		String len = request.getParameter("len");
		List<Map<String, Object>> rechargeList = OrgInformationMngr.getOrgInformationList(txtOrg, txtStartDate,
				txtEndDate, Integer.parseInt(start), Integer.parseInt(len));
		Iterator<Map<String, Object>> iMap = rechargeList.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append(
				"<table width=\"100%\" id=\"tabserinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>机构名</th>");
		sbHtml.append("<th>机构用户名</th>");
		sbHtml.append("<th>IP</th>");
		sbHtml.append("<th>经度</th>");
		sbHtml.append("<th>纬度</th>");
		sbHtml.append("<th>时间</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		String strServerID = "";
		String unitnamestr = null;
		String orgnamestr = null;
		String ipstr = null;
		String longitudestr = null;
		String latitudestr = null;

		StringBuilder sbHostID = new StringBuilder();
		while (iMap.hasNext()) {
			mapData = iMap.next();
			unitnamestr = String.valueOf(mapData.get("unitname") == null ? "" : mapData.get("unitname"));
			orgnamestr = String.valueOf(mapData.get("orgname") == null ? "" : mapData.get("orgname"));
			ipstr = String.valueOf(mapData.get("ip") == null ? "" : mapData.get("ip"));
			longitudestr = String.valueOf(mapData.get("longitude") == null ? "" : mapData.get("longitude"));
			latitudestr = String.valueOf(mapData.get("latitude") == null ? "" : mapData.get("latitude"));

			sbHtml.append("<tr id=\"tr").append(strServerID).append("\">");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(unitnamestr).append("</td>");

			sbHtml.append("<td class=\"tabcent\">").append(orgnamestr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(ipstr).append("</td>");

			sbHtml.append("<td class=\"tabcent\">").append(longitudestr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(latitudestr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(
					mapData.get("time") == null ? "" : Common.ConvertToDateTime(mapData.get("time").toString())))
					.append("</td>");
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
