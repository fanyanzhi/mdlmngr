package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.AdvertisementMngr;
import BLL.BehaviourMngr;
import BLL.RecommendationInfoMngr;
import BLL.SysConfigMngr;

/**
 * Servlet implementation class RecommendServlet
 */
@WebServlet("/recommend/*")
public class RecommendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RecommendServlet() {
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
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}
		String appId = String.valueOf(request.getAttribute("app_id"));
		int iReqContet = request.getContentLength();
		Map<String, Object> mapInfo = null;
		if (iReqContet > 0) {
			byte[] arrReq = new byte[request.getContentLength()];
			request.getInputStream().read(arrReq);
			String strReq = new String(arrReq, "utf-8");
			arrReq = null;

			JSONObject jo = JSONObject.fromObject(strReq);
			mapInfo = (Map<String, Object>) jo;
		}
		// String strToken = (String) mapInfo.get("usertoken");
		String strRet;
		// String strUserName = UserInfoMngr.UserLogin(strToken);
		// if (strUserName.startsWith("@-")) {
		// sendResponseData(response,
		// "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\"}"));
		// return;
		// }
		switch (strAction.replace("/", "").toLowerCase()) {
		case "getmodule":
			strRet = getModule(appId);
			break;
		case "get":
			strRet = getInfo(appId, mapInfo);
			break;
		case "getimportant":
			strRet = getImportantInfo(appId, mapInfo);
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

	private String getModule(String appId) {
		List<Map<String, Object>> lstResult = RecommendationInfoMngr.getRecommendationTypeList(appId);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("typeid", map.get("name_en"));
				jsonObj.put("name", map.get("name_ch"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	private String getInfo(String appID, Map<String, Object> arg) {
		String strType = (String) arg.get("typeid");
		String strUpTime = String.valueOf(arg.get("updatetime"));

		if (strType == null) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String strLatestTime = RecommendationInfoMngr.getLatestTime(appID, strType);
		if (strLatestTime.equals(strUpTime)) {
			return "{\"result\":true,\"data\":\"\",\"isupdate\":false}";
		}

		int iStart = 1;
		int iLength = 20;
		if (arg.containsKey("start") && arg.get("start") != null) {
			iStart = Integer.valueOf(String.valueOf(arg.get("start")));
		}
		if (arg.containsKey("length") && arg.get("length") != null) {
			iLength = Integer.valueOf(String.valueOf(arg.get("length")));
		}

		List<Map<String, Object>> lstResult = RecommendationInfoMngr.getRecommendList(appID, strType, iStart, iLength);
		String strDesc = null;
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				strDesc = (String) map.get("description");
				if (Common.IsNullOrEmpty(strDesc)) {
					strDesc = "";
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", map.get("title"));
				jsonObj.put("instance", map.get("fileid"));
				jsonObj.put("desc", strDesc);
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + ",\"isupdate\":true,\"updatetime\":\"".concat(strLatestTime).concat("\"}");
	}

	private String getImportantInfo(String appid, Map<String, Object> arg) {
		BehaviourMngr.addUseInfo();
		String strUpTime = "";
		if (arg != null) {
			strUpTime = arg.get("updatetime") == null ? "" : String.valueOf(arg.get("updatetime"));
		}
		String strRemLatestTime = RecommendationInfoMngr.getLatestTime(appid);
		String strAdvLatestTime = AdvertisementMngr.getLatestUpTime(appid);
		boolean bUpdate = false;

		if (Common.IsNullOrEmpty(strUpTime)) {
			if (Common.IsNullOrEmpty(strRemLatestTime) && Common.IsNullOrEmpty(strAdvLatestTime)) {
				return "{\"result\":true,\"data\":\"\",\"isupdate\":false}";
			} else {
				bUpdate = true;
			}
		}
		if (!Common.IsNullOrEmpty(strUpTime)&&strUpTime.length()>10) {
			if (Common.IsNullOrEmpty(strRemLatestTime) && Common.IsNullOrEmpty(strAdvLatestTime)) {
				bUpdate = true;
			} else {
				if (!Common.IsNullOrEmpty(strRemLatestTime)) {
					if (Common.ConvertToDate(strUpTime, "yyyy-MM-dd HH:mm:ss").getTime() < Common.ConvertToDate(strRemLatestTime, "yyyy-MM-dd HH:mm:ss").getTime()) {
						bUpdate = true;
					}
				}
				if (!Common.IsNullOrEmpty(strAdvLatestTime)) {
					if (Common.ConvertToDate(strUpTime, "yyyy-MM-dd HH:mm:ss").getTime() < Common.ConvertToDate(strAdvLatestTime, "yyyy-MM-dd HH:mm:ss").getTime()) {
						bUpdate = true;
					}
				}
			}
		}
		if (!bUpdate) {
			return "{\"result\":true,\"data\":\"\",\"isupdate\":false}";
		}

		String strImageID = null;
		JSONArray jsonArray = new JSONArray();

		if (!Common.IsNullOrEmpty(strRemLatestTime)) {
			List<Map<String, Object>> lstResult = RecommendationInfoMngr.getImportantRecommendList(appid);
			String strDesc = null;
			if (lstResult != null) {
				for (Map<String, Object> map : lstResult) {
					strDesc = (String) map.get("description");
					if (map.get("imageid") == null) {
						continue;
					}
					strImageID = String.valueOf(map.get("imageid"));
					if (Common.IsNullOrEmpty(strDesc)) {
						strDesc = "";
					}
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("title", map.get("title"));
					jsonObj.put("typeid", map.get("name_en"));
					jsonObj.put("instance", map.get("fileid"));
					jsonObj.put("desc", strDesc);
					jsonObj.put("imgsrc", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?").concat(strImageID));
					jsonArray.add(jsonObj);
				}
			}
		}
		if (!Common.IsNullOrEmpty(strAdvLatestTime)) {
			List<Map<String, Object>> lstAdv = AdvertisementMngr.getValidAdvertisement(appid);
			if (lstAdv != null) {
				for (Map<String, Object> map : lstAdv) {
					if (map.get("imageid") == null) {
						continue;
					}
					strImageID = String.valueOf(map.get("imageid"));
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("typeid", "advert");
					jsonObj.put("instance", map.get("type"));
					jsonObj.put("desc", map.get("content"));
					jsonObj.put("title", map.get("title") == null ? "" : map.get("title"));
					jsonObj.put("imgsrc", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?").concat(strImageID));
					jsonArray.add(jsonObj);
				}
			}
		}

		String maxUpTime = "";
		if (!Common.IsNullOrEmpty(strRemLatestTime) && !Common.IsNullOrEmpty(strAdvLatestTime)&&strRemLatestTime.length()>10&&strAdvLatestTime.length()>10) {
			maxUpTime = Common.ConvertToDate(strRemLatestTime, "yyyy-MM-dd HH:mm:ss").getTime() >= Common.ConvertToDate(strAdvLatestTime, "yyyy-MM-dd HH:mm:ss").getTime() ? strRemLatestTime : strAdvLatestTime;
		} else {
			if (Common.IsNullOrEmpty(strRemLatestTime)) {
				maxUpTime = strAdvLatestTime;
			}
			if (Common.IsNullOrEmpty(strAdvLatestTime)) {
				maxUpTime = strRemLatestTime;
			}
		}

		return "{\"result\":true,\"data\":" + jsonArray.toString() + ",\"isupdate\":true,\"updatetime\":\"".concat(maxUpTime).concat("\"}");
	}
	// private List<Map<String, Object>> getValidAdvertisement(String[]
	// arrUpdateTime) {
	// String strLatestTime = AdvertisementMngr.getLatestUpTime();
	// if (Common.IsNullOrEmpty(strLatestTime)) {
	// return null;
	// }
	// if (!Common.IsNullOrEmpty(arrUpdateTime[0])) {
	// if (Common.ConvertToDate(arrUpdateTime[0],
	// "yyyy-MM-dd HH:mm:ss").getTime() >= Common.ConvertToDate(strLatestTime,
	// "yyyy-MM-dd HH:mm:ss").getTime()) {
	// return null;
	// }
	// }
	// arrUpdateTime[0] = strLatestTime;
	// return AdvertisementMngr.getValidAdvertisement();
	// }
}
