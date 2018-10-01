package SysMngr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.AdvertisementMngr;
import Model.AdvertisementInfoBean;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class AddAdvertisementServlet
 */
@WebServlet("/AddAdvertisement.do")
public class AddAdvertisementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddAdvertisementServlet() {
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
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strAdvid = request.getParameter("aid");
		request.setAttribute("AdvID", strAdvid);
		request.setAttribute("ModuleID", "3");
		AdvertisementInfoBean AdvertisementInfo = null;
		List<Map<String, Object>> lstAdvertisementInfo = AdvertisementMngr.getAdvertisementInfo(appid, strAdvid);
		if (lstAdvertisementInfo != null) {
			AdvertisementInfo = new AdvertisementInfoBean(lstAdvertisementInfo.get(0));
		}

		request.setAttribute("AdvertisementInfo", AdvertisementInfo);
		request.getRequestDispatcher("/SysMngr/addadvertisement.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strAdvID = request.getParameter("hidaid");
		String strContent = request.getParameter("txtContent");
		String strType = request.getParameter("strType");
		String strImageID = request.getParameter("imgid");
		String strStartDate = request.getParameter("txtStartDate");
		String strEndDate = request.getParameter("txtEndDate");

		AdvertisementInfoBean AdvertisementInfo = new AdvertisementInfoBean();
		AdvertisementInfo.setAppid(appid);
		AdvertisementInfo.setContent(strContent);
		if (!Common.IsNullOrEmpty(strImageID)) {
			AdvertisementInfo.setImageId(Integer.parseInt(strImageID));
		}
		AdvertisementInfo.setType(Integer.parseInt(strType));
		if (!Common.IsNullOrEmpty(strStartDate)) {
			AdvertisementInfo.setStartDate(strStartDate);
		}
		if (!Common.IsNullOrEmpty(strEndDate)) {
			AdvertisementInfo.setEndDate(strEndDate);
		}

		if (!Common.IsNullOrEmpty(strAdvID)) {
			AdvertisementInfo.setId(Integer.parseInt(strAdvID));
			if (AdvertisementMngr.updateAdvertisement(AdvertisementInfo)) {
				request.setAttribute("errmsg", "添加成功");
				response.sendRedirect("AdvertisementList.do");
				return;
			} else {
				request.setAttribute("errmsg", "添加失败");
			}

		} else {
			if (AdvertisementMngr.addAdvertisement(AdvertisementInfo)) {
				request.setAttribute("errmsg", "更新成功");
				response.sendRedirect("AdvertisementList.do");
				return;
			} else {
				request.setAttribute("errmsg", "更新失败");
			}
		}
		request.setAttribute("ModuleID", "3");
		request.setAttribute("AdvertisementInfo", AdvertisementInfo);
		request.getRequestDispatcher("/SysMngr/addadvertisement.jsp").forward(request, response);
	}

}
