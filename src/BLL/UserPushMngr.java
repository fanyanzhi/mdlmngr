package BLL;

import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class UserPushMngr {

	public static List<Map<String, Object>> getUserPush(String userName) {
		List<Map<String, Object>> lstUserPush = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUserPush = dbHelper.ExecuteQuery("type,time", "userpush",
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'");
		} catch (Exception e) {
		}
		return lstUserPush;
	}

	public static boolean setUserPush(String userName, int type) {
		boolean bRet = true;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("userpush", new String[] { "username", "type", "time" },
					new Object[] { userName, type, Common.GetDateTime() });
		} catch (Exception e) {
		}
		return bRet;
	}

	public static boolean cancelUserPush(String userName, int type) {
		boolean bRet = true;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete("userpush",
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "' and type=" + type);
		} catch (Exception e) {
		}
		return bRet;
	}
}
