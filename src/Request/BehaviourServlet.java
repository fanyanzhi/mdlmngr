package Request;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import BLL.BehaviourMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;

/**
 * Servlet implementation class BehaviourServlet
 */
@WebServlet("/behaviour/*")
public class BehaviourServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BehaviourServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");

		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\"}"));
			return;
		}

		byte[] arrReq = new byte[request.getContentLength()];
		DataInputStream dataInput = new DataInputStream(request.getInputStream());
		dataInput.readFully(arrReq);
		request.getInputStream().close();
		dataInput.close();

		String strReq = new String(arrReq, "utf-8");
		arrReq = null;

		JSONObject jo = JSONObject.fromObject(strReq);
		Map<String, Object> mapInfo = (Map<String, Object>) jo;
		String strToken = (String) mapInfo.get("usertoken");
		String strRet = null;
		String strUserName = null;
		if (strToken != null) {
			strUserName = UserInfoMngr.UserLogin(strToken);
			if (strUserName.startsWith("@-")) {
				strUserName = null;
			}
		}

		switch (strAction.replace("/", "").toLowerCase()) {
		case "browse":
			strRet = BehaviourMngr.addBrowseInfo(strUserName, mapInfo);
			break;
		case "read":
			strRet = BehaviourMngr.addReadInfo(strUserName, mapInfo);
			break;
		// case "upload":
		// //strRet = updateInfo(mapInfo);
		// break;
		// case "download":
		// //strRet = getInfo(mapInfo);
		// break;
		case "search":
			strRet = BehaviourMngr.addSearchInfo(strUserName, mapInfo);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}

		sendResponseData(response, strRet);
	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

}
