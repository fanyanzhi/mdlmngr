package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.HotspotMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class HotspotServlet
 */
@WebServlet("/hotspot/*")
public class HotspotServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HotspotServlet() {
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
		String strToken = "";
		if (strAction.replace("/", "").toLowerCase().equals("add")
				|| strAction.replace("/", "").toLowerCase().equals("cancel")
				|| strAction.replace("/", "").toLowerCase().equals("check")
				|| strAction.replace("/", "").toLowerCase().equals("myhotspot")) {
			strToken = (String) mapReq.get("usertoken");
			strUserName = UserInfoMngr.UserLogin(strToken);
			if (strUserName.startsWith("@-")) {
				sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
						.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
				return;
			}
		}
		switch (strAction.replace("/", "").toLowerCase()) {
		case "add":
			strRet = addAtionHotspot(strUserName, mapReq, appid);
			break;
		case "cancel":
			strRet = cancelAtionHotspot(strUserName, mapReq, appid);
			break;
		case "check":
			strRet = checkAtionHotspot(strUserName, mapReq, appid);
			break;
		case "myhotspot":
			strRet = getAtionHotspot(strUserName);
			break;
		case "gets":
			strRet = getHotspots(mapReq);
			break;
		case "count":
			strRet = getHotspotCount(mapReq);
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

	private String getHotspots(Map<String, String> arg) {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		List<Map<String, Object>> lst = HotspotMngr.getHotspotBySortCode(code);
		if (lst == null || lst.size() == 0) {
			return "{\"result\":true,\"count\":0}";
		}
		JSONArray jsonArray = new JSONArray();
		for (Map<String, Object> map : lst) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("spotid", map.get("spotid"));
			jsonObj.put("title", map.get("title"));
			jsonObj.put("expression", map.get("expression"));
			jsonObj.put("seaformal", map.get("seaformal"));
			jsonArray.add(jsonObj);
		}
		return "{\"result\":true,\"count\":" + lst.size() + ",\"data\":" + jsonArray.toString() + "}";
	}

	private String getHotspotCount(Map<String, String> arg) {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		List<Map<String, Object>> lst = HotspotMngr.getHotspotCountBySortCode(code);
		JSONObject jsonObj = new JSONObject();
		if (lst != null && lst.size() > 0) {
			for (Map<String, Object> map : lst) {
				jsonObj.put(map.get("spotid"), map.get("count"));
			}
		}
		return "{\"result\":true,\"data\":" + jsonObj + "}";
	}

	private String addAtionHotspot(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String spotId = (String) arg.get("spotid");
		if (Common.IsNullOrEmpty(spotId)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (HotspotMngr.addUserHotspot(userName, spotId)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String cancelAtionHotspot(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String spotId = (String) arg.get("spotid");
		if (Common.IsNullOrEmpty(spotId)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (HotspotMngr.cancelUserHotspot(userName, spotId)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String checkAtionHotspot(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String spotId = (String) arg.get("spotid");
		if (Common.IsNullOrEmpty(spotId)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		int iRet = HotspotMngr.existUserHotspot(userName, spotId);
		if (iRet > 0) {
			return "{\"result\":true,\"status\":1}";
		} else if (iRet == 0) {
			return "{\"result\":true,\"status\":0}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String getAtionHotspot(String userName) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
		int iCount = HotspotMngr.getMyHotspotCount(userName);
		if (iCount == 0) {
			return "{\"result\":true,\"count\":0}";
		}
		List<Map<String, Object>> myHotspot = HotspotMngr.getMyHotspotList(userName);
		if (myHotspot != null && myHotspot.size() > 0) {
			for (Map<String, Object> map : myHotspot) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("spotid", map.get("spotid"));
				jsonObj.put("title", map.get("title"));
				jsonObj.put("hotspot", map.get("hotspot"));
				jsonObj.put("expression", map.get("expression"));
				jsonObj.put("seaformal", map.get("seaformal"));
				jsonObj.put("sortcode", map.get("sortcode"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + iCount + ",\"data\":" + jsonArray.toString() + "}";
	}

}
