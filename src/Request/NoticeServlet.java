package Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.NoticeMngr;
import BLL.OrgNoticeMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import BLL.UserOrgMngr;
import Util.Common;

/**
 * Servlet implementation class NoticeServlet
 */
@WebServlet("/notice/*")
public class NoticeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NoticeServlet() {
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
		Map<String, Object> mapInfo = (Map<String, Object>) jo;
		String strToken = (String) mapInfo.get("usertoken");
		String strRet;
		String strUserName = UserInfoMngr.UserLogin(strToken);
		if (strUserName.startsWith("@-")) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
					.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
			return;
		}

		if ("get".equalsIgnoreCase(strAction.replace("/", ""))) {
			strRet = getInfo(strUserName);
		} else {
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
		}

		sendResponseData(response, strRet);
	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	private String getInfo(String UserName) {
		List<Map<String, Object>> lstPublicNotice = NoticeMngr.getPublicNoticeList("ttod");
		String orgName = UserOrgMngr.getOrgNameByUserName(UserName);
		boolean isorguser = true;
		if (Common.IsNullOrEmpty(orgName)) {
			isorguser = false;
		}
		JSONArray jsonArray = new JSONArray();
		if (lstPublicNotice != null) {
			for (Map<String, Object> map : lstPublicNotice) {
				JSONObject jsonObj = new JSONObject();
				for (Entry<String, Object> entry : map.entrySet()) {
					jsonObj.put(entry.getKey(), entry.getValue());
				}
				if (jsonObj.getString("ispublic").equals("1")) {
					jsonArray.add(jsonObj);
				} else {
					if (isorguser) {
						if (OrgNoticeMngr.existOrgNotice(jsonObj.getString("noticeid"), orgName))
							jsonArray.add(jsonObj);
					}
				}
			}
		}
		if (isorguser) {
			List<Map<String, Object>> lstOrgNotice = NoticeMngr.getPublicNoticeList(orgName);
			for (Map<String, Object> map : lstOrgNotice) {
				JSONObject jsonObj = new JSONObject();
				for (Entry<String, Object> entry : map.entrySet()) {
					jsonObj.put(entry.getKey(), entry.getValue());
				}
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}
}
