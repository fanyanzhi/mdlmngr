package Request;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.CnkiMngr;
import BLL.CommentMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;

/**
 * Servlet implementation class CommentServlet
 */
@WebServlet("/comment/*")
public class CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommentServlet() {
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
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String appid = String.valueOf(request.getAttribute("app_id"));
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}

		byte[] arrReq = new byte[request.getContentLength()];
		request.getInputStream().read(arrReq);
		String strReq = new String(arrReq, "utf-8");
		arrReq = null;

		JSONObject jo = null;
		Map<String, Object> mapInfo = null;
		JSONArray jsonArray = null;
		if ("count".equalsIgnoreCase(strAction.replace("/", "")) || "praisecount".equalsIgnoreCase(strAction.replace("/", ""))) {
			jsonArray = JSONArray.fromObject(strReq);
		} else {
			jo = JSONObject.fromObject(strReq);
			mapInfo = (Map<String, Object>) jo;

		}

		String strRet;
		String strUserName = null;
		String strTempAction = strAction.replace("/", "").toLowerCase();
		if ("add".equals(strTempAction) || "del".equals(strTempAction) || "update".equals(strTempAction) || "praise".equals(strTempAction) || "haspraised".equals(strTempAction) || "cancelpraise".equals(strTempAction)|| "userpraise".equals(strTempAction) || "ucomcount".equals(strTempAction) || "usercomment".equals(strTempAction) || "userpraise".equals(strTempAction)|| "userpraisecount".equals(strTempAction)) {
			String strToken = (String) mapInfo.get("usertoken");
			strUserName = UserInfoMngr.UserLogin(strToken);
			if (strUserName.startsWith("@-")) {
				sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
				return;
			}
		}

		if (mapInfo != null && mapInfo.containsKey("typeid")) {
			String[] arrType = CnkiMngr.getTypes((String) mapInfo.get("typeid"));
			if (arrType == null) {
				sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"));
				return;
			}
			mapInfo.put("typeid", arrType[0]);
			mapInfo.put("newtypeid", arrType[1]);
		}

		JSONArray newJsonArray = null;
		JSONObject recordObj = null;
		if (jsonArray != null && jsonArray.size() > 0) {
			newJsonArray = new JSONArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				recordObj = JSONObject.fromObject(jsonArray.get(i));
				if (recordObj.containsKey("typeid")) {
					String[] arrType = CnkiMngr.getTypes((String) recordObj.get("typeid"));
					if (arrType == null) {
						sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"));
						return;
					}
					recordObj.put("typeid", arrType[0]);
					recordObj.put("newtypeid", arrType[1]);
				}
				newJsonArray.add(recordObj);
			}
			if (newJsonArray.size() > 0) {
				jsonArray = JSONArray.fromObject(newJsonArray.toString());

			}
		}

		switch (strAction.replace("/", "").toLowerCase()) {
		case "isshow":
			strRet = isShowComment(mapInfo);
			break;
		case "add":
			strRet = addInfo(strUserName,appid, mapInfo);
			break;
		case "get":
			strRet = getInfo(mapInfo);
			break;
		case "del":
			strRet = delInfo(strUserName, mapInfo);
			break;
		case "update":
			strRet = updateInfo(strUserName, mapInfo);
			break;
		case "count":
			strRet = getCount(jsonArray);
			break;
		case "ucomcount":
			strRet = getUserCommentCount(strUserName);
			break;
		case "usercomment":
			strRet = getUserCommentList(strUserName, mapInfo);
			break;
		case "delusercomment":
			strRet = delUserCommentInfo(strUserName, mapInfo);
			break;	
		case "haspraised":
			strRet = hasPraised(strUserName, mapInfo);
			break;
		case "praise":
			strRet = addPraise(strUserName, appid, mapInfo);
			break;
		case "cancelpraise":
			strRet = cancelPraise(strUserName, mapInfo);
			break;
		case "praisecount":
			strRet = getPraiseCount(jsonArray);
			break;
		case "praiseuser":
			strRet = getPraiseUserList(mapInfo);
			break;
		case "userpraisecount":
			strRet=getUserPraiseCount(strUserName);
			break;
		case "userpraise":
			strRet = getUserPraiseList(strUserName, mapInfo);
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

	private String isShowComment(Map<String, Object> arg) {
		String strTypeID = (String) arg.get("typeid");
		String strFileID = (String) arg.get("fileid");
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		boolean bolShow = CommentMngr.isShow(strTypeID, strFileID);
		return "{\"result\":true,\"isshow\":".concat(String.valueOf(bolShow)).concat("}");
	}

	private String addInfo(String UserName, String AppID, Map<String, Object> arg) {
		String strTypeID = (String) arg.get("typeid");
		String strFileID = (String) arg.get("fileid");
		String strTitle = (String) arg.get("title");
		String strScore = (String) arg.get("score");
		String strContent = (String) arg.get("content");
		String strKeyWord = (String) arg.get("keyword");
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID) || Common.IsNullOrEmpty(strTitle) || Common.IsNullOrEmpty(strScore)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (strContent.length() > 300) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (CommentMngr.addCommentInfo(UserName, strTypeID, strFileID, strTitle, strScore, strContent,strKeyWord, AppID)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String getInfo(Map<String, Object> arg) {
		String strTypeID = (String) arg.get("typeid");
		String strFileID = (String) arg.get("fileid");

		int iStart = 1;
		int iLength = 20;
		if (arg.containsKey("start") && arg.get("start") != null) {
			iStart = Integer.valueOf(String.valueOf(arg.get("start")));
		}
		if (arg.containsKey("length") && arg.get("length") != null) {
			iLength = Integer.valueOf(String.valueOf(arg.get("length")));
		}
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		List<Map<String, Object>> lstResult = CommentMngr.getFrontCommentList(strTypeID, strFileID, iStart, iLength);

		String strDesc = null;
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				strDesc = (String) map.get("description");
				if (Common.IsNullOrEmpty(strDesc)) {
					strDesc = "";
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", map.get("id"));
				jsonObj.put("tablename", map.get("tablename"));
				jsonObj.put("username", map.get("username"));
				jsonObj.put("score", map.get("score"));
				jsonObj.put("content", map.get("content"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";

	}

	private String delInfo(String UserName, Map<String, Object> arg) {
		String strTime = (String) arg.get("time");
		String strID = (String) arg.get("id");
		if (Common.IsNullOrEmpty(strID) || Common.IsNullOrEmpty(strTime)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (CommentMngr.delComment(strID, UserName, strTime)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DELETEDATA.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DELETEDATA.code)).concat("}");
		}
	}
	private String delUserCommentInfo(String UserName, Map<String, Object> arg) {
		JSONArray array=null;
		try {
			array = (JSONArray) arg.get("array");
		} catch (Exception e) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (array==null||array.size()==0) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (CommentMngr.delComment(UserName,array)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DELETEDATA.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DELETEDATA.code)).concat("}");
		}
	}

	private String updateInfo(String UserName, Map<String, Object> arg) {
		String strTime = (String) arg.get("time");
		String strID = (String) arg.get("id");
		String strScore = (String) arg.get("score");
		String strContent = (String) arg.get("content");
		if (Common.IsNullOrEmpty(strID) || Common.IsNullOrEmpty(strTime) || Common.IsNullOrEmpty(strScore)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (strContent.length() > 300) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (CommentMngr.updateComment(strID, UserName, strTime, strScore, strContent)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_UPDATEDATA.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_UPDATEDATA.code)).concat("}");
		}
	}

	private String getCount(JSONArray JsonArray) {
		if (JsonArray.size() == 0) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String[] arrParam = new String[JsonArray.size()];
		Map<String, String> mapParam = new IdentityHashMap<String, String>();
		JSONObject recordObj = null;
		for (int i = 0; i < JsonArray.size(); i++) {
			recordObj = JSONObject.fromObject(JsonArray.get(i));
			mapParam.put(String.valueOf(recordObj.get("typeid")), String.valueOf(recordObj.get("fileid")));
			arrParam[i] = String.valueOf(recordObj.get("typeid")).concat("_").concat(String.valueOf(recordObj.get("fileid")));
		}
		Map<String, Integer> mapCount = CommentMngr.getCommentCount(mapParam);
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < arrParam.length; i++) {
			jsonArray.add(mapCount.get(arrParam[i]));
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	private String getUserCommentCount(String UserName) {
		int count = CommentMngr.getUserCommentCount(UserName);
		return "{\"result\":true,\"count\":" + count + "}";
	}

	private String getUserCommentList(String UserName, Map<String, Object> arg) {
		int iStart = 1;
		int iLength = 20;
		if (arg.containsKey("start") && arg.get("start") != null) {
			iStart = Integer.valueOf(String.valueOf(arg.get("start")));
		}
		if (arg.containsKey("length") && arg.get("length") != null) {
			iLength = Integer.valueOf(String.valueOf(arg.get("length")));
		}
		List<Map<String, Object>> lstResult = CommentMngr.getUserCommentList(UserName, iStart, iLength);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", map.get("id"));
				jsonObj.put("tablename", map.get("tablename"));
				jsonObj.put("typeid", CnkiMngr.getTypes((String) map.get("typeid"))[1]);
				jsonObj.put("fileid", map.get("fileid"));
				jsonObj.put("title", map.get("title"));
				jsonObj.put("score", map.get("score"));
				jsonObj.put("content", map.get("content"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	/*********** 推荐相关 ***********/
	/***
	 * 是否已推荐(暂不区分app信息，只要该用户已经点赞，就可以，即使不是在该app上点赞过)
	 * 
	 * @param UserName
	 * @param arg
	 * @return
	 */
	private String hasPraised(String UserName, Map<String, Object> arg) {
		String strTypeID = (String) arg.get("typeid");
		String strFileID = (String) arg.get("fileid");
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (CommentMngr.isExistsUserPraise(UserName, strTypeID, strFileID)) {
			return "{\"result\":true,\"isexist\":true}";
		} else {
			return "{\"result\":true,\"isexist\":false}";
		}
	}

	private String addPraise(String UserName, String AppID, Map<String, Object> arg) {
		String strTypeID = (String) arg.get("typeid");
		String strFileID = (String) arg.get("fileid");
		String strTitle = (String) arg.get("title");
		String strKeyWord = (String) arg.get("keyword");

		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID) || Common.IsNullOrEmpty(strTitle)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (CommentMngr.isExistsUserPraise(UserName, strTypeID, strFileID)) {
			return "{\"result\":true,\"isexist\":true}";
		}
		if (CommentMngr.addPraiseInfo(UserName, strTypeID, strFileID, strTitle, strKeyWord, AppID)) {
			return "{\"result\":true,\"count\":".concat(String.valueOf(CommentMngr.getPraiseCount(strTypeID, strFileID))).concat("}");
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	/***
	 * 取消推荐
	 * 
	 * @param UserName
	 * @param arg
	 * @return
	 */
	private String cancelPraise(String UserName, Map<String, Object> arg) {
		String strTypeID = (String) arg.get("typeid");
		String strFileID = (String) arg.get("fileid");
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (CommentMngr.cancelPraise(UserName, strTypeID, strFileID)) {
			return "{\"result\":true,\"count\":".concat(String.valueOf(CommentMngr.getPraiseCount(strTypeID, strFileID))).concat("}");
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	/**
	 * 
	 * @param arg
	 * @return
	 */
	private String getPraiseCount(JSONArray JsonArray) {
		if (JsonArray.size() == 0) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String[] arrParam = new String[JsonArray.size()];
		Map<String, String> mapParam = new IdentityHashMap<String, String>();
		JSONObject recordObj = null;
		for (int i = 0; i < JsonArray.size(); i++) {
			recordObj = JSONObject.fromObject(JsonArray.get(i));
			mapParam.put(String.valueOf(recordObj.get("typeid")), String.valueOf(recordObj.get("fileid")));
			arrParam[i] = String.valueOf(recordObj.get("typeid")).concat("_").concat(String.valueOf(recordObj.get("fileid")));
		}
		Map<String, Integer> mapCount = CommentMngr.getPraiseCount(mapParam);
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < arrParam.length; i++) {
			jsonArray.add(mapCount.get(arrParam[i]));
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	private String getUserPraiseCount(String UserName) {
		int count = CommentMngr.getUserPraiseCount(UserName);
		return "{\"result\":true,\"data\":" + count + "}";
	}
	/**
	 * 
	 * @param arg
	 * @return
	 */
	private String getPraiseUserList(Map<String, Object> arg) {
		String strTypeID = (String) arg.get("typeid");
		String strFileID = (String) arg.get("fileid");
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String strUsers = CommentMngr.getPraiseUsers(strTypeID, strFileID);
		JSONArray jsonArray = new JSONArray();
		if (!Common.IsNullOrEmpty(strUsers)) {
			String[] arrUsers = strUsers.split(";");
			for (String str : arrUsers) {
				jsonArray.add(str);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	/**
	 * 不兼容云阅读
	 * @param UserName
	 * @param arg
	 * @return
	 */
	private String getUserPraiseList(String UserName, Map<String, Object> arg) {
		int iStart = 1;
		int iLength = 20;
		if (arg.containsKey("start") && arg.get("start") != null) {
			iStart = Integer.valueOf(String.valueOf(arg.get("start")));
		}
		if (arg.containsKey("length") && arg.get("length") != null) {
			iLength = Integer.valueOf(String.valueOf(arg.get("length")));
		}

		List<Map<String, Object>> lstResult = CommentMngr.getUserPraises(UserName, iStart, iLength);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", map.get("title"));
				jsonObj.put("typeid", CnkiMngr.getTypes((String) map.get("typeid"))[1]);
				jsonObj.put("fileid", map.get("fileid"));
				jsonObj.put("time", map.get("inserttime"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

}
