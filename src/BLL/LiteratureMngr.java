package BLL;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import DAL.DBHelper;
import Model.HttpContext;
import Util.Common;

public class LiteratureMngr {
	public static String addBrowseInfo(String appID, String UserName, Map<String, Object> mapInfo) {
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		String strTableName = getTableName("userbrowse", UserName);
		String odatatype = mapInfo.get("odatatype") == null ? "" : mapInfo.get("odatatype").toString();
		String keyword = mapInfo.get("keyword") == null ? "" : mapInfo.get("keyword").toString();
		String date = mapInfo.get("date") == null ? "" : mapInfo.get("date").toString();
		String fileid = mapInfo.get("fileid") == null ? "" : mapInfo.get("fileid").toString();
		int flag = mapInfo.get("flag") == null ? 0 : Integer.parseInt(mapInfo.get("flag").toString());
		DBHelper dbHelper = null;
		String recid = "";
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			if (dbHelper.GetCount(strTableName, "username='".concat(UserName).concat("' and odatatype = '")
					.concat(odatatype).concat("' and fileid='").concat(fileid).concat("' and isdelete=0")) > 0) {
				bolRet = dbHelper.Update(strTableName,
						"username='".concat(UserName).concat("' and odatatype = '").concat(odatatype)
								.concat("' and fileid='").concat(fileid).concat("' and isdelete=0"),
						new String[] { "time" }, new Object[] { Common.GetDateTime() });
			} else {
				bolRet = dbHelper.Insert(strTableName,
						new String[] { "username", "odatatype", "fileid", "title", "scholar", "date", "keyword",
								"client", "clientid", "address", "appid", "flag", "time" },
						new Object[] { UserName, odatatype, fileid,
								mapInfo.get("title") == null ? "" : mapInfo.get("title"),
								mapInfo.get("scholar") == null ? "" : mapInfo.get("scholar"), date, keyword, strAgent,
								"", strClientAddr, appID, flag, Common.GetDateTime() });
			}
			List<Map<String, Object>> lstZuji = dbHelper.ExecuteQuery("id", strTableName,
					"username='".concat(UserName).concat("' and odatatype = '").concat(odatatype)
							.concat("' and fileid='").concat(fileid).concat("' and isdelete=0"));
			if (lstZuji != null) {
				recid = lstZuji.get(0).get("id").toString();
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			return "{\"result\":true,\"id\":" + recid + "}";
		} else {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code))
					.concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
		}
	}

	// public static String getBrowseInfo(String UserName, ) {
	// boolean bolRet = false;
	// HttpServletRequest request = HttpContext.GetRequest();
	// String strAgent = request.getHeader("User-Agent");
	// String strClientAddr = Common.getClientIP(request);
	// String strTableName = getTableName("userbrowse", UserName);
	//
	// DBHelper dbHelper = null;
	// try {
	// dbHelper = DBHelper.GetInstance("Behaviour");
	// bolRet = dbHelper.Insert(strTableName, new String[] { "username",
	// "odatatype", "fileid", "title", "scholar", "client", "clientid",
	// "address", "time" }, new Object[] { UserName, mapInfo.get("odatatype") ==
	// null ? "" : mapInfo.get("odatatype"), mapInfo.get("fileid") == null ? ""
	// : mapInfo.get("fileid"), mapInfo.get("title") == null ? "" :
	// mapInfo.get("title"), mapInfo.get("scholar") == null ? "" :
	// mapInfo.get("scholar"), strAgent, "", strClientAddr, Common.GetDateTime()
	// });
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// }
	// if (bolRet) {
	// return "{\"result\":true}";
	// } else {
	// return
	// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code)).concat("}");
	// }
	// }

	public static List<Map<String, Object>> getBrowseInfoList(String username) {
		DBHelper dbHelper = null;
		String strTableName = getTableName("userbrowse", username);
		List<Map<String, Object>> browseInfoList = null;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_MONTH, -10);
			Date date = calendar.getTime();
			DateFormat dateFormat = DateFormat.getDateInstance();
			String hisTime = dateFormat.format(date);
			dbHelper = DBHelper.GetInstance("Behaviour");
			browseInfoList = dbHelper.ExecuteQuery("id,title,fileid,odatatype,scholar,date,time", strTableName,
					"username='" + username + "' and time > '" + hisTime + "' and isdelete = 0 ", "time desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return browseInfoList;
	}

	public static boolean delAllBrowse(String username, String id) {
		DBHelper dbHelper = null;
		String strTableName = getTableName("userbrowse", username);
		boolean result = false;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			result = dbHelper.Update(strTableName, "username='" + username + "' and id in (".concat(id).concat(")"),
					new String[] { "isdelete" }, new Object[] { 1 });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 添加odata检索日志
	public static void addSearchInfo(String appID, Map<String, Object> mapInfo) {
		HttpServletRequest request = HttpContext.GetRequest();
		String ip = Common.getClientIP(request);
		String username = mapInfo.containsKey("username")
				? mapInfo.get("username") == null ? "" : mapInfo.get("username").toString() : "";
		String strTableName = getTableName("usersearch", username);
		String type = mapInfo.get("odatatype") == null ? "" : mapInfo.get("odatatype").toString();
		String seaexpression = mapInfo.get("seaexpression") == null ? "" : mapInfo.get("seaexpression").toString();
		String seaorder = mapInfo.get("seaorder") == null ? "" : mapInfo.get("seaorder").toString();
		String count = mapInfo.get("count") == null ? "" : mapInfo.get("count").toString();
		String sign = mapInfo.containsKey("sign") ? mapInfo.get("sign") == null ? "0" : mapInfo.get("sign").toString()
				: "0";
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			dbHelper.Insert(strTableName,
					new String[] { "username", "type", "seaexpression", "seaorder", "ip", "count", "time", "appid",
							"sign" },
					new Object[] { username, type, seaexpression, seaorder, ip, count, Common.GetDateTime(), appID,
							sign });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}

	public static List<Map<String, Object>> getDaySearchCount(String startDate, String endDate) {

		List<Map<String, Object>> lstSearchInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			lstSearchInfo = dbHelper.ExecuteQuery("count,spottime", "usersearchstatis",
					"spottime between '" + startDate + "' and '" + endDate + "'", "spottime asc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstSearchInfo == null) {
			return null;
		} else {
			return lstSearchInfo;
		}
	}

	public static Map<String, Object> getBrowseStatis(String startDate, String endDate) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		List<Map<String, Object>> lstLogInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			lstLogInfo = dbHelper.ExecuteQuery("count,spottime", "userbrowsestatis",
					"spottime between '" + startDate + "' and '" + endDate + "'", "spottime asc");
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

	private static String getTableName(String SourceTable, String UserName) {
		if (Common.IsNullOrEmpty(UserName))
			return SourceTable + "0";
		return SourceTable.concat(String.valueOf(Math.abs(Common.EnCodeMD5(UserName).hashCode())).substring(0, 1));

	}

	public static boolean addModuleInfo(String username, String type, String baseos) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			bRet = dbHelper.Insert("appmoduleinfo", new String[] { "username", "type", "baseos", "time" },
					new Object[] { username, type, baseos, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	public static List<Map<String, Object>> getHotLiterature(String code) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(code)) {
			String[] arrcode = code.split(",");
			for (int i = 0; i < arrcode.length; i++) {
				sbCondition.append("code like '%").append(arrcode[i]).append("%' or ");
			}
		}
		if (sbCondition.length() > 0)
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			lst = dbHelper.ExecuteQuery(
					"fileid, typeid, filename, author, date, year, issue, code, source, sourcech, factor, time",
					"hotliterature", sbCondition.toString(), "factor desc", 1, 20);
			if (lst == null || lst.size() < 20) {
				lst = dbHelper.ExecuteQuery(
						"fileid, typeid, filename, author, date, year, issue, code, source, sourcech, factor, time",
						"hotliterature", "", "factor desc", 1, 20);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lst;
	}
	
	public static boolean addScholarBrowse(String username, String code){
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			String tablename = getTableName("scholarbrowse",code);
			dbHelper = DBHelper.GetInstance("Behaviour");
			bRet = dbHelper.Insert(tablename, new String[] { "code", "username", "time" },
					new Object[] { code, username, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}
	
	public static int getScholarBrowseCount(String code){
		int ret = 0;
		DBHelper dbHelper = null;
		try {
			String tablename = getTableName("scholarbrowse",code);
			dbHelper = DBHelper.GetInstance("Behaviour");
			ret = dbHelper.GetCount(tablename, "code='"+code+"'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return ret;
	}
	
}
