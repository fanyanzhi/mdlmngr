package UserMngr;

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;

/**
 * Servlet implementation class UserAliveListServlet
 */
@WebServlet("/UserAliveList.do")
public class UserAliveListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserAliveListServlet() {
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
		
		String strStartDate=Common.ConvertToDateTime(Common.GetDateTime(),"yyyy-MM-dd",(long)-24*3600*1000*31);
		String strEndDate=	Common.ConvertToDateTime(Common.GetDateTime(),"yyyy-MM-dd",(long)-24*3600*1000);
		
		request.setAttribute("StartDate", strStartDate);
		request.setAttribute("EndDate", strEndDate);
		request.setAttribute("HandlerURL", "UserAliveListHandler.do");
		request.getRequestDispatcher("/UserMngr/useralivelist.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
