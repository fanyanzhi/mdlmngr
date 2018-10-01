package SourceMngr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;

import BLL.SourceMngr;

/**
 * Servlet implementation class SourceTypeInfoServlet
 */
@WebServlet("/SourceTypeInfo.do")
public class SourceTypeInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SourceTypeInfoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		String strTypeID = request.getParameter("stid");
		if (!Common.IsNullOrEmpty(strTypeID)) {
			List<Map<String, Object>> lstSourceType = SourceMngr.getSourceType(strTypeID);
			if (lstSourceType != null) {
				request.setAttribute("TypeID", strTypeID);
				request.setAttribute("FieldNameCH", lstSourceType.get(0).get("name_ch"));
				request.setAttribute("FieldNameEN", lstSourceType.get(0).get("name_en"));
				request.setAttribute("RelationOData", lstSourceType.get(0).get("nodataname"));


			}
		}
		request.setAttribute("HandlerURL", "SourceTypeInfoHandler.do");
		request.getRequestDispatcher("/SourceMngr/sourcetypeinfo.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
