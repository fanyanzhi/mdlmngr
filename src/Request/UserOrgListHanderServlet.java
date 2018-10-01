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

import BLL.UserFirstLogMngr;
import BLL.UserOrgMngr;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class UserOrgListHanderServlet
 */
@WebServlet("/UserOrgListHander.do")
public class UserOrgListHanderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserOrgListHanderServlet() {
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
			strResult = getUserOrgCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getUserOrgList(request);
		} else if ("delorg".equals(request.getParameter("do"))) {
			strResult = delUserOrg(request);
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
	
	private String delUserOrg(HttpServletRequest request) throws ServletException, IOException {
		String strResult = "";
		String recId = request.getParameter("recid");
		if (recId == null || Common.Trim(recId, " ").length() == 0) {
			return "0";
		}
		if (recId.endsWith(",")) {
			recId = Common.Trim(recId, ",");
		}

		if (UserOrgMngr.delUserOrg(recId)) {
			strResult = "1";
		} else {
			strResult = "0";
		}
		return strResult;
	}
	
	private String getUserOrgCount(HttpServletRequest request) throws ServletException, IOException {
		String strUserName = request.getParameter("uname");
		String strUnitName=request.getParameter("unitname");
		String strOrg = request.getParameter("org");
		return String.valueOf(UserOrgMngr.getUserOrgCount(strUserName, strUnitName, strOrg));
	}
	
	private String getUserOrgList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String strUserName = request.getParameter("uname");
		String strUnitName=request.getParameter("unitname");
		String strOrg = request.getParameter("org");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstUserOrg = null;
		lstUserOrg = UserOrgMngr.getUserOrgList(strUserName, strUnitName, strOrg, iStart, iLength);
		if (lstUserOrg == null) {
			return "";
		}
		
		sbHtml.append("<table width=\"100%\" id=\"tablogs\" name=\"tablogs\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th  width=\"10%\">用户名</th>");
		sbHtml.append("<th width=\"100\">机构名</th>");
		sbHtml.append("<th>ip地址</th>");
		sbHtml.append("<th width=\"80\">机构账号</th>");
		sbHtml.append("<th width=\"12%\">经度</th>");
		sbHtml.append("<th width=\"12%\">纬度</th>");
		sbHtml.append("<th width=\"10%\">绑定时间</th>");
		sbHtml.append("<th width=\"10%\">修改时间</th>");
		sbHtml.append("<th width=\"3%\">天数</th>");
		sbHtml.append("<th width=\"40\">密码登录</th>");
		sbHtml.append("<th width=\"8%\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iUser = lstUserOrg.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;
		while (iUser.hasNext()) {
			iMap = iUser.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chklogid\" value=\"").append(iMap.get("id")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td>").append(iMap.get("unitname") == null ? "&nbsp" : String.valueOf(iMap.get("unitname"))).append("</td>");
			
			sbHtml.append("<td>").append(iMap.get("ip") == null ? "&nbsp" : String.valueOf(iMap.get("ip"))).append("</td>");
			sbHtml.append("<td>").append(iMap.get("orgname") == null ? "&nbsp" : String.valueOf(iMap.get("orgname"))).append("</td>");
			sbHtml.append("<td>").append(iMap.get("longitude") == null ? "&nbsp" : String.valueOf(iMap.get("longitude"))).append("</td>");
			sbHtml.append("<td>").append(iMap.get("latitude") == null ? "&nbsp" : String.valueOf(iMap.get("latitude"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("updatetime")))).append("</td>");
			sbHtml.append("<td>").append(iMap.get("days") == null ? "&nbsp" : String.valueOf(iMap.get("days"))).append("</td>");
			sbHtml.append("<td>").append(iMap.get("orgpwd") == null ? "no" : String.valueOf(iMap.get("orgpwd")).length()==0?"no":"yes").append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delUserOrg(").append(iMap.get("id")).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

}
