package Request;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Holder;

import org.apache.log4j.Logger;

import com.cnki.mngr.Constant;
import com.cnki.mngr.EcpMngr;
import com.cnki.mngr.RightMngr;
import com.cnki.mngr.UserMngr;

import net.cnki.sso.UserStruct;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.AppInfoMngr;
import BLL.BehaviourMngr;
import BLL.CnkiMngr;
import BLL.KuaiBaoMngr;
import BLL.MobilePhoneMngr;
import BLL.QrcodeMngr;
import BLL.ScholarMngr;
//import BLL.Logger;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import BLL.UserOrgMngr;
import Model.MobileRightStatus;
import Model.UserInfoBean;
import Util.Common;
import Util.EmailUtil;
import net.cnki.sso.UserStruct;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class UsersServlet
 */
@WebServlet("/users/*")
public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String password = "@9akj8ja3k9#-;jdiu$98JH-03H~kpb5";
	Logger logger = Logger.getLogger(Request.UsersServlet.class);

	// private final String strUserImage = Common.GetConfig("UserImage");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UsersServlet() {
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
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String strRet = "";
		String strAction = request.getPathInfo();
		// if("hdcount".equalsIgnoreCase(strAction.replace("/", ""))
		// strRet = getHuodongUser("");
		// sendResponseData(response, strRet);
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

		String appId = String.valueOf(request.getAttribute("app_id"));
		byte[] arrReq = new byte[request.getContentLength()];
		request.getInputStream().read(arrReq);
		// String strReq = new String(arrReq);
		String strReq = new String(arrReq, "utf-8");
		JSONObject jo = null;
		try{
			jo = JSONObject.fromObject(strReq);
		}catch(Exception e){
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"), start);
			return;
		}
		Map<String, String> mapReq = (Map<String, String>) jo;
		String IP = "";
		Boolean bFlag = true;
		if (mapReq.containsKey("ip")) {
			IP = mapReq.get("ip"); // "59.64.113.205";// IP =
									// "121.17.160.181";//
			bFlag = false;
		}
		if (Common.IsNullOrEmpty(IP)) {
			IP = Common.getClientIP(request);
		}
		/*
		 * LoggerFile.appendMethod("E:\\login", "appId " + IP);
		 */
		// IP = "59.64.113.205";
		// IP="218.27.208.198";
		// IP = "192.168.26.81";
		mapReq.put("ip", IP);
		mapReq.put("appid", appId);
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"), start);
			return;
		}
		String strUserName = "";
		if ("bindorg".equalsIgnoreCase(strAction.replace("/", ""))
				|| "getbindcode".equalsIgnoreCase(strAction.replace("/", ""))
				|| "bindphone".equalsIgnoreCase(strAction.replace("/", ""))
				|| "userbalance".equalsIgnoreCase(strAction.replace("/", ""))
				|| "usermobile".equalsIgnoreCase(strAction.replace("/", ""))
				|| "changepassword".equalsIgnoreCase(strAction.replace("/", ""))
				|| "getuid".equalsIgnoreCase(strAction.replace("/", ""))
				|| "binduser".equalsIgnoreCase(strAction.replace("/", ""))
				|| "upduserinfo".equalsIgnoreCase(strAction.replace("/", ""))
				|| "orgreply".equalsIgnoreCase(strAction.replace("/", ""))
				|| "relevanceorg".equalsIgnoreCase(strAction.replace("/", ""))
				|| "chkaudit".equalsIgnoreCase(strAction.replace("/", ""))
				|| "autitmsg".equalsIgnoreCase(strAction.replace("/", ""))
				|| "userdevice".equalsIgnoreCase(strAction.replace("/", ""))
				|| "auditstatus".equalsIgnoreCase(strAction.replace("/", ""))
				|| "orgstatus".equalsIgnoreCase(strAction.replace("/", ""))
				|| "userset".equalsIgnoreCase(strAction.replace("/", ""))
				|| "getuserset".equalsIgnoreCase(strAction.replace("/", ""))
				|| "getuserinfo".equalsIgnoreCase(strAction.replace("/", ""))
				|| "scanlogin".equalsIgnoreCase(strAction.replace("/", ""))) {
			String strToken = (String) mapReq.get("usertoken");
			if (bFlag) {
				strUserName = UserInfoMngr.UserLogin(strToken);
			} else {
				strUserName = UserInfoMngr.UserLogin(IP, strToken);
			}
			if (strUserName.startsWith("@-")) {
				sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
						.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"), start);
				return;
			}
		}

		String strRet;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "login":
			strRet = doLogin(mapReq);
			break;
		case "lbslogin":
			strRet = lBSLogin(mapReq);
			break;
		case "logout":
			strRet = doLogout(mapReq);
			break;
		// case "gettoken":
		// strRet = getUserSysToken(mapReq);
		// break;
		case "chkusername":
			strRet = IsExistUserName(mapReq);
			break;
		case "register":
			strRet = doRegister(mapReq, request);
			break;
		case "findpwd":
			strRet = findPassWord(mapReq);
			break;
		case "bindorg":
			strRet = bindOrg_New(strUserName, mapReq);
			break;
		case "relevanceorg":
			strRet = relevanceOrg(strUserName, mapReq);
			break;
		case "orgreply":
			strRet = orgreply(strUserName, mapReq);
			break;
		case "chkaudit":
			strRet = getOrgAudit(strUserName); // 获取正在审核中的机构
			break;
		case "autitmsg":
			strRet = orgAuditNotice(strUserName); // 获取审核消息记录
			break;
		case "orgstatus":
			strRet = getOrgStatus(strUserName, appId);
			break;
		case "auditstatus":
			strRet = orgAuditStatus(strUserName);
			break;
		case "relationstate": // 暂时不用
			strRet = "";
			break;
		case "relateorg":
			strRet = relateOrg(strUserName, mapReq);
		case "keepalive":
			strRet = keepAlive(mapReq);
			break;
		case "heartbeat":
			strRet = heartBeat(mapReq);
			break;
		case "getusername":
			strRet = getUserName(mapReq);
			break;
		case "binduser":
			strRet = bindUser(strUserName, mapReq);
			break;
		// case "favicon":
		// strRet = setFavicon(mapReq, request);
		// break;
		// case "getfavicon":
		// strRet = getFavicon(mapReq);
		// break;
		case "getcode":
			strRet = getValidateCode(mapReq);
			break;
		case "chkcode":
			strRet = checkValidateCode(mapReq);
			break;
		case "phoneuserreg":// 注册信息未保存到数据库当中
			strRet = phoneUserRegister(mapReq);
			break;
		case "getbindcode":
			strRet = getSymbolValidCode(strUserName, mapReq, request);
			break;
		case "bindphone":
			strRet = bindPhoneNum(strUserName, mapReq, request);
			break;
		case "userbalance":
			strRet = getUserBalance(strUserName, mapReq);
			break;
		case "usermobile":
			strRet = getUserMobile(strUserName, mapReq);
			break;
		case "changepassword":
			strRet = changeUserPassword(strUserName, mapReq);
			break;
		case "resetpwdbymobile":
			strRet = resetPwdByMobile(mapReq);
			break;
		case "resetpwdbyemail":
			strRet = resetPwdByEmail(request, mapReq);
			break;
		case "getuid":
			strRet = getIdentId(strUserName, mapReq);
			break;
		case "upduserinfo":
			strRet = updateUserInfo(strUserName, mapReq);
			break;
		case "getuserinfo":
			strRet = getCnkiUserInfo(strUserName);
			break;
		case "userdevice":
			strRet = setUserDevice(strUserName, mapReq);
			break;
		case "userset":
			strRet = addUserSet(strUserName, mapReq);
			break;
		case "getuserset":
			strRet = getUserSet(strUserName, mapReq);
			break;
		case "ecplogin": // 第三方标识登录
			strRet = ecpLogin(mapReq);
			break;
		case "ecpbinduser": // 绑定第三方账号
			strRet = ecpBindUser(mapReq);
			break;
		case "thirdlogin": // 直接登录
			strRet = thirdLogin(mapReq);
			break;
		case "visitorlogin":// 游客登录
			strRet = visitorLogin(mapReq);
			break;
		case "suguser":
			strRet = sugUserName(mapReq);
			break;
		case "qrcode":
			strRet = qrcode();
			break;
		case "chkqrcode":
			strRet = chkqrcode(mapReq);
			break;
		case "scanlogin":
			strRet = scanlogin(strUserName, mapReq);
			break;
		case "qrcodelogin":
			strRet = qrcodelogin(mapReq);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}

		sendResponseData(response, strRet, start);
	}

	private void sendResponseData(HttpServletResponse response, String Data, long start) throws IOException {
		// System.out.println(Data);
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

	private String doLogin(Map<String, String> UserInfo) {
		String strRet;
		if (UserInfo.containsKey("usertoken")) {
			String strUserName = UserInfoMngr.UserLogin(UserInfo.get("usertoken"));
			if (strUserName.startsWith("@-")) {
				strRet = "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
						.concat(strUserName.substring(1)).concat("}");
			} else {
				strRet = "{\"result\":true,\"username\":\"".concat(strUserName).concat("\"}");
			}
		} else {
			if (Common.IsNullOrEmpty(UserInfo.get("username")) || Common.IsNullOrEmpty(UserInfo.get("password"))) { // ||
																													// Common.IsNullOrEmpty(UserInfo.get("platform"))
																													// ||
																													// Common.IsNullOrEmpty(UserInfo.get("version")))
																													// {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
			}
			String[] loginRest = new String[] { "", "" };
			UserInfoMngr.UserLogin(new UserInfoBean(UserInfo), loginRest);
			if (loginRest[0].startsWith("@-")) {
				strRet = "{\"result\":false,\"message\":\"".concat(loginRest[0].substring(1))
						.concat("\",\"errorcode\":").concat(loginRest[0].substring(1)).concat("}");
			} else {
				// writeLog("login", Common.GetDateTime() + ":userName=" +
				// UserInfo.get("username") + ";returnuserName="
				// + loginRest[1]);
				strRet = "{\"result\":true,\"usertoken\":\"".concat(loginRest[0]).concat("\",\"username\":\"")
						.concat(loginRest[1]).concat("\"}");
			}
		}
		return strRet;
	}

	/**
	 * lbslogin以后他的信息我怎么记录啊。也就就是说是没有用户名和密码的
	 * 
	 * @param UserInfo
	 * @return
	 */
	private String lBSLogin(Map<String, String> UserInfo) {
		// System.out.println("SFS");
		RightMngr rightMngr = new RightMngr(Constant.PLATFORM);
		String ip = UserInfo.get("ip");
		String longitude = UserInfo.get("longitude");
		String latitude = UserInfo.get("latitude");
		if (Common.IsNullOrEmpty(longitude) || Common.IsNullOrEmpty(latitude)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}

		Holder<String> userName = new Holder<String>();
		Holder<String> unitName = new Holder<String>();
		Holder<String> identId = new Holder<String>();
		Holder<Integer> errorCode = new Holder<Integer>();
		if (rightMngr.lbsLogin(ip, Double.parseDouble(longitude), Double.parseDouble(latitude), userName, unitName,
				identId, errorCode)) {
			return "{\"result\":true,\"username\":\"" + userName.value + "\",\"unitName\":\"" + unitName.value + "\"}";
		} else {
			return "{\"result\":false,\"message\":\"位置登陆失败\",\"errorcode\":\"" + errorCode.value + "\"}";
		}
	}

	private String doLogout(Map<String, String> UserInfo) {
		String strUserToken = UserInfo.get("usertoken");
		if (Common.IsNullOrEmpty(strUserToken)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code))
					.concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code))
					.concat("}");
		}
		UserInfoMngr.UserLogout(strUserToken); // 临时注掉，以后使用
		return "{\"result\":true}";
	}

	// private String getUserSysToken(Map<String, String> UserInfo) {
	// String strUserToken = UserInfo.get("usertoken");
	// if (Common.IsNullOrEmpty(strUserToken)) {
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("\"}");
	// }
	// String strUserName = UserInfoMngr.UserLogin(strUserToken);
	// if (strUserName.startsWith("@-")) {
	// return
	// "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\"}");
	// }
	// String strUserSysToken = UserInfoMngr.getUserSysToken(strUserName);
	// if (Common.IsNullOrEmpty(strUserSysToken)) {
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("\"}");
	// }
	// return
	// "{\"result\":true,\"usersystoken\":\"".concat(strUserSysToken).concat("\"}");
	// }

	private String IsExistUserName(Map<String, String> UserInfo) {
		String strUserName = UserInfo.get("username");
		if (Common.IsNullOrEmpty(strUserName)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		// String strRegRet =
		// net.cnki.mngr.TUserMngr.IsExistUserName(strUserName);
		if (UserMngr.IsUserExist(strUserName)) {
			return "{\"result\":true,\"existuser\":true}";
		} else {
			return "{\"result\":true,\"existuser\":false}";
		}
	}

	private String doRegister(Map<String, String> UserInfo, HttpServletRequest request) {
		long t1 = System.currentTimeMillis();
		String strUserName = UserInfo.get("username");
		String strPassWord = UserInfo.get("password");
		String strEMail = UserInfo.get("email");
		String strAppid = UserInfo.get("appid");
		String strip = UserInfo.get("ip");
		String strPlatForm = request.getHeader("User-Agent") == null ? "" : request.getHeader("User-Agent");
		/*
		 * if (!Common.isEmail(strEMail)) { return
		 * "{\"result\":false,\"message\":\"email 格式错误\",\"errorcode\":"
		 * .concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code
		 * )).concat("}"); }
		 */
		UserInfoBean userInfo = new UserInfoBean();
		userInfo.setUserName(strUserName);
		userInfo.setPassWord(strPassWord);
		userInfo.setEMail(strEMail);
		userInfo.setAppid(strAppid);
		userInfo.setAddress(strip);

		// int iRet = UserInfoMngr.addUserInfo(userInfo);
		// String strRegRet =
		// net.cnki.mngr.TUserMngr.CreatPersonLib(strUserName, strPassWord,
		// strEMail, "云阅读系统");

		Holder<String> errmsg = new Holder<String>();
		String strRet = "{\"result\":true}";
		if (!UserMngr.CreateUser(Constant.PLATFORM, strUserName, strPassWord, strEMail, errmsg)) {
			strRet = "{\"result\":false,\"message\":\"".concat(errmsg.value).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_REGISTER.code)).concat("}");
		} else {
			UserInfoMngr.UserRegister(userInfo);
			UserInfoMngr.addUserInfo(strUserName, strPassWord, "", strPlatForm, strAppid, "0");
		}
		long t2 = System.currentTimeMillis();
		// System.out.println(("time=" + String.valueOf(t2 - t1)));
		return strRet;
	}

	private String findPassWord(Map<String, String> UserInfo) {
		// String strUserName = UserInfo.get("username");
		// String strEMail = UserInfo.get("email");
		return "{\"result\":true}";
	}

	/**
	 * 废弃
	 * 
	 * @param userName
	 * @param UserInfo
	 * @return
	 */
	private String bindOrg(String userName, Map<String, String> UserInfo) {
		String appId = UserInfo.get("app_id");
		String strOrgName = UserInfo.get("orgname");
		String strOrgPwd = UserInfo.get("orgpwd");
		String strIP = UserInfo.get("ip");

		String unitname = "";
		String orgname = "";
		String orgpwd = "";
		String longitude = "";
		String latitude = "";

		String strRet = "{\"result\":true}";
		boolean bResult = false;
		CnkiMngr cnkiMngr = new CnkiMngr();
		int[] iResult = new int[2];
		Holder<Integer> errorCode = new Holder<Integer>();
		bResult = cnkiMngr.cnkiUserLogin(strIP, errorCode);
		if (!bResult) {
			if (strOrgName.indexOf(";") != -1) {
				String[] arrOrgName = strOrgName.split(";");
				String[] arrOrgPwd = strOrgPwd.split(";");
				String[] tmpOrgName = arrOrgName[0].split(",");
				String[] tmpOrgPwd = arrOrgPwd[0].split(",");
				String templongitude = arrOrgName.length == 2 ? arrOrgName[1] : "";
				String templatitude = arrOrgPwd.length == 2 ? arrOrgPwd[1] : "";
				for (int i = 0; i < tmpOrgName.length; i++) {
					orgname = tmpOrgName[i];
					orgpwd = tmpOrgPwd[i];
					int[] lResult = new int[2];
					bResult = cnkiMngr.cnkiUserLogin(tmpOrgName[i], tmpOrgPwd[i], strIP, lResult);
					if (bResult) {
						if (lResult[1] == 0) {// 0为机构账号，1为个人账号
							break;
						} else {
							bResult = false;
							continue;
						}
					}
				}
				if (!bResult && templongitude.length() > 0) {
					longitude = templongitude;
					latitude = templatitude;
					orgpwd = "";
					Holder<String> orgnameHolder = new Holder<String>();
					Holder<String> unitName = new Holder<String>();

					bResult = cnkiMngr.cnkiLbsUserLogin(strIP, Double.parseDouble(longitude),
							Double.parseDouble(latitude), orgnameHolder, unitName, errorCode);
				}
			} else {
				bResult = cnkiMngr.cnkiUserLogin(strOrgName, strOrgPwd, strIP, iResult);
				if (iResult[1] == 1)// 0为机构账号，1为个人账号，个人账号不允许绑定，所以设置为false
					bResult = false;
			}
		}
		if (bResult && !Common.IsNullOrEmpty(userName)) {
			UserStruct us = cnkiMngr.getUserInfo(errorCode);
			unitname = us.getComInfo().getValue().getUnitName().getValue();
			orgname = us.getBaseInfo().getValue().getUserName().getValue();
			int days = UserOrgMngr.existValidUserInfo(userName, orgname);
			if (days == -1) {
				UserOrgMngr.insertUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);
			} else {
				UserOrgMngr.updateUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude, days);
			}

		}

		/************************ 验证移动权限 *****************************************/
		/*
		 * if (bResult && "mobilecnki".equals(appId)) { Holder<Boolean> hmr =
		 * new Holder<Boolean>(); Holder<Integer> errorCode = new
		 * Holder<Integer>(); if (!cnkiMngr.haveMobileRight(hmr, errorCode)) {
		 * return
		 * "{\"result\":true,\"passed\":false,\"message\":\" have no MobileRight\",\"msgcode\":-1}"
		 * ; } }
		 */
		if (bResult && MobileRightStatus.getMobileRight()) {
			Holder<Boolean> hmr = new Holder<Boolean>();
			if (!cnkiMngr.haveMobileRight(hmr, errorCode)) {
				return "{\"result\":false,\"message\":\" have no MobileRight\",\"errorcode\":" + errorCode.value + "}";
			}
		}
		if (bResult) {
			strRet = "{\"result\":true,\"binded\":true}";
		} else {
			strRet = "{\"result\":true,\"binded\":false,\"errorcode\":" + iResult[0] + "}";
		}
		return strRet;
	}

	private String bindOrg_New(String userName, Map<String, String> UserInfo) {

		String appId = UserInfo.get("app_id");
		String strOrgName = UserInfo.containsKey("orgname")
				? UserInfo.get("orgname") == null ? "" : UserInfo.get("orgname") : "";
		String strOrgPwd = UserInfo.containsKey("orgpwd") ? UserInfo.get("orgpwd") == null ? "" : UserInfo.get("orgpwd")
				: "";
		String strIP = UserInfo.get("ip");
		String strRet = "";
		List<Map<String, String>> orgList = initOrgList(strIP, strOrgName, strOrgPwd);
		String[] result = new String[orgList.size()];
		boolean bSuccess = false;
		for (int i = 0; i < orgList.size(); i++) {
			boolean bLogined = false;
			CnkiMngr cnkiMngr = new CnkiMngr();
			String longitude = "";
			String latitude = "";
			String orgname = "";
			String orgpwd = "";
			int[] iResult = new int[2];
			Holder<Integer> errorCode = new Holder<Integer>();
			if (orgList.get(i).get("type").equals("ip")) {
				bLogined = cnkiMngr.cnkiUserLogin(strIP, errorCode);
				if (!bLogined)
					result[i] = "-101";
			} else if (orgList.get(i).get("type").equals("orgname")) {
				orgname = orgList.get(i).get("orgname");
				orgpwd = orgList.get(i).get("orgpwd");
				bLogined = cnkiMngr.cnkiUserLogin(orgname, orgpwd, strIP, iResult);
				if (bLogined) {
					if (iResult[1] == 1) {
						bLogined = false;
						result[i] = "-102";// orgname + " is personal account";
					}
				} else {
					result[i] = orgname + " login failed";
				}
			} else if (orgList.get(i).get("type").equals("lbs")) {
				Holder<String> tmpUserName = new Holder<String>();
				Holder<String> unitName = new Holder<String>();
				longitude = orgList.get(i).get("longitude");
				latitude = orgList.get(i).get("latitude");
				bLogined = cnkiMngr.cnkiLbsUserLogin(strIP, Double.parseDouble(longitude), Double.parseDouble(latitude),
						tmpUserName, unitName, errorCode);
				if (!bLogined)
					result[i] = "-103";// "lbs login failed";
			}
			boolean bMobile = true;
			if (bLogined && MobileRightStatus.getMobileRight()) {
				Holder<Boolean> hmr = new Holder<Boolean>();

				if (!cnkiMngr.haveMobileRight(hmr, errorCode)) {
					bMobile = false;
					result[i] = "{\"result\":false,\"message\":\" have no MobileRight\",\"errorcode\":"
							+ errorCode.value + "}";
				}
			}
			if (bLogined && bMobile && !Common.IsNullOrEmpty(userName)) {
				UserStruct us = cnkiMngr.getUserInfo(errorCode);
				String unitname = us.getComInfo().getValue().getUnitName().getValue();
				orgname = us.getBaseInfo().getValue().getUserName().getValue(); // orgname
																				// 是否为空
				UserOrgMngr.insertUserOrgLog(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);

				bSuccess = true;
				int days = UserOrgMngr.existValidUserInfo(userName, orgname);
				if (days > -1) {
					UserOrgMngr.updateUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude, days);
				} else {
					if (UserOrgMngr.isBlackUser(userName, orgname)) {
						return "{\"result\":flase,\"status\":\"-2\",\"binded\":false,\"errmsg\":\"已禁用\",\"errorcode\":"
								+ errorCode.value + "}";
					}
					boolean bWhite = UserOrgMngr.isWhiteUser(userName, orgname);
					if (!bWhite) {
						Map<String, Object> orgset = UserOrgMngr.getOrgRelevanceSet(orgname);
						if (orgset != null) {
							if (orgset.get("sort").toString().equals("3")) {
								return "{\"result\":true,\"status\":\"0\",\"question\":\"" + orgset.get("question")
										+ "\"}";
							} else if (orgset.get("sort").toString().equals("2")) {
								if (UserOrgMngr.existUserOrgAudit(userName, orgname)) {
									return "{\"result\":true,\"status\":\"1\",\"message\":\"关联审核中，请耐心等待。。\"}";
								}
								if (UserOrgMngr.addUserOrgAudit(userName, strIP, unitname, orgname, orgpwd, longitude,
										latitude)) {
									return "{\"result\":true,\"status\":\"1\",\"message\":\"关联已提交，等待审核。。\"}";
								} else {
									return "{\"result\":false,\"status\":\"-1\",\"message\":\"提交审核失败\"}";
								}
							}
						}

					}
					int tdays = UserOrgMngr.existUserInfo(userName, orgname);
					if (tdays == -1) {
						UserOrgMngr.updateUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude,
								tdays);
					} else {
						UserOrgMngr.insertUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);
					}
				}
			}
		}

		if (bSuccess) {
			strRet = "{\"result\":true,\"binded\":true}";
		} else {
			StringBuilder sbResult = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				sbResult.append(result[i]).append(";");
			}
			strRet = "{\"result\":true,\"binded\":false,\"errorcode\":-1,\"errmsg\":\"" + sbResult.toString() + "\"}";
		}
		return strRet;

	}

	private String relevanceOrg(String userName, Map<String, String> UserInfo) {

		String appId = UserInfo.get("app_id");
		String strOrgName = UserInfo.containsKey("orgname")
				? UserInfo.get("orgname") == null ? "" : UserInfo.get("orgname") : "";
		String strOrgPwd = UserInfo.containsKey("orgpwd") ? UserInfo.get("orgpwd") == null ? "" : UserInfo.get("orgpwd")
				: "";
		String strIP = UserInfo.get("ip");
		String strRet = "";
		List<Map<String, String>> orgList = initOrgListNew(strIP, strOrgName, strOrgPwd);
		Map<String, Object> orgset = null;
		String[] result = new String[orgList.size()];
		boolean bSuccess = false;
		for (int i = 0; i < orgList.size(); i++) {
			boolean bLogined = false;
			CnkiMngr cnkiMngr = new CnkiMngr();
			String longitude = "";
			String latitude = "";
			String orgname = "";
			String orgpwd = "";
			int[] iResult = new int[2];
			Holder<Integer> errorCode = new Holder<Integer>();
			if (orgList.get(i).get("type").equals("ip")) {
				bLogined = cnkiMngr.cnkiUserLogin(strIP, errorCode);
				if (!bLogined)
					result[i] = String.valueOf(errorCode.value);// ;"ip login
																// failed";
			} else if (orgList.get(i).get("type").equals("orgname")) {
				orgname = orgList.get(i).get("orgname");
				orgpwd = orgList.get(i).get("orgpwd");
				orgset = UserOrgMngr.getOrgRelevanceSet(orgname);
				if (orgset != null) {
					if (("0").equals(orgset.get("iplimit").toString())) {
						if (orgset.get("ip") != null && !Common.IsNullOrEmpty(orgset.get("ip").toString()))
							strIP = orgset.get("ip").toString();
					}
				}
				bLogined = cnkiMngr.cnkiUserLogin(orgname, orgpwd, strIP, iResult);
				if (bLogined) {
					if (iResult[1] == 1) {
						bLogined = false;
						result[i] = "1018";// orgname + " is personal account";
					}
				} else {
					result[i] = String.valueOf(iResult[0]);// orgname + " login
															// failed";
				}
			} else if (orgList.get(i).get("type").equals("lbs")) {
				Holder<String> tmpUserName = new Holder<String>();
				Holder<String> unitName = new Holder<String>();

				longitude = orgList.get(i).get("longitude");
				latitude = orgList.get(i).get("latitude");
				bLogined = cnkiMngr.cnkiLbsUserLogin(strIP, Double.parseDouble(longitude), Double.parseDouble(latitude),
						tmpUserName, unitName, errorCode);
				if (!bLogined)
					result[i] = String.valueOf(errorCode.value);// "lbs login
																// failed";
			}
			boolean bMobile = true;
			if (bLogined && MobileRightStatus.getMobileRight()) {
				Holder<Boolean> hmr = new Holder<Boolean>();
				if (!cnkiMngr.haveMobileRight(hmr, errorCode)) {
					bMobile = false;
					result[i] = "{\"result\":false,\"message\":\" have no MobileRight\",\"errorcode\":"
							+ errorCode.value + "}";
				}
			}
			if (bLogined && bMobile && !Common.IsNullOrEmpty(userName)) {
				UserStruct us = cnkiMngr.getUserInfo(errorCode);
				String unitname = us.getComInfo().getValue().getUnitName().getValue();
				orgname = us.getBaseInfo().getValue().getUserName().getValue(); // orgname
																				// 是否为空
				UserOrgMngr.insertUserOrgLog(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);
				// 此处经常多次插入数据：需要排查：
				bSuccess = true;
				int days = UserOrgMngr.existValidUserInfo(userName, orgname);
				if (days > -1) {
					UserOrgMngr.updateUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude, days);
				} else {
					if (UserOrgMngr.isBlackUser(userName, orgname)) {
						return "{\"result\":false,\"status\":\"-2\",\"message\":\"已禁用\",\"errorcode\":"
								+ SysConfigMngr.ERROR_CODE.ERROR_NOUSING + "}";
					}
					boolean bWhite = UserOrgMngr.isWhiteUser(userName, orgname);
					if (!bWhite) {
						if (orgset == null)
							orgset = UserOrgMngr.getOrgRelevanceSet(orgname);
						if (orgset != null) {
							if (orgset.get("sort").toString().equals("3")) {
								return "{\"result\":false,\"status\":\"2\",\"message\":\"" + orgset.get("question")
										+ "\"}";
							} else if (orgset.get("sort").toString().equals("2")) {
								if (UserOrgMngr.existUserOrgAudit(userName, orgname)) {
									return "{\"result\":false,\"status\":\"1\",\"message\":\"关联审核中，请耐心等待。。\"}";
								}
								if (UserOrgMngr.addUserOrgAudit(userName, strIP, unitname, orgname, orgpwd, longitude,
										latitude)) {
									return "{\"result\":false,\"status\":\"1\",\"message\":\"关联已提交，等待审核。。\"}";
								} else {
									return "{\"result\":false,\"status\":\"-1\",\"message\":\"提交审核失败\",\"errorcode\":"
											+ SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE + "}";
								}
							}
						}
					}
					int tdays = UserOrgMngr.existUserInfo(userName, orgname);
					if (tdays == -1) {
						UserOrgMngr.updateUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude,
								tdays);
					} else {
						UserOrgMngr.insertUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);
					}
				}
			}
		}

		if (bSuccess) {
			strRet = "{\"result\":true,\"binded\":true}";
		} else {
			StringBuilder sbResult = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				sbResult.append(result[i]).append(";");
			}
			strRet = "{\"result\":true,\"binded\":false,\"errorcode\":-" + result[0] + ",\"errmsg\":\""
					+ sbResult.toString() + "\"}";
		}
		return strRet;
	}

	private String orgreply(String userName, Map<String, String> UserInfo) {

		String appId = UserInfo.get("app_id");
		String strOrgName = UserInfo.containsKey("orgname")
				? UserInfo.get("orgname") == null ? "" : UserInfo.get("orgname") : "";
		String strOrgPwd = UserInfo.containsKey("orgpwd") ? UserInfo.get("orgpwd") == null ? "" : UserInfo.get("orgpwd")
				: "";
		String answer = UserInfo.containsKey("answer") ? UserInfo.get("answer") == null ? "" : UserInfo.get("answer")
				: "";
		String strIP = UserInfo.get("ip");
		String strRet = "";
		List<Map<String, String>> orgList = initOrgListNew(strIP, strOrgName, strOrgPwd);
		String[] result = new String[orgList.size()];
		boolean bSuccess = false;
		for (int i = 0; i < orgList.size(); i++) {
			boolean bLogined = false;
			CnkiMngr cnkiMngr = new CnkiMngr();
			String longitude = "";
			String latitude = "";
			String orgname = "";
			String orgpwd = "";
			int[] iResult = new int[2];
			Holder<Integer> errorCode = new Holder<Integer>();
			if (orgList.get(i).get("type").equals("ip")) {
				bLogined = cnkiMngr.cnkiUserLogin(strIP, errorCode);
				if (!bLogined)
					result[i] = String.valueOf(errorCode.value);// ;"ip login
																// failed";
			} else if (orgList.get(i).get("type").equals("orgname")) {
				orgname = orgList.get(i).get("orgname");
				orgpwd = orgList.get(i).get("orgpwd");
				bLogined = cnkiMngr.cnkiUserLogin(orgname, orgpwd, strIP, iResult);
				if (bLogined) {
					if (iResult[1] == 1) {
						bLogined = false;
						result[i] = "1018";// orgname + " is personal account";
					}
				} else {
					result[i] = orgname + " login failed";
				}
			} else if (orgList.get(i).get("type").equals("lbs")) {
				Holder<String> tmpUserName = new Holder<String>();
				Holder<String> unitName = new Holder<String>();

				longitude = orgList.get(i).get("longitude");
				latitude = orgList.get(i).get("latitude");
				bLogined = cnkiMngr.cnkiLbsUserLogin(strIP, Double.parseDouble(longitude), Double.parseDouble(latitude),
						tmpUserName, unitName, errorCode);
				if (!bLogined)
					result[i] = errorCode.value.toString();// "lbs login
															// failed";
			}
			boolean bMobile = true;
			if (bLogined && MobileRightStatus.getMobileRight()) {
				Holder<Boolean> hmr = new Holder<Boolean>();
				if (!cnkiMngr.haveMobileRight(hmr, errorCode)) {
					bMobile = false;
					result[i] = "{\"result\":false,\"message\":\" have no MobileRight\",\"errorcode\":" + "-"
							+ String.valueOf(Math.abs(errorCode.value)) + "}";
				}
			}
			if (bLogined && bMobile && !Common.IsNullOrEmpty(userName)) {
				UserStruct us = cnkiMngr.getUserInfo(errorCode);
				String unitname = us.getComInfo().getValue().getUnitName().getValue();
				orgname = us.getBaseInfo().getValue().getUserName().getValue(); // orgname
																				// 是否为空

				Map<String, Object> orgset = UserOrgMngr.getOrgRelevanceSet(orgname);
				if (answer.equals(orgset.get("answer"))) {
					bSuccess = true;
					UserOrgMngr.insertUserOrgLog(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);
					// 此处经常多次插入数据：需要排查：
					int tday = UserOrgMngr.existUserInfo(userName, orgname);
					if (tday == -1) {
						UserOrgMngr.updateUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude,
								tday);
					} else {
						UserOrgMngr.insertUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);
					}
				} else {
					result[i] = "问题回答错误";
				}
			}
		}

		if (bSuccess) {
			strRet = "{\"result\":true,\"binded\":true}";
		} else {
			StringBuilder sbResult = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				sbResult.append("-").append(result[i]).append(";");
			}
			strRet = "{\"result\":true,\"binded\":false,\"errorcode\":-1,\"errmsg\":\"" + sbResult.toString() + "\"}";
		}
		return strRet;
	}

	/**
	 * 如果有多个只能一个，关联的最后一个才是有效的,还得想想
	 * 
	 * @param userName
	 * @return
	 */
	private String getOrgAudit(String userName) {
		JSONObject json = new JSONObject();
		JSONArray ary = new JSONArray();
		List<Map<String, Object>> lst = UserOrgMngr.chkOrgAuditing(userName);
		if (lst == null || lst.size() == 0) {
			json.put("result", true);
			json.put("data", ary.toString());
			return json.toString();
		}
		for (Map<String, Object> map : lst) {
			JSONObject datajson = new JSONObject();
			datajson.put("unitname", map.get("unitname").toString());
			datajson.put("orgname", map.get("orgname").toString());
			ary.add(datajson);
		}
		json.put("result", true);
		json.put("data", ary.toString());
		return json.toString();
	}

	private String orgAuditNotice(String userName) {
		JSONObject json = new JSONObject();
		JSONArray ary = new JSONArray();
		String strTime = UserOrgMngr.getOrgAuditNoticeTime(userName);
		List<Map<String, Object>> lst = UserOrgMngr.getOrgAuditNotice(userName, strTime);
		if (lst == null || lst.size() == 0) {
			json.put("result", true);
			json.put("data", ary.toString());
			return json.toString();
		}
		for (Map<String, Object> map : lst) {// unitname,orgname,status
			JSONObject datajson = new JSONObject();
			datajson.put("unitname", map.get("unitname").toString());
			datajson.put("orgname", map.get("orgname").toString());
			datajson.put("status", map.get("status").toString());
			datajson.put("time", map.get("updatetime").toString());
			ary.add(datajson);
		}
		json.put("result", true);
		json.put("data", ary.toString());
		return json.toString();

	}

	private String getOrgStatus(String userName, String appId) {
		JSONObject json = new JSONObject();
		Map<String, Object> map = null;
		int roamDay = AppInfoMngr.getAppRoam(appId);
		map = UserOrgMngr.getOrgStatus(userName, roamDay);
		if (map != null) {
			json.put("result", true);
			json.put("unitname", map.get("unitname"));
			json.put("orgname", map.get("orgname"));
			json.put("time", map.get("updatetime"));
			json.put("days", map.get("days"));
		} else {
			json.put("result", false);
		}
		return json.toString();
	}

	private String orgAuditStatus(String userName) {
		int iCount = 0;
		JSONArray jsonArray = new JSONArray();
		List<Map<String, Object>> lstStatus = UserOrgMngr.orgAuditStatus(userName);
		StringBuilder sbIds = null;
		if (lstStatus != null && lstStatus.size() > 0) {
			sbIds = new StringBuilder();
			iCount = lstStatus.size();
			for (Map<String, Object> map : lstStatus) {
				JSONObject jsonObj = new JSONObject();
				sbIds.append(map.get("id").toString()).append(",");
				jsonObj.put("orgname", map.get("orgname"));
				jsonObj.put("unitname", map.get("unitname"));
				jsonObj.put("status", map.get("status"));
				jsonArray.add(jsonObj);
			}
			if (sbIds != null && sbIds.length() > 1) {
				sbIds = sbIds.delete(sbIds.length() - 1, sbIds.length());
				UserOrgMngr.updOrgAuditStatus(sbIds.toString());
			}
		}
		return "{\"result\":true,\"count\":" + iCount + ",\"data\":" + jsonArray.toString() + "}";
	}

	/**
	 * 机构关联单选，方法实现
	 * 
	 * @param userName
	 * @param UserInfo
	 * @return
	 */
	private String relateOrg(String userName, Map<String, String> UserInfo) {
		String appId = UserInfo.get("app_id");
		String strOrgName = UserInfo.get("orgname");
		String strOrgPwd = UserInfo.get("orgpwd");

		int flag = 0; // 0为ip，1为机构名机构密码登录，2为lbs
		String strIP = UserInfo.get("ip");
		String orgname = "";
		String orgpwd = "";
		String longitude = "";
		String latitude = "";

		if (!Common.IsNullOrEmpty(strOrgName)) {
			String[] arrOrgName = strOrgName.split(";");
			String[] arrOrgPwd = strOrgPwd.split(";");
			if (arrOrgName.length > 0) {
				orgname = arrOrgName[0];
				orgpwd = arrOrgPwd[0];
				if (Common.IsNullOrEmpty(orgname)) {
					if (arrOrgName.length > 1) {
						longitude = arrOrgName[1];
						latitude = arrOrgPwd[1];
						if (!Common.IsNullOrEmpty(longitude))
							flag = 2;
					}
				} else {
					flag = 1;
				}
			}
		}
		boolean bLogined = false;
		int[] iResult = new int[2];
		String result = "";
		Holder<String> tmpUserName = new Holder<String>();
		Holder<String> unitName = new Holder<String>();
		Holder<Integer> errorCode = new Holder<Integer>();
		CnkiMngr cnkiMngr = new CnkiMngr();

		if (flag == 1) {
			bLogined = cnkiMngr.cnkiUserLogin(orgname, orgpwd, strIP, iResult);
			if (bLogined) {
				if (iResult[1] == 1) {
					bLogined = false;
					result = orgname + " is personal account";
				}
			} else {
				result = orgname + " login failed";
			}
		} else if (flag == 2) {
			bLogined = cnkiMngr.cnkiLbsUserLogin(strIP, Double.parseDouble(longitude), Double.parseDouble(latitude),
					tmpUserName, unitName, errorCode);
			if (!bLogined)
				result = "lbs login failed";
		} else {
			bLogined = cnkiMngr.cnkiUserLogin(strIP, errorCode);
			if (!bLogined)
				result = "ip login failed";
		}
		if (bLogined) {
			String[] rest = null;
			if (UserOrgMngr.getOrgSet(rest)) {
				// 判断是否可以直接关联成功，
				UserStruct us = cnkiMngr.getUserInfo(errorCode);
				String unitname = us.getComInfo().getValue().getUnitName().getValue();
				orgname = us.getBaseInfo().getValue().getUserName().getValue();
				UserOrgMngr.insertUserOrgLog(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);
				UserOrgMngr.insertUserOrgToday(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);
				int tdays = UserOrgMngr.existUserInfo(userName, orgname);
				if (tdays == -1) {
					UserOrgMngr.updateUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude, tdays);
				} else {
					UserOrgMngr.insertUserOrg(userName, strIP, unitname, orgname, orgpwd, longitude, latitude);
				}
			}
		}
		return "";
	}

	private String keepAlive(Map<String, String> UserInfo) {
		String strRet = "";
		String strUserToken = UserInfo.get("usertoken");
		String strClientID = UserInfo.get("clientid");
		String strPlatForm = UserInfo.get("platform");
		String strAppInfo = UserInfo.get("appinfo");
		int iStatus = UserInfo.get("status") == null ? 1 : Integer.parseInt(String.valueOf(UserInfo.get("status")));
		if ((strUserToken == null || strUserToken.length() == 0)
				&& (strClientID == null || strClientID.length() == 0)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String strUserName = "";
		if (!Common.IsNullOrEmpty(strUserToken)) {
			strUserName = UserInfoMngr.UserLogin(strUserToken);
			if (strUserName.startsWith("@-")) {
				return "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
						.concat(strUserName.substring(1)).concat("}");
			}
		}
		strRet = BehaviourMngr.addKeepAlive(strUserName, strClientID, strPlatForm, strAppInfo, iStatus);
		// if (iStatus == 1) {
		// strRet = BehaviourMngr.addKeepAlive(strUserName, strClientID,
		// strPlatForm, strAppInfo, iStatus);
		// } else {
		// strRet = BehaviourMngr.updateKeepAlive(strUserName, strClientID,
		// strPlatForm, strAppInfo);
		// }
		return strRet;
	}

	private String heartBeat(Map<String, String> UserInfo) {
		return "{\"result\":true,\"interval\":" + 300000 + "}";
		/*
		 * String strRet = ""; String strUserToken = UserInfo.get("usertoken");
		 * String strClientID = UserInfo.get("clientid"); String strPlatForm =
		 * UserInfo.get("platform"); String strAppInfo =
		 * UserInfo.get("appinfo"); String strIP = UserInfo.get("ip"); String
		 * strSign = String.valueOf(UserInfo.get("sign")); String appId =
		 * UserInfo.get("appid"); int iStatus = UserInfo.get("status") == null ?
		 * 1 : Integer.parseInt(String.valueOf(UserInfo.get("status"))); if
		 * ((strUserToken == null || strUserToken.length() == 0) && (strClientID
		 * == null || strClientID.length() == 0)) { return
		 * "{\"result\":false,\"message\":\""
		 * .concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code
		 * )).concat("\",\"errorcode\":")
		 * .concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code
		 * )).concat("}"); } String strUserName = ""; if
		 * (!Common.IsNullOrEmpty(strUserToken)) { strUserName =
		 * UserInfoMngr.UserLogin(strUserToken); if
		 * (strUserName.startsWith("@-")) { return
		 * "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).
		 * concat("\",\"errorcode\":")
		 * .concat(strUserName.substring(1)).concat("}"); } } if (iStatus == 1)
		 * { strRet = BehaviourMngr.addHeartBeat(strUserName, strClientID,
		 * strPlatForm, strAppInfo, strIP, appId, strSign == null ? (long) 0 :
		 * Long.parseLong(strSign), iStatus); } else { strRet =
		 * BehaviourMngr.updateHeartBeat(strUserName, strClientID, strSign); }
		 * return strRet;
		 */
	}

	private String orgHeatBeat(Map<String, String> UserInfo) {
		String strRet = "";
		String strUserToken = UserInfo.get("usertoken");
		String strClientID = UserInfo.get("clientid");
		String strPlatForm = UserInfo.get("platform");
		String strAppInfo = UserInfo.get("appinfo");
		String strIP = UserInfo.get("ip");
		String strSign = String.valueOf(UserInfo.get("sign"));
		String appId = UserInfo.get("appid");
		int iStatus = UserInfo.get("status") == null ? 1 : Integer.parseInt(String.valueOf(UserInfo.get("status")));
		if ((strUserToken == null || strUserToken.length() == 0)
				&& (strClientID == null || strClientID.length() == 0)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String strUserName = "";
		if (!Common.IsNullOrEmpty(strUserToken)) {
			strUserName = UserInfoMngr.UserLogin(strUserToken);
			if (strUserName.startsWith("@-")) {
				return "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
						.concat(strUserName.substring(1)).concat("}");
			}
		}
		if (iStatus == 1) {
			strRet = BehaviourMngr.addHeartBeat(strUserName, strClientID, strPlatForm, strAppInfo, strIP, appId,
					strSign == null ? (long) 0 : Long.parseLong(strSign), iStatus);
		} else {
			strRet = BehaviourMngr.updateHeartBeat(strUserName, strClientID, strSign);
		}
		return strRet;
	}

	/**
	 * 时间戳必须get方式传输过来，方便filter验证,
	 * 
	 * @param UserInfo
	 * @return
	 */
	private String getUserName(Map<String, String> UserInfo) {
		String strToken = UserInfo.get("usertoken");

		String appID = UserInfo.get("app_id");
		String timeStamp = UserInfo.get("timestamp");
		String sign = UserInfo.get("sign");
		if (!authAppInfo(appID, timeStamp, sign)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String strUserName = UserInfoMngr.UserLogin(strToken);
		if (strUserName.startsWith("@-")) {
			return "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
					.concat(strUserName.substring(1)).concat("}");
		} else {
			return "{\"result\":true,\"usertoken\":\"".concat(strUserName).concat("\"}");
		}
	}

	/**
	 * 
	 * @param appID
	 * @param timeStamp
	 * @param sign
	 * @return
	 */
	private boolean authAppInfo(String appID, String timeStamp, String sign) {
		String appKey = "K55qhHX4fvoE7Rc3";
		return Common.SHA1(timeStamp.concat(appKey)).equals(sign);

	}

	// private String setFavicon(Map<String, String> UserInfo,
	// HttpServletRequest request) {
	// String strUserToken = UserInfo.get("usertoken");
	// String strFavicon = UserInfo.get("favicon"); // 图像名称
	// String strFaviconType = UserInfo.get("type"); // 图像格式
	// String strUserName = "";
	//
	// if (!Common.IsNullOrEmpty(strUserToken)) {
	// strUserName = UserInfoMngr.UserLogin(strUserToken);
	// if (strUserName.startsWith("@-")) {
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("}");
	// }
	// } else {
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("}");
	// }
	// long nowTime = System.currentTimeMillis();
	// String strFaviconPath =
	// strUserImage.concat(strUserName).concat(String.valueOf(nowTime)).concat(".").concat(strFaviconType);
	// int picLength = request.getContentLength();
	// if (picLength == 0) {
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
	// }
	//
	// byte[] bPicContent = new byte[picLength];
	// DataInputStream dataInput;
	// String upTime = "";
	// try {
	// dataInput = new DataInputStream(request.getInputStream());
	// dataInput.readFully(bPicContent);
	// request.getInputStream().close();
	// dataInput.close();
	// File picFile = new File(strFaviconPath);
	// FileOutputStream picOut = new FileOutputStream(picFile);
	// picOut.write(bPicContent);
	// picOut.close();
	// upTime = UserInfoMngr.saveFavicon(strUserName, strFavicon,
	// strFaviconType, strFaviconPath);
	// if (Common.IsNullOrEmpty(upTime)) {
	// picFile.delete();
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
	// }
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_WRITEUPLOADFILE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_WRITEUPLOADFILE.code)).concat("}");
	// }
	// return "{\"result\":true,\"updatetime\":\"".concat(upTime).concat("\"}");
	// }
	//
	// private String getFavicon(Map<String, String> args) {
	// String strUserToken = args.get("usertoken");
	// String strUserName = "";
	// if (!Common.IsNullOrEmpty(strUserToken)) {
	// strUserName = UserInfoMngr.UserLogin(strUserToken);
	// if (strUserName.startsWith("@-")) {
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("}");
	// }
	// } else {
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("}");
	// }
	// //String strUpTime = args.get("updatetime") == null ? "" :
	// String.valueOf(args.get("updatetime"));
	// Map<String, Object> mapFavicon =
	// UserInfoMngr.getUserFavicon(strUserName);
	// if (mapFavicon == null) {
	// return "{\"result\":true,\"imagesrc\":\"\",\"updatetime\":\"\"}";
	// } else {
	// return "{\"result\":true,\"imagesrc\":\"" + mapFavicon.get("path") +
	// "\",\"updatetime\":\"" + mapFavicon.get("updatetime") + "\"}";
	// }
	// }
	/**
	 * 判断是否已经注册，返回验证码
	 * 
	 * @return
	 */
	private String getValidateCode(Map<String, String> UserInfo) {
		String phoneNum = UserInfo.get("phonenum");
		if (Common.IsNullOrEmpty(phoneNum)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String authCode = MobilePhoneMngr.createValidateCode(phoneNum);
		String txtMsg = "欢迎使用中国知网，您此次的验证码：" + authCode + "，请在3分钟内使用，勿泄露！";
		if (UserMngr.SendSMS(phoneNum, txtMsg)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"验证码发送失败\"}";
		}
	}

	private String checkValidateCode(Map<String, String> UserInfo) {
		String phoneNum = UserInfo.get("phonenum");
		String code = UserInfo.get("code");
		if (Common.IsNullOrEmpty(phoneNum) || Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (MobilePhoneMngr.checkValidateCode(phoneNum, code)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"您输入的验证码不正确，或已过期\"}";
		}
	}

	private String phoneUserRegister(Map<String, String> UserInfo) {
		String phoneNum = UserInfo.get("phonenum");
		String password = UserInfo.get("password");
		String strAppid = UserInfo.get("appid");
		String ip = UserInfo.get("ip");
		String code = UserInfo.get("code");
		if (Common.IsNullOrEmpty(phoneNum) || Common.IsNullOrEmpty(password) || Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}

		if (!MobilePhoneMngr.checkValidateCode(phoneNum, code)) {
			return "{\"result\":false,\"message\":\"您输入的验证码不正确，或已过期\"}";
		}
		if (UserMngr.IsUserExist(phoneNum)) {
			return "{\"result\":false,\"message\":\"手机号已存在\"}";
		}

		Holder<String> errmsg = new Holder<String>();
		String userName = "cnki_" + Long.toHexString(System.currentTimeMillis());

		if (UserMngr.IsUserExist(userName)) {
			userName = "cnki_" + Long.toHexString(System.currentTimeMillis());
		}

		if (UserMngr.CreateUserByMobile(Constant.PLATFORM, userName, phoneNum, password, errmsg)) {

			// // 手机号注册成功，把手机号手动绑定到该用户
			if (manualBindPhone(userName, phoneNum, password, ip)) {
				UserInfoBean userInfo = new UserInfoBean();
				userInfo.setUserName(userName);
				userInfo.setPassWord(password);
				userInfo.setEMail("");
				userInfo.setAppid(strAppid);
				userInfo.setAddress(ip);
				userInfo.setMobile(phoneNum);
				UserInfoMngr.UserRegister(userInfo);
				// writeLog("register", Common.GetDateTime() + ":userName=" +
				// userName + ";phone=" + phoneNum);
				return "{\"result\":true}";
			} else
				return "{\"result\":false,\"message\":\"手机注册失败\",\"errorcode\":\"" + errmsg.value + "\"}";
		} else {
			return "{\"result\":false,\"message\":\"手机注册失败\",\"errorcode\":\"" + errmsg.value + "\"}";
		}
	}

	/*
	 * public static void writeLogs(String folder, String data) { File file =
	 * new File("/root/" + folder + ".txt"); try { if (!file.exists()) {
	 * file.createNewFile(); } FileWriter sucsessFile = new FileWriter(file,
	 * true); sucsessFile.write(data + "\r\n"); sucsessFile.close(); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * finally {
	 * 
	 * } }
	 */
	private String getSymbolValidCode(String userName, Map<String, String> UserInfo, HttpServletRequest request)
			throws ServletException, IOException {
		String phoneNum = UserInfo.get("phonenum");
		String ip = UserInfo.get("ip");
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(phoneNum)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		CnkiMngr cnkiMngr = new CnkiMngr();
		Holder<Integer> errorCode = new Holder<Integer>();
		if (!cnkiMngr.cnkiUserLogin(userName, ip, errorCode)) {// CnkiMngr.useridentid.put(userName,
			// cnkiMngr.getIdentId());
			return "{\"result\":false,\"message\":\"登陆失败\",\"errorcode\":"
					+ String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_USERNAMEORPASSWORD) + "}";
		}
		int timeStamp = 60;
		String validTime = Common.GetConfig("ValidTime");
		if (!Common.IsNullOrEmpty(validTime)) {
			timeStamp = Integer.parseInt(validTime);
		}
		Holder<String> validCode = new Holder<String>();

		Holder<Integer> symbolValidCodeResult = new Holder<Integer>();
		if (cnkiMngr.getSymbolValidCode(phoneNum, timeStamp, 6, validCode, symbolValidCodeResult, errorCode)) {
			String txtMsg = "欢迎使用中国知网，您此次的验证码：" + validCode.value + "，请在3分钟内使用，勿泄露！";
			if (UserMngr.SendSMS(phoneNum, txtMsg)) {
				return "{\"result\":true,\"identid\":\"" + cnkiMngr.getIdentId() + "\"}";
			} else {
				return "{\"result\":false,\"message\":\"验证码发送失败\"}";
			}
		} else {
			return "{\"result\":false,\"message\":\"获取验证码失败\",\"errorcode\":\"" + errorCode.value + "\",\"errcode\":\""
					+ errorCode.value + "\"}";
		}
	}

	private String bindPhoneNum(String userName, Map<String, String> UserInfo, HttpServletRequest request)
			throws ServletException, IOException {
		String phoneNum = UserInfo.get("phonenum");
		String identid = UserInfo.get("identid");
		String code = UserInfo.get("code");
		if (Common.IsNullOrEmpty(identid) || Common.IsNullOrEmpty(phoneNum) || Common.IsNullOrEmpty(code)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		/*
		 * String identid = CnkiMngr.useridentid.get(userName);
		 * if(Common.IsNullOrEmpty(identid)){ return
		 * "{\"result\":false,\"message\":\"您输入的验证码不正确，或已过期\"}"; }
		 * CnkiMngr.useridentid.remove(userName);
		 */
		Holder<Integer> errorCode = new Holder<Integer>();
		if (CnkiMngr.setSymbolName(identid, phoneNum, code, errorCode)) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"绑定手机号失败\",\"errorcode\":\"" + errorCode.value + "\"}";
		}
	}

	private String getUserBalance(String userName, Map<String, String> UserInfo) {
		String ip = UserInfo.get("ip");
		if (ip.equals("127.0.0.1"))
			ip = "101.81.229.133";

		CnkiMngr cnkiMngr = new CnkiMngr();
		Holder<Integer> errorCode = new Holder<Integer>();
		if (!cnkiMngr.cnkiUserLogin(userName, ip, errorCode)) {
			return "{\"result\":false,\"message\":\"登陆失败\",\"errorcode\":"
					+ String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_USERNAMEORPASSWORD) + ",\"errcode\":"
					+ String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_USERNAMEORPASSWORD) + "}";
		}
		Holder<Integer> showBalance = new Holder<Integer>();
		Holder<Double> balance = new Holder<Double>();
		Holder<Double> ticket = new Holder<Double>();

		if ("zhu_zhu18".equals(userName)) {
			writeLog("userName2=" + userName + "jinlai");
		}
		// cnkiMngr.getFirstODataFileInfo();
		if (userName.toLowerCase().equals("appletest2013")) {
			return "{\"result\":true,\"userbalance\":\"0.0\",\"userticket\":0}";
		}
		if (cnkiMngr.getUserBalance(userName, showBalance, balance, ticket, errorCode)) {
			return "{\"result\":true,\"userbalance\":\"" + balance.value + "\",\"userticket\":" + ticket.value + "}";
		} else {
			return "{\"result\":false,\"message\":\"获取余额失败\",\"errorcode\":\"" + errorCode.value + "\",\"errcode\":\""
					+ errorCode.value + "\"}";
		}
	}

	public static void writeLog(String data) {
		File file = new File("d:\\cnki0823rightmngr.txt");
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

	private String getUserMobile(String userName, Map<String, String> UserInfo) {
		/*
		 * String ip = UserInfo.get("ip"); CnkiMngr cnkiMngr = new CnkiMngr();
		 * Holder<Integer> errorCode = new Holder<Integer>(); if
		 * (!cnkiMngr.cnkiUserLogin(userName, ip, errorCode)) { return
		 * "{\"result\":false,\"message\":\"登陆失败\",\"errorcode\":\"" +
		 * String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_USERNAMEORPASSWORD) +
		 * "\",\"errcode\":\"" +
		 * String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_USERNAMEORPASSWORD) +
		 * "\"}"; }
		 * 
		 * UserStruct us = cnkiMngr.getUserInfo(errorCode); if (us == null) {
		 * return "{\"result\":false,\"message\":\"获取手机信息失败\",\"errorcode\":" +
		 * String.valueOf(errorCode.value) + ",\"errcode\":" +
		 * String.valueOf(errorCode.value) + "}"; } return
		 * "{\"result\":true,\"mobile\":\"" +
		 * us.getComInfo().getValue().getMobile().getValue() + "\"}";
		 */
		JSONObject json = new JSONObject();
		try {
			json = BLL.EcpMngr.isBindToMobile(userName);
		} catch (Exception e) {
			return "{\"result\":false,\"message\":\"获取手机信息失败\",\"errorcode\":"
					+ String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_URLTIME) + ",\"errcode\":"
					+ String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_URLTIME) + "}";
		}
		String mobile = json.get("Mobile").toString();
		JSONObject ret = new JSONObject();
		ret.put("result", json.get("IsBind"));
		ret.put("mobile", "null".equalsIgnoreCase(mobile) ? "" : mobile);
		return ret.toString();
	}

	private String changeUserPassword(String userName, Map<String, String> UserInfo) {
		String oldPassword = UserInfo.get("oldpassword");
		String newPassword = UserInfo.get("newpassword");
		if (Common.IsNullOrEmpty(oldPassword) || Common.IsNullOrEmpty(newPassword)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String deOldPassword = Common.DESDecrypt(oldPassword, "@a3k9#-;jdiu$98JH-03H~kpb59akj8j");
		String deNewPassword = Common.DESDecrypt(newPassword, "@a3k9#-;jdiu$98JH-03H~kpb59akj8j");
		String strRet = null;
		try {
			strRet = EcpMngr.ChangeUserPassword(userName, deOldPassword, deNewPassword);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Common.IsNullOrEmpty(strRet)) {
			UserInfoMngr.updateUserPassword(userName, deNewPassword);
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"" + strRet + "\",\"errorcode\":\"" + strRet + "\",\"errcode\":\""
					+ strRet + "\"}";
		}

	}

	/**
	 *  通过手机号修改密码
	 */
	private String resetPwdByMobile(Map<String, String> UserInfo) {
		String phoneNumber = UserInfo.get("phone");
		String validateCode = UserInfo.get("validatecode");
		String newPassword = UserInfo.get("newpassword");
		JSONObject res = new JSONObject();
		if (Common.IsNullOrEmpty(phoneNumber) || Common.IsNullOrEmpty(validateCode)|| Common.IsNullOrEmpty(newPassword)) {
			res.put("result", false);
			res.put("message", "参数不能为空");
			res.put("errorcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			return res.toString();
		}
		if(!MobilePhoneMngr.checkValidateCode(phoneNumber, validateCode)) {
			res.put("result", false);
			res.put("message", "验证码错误或已过期");
			res.put("errorcode",String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_VALIDATECODE.code));
			return res.toString();
		}
		String deNewPassword = Common.DESDecrypt(newPassword, "@a3k9#-;jdiu$98JH-03H~kpb59akj8j");
		JSONObject strRet = null;
		if(deNewPassword.contains(" ")||deNewPassword.contains("+")) {
			res.put("result", false);
			res.put("errorcode",String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_INCORRECTCHARACTERS.code));
			res.put("message", "密码禁止包含空格、加号");
			return res.toString();
		}
		try {
			//修改密码
			strRet = BLL.EcpMngr.resetpwdbymobile(phoneNumber, phoneNumber,deNewPassword);
			//{"Success":false,"Code":0,"Message":"手机号码和用户名不匹配"}  "++" ""
			//{"Success":false,"Code":-2,"Message":"参数不能为空"}
			//密码带空格报错  " "
		} catch (Exception e) {
			e.printStackTrace();
			res.put("result", false);
			res.put("message", "修改密码失败");
			return res.toString();
		}
		if (strRet != null &&strRet.containsKey("Success")&& strRet.getBoolean("Success")) {
			UserInfoMngr.updateUserPassword(phoneNumber, deNewPassword);
			res.put("result", true);
			return res.toString();
		} else {
			res.put("result", false);
			res.put("message", strRet.getString("Message"));
			if("0".equals(strRet.getString("Code"))){
				res.put("errorcode",String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_INCONSISTENT.code));
			}
			return res.toString();
		}
		
	}
	
	/**
	 *  通过邮箱修改密码
	 */
	private String resetPwdByEmail(HttpServletRequest request,Map<String, String> UserInfo) {
		String username = UserInfo.get("username");
		String email = UserInfo.get("email");
		JSONObject res = new JSONObject();
		if (Common.IsNullOrEmpty(username) || Common.IsNullOrEmpty(email)) {
			res.put("result", false);
			res.put("message", "参数不能为空");
			res.put("errorcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			return res.toString();
		}		
		
		String NewPassword = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
		JSONObject strRet = null;
		try {
			//修改密码
			strRet = BLL.EcpMngr.resetpwdbyemail(username, email, NewPassword);
			//{"Success":false,"Code":-2,"Message":"参数不能为空"}   "++" ""
			//{"Success":false,"Code":0,"Message":"邮箱和用户名不匹配"}
		} catch (Exception e) {
			e.printStackTrace();
			res.put("result", false);
			res.put("message", "修改密码失败");
			return res.toString();
		}
		if (strRet != null &&strRet.containsKey("Success")&& strRet.getBoolean("Success")) {
			UserInfoMngr.updateUserPassword(username, NewPassword);
			EmailUtil.sendEmail(email, NewPassword);
			res.put("result", true);
			return res.toString();
		} else {
			res.put("result", false);
			res.put("message", strRet.getString("Message"));
			if("0".equals(strRet.getString("Code"))){
				res.put("errorcode",String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_INCONSISTENT.code));
			}
			return res.toString();
		}
		
	}

	private String getIdentId(String userName, Map<String, String> UserInfo) {
		String ip = UserInfo.get("ip");
		CnkiMngr cnkiMngr = new CnkiMngr();
		Holder<Integer> errorCode = new Holder<Integer>();
		if (!cnkiMngr.cnkiUserLogin(userName, ip, errorCode)) {
			return "{\"result\":false,\"message\":\"登陆失败\",\"errorcode\":"
					+ String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_USERNAMEORPASSWORD) + ",\"errcode\":"
					+ String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_USERNAMEORPASSWORD) + "}";
		} else {
			// System.out.println(cnkiMngr.getIdentId());
			// System.out.println(cnkiMngr.getIdentId().substring(0,
			// cnkiMngr.getIdentId().indexOf("|")));
			return "{\"result\":true,\"identid\":\""
					+ cnkiMngr.getIdentId().substring(0, cnkiMngr.getIdentId().indexOf("|")) + "\"}";
		}
	}

	private String updateUserInfo(String userName, Map<String, String> UserInfo) {
		String name = UserInfo.containsKey("name") ? UserInfo.get("name") : "";
		String sex = UserInfo.containsKey("sex") ? UserInfo.get("sex") : "";
		String birthday = UserInfo.containsKey("birthday") ? UserInfo.get("birthday") : "";
		String unitname = UserInfo.containsKey("unitname") ? UserInfo.get("unitname") : "";
		String email = UserInfo.containsKey("email") ? UserInfo.get("email") : "";
		String mobile = UserInfo.containsKey("mobile") ? UserInfo.get("mobile") : "";
		String major = UserInfo.containsKey("major") ? UserInfo.get("major") : "";
		String imageurl = UserInfo.containsKey("imageurl") ? UserInfo.get("imageurl") : "";
		String nickname = UserInfo.containsKey("nickname") ? UserInfo.get("nickname") : "";

		if (!UserInfoMngr.existCnkiUserName(userName)) {
			if (!UserInfoMngr.insertCnkiUser(userName, name, sex, birthday, unitname, email, mobile, major, imageurl,
					nickname)) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
			}
		} else {
			if (!UserInfoMngr.updateCnkiUser(userName, name, sex, birthday, unitname, email, mobile, major, imageurl,
					nickname)) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
			}
		}
		return "{\"result\":true}";
	}

	private String getCnkiUserInfo(String userName) {
		List<Map<String, Object>> lst = UserInfoMngr.getUserInfo(userName);
		JSONObject json = new JSONObject();
		if (lst == null || lst.size() == 0) {
			json.put("result", true);
			json.put("count", 0);
		} else {
			json.put("result", true);
			json.put("count", 1);
			json.put("name", lst.get(0).get("name"));
			json.put("sex", lst.get(0).get("sex"));
			json.put("birthday", lst.get(0).get("birthday"));
			json.put("unitname", lst.get(0).get("unitname"));
			json.put("email", lst.get(0).get("email"));
			json.put("mobile", lst.get(0).get("mobile"));
			json.put("major", lst.get(0).get("major"));
			json.put("imageurl", lst.get(0).get("imageurl"));
			json.put("nickname", lst.get(0).get("nickname"));
		}
		return json.toString();
	}

	/**
	 * 根据设备号，修改信息用户信息。如果一个用户在一个设备上登录了，就会绑定到该设备，设备号唯一。
	 * 但是发送消息的时候是通过用户名去找设备，可能存在多个设备。
	 * 
	 * @param userName
	 * @param UserInfo
	 * @return
	 */
	private String setUserDevice(String userName, Map<String, String> UserInfo) {
		String devicenum = UserInfo.containsKey("devicenum") ? UserInfo.get("devicenum") : "";
		String devicetoken = UserInfo.containsKey("devicetoken") ? UserInfo.get("devicetoken") : "";
		String manu = UserInfo.containsKey("manu") ? UserInfo.get("manu") : "iphone";
		String brand = "";
		if (manu.equals("huawei") || manu.equals("xiaomi") || manu.equals("iphone")) {
			brand = manu;
		} else {
			brand = manu;
			manu = "other";
		}
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(devicenum) || Common.IsNullOrEmpty(devicetoken)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (!UserInfoMngr.existUserDevice(devicenum)) {
			if (!UserInfoMngr.insertUserDevice(userName, devicenum, devicetoken, manu, brand)) {
				return "{\"result\":false}";
			}
		} else {
			if (!UserInfoMngr.updateUserDevice(userName, devicenum, devicetoken, manu, brand)) {
				return "{\"result\":false}";
			}
		}
		return "{\"result\":true}";
	}

	/**
	 * 绑定用户名
	 * 
	 * @param UserInfo
	 * @return
	 */
	private String bindUser(String userName, Map<String, String> UserInfo) {
		String strOrgName = UserInfo.get("orgname"); // 如果
		String strOrgPwd = UserInfo.get("orgpwd");
		Integer operateType = UserInfo.get("operatetype") == null ? 1 : Integer.parseInt(UserInfo.get("operatetype"));
		String strIP = UserInfo.get("ip");
		String strRet = "{\"result\":true}";
		boolean bResult = false;
		CnkiMngr cnkiMngr = new CnkiMngr();
		int[] iResult = new int[2];
		Holder<Integer> errorCode = new Holder<Integer>();
		if (strOrgName == null || strOrgName.length() == 0) {
			bResult = cnkiMngr.cnkiUserLogin(strIP, errorCode);
		} else {
			bResult = cnkiMngr.cnkiUserLogin(strOrgName, strOrgPwd, strIP, iResult);
		}
		if (!bResult) {
			return "{\"result\":false,\"message\":\" org login failure\",\"errorcode\":" + iResult[0] + "}";
		}
		if (bResult && MobileRightStatus.getMobileRight()) {
			Holder<Boolean> hmr = new Holder<Boolean>();
			if (!cnkiMngr.haveMobileRight(hmr, errorCode)) {
				return "{\"result\":false,\"message\":\" have no MobileRight\",\"errorcode\":" + errorCode.value
						+ ",\"errcode\":" + errorCode.value + "}";
			}
		}
		if (!cnkiMngr.bindUser(userName, false, operateType, strOrgName, null)) {
			strRet = "{\"result\":false,\"message\":\" bindUser failure\",\"errorcode\":"
					+ SysConfigMngr.ERROR_CODE.ERROR_BINDPHONE.code + ",\"errcode\":"
					+ SysConfigMngr.ERROR_CODE.ERROR_BINDPHONE.code + "}";
		}
		return strRet;
	}

	private String getHuodongUser(String time) {
		// int hdcount = UserInfoMngr.getHuoDongCount();
		List<Map<String, Object>> lstActiveUser = UserInfoMngr.getHuodongUser(time);
		JSONObject result = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		if (lstActiveUser != null || lstActiveUser.size() > 0) {
			for (Map<String, Object> map : lstActiveUser) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("curtime", map.get("curtime"));
				jsonObj.put("time", map.get("time"));
				jsonObj.put("username", map.get("username"));
				jsonObj.put("name", map.get("name"));
				jsonObj.put("mobile", map.get("mobile"));
				jsonArray.add(jsonObj);
			}
			result.put("result", true);
			result.put("count", jsonArray.size());
			result.put("data", jsonArray.toString());
		} else {
			result.put("result", true);
			result.put("count", 0);
		}
		return result.toString();

	}

	private String addUserSet(String userName, Map<String, String> UserInfo) {
		String key = UserInfo.containsKey("key") ? UserInfo.get("key") : "";
		String content = UserInfo.containsKey("content") ? UserInfo.get("content") : "";
		if (Common.IsNullOrEmpty(key)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		if (!UserInfoMngr.existUserSet(userName, key)) {
			if (UserInfoMngr.addUserSet(userName, key, content)) {
				return "{\"result\":true}";
			}
		} else {
			if (UserInfoMngr.updateUserSet(userName, key, content)) {
				return "{\"result\":true}";
			}
		}
		return "{\"result\":false}";
	}

	private String getUserSet(String userName, Map<String, String> UserInfo) {
		String key = UserInfo.containsKey("key") ? UserInfo.get("key") : "";
		if (Common.IsNullOrEmpty(key)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String ret = UserInfoMngr.getUserSet(userName, key);
		return "{\"result\":true,\"data\":\"" + ret + "\"}";

	}

	/**
	 * 第三方标识登录--》记录登录信息
	 * 
	 * @param UserInfo
	 * @return
	 */
	private String ecpLogin(Map<String, String> UserInfo) {
		String thirdUserId = UserInfo.containsKey("thirduserid") ? UserInfo.get("thirduserid") : "";
		String thirdName = UserInfo.containsKey("thirdname") ? UserInfo.get("thirdname") : "";
		String ip = UserInfo.get("ip");
		if (Common.IsNullOrEmpty(thirdUserId) || Common.IsNullOrEmpty(thirdName)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		return UserInfoMngr.mcnkiThirdLogin(new UserInfoBean(UserInfo), thirdUserId, thirdName, ip);
	}

	private String secEcpLogin(Map<String, String> UserInfo) {
		String thirdUserId = UserInfo.containsKey("thirduserid") ? UserInfo.get("thirduserid") : "";
		String unionid = UserInfo.containsKey("unionid") ? UserInfo.get("unionid") : "";
		String thirdName = UserInfo.containsKey("thirdname") ? UserInfo.get("thirdname") : "";
		String ip = UserInfo.get("ip");
		if (Common.IsNullOrEmpty(thirdUserId) || Common.IsNullOrEmpty(thirdName)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		JSONObject json = UserInfoMngr.ecpThirdLogin(thirdUserId, thirdName, ip);

		if (Common.IsNullOrEmpty(unionid)) {
			return "";
		}
		return "";

	}

	// "isExist": true,
	// "Success": true,
	// "Code": 0,
	// "Message": "string",
	// "IdenId": "string",
	// "Username": "string"

	/**
	 * 关联用户名和密码
	 * 
	 * @param UserInfo
	 * @return
	 */
	private String ecpBindUser(Map<String, String> UserInfo) {
		String userName = UserInfo.containsKey("username") ? UserInfo.get("username") : "";
		String password = UserInfo.containsKey("password") ? UserInfo.get("password") : "";
		String thirdUserId = UserInfo.containsKey("thirduserid") ? UserInfo.get("thirduserid") : "";
		String thirdName = UserInfo.containsKey("thirdname") ? UserInfo.get("thirdname") : "";
		String clientType = UserInfo.containsKey("clienttype") ? UserInfo.get("clienttype") : "";
		String ip = UserInfo.get("ip");
		if (Common.IsNullOrEmpty(userName) || Common.IsNullOrEmpty(password) || Common.IsNullOrEmpty(thirdUserId)
				|| Common.IsNullOrEmpty(clientType)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		JSONObject json = UserInfoMngr.ecpLogin(userName, password, ip);
		if (json == null) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("}");
		}
		if (json.getBoolean("Success")) {
			JSONObject jsonBind = UserInfoMngr.ecpBindUser(thirdUserId, userName, thirdName, clientType, ip);
			if (jsonBind.getBoolean("Success")) {
				UserInfoMngr.addThirdUser(thirdUserId, thirdName, userName);
				return "{\"result\":true}";
			}
			return "{\"result\":false,\"message\":\"" + json.getString("Message") + "\"}"; // 缺少错误码信息
		} else {
			return "{\"result\":false,\"errorcode\":\"" + json.getString("Code") + "\",\"message\":\""
					+ json.getString("Message") + "\"}";
		}
	}

	/**
	 * 第三方直接登录，需要先创建用户，然后绑定 第三方用户先拿第一个接口登录，然后失败后，开始提示界面，绑定/直接登录，
	 * 
	 * @param UserInfo
	 * @return
	 */
	private String thirdLogin(Map<String, String> UserInfo) {
		String thirdUserId = UserInfo.containsKey("thirduserid") ? UserInfo.get("thirduserid") : "";
		String thirdName = UserInfo.containsKey("thirdname") ? UserInfo.get("thirdname") : "";
		String clientType = UserInfo.containsKey("clienttype") ? UserInfo.get("clienttype") : "";
		String strAppid = UserInfo.get("appid");
		String ip = UserInfo.get("ip");
		Holder<String> errmsg = new Holder<String>();
		String userName = "cnki_" + thirdName + "_" + Long.toHexString(System.currentTimeMillis());

		if (UserMngr.IsUserExist(userName)) {
			userName = "cnki_" + thirdName + "_" + Long.toHexString(System.currentTimeMillis());
		}

		String password = Common.getStringRandom(8);
		if (!UserMngr.CreateUser(Constant.PLATFORM, userName, password, userName + "@cnki.net", errmsg)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("}");
		} else {
			UserInfoBean userInfo = new UserInfoBean();
			userInfo.setUserName(userName);
			userInfo.setPassWord(password);
			userInfo.setEMail(userName + "@cnki.net");
			userInfo.setAppid(strAppid);
			userInfo.setAddress(ip);
			userInfo.setMobile("");
			UserInfoMngr.UserRegister(userInfo);
		}
		JSONObject jsonBind = UserInfoMngr.ecpBindUser(thirdUserId, userName, thirdName, clientType, ip);
		if (jsonBind.getBoolean("Success")) {
			UserInfoMngr.addThirdUser(thirdUserId, thirdName, userName);
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("}");
		}
	}

	private String visitorLogin(Map<String, String> UserInfo) {
		String uid = UserInfo.containsKey("uid") ? UserInfo.get("uid") : "";
		String autocreate = UserInfo.containsKey("autocreate") ? UserInfo.get("autocreate") : "";
		String strAppid = UserInfo.get("appid");
		String ip = UserInfo.get("ip");
		if (Common.IsNullOrEmpty(uid) || Common.IsNullOrEmpty(autocreate)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String username = UserInfoMngr.getVisitorUser(uid);
		if ("0".equals(autocreate)) {
			if (Common.IsNullOrEmpty(username)) {
				return "{\"result\":true,\"usertoken\":\"\",\"username\":\"\"}";
			}
		}
		String password = "";
		if (Common.IsNullOrEmpty(username)) {
			Holder<String> errmsg = new Holder<String>();
			username = "cnki_visitor_" + Long.toHexString(System.currentTimeMillis());

			if (UserMngr.IsUserExist(username)) {
				username = "cnki_visitor_" + Long.toHexString(System.currentTimeMillis());
			}

			password = Common.getStringRandom(8);
			if (!UserMngr.CreateUser(Constant.PLATFORM, username, password, username + "@cnki.net", errmsg)) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("}");
			} else {
				UserInfoBean userInfo = new UserInfoBean();
				userInfo.setUserName(username);
				userInfo.setPassWord(password);
				userInfo.setEMail(username + "@cnki.net");
				userInfo.setAppid(strAppid);
				userInfo.setAddress(ip);
				userInfo.setMobile("");
				UserInfoMngr.UserRegister(userInfo);
				UserInfoMngr.addVisitorUser(uid, username);
			}
		}
		return UserInfoMngr.visitorLogin(username, password, strAppid, "iOS-iPhone8,4-9.3.2", "1.1.4", uid, ip);
	}

	/**
	 * 手机号注册完，把手机号绑定到该手机号
	 * 
	 * @param phoneNum
	 * @param password
	 * @param ip
	 * @return
	 */
	private boolean manualBindPhone(String userName, String phoneNum, String password, String ip) {
		CnkiMngr cnkiMngr = new CnkiMngr();
		boolean bResult = false;
		int[] iResult = new int[2];
		bResult = cnkiMngr.cnkiUserLogin(userName, password, ip, iResult);
		if (bResult) {
			int timeStamp = 60;
			Holder<String> validCode = new Holder<String>();
			Holder<Integer> errorCode = new Holder<Integer>();
			Holder<Integer> symbolValidCodeResult = new Holder<Integer>();
			if (cnkiMngr.getSymbolValidCode(phoneNum, timeStamp, 6, validCode, symbolValidCodeResult, errorCode)) {
				if (!CnkiMngr.setSymbolName(cnkiMngr.getIdentId(), phoneNum, validCode.value, errorCode)) {
					logger.error(
							"手机号：" + phoneNum + "绑定失败,错误号为：" + errorCode.value + "/" + symbolValidCodeResult.value);
					return false;
				}
			} else {
				logger.error("手机号：" + phoneNum + "绑定失败,获取Symbol Code失败，错误码为:" + errorCode.value + "/"
						+ symbolValidCodeResult.value);
				return false;
			}
		} else {
			logger.error("手机号：" + phoneNum + "绑定失败,用户登录失败，用户名为：" + phoneNum + "，用户密码为：" + password + "，ip地址为：" + ip
					+ "，错误码为:" + iResult[1]);
			return false;
		}
		return true;
	}

	/**
	 * 初始化机构信息，适用于多个机构的情况
	 * 
	 * @param ip
	 * @param orgName
	 * @param orgPassword
	 * @return
	 */
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

	/**
	 * 初始化机构信息，适用于多个机构的情况
	 * 
	 * @param ip
	 * @param orgName
	 * @param orgPassword
	 * @return
	 */
	private List<Map<String, String>> initOrgListNew(String ip, String orgName, String orgPassword) {
		List<Map<String, String>> orgList = new ArrayList<Map<String, String>>();
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
		if (orgList.size() == 0) {
			Map<String, String> orgIpMap = new HashMap<String, String>();
			orgIpMap.put("type", "ip");
			orgIpMap.put("ip", ip);
			orgList.add(orgIpMap);
		}
		return orgList;
	}

	private String sugUserName(Map<String, String> mapInfo) {
		String keyword = mapInfo.containsKey("keyword") ? mapInfo.get("keyword") : "";
		JSONObject rest = new JSONObject();
		if (Common.IsNullOrEmpty(keyword)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		List<Map<String, Object>> lstUserName = UserInfoMngr.sugUserName(keyword);
		JSONArray jsonArray = new JSONArray();
		if (lstUserName != null && lstUserName.size() > 0) {
			for (Map<String, Object> map : lstUserName) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("username", map.get("username"));
				jsonArray.add(jsonObj);
			}
			rest.put("result", true);
			rest.put("count", jsonArray.size());
			rest.put("data", jsonArray.toString());
		} else {
			rest.put("result", true);
			rest.put("count", 0);
		}
		return rest.toString();
	}

	private String qrcode() {
		JSONObject json = new JSONObject();
		StringBuilder sbqrcode = new StringBuilder();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		long lcurtime = System.currentTimeMillis();
		String qrcode = sbqrcode.append("cnkiexpress@").append(uuid).append("@").append(lcurtime).toString();
		if (!QrcodeMngr.addQrcode(qrcode)) {
			json.put("result", false);
			json.put("message", "server error");
			json.put("errcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
			return json.toString();
		}
		json.put("result", true);
		json.put("qrcode", Common.encodeAES(qrcode, password));
		return json.toString();
	}

	private String chkqrcode(Map<String, String> mapInfo) {
		JSONObject json = new JSONObject();
		String qrcode = mapInfo.containsKey("qrcode") ? mapInfo.get("qrcode") : "";
		if (Common.IsNullOrEmpty(qrcode)) {
			json.put("result", false);
			json.put("message", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			json.put("errcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			return json.toString();
		}
		String deqrcode = null;
		try{
			deqrcode = Common.decodeAES(qrcode, password);
		}catch(Exception e){
		
		}
		int vfcode = verifyQrCode(deqrcode);
		if (vfcode == -2) {
			json.put("result", false);
			json.put("message", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			json.put("errcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			return json.toString();
		} else if (vfcode == -1) {
			json.put("result", true);
			json.put("validcode", false);
			return json.toString();
		}
		json.put("result", true);
		json.put("validcode", true);
		return json.toString();
	}

	private String scanlogin(String userName, Map<String, String> mapInfo) {
		JSONObject json = new JSONObject();
		String qrcode = mapInfo.containsKey("qrcode") ? mapInfo.get("qrcode") : "";
		if (Common.IsNullOrEmpty(qrcode)) {
			json.put("result", false);
			json.put("message", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			json.put("errcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			return json.toString();
		}
		String deqrcode = Common.decodeAES(qrcode, password);
		int vfcode = verifyQrCode(deqrcode);
		if (vfcode == -2) {
			json.put("result", false);
			json.put("message", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			json.put("errcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			return json.toString();
		} else if (vfcode == -1) {
			json.put("result", true);
			json.put("validcode", false);
			return json.toString();
		}
		if (!QrcodeMngr.updateQrcode(deqrcode, userName)) {
			json.put("result", false);
			json.put("message", "server error");
			json.put("errcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
			return json.toString();
		}
		json.put("result", true);
		json.put("islogin", true);
		return json.toString();
	}

	/**
	 * 验证是否已经登录
	 * 
	 * @param mapInfo
	 * @return
	 */
	private String qrcodelogin(Map<String, String> mapInfo) {
		JSONObject json = new JSONObject();
		String qrcode = mapInfo.containsKey("qrcode") ? mapInfo.get("qrcode") : "";
		String clientID = mapInfo.containsKey("clientid") ? mapInfo.get("clientid") : "";
		String appid = mapInfo.get("appid");
		String ip = mapInfo.get("ip");
		String platForm = mapInfo.containsKey("platform") ? mapInfo.get("platform") : "";
		String version = mapInfo.containsKey("version") ? mapInfo.get("version") : "";
		String baseOsName = "window";
		if (Common.IsNullOrEmpty(qrcode)) {
			json.put("result", false);
			json.put("message", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			json.put("errcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			return json.toString();
		}
		String deqrcode = Common.decodeAES(qrcode, password);
		int vfcode = verifyQrCodeExcludeUser(deqrcode);
		if (vfcode == -2) {
			json.put("result", false);
			json.put("message", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			json.put("errcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code));
			return json.toString();
		} else if (vfcode == -1) {
			json.put("result", true);
			json.put("validcode", false);
			return json.toString();
		}
		
		List<Map<String, Object>> lst = QrcodeMngr.getQrcode(deqrcode);
		if(null == lst || lst.size() == 0) {
			json.put("result", true);
			json.put("validcode", false);
			return json.toString();
		}
		
		String userName = (String)lst.get(0).get("username");
		if (Common.IsNullOrEmpty(userName)) {
			json.put("result", true);
			json.put("validcode", true);
			json.put("islogin", false);
			return json.toString();
		}
		String token = UserInfoMngr.qrcodeLogin(userName, clientID, ip, platForm, "", version, baseOsName, appid);
		if (token.startsWith("@-")) {
			return "{\"result\":false,\"message\":\"".concat(token.substring(1)).concat("\",\"errorcode\":")
					.concat(token.substring(1)).concat("}");
		} else {
			return "{\"result\":true,\"islogin\":true,\"usertoken\":\"".concat(token).concat("\",\"username\":\"").concat(userName)
					.concat("\"}");
		}

	}

	/**
	 * 
	 * @param deqrcode
	 * @return
	 */
	private int verifyQrCode(String deqrcode) {
		if(Common.IsNullOrEmpty(deqrcode))
			return -2;
		String[] arrqrcode = deqrcode.split("@");
		int ret = 1;
		if (!deqrcode.contains("@") || arrqrcode.length != 3) {
			ret = -2;
		}
		if (!"cnkiexpress".equals(arrqrcode[0])) {
			ret = -2;
		}
		long qtime = 0;
		try {
			qtime = Long.parseLong(arrqrcode[2]);
		} catch (Exception e) {
			ret = -2;
		}
		long curTime = System.currentTimeMillis();
		boolean vaild = QrcodeMngr.chkQrcode(deqrcode);
		if ((curTime - qtime) > 10 * 60 * 1000 || !vaild) {
			ret = -1;
		}
		return ret;
	}
	
	private int verifyQrCodeExcludeUser(String deqrcode) {
		String[] arrqrcode = deqrcode.split("@");
		int ret = 1;
		if (!deqrcode.contains("@") || arrqrcode.length != 3) {
			ret = -2;
		}
		if (!"cnkiexpress".equals(arrqrcode[0])) {
			ret = -2;
		}
		long qtime = 0;
		try {
			qtime = Long.parseLong(arrqrcode[2]);
		} catch (Exception e) {
			ret = -2;
		}
		long curTime = System.currentTimeMillis();
		if ((curTime - qtime) > 10 * 60 * 1000) {
			ret = -1;
		}
		return ret;
	}
}
