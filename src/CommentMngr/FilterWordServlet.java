package CommentMngr;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;

import BLL.CommentMngr;

/**
 * Servlet implementation class FilterWordServlet
 */
@WebServlet("/FilterWord.do")
public class FilterWordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FilterWordServlet() {
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

		Map<String, Object> mapWord = CommentMngr.getSensitiveWord();
		if (mapWord != null) {
			request.setAttribute("FilterWords", mapWord.get("words"));
			request.setAttribute("WordsID", mapWord.get("id"));
		}
		request.getRequestDispatcher("/CommentMngr/filterword.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		boolean bRet = false;
		String strWord = request.getParameter("keyword");
		String strID = request.getParameter("hidid");
		if (Common.IsNullOrEmpty(strID)) {
			if (Common.IsNullOrEmpty(strWord.trim())) {
				request.setAttribute("errmsg", "请填写敏感词汇");
			} else {
				bRet = CommentMngr.insertSensitiveWord(strWord);
			}
		} else {
			bRet = CommentMngr.updateSensitiveWord(strID, strWord);
		}
		if (bRet) {
			request.setAttribute("errmsg", "保存成功");
			response.sendRedirect("CommentList.do");
			return;
		} else {
			request.setAttribute("errmsg", "保存失败");
		}
		request.setAttribute("FilterWords", strWord);
		request.setAttribute("WordsID", strID);
		request.getRequestDispatcher("/CommentMngr/filterword.jsp").forward(request, response);
	}

}
