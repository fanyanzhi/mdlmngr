package UIL;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.Logger;

/**
 * Servlet implementation class ErrorServlet
 */
@WebServlet("/Error.do")
public class ErrorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ErrorServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");

		try {
			String url = (String) request.getAttribute("javax.servlet.error.request_uri");
			Integer status_code = (Integer) request.getAttribute("javax.servlet.error.status_code");
			Object excep = request.getAttribute("javax.servlet.error.exception");
			if (404 != status_code && 403 != status_code) {
				if (excep == null) {
					excep = new Exception(String.valueOf(status_code).concat(",").concat(url));
				}
				Logger.WriteException((Exception) excep);
			}
		} catch (Exception ex) {
			Logger.WriteException(ex);
		}
		request.getRequestDispatcher("error.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
