package SysMngr;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RechargeListServlet.do")
public class RechargeListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		request.setAttribute("PageSize", "20");
		request.setAttribute("HandlerURL", "RechargeListHandler.do");
		request.getRequestDispatcher("/SysMngr/rechargeList.jsp").forward(request, response);
	}
}
