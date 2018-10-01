package BLL;

import DAL.DBHelper;
import Util.Common;

public class FreeDownMngr {
	public static boolean addFreeDownInfo(String UserName, String Type, String FileID) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("freedown", new String[] { "username", "type", "fileid", "time" },
					new Object[] { UserName, Type, FileID, Common.GetDateTime() });
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return bRet;
	}

	public static int getFreeDownCount(String username) {
		int bRet = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.GetCount("freedown", "username ='".concat(username).concat("'"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return bRet;
	}
}
