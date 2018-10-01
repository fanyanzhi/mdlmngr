package Request;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import BLL.ImageMngr;
import BLL.Logger;
import BLL.ScholarMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Model.AuthImageInfoBean;
import Model.UserLoginBean;
import Util.Common;
import Util.LoggerFile;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class AuthServlet
 */
@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AuthServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String strID = request.getQueryString();
		if (strID == null) {
			return;
		}
		strID = strID.trim();
		byte[] arrImg = ImageMngr.getAuthImageContent(strID);
		if (arrImg == null) {
			return;
		}
		response.setContentType("image/gif");
		response.getOutputStream().write(arrImg);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		long start = System.currentTimeMillis();

		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"), start);
			return;
		}
		String strUserName = "";
		Map<String, String> mapReq = null;
		JSONArray jsonArray = null;
		//LoggerFile.appendMethod("d:\\log\\upload", strAction.replace("/", "").toLowerCase());
		if (!"upload".equals(strAction.replace("/", "").toLowerCase())) {
			long conLength = request.getContentLength();
			if(conLength>0){
				byte[] arrReq = new byte[request.getContentLength()];
				request.getInputStream().read(arrReq);
				String strReq = new String(arrReq, "utf-8");
				JSONObject jo = null;
				if ("expertstatus".equalsIgnoreCase(strAction.replace("/", ""))) { // 可能是在没有登录的情况下
					jsonArray = JSONArray.fromObject(strReq);
				}else{
					jo = JSONObject.fromObject(strReq);
					mapReq = (Map<String, String>) jo;
				}
			}
			String IP = "";
			if ("getclaim".equalsIgnoreCase(strAction.replace("/", ""))
					|| "addclaim".equalsIgnoreCase(strAction.replace("/", ""))
					|| "updateclaim".equalsIgnoreCase(strAction.replace("/", ""))
					|| "getauth".equalsIgnoreCase(strAction.replace("/", ""))
					|| "getauthstatus".equalsIgnoreCase(strAction.replace("/", ""))
					|| "addauth".equalsIgnoreCase(strAction.replace("/", ""))
					|| "cancelclaim".equalsIgnoreCase(strAction.replace("/", ""))
					|| "updateauth".equalsIgnoreCase(strAction.replace("/", ""))
					|| "authappeal".equalsIgnoreCase(strAction.replace("/", ""))
					|| "appealstatus".equalsIgnoreCase(strAction.replace("/", ""))
					|| "getappeal".equalsIgnoreCase(strAction.replace("/", ""))
					|| "updateappeal".equalsIgnoreCase(strAction.replace("/", ""))) {
				String strToken = (String) mapReq.get("usertoken");
				strUserName = UserInfoMngr.UserLogin(strToken);
				if (strUserName.startsWith("@-")) {
					sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
							.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"), start);
					return;
				}
			}
		}

		String strRet;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "upload":
			strRet = uploadFile(request, response);
			break;
		case "getclaim":
			strRet = getClaim(strUserName);
			break;
		case "addclaim":
			strRet = addClaim(strUserName, mapReq);
			break;
		case "cancelclaim":
			strRet = cancelClaim(strUserName, mapReq);
			break;
		case "updateclaim":
			strRet = updateClaim(strUserName, mapReq);
			break;
		case "expertstatus":
			strRet = expertstatus(jsonArray);
			break;
		case "expertnews":  //专家动态
			strRet = expertnews(mapReq);
			break;
		case "getauth":
			strRet = getAuthMsg(strUserName);
			break;
		case "getauthstatus":
			strRet = getAuthStatus(strUserName);
			break;
		case "addauth":
			strRet = addAuth(strUserName, mapReq);
			break;
		case "updateauth":
			strRet = updateAuth(strUserName, mapReq);
			break;
		case "getappeal":
			strRet = getAuthAppeal(strUserName);
			break;
		case "authappeal":
			strRet = addAuthAppeal(strUserName, mapReq);
			break;
		case "updateappeal":
			strRet = updateAuthAppeal(strUserName, mapReq);
			break;
		case "appealstatus":
			strRet = getAuthAppealStatus(strUserName);
			break;

		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}

		sendResponseData(response, strRet, start);
	}

	private void sendResponseData(HttpServletResponse response, String Data, long start) throws IOException {
		long end = System.currentTimeMillis();
		long timestmp = end - start;
		if (Data.startsWith("{")) {
			JSONObject json = JSONObject.fromObject(Data);
			String ip = Common.GetConfig("ServerIp");
			json.put("ip", ip);
			json.put("ProcessingTime", timestmp);
			response.getOutputStream().write(json.toString().getBytes("utf-8"));
			response.getOutputStream().close();
		} else {
			response.getOutputStream().write(Data.getBytes("utf-8"));
			response.getOutputStream().close();
		}
	}

	/**
	 * 获取认领状态，当用户认领作者时，如果作者没有被认领，就会直接成功。也就是有学者库
	 * 
	 * @param userName
	 * @return
	 */
	private String getClaim(String userName) {
		//System.out.println("1:"+userName);
		if (Common.IsNullOrEmpty(userName)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		List<Map<String, Object>> lst = ScholarMngr.getExpertClaim(userName);
		if (lst == null || lst.size() == 0) {
			return "{\"result\":true,\"count\":0}";
		}
		JSONObject retJson = new JSONObject();
		retJson.put("result", true);
		retJson.put("count", 1);
		retJson.put("expcode", lst.get(0).get("expcode").toString());
		retJson.put("realname", lst.get(0).get("realname").toString());
		retJson.put("workunit", lst.get(0).get("workunit")==null?"":lst.get(0).get("workunit").toString());
		retJson.put("lastunit",lst.get(0).get("lastunit")==null?"":lst.get(0).get("lastunit").toString());
		retJson.put("phone", lst.get(0).get("phone").toString());
		retJson.put("email", lst.get(0).get("email").toString());
		return retJson.toString();
	}

	/**
	 * 保存认领信息
	 * 
	 * @param userName
	 * @param paramMap
	 * @return realname,workunit,lastunit,phone,email
	 */
	private String addClaim(String userName, Map<String, String> paramMap) {
		String expcode = paramMap.containsKey("expcode") ? (String) paramMap.get("expcode") : "";
		String realname = paramMap.containsKey("realname") ? (String) paramMap.get("realname") : "";
		String workunit = paramMap.containsKey("workunit") ? (String) paramMap.get("workunit") : "";
		String lastunit = paramMap.containsKey("lastunit") ? (String) paramMap.get("lastunit") : "";
		String phone = paramMap.containsKey("phone") ? (String) paramMap.get("phone") : "";
		String email = paramMap.containsKey("email") ? (String) paramMap.get("email") : "";
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(expcode) || Common.IsNullOrEmpty(realname)
				|| Common.IsNullOrEmpty(workunit) || Common.IsNullOrEmpty(phone) || Common.IsNullOrEmpty(email)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if(ScholarMngr.existUserClaim(userName, expcode)){
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLAIMED.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLAIMED.code)).concat("}");
		}
		if (ScholarMngr.saveClaim(userName, expcode, realname, workunit, lastunit, phone, email)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String cancelClaim(String userName, Map<String, String> paramMap) {
		String expcode = paramMap.containsKey("expcode") ? (String) paramMap.get("expcode") : "";

		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(expcode)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ScholarMngr.cancelClaim(userName, expcode)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	/**
	 * 修改认领信息
	 * 
	 * @param userName
	 * @param paramMap
	 * @return
	 */
	private String updateClaim(String userName, Map<String, String> paramMap) {
		String realname = paramMap.containsKey("realname") ? (String) paramMap.get("realname") : "";
		String workunit = paramMap.containsKey("workunit") ? (String) paramMap.get("workunit") : "";
		String lastunit = paramMap.containsKey("lastunit") ? (String) paramMap.get("lastunit") : "";
		String phone = paramMap.containsKey("phone") ? (String) paramMap.get("phone") : "";
		String email = paramMap.containsKey("email") ? (String) paramMap.get("email") : "";
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(realname) || Common.IsNullOrEmpty(workunit)
				|| Common.IsNullOrEmpty(phone) || Common.IsNullOrEmpty(email)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ScholarMngr.updateClaim(userName, realname, workunit, lastunit, phone, email)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	public String expertstatus(JSONArray jsonArray) {
		if (jsonArray == null || jsonArray.size() == 0) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		List<Map<String, Object>> lst = ScholarMngr.expertstatus(jsonArray);
		Map<String, Map<String, Object>> tmap = new TreeMap<String, Map<String, Object>>();
		if (lst != null) {
			Iterator<Map<String, Object>> it = lst.iterator();
			Map<String, Object> map = null;
			while (it.hasNext()) {
				map = it.next();
				tmap.put(map.get("expcode").toString(), map);
			}
		}
		JSONArray arrresult = new JSONArray();
		@SuppressWarnings("unchecked")
		Iterator<Object> it = jsonArray.iterator();
		while (it.hasNext()) {
			JSONObject ob = (JSONObject) it.next();
			String excode = ob.getString("expertcode");
			if (Common.IsNullOrEmpty(excode))
				continue;
			JSONObject json = new JSONObject();
			if(tmap.containsKey(excode)) {
				json.put(excode, 1);
				Map<String, Object> map = tmap.get(excode);
				json.put("username", map.get("username").toString());
			}else {
				json.put(excode, 0);
			}
			arrresult.add(json);
		}
		return "{\"result\":true,\"data\":" + arrresult.toString() + "}";

	}
	
	private String expertnews(Map<String, String> paramMap){
		String begintime = paramMap.containsKey("begintime")?(String)paramMap.get("begintime"):"";
		String endtime = paramMap.containsKey("endtime")?(String)paramMap.get("endtime"):"";
		JSONArray jsonArray = new JSONArray();
		List<Map<String, Object>> lst = ScholarMngr.getExpertNews(begintime, endtime);
		Iterator<Map<String, Object>> iterator = lst.iterator();
		Map<String, Object> map = null;
		while(iterator.hasNext()){
			map = iterator.next();
			JSONObject ob = new JSONObject();
			ob.put("realname", map.get("realname").toString());
			ob.put("expcode", map.get("expcode").toString());
			ob.put("username", map.get("username").toString());
			ob.put("time", map.get("time").toString());
			jsonArray.add(ob);
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	/********************
	 * 申诉相关--》申诉通过应该把apply的直接到claim中，然后认证显示通过，
	 **************************/

	/**
	 * 添加申诉信息
	 * 
	 * @param userName
	 * @param paramMap
	 * @return
	 */
	private String addAuthAppeal(String userName, Map<String, String> paramMap) {
		String expcode = paramMap.containsKey("expcode") ? (String) paramMap.get("expcode") : "";
		String realname = paramMap.containsKey("realname") ? (String) paramMap.get("realname") : "";
		String workunit = paramMap.containsKey("workunit") ? (String) paramMap.get("workunit") : "";
		String phone = paramMap.containsKey("phone") ? (String) paramMap.get("phone") : "";
		String email = paramMap.containsKey("email") ? (String) paramMap.get("email") : "";
		String cause = paramMap.containsKey("cause") ? (String) paramMap.get("cause") : "";
		if(!Common.IsNullOrEmpty(cause)) {
			try {
				cause = URLDecoder.decode(cause, "utf-8");
			} catch (UnsupportedEncodingException e) {
			}
		}

		String front = paramMap.containsKey("front") ? (String) paramMap.get("front") : "";

		String back = paramMap.containsKey("back") ? (String) paramMap.get("back") : "";

		String cardNum = paramMap.containsKey("cardnum") ? (String) paramMap.get("cardnum") : "";

		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(expcode) || Common.IsNullOrEmpty(realname)
				|| Common.IsNullOrEmpty(workunit) || Common.IsNullOrEmpty(phone) || Common.IsNullOrEmpty(email)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ScholarMngr.addAuthAppeal(userName, expcode, realname, workunit, phone, email, cause, cardNum, front,
				back)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String getAuthAppeal(String userName) {
		if (Common.IsNullOrEmpty(userName)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		List<Map<String, Object>> lst = ScholarMngr.getAuthAppeal(userName);
		if (lst == null || lst.size() == 0) {
			return "{\"result\":true,\"count\":0}";
		}
		return "{\"result\":true,\"count\":1,\"expcode\":\"" + lst.get(0).get("expcode").toString()
				+ "\",\"realname\":\"" + lst.get(0).get("realname").toString() + "\",\"workunit\":\""
				+ lst.get(0).get("workunit").toString() + "\",\"cause\":\"" + lst.get(0).get("cause").toString()
				+ "\",\"phone\":\"" + lst.get(0).get("phone").toString() + "\",\"email\":\""
				+ lst.get(0).get("email").toString() + "\",\"cardnum\":\"" + lst.get(0).get("cardnum").toString()
				+ "\",\"front\":\"" + lst.get(0).get("front").toString() + "\",\"back\":\""
				+ lst.get(0).get("back").toString() + "\"}";
	}

	/*
	 * 获取申诉状态
	 */
	private String getAuthAppealStatus(String userName) {
		if (Common.IsNullOrEmpty(userName)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String iRet = ScholarMngr.getAuthAppealStatus(userName);
		return "{\"result\":true,\"status\":" + iRet + "}";
	}

	private String updateAuthAppeal(String userName, Map<String, String> paramMap) {
		String realname = paramMap.containsKey("realname") ? (String) paramMap.get("realname") : "";
		String workunit = paramMap.containsKey("workunit") ? (String) paramMap.get("workunit") : "";
		String phone = paramMap.containsKey("phone") ? (String) paramMap.get("phone") : "";
		String email = paramMap.containsKey("email") ? (String) paramMap.get("email") : "";
		String cause = paramMap.containsKey("cause") ? (String) paramMap.get("cause") : "";
		String front = paramMap.containsKey("front") ? (String) paramMap.get("front") : "";
		String back = paramMap.containsKey("back") ? (String) paramMap.get("back") : "";
		String cardNum = paramMap.containsKey("cardnum") ? (String) paramMap.get("cardnum") : "";
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(realname) || Common.IsNullOrEmpty(workunit)
				|| Common.IsNullOrEmpty(phone) || Common.IsNullOrEmpty(email)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ScholarMngr.updateAuthAppeal(userName, realname, workunit, phone, email, cause, front, back, cardNum)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	/**
	 * 获取申诉信息
	 */

	/******************* 认证相关 **************************/
	/**
	 * 获得认证信息
	 * 
	 * @param userName
	 * @return
	 */
	private String getAuthMsg(String userName) {
		if (Common.IsNullOrEmpty(userName)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		List<Map<String, Object>> lst = ScholarMngr.getAuthMsg(userName);
		if (lst == null || lst.size() == 0) {
			return "{\"result\":true,\"count\":0}";
		}
		return "{\"result\":true,\"count\":1,\"cardnum\":\"" + lst.get(0).get("cardnum") + "\",\"front\":\""
				+ lst.get(0).get("front") + "\",\"back\":\"" + lst.get(0).get("back")
				+ "\",\"cause\":\"" + lst.get(0).get("cause") + "\"}";
	}

	/**
	 * 获取认证状态
	 * 
	 * @param userName
	 * @return -2表示未提交认证，-1表示认证失败，可以修改认证信息，0表示已提交认证，未审批不允许修改。1表示认证成功，不允许修改
	 */
	private String getAuthStatus(String userName) {
		if (Common.IsNullOrEmpty(userName)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String iRet = ScholarMngr.getAuthStatus(userName);
		return "{\"result\":true,\"status\":" + iRet + "}";
	}

	/**
	 * 提交认证信息 cause 限制字数,front和back限制图片大小
	 * 
	 * @param userName
	 * @param paramMap
	 * @return
	 */
	private String addAuth(String userName, Map<String, String> paramMap) {
		String cardNum = paramMap.containsKey("cardnum") ? (String) paramMap.get("cardnum") : "";
		String front = paramMap.containsKey("front") ? (String) paramMap.get("front") : "";
		String back = paramMap.containsKey("back") ? (String) paramMap.get("back") : "";
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(front) || Common.IsNullOrEmpty(back)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ScholarMngr.addAuth(userName, cardNum, front, back)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	/**
	 * 修改认证信息
	 * 
	 * @param userName
	 * @param paramMap
	 * @return
	 */
	private String updateAuth(String userName, Map<String, String> paramMap) {
		String cardNum = paramMap.containsKey("cardnum") ? (String) paramMap.get("cardnum") : "";
		String front = paramMap.containsKey("front") ? (String) paramMap.get("front") : "";
		String back = paramMap.containsKey("back") ? (String) paramMap.get("back") : "";
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(front) || Common.IsNullOrEmpty(back)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ScholarMngr.updateAuth(userName, cardNum, front, back)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	/*
	 * public String uploadFile(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException {
	 * 
	 * String savePath =
	 * this.getServletContext().getRealPath("/WEB-INF/upload"); //
	 * 上传时生成的临时文件保存目录 String tempPath =
	 * this.getServletContext().getRealPath("/WEB-INF/temp"); File tmpFile = new
	 * File(tempPath); if (!tmpFile.exists()) { // 创建临时目录 tmpFile.mkdir(); }
	 * 
	 * // 消息提示 String message = ""; try { // 使用Apache文件上传组件处理文件上传步骤： //
	 * 1、创建一个DiskFileItemFactory工厂 DiskFileItemFactory factory = new
	 * DiskFileItemFactory(); //
	 * 设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
	 * factory.setSizeThreshold(1024 * 100);//
	 * 设置缓冲区的大小为100KB，如果不指定，那么缓冲区的大小默认是10KB // 设置上传时生成的临时文件的保存目录
	 * factory.setRepository(tmpFile); // 2、创建一个文件上传解析器 ServletFileUpload upload
	 * = new ServletFileUpload(factory); // 监听文件上传进度
	 * 
	 * upload.setProgressListener(new ProgressListener(){ public void
	 * update(long pBytesRead, long pContentLength, int arg2) {
	 * System.out.println("文件大小为：" + pContentLength + ",当前已处理：" + pBytesRead);
	 * 
	 *//**
		 * 文件大小为：14608,当前已处理：4096 文件大小为：14608,当前已处理：7367 文件大小为：14608,当前已处理：11419
		 * 文件大小为：14608,当前已处理：14608
		 *//*
		 * 
		 * float f = pBytesRead/pContentLength; try {
		 * response.getWriter().write(f+""); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * } });
		 * 
		 * // 解决上传文件名的中文乱码 upload.setHeaderEncoding("UTF-8"); //
		 * 3、判断提交上来的数据是否是上传表单的数据 if
		 * (!ServletFileUpload.isMultipartContent(request)) { // 按照传统方式获取数据
		 * return ""; }
		 * 
		 * // 设置上传单个文件的大小的最大值，目前是设置为1024*1024字节，也就是1MB
		 * upload.setFileSizeMax(1024 * 1024 * 10); //
		 * 设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和，目前设置为10MB upload.setSizeMax(1024
		 * * 1024 * 20); //
		 * 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，
		 * 每一个FileItem对应一个Form表单的输入项 List<FileItem> list =
		 * upload.parseRequest(request); for (FileItem item : list) { //
		 * 如果fileitem中封装的是普通输入项的数据 if (item.isFormField()) { String name =
		 * item.getFieldName(); // 解决普通输入项的数据的中文乱码问题 String value =
		 * item.getString("UTF-8"); // value = new
		 * String(value.getBytes("iso8859-1"),"UTF-8"); System.out.println(name
		 * + "=" + value); } else {// 如果fileitem中封装的是上传文件 // 得到上传的文件名称， String
		 * filename = item.getName(); System.out.println(filename); if (filename
		 * == null || filename.trim().equals("")) { continue; } //
		 * 注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如： //
		 * c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt // 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
		 * filename = filename.substring(filename.lastIndexOf("\\") + 1); //
		 * 得到上传文件的扩展名 String fileExtName =
		 * filename.substring(filename.lastIndexOf(".") + 1); //
		 * 如果需要限制上传的文件类型，那么可以通过文件的扩展名来判断上传的文件类型是否合法
		 * System.out.println("上传的文件的扩展名是：" + fileExtName); // 获取item中的上传文件的输入流
		 * InputStream in = item.getInputStream(); // 得到文件保存的名称 String
		 * saveFilename = makeFileName(filename); // 得到文件的保存目录 String
		 * realSavePath = makePath(saveFilename, savePath);
		 * System.out.println(realSavePath); // 创建一个文件输出流 FileOutputStream out =
		 * new FileOutputStream(realSavePath + "\\" + saveFilename); // 创建一个缓冲区
		 * byte buffer[] = new byte[1024]; // 判断输入流中的数据是否已经读完的标识 int len = 0; //
		 * 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据 while ((len =
		 * in.read(buffer)) > 0) { //
		 * 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" // + filename)当中
		 * out.write(buffer, 0, len); } // 关闭输入流 in.close(); // 关闭输出流
		 * out.close(); // 删除处理文件上传时生成的临时文件 item.delete(); message = "文件上传成功！";
		 * } } } catch (FileUploadBase.FileSizeLimitExceededException e) {
		 * e.printStackTrace(); request.setAttribute("message", "单个文件超出最大值！！！");
		 * request.getRequestDispatcher("/message.jsp").forward(request,
		 * response); return ""; } catch
		 * (FileUploadBase.SizeLimitExceededException e) { e.printStackTrace();
		 * request.setAttribute("message", "上传文件的总的大小超出限制的最大值！！！");
		 * request.getRequestDispatcher("/message.jsp").forward(request,
		 * response); return ""; } catch (Exception e) { message = "文件上传失败！";
		 * e.printStackTrace(); }
		 * 
		 * // request.setAttribute("message", message);
		 * 
		 * }
		 */

	/**
	 * com.mysql.jdbc.PacketTooBigException: Packet for query is too large
	 * (3730266 > 1048576). You can cha
	 * http://blog.csdn.net/fly0744/article/details/13623079
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String uploadFile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String tempPath = this.getServletContext().getRealPath("/WEB-INF/temp");
		//LoggerFile.appendMethod("d:\\log\\upload", tempPath);
		File tmpFile = new File(tempPath);
		if (!tmpFile.exists()) {
			tmpFile.mkdir();
		}

		String message = "";
		try {
			String saveFilename = "";
			String fileExtName = "";
			long fileSize = 0;
			byte imgdata[] = null;
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024 * 100);
			factory.setRepository(tmpFile);
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				return "";
			}

			upload.setFileSizeMax(1024 * 1024 * 10);
			upload.setSizeMax(1024 * 1024 * 20);
			@SuppressWarnings("unchecked")
			List<FileItem> list = upload.parseRequest(request);
			String filename = "";
			for (FileItem item : list) {
				if (item.isFormField()) {
					String name = item.getFieldName();
					String value = item.getString("UTF-8");
//					System.out.println(name + "=" + value);
				} else {
					filename = item.getName();
					if (filename == null || filename.trim().equals("")) {
						continue;
					}
					filename = filename.substring(filename.lastIndexOf("\\") + 1);
					fileExtName = filename.substring(filename.lastIndexOf(".") + 1);
					saveFilename = makeFileName(filename);
					InputStream in = item.getInputStream();
					ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
					byte buffer[] = new byte[1024];
					int len = 0;
					while ((len = in.read(buffer)) > 0) {
						fileSize = fileSize + len;
						bytestream.write(buffer, 0, len);
					}
					in.close();
					imgdata = bytestream.toByteArray();
					bytestream.close();
					item.delete();
				}
			}
			AuthImageInfoBean info = new AuthImageInfoBean();
			info.setName(filename);
			info.setExtName(fileExtName);
			info.setModule(101);
			info.setForeignID("");
			info.setSize(fileSize);
			info.setWidth(0);
			info.setHeight(0);
			info.setAppid("");
			info.setImageid(saveFilename);

			info.setContent(imgdata);
			if (saveFilename.equals(ImageMngr.addAuthImageInfo(info))) {
				message = "{\"result\":true,\"imageid\":\"" + saveFilename + "\"}";
			} else {
				message = "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
			}
		} catch (FileUploadBase.FileSizeLimitExceededException e) {
			e.printStackTrace();
			message = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_OUTMAXSIZE.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_OUTMAXSIZE.code)).concat("}");
		} catch (FileUploadBase.SizeLimitExceededException e) {
			e.printStackTrace();
			message = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_IMAGEMAXCOUNT.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_IMAGEMAXCOUNT.code)).concat("}");
		} catch (Exception e) {
			message = "文件上传失败！";
			message = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
		return message;
		// request.setAttribute("message", message);

	}

	/**
	 * @Method: makeFileName
	 * @Description: 生成上传文件的文件名，文件名以：uuid+"_"+文件的原始名称
	 * @Anthor:孤傲苍狼
	 * @param filename
	 *            文件的原始名称
	 * @return uuid+"_"+文件的原始名称
	 */
	private String makeFileName(String filename) { // 2.jpg
		// 为防止文件覆盖的现象发生，要为上传文件产生一个唯一的文件名
		// return UUID.randomUUID().toString() + "_" + filename;
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 为防止一个目录下面出现太多文件，要使用hash算法打散存储
	 * 
	 * @Method: makePath
	 * @Description:
	 * @Anthor:孤傲苍狼
	 * 
	 * @param filename
	 *            文件名，要根据文件名生成存储目录
	 * @param savePath
	 *            文件存储路径
	 * @return 新的存储目录
	 */
	private String makePath(String filename, String savePath) {
		// 得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
		int hashcode = filename.hashCode();
		int dir1 = hashcode & 0xf; // 0--15
		int dir2 = (hashcode & 0xf0) >> 4; // 0-15
		// 构造新的保存目录
		String dir = savePath + "\\" + dir1 + "\\" + dir2; // upload\2\3
															// upload\3\5
		// File既可以代表文件也可以代表目录
		File file = new File(dir);
		// 如果目录不存在
		if (!file.exists()) {
			// 创建目录
			file.mkdirs();
		}
		return dir;
	}
}
