package Request;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Holder;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import BLL.AppInfoMngr;
import BLL.CnkiMngr;
import BLL.DownloadMngr;
import BLL.FreeDownMngr;
import BLL.JournalInfoMngr;
import BLL.Logger;
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
 * Servlet implementation class JournalsInfoServlet
 */
@WebServlet("/journalinfo/*")
public class JournalsInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JournalsInfoServlet() {
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

		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}

		byte[] arrReq = new byte[request.getContentLength()];
		DataInputStream dataInput = new DataInputStream(request.getInputStream());
		dataInput.readFully(arrReq);
		String strReq = new String(arrReq, "utf-8");
		arrReq = null;
		String strUserName = "";
		JSONObject jo = null;
		Map<String, Object> mapInfo = null;

		jo = JSONObject.fromObject(strReq);
		mapInfo = (Map<String, Object>) jo;
		if (!"existsfile".equals(strAction.replace("/", "").toLowerCase())) {
			String strToken = (String) mapInfo.get("usertoken");
			strUserName = UserInfoMngr.UserLogin(strToken);
			if (strUserName.startsWith("@-")) {
				if ("getfile".equals(strAction)) {
					response.setStatus(500);
					response.setHeader("error",
							"{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
									.concat("\",\"errorcode\":").concat(strUserName.substring(1))
									.concat(",\"errcode\":").concat(strUserName.substring(1)).concat("}"));
				}
				sendResponseData(response,
						"{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
								.concat(strUserName.substring(1)).concat(",\"errcode\":")
								.concat(strUserName.substring(1)).concat("}"));
				return;
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
		case "existsfile":
			strRet = isExistsFile(mapInfo, request);
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

	private String chkUserAuthority(Map<String, Object> arg, final String UserName, HttpServletRequest request) {
		System.out.println("shijain:" + Common.GetDateTime());
		String appId = String.valueOf(request.getAttribute("app_id"));
		String dbcode = arg.containsKey("dbcode") ? ((String) arg.get("dbcode")).trim() : "";
		String year = arg.containsKey("year") ? ((String) arg.get("year")).trim() : "";
		String issue = arg.containsKey("issue") ? ((String) arg.get("issue")).trim() : "";
		String titlepy = arg.containsKey("titlepy") ? ((String) arg.get("titlepy")).trim() : "";
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
		if (Common.IsNullOrEmpty(titlepy) || Common.IsNullOrEmpty(year) || Common.IsNullOrEmpty(issue)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		boolean bolCheckAuthority = JournalInfoMngr.existUserJournalInfo(UserName, titlepy + year + issue);
		if (!bolCheckAuthority) {
			String[] arrOrg = new String[] { "", "", "", "" };
			int roamDay = AppInfoMngr.getAppRoam(appId); // 支持漫游的appid，如果不支持漫游的，默认的机构信息是带着机构后面的
			int validOrg = 0;  //0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，
			if (roamDay > 0) {
				validOrg = UserOrgMngr.getUserOrgInfo(UserName, roamDay, arrOrg);
			}
			if(roamDay>0&&validOrg==0){
				strIsBind = "0";
			}
			if ("1".equals(strIsBind) || "2".equals(strIsBind)) {
				List<Map<String, String>> orgList = null;
				if (validOrg==1) {
					orgList = initOrgList_New(arrOrg);
					strIP = arrOrg[1];
				} else {
					if(validOrg == -1){ //0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，目前只在快报中才会出现1，或者-1，也就是app是允许漫游的
						return "{\"result\":true,\"passed\":false,\"message\":\"机构关联错误\",\"msgcode\":"
								+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + ",\"errcode\":"
								+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + "}";
						}
					/*
					 * if (roamDay > 0) { return
					 * "{\"result\":true,\"passed\":false,\"message\":\"机构账号登录失败\",\"msgcode\":-1,\"errcode\":"
					 * + String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.
					 * code) + "}"; } else {
					 */
					orgList = initOrgList(strIP, strOrgName, strOrgPwd);
					// }
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
							if (iResult[1] == 1) { // 1为个人账号，不允许绑定下载
								bLogined = false;
								errorCode.value = SysConfigMngr.ERROR_CODE.ERROR_BINDPERSONAL.code;
							}
						} else {
							errorCode.value = iResult[0];
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
						returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"机构账号登录失败\",\"msgcode\":-1,\"errcode\":"
								+ String.valueOf(errorCode.value) + "}";
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
									+ String.valueOf(errorCode.value) + "}";
						}
					}
					String[] arrAuthRet = new String[6];
					int iAuthRet = cnkiMngr.cnkJournalInfoAuthority(titlepy, dbcode, year, issue, arrAuthRet);
					if (iAuthRet == -1) {
						if ("-5".equals(arrAuthRet[1])) {
							// 余额不够
							returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"机构账户余额不足，机构账户余额:"
									+ arrAuthRet[2] + ",UserTicket:" + arrAuthRet[3] + ",Price:" + arrAuthRet[4]
									+ "\",\"msgcode\":-7,\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_BALANCE.code
									+ ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
									+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + "}";
						} else { // 没权限或获取不到余额
							returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0]
									+ "\",\"msgcode\":-5,\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_ACCESSRIGHT.code
									+ "}";
						}
					} else if (iAuthRet == 0) {
						returnRsult = "{\"result\":true,\"passed\":true,\"message\":\"机构账户余额:" + arrAuthRet[2]
								+ ",UserTicket:" + arrAuthRet[3] + ",Price:" + arrAuthRet[4] + "\",\"msgcode\":"
								+ arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":"
								+ arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5]
								+ ",\"feeuser\":\"org\"}";
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
					return "{\"result\":true,\"passed\":false,\"message\":\" login failure\",\"msgcode\":-1,\"errcode\":"
							+ errorCode.value + "}";
				}
				Holder<Integer> errorCodea = new Holder<Integer>();
				UserStruct us = cnkiMngr.getUserInfo(errorCodea);
				organizationName = us.getBaseInfo().getValue().getParentName().getValue();
				String[] arrAuthRet = new String[6];
				int iAuthRet = cnkiMngr.cnkJournalInfoAuthority(titlepy, dbcode, year, issue, arrAuthRet);
				if (iAuthRet == -1) {
					if ("-5".equals(arrAuthRet[1])) {
						return "{\"result\":true,\"passed\":false,\"message\":\" 余额不足. UserBalance:" + arrAuthRet[2]
								+ ",UserTicket:" + arrAuthRet[3] + ",Price:" + arrAuthRet[4] + "\",\"msgcode\":"
								+ arrAuthRet[1] + ",\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_BALANCE.code
								+ ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
								+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + "}";
					} else {
						return "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
								+ arrAuthRet[1] + ",\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_ACCESSRIGHT.code
								+ "}";
					}
				} else if (iAuthRet == 0) {
					return "{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
							+ arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
							+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + "}";
				}
			}
		}
		String strDownUrl = getDownLoadUrl(dbcode, titlepy + year + issue, "");
		if (!bolCheckAuthority) {
			JournalInfoMngr.saveUserJournalInfo(UserName, dbcode, titlepy + year + issue);
		}
		System.out.println(strDownUrl);
		addDownloadInfo(UserName, appId, "journalinfo", titlepy + year + issue, "pdf", Integer.parseInt(strIsBind), strOrgName);
		return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
	}

	private void addDownloadInfo(String userName, String appId, String typeId, String fileName, String fileType,
			int isOrg, String orgName) {
		DownloadInfoBean downloadInfo = new DownloadInfoBean();
		downloadInfo.setUserName(userName);
		downloadInfo.setAppID(appId);
		downloadInfo.setTypeID(typeId);
		downloadInfo.setFileID(fileName);
		downloadInfo.setFileName(fileName);
		downloadInfo.setFileType(fileType);
		downloadInfo.setIsorg(isOrg);
		downloadInfo.setOrgName(orgName);
		DownloadMngr.addDownloadInfo(downloadInfo);
	}

	private String feeForFile(Map<String, Object> arg, String UserName, HttpServletRequest request) {
		String appId = String.valueOf(request.getAttribute("app_id"));
		String dbcode = arg.containsKey("dbcode") ? ((String) arg.get("dbcode")).trim() : "";
		String year = arg.containsKey("year") ? ((String) arg.get("year")).trim() : "";
		String issue = arg.containsKey("issue") ? ((String) arg.get("issue")).trim() : "";
		String titlepy = arg.containsKey("titlepy") ? ((String) arg.get("titlepy")).trim() : "";
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
		if (Common.IsNullOrEmpty(titlepy) || Common.IsNullOrEmpty(year) || Common.IsNullOrEmpty(issue)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String[] arrOrg = new String[] { "", "", "", "" };
		int roamDay = AppInfoMngr.getAppRoam(appId);
		int validOrg = 0;  //0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，
		if (roamDay > 0) {
			validOrg = UserOrgMngr.getUserOrgInfo(UserName, roamDay, arrOrg);
		}
		if(roamDay>0&&validOrg==0){
			strIsBind = "0";
		}
		CnkiMngr cnkiMngr = new CnkiMngr();
		boolean bLogined = false;
		int[] iResult = new int[2];
		Holder<Integer> errorCode = new Holder<Integer>();
		if ("1".equals(strIsBind) || "2".equals(strIsBind)) {
			if (validOrg==1) {
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
					if(validOrg == -1){ //0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，目前只在快报中才会出现1，或者-1，也就是app是允许漫游的
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
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_USERNAMEORPASSWORD.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_USERNAMEORPASSWORD.code))
					.concat(",\"errcode\":").concat(String.valueOf(errorCode.value)).concat("}");
		}
		String[] arrAuthRet = new String[2];
		if (!cnkiMngr.getJournalInfoPermision(dbcode, titlepy, year, issue, arrAuthRet, errorCode)) {
			return "{\"result\":true,\"pay\":0,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":-1,\"errcode\":"
					+ errorCode.value + "}";
		} else {
			UserFeeMngr.setUserFee(UserName, dbcode, titlepy + year + issue, Float.parseFloat(arrAuthRet[0]));
		}
		addDownloadInfo(UserName, appId, "journalinfo", titlepy + year + issue, "pdf", Integer.parseInt(strIsBind), strOrgName);
		String strDownUrl = getDownLoadUrl(dbcode, titlepy + year + issue, "");
		JournalInfoMngr.saveUserJournalInfo(UserName, dbcode, titlepy + year + issue);
		return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
	}

	/*public static void main(String[] args) {
		String q = "RBZI201505*20*" + Common.GetDateTime() + "**";
		String fileUrl = "";
		try {
			fileUrl = "http://192.168.107.16/kdoc/view.aspx".concat("?dbcode=").concat("cfjd").concat("&cer=false&q="
					+ URLEncoder.encode(Encrypt(q, "lczl2014", true), "UTF-8") + "&uid=&act=local&t=pdf&zk=1");
		} catch (Exception e) {

		}
		System.out.println(fileUrl);
		String str = "";
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);// 连接时间
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000);// 数据传输时间
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(fileUrl);
			HttpResponse hr = client.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.abort();
			client.getConnectionManager().shutdown();
		}
		System.out.println(str);
		if (str.length() > 0 && str.indexOf("{") > -1) {
			JSONObject result = JSONObject.fromObject(str.toLowerCase().indexOf("null") > -1
					? str.toLowerCase().replaceAll("null", "true") : str.toLowerCase());

		} else {
		}
	}*/

	public static String isExistsFile(Map<String, Object> arg, HttpServletRequest request)
			throws UnsupportedEncodingException {
		// String appId = String.valueOf(request.getAttribute("app_id"));
		return "{\"result\":true,\"exists\":" + true + "}";
/*		String dbcode = arg.containsKey("dbcode") ? ((String) arg.get("dbcode")).trim() : "";
		String year = arg.containsKey("year") ? ((String) arg.get("year")).trim() : "";
		String issue = arg.containsKey("issue") ? ((String) arg.get("issue")).trim() : "";
		String titlepy = arg.containsKey("titlepy") ? ((String) arg.get("titlepy")).trim() : "";
		String journalUrl = Common.GetConfig("journalurl");
		String journalPwd = Common.GetConfig("journalpwd");
		String q = titlepy + year + issue + "*20*" + Common.GetDateTime() + "**";
		String fileUrl = journalUrl.concat("?dbcode=").concat(dbcode).concat("&cer=false&q="
				+ URLEncoder.encode(Encrypt(q, journalPwd, true), "utf-8") + "&uid=&act=exlocal&t=pdf&zk=1");
		writeLog("journalinfo",fileUrl);
		String str = "";
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);// 连接时间
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000);// 数据传输时间
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(fileUrl);
			HttpResponse hr = client.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he, "utf-8");
			}
		} catch (Exception e) {
			System.out.println("zhengkan:"+e.getMessage());
			e.printStackTrace();
		} finally {
			httpGet.abort();
			client.getConnectionManager().shutdown();
		}
		if (str.length() > 0 && str.indexOf("{") > -1) {
			JSONObject result = JSONObject.fromObject(str.toLowerCase().indexOf("null") > -1
					? str.toLowerCase().replaceAll("null", "true") : str.toLowerCase());

			return "{\"result\":true,\"exists\":" + result.get("istrue").toString() + "}";
		} else {
			return "{\"result\":true,\"exists\":" + false + "}";
		}*/
	}
	
	public static void writeLog(String folder,String data) {
		File file = new File("d:\\"+folder+".txt");
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

	private static String getDownLoadUrl(String dbCode, String fileName, String uid) {
		String journalUrl = Common.GetConfig("journalurl");// http://192.168.107.16/kdoc/view.aspx
		String journalPwd = Common.GetConfig("journalpwd");
		String q = fileName + "*20*" + Common.GetDateTime() + "*1-5*";
		String fileUrl = journalUrl.concat("?dbcode=").concat(dbCode)
				.concat("&cer=false&q=" + Encrypt(q, journalPwd, true) + "&uid=&act=local&t=pdf&zk=1");
		return fileUrl;
	}
	
	public static void main(String[] args) {
		String tt=getDownLoadUrl("CFJD","RMJY2017Z3","");
		System.out.println(tt);
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

	public static String Encrypt(String input, String password, boolean isBase64OrHex) {
		byte[] Keys = { 0x16, 0x34, 0x56, 0x58, (byte) 0x88, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
		IvParameterSpec zeroIv;
		SecretKeySpec key;
		byte[] encryptedData = null;
		try {
			zeroIv = new IvParameterSpec(Keys);
			key = new SecretKeySpec(password.getBytes("utf-8"), "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			encryptedData = cipher.doFinal(input.getBytes("utf-8"));
		} catch (Exception e) {
			return e.getMessage();
		}
		if (isBase64OrHex) {
			// return
			// EncryptEncoding(Base64.getEncoder().encodeToString(encryptedData));
			// BASE64Encoder base64 = new BASE64Encoder();
			return EncryptEncoding(new String(Base64.encodeBase64(encryptedData)));
		}
		return new String(encryptedData);
	}

	public static String EncryptEncoding(String OldStr) {
		return OldStr.replace("+", "%mmd2B").replace("\"", "%mmd22").replace("'", "%mmd27").replace("/", "%mmd2F");
	}
}
