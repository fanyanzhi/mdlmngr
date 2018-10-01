package BLL;

import java.util.List;
import java.util.Map;




import Util.Common;
import DAL.DBHelper;

public class ProjectMngr {
	
	public static String checkAttention(String userName, String code) {
		String strRet = "0";
		DBHelper dbHelper = null;
		String strTableName = getTableName("userproject", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount(strTableName, "username = '".concat(userName).concat("' and sortcode='").concat(code).concat("'")) > 0) {
				strRet = "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strRet;
	}
	
	public static boolean addAttention(String userName, String code) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userproject", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			if(checkAttention(userName,code).equals("0")){
				bRet = dbHelper.Insert(strTableName, new String[] { "username", "sortcode", "time" }, new Object[] { userName, code, Common.GetDateTime() });
			}else{
				bRet = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}
	
	public static Map<String, Object> getLastRecord(String userName) {
		DBHelper dbHelper = null;
		String strTableName = getTableName("userproject", userName);
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("id, time", strTableName,
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'", "id desc", 1, 1);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (lst != null && lst.size() > 0)
			return lst.get(0);
		return null;
	}

	public static boolean delAttention(String userName, String id) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userproject", userName);
		if (id.startsWith("[") && id.endsWith("]")) {
			id = id.substring(1, id.length() - 1).trim();
		}
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(strTableName,"id in('" + id.replace(",","','") + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}
	
	public static int getAtionSubjectCount(String userName) {
		int iCount = 0;
		String strTableName = getTableName("userproject", userName);
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(strTableName, "username ='" + dbHelper.FilterSpecialCharacter(userName) + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iCount;
	}
	
	public static List<Map<String, Object>> getAtionSubjectList(String userName, int iStart, int iLength) {
		String strTableName = getTableName("userproject", userName);
		List<Map<String, Object>> lstAtionSubject = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAtionSubject = dbHelper.ExecuteQuery("id,sortcode,name,pathname,time", "(select tab1.id,tab1.sortcode,tab1.time,tab2.`name`,tab2.pathname from " + strTableName + " tab1 LEFT JOIN zjcls tab2 ON tab1.sortcode=tab2.`code`  where tab1.username='" + userName + "')tab", "", "time desc");
		} catch (Exception e) {
			// Logger.WriteException(e);
		}
		return lstAtionSubject;
	}
	
	private static String getTableName(String SourceTable, String UserName) {
		if(Common.IsNullOrEmpty(UserName))
			return SourceTable.concat("0");
		return SourceTable.concat(String.valueOf(Math.abs(Common.EnCodeMD5(UserName).hashCode())).substring(0, 1));

	}
	public static int getProjectAllCount(String sortcode, String UserName, String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (!Common.IsNullOrEmpty(sortcode)) {
			sbCondition.append("sortcode = '").append(dbHelper.FilterSpecialCharacter(sortcode)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
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
		Object[] arrParam = new Object[3];
		arrParam[0] = "userproject";
		arrParam[1] = sbCondition.toString();
		arrParam[2] ="cloudtable";

		List<Map<String, Object>> lstDownloadList = null;
		try {
			lstDownloadList = dbHelper.ExecuteQueryProc("sp_getDataCount", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			return 0;
		}
		arrParam = null;
		if (lstDownloadList == null) {
			return 0;
		}
		return Integer.valueOf(lstDownloadList.get(0).get("totalcount").toString());

	}

	public static List<Map<String, Object>> getProjectList(String sortcode,String UserName, String StartDate, String EndDate, int Start, int Length) {
		List<Map<String, Object>> lstFile = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if(!Common.IsNullOrEmpty(sortcode)){
			sbCondition.append("sortcode = '").append(dbHelper.FilterSpecialCharacter(sortcode)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
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
		arrParam[0] = "userproject";
		arrParam[1] = "id,username,sortcode,time";
		arrParam[2] = sbCondition.toString();
		arrParam[3] = "cloudtable";
		arrParam[4] = Start;
		arrParam[5] = Length;
		try {
			lstFile = dbHelper.ExecuteQueryProc("sp_getDataRecord", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		arrParam = null;
		// lstFile =
		// dbHelper.ExecuteQuery("id,username,filename,fileid,client,address,time",
		// "downloadinfo", sbCondition.toString(), "time desc", Start, Length);

		return lstFile;
	}
	
	public static String getProjectUserCount(String sortcode, String UserName, String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if(!Common.IsNullOrEmpty(sortcode)){
			sbCondition.append("sortcode = '").append(dbHelper.FilterSpecialCharacter(sortcode)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
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
		int allCount = 0;
		
		List<Map<String, Object>> lstCount = null;
		for(int i = 0; i < 10; i++){
			try {
				lstCount = dbHelper.ExecuteQuery("count(DISTINCT username) cut", "userproject" + i, sbCondition.toString());
			} catch (Exception e) {
				
			} 
			allCount = allCount +Integer.parseInt(lstCount.get(0).get("cut").toString());
		}
		return String.valueOf(allCount);
	}
}
