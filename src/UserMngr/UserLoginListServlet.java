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
 * Servlet implementation class UserLoginListServlet
 */
@WebServlet("/UserLoginList.do")
public class UserLoginListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLoginListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		if (role != 3) {
			Map<String, String> appMap = getAppID();
			request.setAttribute("drpappid", appMap);
		}
		request.setAttribute("PageSize", 20);
		request.setAttribute("HandlerURL", "UserLoginListHander.do");
		request.getRequestDispatcher("/UserMngr/userloginlist.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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

}
