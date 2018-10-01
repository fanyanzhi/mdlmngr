package UserMngr;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginLogPicAnalysisServlet
 */
@WebServlet("/LoginLogPicAnalysis.do")
public class LoginLogPicAnalysisServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginLogPicAnalysisServlet() {
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
		StringBuilder sbParam=new StringBuilder();
	
		if(request.getParameter("uname")!=null){
			sbParam.append("uname=").append(request.getParameter("uname")).append("&");
		}
		if(request.getParameter("startdate")!=null){
			sbParam.append("startdate=").append(request.getParameter("startdate")).append("&");
		}
		if(request.getParameter("enddate")!=null){
			sbParam.append("enddate=").append(request.getParameter("enddate")).append("&");
		}
		if(request.getParameter("tmal")!=null){
			sbParam.append("tmal=").append(request.getParameter("tmal")).append("&");	
		}
		if(request.getParameter("singleTer")!=null){
			sbParam.append("singleTer=").append(request.getParameter("singleTer")).append("&");	
		}
		request.setAttribute("PageParam", sbParam.toString());
		request.setAttribute("HandlerURL", "LoginPic.do");
		request.getRequestDispatcher("/UserMngr/loginlogpicanalysis.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
