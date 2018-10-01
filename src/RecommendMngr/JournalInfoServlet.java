package RecommendMngr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import BLL.Logger;
import BLL.RecommendationInfoMngr;
import BLL.UserInfoMngr;
import Model.JournalInfoBean;
import Util.Common;

/**
 * Servlet implementation class JournalInfoServlet
 */
@WebServlet("/JournalInfo.do")
public class JournalInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JournalInfoServlet() {
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

		String strJournalPY = request.getParameter("jpy");
		String strTypeID = request.getParameter("vtypeid");
		if (Common.IsNullOrEmpty(strJournalPY) || Common.IsNullOrEmpty(strTypeID)) {
			response.sendRedirect("RecommendSearch.do");
			return;
		}

		JournalInfoBean JournalInfo = getJournalInfo(request);
		if (JournalInfo == null) {
			response.sendRedirect("RecommendSearch.do");
			return;
		}
		getJournalYearInfo(request);
		boolean bRecomand = RecommendationInfoMngr.isHasRecommend(strTypeID, strJournalPY);
		request.setAttribute("ImageURL", getImageURL(request));
		request.setAttribute("IsRecomand", bRecomand);
		request.setAttribute("JournalInfo", JournalInfo);
		request.setAttribute("JournalPY", strJournalPY);
		request.setAttribute("TypeID", strTypeID);
		request.setAttribute("HandlerURL", "JournalInfoHandler.do");
		request.getRequestDispatcher("/RecommendMngr/journalinfo.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected JournalInfoBean getJournalInfo(HttpServletRequest request) throws ServletException, IOException {
		JournalInfoBean JournalInfo = null;
		String strJournalPY = request.getParameter("jpy");
		if (Common.IsNullOrEmpty(strJournalPY)) {
			return null;
		}
		String type="JournalInfo";
		String fields = "CitedTimes,Title,Title@EN,Author,Type,Address,Language,Size,ISSN,CN,MailCode,Impactfactor,CompositeImpactfactor";// "title,SubjectSubColumnCode,PageCount,PageRange,Date,Issue,year,TableName,FileSize,PhysicalTableName";
		JSONObject jsonSeaData = CnkiMngr.getSingleNewOdata(type, strJournalPY,fields);
		Map<String, Object> mapJournalInfo = new HashMap<String, Object>();
		JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("Rows"));
		JSONArray instanceArray = JSONArray.fromObject(JSONObject.fromObject(jsonArray.get(0)).get("Cells"));
		for (int i = 0; i < instanceArray.size(); i++) {
			JSONObject recordObj = JSONObject.fromObject(instanceArray.get(i));
			if(String.valueOf(recordObj.get("Name")).equals("Title@EN")){
				mapJournalInfo.put("titleen", String.valueOf(recordObj.get("Value")));
			}
			mapJournalInfo.put(String.valueOf(recordObj.get("Name")).toLowerCase(), String.valueOf(recordObj.get("Value")));
		}
		if (mapJournalInfo != null) {
			JournalInfo = new JournalInfoBean(mapJournalInfo);
		}
		return JournalInfo;
	}

	protected void getJournalYearInfo(HttpServletRequest request) throws ServletException, IOException {
			String strTypeID = request.getParameter("vtypeid");
			String strJournalPY = request.getParameter("jpy");
			String type = "JournalYearInfo";
			String fields = "Year";
			String query = "id eq "+strJournalPY;
			String group = "";
			// 排序依据
			String order = "YearIssue desc";
			JSONObject jsonData = CnkiMngr.getNewOdataInfo(type, fields, query, group, order, 0, 1);
			int length=(int) jsonData.get("Count");
			int start = 0;
			JSONObject jsonSeaData = CnkiMngr.getNewOdataInfo(type, fields, query, group, order, start, length);
			List<String> lstValue = new ArrayList<String>();
			StringBuilder sbYearInfo = new StringBuilder();
			Integer maxYear = null;
			JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("Rows"));
			for(int i=0;i<jsonArray.size();i++){
				JSONArray jsonArraytwo = JSONArray.fromObject(JSONObject.fromObject(jsonArray.get(i)).get("Cells"));
				for(int j=0;j<jsonArraytwo.size();j++){
					JSONObject object = JSONObject.fromObject(jsonArraytwo.get(j));
					String aaa = (String)object.get("Name");
					if("Year".equals(aaa)){
						String year = (String)object.get("Value");
						if (!lstValue.contains(year)) {
							lstValue.add(year);
						}
					}
				}
			}
			Collections.sort(lstValue);
			if (lstValue.size() > 0) {
				maxYear = Integer.parseInt(lstValue.get(lstValue.size() - 1));
			}
			for (int i = lstValue.size() - 1; i > -1; i--) {
				sbYearInfo.append("<a href=\"javascript:void(0);\" onclick=\"getIssueInfo('").append(strJournalPY).append("','").append(lstValue.get(i)).append("','").append(strTypeID).append("')\">").append(lstValue.get(i)).append("年</a>");
			}
			sbYearInfo.append("<em class=\"classclose\" title=\"关闭\" onclick=\"$('#divyear').hide();$('#divmonth').show();\" title=\"关闭\"></em>");
			request.setAttribute("LatestYear", maxYear);
			request.setAttribute("YearsInfo", sbYearInfo.toString());
	}
	protected String getImageURL(HttpServletRequest request) throws ServletException, IOException {
		//http://epub.cnki.net/fengmian/CJFD/big/HSJC.jpg
		String strJournalPY = request.getParameter("jpy");
		String strUrl = "http://api.cnki.net/image/journals/".concat(strJournalPY).concat("/big");

		// 获取用户Token
		String strSysToken = null;
		int iNum = 10;
		while (strSysToken == null) {
			strSysToken = UserInfoMngr.getUserToken();
			--iNum;
			if (iNum < 0) {
				return "\"images/nopic.png\"";
			}
		}
		try {
			return Common.getXmlSearchData(strUrl, strSysToken);
		} catch (IOException e) {
			Logger.WriteException(e);
		}
		return "\"images/nopic.png\"";

	}

}
