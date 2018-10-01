package Request;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ImageMngr;
import BLL.Logger;

/**
 * Servlet implementation class ImgSrcHandlerServlet
 */
@WebServlet("/ImgSrcHandler")
public class ImgSrcHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImgSrcHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String strID = request.getQueryString();
		if (strID == null) {
			return;
		}
		strID = strID.trim();
		int iImageID = -1;
		try {
			iImageID = Integer.valueOf(strID);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iImageID == -1) {
			return;
		}
		byte[] arrImg = ImageMngr.getImageContent(iImageID);
		if (arrImg == null) {
			return;
		}
		response.setContentType("image/gif");
		response.getOutputStream().write(arrImg);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
