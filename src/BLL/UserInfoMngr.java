package BLL;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Holder;

import BLL.Logger.BaseOsNames;
import DAL.DBHelper;
import Model.HttpContext;
import Model.UserInfoBean;
import Model.UserLoginBean;
import Util.Common;
import net.cnki.sso.UserStruct;
import net.sf.json.JSONObject;

public class UserInfoMngr {

	private static String TokenPassWord = "@a3k9#-;jdiu$98JH-03H~kpb59akj8j";

	public static void UserLogin(UserInfoBean UserInfo, String[] arrResult) {

		String strUserName = UserInfo.getUserName();
		String strPassWord = UserInfo.getPassWord();
		String strPlatForm = UserInfo.getPlatForm();
		String strIP = UserInfo.getAddress();
		String appid = UserInfo.getAppid();

		// LoggerFile.appendMethod("E:\\Login", "appid:" + appid + ";userName:"
		// + strUserName + ";IP:" + strIP);
		int iTimes = 3;
		String strTimes = Common.GetConfig("AuthConnectionTimes");
		if (strTimes != null) {
			iTimes = Integer.valueOf(strTimes);
		}
		boolean bResult = false;
		String realUserName = "";
		if (AppInfoMngr.isAuthUser(appid)) {
			CnkiMngr cnkiMngr = new CnkiMngr();
			int[] iResult = new int[2];
			for (int i = 0; i < iTimes; i++) {
				bResult = cnkiMngr.cnkiUserLogin(strUserName, strPassWord, strIP, iResult);
				if (bResult)
					break;
			}
			if (!bResult) {
				arrResult[0] = "@-".concat(String.valueOf(Math.abs(iResult[0])));
				// LoggerFile.appendMethod("E:\\Login", "login failed;userName:"
				// + strUserName + "errcode:" + arrResult[0]);
				return;
			}
			// LoggerFile.appendMethod("E:\\Login", "login success;userName:" +
			// strUserName + "errcode:" + arrResult[0]);
			String strSpUser = Common.GetConfig("SpecialUser");
			/*
			 * if (!strUserName.toLowerCase().equals(strSpUser)) { if (0 ==
			 * iResult[1]) { arrResult[0] =
			 * "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.
			 * ERROR_ORGLOGIN. code)); } } else { Holder<Integer> errorCode =
			 * new Holder<Integer>(); UserStruct us =
			 * cnkiMngr.getUserInfo(errorCode); arrResult[1] =
			 * us.getBaseInfo().getValue().getUserName().getValue(); Map<String,
			 * Object> mapInfo =
			 * SysConfigMngr.getConfigValueAndTime("specialuser"); if (mapInfo
			 * == null || !"1".equals(String.valueOf(mapInfo.get("value")))) {
			 * if (0 == iResult[1]) { arrResult[0] =
			 * "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.
			 * ERROR_ORGLOGIN. code)); } } }
			 */
			if (!strUserName.toLowerCase().equals(strSpUser)) {
				if (0 == iResult[1]) {
					arrResult[0] = "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ORGLOGIN.code));
					return;
				}
			}

			if (!strUserName.equalsIgnoreCase("appletest2013")) {
				Holder<Integer> errorCode = new Holder<Integer>();
				UserStruct us = cnkiMngr.getUserInfo(errorCode);
				realUserName = us.getBaseInfo().getValue().getUserName().getValue();
				arrResult[1] = realUserName;
				UserInfo.setUserName(realUserName);
				UserInfo.setMobile(us.getComInfo().getValue().getMobile().getValue());
			} else {
				realUserName = "appletest2013";
				arrResult[1] = realUserName;
				UserInfo.setMobile("");
			}
		}
		String[] arrUserToken = new String[] { "" };
		if (!strUserName.equalsIgnoreCase("appletest2013")) {
			if (!checkOnlineCount(appid, realUserName, strPlatForm, UserInfo.getClientID(), strIP, arrUserToken)) {
				arrResult[0] = "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLIENTOVERFULL.code));
				return;
			}
		}
		String strUserToken = "";
		boolean bOnline = !Common.IsNullOrEmpty(arrUserToken[0]);
		if (bOnline) {
			strUserToken = arrUserToken[0];
		} else {
			strUserToken = CreateUserToken(realUserName);
		}
		if (!setUserInfo(UserInfo, strUserToken, bOnline, 0)) {
			arrResult[0] = "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
			return;
		}
		arrResult[0] = strUserToken;

		// if (!setOnLineInfo(UserInfo, strUserToken)) {
		// return
		// "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		// }
		//
		// if (!setUserLoginInfo(strUserName)) {
		// return
		// "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		// }

		// if (!updateLoginCount(strUserName)) {
		// return
		// "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		// }

		HttpServletRequest request = HttpContext.GetRequest();
		if (Common.IsNullOrEmpty(strPlatForm)) {
			strPlatForm = request.getHeader("User-Agent");
		}
		if (!Common.IsNullOrEmpty(strPlatForm)) {
			Logger.setOperatorSystem(strPlatForm);
		}
		// return strUserToken;
	}

	public static String UserLogin(String Token) {
		List<Map<String, Object>> lstUserInfo = null;
		HttpServletRequest request = HttpContext.GetRequest();
		String strClientAddr = Common.getClientIP(request);
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUserInfo = dbHelper.ExecuteQuery("username", "useronline",
					"token='".concat(dbHelper.FilterSpecialCharacter(Token)).concat("'"));
			/*
			 * if (lstUserInfo != null) { dbHelper.Update("useronline",
			 * "token='".concat(dbHelper.FilterSpecialCharacter(Token)).concat(
			 * "'"), new String[] { "address", "lasttime" }, new Object[] {
			 * strClientAddr, Common.GetDateTime() }); }
			 */
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		}
		if (lstUserInfo == null) {
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code));
		}
		String strUserName = lstUserInfo.get(0).get("username").toString();
		lstUserInfo = null;
		return strUserName;
	}

	public static String UserLogin(String ip, String Token) {
		List<Map<String, Object>> lstUserInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUserInfo = dbHelper.ExecuteQuery("username", "useronline",
					"token='".concat(dbHelper.FilterSpecialCharacter(Token)).concat("'"));
			/*
			 * if (lstUserInfo != null) { dbHelper.Update("useronline",
			 * "token='".concat(dbHelper.FilterSpecialCharacter(Token)).concat(
			 * "'"), new String[] { "address", "lasttime" }, new Object[] { ip,
			 * Common.GetDateTime() }); }
			 */
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		}
		if (lstUserInfo == null) {
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code));
		}
		String strUserName = lstUserInfo.get(0).get("username").toString();
		lstUserInfo = null;
		return strUserName;
	}

	public static boolean UserLogout(String Token) {
		boolean bolRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("useronline", "token='".concat(Token).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		return bolRet;
	}

	public static boolean UserRegister(UserInfoBean userInfo) {
		HttpServletRequest request = HttpContext.GetRequest();
		String platForm = request.getHeader("User-Agent") == null ? "" : request.getHeader("User-Agent");
		boolean bolRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("userregister",
					new String[] { "username", "password", "email", "address", "platform", "appid", "time", "mobile" },
					new Object[] { userInfo.getUserName(), Common.EncryptData(userInfo.getPassWord(), TokenPassWord),
							userInfo.getEMail(), Common.ipToLong(userInfo.getAddress()), platForm, userInfo.getAppid(),
							Common.GetDateTime(), userInfo.getMobile() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/**
	 * 
	 * @param userName
	 * @param password
	 * @param mobile
	 * @param platForm
	 *            ios或者安卓
	 * @param appId
	 * @param sign
	 * @return
	 */
	public static boolean addUserInfo(String userName, String password, String mobile, String platForm, String appId,
			String sign) {
		String baseOsName = "other";
		BaseOsNames[] baseOsNames = BaseOsNames.values();
		for (BaseOsNames base : baseOsNames) {
			int index = platForm.toLowerCase().indexOf(base.value.toLowerCase());
			if (index >= 0) {
				baseOsName = base.value;
				break;
			}
		}
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			String time = Common.GetDateTime();
			if (isExistUser(userName, appId)) {
				dbHelper.Update("userinfo", "username='".concat(dbHelper.FilterSpecialCharacter(userName)),
						new String[] { "password", "updatetime" },
						new Object[] { Common.EncryptData(password, TokenPassWord), time });
			} else {
				dbHelper.Insert("userinfo",
						new String[] { "username", "password", "mobile", "client", "appid", "updatetime", "time",
								"thduser" },
						new Object[] { userName, Common.EncryptData(password, TokenPassWord), mobile, baseOsName, appId,
								time, time, sign });
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static int getOnlineUserCount(String AppID, String UserName) {
		int iCount = 0;
		String strTabName = "";
		try {// select count(*) from (select username,count(username) from
				// useronline where username='ttod' GROUP BY username) tab1
			DBHelper dbHelper = DBHelper.GetInstance();
			StringBuilder sbCondition = new StringBuilder();
			if (!Common.IsNullOrEmpty(AppID)) {
				sbCondition.append("appid='".concat(AppID).concat("' and "));
			}
			if (!Common.IsNullOrEmpty(UserName)) {
				sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName))
						.append("%' and ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
				strTabName = "(select username,count(username) from useronline where ".concat(sbCondition.toString())
						.concat(" GROUP BY username) tab1");
			} else {
				strTabName = "(select username,count(username) from useronline GROUP BY username) tab1";
			}
			iCount = dbHelper.GetCount(strTabName, "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	// public static List<Map<String, Object>> getOnlineUserList(String
	// UserName, int Start, int Length) {
	// List<Map<String, Object>> lstOnlineUser = null;
	// String strTabName = "";
	// try {
	// DBHelper dbHelper = DBHelper.GetInstance();
	// if (UserName != null && UserName.length() > 0) {
	// strTabName =
	// "(select username,address,client,lasttime,count(username) as usercount
	// from useronline where username like
	// '%".concat(dbHelper.FilterSpecialCharacter(UserName)).concat("%' GROUP BY
	// username) tab1");
	// } else {
	// strTabName =
	// "(select username,address,client,lasttime,count(username) as usercount
	// from useronline GROUP BY username) tab1";
	// }
	// lstOnlineUser =
	// dbHelper.ExecuteQuery("username,address,client,lasttime,usercount",
	// strTabName, "", "lasttime desc", Start, Length);
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// }
	// return lstOnlineUser;
	// }

	public static List<Map<String, Object>> getOnlineUserNameList(String AppID, String UserName, int Start,
			int Length) {
		List<Map<String, Object>> lstOnlineUserName = null;
		StringBuilder sbCondition = new StringBuilder();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(AppID)) {
				sbCondition.append("appid='".concat(AppID).concat("' and "));
			}
			if (!Common.IsNullOrEmpty(UserName)) {
				sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName))
						.append("%' and ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
				lstOnlineUserName = dbHelper.ExecuteQuery("DISTINCT username", "(select username from useronline where "
						.concat(sbCondition.toString()).concat(" ORDER BY id desc)tab1"), "", "", Start, Length);
			} else {
				lstOnlineUserName = dbHelper.ExecuteQuery("DISTINCT username",
						"(select username from useronline ORDER BY id desc)tab1", "", "", Start, Length);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOnlineUserName;
	}

	public static List<Map<String, Object>> getOnlineUserInfoList(String UserName) {
		List<Map<String, Object>> lstOnlineUserInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOnlineUserInfo = dbHelper.ExecuteQuery("username,address,client,appid,time", "useronline",
					"username in(".concat(UserName).concat(") group by username"), "time desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOnlineUserInfo;
	}

	public static List<Map<String, Object>> getOnlineUserLoginCount(String UserName) {
		List<Map<String, Object>> lstOnlineUserLoginCount = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOnlineUserLoginCount = dbHelper.ExecuteQuery(
					"select username,count(username) as onlinecount from (select username from useronline where username in("
							.concat(UserName).concat(")) tt GROUP BY username"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOnlineUserLoginCount;
	}

	public static List<Map<String, Object>> getOnlineUserDetail(String UserName) {
		List<Map<String, Object>> lstOnlineUser = null;
		StringBuilder sbCondition = new StringBuilder();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (UserName != null && UserName.length() > 0) {
				sbCondition.append(" username ='").append(dbHelper.FilterSpecialCharacter(UserName)).append("'");
			}
			lstOnlineUser = dbHelper.ExecuteQuery("id,username,address,client,version,appid,lasttime,time",
					"useronline", sbCondition.toString(), "lasttime desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOnlineUser;
	}

	public static boolean checkOnlineUser() {
		boolean bolRet = false;
		int iDay = 7;
		String strKeepUserDays = Common.GetConfig("UserOnlineDays");
		if (strKeepUserDays != null) {
			iDay = Integer.valueOf(strKeepUserDays);
		}
		DBHelper dbHelper = null;
		String strTime = Common.GetDateTime(-iDay * 86400000);
		try {
			dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("useronline", "lasttime < '".concat(strTime).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean delOnlineUser(String UserID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.ExecuteSql("DELETE  from useronline where id in(".concat(UserID).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	// /�û���Ϣ�����
	/*
	 * 是否存在用户
	 */
	private static boolean isExistUser(String UserName, String AppID) {
		StringBuilder sbCondition = new StringBuilder();
		sbCondition.append("username='").append(UserName).append("'");
		// sbCondition.append("and appid='").append(AppID).append("'");
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("userinfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		} finally {
			sbCondition = null;
			dbHelper = null;
		}
		if (iCount == 0) {
			return false;
		}
		return true;
	}

	public static UserLoginBean sysUserLogin(String UserName, String PassWord) {
		UserLoginBean userInfo = null;
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("id,username,password,role,appid", "sysuserinfo",
					"username='".concat(dbHelper.FilterSpecialCharacter(UserName)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return userInfo;
		}
		String strPassWord = (String) lstInfo.get(0).get("password");
		if (Common.EnCodeMD5(Common.EncryptData(PassWord, TokenPassWord)).equals(strPassWord)) {
			userInfo = new UserLoginBean(lstInfo.get(0));
		}

		return userInfo;
	}

	public static boolean updateSysUserPassWord(String OldUserName, String OldPassWord, String NewUserName,
			String NewPassWord) {
		boolean bolRet = false;
		if (sysUserLogin(OldUserName, OldPassWord) == null) {
			return bolRet;
		}
		String strPassword = Common.EnCodeMD5(Common.EncryptData(NewPassWord, TokenPassWord));
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update("sysuserinfo",
					"username='".concat(dbHelper.FilterSpecialCharacter(OldUserName)).concat("'"),
					new String[] { "username", "password" }, new Object[] { NewUserName, strPassword });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static int getSysUserCount() {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("sysuserinfo", "username !='root'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getSysUserList() {
		List<Map<String, Object>> lstUser = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstUser = dbHelper.ExecuteQuery("id,role,username", "sysuserinfo");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstUser;
	}

	public static boolean existsSysUser(String UserName) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("sysuserinfo", "username='".concat(UserName).concat("'")) > 0) {
				bResult = true;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean addSysUserInfo(UserLoginBean UserBean) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			return dbHelper.Insert("sysuserinfo",
					new String[] { "username", "password", "role", "appid", "comment", "time" },
					new String[] { UserBean.getUserName(),
							Common.EnCodeMD5(Common.EncryptData(UserBean.getPassword(), TokenPassWord)),
							String.valueOf(UserBean.getRole()), UserBean.getAppid(), UserBean.getComment(),
							Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean updateSysUserInfo(UserLoginBean UserBean, boolean isPwdChanged) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (isPwdChanged) {
				return dbHelper.Update("sysuserinfo", "id='".concat(String.valueOf(UserBean.getId())).concat("'"),
						new String[] { "password", "role", "appid", "comment", "time" },
						new String[] { Common.EnCodeMD5(Common.EncryptData(UserBean.getPassword(), TokenPassWord)),
								String.valueOf(UserBean.getRole()), UserBean.getAppid(), UserBean.getComment(),
								Common.GetDateTime() });
			} else {
				return dbHelper.Update("sysuserinfo", "id='".concat(String.valueOf(UserBean.getId())).concat("'"),
						new String[] { "role", "appid", "comment", "time" },
						new String[] { String.valueOf(UserBean.getRole()), UserBean.getAppid(), UserBean.getComment(),
								Common.GetDateTime() });
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean delSysUserInfo(String UserID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("sysuserinfo",
					"id='".concat(dbHelper.FilterSpecialCharacter(UserID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static UserLoginBean getSysUserInfo(String UserID) {
		UserLoginBean userBean = null;
		List<Map<String, Object>> lstUser = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstUser = dbHelper.ExecuteQuery("id,role,username,password,appid,comment", "sysuserinfo",
					"id='".concat(dbHelper.FilterSpecialCharacter(UserID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstUser != null) {
			userBean = new UserLoginBean(lstUser.get(0));
		}
		return userBean;
	}

	public static boolean equalsSysUserPwd(String OldPassword, String UserName) {
		boolean bResult = false;
		List<Map<String, Object>> lstUser = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstUser = dbHelper.ExecuteQuery("password", "sysuserinfo", "username='".concat(UserName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstUser != null) {
			String tempPwd = String.valueOf(lstUser.get(0).get("password"));
			bResult = tempPwd.equals(Common.EnCodeMD5(Common.EncryptData(OldPassword, TokenPassWord)));
		}
		return bResult;
	}

	public static boolean updateSysUserPwd(String UserName, String Password) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			return dbHelper.Update("sysuserinfo", "username='".concat(UserName).concat("'"),
					new String[] { "password" },
					new String[] { Common.EnCodeMD5(Common.EncryptData(Password, TokenPassWord)) });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/**
	 * 
	 * @param UserInfo
	 * @param UserToken
	 * @param IsOnline
	 * @param sign
	 *            标记是否为第三方账号登录，1为第三方账户，0位普通账户登录
	 * @return
	 */
	private static boolean setUserInfo(UserInfoBean UserInfo, String UserToken, boolean IsOnline, int sign) {
		boolean bolRet = false;
		DBHelper dbHelper = null;
		String strUserName = UserInfo.getUserName();
		String strPassword = UserInfo.getPassWord();
		String strPlatForm = UserInfo.getPlatForm();
		String strClientID = UserInfo.getClientID();
		String strClientAddr = UserInfo.getAddress();
		String mobile = UserInfo.getMobile() == null ? "" : UserInfo.getMobile();
		String appId = UserInfo.getAppid();
		String strVersion = UserInfo.getVersion();
		String longitude = UserInfo.getLongitude();
		String latitude = UserInfo.getLatitude();
		HttpServletRequest request = HttpContext.GetRequest();
		if (Common.IsNullOrEmpty(strPlatForm)) {
			strPlatForm = request.getHeader("User-Agent") == null ? "" : request.getHeader("User-Agent");
		}
		if (Common.IsNullOrEmpty(strClientID)) {
			strClientID = "";
		}
		String baseOsName = "other";
		BaseOsNames[] baseOsNames = BaseOsNames.values();
		for (BaseOsNames base : baseOsNames) {
			int index = strPlatForm.toLowerCase().indexOf(base.value.toLowerCase());
			if (index >= 0) {
				baseOsName = base.value;
				break;
			}
		}
		String strTime = Common.GetDateTime();
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
			return bolRet;
		}
		long time1 = System.currentTimeMillis();
		List<String> lstSql = new ArrayList<String>(3);
		if (isExistUser(strUserName, appId)) {
			lstSql.add("update userinfo set password ='".concat(Common.EncryptData(strPassword, TokenPassWord))
					.concat("',updatetime='").concat(strTime).concat("' where username='")
					.concat(dbHelper.FilterSpecialCharacter(strUserName)) // .concat("'
																			// and
																			// appid='").concat(appId)
					.concat("'"));
		} else {
			// lstSql.add("insert into userinfo
			// (username,password,address,client,clientid,logincount,updatetime,time)
			// values
			// ('".concat(strUserName).concat("','").concat(Common.EncryptData(strPassword,
			// TokenPassWord)).concat("','").concat(strClientAddr).concat("','").concat(strPlatForm).concat("','").concat(strClientID).concat("',1,'").concat(strTime).concat("','").concat(strTime).concat("')"));
			lstSql.add("insert into userinfo (username,password,mobile,client,appid,updatetime,time,thduser) values ('"
					.concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("','")
					.concat(Common.EncryptData(strPassword, TokenPassWord)).concat("','")
					.concat(dbHelper.FilterSpecialCharacter(mobile)).concat("','").concat(baseOsName).concat("','")
					.concat(appId).concat("','").concat(strTime).concat("','").concat(strTime).concat("',")
					.concat(String.valueOf(sign)).concat(")"));
		}
		if (IsOnline) {
			lstSql.add("update useronline set version='".concat(strVersion).concat("',lasttime ='")
					.concat(Common.GetDateTime()).concat("',address='")
					.concat(dbHelper.FilterSpecialCharacter(strClientAddr)).concat("' where token = '")
					.concat(UserToken).concat("'"));
		} else {
			lstSql.add(
					"insert into useronline (username,token,address,client,clientid,version,appid,time,lasttime) values ('"
							.concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("','").concat(UserToken)
							.concat("','").concat(dbHelper.FilterSpecialCharacter(strClientAddr)).concat("','")
							.concat(dbHelper.FilterSpecialCharacter(strPlatForm)).concat("','")
							.concat(dbHelper.FilterSpecialCharacter(strClientID)).concat("','")
							.concat(dbHelper.FilterSpecialCharacter(strVersion)).concat("','").concat(appId)
							.concat("','").concat(strTime).concat("','").concat(strTime).concat("')"));
		}
		// lstSql.add("insert into userlogininfo
		// (userid,username,address,client,clientid,version,time) select
		// id,username,address,client,clientid,'"
		// + strVersion +
		// "',time from userinfo where username
		// ='".concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("'"));
		if (isFirstLogin(strUserName)) {
			lstSql.add(
					"insert into userfirstlogin (username,mobile,address,client,clientid,version,baseos,appid,time,thduser) values ('"
							.concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("','")
							.concat(dbHelper.FilterSpecialCharacter(mobile)).concat("','")
							.concat(dbHelper.FilterSpecialCharacter(strClientAddr)).concat("','")
							.concat(dbHelper.FilterSpecialCharacter(strPlatForm)).concat("','").concat(strClientID)
							.concat("','").concat(dbHelper.FilterSpecialCharacter(strVersion)).concat("','")
							.concat(baseOsName).concat("','").concat(appId).concat("','").concat(strTime).concat("',")
							.concat(String.valueOf(sign)).concat(")"));
		}
		lstSql.add("insert into " + getUserLoginInfoTable()
				+ " (username,mobile,address,client,clientid,version,baseos,appid,time,thduser) values ('"
						.concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("','")
						.concat(dbHelper.FilterSpecialCharacter(mobile)).concat("','")
						.concat(dbHelper.FilterSpecialCharacter(strClientAddr)).concat("','")
						.concat(dbHelper.FilterSpecialCharacter(strPlatForm)).concat("','").concat(strClientID)
						.concat("','").concat(dbHelper.FilterSpecialCharacter(strVersion)).concat("','")
						.concat(baseOsName).concat("','").concat(appId).concat("','").concat(strTime).concat("',")
						.concat(String.valueOf(sign)).concat(")"));
		/*
		 * if (!Common.IsNullOrEmpty(longitude)) { lstSql.add(
		 * "insert into userlbs (username,longitude,latitude,appid,time) values('"
		 * .concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("','")
		 * .concat(dbHelper.FilterSpecialCharacter(longitude)).concat("','")
		 * .concat(dbHelper.FilterSpecialCharacter(latitude)).concat("','").
		 * concat(appId).concat("','") .concat(strTime).concat("')")); }
		 */
		long time2 = System.currentTimeMillis();
		// if (IsOnline) {
		// lstSql.add("update userinfo set password
		// ='".concat(Common.EncryptData(strPassword,
		// TokenPassWord)).concat("',address
		// ='").concat(strClientAddr).concat("',client='").concat(strPlatForm).concat("',clientid='").concat(strClientID).concat("',logincount=
		// (select count(1)+1 from userlogininfo where
		// username='").concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("'),time='").concat(strTime).concat("'
		// where
		// username='").concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("'"));
		// lstSql.add("update useronline set lasttime
		// ='".concat(Common.GetDateTime()).concat("',address='").concat(strClientAddr).concat("'
		// where token = '").concat(UserToken).concat("'"));
		// } else {
		// if (isExistUser(strUserName)) {
		// lstSql.add("update userinfo set password
		// ='".concat(Common.EncryptData(strPassword,
		// TokenPassWord)).concat("',address
		// ='").concat(strClientAddr).concat("',client='").concat(strPlatForm).concat("',clientid='").concat(strClientID).concat("',logincount=
		// (select count(1)+1 from userlogininfo where
		// username='").concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("'),time='").concat(strTime).concat("'
		// where
		// username='").concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("'"));
		// } else {
		// lstSql.add("insert into userinfo
		// (username,password,address,client,clientid,logincount,time) values
		// ('".concat(strUserName).concat("','").concat(Common.EncryptData(strPassword,
		// TokenPassWord)).concat("','").concat(strClientAddr).concat("','").concat(strPlatForm).concat("','").concat(strClientID).concat("',1,'").concat(strTime).concat("')"));
		// }
		// lstSql.add("insert into useronline
		// (username,token,address,client,clientid,time,lasttime) values
		// ('".concat(strUserName).concat("','").concat(UserToken).concat("','").concat(strClientAddr).concat("','").concat(strPlatForm).concat("','").concat(strClientID).concat("','").concat(strTime).concat("','").concat(strTime).concat("')"));
		// }
		// lstSql.add("insert into userlogininfo
		// (userid,username,address,client,clientid,version,time) select
		// id,username,address,client,clientid,'"
		// + strVersion +
		// "',time from userinfo where username
		// ='".concat(dbHelper.FilterSpecialCharacter(strUserName)).concat("'"));

		try {
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		} finally {
			dbHelper = null;
		}

		long time3 = System.currentTimeMillis();
		long span1 = (time2 - time1) / 1000;
		long span2 = (time3 - time2) / 1000;
		/*
		 * LoggerFile.appendMethod("d:\\logintime", "time1-time2=" + span1 +
		 * ";time3-time2=" + span2 );
		 */
		return bolRet;
	}
	
	/**
	 * 
	 * @param userName
	 * @param clientID
	 * @param ip
	 * @param platForm
	 * @param mobile
	 * @param version
	 * @param baseOsName
	 * @param appid
	 */
	public static String qrcodeLogin(String userName, String clientID, String ip, String platForm, String mobile, String version, String baseOsName, String appid){
		String[] arrUserToken = new String[] { "" };
		if (!checkOnlineCount(appid, userName, platForm, clientID, ip, arrUserToken)) {
				return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLIENTOVERFULL.code));
		}
		String userToken = arrUserToken[0];
		String time = Common.GetDateTime();
		if (Common.IsNullOrEmpty(userToken)) {
			userToken = CreateUserToken(userName);
		}
		List<String> lstSql = new ArrayList<String>(3);
		DBHelper dbHelper = null;
		try{
			dbHelper = DBHelper.GetInstance();
			lstSql.add(
				"insert into useronline (username,token,address,client,clientid,version,appid,time,lasttime) values ('"
						.concat(dbHelper.FilterSpecialCharacter(userName)).concat("','").concat(userToken)
						.concat("','").concat(dbHelper.FilterSpecialCharacter(ip)).concat("','")
						.concat(dbHelper.FilterSpecialCharacter(platForm)).concat("','")
						.concat(dbHelper.FilterSpecialCharacter(clientID)).concat("','")
						.concat(dbHelper.FilterSpecialCharacter(version)).concat("','").concat(appid)
						.concat("','").concat(time).concat("','").concat(time).concat("')"));
			lstSql.add("insert into " + getUserLoginInfoTable()
			+ " (username,mobile,address,client,clientid,version,baseos,appid,time,thduser) values ('"
					.concat(dbHelper.FilterSpecialCharacter(userName)).concat("','")
					.concat(dbHelper.FilterSpecialCharacter(mobile)).concat("','")
					.concat(dbHelper.FilterSpecialCharacter(ip)).concat("','")
					.concat(dbHelper.FilterSpecialCharacter(platForm)).concat("','").concat(clientID)
					.concat("','").concat(dbHelper.FilterSpecialCharacter(version)).concat("','")
					.concat(baseOsName).concat("','").concat(appid).concat("','").concat(time).concat("',")
					.concat(String.valueOf(2)).concat(")"));
			dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		} finally {
			dbHelper = null;
		}
		return userToken;
	}

	// private static boolean setUserLoginInfo(String UserName) {
	// boolean bolRet = false;
	// List<String> lstSql = new ArrayList<String>(2);
	// lstSql.add("update userinfo set logincount= (select count(1)+1 from
	// userlogininfo where username='".concat(UserName).concat("') where
	// username='").concat(UserName).concat("'"));
	// lstSql.add("insert into userlogininfo
	// (userid,username,address,client,clinetid,time) select
	// id,username,address,client,clientid,time from userinfo where username
	// ='".concat(UserName).concat("'"));
	// try {
	// DBHelper dbHelper = DBHelper.GetInstance();
	// bolRet = dbHelper.ExecuteSql(lstSql);
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// }
	// lstSql = null;
	// return bolRet;
	// }
	//
	// private static boolean setOnLineInfo(UserInfoBean UserInfo, String Token)
	// {
	// boolean bolRet = false;
	// DBHelper dbHelper = null;
	// String strUserName = UserInfo.getUserName();
	// String strPlatForm = UserInfo.getPlatForm();
	// String strClientID = UserInfo.getClientID();
	// HttpServletRequest request = HttpContext.GetRequest();
	// if (Common.IsNullOrEmpty(strPlatForm)) {
	// strPlatForm = request.getHeader("User-Agent");
	// }
	// String strClientAddr = request.getRemoteAddr();
	// try {
	// dbHelper = DBHelper.GetInstance();
	// // if (dbHelper.GetCount("useronline",
	// // "username='".concat(UserName).concat("'")) == 0) {
	// // bolRet = dbHelper.Insert("useronline", new String[] { "username",
	// // "token", "time" }, new Object[] { UserName, Token,
	// // Common.GetDateTime() });
	// // } else {
	// // bolRet = dbHelper.Update("useronline",
	// // "username='".concat(UserName).concat("'"), new String[] {
	// // "token", "time" }, new Object[] { Token, Common.GetDateTime() });
	// // }
	// bolRet = dbHelper.Insert("useronline", new String[] { "username",
	// "token", "address", "client", "clientid", "time", "lasttime" }, new
	// Object[] { strUserName, Token, strClientAddr, strPlatForm, strClientID,
	// Common.GetDateTime(), Common.GetDateTime() });
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// } finally {
	// dbHelper = null;
	// }
	// return bolRet;
	// }

	private static String CreateUserToken(String UserName) {
		String strInfo = UserName.concat(String.valueOf(new Date().getTime()));
		String strMD5 = Common.EnCodeMD5(strInfo);
		String strEncrypt = Common.EncryptData(strInfo, TokenPassWord);
		return strMD5.concat(strEncrypt).replace("\\", "X").replace("/", "Y").replace("+", "Z");
	}

	public static boolean IsValidToken(String Token) {
		if (Token == null) {
			return false;
		}
		String strMD5 = Token.substring(0, 32);
		String strEncrypt = Token.substring(32);
		String strInfo = Common.DecryptData(strEncrypt, TokenPassWord);
		if (strMD5.equals(Common.EnCodeMD5(strInfo))) {
			return true;
		}
		return false;
	}

	public static List<Map<String, Object>> getLastLoginListOrderByTime(String UserID, String Terminal,
			String StartDate, String EndDate, String singleTer, int Order, int Start, int Length) {
		String strOrder;
		if (Order == 0) {
			strOrder = "time asc";
		} else {
			strOrder = "time desc";
		}
		return getLastLoginList(UserID, Terminal, StartDate, EndDate, singleTer, strOrder, Start, Length);
	}

	public static List<Map<String, Object>> getLastLoginListOrderByCount(String UserID, String Terminal,
			String StartDate, String EndDate, String singleTer, int Order, int Start, int Length) {
		String strOrder;
		if (Order == 0) {
			strOrder = "logincount asc";
		} else {
			strOrder = "logincount desc";
		}
		return getLastLoginList(UserID, Terminal, StartDate, EndDate, singleTer, strOrder, Start, Length);
	}

	public static int getSumLoginCount(String UserID, String Terminal, String StartDate, String EndDate,
			String singleTer) {
		List<Map<String, Object>> lstSumLoginCount = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		if (UserID != null) {
			sbCondition.append("username in (").append(UserID).append(") and ");
		}
		if (Terminal != null) {
			sbCondition.append("baseos in ('").append(Terminal.replace(",", "','")).append("') and ");
		}
		if (singleTer != null) {
			sbCondition.append("client like '").append(singleTer).append("%' and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <='")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 3600 * 24 * 1000)))
					.append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		sbCondition.append("");
		StringBuilder sbTableName = new StringBuilder();
		sbTableName.append(
				"(select username,`client`,`address`,MAX(`time`) as time,COUNT(*) as logincount from userlogininfo ");
		if (sbCondition.length() > 0) {
			sbTableName.append(" WHERE ").append(sbCondition.toString());
		}
		sbTableName.append(")as tab");
		try {
			lstSumLoginCount = dbHelper
					.ExecuteQuery("select SUM(logincount) as sumcount from ".concat(sbTableName.toString()));
			// iCount=dbHelper.GetCount("(select
			// username,`client`,`address`,`time`,`userid`,COUNT(*) from
			// userlogininfo WHERE ".concat(sbCondition.toString()).concat(")as
			// tab"),
			// "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstSumLoginCount == null) {
			return 0;
		} else {
			return Integer.parseInt(String.valueOf(lstSumLoginCount.get(0).get("sumcount")));
		}

	}

	public static int getLastLoginCount(String UserID, String Terminal, String StartDate, String EndDate,
			String singleTer) {
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		if (UserID != null) {
			sbCondition.append("username in (").append(UserID).append(") and ");
		}
		if (Terminal != null) {
			sbCondition.append("baseos in ('").append(Terminal.replace(",", "','")).append("') and ");
		}
		if (singleTer != null) {
			sbCondition.append("client like '").append(singleTer).append("%' and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <='")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 3600 * 24 * 1000)))
					.append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		sbCondition.append("");
		StringBuilder sbTableName = new StringBuilder();
		sbTableName.append("(select username from userlogininfo ");
		if (sbCondition.length() > 0) {
			sbTableName.append(" WHERE ").append(sbCondition.toString());
		}
		sbTableName.append("  group by username)as tab");
		try {
			iCount = dbHelper.GetCount(sbTableName.toString(), "");
			// iCount=dbHelper.GetCount("(select
			// username,`client`,`address`,`time`,`userid`,COUNT(*) from
			// userlogininfo WHERE ".concat(sbCondition.toString()).concat(")as
			// tab"),
			// "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	private static List<Map<String, Object>> getLastLoginList(String UserID, String Terminal, String StartDate,
			String EndDate, String singleTer, String Order, int Start, int Length) {
		List<Map<String, Object>> lstRet = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		if (UserID != null) {
			sbCondition.append("username in (").append(UserID).append(") and ");
		}
		if (Terminal != null) {
			sbCondition.append("baseos in ('").append(Terminal.replace(",", "','")).append("') and ");
		}
		if (singleTer != null) {
			sbCondition.append("client like '").append(singleTer).append("%' and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <='")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 3600 * 24 * 1000)))
					.append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		// sbCondition.append(" group by username");

		// if(Terminal!=null){
		// sbCondition.append(",client");
		// }
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(
				"select username,`client`,`address`,MAX(`time`) as time,COUNT(*) as logincount FROM userlogininfo");
		if (sbCondition.length() > 0) {
			sbSql.append(" where ").append(sbCondition.toString());
		}
		sbSql.append("  group by username ");
		if (Order != null) {
			sbSql.append(" order by ").append(Order);
		}
		sbSql.append(" LIMIT ").append(Start - 1).append(",").append(Length);
		try {
			// String strSQL=" WHERE group by username ORDER BY time asc"
			lstRet = dbHelper.ExecuteQuery(sbSql.toString());// ("
																// username,`client`,`address`,`time`,`userid`,COUNT(*)
																// as
																// logincount",
																// "userlogininfo",
																// sbCondition.toString(),Order);
			// select username,`client`,`address`,`time`,`userid`,COUNT(*) from
			// userlogininfo WHERE time>='2013-05-01' and time<='2013-12-31' and
			// userid in(1,2,3) and `client` in ('ipad','ipone4') GROUP BY
			// username,`client`
			// DBHelper dbHelper = DBHelper.GetInstance();
			// lstRet =
			// dbHelper.ExecuteQuery("id,username,address,client,logincount,time",
			// "userinfo", "", Order, Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRet;
	}

	public static int getLastLoginCount(String UserName) {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("userinfo", "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static int getLastLoginLogCount(String appID, String UserName) {
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(appID)) {
			sbCondition.append("appID = '").append(dbHelper.FilterSpecialCharacter(appID)).append("' and ");
		}
		if (UserName != null && UserName.length() > 0) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		try {
			if (Common.IsNullOrEmpty(appID)) {
				iCount = dbHelper.GetCount("(select username from userinfo group by username) tab",
						sbCondition.toString());
			} else {
				iCount = dbHelper.GetCount("userinfo", sbCondition.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iCount;
	}

	public static List<Map<String, Object>> getLastLoginLogList(String appID, String UserName, int Start, int Length) {
		List<Map<String, Object>> lstRet = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(appID)) {
			sbCondition.append("appID = '").append(dbHelper.FilterSpecialCharacter(appID)).append("' and ");
		}
		// else {
		// //sbCondition.append("id in( select max(id) from userinfo GROUP BY
		// username ) and ");
		// }
		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		try {
			lstRet = dbHelper.ExecuteQuery("id,username,appid,updatetime,time", "userinfo", sbCondition.toString(),
					"updatetime desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRet;
	}

	public static int getUserLoginCount(String appID, String userName) {
		int iCount = 0;

		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(appID)) {
			sbCondition.append("appid='").append(appID).append("' and ");
		}
		if (!Common.IsNullOrEmpty(userName)) {
			sbCondition.append("username='").append(userName).append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("userlogininfo", sbCondition.toString());
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return iCount;
	}

	public static int getUserLoginCountByUserID(String appid, String UserName, String Terminal, String StartDate,
			String EndDate) {
		int iCount = 0;

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();

		sbCondition.append("username ='").append(UserName).append("' and ");
		if (!Common.IsNullOrEmpty(appid)) {
			sbCondition.append("appid='").append(appid).append("' and ");
		}
		if (Terminal != null) {
			sbCondition.append("baseos in ('").append(Terminal.replace(",", "','")).append("') and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <='").append(dbHelper.FilterSpecialCharacter(EndDate)).append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}

		try {
			iCount = dbHelper.GetCount("userlogininfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getUserLoginListByUserID(String appid, String UserName, String Terminal,
			String StartDate, String EndDate, int Start, int Length) {

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		sbCondition.append("username ='").append(UserName).append("' and ");
		if (!Common.IsNullOrEmpty(appid)) {
			sbCondition.append("appid='").append(appid).append("' and ");
		}
		if (Terminal != null) {
			sbCondition.append("client in ('").append(Terminal.replace(",", "','")).append("') and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <='").append(dbHelper.FilterSpecialCharacter(EndDate)).append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		List<Map<String, Object>> lstRet = null;
		try {
			lstRet = dbHelper.ExecuteQuery("id,username,address,client,appid,version,time", "userlogininfo",
					sbCondition.toString(), "time desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRet;
	}

	public static boolean delUserLoginLog(String LogID, String UserName) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("userlogininfo", "id in (".concat(LogID).concat(")"));
			if (bResult) {
				updateLoginCount(UserName);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static List<Map<String, Object>> getLoginList(String UserIDs, String StartTime, String EndTime,
			String Clients) {
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(UserIDs)) {
			sbCondition.append("userid in (").append(UserIDs).append(") and ");
		}
		if (!Common.IsNullOrEmpty(StartTime)) {
			sbCondition.append("time >='").append(StartTime).append("' and ");
		}
		if (!Common.IsNullOrEmpty(EndTime)) {
			sbCondition.append("time <='").append(EndTime).append("' and ");
		}
		if (!Common.IsNullOrEmpty(Clients)) {
			sbCondition.append("client in ('").append(Clients.replace(",", "','")).append("') and");
		}
		int iLen = sbCondition.length();
		if (iLen > 0) {
			sbCondition.delete(iLen - 4, iLen);
		}
		List<Map<String, Object>> lstRet = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRet = dbHelper.ExecuteQuery("id,username,address,client,appid,time", "userlogininfo",
					sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		sbCondition = null;

		return lstRet;
	}

	public static int getAllLoginCount(String UserIDs, String StartTime, String EndTime, String Clients) {
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(UserIDs)) {
			sbCondition.append("userid in (").append(UserIDs).append(") and ");
		}
		if (!Common.IsNullOrEmpty(StartTime)) {
			sbCondition.append("time >='").append(StartTime).append("' and ");
		}
		if (!Common.IsNullOrEmpty(EndTime)) {
			sbCondition.append("time <='").append(EndTime).append("' and ");
		}
		if (!Common.IsNullOrEmpty(Clients)) {
			sbCondition.append("client in ('").append(Clients.replace(",", "','")).append("') and");
		}
		int iLen = sbCondition.length();
		if (iLen > 0) {
			sbCondition.delete(iLen - 4, iLen);
		}

		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("userlogininfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static Map<String, Integer> getLoginCount(String UserIDs, String StartTime, String EndTime, String Clients) {
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(UserIDs)) {
			sbCondition.append("userid in (").append(UserIDs).append(") and ");
		}
		if (!Common.IsNullOrEmpty(StartTime)) {
			sbCondition.append("time >='").append(StartTime).append("' and ");
		}
		if (!Common.IsNullOrEmpty(EndTime)) {
			sbCondition.append("time <='").append(EndTime).append("' and ");
		}
		if (!Common.IsNullOrEmpty(Clients)) {
			sbCondition.append("client in ('").append(Clients.replace(",", "','")).append("') and");
		}
		int iLen = sbCondition.length();
		if (iLen > 0) {
			sbCondition.delete(iLen - 4, iLen);
		}

		List<Map<String, Object>> lstCount = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstCount = dbHelper.ExecuteQuery("select username,count(1) count from userlogininfo group by userid");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		Map<String, Integer> mapRet = new HashMap<String, Integer>(lstCount.size());
		for (Map<String, Object> map : lstCount) {
			mapRet.put(map.get("username").toString(), Integer.valueOf(map.get("count").toString()));
		}
		lstCount = null;
		return mapRet;
	}

	public static String getUserToken(String UserName, String PassWord) {
		String strUrl = Common.GetConfig("AuthUrl");
		String strAppID = Common.GetConfig("AppID");
		String strAppSecret = Common.GetConfig("AppSecret");
		URL url;
		StringBuilder sbToken = new StringBuilder();
		try {
			url = new URL(strUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			String timeSign = String.valueOf(System.currentTimeMillis());
			String strParam = "grant_type=password&username=".concat(UserName).concat("&password=")
					.concat(Common.getBase64Password(PassWord, "jds)(#&dsa7SDNJ32hwbds%u32j33edjdu2@**@3w"))
					.concat("&client_id=").concat(strAppID).concat("&client_secret=")
					.concat(Common.SHA1(timeSign.concat(strAppSecret))).concat("&sign=").concat(timeSign);
			http.getOutputStream().write(strParam.getBytes("utf-8"));
			http.getOutputStream().flush();
			http.getOutputStream().close();
			if (http.getResponseCode() != 200) {
				Logger.WriteException(new Exception("获取OataToken失败:" + String.valueOf(http.getResponseCode())));
				return null;
			}
			InputStream input = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				sbToken.append(line);
			}
			http.disconnect();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return String.valueOf(JSONObject.fromObject(sbToken.toString()).get("access_token"));

	}

	public static String getUserToken() {
		String strUrl = Common.GetConfig("AuthUrl");
		String strAppID = Common.GetConfig("AppID");
		String strAppSecret = Common.GetConfig("AppSecret");
		URL url;
		StringBuilder sbToken = new StringBuilder();
		try {
			url = new URL(strUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			String timeSign = String.valueOf(System.currentTimeMillis());
			String strParam = "grant_type=client_credentials&client_id=".concat(strAppID).concat("&client_secret=")
					.concat(Common.SHA1(timeSign.concat(strAppSecret))).concat("&sign=").concat(timeSign);
			http.getOutputStream().write(strParam.getBytes("utf-8"));
			http.getOutputStream().flush();
			http.getOutputStream().close();
			if (http.getResponseCode() != 200) {
				InputStream input = http.getErrorStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				Logger.WriteException(new Exception("获取OataToken失败:" + String.valueOf(http.getResponseCode())));
				return null;
			}
			InputStream input = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				sbToken.append(line);
			}
			http.disconnect();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return String.valueOf(JSONObject.fromObject(sbToken.toString()).get("access_token"));

	}

	public static JSONObject getAppToken() {
		String strUrl = Common.GetConfig("AuthUrl");
		String strAppID = Common.GetConfig("AppID");
		String strAppSecret = Common.GetConfig("AppSecret");
		URL url;
		StringBuilder sbToken = new StringBuilder();
		try {
			url = new URL(strUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			String timeSign = String.valueOf(System.currentTimeMillis());
			String strParam = "grant_type=client_credentials&client_id=".concat(strAppID).concat("&client_secret=")
					.concat(Common.SHA1(timeSign.concat(strAppSecret))).concat("&sign=").concat(timeSign);
			http.getOutputStream().write(strParam.getBytes("utf-8"));
			http.getOutputStream().flush();
			http.getOutputStream().close();
			if (http.getResponseCode() != 200) {
				InputStream input = http.getErrorStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				Logger.WriteException(new Exception("获取OataToken失败:" + String.valueOf(http.getResponseCode())));
				return null;
			}
			InputStream input = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				sbToken.append(line);
			}
			http.disconnect();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return JSONObject.fromObject(sbToken.toString());

	}

	public static String getUserSysToken(String UserName) {
		List<Map<String, Object>> lstToken = null;
		String strToken = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstToken = dbHelper.ExecuteQuery("systoken", "userinfo", "username ='".concat(UserName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstToken == null) {
			return strToken;
		}
		strToken = (String) lstToken.get(0).get("systoken");
		lstToken = null;
		return strToken;
	}

	public static String getUserIdenID(String UserName) {
		List<Map<String, Object>> lstIdenID = null;
		String strIdenID = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstIdenID = dbHelper.ExecuteQuery("idenid", "userinfo", "username ='".concat(UserName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstIdenID == null) {
			return strIdenID;
		}
		strIdenID = (String) lstIdenID.get(0).get("idenid");
		lstIdenID = null;
		return strIdenID;
	}

	public static String getUserPassword(String UserName) {
		List<Map<String, Object>> lstUserPwd = null;
		String strUserPwd = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstUserPwd = dbHelper.ExecuteQuery("password", "userinfo", "username ='".concat(UserName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstUserPwd == null) {
			return strUserPwd;
		}
		strUserPwd = (String) lstUserPwd.get(0).get("password");
		lstUserPwd = null;
		return Common.DecryptData(strUserPwd, TokenPassWord);
	}

	public static void main(String[] args) {
		// System.out.println(ecpLogin(getEcpToken(), "zwxtest",
		// "123456","202.96.31.95"));
		System.out.println(Common.DecryptData("wtu0+kZR+aXz/2KbsMFXNQ==", TokenPassWord));
		// System.out.println(Common.EncryptData("ttod", TokenPassWord));
		/*
		 * System.out.println(ecpBindUser("o16EX1dEcS8meAgun4LNPD8FovBA",
		 * "lwztest", "weixin", "ios", "202.96.31.95"));
		 */
		// System.out.println(ecpThirdLogin("o16EX1dEcS8meAgun4LNPD8FovBA","weixin","202.96.31.95"));
		Calendar instance = Calendar.getInstance();

		long timeInMillis = instance.getTimeInMillis();

		Date now = new Date(timeInMillis);
		long endMillis = timeInMillis - (1000 * 1800);
		Date endDate = new Date(endMillis);

		instance.set(Calendar.HOUR_OF_DAY, 0);
		instance.set(Calendar.MINUTE, 0);
		instance.set(Calendar.SECOND, 0);
		instance.set(Calendar.MILLISECOND, 0);
		long zero = instance.getTimeInMillis();
		Date startDate = new Date(zero);
		// int halfHourUsers = getHalfHourUsers(startDate, endDate);
		// System.out.println(halfHourUsers);

		String convertToDateTime = Common.ConvertToDateTime("2017-05-22", "yyyy-MM-dd", 86400000);
		System.out.println(convertToDateTime);
		List<Map<String, Object>> statistics = getStatistics("2017-10-23");
		for (Map<String, Object> map : statistics) {
			Set<Entry<String, Object>> entrySet = map.entrySet();
			for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				System.out.println(key + ":" + value);
			}

		}
	}

	// public static int addUserInfo(UserInfoBean UserInfo) {
	// boolean bolRet = false;
	// String strUserName = UserInfo.getUserName();
	// String strPassWord = UserInfo.getPassWord();
	// String strEMail = UserInfo.getEMail();
	// if (isExistUser(strUserName)) {
	// return SysConfigMngr.ERROR_CODE.ERROR_USEREXIST.code;
	// }
	// try {
	// DBHelper dbHelper = DBHelper.GetInstance();
	// bolRet = dbHelper.Insert("userinfo", new String[] { "username",
	// "password", "email" }, new Object[] { strUserName, strPassWord, strEMail
	// });
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// }
	// int iRet = SysConfigMngr.ERROR_CODE.NO_ERROR.code;
	// if (!bolRet) {
	// iRet = SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code;
	// }
	// return iRet;
	// }

	private static boolean updateLoginCount(String UserName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper
					.ExecuteSql("update userinfo set logincount= (select count(1) from userlogininfo where username='"
							.concat(UserName).concat("') where username='").concat(UserName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static Map<String, Object> getOnlineUserInfo(String Token) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("username,address,client,clientid,time", "useronline",
					"token='".concat(Token).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return null;
		}
		return lstInfo.get(0);
	}

	public static boolean updateUserOnlineCount() {
		boolean bolRet = false;
		String strDate = Common.GetDateTime("yyyy-MM-dd");
		String strTime = Common.GetDateTime();
		try {
			String strSql;
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("useronlinecount", "date='".concat(strDate).concat("'")) == 0) {
				strSql = "insert into useronlinecount (usercount,onlinecount,date,time) values ( (select count(distinct username) from useronline where time like '"
						.concat(strDate).concat(" %') ,(select count(1) from useronline where time like '")
						.concat(strDate).concat(" %'),'").concat(strDate).concat("','").concat(strTime).concat("')");
			} else {
				strSql = "update useronlinecount SET usercount=greatest(usercount,(select count(distinct username) from useronline where time like '"
						.concat(strDate)
						.concat(" %')),onlinecount=greatest(onlinecount ,(select count(1) from useronline where time like '")
						.concat(strDate).concat(" %')),time='").concat(strTime).concat("' where date='").concat(strDate)
						.concat("'");
			}
			bolRet = dbHelper.ExecuteSql(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		return bolRet;
	}

	public static List<Map<String, Object>> getOnlineCount(String StartDate, String EndDate) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <'")
					.append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 86400000)))
					.append("' and ");
		}
		String strCondition = "";
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			strCondition = " where ".concat(sbCondition.toString());
		}

		List<Map<String, Object>> lstInfo = null;
		try {
			lstInfo = dbHelper
					.ExecuteQuery("select usercount,onlinecount,date  from useronlinecount".concat(strCondition));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return null;
		}

		return lstInfo;
	}

	public static List<Map<String, Object>> getStatistics(String StartDate) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		if (StartDate != null) {// yyyy-MM-dd HH:mm
			sbCondition.append("spottime >='").append(dbHelper.FilterSpecialCharacter(StartDate + " 00:00:00"))
					.append("' and ");

			sbCondition.append("spottime <'")
					.append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(StartDate, "yyyy-MM-dd", 86400000))
							+ " 00:00:00")
					.append("' and ");
		}
		String strCondition = "";
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			strCondition = " where ".concat(sbCondition.toString());
		}

		List<Map<String, Object>> lstInfo = null;
		try {
			lstInfo = dbHelper.ExecuteQuery(
					"select count,DATE_FORMAT(spottime, '%Y-%m-%d %H:%i') as spottime  from useronlinestatic"
							.concat(strCondition));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return Collections.emptyList();
		}

		return lstInfo;
	}

	public static List<Map<String, Object>> getLoginCount(String UserIDs, String Clients, String StartDate,
			String EndDate, String Type, String singleTer) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		if (UserIDs != null) {
			sbCondition.append("username in (").append(UserIDs).append(") and ");
		}
		if (Clients != null) {
			sbCondition.append("baseos in ('").append(Clients.replace(",", "','")).append("') and ");
		}
		if (singleTer != null) {
			sbCondition.append("client like '").append(singleTer).append("%' and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <'")
					.append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 86400000)))
					.append("' and ");
		}
		String strCondition = "";
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			strCondition = " where ".concat(sbCondition.toString());
		}

		String strLen = null;
		switch (Type.toLowerCase()) {
		case "y":
			strLen = "4";
			break;
		case "m":
			strLen = "7";
			break;
		case "d":
			strLen = "10";
			break;
		default:
			break;

		}

		if (strLen == null) {
			return null;
		}

		String strSql = "select count(1) count, left(time,".concat(strLen).concat(") date from userlogininfo ")
				.concat(strCondition).concat(" GROUP BY date order by time");
		List<Map<String, Object>> lstInfo = null;
		try {
			lstInfo = dbHelper.ExecuteQuery(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		return lstInfo;
	}

	public static List<Map<String, Object>> getMaxOnlineCountList() {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("id,onlinecount,client,time", "maxonlinecount");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstInfo;
	}

	public static boolean updateMaxOnlineCount(int ID, int Count, String Client) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update("maxonlinecount", "id=".concat(String.valueOf(ID)),
					new String[] { "onlinecount", "client", "time" },
					new Object[] { Count, Client, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean addMaxOnlineCount(int Count, String Client) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("maxonlinecount", new String[] { "onlinecount", "client", "time" },
					new Object[] { Count, Client, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static Map<String, Object> getMaxOnlineInfo(String ID) {
		List<Map<String, Object>> lstOnline = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOnline = dbHelper.ExecuteQuery("onlinecount,client,time", "maxonlinecount", "id=".concat(ID));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstOnline != null)
			return lstOnline.get(0);
		return null;
	}

	public static boolean deleteMaxOnlineCount(int ID) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("maxonlinecount", "id=".concat(String.valueOf(ID)));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static int getMaxOnlineCount(String Client, String AppID) {
		int iCount = -1;
		List<Map<String, Object>> lstInfo = null;

		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if ("cnki_law".equals(AppID.toLowerCase()))
				return 1;
			lstInfo = dbHelper.ExecuteQuery("onlinecount", "maxonlinecount", "client='".concat(Client).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo != null) {
			iCount = (int) lstInfo.get(0).get("onlinecount");
			lstInfo = null;
		}
		return iCount;
	}

	public static boolean checkOnlineCount(String AppID, String UserName, String Client, String ClientID,
			String address, String[] ArrUserToken) {
		ArrUserToken[0] = "";
		int iCount = 0;
		String strClient = "";
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			List<Map<String, Object>> lstUserToken = dbHelper.ExecuteQuery("token", "useronline",
					"username='".concat(UserName).concat("' and clientid='").concat(ClientID).concat("' and appid='")
							.concat(AppID).concat("'"));
			if (lstUserToken != null) {
				ArrUserToken[0] = lstUserToken.get(0).get("token") == null ? ""
						: String.valueOf(lstUserToken.get(0).get("token"));
				dbHelper.Update("useronline",
						"token='".concat(dbHelper.FilterSpecialCharacter(ArrUserToken[0])).concat("'"),
						new String[] { "address", "lasttime" }, new Object[] { address, Common.GetDateTime() });
				return true;
			}
			strClient = Client.toLowerCase().split("-")[0];
			if ("cnki_law".equals(AppID.toLowerCase())) {
				lstInfo = dbHelper.ExecuteQuery("DISTINCT clientid", "useronline",
						"username='".concat(UserName).concat("' and appid='").concat(AppID).concat("'"));
			} else {
				lstInfo = dbHelper.ExecuteQuery("DISTINCT clientid", "useronline",
						"username='".concat(UserName).concat("' and appid='").concat(AppID)
								.concat("' and client like '").concat(strClient).concat("%'"));
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo != null) {
			iCount = lstInfo.size();
			lstInfo = null;
		}
		int iMaxCount = getMaxOnlineCount(strClient, AppID);
		if (iMaxCount < 0 || iCount < iMaxCount) {
			return true;
		}
		return false;
	}

	public static boolean clearUserTerminal(String User) {
		boolean bResult = false;
		String[] arrUser = User.split(";");
		List<String> arrList = new ArrayList<String>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			for (int i = 0; i < arrUser.length; i++) {
				if (Common.IsNullOrEmpty(arrUser[i]))
					continue;
				arrList.add("delete from useronline where username = '".concat(arrUser[i]).concat("'"));
			}
			bResult = dbHelper.ExecuteSql(arrList);

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	// /**
	// *
	// * @param UserName
	// * 用户名
	// * @param Favicon
	// * 头像名称
	// * @param FaviconType
	// * 头像类型
	// * @param FaviconPath
	// * 物理路径
	// * @return
	// */
	// public static String saveFavicon(String UserName, String Favicon, String
	// FaviconType, String FaviconPath) {
	// DBHelper dbHelper = null;
	// String strTime = Common.GetDateTime();
	// try {
	// dbHelper = DBHelper.GetInstance();
	// dbHelper.Insert(getUserFaviconTable(UserName), new String[] { "username",
	// "favicon", "type", "path", "updatetime", "inserttime" }, new Object[] {
	// UserName, Favicon, FaviconType, FaviconPath, strTime, strTime });
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// return "";
	// }
	// return strTime;
	// }
	//
	// public static Map<String, Object> getUserFavicon(String UserName) {
	// List<Map<String, Object>> lstFavicon = null;
	// DBHelper dbHelper = null;
	// try {
	// dbHelper = DBHelper.GetInstance();
	// lstFavicon =
	// dbHelper.ExecuteQuery("username, favicon, type, path, updatetime",
	// getUserFaviconTable(UserName),
	// "username='".concat(UserName).concat("'"));
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// }
	// if (lstFavicon == null) {
	// return null;
	// }
	// return lstFavicon.get(0);
	// }
	//
	// private static String getUserFaviconTable(String UserName) {
	// return "userfavicon".concat(String.valueOf(Math.abs(UserName.hashCode())
	// % 10));
	// }

	public static boolean saveUserCnkiFile(String UserName, String TypeID, String FileID) {
		boolean bResult = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Insert(getUserCnkiTable(UserName),
					new String[] { "username", "typeid", "fileid", "isdelete", "updatetime", "inserttime" },
					new Object[] { UserName, TypeID, FileID, 0, Common.GetDateTime(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/***
	 * 暂时只用了UserName，FileID，没有用到TypeID，如果fileid不唯一，再添加TypeID
	 * 
	 * @param UserName
	 * @param TypeID
	 * @param FileID
	 * @return
	 */
	public static boolean isExistsCnkiFile(String UserName, String TypeID, String FileID) {
		boolean bResult = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			// bResult = dbHelper.GetCount(getUserCnkiTable(UserName),
			// "username='".concat(UserName).concat("' and
			// typeid='".concat(TypeID).concat("' and
			// fileid='".concat(FileID).concat("' and isdelete = 0 "))))
			// > 1;
			bResult = dbHelper.GetCount(getUserCnkiTable(UserName), "username='".concat(UserName)
					.concat("' and fileid='").concat(FileID).concat("' and isdelete = 0 ")) > 0;
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	private static String getUserCnkiTable(String UserName) {
		return "userfileinfo".concat(String.valueOf(Math.abs(UserName.hashCode()) % 10));
	}

	public static List<Map<String, Object>> getLoginLogCount(String startDate, String endDate) {

		List<Map<String, Object>> lstLogInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstLogInfo = dbHelper.ExecuteQuery("count,spottime", "userloginfo",
					"type=0 and spottime between '" + startDate + "' and '" + endDate + "'", "spottime asc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstLogInfo == null) {
			return null;
		} else {
			return lstLogInfo;
		}
	}

	public static Map<String, Object> getUserLoginLogCount(String startDate, String endDate) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		List<Map<String, Object>> lstLogInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstLogInfo = dbHelper.ExecuteQuery("count,spottime", "userloginfo",
					"type=1 and spottime between '" + startDate + "' and '" + endDate + "'", "spottime asc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstLogInfo == null) {
			return null;
		} else {
			for (Map<String, Object> tmp : lstLogInfo) {
				userMap.put(tmp.get("spottime").toString(), tmp.get("count").toString());
			}
		}
		return userMap;
	}

	public static List<Map<String, Object>> getUserStatist(String StartDate, String EndDate, String Type) {
		List<Map<String, Object>> lstUserInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUserInfo = dbHelper.ExecuteQuery("spottime,count,ios,windows,android,other", "userstatist",
					"spottime between '" + StartDate + "' and '" + EndDate + "' and type=" + Type);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstUserInfo;
	}

	public static String checkQQOpenID(String OpenID) {
		List<Map<String, Object>> lstUserInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUserInfo = dbHelper.ExecuteQuery("username", "userinfo", "qqopenid='".concat(OpenID).concat("'"));
			if (lstUserInfo != null)
				return lstUserInfo.get(0).get("username").toString();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return null;
	}

	public static String checkSinaOpenID(String OpenID) {
		List<Map<String, Object>> lstUserInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUserInfo = dbHelper.ExecuteQuery("username", "userinfo", "sinaopenid='".concat(OpenID).concat("'"));
			if (lstUserInfo != null)
				return lstUserInfo.get(0).get("username").toString();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return null;
	}

	/**
	 * 暂时没有验证在线用户个数
	 * 
	 * @param UserInfo
	 * @param Exist
	 * @return
	 */
	public static String qqUserLogin(UserInfoBean UserInfo, boolean Exist) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		}
		// String[] arrUserToken = new String[] { "" };
		// if (!checkOnlineCount(UserInfo.getUserName(), UserInfo.getPlatForm(),
		// UserInfo.getClientID(), arrUserToken)) {
		// return
		// "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLIENTOVERFULL.code));
		// }
		// String userToken = "";
		// boolean bOnline = !Common.IsNullOrEmpty(arrUserToken[0]);
		// if (bOnline) {
		// userToken = arrUserToken[0];
		// } else {
		String userToken = CreateUserToken(UserInfo.getUserName());
		// }
		List<String> lstSql = new ArrayList<String>(3);
		String strTime = Common.GetDateTime();
		if (!Exist) {
			lstSql.add(
					"insert into userinfo (username,password,address,client,clientid,logincount,qqopenid,updatetime,time) values ('"
							.concat(UserInfo.getUserName()).concat("','")
							.concat(Common.EncryptData(UserInfo.getPassWord(), TokenPassWord)).concat("','")
							.concat(UserInfo.getAddress()).concat("','").concat(UserInfo.getPlatForm()).concat("','")
							.concat(UserInfo.getClientID()).concat("',1,'").concat(UserInfo.getQqopenid()).concat("','")
							.concat(strTime).concat("','").concat(strTime).concat("')"));
		}
		// if (bOnline) {
		// lstSql.add("update useronline set
		// version='".concat(UserInfo.getVersion()).concat("',lasttime
		// ='").concat(Common.GetDateTime()).concat("',address='").concat(UserInfo.getAddress()).concat("'
		// where token = '").concat(userToken).concat("'"));
		// } else {
		lstSql.add("insert into useronline (username,token,address,client,clientid,version,time,lasttime) values ('"
				.concat(UserInfo.getUserName()).concat("','").concat(userToken).concat("','")
				.concat(UserInfo.getAddress()).concat("','").concat(UserInfo.getPlatForm()).concat("','")
				.concat(UserInfo.getClientID()).concat("','").concat(UserInfo.getVersion()).concat("','")
				.concat(strTime).concat("','").concat(strTime).concat("')"));
		// }
		String baseOsName = "other";
		BaseOsNames[] baseOsNames = BaseOsNames.values();
		for (BaseOsNames base : baseOsNames) {
			int index = UserInfo.getPlatForm().toLowerCase().indexOf(base.value.toLowerCase());
			if (index >= 0) {
				baseOsName = base.value;
				break;
			}
		}
		lstSql.add("insert into userlogininfo (username,address,client,clientid,version,baseos,sign,time) values ('"
				.concat(UserInfo.getUserName()).concat("','").concat(UserInfo.getAddress()).concat("','")
				.concat(UserInfo.getPlatForm()).concat("','").concat(UserInfo.getClientID()).concat("','")
				.concat(UserInfo.getVersion()).concat("','").concat(baseOsName).concat("',1,'").concat(strTime)
				.concat("')"));
		try {
			if (!dbHelper.ExecuteSql(lstSql)) {
				return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		} finally {
			dbHelper = null;
		}
		return userToken;
	}

	public static String sinaUserLogin(UserInfoBean UserInfo, boolean Exist) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		}
		/*
		 * String[] arrUserToken = new String[] { "" }; if
		 * (!checkOnlineCount(UserInfo.getUserName(), UserInfo.getPlatForm(),
		 * UserInfo.getClientID(), arrUserToken)) { return
		 * "@".concat(String.valueOf
		 * (SysConfigMngr.ERROR_CODE.ERROR_CLIENTOVERFULL.code)); } String
		 * userToken = ""; boolean bOnline =
		 * !Common.IsNullOrEmpty(arrUserToken[0]); if (bOnline) { userToken =
		 * arrUserToken[0]; } else {
		 */
		String userToken = CreateUserToken(UserInfo.getUserName());
		// }
		List<String> lstSql = new ArrayList<String>(3);
		String strTime = Common.GetDateTime();
		if (!Exist) {
			lstSql.add(
					"insert into userinfo (username,password,address,client,clientid,logincount,qqopenid,updatetime,time) values ('"
							.concat(UserInfo.getUserName()).concat("','")
							.concat(Common.EncryptData(UserInfo.getPassWord(), TokenPassWord)).concat("','")
							.concat(UserInfo.getAddress()).concat("','").concat(UserInfo.getPlatForm()).concat("','")
							.concat(UserInfo.getClientID()).concat("',1,'").concat(UserInfo.getQqopenid()).concat("','")
							.concat(strTime).concat("','").concat(strTime).concat("')"));
		}
		/*
		 * if (bOnline) { lstSql.add("update useronline set version='"
		 * .concat(UserInfo .getVersion ()).concat("',lasttime ='"
		 * ).concat(Common.GetDateTime()).concat ("',address='"
		 * ).concat(UserInfo.getAddress()).concat("' where token = '"
		 * ).concat(userToken).concat("'")); } else {
		 */
		lstSql.add("insert into useronline (username,token,address,client,clientid,version,time,lasttime) values ('"
				.concat(UserInfo.getUserName()).concat("','").concat(userToken).concat("','")
				.concat(UserInfo.getAddress()).concat("','").concat(UserInfo.getPlatForm()).concat("','")
				.concat(UserInfo.getClientID()).concat("','").concat(UserInfo.getVersion()).concat("','")
				.concat(strTime).concat("','").concat(strTime).concat("')"));
		// }
		String baseOsName = "other";
		BaseOsNames[] baseOsNames = BaseOsNames.values();
		for (BaseOsNames base : baseOsNames) {
			int index = UserInfo.getPlatForm().toLowerCase().indexOf(base.value.toLowerCase());
			if (index >= 0) {
				baseOsName = base.value;
				break;
			}
		}
		lstSql.add("insert into userlogininfo (username,address,client,clientid,version,baseos,sign,time) values ('"
				.concat(UserInfo.getUserName()).concat("','").concat(UserInfo.getAddress()).concat("','")
				.concat(UserInfo.getPlatForm()).concat("','").concat(UserInfo.getClientID()).concat("','")
				.concat(UserInfo.getVersion()).concat("','").concat(baseOsName).concat("',1,'").concat(strTime)
				.concat("')"));
		try {
			if (!dbHelper.ExecuteSql(lstSql)) {
				return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		} finally {
			dbHelper = null;
		}
		return userToken;
	}

	public static int getRegistCount(String appid, String phone, String userName, String startDate, String endDate) {
		int count = 0;
		DBHelper dbHelper = null;
		StringBuilder sbCondition = new StringBuilder();
		try {
			dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(appid)) {
				sbCondition.append("appid='" + appid + "' and ");
			}
			if (!Common.IsNullOrEmpty(phone)) {
				sbCondition.append("mobile ='").append(dbHelper.FilterSpecialCharacter(phone)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(userName)) {
				sbCondition.append("username ='").append(dbHelper.FilterSpecialCharacter(userName)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(startDate)) {
				sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(startDate)).append("' AND ");
			}
			if (!Common.IsNullOrEmpty(endDate)) {
				sbCondition.append("time <'").append(dbHelper
						.FilterSpecialCharacter(Common.ConvertToDateTime(endDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
						.append("' AND ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			}
			count = dbHelper.GetCount("userregister", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return count;
	}

	public static List<Map<String, Object>> getRegistUserList(String appid, String phone, String userName,
			String startDate, String endDate, int start, int length) {
		List<Map<String, Object>> list = null;
		DBHelper dbHelper = null;
		StringBuilder sbCondition = new StringBuilder();
		try {
			dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(appid)) {
				sbCondition.append("appid='" + appid + "' and ");
			}
			if (!Common.IsNullOrEmpty(phone)) {
				sbCondition.append("mobile ='").append(dbHelper.FilterSpecialCharacter(phone)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(userName)) {
				sbCondition.append("username ='").append(dbHelper.FilterSpecialCharacter(userName)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(startDate)) {
				sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(startDate)).append("' AND ");
			}
			if (!Common.IsNullOrEmpty(endDate)) {
				sbCondition.append("time <'").append(dbHelper
						.FilterSpecialCharacter(Common.ConvertToDateTime(endDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
						.append("' AND ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			}
			list = dbHelper.ExecuteQuery("id,username,email,appid,address,platform,time,mobile", "userregister",
					sbCondition.toString(), "time desc", start, length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return list;
	}

	public static void updateUserPassword(String userName, String password) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			dbHelper.Update("userinfo", "username ='".concat(userName).concat("'"), new String[] { "password" },
					new Object[] { Common.EncryptData(password, TokenPassWord) });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}

	public static int getUserLoginCount(String appid, String UserName, String StartDate, String EndDate) {
		int iCount = 0;

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();

		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '").append(UserName).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(appid)) {
			sbCondition.append("appid='").append(appid).append("' and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >'").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <='")
					.append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 86400000)))
					.append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		// System.out.println("co-->" + sbCondition.toString());

		/*
		 * try { iCount = dbHelper.GetCount("userlogininfo",
		 * sbCondition.toString()); } catch (Exception e) {
		 * Logger.WriteException(e); } return iCount;
		 */
		Object[] objParam = new Object[2];
		objParam[0] = "userlogininfo";
		objParam[1] = sbCondition.toString();
		List<Map<String, Object>> lstUserLoginInfo = null;
		try {
			lstUserLoginInfo = dbHelper.ExecuteQueryProc("up_GetViewCount", objParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstUserLoginInfo == null) {
			return 0;
		}
		return Integer.valueOf(lstUserLoginInfo.get(0).get("totalcount").toString());

	}

	public static List<Map<String, Object>> getUserLoginList(String appid, String UserName, String StartDate,
			String EndDate, int Start, int Length) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '").append(UserName).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(appid)) {
			sbCondition.append("appid='").append(appid).append("' and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >'").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <='")
					.append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 86400000)))
					.append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		/*
		 * System.out.println("condition:" + sbCondition.toString());
		 * List<Map<String, Object>> lstRet = null; try { lstRet =
		 * dbHelper.ExecuteQuery(
		 * "id,username,address,client,appid,version,time", "userlogininfo",
		 * sbCondition.toString(), "time desc", Start, Length); } catch
		 * (Exception e) { Logger.WriteException(e); } return lstRet;
		 */
		Object[] objParam = new Object[5];
		objParam[0] = "userlogininfo";
		objParam[1] = "id,username,address,client,appid,version,time";
		objParam[2] = sbCondition.toString();
		objParam[3] = Start;
		objParam[4] = Length;
		List<Map<String, Object>> lstUserLoginInfo = null;
		try {
			lstUserLoginInfo = dbHelper.ExecuteQueryProc("up_GetViewData", objParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstUserLoginInfo;

	}

	public static List<Map<String, Object>> getUserInfo(String userName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("name, sex, birthday, unitname, mobile, email, major, imageurl, nickname",
					"cnkiuser", "username ='".concat(userName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lst;
	}

	public static boolean existUserDevice(String devicenum) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("userdevice", "devicenum='".concat(devicenum).concat("'")) > 0)
				return true;
		} catch (Exception e) {

		}
		return false;
	}

	public static boolean insertUserDevice(String userName, String devicenum, String devicetoken, String manu,
			String brand) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.Insert("userdevice",
					new String[] { "username", "devicenum", "devicetoken", "manu", "brand", "updatetime", "time" },
					new Object[] { userName, devicenum, devicetoken, manu, brand, Common.GetDateTime(),
							Common.GetDateTime() }))
				return true;
		} catch (Exception e) {

		}
		return false;
	}

	public static boolean updateUserDevice(String userName, String devicenum, String devicetoken, String manu,
			String brand) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.Update("userdevice", "devicenum = '".concat(devicenum).concat("'"),
					new String[] { "username", "devicetoken", "manu", "brand", "updatetime" },
					new Object[] { userName, devicetoken, manu, brand, Common.GetDateTime() }))
				return true;
		} catch (Exception e) {

		}
		return false;
	}

	public static List<Map<String, Object>> getUserDevice(String userName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("devicetoken, manu, updatetime", "userdevice",
					"username ='".concat(userName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lst;
	}

	public static boolean existCnkiUserName(String userName) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("cnkiuser", "username ='".concat(userName).concat("'")) > 0)
				return true;
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return false;
	}

	public static boolean insertCnkiUser(String userName, String name, String sex, String birthday, String unitname,
			String email, String mobile, String major, String imageurl, String nickname) {
		DBHelper dbHelper = null;
		boolean bret = false;
		try {
			dbHelper = DBHelper.GetInstance();
			bret = dbHelper.Insert("cnkiuser",
					new String[] { "username", "name", "sex", "birthday", "unitname", "mobile", "email", "major",
							"updatetime", "time", "imageurl", "nickname" },
					new Object[] { userName, name, sex, birthday, unitname, mobile, email, major, Common.GetDateTime(),
							Common.GetDateTime(), imageurl, nickname });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bret;
	}

	public static boolean updateCnkiUser(String userName, String name, String sex, String birthday, String unitname,
			String email, String mobile, String major, String imageurl, String nickname) {
		DBHelper dbHelper = null;
		boolean bret = false;
		try {
			dbHelper = DBHelper.GetInstance();
			bret = dbHelper.Update("cnkiuser", "username='" + userName + "'",
					new String[] { "name", "sex", "birthday", "unitname", "mobile", "email", "major", "updatetime",
							"imageurl", "nickname" },
					new Object[] { name, sex, birthday, unitname, mobile, email, major, Common.GetDateTime(), imageurl,
							nickname });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bret;
	}

	private static boolean isFirstLogin(String userName) {
		DBHelper dbHelper = null;
		boolean bret = true;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("userfirstlogin", "username='" + userName + "'") > 0)
				bret = false;
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bret;
	}

	public static List<Map<String, Object>> getHuodongUser(String time) {
		List<Map<String, Object>> lstHuodongUser = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstHuodongUser = dbHelper.ExecuteQuery(
					"SELECT NOW() curtime,uf.time,uf.username,cu.`name`,cu.mobile from userfirstlogin uf INNER JOIN cnkiuser cu on uf.username=cu.username ORDER BY uf.time desc limit 20");
		} catch (Exception e) {

		}
		return lstHuodongUser;
	}

	public static boolean unLawfulUser(String userName) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("tempusername", "username='" + userName + "'") > 0)
				return true;
		} catch (Exception e) {

		}
		return false;
	}

	public static boolean existUserSet(String userName, String key) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("userset",
					"userName = '".concat(userName).concat("' and keyword='").concat(key).concat("'")) > 0) {
				bRet = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static boolean addUserSet(String userName, String key, String content) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("userset", new String[] { "username", "keyword", "content", "updatetime", "time" },
					new Object[] { userName, key, content, Common.GetDateTime(), Common.GetDateTime() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static boolean updateUserSet(String userName, String key, String content) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("userset", "username='" + userName + "' and keyword='" + key + "'",
					new String[] { "content", "updatetime" }, new Object[] { content, Common.GetDateTime() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static String getUserSet(String userName, String key) {
		List<Map<String, Object>> lst = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("content", "userset", "username='" + userName + "' and keyword='" + key + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (lst != null && lst.size() > 0)
			return lst.get(0).get("content").toString();
		return "";
	}

	private static String getUserLoginInfoTable() {
		String strNow = Common.GetDateTime("yyMMdd");
		String strYear = strNow.substring(0, 2);
		String strMonth = strNow.substring(2, 4);
		String strTablePost;

		switch (strMonth) {
		case "01":
		case "02":
		case "03":
			strTablePost = "0103";
			break;
		case "04":
		case "05":
		case "06":
			strTablePost = "0406";
			break;
		case "07":
		case "08":
		case "09":
			strTablePost = "0709";
			break;
		case "10":
		case "11":
		case "12":
			strTablePost = "1012";
			break;
		default:
			return "userlogininfo";
		}

		return "userlogininfo".concat(strYear).concat(strTablePost);
	}

	private static String getEcpToken() {
		String ecpUrl = "http://oauth.cnki.net/Auth/oauth/token";
		String client_id = "96d4368cd99d496d";
		String client_secret = "54b0c4ba652c4b4ea99d802490edbf1c";
		URL url;
		StringBuilder sbToken = new StringBuilder();
		try {
			url = new URL(ecpUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			String strParam = "grant_type=client_credentials&client_id=".concat(client_id).concat("&client_secret=")
					.concat(client_secret);
			http.getOutputStream().write(strParam.getBytes("utf-8"));
			http.getOutputStream().flush();
			http.getOutputStream().close();
			if (http.getResponseCode() != 200) {
				InputStream input = http.getErrorStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				return null;
			}
			InputStream input = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				sbToken.append(line);
			}
			http.disconnect();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		JSONObject json = JSONObject.fromObject(sbToken.toString());
		return json.getString("access_token");
	}

	public static JSONObject ecpLogin(String userName, String password, String ip) {
		String token = getEcpToken();
		String ecpUrl = "http://oauth.cnki.net:8450/Login/login?username=" + userName + "&password=" + password + "&ip="
				+ ip + "&logonType=NamePassLogin";
		URL url;
		StringBuilder sbToken = new StringBuilder();
		try {
			url = new URL(ecpUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.addRequestProperty("authorization", "Bearer ".concat(token));
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			http.getOutputStream().flush();
			http.getOutputStream().close();
			if (http.getResponseCode() != 200) {
				InputStream input = http.getErrorStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				return null;
			}
			InputStream input = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				sbToken.append(line);
			}
			http.disconnect();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return JSONObject.fromObject(sbToken.toString());
	}

	public static JSONObject ecpBindUser(String thirdUserId, String userName, String thirdName, String clientType,
			String ip) {
		String token = getEcpToken();
		String ecpUrl = "http://oauth.cnki.net:8450/Login/bind_user?thirdUserId=" + thirdUserId + "&thirdName="
				+ thirdName + "&userName=" + userName + "&clientType=" + clientType + "&userIp=" + ip;
		URL url;
		StringBuilder sbToken = new StringBuilder();
		try {
			url = new URL(ecpUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.addRequestProperty("authorization", "Bearer ".concat(token));
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			http.getOutputStream().flush();
			http.getOutputStream().close();
			if (http.getResponseCode() != 200) {
				InputStream input = http.getErrorStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				return null;
			}
			InputStream input = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				sbToken.append(line);
			}
			http.disconnect();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return JSONObject.fromObject(sbToken.toString());
	}

	public static String mcnkiThirdLogin(UserInfoBean userInfo, String thirdUserId, String thirdName, String ip) {
		JSONObject json = ecpThirdLogin(thirdUserId, thirdName, ip);
		if (json == null) { // 连接异常 -->一般为服务器错误
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CONNECT.code)).concat("}");
		}
		if (json.getBoolean("Success")) {
			String userName = json.getString("Username");
			addThirdUser(thirdUserId, thirdName, userName);
			String[] arrUserToken = new String[] { "" };

			if (!checkOnlineCount(userInfo.getAppid(), userName, userInfo.getPlatForm(), userInfo.getClientID(), ip,
					arrUserToken)) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLIENTOVERFULL.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLIENTOVERFULL.code)).concat("}");
			}
			String strUserToken = "";
			boolean bOnline = !Common.IsNullOrEmpty(arrUserToken[0]);
			if (bOnline) {
				strUserToken = arrUserToken[0];
			} else {
				strUserToken = CreateUserToken(userName);
			}
			userInfo.setUserName(userName);
			if (!setUserInfo(userInfo, strUserToken, bOnline, 1)) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
			}
			return "{\"result\":true,\"usertoken\":\"" + strUserToken + "\",\"username\":\""
					+ json.getString("Username") + "\"}";
		} else {
			return "{\"result\":false,\"isExist\":\"" + json.getString("isExist") + "\",\"errorcode\":\""
					+ json.getString("Code") + "\",\"message\":\"" + json.getString("Message") + "\"}";
		}
	}

	public static JSONObject ecpThirdLogin(String thirdUserId, String thirdName, String ip) {
		String token = getEcpToken();
		String ecpUrl = "http://oauth.cnki.net:8450//Login/login_by_third?thirdUserId=" + thirdUserId + "&thirdName="
				+ thirdName + "&userIp=" + ip;
		URL url;
		StringBuilder sbToken = new StringBuilder();
		try {
			url = new URL(ecpUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.addRequestProperty("authorization", "Bearer ".concat(token));
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			http.getOutputStream().flush();
			http.getOutputStream().close();
			if (http.getResponseCode() != 200) {
				InputStream input = http.getErrorStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				return null;
			}
			InputStream input = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				sbToken.append(line);
			}
			http.disconnect();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return JSONObject.fromObject(sbToken.toString());
	}

	public static boolean addThirdUser(String thirdUserId, String thirdName, String userName) {
		boolean bRet = true;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("thirduser",
					"thirduserid='" + thirdUserId + "' and username='" + userName + "'") == 0)
				bRet = dbHelper.Insert("thirduser", new String[] { "thirduserid", "thirdname", "username", "time" },
						new Object[] { thirdUserId, thirdName, userName, Common.GetDateTime() });
		} catch (Exception e) {

		}
		return bRet;
	}

	public static Map<String, Object> getThirdName(String thirdUserId) {
		List<Map<String, Object>> list = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			list = dbHelper.ExecuteQuery("thirduserid,thirdname", "thirduser", "username='" + thirdUserId + "'");
			if (list == null || list.size() == 0)
				return null;
			return list.get(0);
		} catch (Exception e) {

		}
		return null;
	}

	public static int getHalfHourUsers(Date endDate) {
		int iCount = 0;
		try {
			// select count(1) from mdlmngr.useronline where lasttime>='' and
			// lasttime<='';
			DBHelper dbHelper = DBHelper.GetInstance();
			StringBuilder sbCondition = new StringBuilder();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String end = simpleDateFormat.format(endDate);
			sbCondition.append("lasttime>='").append(end).append("'");

			iCount = dbHelper.GetCount("useronline", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static String getVisitorUser(String clientid) {
		DBHelper dbHelper = null;
		String rest = "";
		List<Map<String, Object>> list;
		try {
			dbHelper = DBHelper.GetInstance();
			list = dbHelper.ExecuteQuery("username", "visitor",
					"clientid = '" + dbHelper.FilterSpecialCharacter(clientid) + "'");
			if (list == null || list.size() == 0)
				return "";
			rest = list.get(0).get("username").toString();
		} catch (Exception e) {

		}
		return rest;
	}

	public static boolean addVisitorUser(String clientid, String userName) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("visitor", "clientid = '" + dbHelper.FilterSpecialCharacter(clientid) + "'") == 0) {
				bRet = dbHelper.Insert("visitor", new String[] { "clientid", "username", "time" },
						new Object[] { clientid, userName, Common.GetDateTime() });
			} else {
				bRet = true;
			}
		} catch (Exception e) {

		}
		return bRet;
	}

	public static String visitorLogin(String username, String password, String appid, String client, String version,
			String clientid, String ip) {
		String[] arrUserToken = new String[] { "" };
		if (!checkOnlineCount(appid, username, client, clientid, ip, arrUserToken)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLIENTOVERFULL.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLIENTOVERFULL.code)).concat("}");
		}
		String strUserToken = "";
		boolean bOnline = !Common.IsNullOrEmpty(arrUserToken[0]);
		if (bOnline) {
			strUserToken = arrUserToken[0];
		} else {
			strUserToken = CreateUserToken(username);
		}
		UserInfoBean userInfo = new UserInfoBean();
		if (Common.IsNullOrEmpty(password)) {
			userInfo.setPassWord(getUserPassword(username));
		} else {
			userInfo.setPassWord(password);
		}
		userInfo.setAddress(ip);
		userInfo.setAppid(appid);
		userInfo.setClientID(clientid);
		userInfo.setVersion(version);
		userInfo.setUserName(username);
		if (!setUserInfo(userInfo, strUserToken, bOnline, 1)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
		return "{\"result\":true,\"usertoken\":\"" + strUserToken + "\",\"username\":\"" + username + "\"}";
	}

	public static List<Map<String, Object>> sugUserName(String keyword) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> list = null;
		try {
			dbHelper = DBHelper.GetInstance();
			list = dbHelper.ExecuteQuery("username", "userinfo", "username like '" + keyword + "%'", "updatetime desc", 1, 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
