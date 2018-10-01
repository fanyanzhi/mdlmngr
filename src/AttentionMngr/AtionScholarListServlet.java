package AttentionMngr;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/AtionScholarList.do")
public class AtionScholarListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public AtionScholarListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		
		request.setAttribute("PageSize", "20");
		request.setAttribute("HandlerURL", "AtionScholarListHandler.do");
		request.getRequestDispatcher("/AttentionMngr/ationscholarlist.jsp").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet( request,  response);
	}

}
