package Request;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.CnkiMngr;
import BLL.Logger;
import BLL.Pdf2Epub;

/**
 * Servlet implementation class BackgroundProcessServlet
 */
@WebServlet("/BackgroundProcess/*")
public class BackgroundProcessServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BackgroundProcessServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");

		String strAction = request.getPathInfo();
		String strResult = "";
		if (strAction == null) {
			return;
		}
		if ("examineerrorfile".equalsIgnoreCase(strAction.replace("/", ""))) {
			strResult = examineErrorFile();
		} else if ("transepub".equalsIgnoreCase(strAction.replace("/", ""))) {
			strResult = transEpub();
		} 
		PrintWriter pw=response.getWriter();
		pw.write(strResult);
		pw.flush();
		pw.close();

	}

	private String examineErrorFile() {
		Logger.WriteDownTraceLog("", "", "", 3, "examineerrorfile BackgroundProcess Start", 1);// 4）后台对错误的原文重新下载或者补录，用系统机构账号登陆失败
		CnkiMngr.examineErrorFile();
		return "1";
	}

	private String transEpub() {
		Logger.WriteDownTraceLog("", "", "", 3, "pdftransepub BackgroundProcess Start", 1);
		Pdf2Epub.pdfTransEpub();
		return "1";
	}

}
