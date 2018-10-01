package RecommendMngr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.CnkiMngr;
import BLL.RecommendationInfoMngr;
import Util.Common;

/**
 * Servlet implementation class JournalMonthDetailServlet
 */
@WebServlet("/JournalMonthDetail.do")
public class JournalMonthDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JournalMonthDetailServlet() {
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
		
		String strJournalPY=request.getParameter("jpy");
		String strYearInfo=request.getParameter("yearinfo");
		String strIssueInfo=request.getParameter("issueinfo");
		String strTypeID=request.getParameter("vtypeid");
		String strJournalName=request.getParameter("journalname");
		if(Common.IsNullOrEmpty(strJournalPY)||Common.IsNullOrEmpty(strTypeID)||Common.IsNullOrEmpty(strYearInfo)||Common.IsNullOrEmpty(strIssueInfo)){
			response.sendRedirect("RecommendSearch.do");
			return;
		}
		
		getJournalYearInfo(request);
		boolean bRecomand=RecommendationInfoMngr.isHasRecommend(strTypeID,strJournalPY);
		request.setAttribute("JournalName", strJournalName);
		request.setAttribute("IsRecomand", bRecomand);
		request.setAttribute("JournalPY", strJournalPY);
		request.setAttribute("YearInfo", strYearInfo);
		request.setAttribute("IssueInfo", strIssueInfo);
		request.setAttribute("TypeID", strTypeID);
		request.setAttribute("PageSize", 20);

		request.setAttribute("HandlerURL", "JournalMonthDetailHandler.do");
		request.getRequestDispatcher("/RecommendMngr/journalmonthdetail.jsp").forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	protected void getJournalYearInfo(HttpServletRequest request) throws ServletException, IOException {
		String strTypeID=request.getParameter("vtypeid");
		String strJournalPY = request.getParameter("jpy");
		String strYearInfo=request.getParameter("yearinfo");
		if (Common.IsNullOrEmpty(strJournalPY)) {
			return;
		}
		String type = "JournalYearInfo";
		String fields = "Year";
		String query = "id eq "+strJournalPY;
		String group = "";
		// 排序依据
		String order = "YearIssue DESC";
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
				String name = (String)object.get("Name");
				if("Year".equals(name)){
					String year = (String)object.get("Value");
					if (!lstValue.contains(year)) {
						lstValue.add(year);
					}
				}
			}
		}
		Collections.sort(lstValue);
		if(lstValue.size()>0){
			maxYear=Integer.parseInt(lstValue.get(lstValue.size()-1));
		}
		for(int i=lstValue.size()-1;i>-1;i--){
			sbYearInfo.append("<a href=\"javascript:void(0);\" onclick=\"getInssueandData('").append(strJournalPY).append("','").append(lstValue.get(i)).append("','").append(strTypeID).append("')\">").append(lstValue.get(i)).append("年</a>");
		}
		sbYearInfo.append("<em class=\"classclose\"  title=\"关闭\" onclick=\"$('#divyear').hide();$('#divmonth').show();\" title=\"关闭\"></em>");
		if(!Common.IsNullOrEmpty(strYearInfo)){
			maxYear=Integer.parseInt(strYearInfo);
		}
		request.setAttribute("LatestYear", maxYear);
		request.setAttribute("YearsInfo", sbYearInfo.toString());
	}
}
