package Request;

import java.io.DataInputStream;
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
import BLL.MeetingMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;

/**
 * Servlet implementation class LiteratureServlet
 */
@WebServlet("/meeting/*")
public class MeetingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MeetingServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		// String strAction = request.getPathInfo();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");

		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\"}"));
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
		@SuppressWarnings("unchecked")
		Map<String, Object> mapInfo = (Map<String, Object>) jo;
		String strToken = (String) mapInfo.get("usertoken");
		String strRet = null;
		String strUserName = null;
		//if (strToken != null) {
			strUserName = UserInfoMngr.UserLogin(strToken);
			if (strUserName.startsWith("@-")) {
				sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
				return;
			}
		//}
		String appId = String.valueOf(request.getAttribute("app_id"));
		switch (strAction.toLowerCase()) {
			case "/check":
				strRet = checkMeetingInfo(strUserName, mapInfo);
				break;
			case "/add":
				strRet = addMeetingInfo(appId, strUserName, mapInfo);
				break;
			case "/gets":
				strRet = getMeetingList(strUserName);
				break;
			case "/cancel":
				strRet = cancelMeeting(strUserName, mapInfo);
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

	private String checkMeetingInfo(String username,Map<String, Object> arg){
		String sortcode="";
		if (arg.containsKey("sortcode") && arg.get("sortcode") != null) {
			sortcode=String.valueOf(arg.get("sortcode"));
		}
		if(Common.IsNullOrEmpty(sortcode)){
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String count = MeetingMngr.checkAttention(username, sortcode);
		if("1".equals(count)){
			return "{\"result\":true,\"isexist\":true}";
		}else{
			return "{\"result\":true,\"isexist\":false}";
		}
	}
	
	private String addMeetingInfo(String appid,String username,Map<String, Object> arg) {
		String sortcode="";
		if (arg.containsKey("sortcode") && arg.get("sortcode") != null) {
			sortcode=String.valueOf(arg.get("sortcode"));
		}
		if(Common.IsNullOrEmpty(appid)||Common.IsNullOrEmpty(sortcode)){
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		boolean result =MeetingMngr.addAttention(username, sortcode);
		if(result){
			Map<String, Object> map = MeetingMngr.getLastRecord(username);
			if (map != null)
				return "{\"result\":true,\"id\":" + map.get("id") + ",\"time\":\"" + map.get("time") + "\"}";
		}
		return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		
	}
	
	private String getMeetingList(String UserName){
		List<Map<String, Object>> lstResult = MeetingMngr.getAtionSubjectList(UserName, 1, 7);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", map.get("id"));
				jsonObj.put("name", map.get("name"));
				jsonObj.put("username", map.get("username"));
				jsonObj.put("sortcode", map.get("sortcode"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}
	
	private String cancelMeeting(String UserName, Map<String, Object> arg) {
		String ids="";
		if (arg.containsKey("ids") && arg.get("ids") != null) {
			ids=String.valueOf(arg.get("ids"));
		}
		if(Common.IsNullOrEmpty(ids)){
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (MeetingMngr.delAttention(UserName, ids)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}
}
