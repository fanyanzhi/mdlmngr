package BLL;

import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class HotspotMngr {

	/**
	 * 获取学科下面的热点信息
	 * 
	 * @param sortCode
	 * @return
	 */
	public static List<Map<String, Object>> getHotspotBySortCode(String sortCode) {
		List<Map<String, Object>> lstHotspot = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstHotspot = dbHelper.ExecuteQuery("spotid,title,expression,seaformal", "hotspot",
					"sortcode ='" + sortCode + "'", "priority asc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstHotspot;
	}

	public static List<Map<String, Object>> getHotspotCountBySortCode(String sortCode) {
		List<Map<String, Object>> lstHotspotCount = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstHotspotCount = dbHelper.ExecuteQuery(
					"select spotid,count(1) count from userhotspot where spotid in(select spotid from hotspot where sortcode='"
							+ sortCode + "') GROUP BY spotid");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstHotspotCount;
	}

	public static int getMyHotspotCount(String userName) {
		int hotspotCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			hotspotCount = dbHelper.GetCount("userhotspot",
					"username='".concat(dbHelper.FilterSpecialCharacter(userName)) + "'");

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return hotspotCount;
	}

	/**
	 * 获取用户定制的热点信息
	 * 
	 * @param userName
	 * @param start
	 * @param length
	 * @return
	 */
	public static List<Map<String, Object>> getMyHotspotList(String userName) {
		List<Map<String, Object>> lstHotspot = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstHotspot = dbHelper.ExecuteQuery(
					"SELECT hotspot.spotid,hotspot.title,hotspot.expression,hotspot.seaformal,hotspot.sortcode,userhotspot.time FROM userhotspot left join hotspot on userhotspot.spotid=hotspot.spotid where userhotspot.username='"
							+ dbHelper.FilterSpecialCharacter(userName) + "' ORDER BY userhotspot.time DESC");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstHotspot;
	}

	/**
	 * 是否已定制该热点
	 * 
	 * @param userName
	 * @param spotId
	 */
	public static int existUserHotspot(String userName, String spotId) {
		int iRet = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iRet = dbHelper.GetCount("userhotspot",
					"username = '".concat(userName).concat("' and spotid = '").concat(spotId).concat("'"));
		} catch (Exception e) {
			iRet = -1;
		}
		return iRet;
	}

	/**
	 * 添加热点定制
	 * 
	 * @param userName
	 * @param spotId
	 */
	public static boolean addUserHotspot(String userName, String spotId) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.Insert("userhotspot", new String[] { "username", "spotid", "time" },
					new Object[] { userName, spotId, Common.GetDateTime() }))
				bRet = true;
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	/**
	 * 取消热点定制
	 * 
	 * @param userName
	 * @param spotId
	 * @return
	 */
	public static boolean cancelUserHotspot(String userName, String spotId) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.Delete("userhotspot",
					"username = '".concat(userName).concat("' and spotid in ('").concat(spotId.replace(",", "','")).concat("')")))
				bRet = true;
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	/****************************************************************/

}
