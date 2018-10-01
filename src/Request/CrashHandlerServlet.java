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

import BLL.AppInfoMngr;

/**
 * Servlet implementation class UserModuleContentListHandlerServlet
 */
@WebServlet("/CrashHandler.do")
public class CrashHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CrashHandlerServlet() {
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
			strResult = getAppCrashContentCount(request);
		}else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getAppCrashContentList(request);
		}else if("del".equals(request.getParameter("do"))){
			strResult =  delCrashRecord(request);
		}
		/*else if("delinfo".equals(request.getParameter("do"))){
			strResult=delModuleMessage(request);
		}*/
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

	private String getAppCrashContentCount(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		return String.valueOf(AppInfoMngr.getAppCrashContentCount(strStartDate, strEndDate));

	}

	private String getAppCrashContentList(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		StringBuilder sbHtml = new StringBuilder();
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		List<Map<String, Object>> lstAppCrash = null;
		lstAppCrash = AppInfoMngr.getAppCrashContentCount(strStartDate,strEndDate,iStart,iLength);
		if (lstAppCrash == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" id=\"tabcrash\" name=\"tabcrash\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20px\">&nbsp;</th>");
		sbHtml.append("<th width=\"40px\"  class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"10%\">设备信息</th>");
		sbHtml.append("<th width=\"10%\">设备型号</th>");
		sbHtml.append("<th>崩溃信息</th>");
		sbHtml.append("<th width=\"15%\">时间</th>");
		sbHtml.append("<th width=\"95px\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iAppCrash = lstAppCrash.iterator();
		Map<String, Object> iMapAppCrash = null;
		int iNum = iStart;
		while (iAppCrash.hasNext()) {
			iMapAppCrash = iAppCrash.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"appcrashid\" value=\"").append(iMapAppCrash.get("id")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMapAppCrash.get("appinfo")).append("</td>");
			sbHtml.append("<td>").append(iMapAppCrash.get("platform")==null?"":String.valueOf(iMapAppCrash.get("platform"))).append("</td>");
			String str = "";
			String strerrorinfo = String.valueOf(iMapAppCrash.get("errorinfo"));
			if(strerrorinfo!=null){
				if(strerrorinfo.length()>40){
					str = strerrorinfo.substring(0, 80).concat(" ......");
				}else{
					str = strerrorinfo;
				}
			}
			sbHtml.append("<td class=\"tabcent\">").append(str).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMapAppCrash.get("time") == null ? "" : String.valueOf(iMapAppCrash.get("time"))).append("</td>");
			sbHtml.append("<td><a href=\"AppCrashContentDetail.do?rid=").append(iMapAppCrash.get("id")).append("\" class=\"view\" title=\"查看\"></a><a href=\"javascript:void(0);\" onclick=\"delCrashRec(").append(iMapAppCrash.get("id")).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table><input type=\"hidden\" id=\"hidusers\" name=\"hidusers\" value=\"").append("").append("\" />");
		return sbHtml.toString();
	}
	
	private String delCrashRecord(HttpServletRequest request)throws ServletException, IOException {
		String strID = request.getParameter("acid");
		if (strID == null) {
			return "0";
		}
		strID = Common.Trim(strID, ",");
		if (AppInfoMngr.delCrashRecord(strID)) {
			return "1";
		} else {
			return "0";
		}
	}
	
}
