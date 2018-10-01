package Request;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.KuaiBaoMngr;
import BLL.MeetingMngr;
import BLL.ODataHelper;
import BLL.ProjectMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import BLL.ZjclsMngr;

/**
 * Servlet implementation class ZjclsServlet
 */
@WebServlet("/zjcls/*")
public class ZjclsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ZjclsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String ip = Common.getClientIP(request);
		/*if (!"10.1.201.178".equals(ip))
			return;*/
		String name = request.getParameter("name");
		
		String code = request.getParameter("code");
		String count = request.getParameter("count");
		if(Common.IsNullOrEmpty(code)||Common.IsNullOrEmpty(count))
			return;
		writeLog("zjclspush",name+":"+code+":"+count);
		if (Common.IsNullOrEmpty(name) || Common.IsNullOrEmpty(code) || Common.IsNullOrEmpty(count))
			return;
		KuaiBaoMngr.iosPush(code, name, count);
		KuaiBaoMngr.xiaoMiPush(code, name, count);
		KuaiBaoMngr.huaWeiPush(code, name, count);
		KuaiBaoMngr.uMengPush(code, name, count);

	}
	public static void main(String[] args) {
		KuaiBaoMngr.huaWeiPush("128010202", "声学",  "19");
	}
	
	public static void writeLog(String folder, String data) {
		String time = Common.GetDate();
		File file = new File("d:\\" + folder + time + ".txt");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter sucsessFile = new FileWriter(file, true);
			sucsessFile.write(data + "\r\n");
			sucsessFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
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
		Map<String, String> mapReq = null;
		int iLength = request.getContentLength();
		if (iLength > 0) {
			byte[] arrReq = new byte[iLength];
			request.getInputStream().read(arrReq);
			String strReq = new String(arrReq, "utf-8");
			arrReq = null;

			JSONObject jo = JSONObject.fromObject(strReq);
			mapReq = (Map<String, String>) jo;
		}

		String strRet;
		String strUserName = null;
		if (!"hot".equals(strAction.replace("/", "").toLowerCase())
				&& !"count".equals(strAction.replace("/", "").toLowerCase())) {
			if (mapReq != null) {
				if (mapReq.get("usertoken") != null) {
					String strToken = (String) mapReq.get("usertoken");
					strUserName = UserInfoMngr.UserLogin(strToken);
					if (strUserName.startsWith("@-")) {
						if (!"recommend".equals(strAction.replace("/", "").toLowerCase())) {
							sendResponseData(response,
									"{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
											.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
							return;
						}
					}
				}
			}
		}
		switch (strAction.replace("/", "").toLowerCase()) {
		case "add":
			strRet = addAtionZjcls(strUserName, mapReq, appid);
			break;
		case "cancel":
			strRet = cancelAtionZjcls(strUserName, mapReq, appid);
			break;
		case "gets":
			strRet = getZjclsList(strUserName);
			break;
		case "chkation":
			strRet = chkAtionZjcls(strUserName, mapReq);
			break;
		case "reval":
			strRet = getRevalZjcls(strUserName);
			break;
		case "hot":
			strRet = getHotZjcls();
			break;
		case "count":
			strRet = getZjclsCount(mapReq);
			break;
		case "recommend":
			strRet = getRecommendExpression(strUserName, mapReq);
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

	private String addAtionZjcls(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ZjclsMngr.addAttention(userName, code)) {
			KuaiBaoMngr.addAttention(userName, code);
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String cancelAtionZjcls(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (ZjclsMngr.cancelAttention(userName, code)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String getZjclsList(String userName) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
		int iCount = ZjclsMngr.getAtionSubjectCount(userName);
		if (iCount == 0) {
			return "{\"result\":true,\"count\":0,\"data\":" + jsonArray.toString() + "}";
		}
		List<Map<String, Object>> lstZjcls = ZjclsMngr.getAtionSubjectList(userName);
		if (lstZjcls != null && lstZjcls.size() > 0) {
			for (Map<String, Object> map : lstZjcls) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", map.get("id"));
				jsonObj.put("sortcode", map.get("sortcode"));
				jsonObj.put("name", map.get("name"));
				jsonObj.put("pathname", map.get("pathname"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + iCount + ",\"data\":" + jsonArray.toString() + "}";
	}

	private String chkAtionZjcls(String userName, Map<String, String> arg) throws ServletException, IOException {
		String code = (String) arg.get("code");
		if (Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String[] arrcode = code.split(";");
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < arrcode.length; i++) {
			JSONObject jsonObj = new JSONObject();
			if (Common.IsNullOrEmpty(arrcode[i]))
				continue;
			jsonObj.put("name", arrcode[i]);
			jsonObj.put("val", ZjclsMngr.checkAttention(userName, arrcode[i]));
			jsonArray.add(jsonObj);
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	private String getRevalZjcls(String userName) throws ServletException, IOException {
		Map<String, String> resMap = new HashMap<String, String>();
		Map<String, String> revMap = new HashMap<String, String>();

		List<Map<String, Object>> lstZjcls = ZjclsMngr.getAtionSubjectList(userName);
		String code = "";
		if (lstZjcls != null && lstZjcls.size() > 0) {
			for (Map<String, Object> map : lstZjcls) {
				code = map.get("sortcode").toString();
				if (!resMap.containsKey(map.get("sortcode"))) {
					revMap = ZjclsMngr.getRelevantZjcls(code);
					if (revMap != null && revMap.size() > 0) {
						for (Map.Entry<String, String> entry : revMap.entrySet()) {
							resMap.put(entry.getKey(), entry.getValue());
						}
					}
				} else {
					resMap.remove(code);
				}
			}
		}
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		int i = 0;
		for (Entry<String, String> entry : resMap.entrySet()) {
			i++;
			jsonObj.put("code", entry.getKey());
			jsonObj.put("name", entry.getValue());
			jsonArray.add(jsonObj);
			if (i > 4)
				break;
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	private String getHotZjcls() throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
		List<Map<String, Object>> lstHotZjcls = ZjclsMngr.getHotZjcls();
		if (lstHotZjcls != null && lstHotZjcls.size() > 0) {
			for (Map<String, Object> map : lstHotZjcls) {
				JSONObject jsonObj = new JSONObject();
				for (Entry<String, Object> entry : map.entrySet()) {
					jsonObj.put(entry.getKey(), entry.getValue());
				}
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	private String getZjclsCount(Map<String, String> arg) throws ServletException, IOException {
		return "";
	}

	/*
	 * private String addSubject(HttpServletRequest request) throws
	 * ServletException, IOException {
	 * 
	 * } private String getSubjectList(HttpServletRequest request) throws
	 * ServletException, IOException { String condition =
	 * request.getParameter("query"); String order =
	 * request.getParameter("order"); int iStart = 1; int iLength = 20; if
	 * (request.getParameter("start") != null) { try { iStart =
	 * Integer.valueOf(request.getParameter("start")); } catch (Exception e) {
	 * iStart = 1; } } if (request.getParameter("length") != null) { try {
	 * iLength = Integer.valueOf(request.getParameter("length")); } catch
	 * (Exception e) { iLength = 20; } } if (iLength > 50) { iLength = 20; } int
	 * iCount = SubjectMngr.getSubjectCount(condition); if(iCount == 0){ return
	 * "{\"result\":true,\"count\":0,\"data\":\"\"}"; } List<Map<String,
	 * Object>> lstResult = SubjectMngr.getSubjectList(condition, order, iStart,
	 * iLength); JSONArray jsonArray = new JSONArray(); if (lstResult != null) {
	 * for (Map<String, Object> map : lstResult) { JSONObject jsonObj = new
	 * JSONObject(); jsonObj.put("id", map.get("id")); jsonObj.put("code",
	 * map.get("code")); jsonObj.put("name", map.get("name"));
	 * jsonObj.put("grade", map.get("grade")); jsonObj.put("parentcode",
	 * map.get("parentcode")); jsonObj.put("ishavechild",
	 * map.get("ishavechild")); jsonObj.put("pathcode", map.get("pathcode"));
	 * jsonObj.put("pathname", map.get("pathname")); jsonArray.add(jsonObj); } }
	 * return "{\"result\":true,\"count\":"+iCount+",\"data\":" +
	 * jsonArray.toString() + "}"; }
	 */

	private String getRecommendExpression(String userName, Map<String, String> arg)
			throws ServletException, IOException {
		// String clientid = (String) arg.get("clientid");
		if (Common.IsNullOrEmpty(userName) || userName.startsWith("@-")) {
			return "{\"result\":true,\"data\":{\"type\":\"Literature{CJFD,CDFD,CMFD,CCND,CPFD}\",\"query\":\"\",\"order\":\"UpdateDate desc\",\"length\":\"10\"}}";
		}

		Map<String, String> resMap = new HashMap<String, String>();
		Map<String, String> revMap = new HashMap<String, String>();

		List<Map<String, Object>> lstZjcls = ZjclsMngr.getAtionSubjectList(userName);
		String code = "";
		if (lstZjcls != null && lstZjcls.size() > 0) {
			for (Map<String, Object> map : lstZjcls) {
				code = map.get("sortcode").toString();
				if (!resMap.containsKey(map.get("sortcode"))) {
					revMap = ZjclsMngr.getRelevantZjcls(code);
					if (revMap != null && revMap.size() > 0) {
						for (Map.Entry<String, String> entry : revMap.entrySet()) {
							resMap.put(entry.getKey(), entry.getValue());
						}
					}
				} else {
					resMap.remove(code);
				}
			}
		} else {
			return "{\"result\":true,\"data\":{\"type\":\"Literature{CJFD,CDFD,CMFD,CCND,CPFD}\",\"query\":\"\",\"order\":\"UpdateDate desc\",\"length\":\"10\"}}";
		}
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		int i = 0;
		for (Entry<String, String> entry : resMap.entrySet()) {
			i++;
			jsonObj.put("type", "Literature{CJFD,CDFD,CMFD,CCND,CPFD}");
			jsonObj.put("code", entry.getKey());
			jsonObj.put("name", entry.getValue());
			jsonObj.put("query", "IndustryCatagoryCode = '" + entry.getKey() + "?'");
			jsonObj.put("order", "UpdateDate");
			jsonObj.put("length", "2");
			jsonArray.add(jsonObj);
			if (i > 4)
				break;
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";

	}

	private String getIndexZjcls(String userName) {
		JSONObject result = new JSONObject(); // 返回结果
		boolean orderFlag = false; // 定制相关
		boolean remFlag = false; // 推荐相关

		// 会议
		/*
		 * JSONObject jsonMeeting = null; List<Map<String, Object>> mmeeting =
		 * MeetingMngr.getAtionSubjectList(userName, 1, 7); if (mmeeting != null
		 * && mmeeting.size() > 0) { jsonMeeting = new JSONObject();
		 * jsonMeeting.put("type", "MMEETING"); jsonMeeting.put("typech", "会议");
		 * JSONArray jsonArray = new JSONArray(); for (Map<String, Object> map :
		 * mmeeting) { JSONObject jsonObj = new JSONObject();
		 * jsonObj.put("sortcode", map.get("sortcode")); jsonObj.put("sortname",
		 * map.get("name")); jsonObj.put("updcount", 0); JSONObject jsonData =
		 * ODataHelper.GetObjDataLists("MMEETING",
		 * "CLASS,NO,SPONSORUNIT,CONFERENCEENGLISHNAME,CONFERENCENAME",
		 * "class EQ '" + map.get("name") + "'", "", "", 0, 2);
		 * jsonObj.put("Rows", jsonData.get("Rows")); jsonArray.add(jsonObj); }
		 * jsonMeeting.put("sort", jsonArray.toString()); }
		 */
		// 工程
		/*
		 * JSONObject jsonProject= null; List<Map<String, Object>> mproject =
		 * ProjectMngr.getAtionSubjectList(userName, 1, 7); if (mproject != null
		 * && mproject.size() > 0) { jsonProject = new JSONObject();
		 * jsonProject.put("type", "MPROJECTS"); jsonProject.put("typech",
		 * "项目"); JSONArray jsonArray = new JSONArray(); for (Map<String,
		 * Object> map : mproject) { JSONObject jsonObj = new JSONObject();
		 * jsonObj.put("sortcode", map.get("sortcode")); jsonObj.put("sortname",
		 * map.get("name")); jsonObj.put("updcount", 0); JSONObject jsonData =
		 * ODataHelper.GetObjDataLists("MPROJECTS",
		 * "Title,Nature,Status,Contributor,EndDate,Date", "class EQ '" +
		 * map.get("name") + "'", "", "", 0, 2); jsonObj.put("Rows",
		 * jsonData.get("Rows")); jsonArray.add(jsonObj); }
		 * jsonProject.put("sort", jsonArray.toString()); }
		 */
		// 文献
		JSONObject jsonZjcls = null;
		List<Map<String, Object>> zjcls = ZjclsMngr.getAtionSubjectList(userName);
		if (zjcls != null && zjcls.size() > 0) {
			jsonZjcls = new JSONObject();
			jsonZjcls.put("type", "Literature{CJFD,CDFD,CMFD,CCND,CPFD}");
			jsonZjcls.put("typech", "文献");
			JSONArray jsonArray = new JSONArray();
			for (Map<String, Object> map : zjcls) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("sortcode", map.get("sortcode"));
				jsonObj.put("sortname", map.get("name"));
				jsonObj.put("updcount", 0);
				JSONObject jsonData = ODataHelper.GetObjDataLists("Literature{CJFD,CDFD,CMFD,CCND,CPFD}",
						"Title,Nature,Status,Contributor,EndDate,Date", "class EQ '" + map.get("name") + "'", "", "", 0,
						2);
				jsonObj.put("Rows", jsonData.get("Rows"));
				jsonArray.add(jsonObj);
			}
			jsonZjcls.put("sort", jsonArray.toString());
		}

		return "{\"result\":true,\"data\":" + result.toString() + "}";
	}

}
