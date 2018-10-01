package UserMngr;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class OnlineUserDetailServlet
 */
@WebServlet("/OnlineUserDetail.do")
public class OnlineUserDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OnlineUserDetailServlet() {
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
		
		String strUserName=request.getParameter("uname");
		if(strUserName==null||strUserName.trim().length()==0){
			response.sendRedirect("OnlineUserList.do");
			return;
		}
		request.setAttribute("UserName", strUserName);
		request.setAttribute("Parameter","uname=".concat(strUserName));
		request.setAttribute("HandlerURL", "OnlineUserDetailHandler.do");
		
		request.getRequestDispatcher("/UserMngr/onlineuserdetail.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
