package RecommendMngr;


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.RecommendationInfoMngr;
import Model.UserLoginBean;


/**
 * Servlet implementation class RecommendServlet
 */
@WebServlet("/RecommendList.do")
public class RecommendListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RecommendListServlet() {
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
		response.setContentType("text/html;utf-8");

//		String type=request.getParameter("vtype");
//		String keyWord=request.getParameter("searchVal");
//		
//		if(type==null){
//			response.sendRedirect("RecommendSearch.do");
//			return;
//		}
//		
//		request.setAttribute("Type", type);
//		request.setAttribute("KeyWord", keyWord);
		
		Map<String, String> typeMap=getSearchType(request);
		request.setAttribute("drpSeaType", typeMap);
		
		request.setAttribute("PageSize", 20);
		request.setAttribute("HandlerURL", "RecommendListHandler.do");
		request.getRequestDispatcher("/RecommendMngr/recommendlist.jsp").forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	
	protected Map<String, String> getSearchType(HttpServletRequest request) {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		Map<String, String> typeMap=new LinkedHashMap<String,String>();
		List<Map<String, Object>> RecommendationType = RecommendationInfoMngr.getRecommendationTypeList(appid);
		if (RecommendationType == null) {
			typeMap.put("暂无数据", "");
			return null;
		}else{
			typeMap.put("全部", "");
		}
		Iterator<Map<String, Object>> iterator = RecommendationType.iterator();
		Map<String, Object> imap = null;
		while (iterator.hasNext()) {
			imap = iterator.next();
			typeMap.put(String.valueOf(imap.get("name_ch")),String.valueOf(imap.get("id")));
		}
		return typeMap;
	}
	
	
}
