package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.Logger;
import BLL.OrgImageMngr;

/**
 * Servlet implementation class ImgSrcHandlerServlet
 */
@WebServlet("/OrgImgSrcHandler")
public class OrgImgSrcHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrgImgSrcHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String strID = request.getParameter("id");
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
		List<Map<String, Object>> lstImageInfo = OrgImageMngr.getActiveById(iImageID);
		byte[] arrImg = null;
		if (lstImageInfo != null) {
			arrImg = (byte[]) lstImageInfo.get(0).get("active");
		}
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
