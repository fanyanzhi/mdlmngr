package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.CnkiMngr;
import BLL.RecommendationInfoMngr;
import BLL.SourceMngr;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class JournalMonthDetailHandlerServlet
 */
@WebServlet("/JournalMonthDetailHandler.do")
public class JournalMonthDetailHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JournalMonthDetailHandlerServlet() {
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
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getJournalsYearInfo(request);
		} else if ("commend".equals(request.getParameter("do"))) {
			strResult = recommendArticles(request);
		} else if ("discommend".equals(request.getParameter("do"))) {
			strResult = disRecommendArticles(request);
		} else if ("getissueinfo".equals(request.getParameter("do"))) {
			strResult = getIssueInfo(request);
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
	protected String getJournalsYearInfo(HttpServletRequest request) throws ServletException, IOException {
		int iStart = Integer.parseInt(request.getParameter("start"));
		String strJournalName = request.getParameter("journalname");
		String strYearInfo = request.getParameter("yearinfo");
		String strIssueInfo = request.getParameter("issueinfo");
		if (Common.IsNullOrEmpty(strJournalName) || Common.IsNullOrEmpty(strYearInfo)) {
			return "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
		}
		String type = "CJFD";
		String fields = "id,Creator,title,TableName";
		String query = "SourceCode eq '"+strJournalName+"' and YearIssue eq '"+strYearInfo+strIssueInfo+"'";
		String group = "";
		// 排序依据
		String order = "";
		int length = 20;
		JSONObject jsonSeaData = CnkiMngr.getNewOdataInfo(type, fields, query, group, order, iStart-1, length);
		String strRecordCount = String.valueOf(jsonSeaData.get("Count"));
		int pageCount = 0;
			int count = Integer.parseInt(strRecordCount);
			if(count>0&&count%length >0){
				pageCount=count/length+1;
			}else{
				pageCount=count/length;
			}
		String strPageCount = String.valueOf(pageCount);
		strRecordCount=String.valueOf(count);
		if ("0".equals(strRecordCount)) {
			return "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
		}
		StringBuilder sbHtml = new StringBuilder();
		boolean bFirst = false;
		if (iStart == 1) {
			bFirst = true;
			sbHtml.append("<table width=\"100%\" name=\"tabarticle\" id=\"tabarticle\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
			sbHtml.append("<tr>");
			sbHtml.append("<th width=\"20\" >&nbsp;</th>");
			sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
			sbHtml.append("<th>篇名</th>");
			sbHtml.append("<th>作者</th>");
			sbHtml.append("<th width=\"50\">推荐</th>");
			sbHtml.append("</tr>");
			sbHtml.append("<tbody id=\"tableBody\">");
		}
		JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("Rows"));
		List<Map<String, String>> lstArticle = new ArrayList<Map<String, String>>();
		for(int j=0;j<jsonArray.size();j++){
			JSONObject jsonData = JSONObject.fromObject(jsonArray.get(j));
			JSONArray jsonDataArray = JSONArray.fromObject(jsonData.get("Cells"));
			Map<String, String> keyVal = new LinkedHashMap<String, String>();
			for(int i=0;i<jsonDataArray.size();i++){
				JSONObject object = JSONObject.fromObject(jsonDataArray.get(i));
				String key = (String)object.get("Name");
				if (!keyVal.containsKey(key)) {
					keyVal.put(key, (String)object.get("Value"));
				}
			}
			lstArticle.add(keyVal);
		}
		// 此时推荐就会制约期刊，如果没有期刊就不能推荐
		String strTypeID = SourceMngr.getJournalID();
		boolean bFlag = false;
		List<String> lstRecomdInfo = RecommendationInfoMngr.getRecommendationFileID(strTypeID);
		for (int i = 0; i < lstArticle.size(); i++) {
			String tablename = lstArticle.get(i).get("TableName");
			String strInstance = String.valueOf(lstArticle.get(i).get("Id"));
			String strODataDetail = Common.GetConfig("RecommendDetailUrl");
			if (strODataDetail == null) {
				strODataDetail = "http://192.168.100.122/KCMS/detail/detail.aspx";
			}
			strODataDetail = strODataDetail.concat("?dbcode=").concat(tablename.substring(0, 4)).concat("&dbname=").concat(tablename).concat("&filename=").concat(strInstance);// "ODataDetail.do?type=".concat("journals").concat("&fileid=");

			if (lstRecomdInfo.contains(strInstance)) {
				bFlag = true;
			} else {
				bFlag = false;
			}
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chkfileid\" type=\"checkbox\" value=\"").append(strInstance).append("\"></td>");
			sbHtml.append("<td class=\"num\">").append(iStart++).append("<input type=\"hidden\" name=\"hidtablename\" value=\"").append(tablename == null ? "" : tablename).append("\"/></td>");
			if (bFlag) {
				sbHtml.append("<td><a target='_blank' href=\"").append(strODataDetail).append("\" ><span name='title'>").append(lstArticle.get(i).get("Title")).append("</span><em class=\"fine\">已推荐</em></a></td>");
			} else {
				sbHtml.append("<td><a target='_blank' href=\"").append(strODataDetail).append("\" ><span name='title'>").append(lstArticle.get(i).get("Title")).append("</span></a></td>");
			}
			sbHtml.append("<td>").append(lstArticle.get(i).get("Creator")).append("</td>");
			if (bFlag) {
				sbHtml.append("<td class=\"tabopt\"><a class=\"discommend\" title=\"取消推荐\" href=\"javascript:void(0);\" onclick=\"DisRemdJournalArticle(this,'").append(strInstance).append("',0)\"></a></td>");
			} else {
				sbHtml.append("<td class=\"tabopt\"><a title=\"推荐\" class=\"commend\" href=\"javascript:void(0);\" onclick=\"RemdJournalArticle(this,'").append(strInstance).append("',0)\"></a></td>");
			}
			sbHtml.append("</tr>");
		}
		if (bFirst) {
			sbHtml.append("</tbody>");
			sbHtml.append("</table><input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"").append(strRecordCount).append("\" /><input type=\"hidden\" id=\"hidpagecount\" name=\"hidpagecount\" value=\"").append(strPageCount).append("\" />");
		}
		return sbHtml.toString();

	}
	
	private String recommendArticles(HttpServletRequest request) throws ServletException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strType = SourceMngr.getJournalID();
		String strFileID = request.getParameter("fileid");
		StringBuilder sbFileID = new StringBuilder();
		JSONObject jsonObj = JSONObject.fromObject(strFileID);
		JSONArray recordArray = JSONArray.fromObject(jsonObj.get("jsondata"));
		List<Map<String, String>> lstArticle = new ArrayList<Map<String, String>>();
		Map<String, String> mapArticle = null;
		for (int i = 0; i < recordArray.size(); i++) {
			mapArticle = new HashMap<String, String>();
			mapArticle.put("rid", String.valueOf(recordArray.getJSONObject(i).get("rid")));
			mapArticle.put("rval", String.valueOf(recordArray.getJSONObject(i).get("rval")));
			mapArticle.put("rtab", String.valueOf(recordArray.getJSONObject(i).get("rtab")));
			lstArticle.add(mapArticle);
			sbFileID.append(String.valueOf(recordArray.getJSONObject(i).get("rid"))).append(",");
		}
		if (Common.IsNullOrEmpty(strType) || sbFileID.length() == 0) {
			return "0";
		}

		if (RecommendationInfoMngr.recommendArticles(appid, strType, sbFileID.delete(sbFileID.length() - 1, sbFileID.length()).toString(), lstArticle)) {
			return "1";
		} else {
			return "0";
		}
	}

	private String disRecommendArticles(HttpServletRequest request) throws ServletException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strType = SourceMngr.getJournalID();
		String strFileID = request.getParameter("fileid");

		if (Common.IsNullOrEmpty(strType) || Common.IsNullOrEmpty(strFileID)) {
			return "0";
		}
		strFileID = Common.Trim(strFileID, ",");
		if (RecommendationInfoMngr.disRecommendArticles(appid, strType, strFileID)) {
			return "1";
		} else {
			return "0";
		}
	}

	protected String getIssueInfo(HttpServletRequest request) throws ServletException, IOException {
		String strJournalPY = request.getParameter("jpy");
		String strJournalYear = request.getParameter("year");
		// String strTypeID = request.getParameter("vtypeid");
		String strCurIssue = request.getParameter("curissue");
		if (Common.IsNullOrEmpty(strJournalPY) || Common.IsNullOrEmpty(strJournalYear)) {
			return "";
		}
		String type = "JournalYearInfo";
		String fields = "Id,Issue,THNAME,Title,Year,YearIssue,YearId";
		String query = "id eq "+strJournalPY + " and year eq "+strJournalYear;
		String group = "";
		// 排序依据
		String order = "Issue DESC";
		int start = 0;
		int length = 40;
		StringBuilder sbMonthInfo = new StringBuilder();
		JSONObject jsonSeaData = CnkiMngr.getNewOdataInfo(type, fields, query, group, order, start, length);
		JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("Rows"));
		if(jsonArray.size()>0){
			for(int i=0;i<jsonArray.size();i++){
			JSONArray jsonArraytwo = JSONArray.fromObject(JSONObject.fromObject(jsonArray.get(i)).get("Cells"));
				for(int j=0;j<jsonArraytwo.size();j++){
					JSONObject object = JSONObject.fromObject(jsonArraytwo.get(j));
					String name = (String)object.get("Name");
					if("Issue".equals(name)){
						String strQi = String.valueOf(object.get("Value"));
						if (strQi.equals(strCurIssue)) {
							sbMonthInfo.append("<a href=\"javascript:void(0);\" class=\"current\" onclick=\"setInssueInfo(this,'").append(strQi).append("');\" >第").append(strQi).append("期 </a>");
						} else {
							sbMonthInfo.append("<a href=\"javascript:void(0);\" onclick=\"setInssueInfo(this,'").append(strQi).append("');\" >第").append(strQi).append("期 </a>");
						}
					}
				}
			}
		}
		return sbMonthInfo.toString();
	}

}
