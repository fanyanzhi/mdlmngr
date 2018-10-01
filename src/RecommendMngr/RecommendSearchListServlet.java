package RecommendMngr;

import java.io.IOException;
import java.util.List;
import java.util.Map;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.SourceMngr;


/**
 * Servlet implementation class RecommendSearchListServlet
 */
@WebServlet("/RecommendSearchList.do")
public class RecommendSearchListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendSearchListServlet() {
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
		
		String strTypeID=request.getParameter("vtypeid");
		String strSeaFiled=request.getParameter("seafiled");
		String strKeyWord=request.getParameter("searchval");
		
		if(strTypeID==null){
			response.sendRedirect("RecommendSearch.do");
			return;
		}
		List<Map<String, Object>> lstType = SourceMngr.getSourceType(strTypeID);
		String strTypeCH ="";
		if (lstType != null) {
			strTypeCH = String.valueOf(lstType.get(0).get("name_ch"));
		}
		
		
		request.setAttribute("TypeID", strTypeID);
		request.setAttribute("SeaFiled", strSeaFiled);
		request.setAttribute("KeyWord", strKeyWord);
		request.setAttribute("TypeNameCH", strTypeCH);
		request.setAttribute("PageSize", 20);
		request.setAttribute("HandlerURL", "RecommendSearchListHandler.do");
		request.getRequestDispatcher("/RecommendMngr/recommendsearchlist.jsp").forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
