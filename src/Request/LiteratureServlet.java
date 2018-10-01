package Request;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
import BLL.LiteratureMngr;
import BLL.ScholarMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import DAL.DBHelper;
import Util.Common;

/**
 * Servlet implementation class LiteratureServlet
 */
@WebServlet("/literature/*")
public class LiteratureServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LiteratureServlet() {
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
		// String strAction = request.getPathInfo();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");

		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\"}"));
			return;
		}
		String strRet = null;
		String strUserName = null;
		Map<String, Object> mapInfo = new HashMap<String, Object>();
		long length=request.getContentLength();
		if(length!=0){
			byte[] arrReq = new byte[request.getContentLength()];
			DataInputStream dataInput = new DataInputStream(request.getInputStream());
			dataInput.readFully(arrReq);
			request.getInputStream().close();
			dataInput.close();
			String strReq = new String(arrReq, "utf-8");
			arrReq = null;
			JSONObject jo = JSONObject.fromObject(strReq);
			mapInfo = (Map<String, Object>) jo;
			String strToken = (String) mapInfo.get("usertoken");
			
			if (strToken != null) {
				strUserName = UserInfoMngr.UserLogin(strToken);
				if (strUserName.startsWith("@-")) {
					sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
							.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
					return;
				}
			}
		}
		String appId = String.valueOf(request.getAttribute("app_id"));
		switch (strAction.toLowerCase()) {
		case "/browse/add":
			strRet = LiteratureMngr.addBrowseInfo(appId, strUserName, mapInfo);
			break;
		case "/browse/get":
			strRet = getBrowseInfoList(strUserName);
			break;
		case "/browse/del":
			strRet = delAllBrowse(strUserName, mapInfo);
			break;
		case "/download/check":
			strRet = chkDownload(mapInfo);
			break;
		case "/search/add":
			LiteratureMngr.addSearchInfo(appId, mapInfo);
			break;
		case "/module/add":
			strRet = addModuleInfo(strUserName, mapInfo);
			break;
		case "/hotliterature":
			strRet = hotLiterature(mapInfo);
			break;
			
		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}
		if (strRet != null)
			sendResponseData(response, strRet);
	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	private String getBrowseInfoList(String username) {
		List<Map<String, Object>> browseList = LiteratureMngr.getBrowseInfoList(username);
		if (browseList == null || browseList.size() == 0) {
			return "{\"result\":true,\"count\":0,\"data\":\"[]\"}";
		}
		JSONArray jsonArray = new JSONArray();
		int count = 0;
		if (browseList != null && browseList.size() > 0) {
			for (Map<String, Object> map : browseList) {
				count = count + 1;
				JSONObject jsonObj = new JSONObject();
				for (Entry<String, Object> entry : map.entrySet()) {
					jsonObj.put(entry.getKey(), entry.getValue());
				}
				jsonArray.add(jsonObj);
			}
			return "{\"result\":true,\"count\":" + count + ",\"data\":" + jsonArray.toString() + "}";
		}
		return "{\"result\":true,\"count\":0,\"data\":\"[]\"}";
	}

	private String delAllBrowse(String username, Map<String, Object> mapInfo) {
		String id = mapInfo.get("id").toString();
		if (Util.Common.IsNullOrEmpty(id)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (LiteratureMngr.delAllBrowse(username, id)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	private String chkDownload(Map<String, Object> mapInfo) {
		/*
		 * String odataType = (String) mapInfo.get("typeid"); String fileId =
		 * (String) mapInfo.get("fileid");
		 */

		return "{\"result\":true}";

	}
	
	private String addModuleInfo(String username, Map<String, Object> mapInfo) {
		String type = mapInfo.get("type")==null?"":(String)mapInfo.get("type");
		String baseos = mapInfo.get("baseos")==null?"":(String)mapInfo.get("baseos");
		if(Common.IsNullOrEmpty(type)||Common.IsNullOrEmpty(baseos)){
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if(LiteratureMngr.addModuleInfo(username, type, baseos)){
			return "{\"result\":true}";
		}else{
			return "{\"result\":false}";
		}
		
	}
	
	private String hotLiterature(Map<String, Object> mapInfo){
		String code = mapInfo.containsKey("code")?(String)mapInfo.get("code"):"";
		/*int start = mapInfo.containsKey("start")?Integer.parseInt((String)mapInfo.get("start")):1;
		int length = 2;*/
		JSONArray jsonArray = new JSONArray();
		List<Map<String, Object>> lst = LiteratureMngr.getHotLiterature(code);
		Iterator<Map<String, Object>> iterator = lst.iterator();
		Map<String, Object> map = null;
		while(iterator.hasNext()){
			map = iterator.next();
			JSONObject ob = new JSONObject();
			ob.put("fileid", map.get("fileid").toString());
			ob.put("typeid", map.get("typeid").toString());
			ob.put("filename", map.get("filename").toString());
			ob.put("author", map.get("author").toString());
			ob.put("date", map.get("date").toString());
			ob.put("year", map.get("year").toString());
			ob.put("issue", map.get("issue").toString());
			ob.put("code", map.get("code").toString());
			ob.put("source", map.get("source").toString());
			ob.put("sourcech", map.get("sourcech").toString());
			ob.put("time", map.get("time").toString());
			jsonArray.add(ob);
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}
}
