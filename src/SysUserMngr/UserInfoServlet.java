package SysUserMngr;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.AppInfoMngr;
import BLL.UserInfoMngr;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class UserInfoServlet
 */
@WebServlet("/UserInfo.do")
public class UserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserInfoServlet() {
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

		String strUserID = request.getParameter("uid");
		if (strUserID != null) {
			UserLoginBean userBean = UserInfoMngr.getSysUserInfo(strUserID);
			if (userBean != null) {
				request.setAttribute("UserBean", userBean);
			} else {
				response.sendRedirect("UserList.do");
				return;
			}
		}
		Map<String, String> appMap = getAppID();
		request.setAttribute("drpappid", appMap);
		request.getRequestDispatcher("/SysUserMngr/userinfo.jsp").forward(request, response);
	}

	private Map<String, String> getAppID() {
		Map<String, String> appMap = new LinkedHashMap<String, String>();
		List<Map<String, Object>> lstAppID = AppInfoMngr.getAppInfoList();
		if (lstAppID == null || lstAppID.size() == 0) {
			appMap.put("暂无数据", "");
			return appMap;
		}
		Iterator<Map<String, Object>> iApp = lstAppID.iterator();
		Map<String, Object> iMap = null;
		while (iApp.hasNext()) {
			iMap = iApp.next();
			appMap.put(iMap.get("appid").toString(), iMap.get("appid").toString());
		}
		return appMap;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		UserLoginBean userBean = setUserBean(request);

		if (userBean.getId() == 0) {
			if (!UserInfoMngr.existsSysUser(userBean.getUserName())) {
				if (UserInfoMngr.addSysUserInfo(userBean)) {
					response.sendRedirect("UserList.do");
					return;
				} else {
					request.setAttribute("errmsg", "保存失败");
				}
			} else {
				request.setAttribute("errmsg", "用户名已经存在");
			}

		} else {
			UserLoginBean newUserBean = UserInfoMngr.getSysUserInfo(String.valueOf(userBean.getId()));
			boolean isPwdChanged = true;
			if (userBean.getPassword().equals(newUserBean.getPassword())) {
				isPwdChanged = false;
			}
			if (UserInfoMngr.updateSysUserInfo(userBean, isPwdChanged)) {
				response.sendRedirect("UserList.do");
				return;
			} else {
				request.setAttribute("errmsg", "修改失败");
			}
		}
		request.setAttribute("UserBean", userBean);
		request.getRequestDispatcher("/SysUserMngr/userinfo.jsp").forward(request, response);

	}

	private UserLoginBean setUserBean(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		UserLoginBean userBean = new UserLoginBean();
		if (request.getParameter("hiduid") != null && Common.Trim(request.getParameter("hiduid"), " ").length() > 0) {
			userBean.setId(Integer.parseInt(request.getParameter("hiduid")));
		}
		userBean.setUserName(Common.Trim(request.getParameter("txtUser"), " "));
		userBean.setPassword(Common.Trim(request.getParameter("txtPwd"), " "));
		userBean.setRole(Integer.parseInt(request.getParameter("isAdmin")));
		userBean.setComment(request.getParameter("txtComment"));
		userBean.setAppid(request.getParameter("selappid"));
		return userBean;

	}

}
