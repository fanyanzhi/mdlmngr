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

import BLL.SignMngr;
import DAL.DBHelper;
import Util.Common;

/**
 * Servlet implementation class QueryTableHandlerServlet
 */
@WebServlet("/QueryTableHandler.do")
public class QueryTableHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QueryTableHandlerServlet() {
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
			strResult = getQueryCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getQueryData(request);
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

	protected String getQueryCount(HttpServletRequest request) {
		String txtDataBase = request.getParameter("db");
		String txtTableName = request.getParameter("tb");
		DBHelper dbHelper = null;
		int count = 0;
		try {
			if ("0".equals(txtDataBase) || txtDataBase.indexOf("mdlmngr") > -1) {
				dbHelper = DBHelper.GetInstance();
			} else {
				dbHelper = DBHelper.GetInstance("Behaviour");
			}
			if (txtTableName.indexOf("select ") > -1) {
				count = 0;
			} else {
				List<Map<String, Object>> lst = dbHelper.ExecuteQuery("select count(1) from " + txtTableName);
				if (lst != null && lst.size() > 0) {
					count = Integer.parseInt(lst.get(0).get("count").toString());
				}
			}
		} catch (Exception e) {

		}
		return String.valueOf(count);
	}

	protected String getQueryData(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String txtDataBase = request.getParameter("db");
		String txtTableName = request.getParameter("tb");
		String start = request.getParameter("start");
		String len = request.getParameter("len");
		List<Map<String, Object>> userSignList = null;
		Iterator<Map<String, Object>> iMap = userSignList.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append(
				"<table width=\"100%\" id=\"tabserinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>用户名</th>");
		sbHtml.append("<th>连续签到天数</th>");
		sbHtml.append("<th>累计签到天数</th>");
		sbHtml.append("<th>积分</th>");
		sbHtml.append("<th>最近签到时间</th>");
		sbHtml.append("<th>签到日志</th>");
		sbHtml.append("</tr>");
		int iNum = Integer.parseInt(start);
		while (iMap.hasNext()) {
			mapData = iMap.next();
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("username")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("scount")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("ssum")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("score")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">")
					.append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"UserSignDetail.do?uname=").append(mapData.get("username"))
					.append("\" class=\"view\" title=\"查看\"></a></td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
}
