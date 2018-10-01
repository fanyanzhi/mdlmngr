package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.OrgHomePageMngr;

/**
 * Servlet implementation class RecommendEditServlet
 */
@WebServlet("/OrgHomePageEdit.do")
public class OrgHomePageEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrgHomePageEditServlet() {
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

		String id = request.getParameter("id");
		if (null == id) {
			request.getRequestDispatcher("/OrgMngr/orgHomePageAdd.jsp").forward(request, response);
		} else {
			List<Map<String, Object>> list = OrgHomePageMngr.getById(id);
			Map<String, Object> entity = null;
			if (list != null) {
				entity = list.get(0);
			}
			request.setAttribute("entity", entity);
			request.getRequestDispatcher("/OrgMngr/orgHomePageEdit.jsp").forward(request, response);
		}

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
		String unitname = request.getParameter("unitname");
		String orgname = request.getParameter("orgname");
		String weburl = request.getParameter("weburl");
		if (null==id) {
			OrgHomePageMngr.add(unitname, orgname, weburl);
		}else {
			
			OrgHomePageMngr.updateUrlById(id, unitname, orgname, weburl);
		}
		response.sendRedirect("OrgHomePageList.do");

	}

}
