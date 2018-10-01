package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.AdvertisementMngr;
import BLL.Logger;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class AdvertisementListHandlerServlet
 */
@WebServlet("/AdvertisementListHandler.do")
public class AdvertisementListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdvertisementListHandlerServlet() {
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
		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getVersionList(request);
		} else if ("del".equals(request.getParameter("do"))) {
			strResult = delAdvertisement(request);
		}

		out.write(strResult);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");

		PrintWriter out = response.getWriter();
		String strResult = "";

		strResult = AdvertisementList(request);
		
		out.write(strResult);
		out.flush();
		out.close();
	}
	
	private String AdvertisementList(HttpServletRequest request) throws ServletException, IOException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strFlag = request.getParameter("hidcount");
		int iFlag = 0;
		try {
			iFlag = Integer.parseInt(strFlag);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		List<Map<String, String>> adList=new ArrayList<Map<String, String>>();
		Map<String, String> mapAdInfo=null;
		for(int i=1;i<=iFlag;i++){
			mapAdInfo=new HashMap<String,String>();
			mapAdInfo.put("id", request.getParameter("hidid"+i));
			mapAdInfo.put("showorder",String.valueOf(i));
			adList.add(mapAdInfo);
		}
		if(AdvertisementMngr.saveAdVertisementOrder(appid, adList)){
			return "1";
		}else{
			return "0";
		}		
	}
	
	private String getVersionList(HttpServletRequest request) {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		List<Map<String, Object>> lstVersionInfo = AdvertisementMngr.getAdvertisementList(appid);
		if (lstVersionInfo == null) {
			return "";
		}
		Iterator<Map<String, Object>> iMap = lstVersionInfo.iterator();
		Map<String, Object> mapData = null;
		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<table width=\"100%\" id=\"tabadvinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\">&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>内容</th>");
		sbHtml.append("<th width=\"100\">广告图片</th>");
		sbHtml.append("<th width=\"120\">开始日期</th>");
		sbHtml.append("<th width=\"120\">结束日期</th>");
		sbHtml.append("<th width=\"200\">修改时间</th>");
		sbHtml.append("<th width=\"100\">操作</th>");
		sbHtml.append("</tr>");
		int iNum = 0;

		while (iMap.hasNext()) {
			mapData = iMap.next();
			iNum=iNum+1;
			String strAdvID = String.valueOf(mapData.get("id"));
			String strContent = String.valueOf(mapData.get("content"));
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0)\" class=\"downbtn\" title=\"向上\" onclick=\"ordergoup(this);\"></a></td> ");
			sbHtml.append("<td class=\"num\"><span name='tdnum'>").append(iNum).append("</span><input type=\"hidden\" id=\"hidid").append(iNum).append("\" name=\"hidid").append(iNum).append("\" value=\"").append(strAdvID).append("\" /></td>");
			sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"window.location.href='AddAdvertisement.do?aid=").append(strAdvID).append("'\" title=\"编辑\">").append(strContent.length()>50?strContent.substring(0, 50):strContent).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("imageid")==null?"无":"有").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("startdate")==null?"":String.valueOf(mapData.get("startdate"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("enddate")==null?"":String.valueOf(mapData.get("enddate"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='AddAdvertisement.do?aid=").append(strAdvID).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delAdvertisement(").append(strAdvID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table><input type=\"hidden\" name=\"hidcount\" id=\"hidcount\" value=\"").append(iNum).append("\"/>");
		return sbHtml.toString();
	}

	private String delAdvertisement(HttpServletRequest request) {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String advid = request.getParameter("advid");
		if (Common.IsNullOrEmpty(advid)) {
			return "0";
		}
		if (AdvertisementMngr.delAdvertisement(appid, Integer.parseInt(advid))) {
			return "1";
		} else {
			return "0";
		}
	}

}
