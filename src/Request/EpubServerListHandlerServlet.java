package Request;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.Pdf2Epub;
import BLL.SocketMngr;
import Util.Common;

/**
 * Servlet implementation class EpubServerListHandlerServlet
 */
@WebServlet("/EpubServerListHandler.do")
public class EpubServerListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EpubServerListHandlerServlet() {
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
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("del".equals(request.getParameter("do"))) {
			strResult = delEpubServer(request);
		}else if("getstatus".equals(request.getParameter("do"))){
			strResult=getEpubServerSatus(request);
		}

		out.write(strResult);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private String delEpubServer(HttpServletRequest request) {
		String strServerID = request.getParameter("sid");
		if (Common.IsNullOrEmpty(strServerID)) {
			return "0";
		}
		if (Pdf2Epub.delEpubServer(Integer.parseInt(strServerID))) {
			return "1";
		} else {
			return "0";
		}
	}

	private String getEpubServerSatus(HttpServletRequest request){
		String strHost=request.getParameter("hostid");
		String strStatusPort = request.getParameter("portid");
		String strStatusRet = SocketMngr.sendSocketData(strHost.concat(":").concat(strStatusPort), "status -maxpoolsize -activecount -queuesize", 1000);
		if (!Common.IsNullOrEmpty(strStatusRet)) {
			strStatusRet=strStatusRet.replace("\r", "").replace("\n", ";");
		}else{
			strStatusRet="error;error;error";
		}
		return strStatusRet;
	}
}
