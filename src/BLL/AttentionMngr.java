package BLL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Util.Common;
import DAL.DBHelper;

public class AttentionMngr {
	private static String getTableName(String SourceTable, String UserName) {
		return SourceTable.concat(String.valueOf(Math.abs(Common.EnCodeMD5(UserName).hashCode())).substring(0, 1));

	}
	public static int addAttention(String appid,String username,String ationid,String title,String author,String type){
		DBHelper dbHelper = null;
		String strTableName = getTableName("userpublication", username);
		int iRet = -1;
		try {
			dbHelper = DBHelper.GetInstance();
			if(dbHelper.Insert(strTableName, new String[]{"username","ationid","title","appid","author","type","time"}, new Object[]{username,ationid,title,appid,author,type,Common.GetDateTime()})){
				List<Map<String, Object>> attInfoList = dbHelper.ExecuteQuery("max(id) id", strTableName, "username='"+username+"' and ationid='"+ationid+"'");
				iRet = Integer.parseInt(attInfoList.get(0).get("id").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return iRet;
	}
	
	public static boolean cancelAttention(String username,String ationid){
		DBHelper dbHelper = null;
		String strTableName = getTableName("userpublication", username);
		boolean result = false;
		try {
			dbHelper = DBHelper.GetInstance();
			result = dbHelper.Delete(strTableName, "username='"+username+"' and ationid in('"+ationid.replace(",", "','")+"')");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	
	
	public static Map<String,Integer> checkAttention(String username,String ationid ){
		DBHelper dbHelper = null;
		String strTableName = getTableName("userpublication", username);
		int result = -1;
		int iRet=-1;
		Map<String,Integer> map = new HashMap<String, Integer>();
		try {
			dbHelper = DBHelper.GetInstance();
			result=dbHelper.GetCount(strTableName, "username='".concat(username).concat("' and ationid='").concat(ationid).concat("'"));
			if(result>0){
				List<Map<String, Object>> attInfoList = dbHelper.ExecuteQuery("max(id) id", strTableName, "username='".concat(username).concat("' and ationid='").concat(ationid).concat("'"));
				iRet = Integer.parseInt(attInfoList.get(0).get("id").toString());
			}
			map.put("count", result);
			map.put("id", iRet);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return map;
	}
	
	
	public static int getUserAttentionCount(String username){
		DBHelper dbHelper = null;
		String strTableName = getTableName("userpublication", username);
		int result = 0;
		try {
			dbHelper = DBHelper.GetInstance();
			result=dbHelper.GetCount(strTableName, "username='".concat(username).concat("'"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	
	public static List<Map<String, Object>> getAttentionList(String username,int start,int length){
		String strTableName = getTableName("userpublication", username);
		List<Map<String, Object>> attentionInfoList = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			attentionInfoList = dbHelper.ExecuteQuery("id,username,ationid,title,author,type,time", strTableName, "username='".concat(username).concat("'"), "time desc", start, length);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return attentionInfoList;
	}
	public static boolean delAttention(String username,String id){
		String strTableName = getTableName("userpublication", username);
		boolean result = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			result = dbHelper.Delete(strTableName, "username='".concat(username).concat("' and id=").concat(id));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	public static boolean delAllAttention(String username,String ids){
		String strTableName = getTableName("userpublication", username);
		boolean result = false;
		DBHelper dbHelper = null;
		if (ids.startsWith("[") && ids.endsWith("]")) {
			ids = ids.substring(1, ids.length() - 1).trim();
		}
		try {
			dbHelper = DBHelper.GetInstance();
			result = dbHelper.Delete(strTableName, "id in('" + ids.replace(",","','") + "')");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	public static int getAttentionCount(String appID,String FileName, String UserName, String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if(!Common.IsNullOrEmpty(appID)){
			sbCondition.append("appid = '").append(dbHelper.FilterSpecialCharacter(appID)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(FileName)) {
			sbCondition.append("title like '%").append(dbHelper.FilterSpecialCharacter(FileName)).append("%' and ");
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
		arrParam[0] = "userpublication";
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

	public static List<Map<String, Object>> getAttentionFileList(String appID, String FileName, String UserName, String StartDate, String EndDate, int Start, int Length) {
		List<Map<String, Object>> lstFile = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if(!Common.IsNullOrEmpty(appID)){
			sbCondition.append("appid = '").append(dbHelper.FilterSpecialCharacter(appID)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(FileName)) {
			sbCondition.append("title like '%").append(dbHelper.FilterSpecialCharacter(FileName)).append("%' and ");
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
		arrParam[0] = "userpublication";
		arrParam[1] = "id,username,ationid,title,appid,author,type,time";
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
	
	public static String getChuBanWuUserCount(String FileName, String UserName, String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (!Common.IsNullOrEmpty(FileName)) {
			sbCondition.append("title like '%").append(dbHelper.FilterSpecialCharacter(FileName)).append("%' and ");
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
				lstCount = dbHelper.ExecuteQuery("count(DISTINCT username) cut", "userpublication" + i, sbCondition.toString());
			} catch (Exception e) {
				
			} 
			allCount = allCount +Integer.parseInt(lstCount.get(0).get("cut").toString());
		}
		return String.valueOf(allCount);
	}
}
