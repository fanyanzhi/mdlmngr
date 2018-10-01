package Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import BLL.UserPushMngr;
import BLL.JdpushMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import Util.HuaWeiPush;
import Util.UmengSender;
import Util.XiaoMiPush;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class UserPushServlet
 */
@WebServlet("/userpush/*")
public class UserPushServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(Request.UsersServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserPushServlet() {
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
		long start = System.currentTimeMillis();
		String appid = String.valueOf(request.getAttribute("app_id"));
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"), start);
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
					.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"), start);
			return;
		}
		switch (strAction.replace("/", "").toLowerCase()) {
		case "get":
			strRet = getUserPush(strUserName);
			break;
		case "set":
			strRet = setUserPush(strUserName, mapReq, appid);
			break;
		case "cancel":
			strRet = cancelUserPush(strUserName, mapReq, appid);
			break;
		case "send":
			strRet = sendMessage(strUserName, mapReq);
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
			if (timestmp > 8000)
				logger.info(json.toString());
			// System.out.println(json.toString());
			response.getOutputStream().write(json.toString().getBytes("utf-8"));
			response.getOutputStream().close();
		} else {
			response.getOutputStream().write(Data.getBytes("utf-8"));
			response.getOutputStream().close();
		}
	}

	private String getUserPush(String userName) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
		List<Map<String, Object>> lstUserPush = UserPushMngr.getUserPush(userName);
		if (lstUserPush == null || lstUserPush.size() == 0) {
			return "{\"result\":true,\"count\":0,\"data\":" + jsonArray.toString() + "}";
		}
		for (Map<String, Object> map : lstUserPush) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("username", userName);
			jsonObj.put("type", map.get("type"));
			jsonObj.put("time", map.get("time"));
			jsonArray.add(jsonObj);
		}
		return "{\"result\":true,\"count\":" + lstUserPush.size() + ",\"data\":" + jsonArray.toString() + "}";
	}

	private String setUserPush(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String type = arg.containsKey("type") ? (String) arg.get("type") : null;
		if (Common.IsNullOrEmpty(type)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (UserPushMngr.setUserPush(userName, Integer.parseInt(type))) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String cancelUserPush(String userName, Map<String, String> arg, String appID)
			throws ServletException, IOException {
		String type = arg.containsKey("type") ? (String) arg.get("type") : null;
		if (Common.IsNullOrEmpty(type)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (UserPushMngr.cancelUserPush(userName, Integer.parseInt(type))) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String sendMessage(String userName, Map<String, String> arg) throws ServletException, IOException {
		String touser = arg.containsKey("touser") ? (String) arg.get("touser") : null;
		String id = arg.containsKey("id") ? (String) arg.get("id") : null;
		String type = arg.containsKey("type") ? (String) arg.get("type") : null;
		String message = arg.containsKey("message") ? (String) arg.get("message") : null;
		String time = arg.containsKey("time") ? (String) arg.get("time") : null;
		if (Common.IsNullOrEmpty(touser)||Common.IsNullOrEmpty(message)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		List<Map<String, Object>> lstUserDevice = UserInfoMngr.getUserDevice(touser);
		if (lstUserDevice == null || lstUserDevice.size() == 0) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.NO_DEVICETOKEN.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.NO_DEVICETOKEN.code)).concat("}");
		}
		for (Map<String, Object> map : lstUserDevice) {
			String manu = map.get("manu").toString();
			String devicetoken = map.get("devicetoken").toString();
			List<String> devices = new ArrayList<String>();
			devices.add(devicetoken);
			Map<String, String> parammap = new HashMap<String, String>();
			parammap.put("fromuser", userName);
			parammap.put("id", id);
			parammap.put("type", type);
			parammap.put("time", time);
			if ("iphone".equals(manu)) {
				JdpushMngr.iosPushKuaiBao(devices, message, "6", parammap);
				continue;
			}
			if ("huawei".equals(manu)) {
				HuaWeiPush.pushKuaiBaoMsg("全球学术快报", message, devices, "6", parammap);
				continue;
			}
			if ("xiaomi".equals(manu)) {
				try {
					XiaoMiPush.sendKuaiBaoMessage("全球学术快报", message, "", devices, "6", parammap);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			if ("other".equals(manu)) {
				try {
					UmengSender.sendKuaiBaoMessage("全球学术快报", "11", message, devices, "6", parammap);
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			}
		}
		return "{\"result\":true}";
	}
}