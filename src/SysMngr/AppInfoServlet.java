package SysMngr;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.AppInfoMngr;
import Util.Common;

/**
 * Servlet implementation class AppInfoServlet
 */
@WebServlet("/AppInfo.do")
public class AppInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppInfoServlet() {
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

		String id = request.getParameter("aid");
		Map<String, Object> mapAppInfo = null;
		if (!Common.IsNullOrEmpty(id)) {
			mapAppInfo = AppInfoMngr.getAppInfo(id);
			if (mapAppInfo != null) {
				request.setAttribute("appid", mapAppInfo.get("appid"));
				request.setAttribute("appkey", mapAppInfo.get("appkey"));
				request.setAttribute("status", mapAppInfo.get("status"));
				request.setAttribute("comment", mapAppInfo.get("comment"));
				request.setAttribute("fee", mapAppInfo.get("isfee"));
				request.setAttribute("sync", mapAppInfo.get("sync"));
				request.setAttribute("auth", mapAppInfo.get("auth"));
				request.setAttribute("activity", mapAppInfo.get("activity"));
				request.setAttribute("roam", mapAppInfo.get("roam"));
			}
		}
		request.setAttribute("id", id);
		request.getRequestDispatcher("/SysMngr/appinfo.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		String id = request.getParameter("hidid");
		String appId = request.getParameter("txtAppId");
		String appKey = request.getParameter("txtAppKey");
		String status = request.getParameter("txtStatus");
		String comment = request.getParameter("txtComment");
		String sync = request.getParameter("issync");
		String fee = request.getParameter("isfee");
		String auth = request.getParameter("isauth");
		String activity = request.getParameter("isactivity");
		String roam = request.getParameter("isroam");
		if (Common.IsNullOrEmpty(id)) {
			if (!AppInfoMngr.existAppId(appId)) {
				if (AppInfoMngr.addAppInfo(appId, appKey, fee, sync, status, auth, activity, roam, comment)) {
					response.sendRedirect("AppList.do");
					return;
				} else {
					request.setAttribute("errmsg", "保存失败");
				}
			} else {
				request.setAttribute("errmsg", "AppId已经存在");
			}
		} else {
			if (AppInfoMngr.updateAppInfo(appId, appKey, fee, sync, status, auth, activity, roam, comment)) {
				response.sendRedirect("AppList.do");
				return;
			} else {
				request.setAttribute("errmsg", "修改失败");
			}
		}
		request.setAttribute("id", id);
		request.setAttribute("appid", appId);
		request.setAttribute("appkey", appKey);
		request.setAttribute("status", status);
		request.setAttribute("fee", fee);
		request.setAttribute("sync", sync);
		request.setAttribute("auth", auth);
		request.setAttribute("activity", activity);
		request.setAttribute("activity", activity);
		request.setAttribute("roam", roam);
		request.getRequestDispatcher("/SysUserMngr/appinfo.jsp").forward(request, response);
	}

}
