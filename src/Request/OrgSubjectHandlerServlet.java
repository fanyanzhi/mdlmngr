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

import BLL.SubjectRecommendMngr;
import BLL.UploadMngr;
import Util.Common;

/**
 * Servlet implementation class OrgSubjectHandlerServlet
 */
@WebServlet("/OrgSubjectHandler.do")
public class OrgSubjectHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrgSubjectHandlerServlet() {
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
		response.setContentType("html/text;utf-8");
		String strResult = "";
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getSubjectOrgCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSubjectOrgList(request);
		} else if ("delorg".equals(request.getParameter("do"))) {
			strResult = delSubjectOrg(request);
		}
		if (!Common.IsNullOrEmpty(strResult)) {
			PrintWriter out = response.getWriter();
			out.write(strResult);
			out.flush();
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	protected String getSubjectOrgCount(HttpServletRequest request) {
		String subjectId = request.getParameter("subjectid");
		int count = SubjectRecommendMngr.getSubjectOrgCount(subjectId);
		return String.valueOf(count);
	}

	protected String getSubjectOrgList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String subjectId = request.getParameter("subjectid");
		String subjectName = request.getParameter("subjectname");
		String start = request.getParameter("start");
		String len = request.getParameter("len");
		List<Map<String, Object>> userSignList = SubjectRecommendMngr.getSubjectOrgList(subjectId,
				Integer.parseInt(start), Integer.parseInt(len));
		Iterator<Map<String, Object>> iMap = userSignList.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append(
				"<table width=\"100%\" id=\"tabsubjectorg\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>主题名称</th>");
		sbHtml.append("<th>机构账号</th>");
		sbHtml.append("<th>机构名称</th>");
		sbHtml.append("<th>操作</th>");
		sbHtml.append("</tr>");
		int iNum = Integer.parseInt(start);
		while (iMap.hasNext()) {
			mapData = iMap.next();
			sbHtml.append("<td><input name=\"chkorgname\" value=\"").append(mapData.get("orgname")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(subjectName).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("orgname")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("unitname")).append("</a></td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delSubjectOrg('")
					.append(String.valueOf(mapData.get("orgname")))
					.append("')\" class=\"del\" class=\"del\" title=\"删除\"></a></td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
	
	private String delSubjectOrg(HttpServletRequest request) throws ServletException, IOException {
		String strResult = "";
		String orgname = request.getParameter("orgname");
		String subjectid = request.getParameter("subjectid");
		if (Common.IsNullOrEmpty(orgname) || Common.IsNullOrEmpty(subjectid)) {
			return "0";
		}
		if (SubjectRecommendMngr.delSubjectOrg(orgname, subjectid)) {
			strResult = "1";
		} else {
			strResult = "0";
		}
		return strResult;
	}
}
