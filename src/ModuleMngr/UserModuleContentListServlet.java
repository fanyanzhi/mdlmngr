package ModuleMngr;

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ModuleMngr;

/**
 * Servlet implementation class UserModuleContentListServlet
 */
@WebServlet("/UserModuleContentList.do")
public class UserModuleContentListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserModuleContentListServlet() {
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
		
		
		String strModuleID=request.getParameter("mid");
		String strModuleType=request.getParameter("tid");
		String strParam="";
		
		if(strModuleID==null||!ModuleMngr.isValidModuleID(strModuleID)){
			response.sendRedirect("Error.do");
			return;
		}
		
		if(strModuleType==null){
			strParam="mid=".concat(strModuleID);
		}else{
			strParam="mid=".concat(strModuleID).concat("&tid=").concat(strModuleType);
		}
		
		request.setAttribute("PageParam", strParam);
		request.setAttribute("PageSize", "20");
		request.setAttribute("HandlerURL", "UserModuleContentListHandler.do");
		request.getRequestDispatcher("/ModuleMngr/usermodulecontentlist.jsp").forward(request, response);
		
		
		//List<Map<String,Object>> lstField="select TableID,FieldName,FieldName_CH from modulecontentinfo WHERE TableID=3 AND IsDisplay=1";
		
		//然后 sbFields="";,sbHtml
		
		//"select sbFields from "strModuleID_strModuleType" limit 0,30"
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
