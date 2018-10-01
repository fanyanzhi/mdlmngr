package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.FavoriteMngr;
import BLL.ProjectMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class FavoriteServlet
 */
@WebServlet("/favorite/*")
public class FavoriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FavoriteServlet() {
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
		String strToken = (String) mapReq.get("usertoken");
		strUserName = UserInfoMngr.UserLogin(strToken);
		if (strUserName.startsWith("@-")) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
					.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
			return;
		}
		switch (strAction.replace("/", "").toLowerCase()) {
		case "add":
			strRet = addFavoriteInfo(strUserName, mapReq, appid);
			break;
		case "cancel":
			strRet = cancelUserFavorite(strUserName, mapReq, appid);
			break;
		case "check":
			strRet = checkUserFavorite(strUserName, mapReq, appid);
			break;
		case "count":
			strRet = getUserFavoriteCount(strUserName);
			break;
		case "gets":
			strRet = getUserFavorite(strUserName, mapReq);
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

	private String addFavoriteInfo(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String odatatype = (String) arg.get("odatatype");
		String fileid = (String) arg.get("fileid");
		String title = (String) arg.get("title");
		String author = (String) arg.get("author");
		String source = (String) arg.get("source");

		if (Common.IsNullOrEmpty(odatatype) || Common.IsNullOrEmpty(fileid)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (FavoriteMngr.addFavoriteInfo(userName, odatatype, fileid, title, author, source, appID)) {
			Map<String, Object> map = FavoriteMngr.getFavoriteID(userName,odatatype,fileid);
			if (map != null)
				return "{\"result\":true,\"id\":\"" + map.get("id") + "\",\"time\":\"" + map.get("time") + "\"}";
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String cancelUserFavorite(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String id = (String) arg.get("id");
		if (Common.IsNullOrEmpty(id)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (FavoriteMngr.delFavorite(userName, id)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String checkUserFavorite(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String odatatype = (String) arg.get("odatatype");
		String fileid = (String) arg.get("fileid");
		if (Common.IsNullOrEmpty(odatatype) || Common.IsNullOrEmpty(fileid)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		int iRet = FavoriteMngr.checkUserFavorite(userName, odatatype, fileid);
		if (iRet == 1) {
			return "{\"result\":true,\"status\":\"1\"}";
		} else if (iRet == 0) {
			return "{\"result\":true,\"status\":\"0\"}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String getUserFavoriteCount(String userName) {
		int iCount = FavoriteMngr.getFavoriteCount(userName);
		return "{\"result\":true,\"count\":\"" + iCount + "\"}";
	}

	private String getUserFavorite(String userName, Map<String, String> arg) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
		int start = 1;
		int length = 20;
		if (arg.containsKey("start") && arg.get("start") != null) {
			start = Integer.valueOf(String.valueOf(arg.get("start")));
		}
		if (arg.containsKey("length") && arg.get("length") != null) {
			length = Integer.valueOf(String.valueOf(arg.get("length")));
		}
		int iCount = FavoriteMngr.getFavoriteCount(userName);
		if (iCount == 0) {
			return "{\"result\":true,\"count\":\"0\",\"data\":" + jsonArray.toString() + "}";
		}
		List<Map<String, Object>> lstFavorite = FavoriteMngr.getFavoriteList(userName, start, length);
		if (lstFavorite != null && lstFavorite.size() > 0) {
			for (Map<String, Object> map : lstFavorite) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", map.get("id"));
				jsonObj.put("odatatype", map.get("odatatype"));
				jsonObj.put("fileid", map.get("fileid"));
				jsonObj.put("title", map.get("title"));
				jsonObj.put("author", map.get("author"));
				jsonObj.put("source", map.get("source"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":\"" + iCount + "\",\"data\":" + jsonArray.toString() + "}";
	}

}
