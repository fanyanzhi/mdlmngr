package Request;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;

import BLL.CommentMngr;

/**
 * Servlet implementation class CommentSortControlHandlerServlet
 */
@WebServlet("/CommentSortControlHandler.do")
public class CommentSortControlHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommentSortControlHandlerServlet() {
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
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("openall".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = openAllComment(request);
		} else if ("closenall".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = closeAllComment(request);
		} else if ("opensort".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = openSortComment(request);
		} else if ("closesort".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = closeSortComment(request);
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

	private String openAllComment(HttpServletRequest request) {
		if (CommentMngr.openAllComment()) {
			return "1";
		} else {
			return "0";
		}
	}

	private String closeAllComment(HttpServletRequest request) {
		if (CommentMngr.closeAllComment()) {
			return "1";
		} else {
			return "0";
		}
	}

	private String openSortComment(HttpServletRequest request) {
		String strTypeID = request.getParameter("typeid");
		if (Common.IsNullOrEmpty(strTypeID)) {
			return "0";
		}
		if (CommentMngr.openSortComment(strTypeID)) {
			return "1";
		} else {
			return "0";
		}
	}

	private String closeSortComment(HttpServletRequest request) {
		String strTypeID = request.getParameter("typeid");
		if (Common.IsNullOrEmpty(strTypeID)) {
			return "0";
		}
		if (CommentMngr.closeSortComment(strTypeID)) {
			return "1";
		} else {
			return "0";
		}
	}
}
