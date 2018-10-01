package RecommendMngr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ImageMngr;
import BLL.RecommendationInfoMngr;
import Model.RecommendationInfoBean;

/**
 * Servlet implementation class RecommendEditServlet
 */
@WebServlet("/RecommendEdit.do")
public class RecommendEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RecommendEditServlet() {
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

		String strRemmandID = request.getParameter("rid");
		RecommendationInfoBean RecommendationInfo = null;
		List<Map<String, Object>> lstRecommendationInfo = RecommendationInfoMngr.getRecommendationInfo(strRemmandID);
		if (lstRecommendationInfo != null) {
			RecommendationInfo = new RecommendationInfoBean(lstRecommendationInfo.get(0));
		}
		request.setAttribute("ModuleID", "1");
		int iImgID = ImageMngr.getImageID(strRemmandID);
		if (-1 != iImgID) {
			request.setAttribute("ImageID", iImgID);
		}
		request.setAttribute("RecommendationInfo", RecommendationInfo);
		request.getRequestDispatcher("/RecommendMngr/recommendedit.jsp").forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		String strRemmandID = request.getParameter("hidrid");
		String strTitle = request.getParameter("hidtitle");
		String strDescription = request.getParameter("txtDescription");
		String strIsImportant = request.getParameter("keyrecom");
		String strOldImportant = request.getParameter("hidimport");

		RecommendationInfoBean RecommendationInfo = new RecommendationInfoBean();
		RecommendationInfo.setId(Integer.parseInt(strRemmandID));
		RecommendationInfo.setTitle(strTitle);
		RecommendationInfo.setDescription(strDescription);
		RecommendationInfo.setImportant(Integer.parseInt(strIsImportant));
		if (RecommendationInfoMngr.updateRecdationDescription(strRemmandID, strDescription, strIsImportant, strOldImportant)) {
			request.setAttribute("errmsg", "保存成功");
			response.sendRedirect("RecommendList.do");
			return;
		} else {
			request.setAttribute("errmsg", "保存失败");
		}
		request.setAttribute("ModuleID", "1");
		int iImgID = ImageMngr.getImageID(strRemmandID);
		if (-1 != iImgID) {
			request.setAttribute("ImageID", iImgID);
		}
		request.setAttribute("RecommendationInfo", RecommendationInfo);
		request.getRequestDispatcher("/RecommendMngr/recommendedit.jsp").forward(request, response);

	}

}
