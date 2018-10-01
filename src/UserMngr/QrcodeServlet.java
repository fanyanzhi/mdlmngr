package UserMngr;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.QrcodeMngr;
import Util.Common;

@WebServlet("/qrcodegenerate")
public class QrcodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String password = "@9akj8ja3k9#-;jdiu$98JH-03H~kpb5";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		
		String action = request.getParameter("sw");
		if("success".equals(action)) {
			String p1 = request.getParameter("p1");
			String p2 = request.getParameter("p2");
			request.setAttribute("usertoken", p1);
			request.setAttribute("username", p2);
			request.getRequestDispatcher("/qrcode/success.jsp").forward(request, response);
			return ;
		}
		
		StringBuilder sbqrcode = new StringBuilder();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		long lcurtime = System.currentTimeMillis();
		String qrcode = sbqrcode.append("cnkiexpress@").append(uuid).append("@").append(lcurtime).toString();
		if (!QrcodeMngr.addQrcode(qrcode)) {
			request.getRequestDispatcher("/error.jsp").forward(request, response);
		}else {
			request.setAttribute("qrcode", Common.encodeAES(qrcode, password));
			request.getRequestDispatcher("/qrcode/generate.jsp").forward(request, response);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
