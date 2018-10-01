package UserMngr;

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
import Model.UserLoginBean;

/**
 * Servlet implementation class RegistUserList
 */
@WebServlet("/RegistUserList.do")
public class RegistUserList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		
		request.setAttribute("PageSize", 20);
		request.setAttribute("HandlerURL", "RegistUserListHandler.do");
		
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		if (role != 3) {
			Map<String, String> appMap = getAppID();
			request.setAttribute("drpappid", appMap);
		}
		request.getRequestDispatcher("/UserMngr/registuserlist.jsp").forward(request, response);
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
