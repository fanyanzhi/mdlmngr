package Request;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.MiniAdmin;

import BLL.LiteratureMngr;
import BLL.ODataHelper;
import BLL.ScholarMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class ScholarServlet
 */
@WebServlet("/scholar/*")
public class ScholarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ScholarServlet() {
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
		if ("add".equalsIgnoreCase(strAction.replace("/", "")) || "cancel".equalsIgnoreCase(strAction.replace("/", ""))
				|| "check".equalsIgnoreCase(strAction.replace("/", ""))
				|| "gets".equalsIgnoreCase(strAction.replace("/", ""))
				|| "msgset".equalsIgnoreCase(strAction.replace("/", ""))
				|| "scholarstatus".equalsIgnoreCase(strAction.replace("/", ""))) {
			String strToken = (String) mapReq.get("usertoken");
			strUserName = UserInfoMngr.UserLogin(strToken);
			if (strUserName.startsWith("@-")) {
				sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
						.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
				return;
			}
		}
		switch (strAction.replace("/", "").toLowerCase()) {
		case "add":
			strRet = addAtionScholar(strUserName, mapReq, appid);
			break;
		case "cancel":
			strRet = cancelAtionScholar(strUserName, mapReq, appid);
			break;
		case "check":
			strRet = checkAtionScholar(strUserName, mapReq, appid);
			break;
		case "gets":
			strRet = getSeaFormalList(strUserName);
			break;
		case "actnum":
			strRet = getScholarActionNum(mapReq);
			break;
		case "browse":
			strRet = addScholarBrowse(mapReq);
			break;
		case "brocount":
			strRet = getScholarBrowseCount(mapReq);
			break;
		case "msgset":
			strRet = setSchloarMsg(strUserName, mapReq);
			break;
		case "msgstatus":
			strRet = getSchloarMsgStatus(mapReq);
			break;
		case "scholarstatus":
			strRet = getScholarstatus(strUserName, mapReq);
			break;
		case "scholarstatuscount":
			strRet = scholarstatusCount(strUserName, mapReq);
			break;
		case "search":
			strRet = scholarSearch(mapReq);
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

	private String addAtionScholar(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ScholarMngr.addAtionScholar(userName, arg, appID)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String cancelAtionScholar(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ScholarMngr.cancelAtionScholar(userName, code)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String checkAtionScholar(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		int iRet = ScholarMngr.checkAtionScholar(userName, code);
		if (iRet == 1) {
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

	private String getSeaFormalList(String userName) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
		int iCount = ScholarMngr.getAtionScholarCount(userName);
		if (iCount == 0) {
			return "{\"result\":true,\"count\":0,\"data\":" + jsonArray.toString() + "}";
		}
		List<Map<String, Object>> lstZjcls = ScholarMngr.getAtionScholarList(userName);
		if (lstZjcls != null && lstZjcls.size() > 0) {
			for (Map<String, Object> map : lstZjcls) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("code", map.get("code"));
				jsonObj.put("name", map.get("name"));
				jsonObj.put("contributor", map.get("contributor"));
				jsonObj.put("investigation", map.get("investigation"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + iCount + ",\"data\":" + jsonArray.toString() + "}";
	}

	private String getScholarActionNum(Map<String, String> arg) throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		int atcnum = ScholarMngr.getScholarActionNum(code);
		return "{\"result\":true,\"count\":" + atcnum + "}";
	}

	private String addScholarBrowse(Map<String, String> arg) {
		String username = arg.containsKey("username") ? (String) arg.get("username") : "";
		String code = arg.containsKey("code") ? (String) arg.get("code") : "";
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		boolean ret = LiteratureMngr.addScholarBrowse(username, code);
		if (ret) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}

	}

	private String getScholarBrowseCount(Map<String, String> arg) {
		String code = arg.containsKey("code") ? (String) arg.get("code") : "";
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		int count = LiteratureMngr.getScholarBrowseCount(code);
		return "{\"result\":true,\"count\":" + count + "}";
	}

	private String setSchloarMsg(String userName, Map<String, String> arg) {
		String code = arg.containsKey("code") ? (String) arg.get("code") : "";
		String openStatus = arg.containsKey("status") ? (String) arg.get("status") : ""; // 1.为允许，0为禁止
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(code) || Common.IsNullOrEmpty(openStatus)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		int status = ScholarMngr.setSchloarMsg(code, openStatus);
		if (status == -1)
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		return "{\"result\":true,\"status\":" + status + "}";
	}

	private String getSchloarMsgStatus(Map<String, String> arg) {
		String code = arg.containsKey("code") ? (String) arg.get("code") : "";
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		int status = ScholarMngr.getSchloarMsg(code);
		return "{\"result\":true,\"status\":" + status + "}";
	}

	private String scholarstatusCount(String userName, Map<String, String> arg) {
		String time = arg.containsKey("time") ? (String) arg.get("time") : "";
		List<Map<String, Object>> lst = ScholarMngr.getAtionScholarList(userName);
		if (lst == null || lst.size() == 0) {
			return "{\"result\":true,\"count\":0}";
		}
		JSONArray arr = new JSONArray();
		JSONObject json = new JSONObject();
		Iterator<Map<String, Object>> iterator = lst.iterator();
		Map<String, Object> map = null;
		while (iterator.hasNext()) {
			map = iterator.next();
			json.put("expertcode", map.get("code"));
			arr.add(json);
		}
		List<Map<String, Object>> lstExpert = ScholarMngr.expertstatus(arr, time);
		if (lstExpert == null || lstExpert.size() == 0) {
			return "{\"result\":true,\"count\":0}";
		}
		return "{\"result\":true,\"count\":" + lstExpert.size() + "}";
	}

	private String getScholarstatus(String userName, Map<String, String> arg) {
		String time = arg.containsKey("time") ? (String) arg.get("time") : "";
		List<Map<String, Object>> lst = ScholarMngr.getAtionScholarList(userName);
		if (lst == null || lst.size() == 0) {
			return "{\"result\":true,\"count\":0}";
		}
		JSONArray arr = new JSONArray();
		JSONObject json = new JSONObject();
		Iterator<Map<String, Object>> iterator = lst.iterator();
		Map<String, Object> map = null;
		while (iterator.hasNext()) {
			map = iterator.next();
			json.put("expertcode", map.get("code"));
			arr.add(json);
		}
		List<Map<String, Object>> lstExpert = ScholarMngr.expertstatus(arr, time);
		if (lstExpert == null || lstExpert.size() == 0) {
			return "{\"result\":true,\"count\":0}";
		}
		JSONArray jsonArray = new JSONArray();
		if (lstExpert != null && lstExpert.size() > 0) {
			for (Map<String, Object> maps : lstExpert) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("expcode", maps.get("expcode"));
				jsonObj.put("realname", maps.get("realname"));
				jsonObj.put("time", maps.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + jsonArray.size() + ",\"data\":" + jsonArray.toString() + "}";
	}

	public static void main(String[] args) {
		System.out.println(Common.getMilliSeconds("2018-02-10 14:51:12"));
	}

	private String scholarSearch(Map<String, String> arg) {
		String fields = arg.containsKey("fields") ? arg.get("fields") : "";
		String query = arg.containsKey("query") ? arg.get("query") : "";
		String order = arg.containsKey("order") ? arg.get("order") : "";
		String group = arg.containsKey("group") ? arg.get("group") : "";
		String start = arg.containsKey("start") ? arg.get("start") : "0";
		String length = arg.containsKey("length") ? arg.get("length") : "10";
		JSONObject JsonSeaData = ODataHelper.GetObjDataLists("EXPERTBASEINFO", fields, query, order, group,
				Integer.parseInt(start), Integer.parseInt(length));
		return JsonSeaData.toString();
	}
}
