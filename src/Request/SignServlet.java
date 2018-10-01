package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import BLL.CommentMngr;
import BLL.SignMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class SignServlet
 */
@WebServlet("/sign/*")
public class SignServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SignServlet() {
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

		String appId = String.valueOf(request.getAttribute("app_id"));
		byte[] arrReq = new byte[request.getContentLength()];
		request.getInputStream().read(arrReq);
		String strReq = new String(arrReq, "utf-8");
		JSONObject jo = JSONObject.fromObject(strReq);
		Map<String, String> mapReq = (Map<String, String>) jo;
		String IP = "";
		if (mapReq.containsKey("ip")) {
			IP = mapReq.get("ip");
		}
		if (Common.IsNullOrEmpty(IP)) {
			IP = Common.getClientIP(request);
		}
		mapReq.put("ip", IP);
		mapReq.put("appid", appId);
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}
		String strToken = (String) mapReq.get("usertoken");
		String strUserName = UserInfoMngr.UserLogin(strToken);

		if (strUserName.startsWith("@-")) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
					.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
			return;
		}

		if (UserInfoMngr.unLawfulUser(strUserName)) {
			sendResponseData(response, "{\"result\":false,\"message\":\"非法用户\",\"errorcode\":-110}");
			return;
		}

		String strRet;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "add":
			strRet = userSign(strUserName, mapReq);
			break;
		case "expense":
			strRet = signExpense(strUserName, mapReq);
			break;
		case "obtain":
			strRet = obtainScore(strUserName, mapReq);
			break;
		case "status":
			strRet = signStatus(strUserName);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}

		sendResponseData(response, strRet);
	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	/**
	 * 签到
	 * 
	 * @param userName
	 * @param paramMap
	 * @return
	 */
	private String userSign(String userName, Map<String, String> paramMap) {
		String ip = paramMap.get("ip").toString();
		String version = paramMap.get("version") == null ? "" : paramMap.get("version").toString();
		return SignMngr.userSign(userName, ip, version, 1);
	}

	/**
	 * 获取用户当天签到状态 //签到状态：0为未签到，1为已签到。 当未签到时，返回：状态位为0，连续签到天数，累计积分，本次签到可以得到的积分
	 * 已签到返回值：状态为为1，连续签到天数，累计积分
	 * 
	 * @param userName
	 * @return false true
	 */
	private String signStatus(String userName) {
		JSONObject result = new JSONObject();
		int scount = 0; // 累计签到次数
		int score = 0; // 累计积分
		int tscore = 0; // 本次签到积分
		int status = 0;
		Map<String, Object> map = SignMngr.userStatus(userName);
		if (map == null) {
			tscore = SignMngr.getScore(scount + 1);
			result.put("result", true);
			result.put("status", status);
			result.put("scount", scount);
			result.put("score", score);
			result.put("tscore", tscore);
		} else {
			String ltime = map.get("time").toString();
			DateTimeFormatter format1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			DateTime tmptime = DateTime.parse(ltime, format1);
			LocalDate lastDate = new LocalDate(tmptime);
			LocalDate curDate = new LocalDate();
			int days = Days.daysBetween(lastDate, curDate).getDays();
			if (days == 0) {
				status = 1;
				score = Integer.parseInt(map.get("score").toString());
				scount = Integer.parseInt(map.get("scount").toString());
				result.put("result", true);
				result.put("status", status);
				result.put("score", score);
				result.put("scount", scount);
			} else if (days == 1) {
				score = Integer.parseInt(map.get("score").toString());
				scount = Integer.parseInt(map.get("scount").toString());
				tscore = SignMngr.getScore(scount + 1);
				result.put("result", true);
				result.put("status", status);
				result.put("score", score);
				result.put("scount", scount);
				result.put("tscore", tscore);
			} else if (days > 1) {
				score = Integer.parseInt(map.get("score").toString());
				tscore = SignMngr.getScore(scount + 1);
				result.put("result", true);
				result.put("status", status);
				result.put("score", score);
				result.put("scount", scount);
				result.put("tscore", tscore);
			}
		}
		return result.toString();// 签到状态：0为未签到，1为已签到。
	}

	private String signExpense(String userName, Map<String, String> arg) {
		int iStart = 1;
		int iLength = 20;
		if (arg.containsKey("start") && arg.get("start") != null) {
			iStart = Integer.valueOf(String.valueOf(arg.get("start")));
		}
		if (arg.containsKey("length") && arg.get("length") != null) {
			iLength = Integer.valueOf(String.valueOf(arg.get("length")));
		}
		iStart = iStart < 1 ? 1 : iStart;
		iLength = iLength < 1 ? 50 : iLength;
		List<Map<String, Object>> lstResult = SignMngr.signExpense(userName, iStart, iLength);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("type", map.get("type"));
				jsonObj.put("score", map.get("score"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}
	
	private String obtainScore(String userName, Map<String, String> arg){
		int iStart = 1;
		int iLength = 20;
		if (arg.containsKey("start") && arg.get("start") != null) {
			iStart = Integer.valueOf(String.valueOf(arg.get("start")));
		}
		if (arg.containsKey("length") && arg.get("length") != null) {
			iLength = Integer.valueOf(String.valueOf(arg.get("length")));
		}
		iStart = iStart < 1 ? 1 : iStart;
		iLength = iLength < 1 ? 50 : iLength;
		List<Map<String, Object>> lstResult = SignMngr.obtainScore(userName, iStart, iLength);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("type", map.get("type"));
				jsonObj.put("score", map.get("score"));
				jsonObj.put("time", map.get("time"));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

}
