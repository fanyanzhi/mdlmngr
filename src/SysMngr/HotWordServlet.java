package SysMngr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;
import BLL.HotWordMngr;

@WebServlet("/HotWord.do")
public class HotWordServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strhwid = request.getParameter("hwid");
		request.setAttribute("hwid", strhwid);
		List<Map<String, Object>> list = HotWordMngr.getHotWordDetail(strhwid);
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(list.size() - 1);
			request.setAttribute("type", map.get("type").toString());
			request.setAttribute("hotword", map.get("hotword").toString());
			request.setAttribute("hid", strhwid);
		}
		request.getRequestDispatcher("/SysMngr/hotword.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		
		String hidid = request.getParameter("hidid");
		String type = request.getParameter("txtType");
		String keyword = request.getParameter("keyword");
		
		if (Common.IsNullOrEmpty(hidid) && !Common.IsNullOrEmpty(keyword)) {
			if(HotWordMngr.addHotWordInfo(type, keyword)){
				response.sendRedirect("HotwordList.do");
				return;
			}else{
				request.setAttribute("type", type);
				request.setAttribute("hotword", keyword);
				request.setAttribute("errmsg", "添加失败");
				request.getRequestDispatcher("/SysMngr/hotword.jsp").forward(request, response);
				return;
			}
		}else{
			if( HotWordMngr.updateImageInfo(hidid, type, keyword)){
				response.sendRedirect("HotwordList.do");
				return;
			}else{
				request.setAttribute("type", type);
				request.setAttribute("hotword", keyword);
				request.setAttribute("hidid", hidid);
				request.setAttribute("errmsg", "更新失败");
				request.getRequestDispatcher("/SysMngr/hotword.jsp").forward(request, response);
				return;
			}
		}
	}

}
