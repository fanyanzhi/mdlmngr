package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.AppealMngr;
import BLL.ScholarMngr;
import Model.AppealInfoBean;

/**
 * Servlet implementation class RecommendEditServlet
 */
@WebServlet("/AppealEdit.do")
public class AppealEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppealEditServlet() {
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
		List<Map<String, Object>> list = AppealMngr.getById(appealId);
		AppealInfoBean appealInfo = null;
		if (list != null) {
			appealInfo = new AppealInfoBean(list.get(0));
		}
		request.setAttribute("entity", appealInfo);
		request.getRequestDispatcher("/UserMngr/appealEdit.jsp").forward(request, response);

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
		String remark = request.getParameter("remark");
		ScholarMngr.updateAppealStatusById(id, status, remark);
		response.sendRedirect("AppealList.do");

	}

}
