package BLL;

import java.util.List;
import java.util.Map;
import DAL.DBHelper;
import Util.Common;

public class UserFirstLogMngr {

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
		System.out.println("co-->" + sbCondition.toString());
		try {
			iCount = dbHelper.GetCount("userfirstlogin", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;

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
		System.out.println("condition:" + sbCondition.toString());
		List<Map<String, Object>> lstRet = null;
		try {
			lstRet = dbHelper.ExecuteQuery("id,username,address,client,appid,version,time", "userfirstlogin",
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
			bResult = dbHelper.Delete("userfirstlogin", "id in (".concat(LogID).concat(")"));
			/*if (bResult) {
				updateLoginCount(UserName);
			}*/
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}
	
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
	
	public static List<Map<String, Object>> getAll(String appid, String UserName, String StartDate, String EndDate) {
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
		
		//System.out.println("condition:" + sbCondition.toString());
		List<Map<String, Object>> lstRet = null;
		try {
			lstRet = dbHelper.ExecuteQuery("id,username,address,client,appid,version,time", "userfirstlogin", sbCondition.toString(), "time desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRet;

	}
}
