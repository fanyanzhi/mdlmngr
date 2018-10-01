package BLL;

import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class FavoriteMngr {
	/**
	 * 
	 * @param UserName
	 * @param TypeID
	 * @param FileID
	 * @param Title
	 * @param Score
	 * @param Content
	 * @return
	 */
	public static boolean addFavoriteInfo(String UserName, String odatatype, String FileID, String Title, String author,
			String source, String appid) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userfavorite", UserName);
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert(strTableName,
					new String[] { "username", "odatatype", "fileid", "title", "author", "source", "appid", "time" },
					new Object[] { UserName, odatatype, FileID, Title, author, source, appid, Common.GetDateTime() });
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return bRet;
	}

	public static Map<String, Object> getFavoriteID(String UserName, String odatatype, String fileid) {
		List<Map<String, Object>> list = null;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userfavorite", UserName);
		try {
			dbHelper = DBHelper.GetInstance();
			list = dbHelper.ExecuteQuery("id,time", strTableName,
					"username ='".concat(UserName).concat("'").concat(" and odatatype='").concat(odatatype).concat("'")
							.concat(" and fileid='").concat(fileid).concat("'"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public static int checkUserFavorite(String username, String odatatype, String fileid) {
		int bRet = -1;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userfavorite", username);
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.GetCount(strTableName, "username ='".concat(username).concat("'").concat(" and odatatype='")
					.concat(odatatype).concat("'").concat(" and fileid='").concat(fileid).concat("'"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return bRet;
	}

	public static int getFavoriteCount(String username) {
		int bRet = 0;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userfavorite", username);
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.GetCount(strTableName, "username ='".concat(username).concat("'"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return bRet;
	}

	public static List<Map<String, Object>> getFavoriteList(String username, int start, int length) {
		List<Map<String, Object>> list = null;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userfavorite", username);
		try {
			dbHelper = DBHelper.GetInstance();
			list = dbHelper.ExecuteQuery("id,odatatype,fileid,title,author,source,time", strTableName,
					"username='".concat(username).concat("'"), "time desc", start, length);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return list;
	}

	public static boolean delFavorite(String UserName, String id) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userfavorite", UserName);
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(strTableName, "username='" + dbHelper.FilterSpecialCharacter(UserName)
					+ "' and id in('" + dbHelper.FilterSpecialCharacter(id).replace(",", "','") + "')");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return bRet;
	}

	public static boolean delAllFavorite(String UserName, String array) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userfavorite", UserName);
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(strTableName, "id in (".concat(array).concat(")"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return bRet;
	}

	private static String getTableName(String SourceTable, String UserName) {
		return SourceTable.concat(String.valueOf(Math.abs(Common.EnCodeMD5(UserName).hashCode())).substring(0, 1));

	}
}
