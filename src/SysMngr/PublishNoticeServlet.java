package SysMngr;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.NoticeInfoBean;
import Model.SubjectRecommendInfoBean;
import Util.Common;

import BLL.NoticeMngr;

/**
 * Servlet implementation class PublishNoticeServlet
 */
@WebServlet("/PublishNotice.do")
public class PublishNoticeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PublishNoticeServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		NoticeInfoBean noticeInfo = null;
		String nid = request.getParameter("nid");
		String strUserID = "";
		if (!Common.IsNullOrEmpty(nid)) {
			Map<String, Object> mapNoticeInfo = NoticeMngr.getNoticeInfo(nid);
			if (mapNoticeInfo != null) {
				noticeInfo = new NoticeInfoBean(mapNoticeInfo);
				if (0 == noticeInfo.getIsPublic()) {
					strUserID = getRelationUserID(String.valueOf(noticeInfo.getId()));
				}
			}
		}

		
		request.setAttribute("NoticeInfo", noticeInfo);
		request.setAttribute("UserID", strUserID);
		request.setAttribute("PageSize", 20);  //取统计用户用的
		request.setAttribute("HandlerURL", "StatisticAnalysisHandler.do"); //取统计用户用的，操作用户列表时用该url
		request.getRequestDispatcher("/SysMngr/publishnotice.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "1";
		
		PrintWriter out = response.getWriter();

		// String strUserId = request.getParameter("hiduid");
		// String strTitle = request.getParameter("txtTitle");
		// String strNotice = request.getParameter("txtNotice");

		NoticeInfoBean noticeInfo = setNoticeInfoBean(request);
		String strUserId = Common.Trim(request.getParameter("hiduname"), " ");
		if(noticeInfo.getId()==0){
			if(!NoticeMngr.publishNoticeInfo(noticeInfo,strUserId)){
				strResult="0";
			}
		}else{
			if(!NoticeMngr.updateNoticeInfo(noticeInfo,strUserId)){
				strResult="0";
			}
		}
		out.write(strResult);
		out.flush();
		out.close();

	}

	private String getRelationUserID(String NoticeID) {
		List<Map<String, Object>> lstUserID = NoticeMngr.getRelationUserID(NoticeID);
		StringBuilder sbUser = new StringBuilder();
		if (lstUserID == null) {
			return "";
		}
		for (Map<String, Object> map : lstUserID) {
			sbUser.append(map.get("username")).append(":").append(map.get("username")).append(",");
		}
		if (sbUser.length() > 0) {
			sbUser.delete(sbUser.length() - 1, sbUser.length());
		}
		return sbUser.toString();

	}

	private NoticeInfoBean setNoticeInfoBean(HttpServletRequest request) throws ServletException, IOException {
		NoticeInfoBean noticeInfo = new NoticeInfoBean();

		String strID = request.getParameter("hidnid");
		if (!Common.IsNullOrEmpty(strID)) {
			noticeInfo.setId(Integer.parseInt(strID));
		}
		noticeInfo.setNoticeid(Common.GetDateTime("yyyyMMddhhmmss"));
		noticeInfo.setTitle(Common.Trim(request.getParameter("txtTitle"), " "));
		noticeInfo.setType(Integer.valueOf(Common.Trim(request.getParameter("strNoticeType"), " ")));
		noticeInfo.setIsPublic(Integer.valueOf(Common.Trim(request.getParameter("strIsPublic"), " ")));
		noticeInfo.setContent(Common.Trim(request.getParameter("txtNotice"), " "));
		return noticeInfo;
	}


}
