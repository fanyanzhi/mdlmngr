package Request;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.SubjectRecommendMngr;
import Model.SubjectRecommendInfoBean;
import Util.Common;

/**
 * Servlet implementation class AppListHandlerServlet
 */
@WebServlet("/SubjectRecommendHandler.do")
public class SubjectRecommendHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String txtTitle = request.getParameter("txtTitle");
		String txtWord = request.getParameter("txtWord");
		String txtType = request.getParameter("txtType");
		String strTop = request.getParameter("strTop");
		String txtadv = request.getParameter("isadv");
		String strOpenClass = request.getParameter("strOpen");
		String strLinkType = request.getParameter("strLinkType");
		String txtSummary = request.getParameter("txtSummary");
		String simageid = request.getParameter("simageid");
		String bimageid = request.getParameter("bimageid");
		String strRecomd = request.getParameter("strRecomd");
		SubjectRecommendInfoBean subject = new SubjectRecommendInfoBean();
		subject.setSubjectid(Common.GetDateTime("yyyyMMddhhmmss"));
		subject.setTitle(txtTitle);
		subject.setKeyword(txtWord);
		subject.setType(txtType);
		subject.setIstop(Integer.parseInt(strTop.trim()));
		subject.setIsadv(Integer.parseInt(txtadv.trim()));
		subject.setOpenclass(Integer.parseInt(strOpenClass.trim()));
		subject.setIsrecomd(Integer.parseInt(strRecomd.trim()));
		subject.setLinktype(Integer.parseInt(strLinkType.trim()));
		subject.setSummary(txtSummary);

		if (!Common.IsNullOrEmpty(simageid.trim())) {
			subject.setSimageid(Integer.parseInt(simageid.trim()));
		}
		if (!Common.IsNullOrEmpty(bimageid.trim())) {
			subject.setBimageid(Integer.parseInt(bimageid.trim()));
		}
		String sign = request.getParameter("sign");
		String id = request.getParameter("id");
		boolean result = false;
		if ("1".equals(sign)) {
			result = SubjectRecommendMngr.editSubjectInfo(subject, id);
		} else {
			result = SubjectRecommendMngr.addSubjectRecommend(subject);
		}

		if (result) {
			response.sendRedirect("/mdlmngr/SubjectList.do");
		} else {
			request.setAttribute("subject", subject);
			request.getRequestDispatcher("/mdlmngr/SubjectRecommend.do").forward(request, response);
		}
	}

}
