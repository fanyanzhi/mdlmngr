package SysMngr;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.VersionBean;
import Util.Common;

import BLL.VersionMngr;

/**
 * Servlet implementation class VersionInfoServlet
 */
@WebServlet("/VersionInfo.do")
public class VersionInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VersionInfoServlet() {
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

		String strClientName = request.getParameter("client");
		if (!Common.IsNullOrEmpty(strClientName)) {
			VersionBean verBean = VersionMngr.getForceVersion(strClientName);
			request.setAttribute("isAdd", false);
			request.setAttribute("VersionBean", verBean);
		} else {
			request.setAttribute("isAdd", true);
		}
		request.getRequestDispatcher("/SysMngr/versioninfo.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		VersionBean verBean = new VersionBean();
		verBean.setClient(request.getParameter("txtclient"));
		verBean.setVersion(request.getParameter("txtversion"));
		verBean.setVersionName(request.getParameter("txtvername"));
		verBean.setApkUrl(request.getParameter("txtApkUrl"));
		verBean.setType(1);
		boolean isAdd = "1".equals(request.getParameter("hidadd")) ? true : false;
		if (Common.IsNullOrEmpty(verBean.getClient()) || Common.IsNullOrEmpty(verBean.getVersion())) {
			request.setAttribute("errmsg", "请把信息补充完整");
			request.setAttribute("VersionBean", verBean);
			request.getRequestDispatcher("/SysMngr/versioninfo.jsp").forward(request, response);
			return;
		}
		if (isAdd) {
			if (VersionMngr.addForceVersion(verBean)) {
				response.sendRedirect("VersionList.do");
				return;
			} else {
				request.setAttribute("errmsg", "保存失败");
				request.setAttribute("isAdd", true);
				request.setAttribute("VersionBean", verBean);
				request.getRequestDispatcher("/SysMngr/versioninfo.jsp").forward(request, response);
			}
		} else {
			if (VersionMngr.updateForceVersion(verBean)) {
				response.sendRedirect("VersionList.do");
				return;
			} else {
				request.setAttribute("errmsg", "保存失败");
				request.setAttribute("isAdd", false);
				request.setAttribute("VersionBean", verBean);
				request.getRequestDispatcher("/SysMngr/versioninfo.jsp").forward(request, response);
			}
		}

	}

}
