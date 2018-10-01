package SysMngr;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.UserInfoMngr;
import Util.Common;

/**
 * Servlet implementation class TerminalNumInfoServlet
 */
@WebServlet("/TerminalNumInfo.do")
public class TerminalNumInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TerminalNumInfoServlet() {
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

		String strTernimalID = request.getParameter("tid");
		if (!Common.IsNullOrEmpty(strTernimalID)) {
			Map<String, Object> mapMaxCount = UserInfoMngr.getMaxOnlineInfo(strTernimalID);
			request.setAttribute("TerminalID", strTernimalID);
			request.setAttribute("TeminalNum", mapMaxCount.get("onlinecount"));
			request.setAttribute("Client", mapMaxCount.get("client"));
		}

		request.getRequestDispatcher("/SysMngr/terminalnuminfo.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strClient = request.getParameter("txtclient");
		String strTenimalNum = request.getParameter("txtTenimalNum");
		String strTeminalID = request.getParameter("hidternimalid");
		if (Common.IsNullOrEmpty(strClient) || Common.IsNullOrEmpty(strTenimalNum)) {
			request.setAttribute("errmsg", "请把信息补充完整");
			request.setAttribute("TeminalNum", strTenimalNum);
			request.setAttribute("Client", strClient);
			request.getRequestDispatcher("/SysMngr/terminalnuminfo.jsp").forward(request, response);
			return;
		}
		if (Common.IsNullOrEmpty(strTeminalID)) {
			if (UserInfoMngr.addMaxOnlineCount(Integer.parseInt(strTenimalNum), strClient)) {
				response.sendRedirect("TerminalNumList.do");
				return;
			} else {
				request.setAttribute("errmsg", "保存失败");
				request.setAttribute("isAdd", true);
				request.setAttribute("TeminalNum", strTenimalNum);
				request.setAttribute("Client", strClient);
				request.getRequestDispatcher("/SysMngr/terminalnuminfo.jsp").forward(request, response);
			}
		} else {
			if (UserInfoMngr.updateMaxOnlineCount(Integer.parseInt(String.valueOf(strTeminalID)), Integer.parseInt(strTenimalNum), strClient)) {
				response.sendRedirect("TerminalNumList.do");
				return;
			} else {
				request.setAttribute("errmsg", "保存失败");
				request.setAttribute("isAdd", false);
				request.setAttribute("TeminalNum", strTenimalNum);
				request.setAttribute("Client", strClient);
				request.getRequestDispatcher("/SysMngr/terminalnuminfo.jsp").forward(request, response);
			}
		}
	}

}
