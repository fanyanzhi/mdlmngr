package UIL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import BLL.ModuleMngr;
import BLL.UserInfoMngr;
import Model.UserLoginBean;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//writeLog(LoginServlet.class.getClassLoader().getResource("").getPath());
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strDomain = request.getContextPath();
		if("mdlmngr".equals(strDomain.substring(1).toLowerCase())){
			request.setAttribute("flag", "1");
		}else{
			request.setAttribute("flag", "0");
		}
		request.getRequestDispatcher("/login.jsp").forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strDomain = request.getContextPath();
		String strUserName = request.getParameter("txtUser");
		String strPassword = request.getParameter("txtPwd");
		String strValidateCode = request.getParameter("txtValidate");
		HttpSession session = request.getSession();

		if(session==null){
			System.out.println("seesion is null");
		}
		if (session.getAttribute("ValidateCode") == null || !strValidateCode.toLowerCase().equals(session.getAttribute("ValidateCode").toString().toLowerCase())) {
			UpdateSession(request);
			request.setAttribute("errmsg", "验证码错误!");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
			return;
		}
		UserLoginBean userBean = UserInfoMngr.sysUserLogin(strUserName, strPassword);
		if (userBean != null) {
			if (userBean.getRole() == 3) {
				if (!strDomain.substring(1).equals(userBean.getAppid())) {
					request.setAttribute("errmsg", "域名错误");
					request.getRequestDispatcher("/login.jsp").forward(request, response);
					return;
				}
			}
			UpdateSession(request);
			session = request.getSession();
			session.setAttribute("LoginObj", userBean);
			if (userBean.getRole() == 1) {
				response.sendRedirect("UserList.do?curmenu=01");
			} else if (userBean.getRole() == 2) {
				Map<String, String> mapModuleMenu = getModuleMenu();
				if (mapModuleMenu != null && mapModuleMenu.size() > 0) {
					Map<String, Map<String, String>> mapModuleTypeMenu = getModuleTypeMenu(mapModuleMenu);
					session.setAttribute("ModuleTypeMenu", mapModuleTypeMenu);
				}
				session.setAttribute("ModuleMenu", mapModuleMenu);
				response.sendRedirect("LoginLogList.do?curmenu=011");
			} else if (userBean.getRole() == 3) {
				response.sendRedirect("LoginLogList.do?curmenu=011");
			}
		} else {
			UpdateSession(request);
			request.setAttribute("errmsg", "用户名或密码错误");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}

	}

	private void UpdateSession(HttpServletRequest request) {
		request.getSession().invalidate();
		Cookie[] arrCookies = request.getCookies();
		if (arrCookies != null) {
			for (Cookie cookie : arrCookies) {
				cookie.setMaxAge(0);
			}
		}
		request.getSession().setAttribute("sessionid", request.getSession().getId());
	}

	private Map<String, String> getModuleMenu() {
		List<Map<String, Object>> lstModuleInfo = ModuleMngr.getDisplayModuleList();
		if (lstModuleInfo != null && lstModuleInfo.size() > 0) {
			Map<String, String> mapModuleMenu = new HashMap<String, String>(lstModuleInfo.size());
			for (Map<String, Object> mapTemp : lstModuleInfo) {
				mapModuleMenu.put(String.valueOf(mapTemp.get("id")), (String) mapTemp.get("tablename_ch"));
			}
			return mapModuleMenu;
		}
		return null;
	}

	private Map<String, Map<String, String>> getModuleTypeMenu(Map<String, String> ModuleList) {
		String strTableID;
		List<Map<String, Object>> lstModuleType;
		Map<String, String> mapTypeMenu = null;
		Map<String, Map<String, String>> mapModuleTypeMenu = new HashMap<String, Map<String, String>>();
		for (Entry<String, String> moduleInfo : ModuleList.entrySet()) {
			strTableID = moduleInfo.getKey();
			lstModuleType = ModuleMngr.getModuleTypeList(strTableID, "0");
			if (lstModuleType == null) {
				continue;
			}
			mapTypeMenu = new HashMap<String, String>();
			for (Map<String, Object> mapTemp : lstModuleType) {
				mapTypeMenu.put(String.valueOf(mapTemp.get("id")), (String) mapTemp.get("typename_ch"));
			}
			mapModuleTypeMenu.put(strTableID, mapTypeMenu);
		}
		return mapModuleTypeMenu;
	}
	
	public static void writeLog(String data) {
		File file = new File("d:\\path.txt");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter sucsessFile = new FileWriter(file, true);
			sucsessFile.write(data + "\r\n");
			sucsessFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}
}
