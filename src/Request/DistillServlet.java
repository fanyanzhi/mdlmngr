package Request;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import BLL.ODataHelper;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import Util.LoggerFile;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class DistillServlet
 */
@WebServlet("/distill/*")
public class DistillServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DistillServlet() {
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
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		long start = System.currentTimeMillis();
		String strAction = request.getPathInfo();
		if (strAction == null) {
			response.setStatus(500);
			response.setHeader("error", "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"), start);
			return;
		}

		byte[] arrReq = new byte[request.getContentLength()];
		DataInputStream dataInput = new DataInputStream(request.getInputStream());
		dataInput.readFully(arrReq);
		String strReq = new String(arrReq, "utf-8");
		arrReq = null;
		String strUserName = "";
		Map<String, Object> mapInfo = null;
		JSONObject jo = JSONObject.fromObject(strReq);
		mapInfo = (Map<String, Object>) jo;
		String strToken = (String) mapInfo.get("usertoken");
		strUserName = UserInfoMngr.UserLogin(strToken);
		if (strUserName.startsWith("@-")) {
			response.setStatus(500);
			response.setHeader("error",
					"{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
							.concat(strUserName.substring(1)).concat(",\"errcode\":").concat(strUserName.substring(1))
							.concat("}"));
			sendResponseData(response,
					"{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
							.concat(strUserName.substring(1)).concat(",\"errcode\":").concat(strUserName.substring(1))
							.concat("}"),
					start);
			return;
		}
		if (mapInfo != null && mapInfo.containsKey("typeid")) {
			if ("CAPJ".equalsIgnoreCase((String) mapInfo.get("typeid"))) {
				mapInfo.put("typeid", "cjfd");
			}
		}

		String strRet = null;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "journal":
			strRet = distillJournal(mapInfo, strUserName, request);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}
		JSONObject ret = JSONObject.fromObject(strRet);
		if (ret.getBoolean("result") && ret.containsKey("downurl")) {
			//LoggerFile.appendMethod("d:\\log\\distilljournal.txt","url:" + ret.getString("downurl"));
			sendResponseData(response, ret.getString("downurl"));
		} else {
			response.setStatus(500);
			response.setHeader("error", strRet);
			sendResponseData(response, strRet, start);
		}
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

	// css放哪里
	private void sendResponseData(HttpServletResponse response, String downUrl) throws IOException {
		long start = System.currentTimeMillis();
		String token = getEcpToken();
		//String pageRange = "1-2" + URLEncoder.encode("+", "utf-8") + "5-8";
		/*downUrl = "http://192.168.106.70/KFDRes/api/download_journal?fileName=RBZI201505&resourceType=pdf&outType=pdf&page="
				+ pageRange;*/
		//System.out.println("url2:" + reqUrl);
		URL url = null;  
		HttpURLConnection http = null;
		BufferedOutputStream  fo = null;
		//BufferedInputStream input = null;
		try{
		url = new URL(downUrl);
		http = (HttpURLConnection) url.openConnection();
		http.setRequestProperty("Authorization", "Bearer ".concat(token));
		http.setRequestMethod("GET");
		http.setDefaultUseCaches(false);
		http.setDoInput(true);
		http.setDoOutput(true);
		http.setConnectTimeout(99999999);  
		http.setReadTimeout(99999999); 

		http.connect();
		if (http.getResponseCode() != 200) {
			response.setStatus(500);
			response.setHeader("error", "{\"result\":false,\"message\":\""
					.concat(String.valueOf(http.getResponseCode())).concat("\",\"errorcode\":")
					.concat(String.valueOf(http.getResponseCode())).concat("}"));
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(http.getResponseCode())).concat("\",\"errorcode\":")
					.concat(String.valueOf(http.getResponseCode())).concat("}"),start);
			return;
		}
		
		BufferedInputStream in = new BufferedInputStream(http.getInputStream());        
        ByteArrayOutputStream out = new ByteArrayOutputStream(10240);        
        byte[] temp = new byte[10240];        
        int size = 0;        
        while ((size = in.read(temp)) != -1) {        
            out.write(temp, 0, size);        
        }        
        in.close();
        byte[] content = out.toByteArray(); 
        if(out!=null)
        	out.close();
        
		fo = new BufferedOutputStream(response.getOutputStream());
		
		fo.write(content); 
		fo.flush();
		content = null;
		}catch(Exception e){
			e.printStackTrace();
			LoggerFile.appendMethod("d:\\log\\distilljournal.txt",e.getMessage());
			//LoggerFile.appendMethod("/home/distilljournal.txt",e.getMessage());
		}finally{
			if( fo != null ){
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            
			if(http!=null)  
				http.disconnect();  
		}
	}

	public static String getEcpToken() {
		String journalip = Common.GetConfig("journalip");
		String ecpUrl = "http://" + journalip + "/KFDAuth/token";
		String client_id = Common.GetConfig("client_id");
		String client_secret = Common.GetConfig("client_secret");
		String strParam = "grant_type=client_credentials&client_id=".concat(client_id).concat("&client_secret=")
				.concat(client_secret);
		JSONObject json = getServerResult(ecpUrl, null, strParam, null, null);
		return json.getString("access_token");
	}

	public static JSONObject getServerResult(String url, String token, String params, Map<String, String> headers,
			String type) {
		String jsonResult = sendPost(url, token, params, null, type);
		return JSONObject.fromObject(jsonResult.replaceAll("\r", "").replaceAll("\n", ""));
	}

	private static String sendPost(String url, String token, String params, Map<String, String> headers, String type) {
		String str = "";
		HttpClient hc = new DefaultHttpClient();
		try {
			HttpPost httpPost = new HttpPost(url);
			if (token != null)
				httpPost.addHeader("authorization", "Bearer ".concat(token));
			if ("json".equals(type)) {
				httpPost.addHeader("Content-type", "application/json; charset=utf-8");
				httpPost.setHeader("Accept", "application/json");
			}
			if (headers != null && headers.size() > 0) {
				Set<String> keys = headers.keySet();
				for (Iterator<String> i = keys.iterator(); i.hasNext();) {
					String key = (String) i.next();
					httpPost.addHeader(key, headers.get(key));
				}
			}
			if (params != null)
				httpPost.setEntity(new StringEntity(params.toString(), "utf-8"));
			HttpResponse hr = null;
			hr = hc.execute(httpPost);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he, "utf-8");
			} else {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he, "utf-8");
			}
			httpPost.abort();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hc.getConnectionManager().shutdown();
		}
		return str;
	}

	/**
	 * 作者已认领且实名过。属于该篇文献作者，即可下载该单行本，目前不做下载次数限制
	 * 
	 * @param arg
	 * @param UserName
	 * @param request
	 * @return
	 */
	private String distillJournal(Map<String, Object> arg, final String UserName, HttpServletRequest request) {
		//String appId = String.valueOf(request.getAttribute("app_id"));
		String SourceCode = ((String) arg.get("sourcecode")).trim();
		String YearIssue = ((String) arg.get("yearissue")).trim();
		String PageRange = ((String) arg.get("pagerange")).trim();
		String FileType = arg.containsKey("filetype")?((String) arg.get("filetype")).trim():"";

		String strIP = "";
		if (arg.containsKey("ip")) {
			strIP = ((String) arg.get("ip")).trim();
		}

		if (Common.IsNullOrEmpty(strIP) || "null".equals(strIP.toLowerCase())) {
			strIP = Common.getClientIP(request);
		}
		if (Common.IsNullOrEmpty(SourceCode) || Common.IsNullOrEmpty(YearIssue) || Common.IsNullOrEmpty(PageRange)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if(Common.IsNullOrEmpty(FileType)){
			FileType = "pdf";
		}
		int[] htpage = getPageNo(SourceCode, YearIssue);
		// 暂时下载次数判断
		// 获取首页
		if (htpage[0] == 0 || htpage[1] == 0) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		// 获取目录
		String homePage = "1-" + (htpage[0] - 1);
		// 获取封底
		String trailerPage = (htpage[1] + 1) + "-";
		String authorpage = "";
		try {
//			authorpage = homePage + URLEncoder.encode("+", "utf-8") + PageRange + URLEncoder.encode("+", "utf-8")
//					+ trailerPage;
			authorpage = URLEncoder.encode(homePage + "+" + PageRange + "+" + trailerPage, "utf-8");
		} catch (Exception e) {

		}
		String strDownUrl = "http://" + Common.GetConfig("journalip") + "/KFDRes/api/download_journal?fileName="
				+ SourceCode + YearIssue + "&resourceType=CJFD&outType="+FileType+"&page=" + authorpage;
		return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
	}

	private static int[] getPageNo(String SourceCode, String YearIssue) {
		int[] htpage = new int[] { 0, 0 };
		JSONObject jsonData = ODataHelper.GetObjDataLists("cjfd", "PageRange",
				"SourceCode='" + SourceCode + "' and YearIssue='" + YearIssue + "'", "", "", 1, 128);
		try {
			if (jsonData.containsKey("Count") && jsonData.getInt("Count") > 0) {
				JSONArray jsonArray = JSONArray.fromObject(jsonData.get("Rows"));
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject recordObj = JSONObject.fromObject(jsonArray.get(i));
					JSONArray subJsonAry = JSONArray.fromObject(recordObj.get("Cells"));
					for (int j = 0; j < subJsonAry.size(); j++) {
						JSONObject subRecObj = JSONObject.fromObject(subJsonAry.get(j));
						String PageRange = subRecObj.get("Value").toString().toLowerCase();
						int[] page = new int[] { 0, 0 };
						String[] pageRange = PageRange.split("-");
						if(!Common.IsNullOrEmpty(pageRange[0])){
							if (pageRange[0].contains("+")) {
								String[] pRange = pageRange[0].split("\\+");
								page[0] = Integer.parseInt(pRange[0]);
							} else {
								page[0] = Integer.parseInt(pageRange[0]);
							}
							if (pageRange[pageRange.length - 1].contains("+")) {
								String[] pRange = pageRange[pageRange.length - 1].split("\\+");
								page[1] = Integer.parseInt(pRange[pRange.length - 1]);
							} else {
								page[1] = Integer.parseInt(pageRange[pageRange.length - 1]);
							}
							if (htpage[0] == 0 && htpage[1] == 0) {
								htpage[0] = page[0];
								htpage[1] = page[1];
							} else {
								if (htpage[0] > page[0])
									htpage[0] = page[0];
								if (htpage[1] < page[1])
									htpage[1] = page[1];
							}
						}
					}
				}
			}
		} catch (Exception e) {
			StringBuilder sbTrace = new StringBuilder();
			StackTraceElement[] arrSTE = e.getStackTrace();
			for (int i = 0; i < 20 && i < arrSTE.length; i++) {
				sbTrace.append(arrSTE[i]).append("\r\n");
			}
			int iCount = sbTrace.length();
			if (iCount > 0) {
				sbTrace.delete(iCount - 2, iCount);
			}
		}
		return htpage;
	}

	public static void main(String[] args) {
		int[] page = new int[] { 0, 0 };
		String PageRange = "";
		String[] pageRange = PageRange.split("-");
		if(!Common.IsNullOrEmpty(pageRange[0])){
		if (pageRange[0].contains("+")) {
			String[] pRange = pageRange[0].split("\\+");
			page[0] = Integer.parseInt(pRange[0]);
		} else {
			page[0] = Integer.parseInt(pageRange[0]);
		}
		if (pageRange[pageRange.length - 1].contains("+")) {
			String[] pRange = pageRange[pageRange.length - 1].split("\\+");
			page[1] = Integer.parseInt(pRange[pRange.length - 1]);
		} else {
			page[1] = Integer.parseInt(pageRange[pageRange.length - 1]);
		}
		}
		System.out.println(page[0] + "&&&&&" + page[1]);
	}

	static String F(String n)// 函数返回一个数对应的Fibonacci数
	{
		String[] pages = new String[] { "", "" };
		if (!n.contains("+"))
			return n;
		else
			pages = n.split("+");
		return F(n);// 递归公式
	}

}
