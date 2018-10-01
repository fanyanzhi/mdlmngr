package BLL;

import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Model.FavoriteInfoBean;
import Util.Common;

public class FavoriteInfoMngr {

	public static boolean addFavoriteInfo(FavoriteInfoBean FavoriteInfo) {
		boolean bolRet = false;
		String strFavotiteType = FavoriteInfo.getFavoriteType();
		String strTableName = getFavoriteTable(strFavotiteType);
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(strTableName, new String[] { "favoritename", "username", "content", "time" }, new Object[] { FavoriteInfo.getFavoriteName(), FavoriteInfo.getUserName(), FavoriteInfo.getFavoriteContent(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean delFavoriteInfo(FavoriteInfoBean FavoriteInfo) {
		boolean bolRet = false;
		String strTableName = getFavoriteTable(FavoriteInfo.getFavoriteType());
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete(strTableName, "id=".concat(String.valueOf(FavoriteInfo.getID())));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean isExistFavorite(FavoriteInfoBean FavoriteInfo) {
		int iCount = 0;
		String strCondition = "favoritename='".concat(FavoriteInfo.getFavoriteName()).concat("' and username='").concat(FavoriteInfo.getUserName()).concat("'");
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("favorite", strCondition);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount > 0) {
			return true;
		}
		return false;
	}

	public static List<Map<String, Object>> getFavoriteList(String UserName, String FavoriteType) {
		List<Map<String, Object>> lstFavorite = null;
		String strTableName = "favorite_".concat(FavoriteType).toLowerCase();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstFavorite = dbHelper.ExecuteQuery("id,favoritename,time", strTableName, "username='".concat(UserName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstFavorite;
	}

	private static boolean isExistFavoriteType(String FavoriteType) {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("favoritetype", "typename='".concat(FavoriteType).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount > 0) {
			return true;
		}
		return false;
	}

	private static boolean addFavoriteType(String FavoriteType) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("favoritetype", new String[] { "typename", "time" }, new Object[] { FavoriteType, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static boolean createFavoriteTable(String TableName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql("create table if not exists ".concat(TableName).concat(" like favorite"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static String getFavoriteTable(String FavoriteType) {
		String strTableName = "favorite_".concat(FavoriteType).toLowerCase();
		if (isExistFavoriteType(FavoriteType)) {
			return strTableName;
		}

		if (!createFavoriteTable(strTableName)) {
			return null;
		}

		if (!addFavoriteType(FavoriteType)) {
			return null;
		}

		return strTableName;
	}
}
