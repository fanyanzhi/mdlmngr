package SysMngr;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.UserInfoMngr;
import Util.Common;

/**
 * Servlet implementation class ClearUserTerminalServlet
 */
@WebServlet("/ClearUserTerminal.do")
public class ClearUserTerminalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClearUserTerminalServlet() {
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
		request.getRequestDispatcher("/SysMngr/clearuserterminal.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		boolean bRet = false;
		String strUser = request.getParameter("keyuser");
		
		if (Common.IsNullOrEmpty(strUser.trim())) {
			request.setAttribute("errmsg", "请填写要清空的用户信息");
		} else {
			if(!strUser.endsWith(";"))
				strUser = strUser + ";";
			bRet = UserInfoMngr.clearUserTerminal(strUser);
		}
		
		if (bRet) {
			request.setAttribute("errmsg", "清除成功");
		} else {
			request.setAttribute("errmsg", "清除失败");
		}
		request.setAttribute("keyuser", strUser);
		request.getRequestDispatcher("/SysMngr/clearuserterminal.jsp").forward(request, response);
	}

}
