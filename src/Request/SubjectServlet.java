package Request;

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

import BLL.SubjectRecommendMngr;
import BLL.SysConfigMngr;
import BLL.UserOrgMngr;
import Util.Common;

/**
 * Servlet implementation class SubjectServlet
 */
@WebServlet("/subject/*")
public class SubjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubjectServlet() {
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
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"请求地址错误\",\"errorcode\":"
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}
		String strRet;

		switch (strAction.replace("/", "").toLowerCase()) {
		case "gets": // 老版本保留
			strRet = getSubjectList(request);
			break;
		case "newgets": // 新版本
			strRet = getIndexSubject(request);
			break;
		case "adv": // 广告 用于2.0
			strRet = getAdvSubject(request);
			break;
		case "remd": // 推荐的主题，用于2.0
			strRet = getRemdSubject(request);
			break;
		case "orgsubject":
			strRet = getOrgSubject(request);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\"请求地址错误\",\"errorcode\":"
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}
		if (Common.IsNullOrEmpty(strRet)) {
			return;
		}
		sendResponseData(response, strRet);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	private String getSubjectList(HttpServletRequest request) throws ServletException, IOException {
		String condition = request.getParameter("query");
		String order = request.getParameter("order");
		int iStart = 1;
		int iLength = 20;
		if (request.getParameter("start") != null) {
			try {
				iStart = Integer.valueOf(request.getParameter("start"));
			} catch (Exception e) {
				iStart = 1;
			}
		}
		if (request.getParameter("length") != null) {
			try {
				iLength = Integer.valueOf(request.getParameter("length"));
			} catch (Exception e) {
				iLength = 20;
			}
		}
		if (iLength > 50) {
			iLength = 20;
		}
		int iCount = SubjectRecommendMngr.getSubjectRecommendCount(condition);
		if (iCount == 0) {
			return "{\"result\":true,\"count\":0,\"data\":\"\"}";
		}
		List<Map<String, Object>> lstResult = SubjectRecommendMngr.getSubjectRecommendList(condition, order, iStart,
				iLength);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", map.get("title"));
				jsonObj.put("keyword", map.get("keyword"));
				jsonObj.put("type", map.get("type"));
				jsonObj.put("istop", map.get("istop"));
				jsonObj.put("summary", map.get("summary"));
				jsonObj.put("linktype", map.get("linktype"));
				jsonObj.put("simageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("simageid"))));
				jsonObj.put("bimageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("bimageid"))));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + iCount + ",\"data\":" + jsonArray.toString() + "}";
	}

	private String getIndexSubject(HttpServletRequest request) throws ServletException, IOException {
		List<Map<String, Object>> lstResult = SubjectRecommendMngr.getIndexSubject();
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", map.get("title"));
				jsonObj.put("keyword", map.get("keyword"));
				jsonObj.put("type", map.get("type"));
				jsonObj.put("istop", map.get("istop"));
				jsonObj.put("summary", map.get("summary"));
				jsonObj.put("linktype", map.get("linktype"));
				jsonObj.put("isrecomd", map.get("isrecomd"));
				jsonObj.put("time", map.get("time"));
				jsonObj.put("simageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("simageid"))));
				jsonObj.put("bimageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("bimageid"))));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + jsonArray.size() + ",\"data\":" + jsonArray.toString() + "}";
	}
	
	private String getAdvSubject(HttpServletRequest request) throws ServletException, IOException {
		List<Map<String, Object>> lstResult = SubjectRecommendMngr.getAdvSubject();
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", map.get("title"));
				jsonObj.put("keyword", map.get("keyword"));
				jsonObj.put("type", map.get("type"));
				jsonObj.put("istop", map.get("istop"));
				jsonObj.put("summary", map.get("summary"));
				jsonObj.put("linktype", map.get("linktype"));
				jsonObj.put("isrecomd", map.get("isrecomd"));
				jsonObj.put("time", map.get("time"));
				jsonObj.put("simageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("simageid"))));
				jsonObj.put("bimageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("bimageid"))));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + jsonArray.size() + ",\"data\":" + jsonArray.toString() + "}";
	}
	
	private String getRemdSubject(HttpServletRequest request) throws ServletException, IOException {
		List<Map<String, Object>> lstResult = SubjectRecommendMngr.getRemdSubject();
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", map.get("title"));
				jsonObj.put("keyword", map.get("keyword"));
				jsonObj.put("type", map.get("type"));
				jsonObj.put("istop", map.get("istop"));
				jsonObj.put("summary", map.get("summary"));
				jsonObj.put("linktype", map.get("linktype"));
				jsonObj.put("isrecomd", map.get("isrecomd"));
				jsonObj.put("time", map.get("time"));
				jsonObj.put("simageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("simageid"))));
				jsonObj.put("bimageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("bimageid"))));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + jsonArray.size() + ",\"data\":" + jsonArray.toString() + "}";
	}

	
	/**
	 * 获取机构推荐的主题
	 * @param userName
	 * @return
	 */
	private String getOrgSubject(HttpServletRequest request) {
		String userName = request.getQueryString();
		String orgName = UserOrgMngr.getOrgNameByUserName(userName);// 获取机构账号
		if (Common.IsNullOrEmpty(orgName)) {
			return "{\"result\":true,\"count\":0}";
		}
		List<Map<String, Object>> lstSubject = SubjectRecommendMngr.getSubjectIdss(orgName);
		if (lstSubject==null)
			return "{\"result\":true,\"count\":0}";
		StringBuilder sbSubject = new StringBuilder();
		for(Map<String, Object> map:lstSubject){
			sbSubject.append(map.get("subjectid").toString()).append(",");
		}
		if(sbSubject.length()>0){
			sbSubject.delete(sbSubject.length()-1, sbSubject.length());
		}
		List<Map<String, Object>> lstResult = SubjectRecommendMngr.getOrgSubject(sbSubject.toString());
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", map.get("title"));
				jsonObj.put("keyword", map.get("keyword"));
				jsonObj.put("type", map.get("type"));
				jsonObj.put("istop", map.get("istop"));
				jsonObj.put("summary", map.get("summary"));
				jsonObj.put("linktype", map.get("linktype"));
				jsonObj.put("isrecomd", map.get("isrecomd"));
				jsonObj.put("time", map.get("time"));
				jsonObj.put("simageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("simageid"))));
				jsonObj.put("bimageid", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?")
						.concat(String.valueOf(map.get("bimageid"))));
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"count\":" + jsonArray.size() + ",\"data\":" + jsonArray.toString() + "}";
	}
}
