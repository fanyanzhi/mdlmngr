package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.AdvertisementMngr;
import BLL.SysConfigMngr;
import Util.Common;

/**
 * Servlet implementation class AdvertisementServlet
 */
@WebServlet("/advertisement/*")
public class AdvertisementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdvertisementServlet() {
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
		String appId = String.valueOf(request.getAttribute("app_id"));
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}

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

		String strRet;

		switch (strAction.replace("/", "").toLowerCase()) {
		case "getadv":
			strRet = getAdvertisement(appId, mapInfo);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
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

	private String getAdvertisement(String appid, Map<String, Object> arg) {
		String strUpTime = "";
		if (arg != null) {
			strUpTime = String.valueOf(arg.get("updatetime"));
		}
		String strLatestTime = AdvertisementMngr.getLatestUpTime(appid);
		if (strLatestTime.equals(strUpTime)) {
			return "{\"result\":true,\"data\":\"\",\"isupdate\":false}";
		}
		List<Map<String, Object>> lstResult = AdvertisementMngr.getValidAdvertisement(appid);
		String strImageID = null;
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				if (map.get("imageid") == null) {
					continue;
				}
				strImageID = String.valueOf(map.get("imageid"));
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("content", map.get("content"));
				jsonObj.put("type", map.get("type"));
				jsonObj.put("imgsrc", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?").concat(strImageID));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + ",\"isupdate\":true,\"updatetime\":\"".concat(strLatestTime).concat("\"}");
	}

}
