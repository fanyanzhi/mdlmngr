package BLL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Util.Common;
import DAL.DBHelper;

public class ZjclsMngr {
	public static boolean addAttention(String userName, String code) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userzjcls", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			if (checkAttention(userName, code).equals("0")) {
				bRet = dbHelper.Insert(strTableName, new String[] { "username", "sortcode", "time" }, new Object[] { userName, code, Common.GetDateTime() });
			} else {
				bRet = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}
	
	public static String checkAttention(String userName, String code) {
		String strRet = "0";
		DBHelper dbHelper = null;
		String strTableName = getTableName("userzjcls", userName);
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
	
	public static boolean cancelAttention(String userName, String code) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userzjcls", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(strTableName, "username='" + dbHelper.FilterSpecialCharacter(userName) + "' and sortcode='" + dbHelper.FilterSpecialCharacter(code) + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}
	
	public static int getAtionSubjectCount(String userName) {
		int iCount = 0;
		String strTableName = getTableName("userzjcls", userName);
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(strTableName, "username ='" + dbHelper.FilterSpecialCharacter(userName) + "'");
		} catch (Exception e) {
			// Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getAtionSubjectList(String userName) {
		String strTableName = getTableName("userzjcls", userName);
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
	
	public static int getSubjectCount(String condition) {
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("zjcls", Common.IsNullOrEmpty(condition) ? "grade =3 " : "grade =3 and name like '%" + dbHelper.FilterSpecialCharacter(condition) + "%'");
		} catch (Exception e) {
			// Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getSubjectList(String condition, String order, int iStart, int iLength) {
		List<Map<String, Object>> lstSubject = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubject = dbHelper.ExecuteQuery("id,code,name,ishavechild,grade,parentcode,pathname", "zjcls", Common.IsNullOrEmpty(condition) ? "" : "grade =3 and name like '%" + dbHelper.FilterSpecialCharacter(condition) + "%'", Common.IsNullOrEmpty(order) ? "" : order, iStart, iLength);
		} catch (Exception e) {
			// Logger.WriteException(e);
		}
		return lstSubject;
	}
	
	public static Map<String, String> getRelevantZjcls(String Code) {
		List<Map<String, Object>> lstSubZjcls = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubZjcls = dbHelper.ExecuteQuery("select code,name from zjcls where parentcode = (select parentcode from zjcls where code='" + dbHelper.FilterSpecialCharacter(Code) + "')");
		} catch (Exception e) {
		}
		Map<String, String> mapRelZjcls = new HashMap<String, String>();
		if (lstSubZjcls != null && lstSubZjcls.size() > 0) {
			for (Map<String, Object> map : lstSubZjcls) {
				if (!Code.equals(map.get("code").toString())) {
					mapRelZjcls.put(map.get("code").toString(), map.get("name").toString());
				}
			}
		}
		return mapRelZjcls;
	}
	
	public static void setZjclsAtion(String code) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			dbHelper.ExecuteSql("update zjcls set count= count+1 where `code`='" + code + "'");
		} catch (Exception e) {
		}
	}
	
	public static List<Map<String,Object>> getHotZjcls(){
		List<Map<String, Object>> lstHotZjcls = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstHotZjcls = dbHelper.ExecuteQuery("select code,name,pathname,count from zjcls where grade = 3 order by count desc limit 0,8");
		} catch (Exception e) {
		}
		return lstHotZjcls;
	}
	
	public static String getCodeByName(String name){
		List<Map<String, Object>> lstHotZjcls = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstHotZjcls = dbHelper.ExecuteQuery("code", "zjcls", "name = '".concat(name).concat("'"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(lstHotZjcls!=null&&lstHotZjcls.size()>0){
			return (String) lstHotZjcls.get(0).get("code");
		}
		return null;
	}
	public static String getNameByCode(String code){
		List<Map<String, Object>> lstHotZjcls = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstHotZjcls = dbHelper.ExecuteQuery("name", "zjcls", "code = '".concat(code).concat("'"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(lstHotZjcls!=null&&lstHotZjcls.size()>0){
			return (String) lstHotZjcls.get(0).get("name");
		}
		return null;
	}
	private static String getTableName(String SourceTable, String UserName) {
		if(Common.IsNullOrEmpty(UserName))
			return SourceTable.concat("0");
		return SourceTable.concat(String.valueOf(Math.abs(Common.EnCodeMD5(UserName).hashCode())).substring(0, 1));

	}
	public static int getZjclsCount(String sortcode, String UserName, String StartDate, String EndDate) {
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
		arrParam[0] = "userzjcls";
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

	public static List<Map<String, Object>> getZjclsList(String sortcode,String UserName, String StartDate, String EndDate, int Start, int Length) {
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
		arrParam[0] = "userzjcls";
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
		return lstFile;
	}
	public static boolean delAttention(String userName, String id) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userzjcls", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(strTableName, "username='" + dbHelper.FilterSpecialCharacter(userName) + "' and id in('" + id.replace(",","','") + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}
	
	public static String getXueKeUserCount(String sortcode, String UserName, String StartDate, String EndDate) {
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
		int allCount = 0;
		
		List<Map<String, Object>> lstCount = null;
		for(int i = 0; i < 10; i++){
			try {
				lstCount = dbHelper.ExecuteQuery("count(DISTINCT username) cut", "userzjcls" + i, sbCondition.toString());
			} catch (Exception e) {
				
			} 
			allCount = allCount +Integer.parseInt(lstCount.get(0).get("cut").toString());
		}
		return String.valueOf(allCount);
	}
}
