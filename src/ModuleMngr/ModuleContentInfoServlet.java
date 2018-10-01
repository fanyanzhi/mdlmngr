package ModuleMngr;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ModuleContentInfoServlet
 */
@WebServlet("/ModuleContentInfo.do")
public class ModuleContentInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ModuleContentInfoServlet() {
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
		String strTableID = request.getParameter("tabid");
		String strTableName=request.getParameter("tabname");
		if (strTableID != null && strTableID.length() > 0) {
			request.setAttribute("TableID", strTableID);
			request.setAttribute("ModuleName", strTableName);
		}else{
				response.sendRedirect("ModuleList.do");
				return;
		}
		request.setAttribute("HandlerURL", "ModuleContentInfoHandler.do");
		request.getRequestDispatcher("/ModuleMngr/modulecontentinfo.jsp").forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
