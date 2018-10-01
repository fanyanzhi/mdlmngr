package Request;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.AppInfoMngr;
import BLL.CommonModuleInfoMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;

/**
 * Servlet implementation class InfoServlet
 */
@WebServlet("/info/*")
public class InfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InfoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String appid = String.valueOf(request.getAttribute("app_id"));
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
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
		if (Common.IsNullOrEmpty(strToken)) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("}"));
			return;
		}
		mapInfo.put("appid", appid);
		mapInfo.put("sync", AppInfoMngr.isSync(appid));
		String strRet;
		String strUserName = UserInfoMngr.UserLogin(strToken);
		if (strUserName.startsWith("@-")) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
			return;
		}
		//System.out.println(strAction+"--mapInfo--"+mapInfo.toString());
		switch (strAction.replace("/", "").toLowerCase()) {
		case "add":
			strRet = addInfo(mapInfo);
			break;
		case "delete":
			strRet = delInfo(mapInfo);
			break;
		case "update":
			strRet = updateInfo(mapInfo);
			break;
		case "updates":
			strRet = updatesInfo(mapInfo);
			break;
		case "get":
			strRet = getInfo(mapInfo);
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

	private String addInfo(Map<String, Object> arg) {
		String strRet = null;
		String strTime = CommonModuleInfoMngr.addModuleInfo(arg);
		if (strTime.startsWith("@-")) {
			strRet = "{\"result\":false,\"message\":\"".concat(strTime.substring(1)).concat("\",\"errorcode\":").concat(strTime.substring(1)).concat("}");
		} else {
			strRet = "{\"result\":true,\"time\":\"".concat(strTime).concat("\"}");
		}
		return strRet;
	}

	private String updateInfo(Map<String, Object> arg) {
		String strRet = null;
		String strTime = CommonModuleInfoMngr.updateModuleInfo(arg);
		if (strTime.startsWith("@-")) {
			strRet = "{\"result\":false,\"message\":\"".concat(strTime.substring(1)).concat("\",\"errorcode\":").concat(strTime.substring(1)).concat("}");
		} else {
			strRet = "{\"result\":true,\"time\":\"".concat(strTime).concat("\"}");
		}
		return strRet;
	}
	
	private String updatesInfo(Map<String, Object> arg) {
		String strRet = null;
		String strTime = CommonModuleInfoMngr.updateModuleInfo(arg);
		if (strTime.startsWith("@-")) {
			strRet = "{\"result\":false,\"message\":\"".concat(strTime.substring(1)).concat("\",\"errorcode\":").concat(strTime.substring(1)).concat("}");
		} else {
			strRet = "{\"result\":true,\"time\":\"".concat(strTime).concat("\"}");
		}
		return strRet;
	}

	/**
	 * 暂时没用
	 * 
	 * @param arg
	 * @return
	 */
	private String delInfo(Map<String, Object> arg) {
		int iRet = CommonModuleInfoMngr.delModuleInfo(arg);
		String strRet;
		if (iRet == 0) {
			strRet = "{\"result\":true}";
		} else {
			strRet = "{\"result\":false,\"message\":\"".concat(String.valueOf(iRet)).concat("\",\"errorcode\":").concat(String.valueOf(iRet)).concat("}");
		}
		return strRet;
	}

	private String getInfo(Map<String, Object> arg) {
		if("1".equals(arg.get("sync").toString())){
			arg.remove("appid");
		}else{
			arg.remove("sync");
		}
		List<Map<String, Object>> lstResult = CommonModuleInfoMngr.getModuleList(arg);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				for (Entry<String, Object> entry : map.entrySet()) {
					jsonObj.put(entry.getKey(), entry.getValue());
				}
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + ",\"updatetime\":\"".concat(Common.GetDateTime()) + "\"}";
	}

}
