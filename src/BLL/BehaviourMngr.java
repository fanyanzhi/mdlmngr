package BLL;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import DAL.DBHelper;
import Model.HttpContext;
import Util.Common;

public class BehaviourMngr {

	private static int interval = Integer.parseInt(Common.GetConfig("interval"));

	public static String addBrowseInfo(String UserName, Map<String, Object> mapInfo) {
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		String strTableName = getTableName("browseinfo", String.valueOf(mapInfo.get("filename")));

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.Insert(strTableName, new String[] { "username", "client", "clientid", "address", "time", "filename", "filetype" }, new Object[] { UserName, strAgent, "", strClientAddr, Common.GetDateTime(), mapInfo.get("filename"), mapInfo.get("filetype") });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	/**
	 * 
	 * @param UserName
	 * @param FileName
	 * @param FileType
	 * @return
	 */
	public static String addDownloadInfo(String appID, String UserName, String FileName, String FileType, String TypeID, String FileID) {
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		String strTableName = getTableName("downloadinfo", UserName);

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.Insert(strTableName, new String[] { "appid", "username", "client", "clientid", "address", "time", "filename", "filetype", "typeid", "fileid" }, new Object[] { appID, UserName, strAgent, "", strClientAddr, Common.GetDateTime(), FileName, FileType, TypeID, FileID });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	public static String addReadInfo(String UserName, Map<String, Object> mapInfo) {
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		String strTableName = getTableName("readinfo", String.valueOf(mapInfo.get("filename")));

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.Insert(strTableName, new String[] { "username", "client", "clientid", "address", "time", "filename", "filetype", "timespan" }, new Object[] { UserName, strAgent, "", strClientAddr, Common.GetDateTime(), mapInfo.get("filename"), mapInfo.get("filetype"), mapInfo.get("timespan") });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	public static String addSearchInfo(String UserName, Map<String, Object> mapInfo) {
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		String strTableName = getTableName("searchinfo", String.valueOf(mapInfo.get("keywords")));

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.Insert(strTableName, new String[] { "username", "client", "clientid", "address", "time", "keywords", "type" }, new Object[] { UserName, strAgent, "", strClientAddr, Common.GetDateTime(), mapInfo.get("keywords"), mapInfo.get("typeid") });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	public static String addUploadInfo(String UserName, String FileName, String FileType) {
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		String strTableName = getTableName("uploadinfo", FileName);

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.Insert(strTableName, new String[] { "username", "client", "clientid", "address", "time", "filename", "filetype" }, new Object[] { UserName, strAgent, "", strClientAddr, Common.GetDateTime(), FileName, FileType });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	/**
	 * 添加cajviewer使用情况信息
	 * 
	 * @param UserName
	 * @param FileName
	 * @param FileType
	 * @return
	 */
	public static String addUseInfo() {
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		String strTableName = getTableName("useinfo", strClientAddr);

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.Insert(strTableName, new String[] { "address", "client", "time" }, new Object[] { strClientAddr, strAgent, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	public static String addKeepAlive(String UserName, String ClientID, String PlatForm, String AppInfo, int Status) {
		boolean bolRet = false;
		String strTableName = getTableName("useralive", UserName);

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.Insert(strTableName, new String[] { "username", "clientid", "platform", "appinfo", "status", "time" }, new Object[] { UserName, ClientID, PlatForm, AppInfo, Status, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true,\"interval\":" + interval + "}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	public static String updateKeepAlive(String UserName, String ClientID, String PlatForm, String AppInfo) {
		boolean bolRet = false;
		String strTableName = getTableName("useralive", UserName);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = format.format(new Date());
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			List<Map<String, Object>> lstKeepAlive = dbHelper.ExecuteQuery("id,time,`interval`", strTableName, "clientid='".concat(ClientID).concat("' ORDER BY time desc limit 1"));
			if (lstKeepAlive != null && lstKeepAlive.size() > 0) {
				Map<String, Object> mapKeepAlive = lstKeepAlive.get(0);
				if (mapKeepAlive.get("id") != null && mapKeepAlive.get("interval") == null) {
					bolRet = dbHelper.Update(strTableName, "id=".concat(mapKeepAlive.get("id").toString()), new String[] { "`interval`" }, new Object[] { Common.GetTimeDiff(mapKeepAlive.get("time").toString(), strDate) / 1000 });
				}
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true,\"interval\":" + interval + "}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	public static List<Map<String, Object>> getHeartBeatValue(String ClientID, String Sign) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lstHeartBeat = null;
		String strTableName = getTableName("heartbeat", ClientID);
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			lstHeartBeat = dbHelper.ExecuteQuery("id,time,`interval`", strTableName, "clientid='".concat(ClientID).concat("' and sign=").concat(Sign).concat(" ORDER BY time desc limit 1"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstHeartBeat;
	}

	public static String addHeartBeat(String UserName, String ClientID, String PlatForm, String AppInfo, String IP, String appId, long Sign, int Status) {
		boolean bolRet = false;
		String strTableName = getTableName("heartbeat", ClientID);
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.Insert(strTableName, new String[] { "username", "clientid", "platform", "appinfo", "address", "appId", "sign", "status", "time" }, new Object[] { UserName, ClientID, PlatForm, AppInfo, Common.ipToLong(IP), appId, Sign, Status, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true,\"interval\":" + interval + "}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	public static String updateHeartBeat(String UserName, String ClientID, String Sign) {
		boolean bolRet = false;
		String strTableName = getTableName("heartbeat", ClientID);
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("update ").append(strTableName).append(" set ");
		if (!Common.IsNullOrEmpty(UserName)) {
			sbSql.append(" username = '").append(UserName).append("',");
		}
		sbSql.append(" `interval` = TIMESTAMPDIFF(SECOND,time,now()) where clientid='").append(ClientID).append("' and sign=").append(Sign);
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.ExecuteSql(sbSql.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true,\"interval\":" + interval + "}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	/**
	 * 
	 * @param UserName
	 * @param ClientID
	 * @param StartDate
	 * @param EndDate
	 * @return
	 */
	public static int getHeartBeatCount(String UserName, String ClientID, String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (UserName != null) {
			sbCondition.append("username like '").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
		}
		if (ClientID != null) {
			sbCondition.append("clientid = '").append(dbHelper.FilterSpecialCharacter(ClientID)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(StartDate)) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(EndDate)) {
			sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000))).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		// iCount = dbHelper.GetCount("downloadinfo", sbCondition.toString());

		Object[] arrParam = new Object[3];
		String BehaviorStart = Common.GetConfig("behaviorstart");
		arrParam[0] = BehaviorStart==null?"2015-01-02":BehaviorStart;
		arrParam[1] = "heartbeat";
		arrParam[2] = sbCondition.toString();

		List<Map<String, Object>> lstHeartBeatList = null;
		try {
			lstHeartBeatList = dbHelper.ExecuteQueryProc("up_GetDataCount", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			return 0;
		}
		arrParam = null;
		if (lstHeartBeatList == null) {
			return 0;
		}
		return Integer.valueOf(lstHeartBeatList.get(0).get("totalcount").toString());

	}

	/**
	 * 
	 * @param UserName
	 * @param ClientID
	 * @param StartDate
	 * @param EndDate
	 * @param Start
	 * @param Length
	 * @return
	 */
	public static List<Map<String, Object>> getHeartBeatList(String UserName, String ClientID, String StartDate, String EndDate, int Start, int Length) {
		List<Map<String, Object>> lstFile = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (UserName != null) {
			sbCondition.append("username like '").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
		}
		if (ClientID != null) {
			sbCondition.append("clientid = '").append(dbHelper.FilterSpecialCharacter(ClientID)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(StartDate)) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(EndDate)) {
			sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000))).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[6];
		String BehaviorStart = Common.GetConfig("behaviorstart");
		arrParam[0] = BehaviorStart==null?"2015-01-02":BehaviorStart;
		arrParam[1] = "heartbeat";
		arrParam[2] = "username,clientid,appinfo,platform,address,`interval`,time";
		arrParam[3] = sbCondition.toString();
		arrParam[4] = Start;
		arrParam[5] = Length;
		try {
			lstFile = dbHelper.ExecuteQueryProc("up_GetData", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		arrParam = null;
		// lstFile =
		// dbHelper.ExecuteQuery("id,username,filename,fileid,client,address,time",
		// "downloadinfo", sbCondition.toString(), "time desc", Start, Length);

		return lstFile;
	}

	private static String getTableName(String SourceTable, String TabSign) {
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
			return "download";
		}
		if (Common.IsNullOrEmpty(TabSign)) {
			return SourceTable.concat(strYear).concat(strTablePost).concat("1");
		} else {
			return SourceTable.concat(strYear).concat(strTablePost).concat(String.valueOf(Math.abs(TabSign.hashCode())).substring(0, 1));
		}

	}

	public static List<Map<String, Object>> getAliveUserInTime() {
		List<Map<String, Object>> lstFile = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			lstFile = dbHelper.ExecuteQueryProc("up_curUserAlive", null);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstFile;
	}

	public static List<Map<String, Object>> getUserAliveList(String StartDate, String EndDate) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		sbCondition.append("tablename = 'useralive' and ");
		if (StartDate != null) {
			sbCondition.append("date >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("date <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 86400000))).append("' and ");
		}
		String strCondition = "";
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			strCondition = " where ".concat(sbCondition.toString());
		}

		String strSql = "select date,count,time from summarys ".concat(strCondition).concat(" order by date");
		List<Map<String, Object>> lstInfo = null;
		try {
			lstInfo = dbHelper.ExecuteQuery(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		return lstInfo;
	}

	public static List<Map<String, Object>> getAliveCount(String StartDate, String EndDate) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 86400000))).append("' and ");
		}
		String strCondition = "";
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			strCondition = " where ".concat(sbCondition.toString());
		}

		String strSql = "select count(1) count, left(time,".concat("13").concat(") date from useralive1501035 ").concat(strCondition).concat(" GROUP BY date order by time");
		List<Map<String, Object>> lstInfo = null;
		try {
			lstInfo = dbHelper.ExecuteQuery(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		return lstInfo;
	}

	public static List<Map<String, Object>> getHeartBeat(String startDate, String endDate) {

		List<Map<String, Object>> lstLogInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			lstLogInfo = dbHelper.ExecuteQuery("usernamecount,clientidcount,date", "heartbeatstatis", "date between '" + startDate + "' and '" + endDate + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstLogInfo == null) {
			return null;
		} else {
			return lstLogInfo;
		}

	}

	public static int getHeartBeatClientidCount(String ClientID, String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (ClientID != null) {
			sbCondition.append("clientid = '").append(dbHelper.FilterSpecialCharacter(ClientID)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(StartDate)) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(EndDate)) {
			sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000))).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		String tablenameTail = String.valueOf(Math.abs(ClientID.hashCode())).substring(0, 1);
		Object[] arrParam = new Object[4];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date sDate = null;
		Date iDate = null;
		try {
			if (StartDate != null && !"".equals(StartDate)) {
				sDate = sdf.parse(StartDate);
			}
			iDate = sdf.parse("2015-01-02");
		} catch (ParseException e1) {
			Logger.WriteException(e1);
		} finally {
			if (sDate != null && sDate.before(iDate)) {
				arrParam[0] = StartDate;
			} else {
				arrParam[0] = "2015-01-02";
			}
		}
		arrParam[1] = "heartbeat";
		arrParam[2] = tablenameTail;
		arrParam[3] = sbCondition.toString();
		List<Map<String, Object>> lstHeartBeatList = null;
		try {
			lstHeartBeatList = dbHelper.ExecuteQueryProc("up_GetbeatDataCount", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			e.printStackTrace();
			return 0;
		}
		arrParam = null;
		if (lstHeartBeatList == null) {
			return 0;
		}
		return Integer.valueOf(lstHeartBeatList.get(0).get("totalcount").toString());

	}

	public static List<Map<String, Object>> getHeartBeatClientidList(String ClientID, String StartDate, String EndDate, int Start, int Length) {
		List<Map<String, Object>> lstFile = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (ClientID != null) {
			sbCondition.append("clientid = '").append(dbHelper.FilterSpecialCharacter(ClientID)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(StartDate)) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(EndDate)) {
			sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000))).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		String tablenameTail = String.valueOf(Math.abs(ClientID.hashCode())).substring(0, 1);
		Object[] arrParam = new Object[7];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date sDate = null;
		Date iDate = null;
		try {
			if (StartDate != null && !"".equals(StartDate)) {
				sDate = sdf.parse(StartDate);
			}
			iDate = sdf.parse("2015-01-02");
		} catch (ParseException e1) {
			Logger.WriteException(e1);
		} finally {
			if (sDate != null && sDate.before(iDate)) {
				arrParam[0] = StartDate;
			} else {
				arrParam[0] = "2015-01-02";
			}
		}
		arrParam[1] = "heartbeat";
		arrParam[2] = tablenameTail.toString();
		arrParam[3] = "username,clientid,appinfo,platform,address,`interval`,time";
		arrParam[4] = sbCondition.toString();
		arrParam[5] = Start;
		arrParam[6] = Length;
		try {
			lstFile = dbHelper.ExecuteQueryProc("up_GetHeartBeatData", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		arrParam = null;
		// lstFile =
		// dbHelper.ExecuteQuery("id,username,filename,fileid,client,address,time",
		// "downloadinfo", sbCondition.toString(), "time desc", Start, Length);

		return lstFile;
	}
	
	
	public static String addAppStatist(String userName, String clientId, String platForm, String version, String ip, String appId, int waittime) {
		boolean bolRet = false;
		String strTableName = getTableName("appstatist", userName);
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bolRet = dbHelper.Insert(strTableName, new String[] { "username", "clientid", "platform", "version", "address", "appId", "waittime", "time" }, new Object[] { userName, clientId, platForm, version, Common.ipToLong(ip), appId, waittime, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true" + "}";
		} else {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}
	
}
