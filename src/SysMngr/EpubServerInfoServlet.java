package SysMngr;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.Pdf2Epub;
import Util.Common;

/**
 * Servlet implementation class EpubServerInfoServlet
 */
@WebServlet("/EpubServerInfo.do")
public class EpubServerInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EpubServerInfoServlet() {
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

		String strServerID = request.getParameter("sid");
		if (!Common.IsNullOrEmpty(strServerID)) {
			Map<String, Object> mapServer = Pdf2Epub.getEpubServerInfo(strServerID);
			request.setAttribute("ServerID", strServerID);
			request.setAttribute("ServerAdd", mapServer.get("host"));
			request.setAttribute("StatusPort", mapServer.get("statusport"));
			request.setAttribute("CmdPort", mapServer.get("cmdport"));
			request.setAttribute("Status", mapServer.get("status"));
		}

		request.getRequestDispatcher("/SysMngr/epubserverinfo.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strServerID = request.getParameter("hidserverid");
		String strHost = request.getParameter("txtServerAdd");
		String strCmdPort = request.getParameter("txtCmdPort");
		String strStatusPort = request.getParameter("txtStatPort");
		String strStatus = request.getParameter("radSatus");
		if (Common.IsNullOrEmpty(strHost) || Common.IsNullOrEmpty(strHost)) {
			request.setAttribute("errmsg", "请把信息补充完整");
			request.setAttribute("ServerID", strServerID);
			request.setAttribute("ServerAdd", strHost);
			request.setAttribute("StatusPort", strStatusPort);
			request.setAttribute("CmdPort", strCmdPort);
			request.setAttribute("Status", strStatus);
			request.getRequestDispatcher("/SysMngr/epubserverinfo.jsp").forward(request, response);
			return;
		}
		if (Common.IsNullOrEmpty(strServerID)) {
			if (Pdf2Epub.addEpubServerInfo(strHost, strStatusPort, strCmdPort, strStatus)) {
				response.sendRedirect("EpubServerList.do");
				return;
			} else {
				request.setAttribute("errmsg", "保存失败");
				request.setAttribute("ServerID", strServerID);
				request.setAttribute("ServerAdd", strHost);
				request.setAttribute("StatusPort", strStatusPort);
				request.setAttribute("CmdPort", strCmdPort);
				request.setAttribute("Status", strStatus);
				request.getRequestDispatcher("/SysMngr/epubserverinfo.jsp").forward(request, response);
			}
		} else {
			if (Pdf2Epub.updateEpubServerInfo(strServerID,strHost, strStatusPort, strCmdPort, strStatus)) {
				response.sendRedirect("EpubServerList.do");
				return;
			} else {
				request.setAttribute("errmsg", "保存失败");
				request.setAttribute("ServerID", strServerID);
				request.setAttribute("ServerAdd", strHost);
				request.setAttribute("StatusPort", strStatusPort);
				request.setAttribute("CmdPort", strCmdPort);
				request.setAttribute("Status", strStatus);
				request.getRequestDispatcher("/SysMngr/epubserverinfo.jsp").forward(request, response);
			}
		}
	}

}
