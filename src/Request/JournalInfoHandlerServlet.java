package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class JournalInfoHandlerServlet
 */
@WebServlet("/JournalInfoHandler.do")
public class JournalInfoHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JournalInfoHandlerServlet() {
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
		if ("getissueinfo".equals(request.getParameter("do"))) {
			strResult = getIssueInfo(request);
		} else if ("remdwjournal".equals(request.getParameter("do"))) {
			strResult = recomandJournals(request);
		} else if ("disremdwjournal".equals(request.getParameter("do"))) {
			strResult = disRecomandJournals(request);
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
	protected String getIssueInfo(HttpServletRequest request) throws ServletException, IOException {
		String strJournalPY = request.getParameter("jpy");
		String strJournalYear = request.getParameter("year");
		String strTypeID = request.getParameter("vtypeid");
		String strCurIssue = request.getParameter("curissue");
		String strJournalName=request.getParameter("journalname");
		if (Common.IsNullOrEmpty(strJournalPY) || Common.IsNullOrEmpty(strJournalYear)) {
			return "";
		}
		String type = "JournalYearInfo";
		String fields = "Id,Issue";
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
					String aaa = (String)object.get("Name");
					if("Issue".equals(aaa)){
						String qihao = (String)object.get("Value");
						if(Common.IsNullOrEmpty(strCurIssue)){
							sbMonthInfo.append("<a href=\"javascrit:void(0);\" onclick=\"window.location.href='JournalMonthDetail.do?jpy=").append(strJournalPY).append("&yearinfo=").append(strJournalYear).append("&issueinfo=").append(qihao).append("&vtypeid=").append(strTypeID).append("&journalname=\'+encodeURI('").append(strJournalName).append("');return false;\" >第").append(qihao).append("期 </a>");
						}
						break;
					}
				}
			}
		}
		return sbMonthInfo.toString();
	}


	protected String recomandJournals(HttpServletRequest request) throws ServletException, IOException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strType = request.getParameter("vtypeid");
		String strFileID = request.getParameter("jpy");
		String strValue = request.getParameter("journalname");

		List<Map<String, String>> lstArticle = new ArrayList<Map<String, String>>();
		Map<String, String> mapArticle = new HashMap<String, String>();
		mapArticle.put("rid",strFileID); 
		mapArticle.put("rval",strValue);
		mapArticle.put("rtab","");
		lstArticle.add(mapArticle);
		if (Common.IsNullOrEmpty(strType) || strFileID.length() == 0) {
			return "0";
		}

		if (RecommendationInfoMngr.recommendArticles(appid, strType, strFileID, lstArticle)) {
			return "1";
		} else {
			return "0";
		}
	}

	protected String disRecomandJournals(HttpServletRequest request) throws ServletException, IOException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strType = request.getParameter("vtypeid");
		String strFileID = request.getParameter("jpy");

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

}
