package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.KuaiBaoMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class KuaiBaoServlet
 */
@WebServlet("/kuaibao/*")
public class KuaiBaoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public KuaiBaoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String appid = String.valueOf(request.getAttribute("app_id"));
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}

		byte[] arrReq = new byte[request.getContentLength()];
		request.getInputStream().read(arrReq);
		String strReq = new String(arrReq, "utf-8");
		arrReq = null;

		JSONObject jo = JSONObject.fromObject(strReq);
		@SuppressWarnings("unchecked")
		Map<String, String> mapReq = (Map<String, String>) jo;

		String strRet;
		String strUserName = null;
		String strToken = (String) mapReq.get("usertoken");
		strUserName = UserInfoMngr.UserLogin(strToken);
		if (strUserName.startsWith("@-")) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
					.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
			return;
		}
		switch (strAction.replace("/", "").toLowerCase()) {
		case "add":
			strRet = addAtionKuaiBao(strUserName, mapReq, appid);
			break;
		case "cancel":
			strRet = cancelAtionKuaiBao(strUserName, mapReq, appid);
			break;
		case "gets":
			strRet = getKuaiBaoList(strUserName);
			break;
		case "chkation":
			strRet = chkAtionKuaiBao(strUserName, mapReq);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}
		if (Common.IsNullOrEmpty(strRet)) {
			return;
		}
		sendResponseData(response, strRet);
	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	private String addAtionKuaiBao(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if ("1".equals(KuaiBaoMngr.checkAttention(userName, code))) {
			return "{\"result\":true}";
		}
		if (KuaiBaoMngr.addAttention(userName, code)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String cancelAtionKuaiBao(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (KuaiBaoMngr.concalAttention(userName, code)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String getKuaiBaoList(String userName) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
		int iCount = KuaiBaoMngr.getAtionSubjectCount(userName);
		if (iCount == 0) {
			return "{\"result\":true,\"count\":0,\"data\":" + jsonArray.toString() + "}";
		}
		List<Map<String, Object>> lstZjcls = KuaiBaoMngr.getAtionSubjectList(userName);
		if (lstZjcls != null && lstZjcls.size() > 0) {
			for (Map<String, Object> map : lstZjcls) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", map.get("id"));
				jsonObj.put("sortcode", map.get("sortcode"));
				jsonObj.put("name", map.get("name"));
				jsonObj.put("pathname", map.get("pathname"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + iCount + ",\"data\":" + jsonArray.toString() + "}";
	}

	private String chkAtionKuaiBao(String userName, Map<String, String> arg) throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String[] arrcode = code.split(";");
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < arrcode.length; i++) {
			JSONObject jsonObj = new JSONObject();
			if (Common.IsNullOrEmpty(arrcode[i]))
				continue;
			jsonObj.put("name", arrcode[i]);
			jsonObj.put("val", KuaiBaoMngr.checkAttention(userName, arrcode[i]));
			jsonArray.add(jsonObj);
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}
}
