package SysMngr;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 用户资料
 *
 */
@WebServlet("/UserInformation.do")
public class UserInformationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		request.setAttribute("PageSize", "20");
		request.setAttribute("HandlerURL", "UserInformationHandler.do");
		request.getRequestDispatcher("/UserMngr/userInformation.jsp").forward(request, response);
	}
}
