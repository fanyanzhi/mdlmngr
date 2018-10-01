package Request;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.KuaiBaoMngr;
import BLL.NoticeMngr;
import BLL.OrgHomePageMngr;
import BLL.ScholarMngr;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class OrgServlet
 */
@WebServlet("/org/*")
public class OrgServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrgServlet() {
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
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}")
					.concat("\",\"errcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code))
					.concat("}"));
			;
			return;
		}
		Map<String, String> mapReq = null;
		if (request.getContentLength() > 0) {
			byte[] arrReq = new byte[request.getContentLength()];
			DataInputStream dataInput = new DataInputStream(request.getInputStream());
			dataInput.readFully(arrReq);
			String strReq = new String(arrReq, "utf-8");
			arrReq = null;
			JSONObject jo = JSONObject.fromObject(strReq);
			mapReq = (Map<String, String>) jo;
		}
		String strRet;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "web":
			strRet = getOrgWeb(mapReq);
			break;
		case "notice":
			strRet = getOrgNotice(mapReq);
			break;
		case "noticecount":
			strRet = getOrgNoticeCount(mapReq);
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

	private String getOrgWeb(Map<String, String> arg) throws ServletException, IOException {
		if (arg == null) {
			return "{\"result\":true,\"url\":\"\"}";
		}
		String org = arg.get("org");
		if (Common.IsNullOrEmpty(org)) {
			return "{\"result\":true,\"url\":\"\"}";
		}
		String orgurl = OrgHomePageMngr.getOrgWeb(org);
		/*if (org.equals("syzgxy")) {
			return "{\"result\":true,\"url\":\"http://em.cnki.net/cela/app/index\"}";
		}
		if (org.equals("cnkigfjs2017")) {
			return "{\"result\":true,\"url\":\"http://em.cnki.net/gfjs/mobile.html\"}";
		}
		if (org.equals("syzsyhgy")) {
			return "{\"result\":true,\"url\":\"http://em.cnki.net/zgsyyjy/\"}";
		}*/
		return "{\"result\":true,\"url\":\""+ orgurl +"\"}";
	}
	
	private String getOrgNoticeCount(Map<String, String> arg){
		String org = arg.containsKey("org")?arg.get("org"):"";
		String time = arg.containsKey("time")?arg.get("time"):"";
		if(Common.IsNullOrEmpty(org)){
			"{\"result\":false,\"message\":\""
			.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
			.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		int count = NoticeMngr.getOrgNoticeCount(org, time);
		return "{\"result\":true,\"count\":"+ count +"}";
	}
	
	private String getOrgNotice(Map<String, String> arg) throws ServletException, IOException {
		String org = arg.containsKey("org")?arg.get("org"):"";
		String time = arg.containsKey("time")?arg.get("time"):"";
		if(Common.IsNullOrEmpty(org)){
			"{\"result\":false,\"message\":\""
			.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
			.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		JSONArray jsonArray = new JSONArray();
		List<Map<String, Object>> lstNotice = NoticeMngr.getOrgNotice(org, time);
		if(lstNotice==null|| lstNotice.size()==0){
			return "{\"result\":true,\"count\":0,\"data\":" + jsonArray.toString() + "}";
		}
		Iterator<Map<String, Object>> iterator = lstNotice.iterator();
		Map<String, Object> map = null;
		while(iterator.hasNext()){
			map = iterator.next();
			JSONObject ob = new JSONObject();
			ob.put("name", map.get("name").toString());
			ob.put("content", map.get("content").toString());
			ob.put("time", map.get("edittime").toString());
			ob.put("imageid", map.get("imageid").toString());
			ob.put("type", map.get("type").toString());
			jsonArray.add(ob);
		}
		return "{\"result\":true,\"count\":"+lstNotice.size()+",\"data\":" + jsonArray.toString() + "}";
		
	}
	
	
	

}
