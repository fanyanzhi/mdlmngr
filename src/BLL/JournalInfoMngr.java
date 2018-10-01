package BLL;

import DAL.DBHelper;
import Util.Common;

public class JournalInfoMngr {
	public static boolean saveUserJournalInfo(String userName, String dbcode, String fileName) {
		boolean bRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("userjournalinfo", new String[] { "username", "dbcode", "filename", "time" },
					new Object[] { userName, dbcode, fileName, Common.GetDateTime() });
		} catch (Exception e) {

		}
		return bRet;
	}

	public static boolean existUserJournalInfo(String userName, String fileName) {
		boolean bRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("userjournalinfo",
					"username = '" + userName + "' and filename='" + fileName + "'") > 0)
				bRet = true;
		} catch (Exception e) {

		}
		return bRet;
	}

}
