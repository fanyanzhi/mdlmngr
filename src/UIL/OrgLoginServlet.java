package UIL;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import BLL.UserInfoMngr;
import Model.UserLoginBean;

/**
 * Servlet implementation class OrgLoginServlet
 */
@WebServlet("/OrgLogin/*")
public class OrgLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrgLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("PathInfo->"+request.getPathInfo());
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
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

}
