package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import BLL.CashMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class KuaiBaoHanderServlet
 */
@WebServlet("/cash/*")
public class CashServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CashServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String appId = String.valueOf(request.getAttribute("app_id"));
		byte[] arrReq = new byte[request.getContentLength()];
		request.getInputStream().read(arrReq);
		String strReq = new String(arrReq, "utf-8");
		JSONObject jo = JSONObject.fromObject(strReq);
		Map<String, String> mapReq = (Map<String, String>) jo;
		String IP = "";
		if (mapReq.containsKey("pi")) {
			IP = mapReq.get("pi");
		}
		if (Common.IsNullOrEmpty(IP)) {
			IP = Common.getClientIP(request);
		}
		mapReq.put("ip", IP);
		mapReq.put("appid", appId);
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}
		String strToken = (String) mapReq.get("usertoken");
		String strUserName = UserInfoMngr.UserLogin(strToken);
		if (strUserName.startsWith("@-")) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
					.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
			return;
		}

		String strRet;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "recharge":
			strRet = recharge(strUserName, mapReq);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}

		sendResponseData(response, strRet);
	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	private String recharge(String userName, Map<String, String> UserInfo) {
		String cash = UserInfo.get("cash");
		String platform = UserInfo.get("platform");
		String environment = UserInfo.get("environment")==null?"":UserInfo.get("environment");
		String status = UserInfo.get("status")==null?"":UserInfo.get("status");
		String ip = UserInfo.get("ip");
		String appId = UserInfo.get("appid");
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(platform) || Common.IsNullOrEmpty(status)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (CashMngr.recharge(userName, cash, platform, environment, status, ip, appId)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false}";
		}
	}

}
