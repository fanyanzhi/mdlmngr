package orglib;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.OrgImageMngr;
@WebServlet("/OrgUploadImage.do")
public class OrgUploadImageServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String id = request.getParameter("id");
		if(id!=null&&!"".equals(id)){
			request.setAttribute("id", id);
			List<Map<String, Object>> list = OrgImageMngr.getActiveById(Integer.parseInt(id));
			request.setAttribute("title",list.get(0).get("title"));
			request.setAttribute("content", list.get(0).get("content"));
			request.setAttribute("appid", list.get(0).get("appid"));
			request.setAttribute("type", list.get(0).get("type"));
		}
		request.getRequestDispatcher("/orglib/orglogoimage.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}
	
}
