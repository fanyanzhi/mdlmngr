package Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import BLL.SysConfigMngr;
import Util.Common;

/**
 * Servlet implementation class ConfServlet
 */
@WebServlet("/conf/*")
public class ConfServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(Request.ConfServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConfServlet() {
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
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
		Map<String, Object> mapInfo = null;
		if (request.getContentLength() > 0) {
			byte[] arrReq = new byte[request.getContentLength()];
			request.getInputStream().read(arrReq);
			String strReq = new String(arrReq, "utf-8");
			arrReq = null;

			JSONObject jo = JSONObject.fromObject(strReq);
			mapInfo = (Map<String, Object>) jo;
		}
		// String strToken = (String) mapInfo.get("usertoken");
		String strRet;
		/*
		 * String strUserName = UserInfoMngr.UserLogin(strToken); if
		 * (strUserName.startsWith("@-")) { sendResponseData(response,
		 * "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
		 * .concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat(
		 * "}")); return; }
		 */

		if ("get".equalsIgnoreCase(strAction.replace("/", ""))) {
			strRet = getInfo(mapInfo);
		} else if ("odataconfig".equalsIgnoreCase(strAction.replace("/", ""))) {
			strRet = getODataConfig(response, mapInfo);
		} else if ("errcode".equalsIgnoreCase(strAction.replace("/", ""))) {
			strRet = getErrorCode(response, mapInfo);
		} else if ("systime".equalsIgnoreCase(strAction.replace("/", ""))) {
			strRet = getSystime();
		} else {
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
		}

		sendResponseData(response, strRet, start);
	}

	private void sendResponseData(HttpServletResponse response, String Data, long start) throws IOException {
		if (!Common.IsNullOrEmpty(Data)) {
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
	}

	private String getInfo(Map<String, Object> arg) {
		String strKey = (String) arg.get("name");
		if (strKey == null) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		Map<String, Object> mapInfo = SysConfigMngr.getConfigValueAndTime(strKey);
		if (mapInfo == null) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_NO_DATA.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_NO_DATA.code)).concat("}");
		}
		String strValue = (String) mapInfo.get("value");
		String strTime = mapInfo.get("time").toString();
		strTime = Common.ConvertToDateTime(strTime, "yyyy-MM-dd HH:mm:ss");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("result", true);
		jsonObj.put("value", strValue);
		jsonObj.put("time", strTime);
		return jsonObj.toString();
	}

	private String getODataConfig(HttpServletResponse response, Map<String, Object> arg) {
		OutputStream out = null;
		String odataconfig = Common.GetConfig("ODataConfig");
		File file = new File(odataconfig);
		try {
			out = response.getOutputStream();
			FileInputStream fi = new FileInputStream(file);
			byte[] readByte = new byte[1024];
			int iRead = 0;
			while ((iRead = fi.read(readByte)) != -1) {
				out.write(readByte, 0, iRead);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String getSystime() {
		return "{\"result\":true,\"message\":\"" + System.currentTimeMillis() + "\"}";
	}
	private String getErrorCode(HttpServletResponse response, Map<String, Object> arg) {
		OutputStream out = null;
		String odataconfig = Common.GetConfig("ErrorCode");
		File file = new File(odataconfig);
		try {
			out = response.getOutputStream();
			FileInputStream fi = new FileInputStream(file);
			byte[] readByte = new byte[1024];
			int iRead = 0;
			while ((iRead = fi.read(readByte)) != -1) {
				out.write(readByte, 0, iRead);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
