package Request;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import BLL.AppInfoMngr;
import BLL.BehaviourMngr;
import BLL.SysConfigMngr;
import Util.AuthDecrypt;
import Util.Common;

/**
 * Servlet implementation class AppServlet
 */
@WebServlet("/app/*")
public class AppServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String TokenPassWord = "@a3k9#-;jdiu$98JH-03H~kpb59akj8j";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String sign = request.getParameter("sign");
		String decSign = "";
		if (!Common.IsNullOrEmpty(sign)) {
			decSign = AuthDecrypt.DecryptRequest(decSign, "/clientcert.jks", "123456", "readerex client", "123456");
			if (decSign != null) {
				String[] arrSign = decSign.split("|");
				if (arrSign.length == 4) {
					Map<String, Object> authMap = AppInfoMngr.validAppAuth("");
					if (arrSign[0].equals(authMap.get("password")) && arrSign[1].equals(authMap.get("package"))
							&& arrSign[2].equals(authMap.get("jkshash"))) {
						long curTime = System.currentTimeMillis();
						long clientTime = Long.parseLong(arrSign[3]);
						if (((((curTime - clientTime) / 1000)) / 60) < 10) {
							String token = CreateUserToken(arrSign[1] + arrSign[2]);
							if(AppInfoMngr.setAccessToken(token)){
								sendResponseData(response, "{\"result\":true,\"acctoken\":\"".concat(token).concat("\"}"));	
							}else{
								sendResponseData(response, "{\"result\":false,\"message\":\""
										.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
										.concat("\",\"errorcode\":")
										.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}"));
							}
						} else {
							sendResponseData(response, "{\"result\":false,\"message\":\""
									.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TIMEOUT.code))
									.concat("\",\"errorcode\":")
									.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TIMEOUT.code)).concat("}"));
						}
					} else {
						sendResponseData(response, "{\"result\":false,\"message\":\""
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
								.concat("\",\"errorcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"));
					}
				}else{
					sendResponseData(response, "{\"result\":false,\"message\":\""
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
							.concat("\",\"errorcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"));
				}

			}
		}*/

	}

	private static String CreateUserToken(String UserName) {
		String strInfo = String.valueOf(new Date().getTime()).concat(UserName);
		String strMD5 = Common.EnCodeMD5(strInfo);
		String strEncrypt = Common.EncryptData(strInfo, TokenPassWord);
		return strMD5.concat(strEncrypt).replace("\\", "X").replace("/", "Y").replace("+", "Z");
	}

	public static void main(String[] args) {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String appId = String.valueOf(request.getAttribute("app_id"));
		String IP = Common.getClientIP(request);
		byte[] arrReq = new byte[request.getContentLength()];
		request.getInputStream().read(arrReq);
		String strReq = new String(arrReq, "utf-8");
		JSONObject jo = null;
		try {
			jo = JSONObject.fromObject(strReq);
		} catch (Exception e) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"));
			return;
			// LoggerFile.appendMethod("/home/appservlet.txt",
			// request.getPathInfo()+"-->"+e.getMessage());
			// LoggerFile.appendMethod("/home/appservlet.txt",
			// request.getPathInfo()+"-->"+strReq);
		}
		Map<String, String> mapReq = (Map<String, String>) jo;
		mapReq.put("ip", IP);
		mapReq.put("appid", appId);
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}

		String strRet;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "crash":
			strRet = setCrashInfo(mapReq);
			break;
		case "start":
			strRet = appStartLog(mapReq);
			break;
		case "statist":
			strRet = appStatist(mapReq);
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

	private String setCrashInfo(Map<String, String> CrashInfo) {
		Boolean bRet = false;
		String strAppInfo = CrashInfo.get("version");
		String strPlatForm = CrashInfo.get("platform");
		String strErrorInfo = CrashInfo.get("crashinfo");
		bRet = AppInfoMngr.setAppCrashInfo(strAppInfo, strPlatForm, strErrorInfo);
		if (bRet) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false}";
		}
	}

	public String appStartLog(Map<String, String> CrashInfo) {
		Boolean bRet = false;

		// bRet = AppInfoMngr.addAppStartInfo(userName, clientid, ip, clientos,
		// baseos, version, appid);
		if (bRet) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false}";
		}

	}

	private String appStatist(Map<String, String> UserInfo) {
		String strUserName = UserInfo.get("username");
		String strClientID = UserInfo.get("clientid");
		String strPlatForm = UserInfo.get("platform");
		String version = UserInfo.get("version");
		String strIP = UserInfo.get("ip");
		String appId = UserInfo.get("appid");
		int timespan = 0;
		if (UserInfo.containsKey("waittime")) {
			try {
				timespan = Integer.parseInt(UserInfo.get("waittime").toString());
			} catch (Exception e) {

			}
		}
		String strRet = BehaviourMngr.addAppStatist(strUserName, strClientID, strPlatForm, version, strIP, appId,
				timespan);
		return strRet;
	}

}
