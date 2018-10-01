package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.SearchFormalMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class SearchFormalServlet
 */
@WebServlet("/seaformal/*")
public class SearchFormalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchFormalServlet() {
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
			strRet = addAtionSeaFormal(strUserName, mapReq, appid);
			break;
		case "update":
			strRet = updateAtionSeaFormal(strUserName, mapReq, appid);
			break;
		case "cancel":
			strRet = cancelAtionSeaFormal(strUserName, mapReq, appid);
			break;
		case "gets":
			strRet = getSeaFormalList(strUserName);
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

	private String addAtionSeaFormal(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String formal = (String) arg.get("formal");
		if (Common.IsNullOrEmpty(formal)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (SearchFormalMngr.addAtionSeaFormal(userName, arg, appID)) {
			Map<String, Object> map = SearchFormalMngr.getLastRecord(userName);
			if (map != null)
				return "{\"result\":true,\"id\":" + map.get("id") + ",\"time\":\"" + map.get("time") + "\"}";
		}
		return "{\"result\":false,\"message\":\""
				.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":")
				.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
	}

	private String updateAtionSeaFormal(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String formal = (String) arg.get("formal");
		String id = (String) arg.get("id");
		if (Common.IsNullOrEmpty(formal) || Common.IsNullOrEmpty(id)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (SearchFormalMngr.updateAtionSeaFormal(userName, arg, appID)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String cancelAtionSeaFormal(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String id = (String) arg.get("id");
		if (Common.IsNullOrEmpty(id)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (SearchFormalMngr.cancelAtionSeaFormal(userName, id)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String getSeaFormalList(String userName) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
		int iCount = SearchFormalMngr.getSeaFormalCount(userName);
		if (iCount == 0) {
			return "{\"result\":true,\"count\":0,\"data\":" + jsonArray.toString() + "}";
		}
		List<Map<String, Object>> lstZjcls = SearchFormalMngr.getSeaFormalList(userName);
		if (lstZjcls != null && lstZjcls.size() > 0) {
			for (Map<String, Object> map : lstZjcls) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", map.get("id"));
				jsonObj.put("formal", map.get("formal"));
				jsonObj.put("content", map.get("content"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + iCount + ",\"data\":" + jsonArray.toString() + "}";
	}

}
