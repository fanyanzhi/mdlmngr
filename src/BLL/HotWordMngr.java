package BLL;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;

public class HotWordMngr {

	public static int getHotWordCount() {
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("hotword", "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getHotWordList() {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("id,type,hotword,time", "hotword");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstInfo;
	}

	/**
	 * 删除app信息
	 * 
	 * @param appId
	 * @return
	 */
	public static boolean delHotWord(String hwId) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("hotword", "id in(".concat(dbHelper.FilterSpecialCharacter(hwId)).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static List<Map<String, Object>> getHotWordDetail(String hwId) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("type,hotword,time", "hotword", "id = ".concat(dbHelper.FilterSpecialCharacter(hwId)).concat(""));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstInfo;
	}

	public static List<Map<String, Object>> getHotWordInfoList(String type) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("id,hotword,time", "hotword", "type='".concat(dbHelper.FilterSpecialCharacter(type)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstInfo;
	}

	public static boolean addHotWordInfo(String type, String hotWord) {
		boolean bRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("hotword", new String[] { "type", "hotword", "time" }, new Object[] { type, hotWord, new Timestamp(new Date().getTime()) });
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		return bRet;
	}

	// public static int getMaxID() {
	// List<Map<String, Object>> lstHotWordInfo = null;
	// try {
	// DBHelper dbHelper = DBHelper.GetInstance();
	// lstHotWordInfo = dbHelper.ExecuteQuery("max(id) id", "hotword");
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// }
	// if (lstHotWordInfo == null) {
	// return -1;
	// }
	// return Integer.parseInt(String.valueOf(lstHotWordInfo.get(0).get("id")));
	// }

	public static boolean updateImageInfo(String id, String type, String hotword) {
		boolean bRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("hotword", "id=" + id, new String[] { "type", "hotword", "time" }, new Object[] { type, hotword, new Timestamp(new Date().getTime()) });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}
}
