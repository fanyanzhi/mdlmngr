package UserMngr;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginLogDetailServlet
 */
@WebServlet("/LoginLogDetail.do")
public class LoginLogDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginLogDetailServlet() {
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
		
		StringBuilder sbParam=new StringBuilder();
		
		//传值有点问题，是传一个还是传两个啊
		if(request.getParameter("tmal")!=null){
			sbParam.append("tmal=").append(request.getParameter("tmal")).append("&");
		}
		if(request.getParameter("startdate")!=null){
			sbParam.append("startdate=").append(request.getParameter("startdate")).append("&");
		}
		if(request.getParameter("enddate")!=null){
			sbParam.append("enddate=").append(request.getParameter("enddate")).append("&");
		}
		
		request.setAttribute("StrParam", sbParam.toString());
		request.setAttribute("UserName", strUserName);
		request.setAttribute("PageSize", 20);
		request.setAttribute("HandlerURL", "LoginLogDetailHandler.do");
		request.getRequestDispatcher("/UserMngr/loginlogdetail.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
