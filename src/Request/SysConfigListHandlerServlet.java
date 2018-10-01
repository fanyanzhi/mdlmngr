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

import Util.Common;

import BLL.SysConfigMngr;


/**
 * Servlet implementation class SysConfigHandlerServlet
 */
@WebServlet("/SysConfigListHandler.do")
public class SysConfigListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SysConfigListHandlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";

		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getSysConfigCount(request);
		}else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSysConfigList(request);
		}else if ("delsysinfo".equals(request.getParameter("do"))) {
			strResult = delSysInfo(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	protected String getSysConfigCount(HttpServletRequest request) throws ServletException, IOException {
		return String.valueOf(SysConfigMngr.getConfigCount());
	}

	protected String getSysConfigList(HttpServletRequest request) throws ServletException, IOException {
		int iStart=Integer.parseInt(request.getParameter("start"));
		int iLength=Integer.parseInt(request.getParameter("len"));
		
		List<Map<String, Object>> lstSysInfo=SysConfigMngr.getConfigList(iStart,iLength);
		if (lstSysInfo == null) {
			return "";
		}
		Iterator<Map<String, Object>> iMap = lstSysInfo.iterator();
		Map<String, Object> mapData = null;
		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<table width=\"100%\" id=\"tabsysinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"200\">名称</th>");
		sbHtml.append("<th>值</th>");
		sbHtml.append("<th>修改时间</th>");
		sbHtml.append("<th width=\"100\"> 操作</th>");
		sbHtml.append(" </tr>");
		int iNum = iStart;

		while (iMap.hasNext()) {
			mapData = iMap.next();
			String strSysID = String.valueOf(mapData.get("id"));
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chksysid\" value=\"").append(strSysID).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"window.location.href='SysConfigInfo.do?sid=").append(strSysID).append("'\" title=\"编辑\">").append(String.valueOf(mapData.get("name"))).append("</a></td>");
			sbHtml.append("<td> ").append(String.valueOf(mapData.get("value"))).append("</td>");
			sbHtml.append("<td> ").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='SysConfigInfo.do?sid=").append(strSysID).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delSysInfo(").append(strSysID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
	
	protected String delSysInfo(HttpServletRequest request) throws ServletException, IOException {
		String strSysID = request.getParameter("sid");
		if (Common.IsNullOrEmpty(strSysID)) {
			return "0";
		}
		strSysID=Common.Trim(strSysID, ",");
		if (SysConfigMngr.deleteConfigInfo(strSysID)) {
			return "1";
		} else {
			return "0";
		}
	}
}
