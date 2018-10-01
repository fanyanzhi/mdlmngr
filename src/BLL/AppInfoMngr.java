package BLL;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.AuthDecrypt;
import Util.Common;

public class AppInfoMngr {
	static Map<String, String> appmap = new HashMap<String, String>();
	static Map<String, String> appsync = new HashMap<String, String>();
	static Map<String, String> appkey = new HashMap<String, String>();
	static Map<String, String> appauth = new HashMap<String, String>();
	static Map<String, String> appactivity = new HashMap<String, String>();

	static {
		List<Map<String, Object>> lstApp = getAppInfo();
		if (lstApp != null && lstApp.size() > 0) {
			Iterator<Map<String, Object>> itr = lstApp.iterator();
			Map<String, Object> mapType = null;
			while (itr.hasNext()) {
				mapType = itr.next();
				appmap.put(String.valueOf(mapType.get("appid")).toLowerCase(),
						String.valueOf(mapType.get("isfee")).toLowerCase());
				appsync.put(String.valueOf(mapType.get("appid")).toLowerCase(),
						String.valueOf(mapType.get("sync")).toLowerCase());
				appauth.put(String.valueOf(mapType.get("appid")).toLowerCase(),
						String.valueOf(mapType.get("auth")).toLowerCase());
				appkey.put(String.valueOf(mapType.get("appid")).toLowerCase(), String.valueOf(mapType.get("appkey")));
				appactivity.put(String.valueOf(mapType.get("appid")).toLowerCase(),
						String.valueOf(mapType.get("activity")));
			}
		}
	}

	public static boolean isFeeApp(String appID) {
		if (appmap.size() == 0 || !appmap.containsKey(appID)) {
			return true;
		} else {
			return "1".equals(appmap.get(appID));
		}
	}

	public static boolean isAuthUser(String appID) {
		if (appauth.size() == 0 || !appauth.containsKey(appID)) {
			return true;
		} else {
			return "1".equals(appauth.get(appID));
		}
	}

	/*
	 * public static boolean isActivityApp(String appID){
	 * if(appactivity.size()==0 ||!appactivity.containsKey(appID)){ return
	 * false; }else{ return "1".equals(appactivity.get(appID)); } }
	 */

	public static int isSync(String appID) {
		if (appsync.size() == 0 || !appsync.containsKey(appID))
			return 0;
		return Integer.parseInt(appsync.get(appID));
	}

	/**
	 * 
	 * @param appId
	 * @return
	 */
	public static boolean existAppId(String appId) {
		boolean isExist = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("appinfo", "appid = '".concat(appId).concat("'")) > 0)
				isExist = true;
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return isExist;
	}

	/**
	 * 添加app信息
	 * 
	 * @param appId
	 * @param appKey
	 * @param endDate
	 * @param comment
	 * @param status
	 * @return
	 */
	public static boolean addAppInfo(String appId, String appKey, String fee, String sync, String status, String auth,
			String activity, String roam, String comment) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("appinfo",
					new String[] { "appid", "appkey", "isfee", "sync", "status", "auth", "activity", "roam", "comment",
							"updatetime", "time" },
					new Object[] { appId, appKey, fee, sync, status, auth, activity, roam, comment,
							Common.GetDateTime(), Common.GetDateTime() });
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	/**
	 * 修改app信息
	 * 
	 * @param appId
	 * @param appKey
	 * @param endDate
	 * @param comment
	 * @param status
	 * @return
	 */
	public static boolean updateAppInfo(String appId, String appKey, String fee, String sync, String status,
			String auth, String activity, String roam, String comment) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("appinfo", "appid='".concat(dbHelper.FilterSpecialCharacter(appId)).concat("'"),
					new String[] { "appkey", "isfee", "sync", "status", "auth", "activity", "roam", "comment",
							"updatetime" },
					new Object[] { appKey, fee, sync, status, auth, activity, roam, comment, Common.GetDateTime() });
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	/**
	 * 获取app详细信息
	 * 
	 * @param id
	 * @return
	 */
	public static Map<String, Object> getAppInfo(String id) {
		Map<String, Object> mapAppInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			List<Map<String, Object>> lstInfo = dbHelper.ExecuteQuery(
					"appid,appkey,isfee,comment,status,sync,auth,activity,roam", "appinfo", "id=".concat(id));
			if (lstInfo != null && lstInfo.size() > 0)
				mapAppInfo = lstInfo.get(0);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return mapAppInfo;
	}

	/**
	 * 
	 * @param appId
	 * @return
	 */
	public static String getValidAppKey(String appId) {
		if (!appkey.containsKey(appId))
			return null;
		return appkey.get(appId);
		/*
		 * String appKey = ""; try { DBHelper dbHelper = DBHelper.GetInstance();
		 * List<Map<String, Object>> lstInfo = dbHelper.ExecuteQuery("appkey",
		 * "appinfo", "status=1 and appid = '".concat(appId).concat("'")); if
		 * (lstInfo != null && lstInfo.size() > 0) appKey =
		 * lstInfo.get(0).get("appkey").toString(); } catch (Exception e) {
		 * Logger.WriteException(e); } return appKey;
		 */
	}

	/**
	 * 获取app条数
	 * 
	 * @param appId
	 * @return
	 */
	public static int getAppInfoCount(String appId) {
		int iCount = 0;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (appId != null) {
			sbCondition.append("appid like '%").append(dbHelper.FilterSpecialCharacter(appId)).append("%'");
		}
		try {
			iCount = dbHelper.GetCount("appinfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	/**
	 * 获取app列表
	 * 
	 * @param appId
	 * @param Start
	 * @param Length
	 * @return
	 */
	public static List<Map<String, Object>> getAppInfoList(String appId, int Start, int Length) {
		List<Map<String, Object>> lstAppInfo = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (appId != null) {
			sbCondition.append("appid like '%").append(dbHelper.FilterSpecialCharacter(appId)).append("%'");
		}
		try {
			lstAppInfo = dbHelper.ExecuteQuery(
					"id,appid,appkey,isfee,status,sync,auth,activity,roam,comment,updatetime,time", "appinfo",
					sbCondition.toString(), "time desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAppInfo;
	}

	public static List<Map<String, Object>> getAppInfoList() {
		List<Map<String, Object>> lstAppInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAppInfo = dbHelper.ExecuteQuery("appid", "appinfo", "status=1");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAppInfo;
	}

	private static List<Map<String, Object>> getAppInfo() {
		List<Map<String, Object>> lstAppInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAppInfo = dbHelper.ExecuteQuery("appid,isfee,sync,auth,activity,roam,appkey", "appinfo", "status=1");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAppInfo;
	}

	/**
	 * 临时添加
	 * 
	 * @param appID
	 * @return
	 */
	public static boolean isActivityApp(String appID) {
		List<Map<String, Object>> lstAppInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAppInfo = dbHelper.ExecuteQuery("activity", "appinfo", "appid='".concat(appID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstAppInfo == null || lstAppInfo.size() == 0)
			return false;
		return "1".equals(lstAppInfo.get(0).get("activity").toString());
	}

	public static int getAppRoam(String appID) {
		List<Map<String, Object>> lstAppInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAppInfo = dbHelper.ExecuteQuery("roam", "appinfo", "appid='".concat(appID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstAppInfo == null || lstAppInfo.size() == 0)
			return 0;
		return Integer.parseInt(lstAppInfo.get(0).get("roam").toString());
	}

	/**
	 * 删除app信息
	 * 
	 * @param appId
	 * @return
	 */
	public static boolean delAppInfo(String appId) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("appinfo", "id in(".concat(dbHelper.FilterSpecialCharacter(appId)).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/**
	 * 
	 * @param AppInfo
	 * @param PlatForm
	 * @param ErrorInfo
	 * @return
	 */
	public static boolean setAppCrashInfo(String AppInfo, String PlatForm, String ErrorInfo) {
		boolean bResult = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Insert("apperror", new String[] { "appinfo", "platform", "errorinfo", "time" },
					new Object[] { AppInfo, PlatForm, ErrorInfo, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static Integer getAppCrashContentCount(String startDate, String endDate) {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("apperror", "time between '" + startDate + "' and '"
					+ dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(endDate, "yyyy-MM-dd", 24 * 3600 * 1000))
					+ "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	/*
	 * public static
	 * getAppCrashContentCount(strStartDate,strEndDate,iStart,iLength){
	 * 
	 * }
	 */

	public static List<Map<String, Object>> getAppCrashContentCount(String startDate, String endDate, int Start,
			int Length) {
		List<Map<String, Object>> lstAppCrash = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			/*
			 * if (UserName != null && UserName.length() > 0) {
			 * sbCondition.append (" where username like '%"
			 * ).append(dbHelper.FilterSpecialCharacter
			 * (UserName)).append("%'"); } lstOnlineUserName =
			 * dbHelper.ExecuteQuery("DISTINCT username",
			 * "(select username from useronline "
			 * .concat(sbCondition.toString()).concat(" ORDER BY id desc)tab1"),
			 * "", "", Start, Length);
			 */
			lstAppCrash = dbHelper.ExecuteQuery("id,appinfo,platform,errorinfo,time", "apperror",
					"time between '" + startDate + "' and '"
							+ dbHelper.FilterSpecialCharacter(
									Common.ConvertToDateTime(endDate, "yyyy-MM-dd", 24 * 3600 * 1000))
							+ "'",
					"time desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAppCrash;
	}

	public static List<Map<String, Object>> getAppCrashInfoByID(String id) {
		List<Map<String, Object>> lstAppCrash = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstAppCrash = dbHelper.ExecuteQuery("id,appinfo,platform,errorinfo,time", "apperror", "id = " + id);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAppCrash;
	}

	public static boolean delCrashRecord(String ACID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("apperror", "id in(".concat(dbHelper.FilterSpecialCharacter(ACID)).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static Map<String, Object> validAppAuth(String auth) {
		List<Map<String, Object>> lstAppAuth = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAppAuth = dbHelper.ExecuteQuery("password,package,jkshash", "appauth", "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAppAuth.get(0);
	}

	/**
	 * 插入AccessToken到数据库
	 * @param token
	 * @return
	 */
	public static boolean setAccessToken(String token) {
		DBHelper dbHelper = null;
		boolean ret = false;
		try {
			dbHelper = DBHelper.GetInstance();
			ret = dbHelper.Insert("apptoken", new String[] { "acctoken", "time" },
					new Object[] { token, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return ret;
	}

	public static int validAccToken(String token) {
		List<Map<String, Object>> lstAccToken = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAccToken = dbHelper.ExecuteQuery("time", "apptoken",
					"acctoken='" + dbHelper.FilterSpecialCharacter(token) + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstAccToken == null || lstAccToken.size() == 0) {
			return chkAccToken(token);
		}else{
			String time = lstAccToken.get(0).get("time").toString();
			long lastTime = Common.getMilliSeconds(time);
			long curTime = System.currentTimeMillis();
			if (((((curTime - lastTime) / 1000)) / 60) > 30) {
				try {
					dbHelper.Delete("apptoken", "acctoken='" + dbHelper.FilterSpecialCharacter(token) + "'");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}
		return 1;
		
	}

	/**
	 * 
	 * @param sign
	 * @return 1:可用
	 */
	private static int chkAccToken(String sign) {
		String decSign = "";
		decSign = AuthDecrypt.DecryptRequest(decSign, "/clientcert.jks", "123456", "readerex client", "123456");
		if (decSign != null) {
			String[] arrSign = decSign.split("|");
			if (arrSign.length == 4) {
				Map<String, Object> authMap = AppInfoMngr.validAppAuth("");
				if (arrSign[0].equals(authMap.get("password")) && arrSign[1].equals(authMap.get("package"))
						&& arrSign[2].equals(authMap.get("jkshash"))) {
					long curTime = System.currentTimeMillis();
					long clientTime = Long.parseLong(arrSign[3]);
					if (((((curTime - clientTime) / 1000)) / 60) < 10) {
						if (AppInfoMngr.setAccessToken(sign)) {
							return 1;
						} else {
							return SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code;
						}
					} else {
						return SysConfigMngr.ERROR_CODE.ERROR_TIMEOUT.code;
					}
				} else {
					return SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code;
				}
			} else {
				return SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code;
			}
		} else {
			return SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code;
		}

	}

}
