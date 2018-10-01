package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.IdentityAuthMngr;
import BLL.ScholarMngr;
import Model.AppealInfoBean;

/**
 * Servlet implementation class RecommendEditServlet
 */
@WebServlet("/IdentityAuthEdit.do")
public class IdentityAuthEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IdentityAuthEditServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		String appealId = request.getParameter("id");
		List<Map<String, Object>> list = IdentityAuthMngr.getById(appealId);
		AppealInfoBean appealInfo = null;
		if (list != null) {
			appealInfo = new AppealInfoBean(list.get(0));
		}
		request.setAttribute("entity", appealInfo);
		request.getRequestDispatcher("/UserMngr/identityAuthEdit.jsp").forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		String id = request.getParameter("id");
		String status = request.getParameter("status");
		ScholarMngr.updateAppealAuthById(id, status);
		response.sendRedirect("IdentityAuthList.do");

	}

}
