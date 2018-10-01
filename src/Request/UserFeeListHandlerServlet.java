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

import BLL.UserFeeMngr;
import Util.Common;

/**
 * Servlet implementation class RecommendSearchListHandlerServlet
 */
@WebServlet("/UserFeeListHandler.do")
public class UserFeeListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public UserFeeListHandlerServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getUserFreeCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSearchData(request);
		} 
		out.write(strResult);
		out.flush();
		out.close();
	}

	protected String getUserFreeCount(HttpServletRequest request){
		String txtUserName = request.getParameter("txtUserName");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		int count = UserFeeMngr.getUserFreeCount(txtUserName, txtStartDate, txtEndDate);
		return String.valueOf(count);
	}
	
	protected String getSearchData(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String txtUserName = request.getParameter("txtUserName");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		String start = request.getParameter("start");
		String len = request.getParameter("len");
		float feeCount = UserFeeMngr.getUserFreeSum(txtUserName, txtStartDate, txtEndDate);
		sbHtml.append("<input id=\"feeCount\" type=\"hidden\" value= \"").append(feeCount).append("\"/>");
		List<Map<String, Object>> userFreeList = UserFeeMngr.getUserFreeList(txtUserName, txtStartDate, txtEndDate,Integer.parseInt(start), Integer.parseInt(len));
		Iterator<Map<String, Object>> iMap = userFreeList.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append("<table width=\"100%\" id=\"tabserinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>用户名</th>");
		sbHtml.append("<th>typeid</th>");
		sbHtml.append("<th>fileid</th>");
		sbHtml.append("<th>价格</th>");
		sbHtml.append("<th>时间</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		String strServerID = "";
		String usernamestr = null;
		String typeidstr = null;
		String fileidstr = null;
		String pricestr=null;
		
		StringBuilder sbHostID=new StringBuilder();
		while (iMap.hasNext()) {
			mapData = iMap.next();
			usernamestr = String.valueOf(mapData.get("username"));
			typeidstr = String.valueOf(mapData.get("typeid"));
			fileidstr = String.valueOf(mapData.get("fileid"));
			float price = (float)mapData.get("price");
			pricestr = String.valueOf(price);
			
			sbHtml.append("<tr id=\"tr").append(strServerID).append("\">");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(usernamestr).append("</a></td>");
			
			sbHtml.append("<td class=\"tabcent\">").append(typeidstr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(fileidstr).append("</td>");
			
			sbHtml.append("<td class=\"tabcent\">").append(pricestr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			//sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='EpubServerInfo.do?sid=").append(strServerID).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delEpubServer(").append(strServerID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		if(!Common.IsNullOrEmpty(sbHostID.toString())){
			sbHostID.delete(sbHostID.length()-1, sbHostID.length());
		}
		sbHtml.append("</table><input type=\"hidden\" name=\"hidtrs\" id=\"hidtrs\" value=\"").append(sbHostID.toString()).append("\" />");
		return sbHtml.toString();
	}
}
