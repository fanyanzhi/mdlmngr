package Request;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Holder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import BLL.AppInfoMngr;
import BLL.BehaviourMngr;
import BLL.CnkiMngr;
import BLL.DownloadMngr;
import BLL.Logger;
import BLL.SignMngr;
import BLL.SysConfigMngr;
import BLL.UserFeeMngr;
import BLL.UserInfoMngr;
import BLL.UserOrgMngr;
import Model.DownloadInfoBean;
import Model.MobileRightStatus;
import Util.Common;
import Util.LoggerFile;
import net.cnki.sso.UserStruct;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class XmlReadServlet
 */
@WebServlet("/XmlRead/*")
public class XmlReadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String cookieKey = "@a3k9#-;jdiu$542H-03H~kdd32akj8j";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XmlReadServlet() {
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

	// 不对odata1的文献进行xml下载
	@SuppressWarnings({ "unused", "unchecked" })
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
		JSONArray jsonArray = null;

		JSONObject jo = JSONObject.fromObject(strReq);
		mapInfo = (Map<String, Object>) jo;
		String strToken = (String) mapInfo.get("usertoken");
		strUserName = UserInfoMngr.UserLogin(strToken);
		if (strUserName.startsWith("@-")) {
			response.setStatus(500);
			response.setHeader("error", "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
					.concat(strUserName.substring(1)).concat(",\"errcode\":").concat(strUserName.substring(1))
					.concat("}"));
			sendResponseData(response,
					"{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
							.concat(strUserName.substring(1)).concat(",\"errcode\":").concat(strUserName.substring(1))
							.concat("}"),
					start);
			return;
		}
		if (!"xml".equals(String.valueOf(mapInfo.get("filetype")))) {
			response.setStatus(500);
			response.setHeader("error", "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"));
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"), start);
			return;
		}
		if (mapInfo != null && mapInfo.containsKey("typeid")) {
			if ("CAPJ".equalsIgnoreCase((String) mapInfo.get("typeid"))) {
				mapInfo.put("typeid", "cjfd");
			}
			String[] arrType = CnkiMngr.getTypes((String) mapInfo.get("typeid"));
			if (arrType == null) {
				response.setStatus(500);
				response.setHeader("error", "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"));
				sendResponseData(response, "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"), start);
				return;
			}
			mapInfo.put("typeid", arrType[0]);
			mapInfo.put("newtypeid", arrType[1]);
			mapInfo.put("odatatype", arrType[2]);
		}

		JSONArray newJsonArray = null;
		JSONObject recordObj = null;
		if (jsonArray != null && jsonArray.size() > 0) {
			newJsonArray = new JSONArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				recordObj = JSONObject.fromObject(jsonArray.get(i));
				if (recordObj.containsKey("typeid")) {
					String[] arrType = CnkiMngr.getTypes((String) recordObj.get("typeid"));
					if (arrType == null) {
						response.setStatus(500);
						response.setHeader("error", "{\"result\":false,\"message\":\""
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
								.concat("\",\"errorcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
								.concat(",\"errcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
								.concat("}"));
						sendResponseData(response,
								"{\"result\":false,\"message\":\""
										.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
										.concat("\",\"errorcode\":")
										.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
										.concat(",\"errcode\":")
										.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
										.concat("}"),
								start);
						return;
					}
					recordObj.put("typeid", arrType[0]);
					recordObj.put("newtypeid", arrType[1]);
				}
				newJsonArray.add(recordObj);
			}
			if (newJsonArray.size() > 0) {
				jsonArray = JSONArray.fromObject(newJsonArray.toString());

			}
		}

		String strRet = null;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "chkauthority":
			strRet = chkUserAuthority(mapInfo, strUserName, request);
			break;
		case "feefile":
			strRet = feeForFile(mapInfo, strUserName, request);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}
		JSONObject ret = JSONObject.fromObject(strRet);
//		if (ret.getBoolean("result") && ret.containsKey("downurl")) {
//			sendResponseData(response, ret.getString("downurl"));
//		} else {
//			response.setStatus(500);
//			response.setHeader("error", strRet);
//			sendResponseData(response, strRet, start);
//		}
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

	//css放哪里
	private void sendResponseData(HttpServletResponse response, String downUrl) throws IOException {
		//downUrl = "http://192.168.51.40:8080/Test/XmlReadServlet?id=DLXB2014120071";
		HttpClient hc = null;
		HttpGet httpGet = null;
		response.setHeader("xslpath", "http://m.cnki.net/article.xsl?v=1");
		OutputStream os = response.getOutputStream();
		
		try {
			hc = new DefaultHttpClient();
			httpGet = new HttpGet(downUrl);
			HttpResponse hr = hc.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			HttpEntity he = hr.getEntity();
			String str = EntityUtils.toString(he, "UTF-8");
			//int start = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>".length();
			//String html = str.substring(start, str.length());
			//String fileHtml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><?xml-stylesheet href=\"http://192.168.51.40:8080/Test/article.xsl\" type=\"text/xsl\" ?>"
					//+ html;
			if (code == 200) {
				os.write(str.getBytes());
				os.flush();
				os.close();
			}
			httpGet.abort();
		} catch (IOException e) {
			LoggerFile.appendMethod("d:\\log\\xmlreaderror.txt", downUrl);
			//LoggerFile.appendMethod("/home/xmlreaderror.txt", downUrl);
		} finally {
			httpGet.abort();
			hc.getConnectionManager().shutdown();
		}
	}
	
	/*private void sendResponseData(HttpServletResponse response, String downUrl) throws IOException {
		URL url;
		try {
			url = new URL(downUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setReadTimeout(10000);
			http.connect();
			if (http.getResponseCode() != 200) {
				response.getOutputStream().write(http.getResponseCode());
				response.getOutputStream().write(http.getResponseMessage().getBytes());
				response.getOutputStream().close();
				return;
			}
			InputStream input = http.getInputStream();
			BufferedInputStream bin = new BufferedInputStream(input);
			OutputStream out = response.getOutputStream();
			int iLength = 102400;
			byte[] inputData = new byte[iLength];
			int iread = 0;

			while ((iread = bin.read(inputData)) > 0) {
				out.write(inputData, 0, iread);
				out.flush();
			}
			bin.close();
			out.close();
		} catch (Exception e) {
			response.getOutputStream().write(e.getMessage().getBytes());
			response.getOutputStream().close();
			return;
		}
	}*/

	private String chkUserAuthority(Map<String, Object> arg, final String UserName, HttpServletRequest request) {
		String appId = String.valueOf(request.getAttribute("app_id"));
		final String strTypeID = ((String) arg.get("typeid")).trim();
		String strNewTypeID = ((String) arg.get("newtypeid")).trim();
		final String strFileID = ((String) arg.get("fileid")).trim();
		String strOrgName = arg.containsKey("orgname") ? arg.get("orgname") == null ? null : (String) arg.get("orgname")
				: "";
		String strOrgPwd = arg.containsKey("orgpwd") ? arg.get("orgpwd") == null ? null : (String) arg.get("orgpwd")
				: "";
		String strIsBind = (String) arg.get("isband");
		String organizationName = "";
		if (strIsBind == null || strIsBind.length() == 0) {
			strIsBind = "0";
		}

		String strIP = "";
		if (arg.containsKey("ip")) {
			strIP = ((String) arg.get("ip")).trim();
		}
		if (Common.IsNullOrEmpty(strIP) || "null".equals(strIP.toLowerCase())) {
			strIP = Common.getClientIP(request);
		}
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		// 活动修改
		boolean bolCheckAuthority = true;
		boolean validUser = false;
		if (Common.GetConfig("expand") != null && "1".equals(Common.GetConfig("expand"))) {
			validUser = UserOrgMngr.isValid(UserName);
			if (validUser) {
				int downCount = DownloadMngr.getDownloadFileCount("", "", "", "", "", Common.GetDateTime("yyyy-MM-dd"),
						Common.ConvertToDateTime(Common.GetDateTime("yyyy-MM-dd"), "yyyy-MM-dd", 24 * 3600 * 1000));
				if (downCount > Integer.parseInt(Common.GetConfig("downlimit"))) {
					return "{\"result\":true,\"download\":false,\"message\":\"今日已达到下载上限\",\"errorcode\":"
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
							.concat("\"errcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
				}
			}
		}
		if (!validUser) {
			bolCheckAuthority = !UserInfoMngr.isExistsCnkiFile(UserName, strTypeID, strFileID); // 判断该用户是否已经下载过该资源,需要走下面的内容；
		}
		String todaydown = Common.GetConfig("todaydown");
		int tdowncount = Integer.parseInt(todaydown);
		if (DownloadMngr.getTodayDownloadCount(UserName) > tdowncount) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.Max_DownLoad.code))
					.concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.Max_DownLoad.code))
					.concat(",\"errcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.Max_DownLoad.code))
					.concat("}");
		}
		// 用户没有下载过需要验证权限
		if (bolCheckAuthority) {
			String[] arrOrg = new String[] { "", "", "", "" };
			int roamDay = AppInfoMngr.getAppRoam(appId);
			int validOrg = 0; // 0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，
			if (roamDay > 0) {
				validOrg = UserOrgMngr.getUserOrgInfo(UserName, roamDay, arrOrg);
			}
			if (roamDay > 0 && validOrg == 0) {
				strIsBind = "0";
			}
			if ("1".equals(strIsBind) || "2".equals(strIsBind)) {
				List<Map<String, String>> orgList = null;
				if (validOrg == 1) {
					orgList = initOrgList_New(arrOrg);
					strIP = arrOrg[1];
				} else {
					if (roamDay > 0) { // 打开了开关，但是没有点立即关联。所有通过快报app的用户，如果想使用机构权限，必须关联，也就是所有移动app的都要先关联
						if (validOrg == -1) { // 0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，目前只在快报中才会出现1，或者-1，也就是app是允许漫游的
							return "{\"result\":true,\"passed\":false,\"message\":\"机构关联错误\",\"msgcode\":"
									+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + ",\"errcode\":"
									+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + "}";
						}
					} else {
						orgList = initOrgList(strIP, strOrgName, strOrgPwd);
					}
				}
				String[] result = new String[orgList.size()];
				boolean bSuccess = false;
				String returnRsult = "";
				for (int i = 0; i < orgList.size(); i++) {
					CnkiMngr cnkiMngr = new CnkiMngr();
					boolean bLogined = false;
					String longitude = "";
					String latitude = "";
					String orgname = "";
					String orgpwd = "";
					int[] iResult = new int[2];
					Holder<Integer> errorCode = new Holder<Integer>();
					if (orgList.get(i).get("type").equals("ip")) {
						bLogined = cnkiMngr.cnkiUserLogin(orgList.get(i).get("ip"), errorCode);
					} else if (orgList.get(i).get("type").equals("orgname")) {
						orgname = orgList.get(i).get("orgname");
						orgpwd = orgList.get(i).get("orgpwd");
						bLogined = cnkiMngr.cnkiUserLogin(orgname, orgpwd, orgList.get(i).get("ip"), iResult);
						if (bLogined) {
							if (iResult[1] == 1)// 1为个人账号，不允许绑定下载
								bLogined = false;
						}
					} else if (orgList.get(i).get("type").equals("lbs")) {
						Holder<String> userName = new Holder<String>();
						Holder<String> unitName = new Holder<String>();
						longitude = orgList.get(i).get("longitude");
						latitude = orgList.get(i).get("latitude");
						bLogined = cnkiMngr.cnkiLbsUserLogin(orgList.get(i).get("ip"), Double.parseDouble(longitude),
								Double.parseDouble(latitude), userName, unitName, errorCode);
					}
					if (!bLogined) {
						returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"下载时机构账号登录失败!\",\"msgcode\":-1,\"errcode\":"
								+ SysConfigMngr.ERROR_CODE.ERROR_ORGLOGIN.code + "}";
						result[i] = "login failed";
						continue;
					}

					Holder<Integer> errorCodea = new Holder<Integer>();
					UserStruct us = cnkiMngr.getUserInfo(errorCodea);
					organizationName = us.getBaseInfo().getValue().getUserName().getValue();
					// 判断权限-->判断下载权限
					if (bLogined && MobileRightStatus.getMobileRight()) {
						Holder<Boolean> hmr = new Holder<Boolean>();
						if (!cnkiMngr.haveMobileRight(hmr, errorCode)) {
							returnRsult = "{\"result\":true,\"passed\":false,\"message\":\" have no MobileRight\",\"msgcode\":-1,\"errcode\":"
									+ errorCode.value + "}";
						}
					}
					String[] errResult = new String[] { "-15506" };
					if (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID, strFileID, errResult)) {
						return "{\"result\":false,\"message\":\"".concat(String.valueOf(errResult[0]))
								.concat("\",\"errorcode\":").concat(String.valueOf(errResult[0]))
								.concat("\",\"errcode\":").concat(String.valueOf(errResult[0])).concat("}");
					}
					int score = SignMngr.signScore(UserName);
					String[] arrAuthRet = new String[6];
					int iAuthRet = cnkiMngr.getUserAuthority(strNewTypeID, strFileID, arrAuthRet);
					if (iAuthRet == -1) {
						if ("-5".equals(arrAuthRet[1])) { // 余额不够
							returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"余额不足.\""
									+ ",\"msgcode\":-7,\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_BALANCE.code
									+ ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
									+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5]
									+ ",\"allscore\":" + score + ",\"curscore\":"
									+ (int) (Float.parseFloat(arrAuthRet[4]) * 15) + "}";
						} else { // 没权限或获取不到余额
							returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0]
									+ "\",\"msgcode\":-5,\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_ACCESSRIGHT.code
									+ "}";
						}
					} else if (iAuthRet == 0) {
						returnRsult = "{\"result\":true,\"passed\":true,\"message\":\"机构账户余额.\"" + ",\"msgcode\":"
								+ arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":"
								+ arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5]
								+ ",\"allscore\":" + score + ",\"curscore\":"
								+ (int) (Float.parseFloat(arrAuthRet[4]) * 15) + ",\"feeuser\":\"org\"}";
					} else {
						bSuccess = true;
						break;
					}
				}
				if (!bSuccess) {
					return returnRsult;
				}
			} else {
				CnkiMngr cnkiMngr = new CnkiMngr();
				Holder<Integer> errorCode = new Holder<Integer>();
				if (!cnkiMngr.cnkiUserLogin(UserName, strIP, errorCode)) {
					Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 7,
							UserName + "chkuserauthority login failed", 0);
					return "{\"result\":true,\"passed\":false,\"message\":\"login failure\",\"msgcode\":-1,\"errcode\":"
							+ errorCode.value + "}";
				}
				UserStruct us = cnkiMngr.getUserInfo(errorCode);
				organizationName = us.getBaseInfo().getValue().getParentName().getValue();
				String[] errResult = new String[] { "-15506" };

				if (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID, strFileID, errResult)) {
					return "{\"result\":false,\"message\":\""
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
							.concat("\",\"errorcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
							.concat(",\"errcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code)).concat("}");
				}
				int score = SignMngr.signScore(UserName);
				String[] arrAuthRet = new String[6];
				int iAuthRet = cnkiMngr.getUserAuthority(strNewTypeID, strFileID, arrAuthRet);
				if (iAuthRet == -1) {
					if ("-5".equals(arrAuthRet[1])) {
						return "{\"result\":true,\"passed\":false,\"message\":\" 余额不足. \"" + ",\"msgcode\":"
								+ arrAuthRet[1] + ",\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_BALANCE.code
								+ ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
								+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + ",\"allscore\":"
								+ score + ",\"curscore\":" + (int) (Float.parseFloat(arrAuthRet[4]) * 15) + "}";
					} else {
						return "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
								+ arrAuthRet[1] + ",\"errcode\":" + arrAuthRet[1] + "}";
					}
				} else if (iAuthRet == 0) {
					return "{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
							+ arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
							+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + ",\"allscore\":"
							+ score + ",\"curscore\":" + (int) (Float.parseFloat(arrAuthRet[4]) * 15) + "}";
				}
			}
		}
		//String strDownUrl = "http://10.1.201.195:8080/HfsClient/ReadXml.ashx?id="+strFileID;
		long timestamp = System.currentTimeMillis();
		String sign = Common.EncryptData(UserName+"|"+timestamp, cookieKey);
		String strDownUrl = "";
		try {
			strDownUrl = "http://m.cnki.net/mcnki/GeHtmlHandler?instanceID="+URLEncoder.encode(strFileID, "utf-8")+"&sign="+URLEncoder.encode(sign, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bolCheckAuthority) {
			UserInfoMngr.saveUserCnkiFile(UserName, strTypeID, strFileID);
		}
		addDownloadRec(arg, appId, UserName, request, Integer.parseInt(strIsBind), organizationName);
		return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
	}

	private String feeForFile(Map<String, Object> arg, String UserName, HttpServletRequest request) {
		String appId = String.valueOf(request.getAttribute("app_id"));
		String strTypeID = ((String) arg.get("typeid")).trim();
		String strNewTypeID = ((String) arg.get("newtypeid")).trim();
		String odatatype = ((String) arg.get("odatatype")).trim();
		String strFileID = ((String) arg.get("fileid")).trim();
		String strOrgName = arg.get("orgname") == null ? null : (String) arg.get("orgname");
		String strOrgPwd = arg.get("orgpwd") == null ? null : (String) arg.get("orgpwd");
		String strIsBind = (String) arg.get("isband");
		String organizationName = "";
		if (strIsBind == null || strIsBind.length() == 0) {
			strIsBind = "0";
		}
		String strIP = "";
		if (arg.containsKey("ip")) {
			strIP = ((String) arg.get("ip")).trim();
		}
		if (Common.IsNullOrEmpty(strIP) || "null".equals(strIP.toLowerCase())) {
			strIP = Common.getClientIP(request);
		}
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String[] arrOrg = new String[] { "", "", "", "" };
		int roamDay = AppInfoMngr.getAppRoam(appId);
		int validOrg = 0;
		if (roamDay > 0) {
			validOrg = UserOrgMngr.getUserOrgInfo(UserName, roamDay, arrOrg);
		}
		if (roamDay > 0 && validOrg == 0) {
			strIsBind = "0";
		}
		CnkiMngr cnkiMngr = new CnkiMngr();
		boolean bLogined = false;
		int[] iResult = new int[2];
		Holder<Integer> errorCode = new Holder<Integer>();
		if ("1".equals(strIsBind) || "2".equals(strIsBind)) {
			if (validOrg == 1) {
				if ("1".equals(arrOrg[0])) {
					Holder<String> userName = new Holder<String>();
					Holder<String> unitName = new Holder<String>();
					bLogined = cnkiMngr.cnkiLbsUserLogin(arrOrg[1], Double.parseDouble(arrOrg[2]),
							Double.parseDouble(arrOrg[3]), userName, unitName, errorCode);
				} else if ("2".equals(arrOrg[0])) {
					bLogined = cnkiMngr.cnkiUserLogin(arrOrg[2], arrOrg[3], arrOrg[1], iResult);
				} else {
					bLogined = cnkiMngr.cnkiUserLogin(arrOrg[1], errorCode);
				}
			} else {
				if (roamDay > 0) { // 打开了开关，但是没有点立即关联。所有通过快报app的用户，如果想使用机构权限，必须关联，也就是所有移动app的都要先关联
					if (validOrg == -1) { // 0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，目前只在快报中才会出现1，或者-1，也就是app是允许漫游的
						return "{\"result\":true,\"passed\":false,\"message\":\"机构关联错误\",\"msgcode\":"
								+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + ",\"errcode\":"
								+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + "}";
					}
				}
				if ("1".equals(strIsBind)) {
					if (strOrgName == null || strOrgName.length() == 0) {
						bLogined = cnkiMngr.cnkiUserLogin(strIP, errorCode);
					} else {
						bLogined = cnkiMngr.cnkiUserLogin(strOrgName, strOrgPwd, strIP, iResult);
					}
				} else if ("2".equals(strIsBind)) {
					Holder<String> userName = new Holder<String>();
					Holder<String> unitName = new Holder<String>();
					bLogined = cnkiMngr.cnkiLbsUserLogin(strIP, Double.parseDouble(strOrgName),
							Double.parseDouble(strOrgPwd), userName, unitName, errorCode);
					if (!bLogined) {
						Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 7, "lbs login failed", 0);
						return "{\"result\":true,\"passed\":false,\"message\":\"lbs login failure\",\"msgcode\":"
								+ errorCode.value + ",\"errcode\":" + errorCode.value + "}";
					}
				}
			}
			if (bLogined) {
				UserStruct us = cnkiMngr.getUserInfo(errorCode);
				organizationName = us.getBaseInfo().getValue().getUserName().getValue();
			}
		} else {
			bLogined = cnkiMngr.cnkiUserLogin(UserName, strIP, errorCode);
		}
		if (!bLogined) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(errorCode.value))
					.concat("\",\"errorcode\":").concat(String.valueOf(errorCode.value)).concat(",\"errcode\":")
					.concat(String.valueOf(errorCode.value)).concat("}");
		}
		String[] errResult = new String[] { "-1506" };
		if (odatatype.equals("1")) {
			if (!cnkiMngr.setFileInfo(strTypeID, strFileID)) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
						.concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code)).concat("}");
			}
		} else {
			if (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID, strFileID, errResult)) {
				return "{\"result\":false,\"message\":\"".concat(String.valueOf(errResult[0]))
						.concat("\",\"errorcode\":").concat(String.valueOf(errResult[0])).concat(",\"errcode\":")
						.concat(String.valueOf(errResult[0])).concat("}");
			}
		}
		String[] arrAuthRet = new String[2];
		if (!cnkiMngr.getPermision(strNewTypeID, strFileID, arrAuthRet, errorCode)) {
			return "{\"result\":true,\"pay\":0,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":-1,\"errcode\":"
					+ errorCode.value + "}";
		} else {
			UserFeeMngr.setUserFee(UserName, strTypeID, strFileID, Float.parseFloat(arrAuthRet[0]));
		}
		UserInfoMngr.saveUserCnkiFile(UserName, strTypeID, strFileID);
		addDownloadRec(arg, appId, UserName, request, Integer.parseInt(strIsBind), organizationName);
		//String strDownUrl = "http://10.1.201.195:8080/HfsClient/ReadXml.ashx?id="+strFileID;
		long timestamp = System.currentTimeMillis();
		String sign = Common.EncryptData(UserName+"|"+timestamp, cookieKey);
		String strDownUrl = "";
		try {
			strDownUrl = "http://m.cnki.net/mcnki/GeHtmlHandler?instanceID="+URLEncoder.encode(strFileID, "utf-8")+"&sign="+URLEncoder.encode(sign, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "{\"result\":true,\"pay\":1,\"downurl\":\"".concat(strDownUrl).concat("\"}");
	}

	private List<Map<String, String>> initOrgList_New(String[] orgInfo) {
		List<Map<String, String>> orgList = new ArrayList<Map<String, String>>();
		Map<String, String> orgIpMap = new HashMap<String, String>();
		if ("2".equalsIgnoreCase(orgInfo[0])) {
			orgIpMap.put("type", "orgname");
			orgIpMap.put("ip", orgInfo[1]);
			orgIpMap.put("orgname", orgInfo[2]);
			orgIpMap.put("orgpwd", orgInfo[3]);
			orgList.add(orgIpMap);
		} else if ("1".equalsIgnoreCase(orgInfo[0])) {
			orgIpMap.put("type", "lbs");
			orgIpMap.put("ip", orgInfo[1]);
			orgIpMap.put("longitude", orgInfo[2]);
			orgIpMap.put("latitude", orgInfo[3]);
			orgList.add(orgIpMap);
		} else {
			orgIpMap.put("type", "ip");
			orgIpMap.put("ip", orgInfo[1]);
			orgList.add(orgIpMap);
		}
		return orgList;
	}

	private List<Map<String, String>> initOrgList(String ip, String orgName, String orgPassword) {
		List<Map<String, String>> orgList = new ArrayList<Map<String, String>>();
		Map<String, String> orgIpMap = new HashMap<String, String>();
		orgIpMap.put("type", "ip");
		orgIpMap.put("ip", ip);
		orgList.add(orgIpMap);
		String[] arrOrgName = orgName.split(";");
		String[] arrOrgPwd = orgPassword.split(";");
		String[] tmpOrgName = arrOrgName.length > 0 ? arrOrgName[0].split(",") : null;
		String[] tmpOrgPwd = arrOrgPwd.length > 0 ? arrOrgPwd[0].split(",") : null;
		String tmpLongitude = "";
		String tmpLatitude = "";
		if (tmpOrgName != null && tmpOrgName.length > 0) {
			for (int i = 0; i < tmpOrgName.length; i++) {
				if (!Common.IsNullOrEmpty(tmpOrgName[i])) {
					Map<String, String> orgOrgMap = new HashMap<String, String>();
					orgOrgMap.put("type", "orgname");
					orgOrgMap.put("orgname", tmpOrgName[i]);
					orgOrgMap.put("orgpwd", tmpOrgPwd[i]);
					orgOrgMap.put("ip", ip);
					orgList.add(orgOrgMap);
				}
			}
		}
		if (arrOrgName.length > 1) {
			tmpLongitude = arrOrgName[1];
			tmpLatitude = arrOrgPwd[1];
			if (!Common.IsNullOrEmpty(tmpLongitude)) {
				Map<String, String> orgLbsMap = new HashMap<String, String>();
				orgLbsMap.put("type", "lbs");
				orgLbsMap.put("longitude", tmpLongitude);
				orgLbsMap.put("latitude", tmpLatitude);
				orgLbsMap.put("ip", ip);
				orgList.add(orgLbsMap);
			}
		}
		return orgList;
	}

	private void addDownloadRec(Map<String, Object> arg, String appID, String UserName, HttpServletRequest request,
			int isOrg, String orgName) {
		DownloadInfoBean downloadInfo = new DownloadInfoBean();
		downloadInfo.setUserName(UserName);
		downloadInfo.setAppID(appID);
		downloadInfo.setTypeID((String) arg.get("typeid"));
		downloadInfo.setFileID((String) arg.get("fileid"));
		downloadInfo.setFileName((String) arg.get("filename"));
		downloadInfo.setFileType((String) arg.get("filetype"));
		downloadInfo.setIsorg(isOrg);
		// if (isOrg == 1) {
		downloadInfo.setOrgName(orgName);
		// }
		DownloadMngr.addTodayDown(downloadInfo);
		DownloadMngr.addDownloadInfo(downloadInfo);
		BehaviourMngr.addDownloadInfo(appID, UserName, downloadInfo.getFileName(), downloadInfo.getFileType(),
				downloadInfo.getTypeID(), downloadInfo.getFileID());
	}

}
