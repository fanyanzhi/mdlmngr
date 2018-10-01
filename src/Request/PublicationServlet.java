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
import BLL.AttentionMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;

/**
 * Servlet implementation class LiteratureServlet
 */
@WebServlet("/publication/*")
public class PublicationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PublicationServlet() {
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
		doPost(request,response);
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
		if (strToken != null) {
			strUserName = UserInfoMngr.UserLogin(strToken);
			if (strUserName.startsWith("@-")) {
				sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
				return;
			}
		}
		String appId = String.valueOf(request.getAttribute("app_id"));
		switch (strAction.toLowerCase()) {
			case "/checkattention":
				strRet = checkAttentionInfo(strUserName, mapInfo);
				break;
			case "/addattention":
				strRet = addAttentionInfo(appId, strUserName, mapInfo);
				break;
			case "/getattentioncount":
				strRet = getAttentionCount(strUserName,mapInfo);
				break;
			case "/getattentions":
				strRet = getAttentionList(strUserName, mapInfo);
				break;
			case "/cancelattention":
				strRet = cancelAttention(strUserName, mapInfo);
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

	private String checkAttentionInfo(String username,Map<String, Object> mapInfo){
		String ationid = (String)mapInfo.get("ationid");
		if(Common.IsNullOrEmpty(ationid)||Common.IsNullOrEmpty(username)){
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		Map<String,Integer> map = AttentionMngr.checkAttention(username, ationid);
		if(map==null||map.size()==0||!map.containsKey("count")){
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
		if(map.get("count")>0){
			return "{\"result\":true,\"isexist\":true,\"id\":" + map.get("id") + "}";
		}else{
			return "{\"result\":true,\"isexist\":false}";
		}
	}
	
	private String addAttentionInfo(String appid,String username,Map<String, Object> mapInfo) {
		String ationid = (String)mapInfo.get("ationid");
		String title = (String)mapInfo.get("title");
		String author = (String)mapInfo.get("author");
		String type = (String)mapInfo.get("type");
		if(Common.IsNullOrEmpty(appid)||Common.IsNullOrEmpty(ationid)||Common.IsNullOrEmpty(ationid)||Common.IsNullOrEmpty(username)){
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		int result = AttentionMngr.addAttention(appid, username,  ationid, title,author,type);
		if(result>0){
			return "{\"result\":true,\"id\":"+result+"}";
		}else{
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}
	private String getAttentionList(String UserName, Map<String, Object> arg) {
		int iStart = 1;
		int iLength = 20;//;Integer.parseInt(Common.GetConfig("PageSize"));
		if (arg.containsKey("start") && arg.get("start") != null) {
			iStart = Integer.valueOf(String.valueOf(arg.get("start")));
		}
		if (arg.containsKey("length") && arg.get("length") != null) {
			iLength = Integer.valueOf(String.valueOf(arg.get("length")));
		}
		List<Map<String, Object>> lstResult = AttentionMngr.getAttentionList(UserName, iStart,iLength);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", map.get("id"));
				jsonObj.put("username", map.get("username"));
				jsonObj.put("ationid", map.get("ationid"));
				jsonObj.put("title", map.get("title"));
				jsonObj.put("author", map.get("author"));
				jsonObj.put("type", map.get("type"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}
	private String cancelAttention(String UserName, Map<String, Object> arg) {
		String ids="";
		if (arg.containsKey("ids") && arg.get("ids") != null) {
			ids=String.valueOf(arg.get("ids"));
		}
		if(Common.IsNullOrEmpty(ids)){
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (AttentionMngr.delAllAttention(UserName, ids)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}
	private String getAttentionCount(String UserName,Map<String, Object> arg) {
		int count = AttentionMngr.getUserAttentionCount(UserName);
		return "{\"result\":true,\"data\":" + count + "}";
	}
}
