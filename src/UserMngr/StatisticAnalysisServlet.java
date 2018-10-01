package UserMngr;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ModuleMngr;

/**
 * Servlet implementation class StatisticAnalysisServlet
 */
@WebServlet("/StatisticAnalysis.do")
public class StatisticAnalysisServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatisticAnalysisServlet() {
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
		
		//List<Map<String,Object>> lstTerminal=ModuleMngr.getOperatorSystem();
		List<Map<String,Object>> lstTerminal=ModuleMngr.getOperatorSystemID();
		Map<String,String> mapTerminal=null;
		if(lstTerminal!=null){
			mapTerminal=new HashMap<String,String>();
			for(Map<String,Object> imap:lstTerminal){
				mapTerminal.put(String.valueOf(imap.get("baseosname")), String.valueOf(imap.get("baseosname")));
			}
		}
		request.setAttribute("Terminal", mapTerminal);	
		request.setAttribute("PageSize", 20);
		request.setAttribute("HandlerURL", "StatisticAnalysisHandler.do");
		request.getRequestDispatcher("/UserMngr/statisticanalysis.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
