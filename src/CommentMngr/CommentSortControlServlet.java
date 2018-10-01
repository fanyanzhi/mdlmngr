package CommentMngr;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.CommentMngr;
import BLL.SourceMngr;

/**
 * Servlet implementation class CommentSortControlServlet
 */
@WebServlet("/CommentSortControl.do")
public class CommentSortControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommentSortControlServlet() {
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

		request.setAttribute("PageSize", "20");
		request.setAttribute("HandlerURL", "CommentSortControlHandler.do");
		request.setAttribute("SortHtml", getSortHtml(request));
		request.getRequestDispatcher("/CommentMngr/commentsortcontrol.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private String getSortHtml(HttpServletRequest request) {
		StringBuilder sbHtml = new StringBuilder();
		boolean bAllCloseStatus = CommentMngr.getAllClostStatus();

		sbHtml.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabcontrol\">");
		sbHtml.append("<tr>");
		if (bAllCloseStatus) {
			sbHtml.append("<th colspan=\"3\" class=\"clssall\"><em>全部文献</em> <span class=\"closeclass\" title=\"点击打开\" onclick=\"openAllComment();\"> </span></th>");
		} else {
			sbHtml.append("<th colspan=\"3\" class=\"clssall\"><em>全部文献</em> <span class=\"openclass\" title=\"点击关闭\" onclick=\"closeAllComment();\"> </span></th>");
		}
		sbHtml.append("</tr>");
		List<Map<String, Object>> lstType = SourceMngr.getSourceType();
		if (lstType == null) {
			sbHtml.append("</table>");
			return sbHtml.toString();
		}
		List<String> lstSort = null;
		boolean bFlag = false;
		if (!bAllCloseStatus) {
			lstSort = CommentMngr.getCommentSort();
			if (lstSort == null) {
				bFlag = true;
			}
		}
		Iterator<Map<String, Object>> iterator = lstType.iterator();
		Map<String, Object> mapData = null;
		int iFlag = 0;
		while (iterator.hasNext()) {
			mapData = iterator.next();
			String strTypeID=String.valueOf(mapData.get("name_en"));
			if (iFlag % 3 == 0) {
				sbHtml.append("<tr>");
			}
			if (bAllCloseStatus) {
				sbHtml.append("<td width=\"33%\"><em>").append(String.valueOf(mapData.get("name_ch"))).append("</em> <span title=\"请操作全部按钮\" class=\"closeclass\"></span></td>");
			} else {
				if (bFlag) {
					sbHtml.append("<td width=\"33%\"><em>").append(String.valueOf(mapData.get("name_ch"))).append("</em> <span title=\"点击关闭\" class=\"openclass\" onclick=\"closeSortComment(this,'").append(strTypeID).append("')\"></span></td>");
				} else {
					if (lstSort.contains(strTypeID)) {
						sbHtml.append("<td width=\"33%\"><em>").append(String.valueOf(mapData.get("name_ch"))).append("</em> <span title=\"点击打开\" class=\"closeclass\" onclick=\"openSortComment(this,'").append(strTypeID).append("')\"></span></td>");
					} else {
						sbHtml.append("<td width=\"33%\"><em>").append(String.valueOf(mapData.get("name_ch"))).append("</em> <span title=\"点击关闭\" class=\"openclass\" onclick=\"closeSortComment(this,'").append(strTypeID).append("')\"></span></td>");
					}
				}
			}
			if (iFlag % 3 == 2) {
				sbHtml.append("</tr>");
			}
			iFlag++;
		}
		if (!sbHtml.toString().endsWith("</tr>")) {
			if (iFlag % 3 == 1) {
				sbHtml.append("<td width=\"33%\"></td><td width=\"33%\"></td>");
			}
			if (iFlag % 3 == 2) {
				sbHtml.append("<td width=\"33%\"></td>");
			}
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
}
