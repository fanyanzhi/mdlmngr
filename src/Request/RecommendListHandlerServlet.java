package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.RecommendationInfoMngr;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class RecommendListHandlerServlet
 */
@WebServlet("/RecommendListHandler.do")
public class RecommendListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RecommendListHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getRecommendCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getRecommendList(request);
		} else if ("discommend".equals(request.getParameter("do"))) {
			strResult = disRecommendArticles(request);
		}
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

	protected String getRecommendCount(HttpServletRequest request) throws ServletException, IOException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strKeyWord = request.getParameter("kw");
		String strTypeID = request.getParameter("tid");
		return String.valueOf(RecommendationInfoMngr.getRecommendCount(appid, strKeyWord, strTypeID));
	}

	protected String getRecommendList(HttpServletRequest request) throws ServletException, IOException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strKeyWord = request.getParameter("kw");
		String strTypeID = request.getParameter("tid");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstRecommendInfo = RecommendationInfoMngr.getRecommendList(appid, strKeyWord, strTypeID, iStart, iLength);
		if (lstRecommendInfo == null) {
			return "";
		}
		Map<String, Map<String, String>> typeMap = getSearchType(request);
		if (typeMap == null) {
			return "";
		}

		StringBuilder sbHtml = new StringBuilder();
		Iterator<Map<String, Object>> iMap = lstRecommendInfo.iterator();

		Map<String, Object> mapData = null;
		sbHtml.append("<table width=\"100%\" id=\"tabarticle\" name=\"tabarticle\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th >名称</th>");
		sbHtml.append("<th width=\"10%\">来源库</th>");
		sbHtml.append("<th width=\"150\">推荐时间</th>");
		sbHtml.append("<th width=\"150\">操作</th>");
		sbHtml.append("</tr> ");
		int iNum = iStart;

		while (iMap.hasNext()) {
			mapData = iMap.next();

			String strDetailUrl = Common.GetConfig("RecommendDetailUrl");
			if (strDetailUrl == null) {
				strDetailUrl = "http://192.168.100.122/KCMS/detail/detail.aspx";
			}
			String sourceType=String.valueOf(typeMap.get(String.valueOf(mapData.get("typeid"))).get("name_en"));
			if ("journalinfo".equals(sourceType)) {
				strDetailUrl = "JournalInfo.do?jpy=".concat(String.valueOf(mapData.get("fileid"))).concat("&vtypeid=").concat(String.valueOf(mapData.get("typeid")));
			}
			else if("YNKX_BOOKINFO".equals(sourceType)|| "YNKX_CACM".equals(sourceType)||"YNKX_CACV".equals(sourceType)||"YNKX_PICINDEX".equals(sourceType)){
				strDetailUrl = "OrgInfo.do?opy=".concat(String.valueOf(mapData.get("fileid"))).concat("&vtypeid=").concat(String.valueOf(mapData.get("typeid")).concat("&sourceType=").concat(sourceType));
			} 
			else {
				strDetailUrl = strDetailUrl.concat("?dbcode=".concat(mapData.get("tablename") == null ? "" : String.valueOf(mapData.get("tablename")).substring(0, 4)).concat("&dbname=").concat(String.valueOf(mapData.get("tablename"))).concat("&filename=").concat(String.valueOf(mapData.get("fileid"))));// "ODataDetail.do?type=".concat("journals").concat("&fileid=");
			}
			if("1".equals(String.valueOf(mapData.get("important")))){
				sbHtml.append("<tr  class=\"importcom\">");
			}else{
				sbHtml.append("<tr>");
			}
			sbHtml.append("<td><input name=\"recmdid\" id=\"recmdid\" type=\"checkbox\" value=\"").append(mapData.get("id")).append("\" /></td>");
			sbHtml.append("<td class=\"num\">").append(String.valueOf(iNum++)).append("</td>");
			if("1".equals(String.valueOf(mapData.get("important")))){
				sbHtml.append("<td class=\"red\"><a href=\"javascript:void(0);\" onclick=\"window.location.href='RecommendEdit.do?rid=").append(mapData.get("id")).append("'\">").append(mapData.get("title")).append("</a><img src=\"./images/starpic.gif\" /></td>");
			}else{
				sbHtml.append("<td><a href=\"javascript:void(0);\" onclick=\"window.location.href='RecommendEdit.do?rid=").append(mapData.get("id")).append("'\">").append(mapData.get("title")).append("</a></td>");
			}
			sbHtml.append("<td>").append(typeMap.get(String.valueOf(mapData.get("typeid"))).get("name_ch")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			if (String.valueOf(typeMap.get(String.valueOf(mapData.get("typeid"))).get("name_en")).equals("journalinfo")) {
				sbHtml.append("<td class=\"tabopt\"><a title=\"查看详情\" class=\"view\" href=\"").append(strDetailUrl).append("\"> </a><a href=\"javascript:void(0);\" onclick=\"window.location.href=\'RecommendEdit.do?rid=").append(mapData.get("id")).append("'\" class=\"edit\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" class=\"del\" title=\"取消推荐\" onclick=\"DisRemdSingleArticle(").append(String.valueOf(mapData.get("id"))).append(")\"></a></td>");
			} else {
				sbHtml.append("<td class=\"tabopt\"><a target=\"_blank\" title=\"查看详情\" class=\"view\" href=\"").append(strDetailUrl).append("\"> </a><a href=\"javascript:void(0);\" onclick=\"window.location.href=\'RecommendEdit.do?rid=").append(mapData.get("id")).append("'\" class=\"edit\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" class=\"del\" title=\"取消推荐\" onclick=\"DisRemdSingleArticle(").append(String.valueOf(mapData.get("id"))).append(")\"></a></td>");
			}
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private String disRecommendArticles(HttpServletRequest request) throws ServletException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strFileID = request.getParameter("fileid");
		if (Common.IsNullOrEmpty(strFileID)) {
			return "0";
		}
		strFileID = Common.Trim(strFileID, ",");
		if (RecommendationInfoMngr.delRecommendArticles(appid, strFileID)) {
			return "1";
		} else {
			return "0";
		}
	}

	protected Map<String, Map<String, String>> getSearchType(HttpServletRequest request) {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		Map<String, Map<String, String>> typeMap = new HashMap<String, Map<String, String>>();
		List<Map<String, Object>> RecommendationType = RecommendationInfoMngr.getRecommendationTypeList(appid);
		if (RecommendationType == null) {
			return null;
		}
		Iterator<Map<String, Object>> iterator = RecommendationType.iterator();
		Map<String, Object> imap = null;
		Map<String, String> nameMap = null;
		while (iterator.hasNext()) {
			imap = iterator.next();
			nameMap = new HashMap<String, String>();
			nameMap.put("name_en", String.valueOf(imap.get("name_en")));
			nameMap.put("name_ch", String.valueOf(imap.get("name_ch")));
			typeMap.put(String.valueOf(imap.get("id")), nameMap);
		}
		return typeMap;
	}

}
