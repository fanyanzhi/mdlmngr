package BLL;

import DAL.DBHelper;
import Util.Common;

public class CashMngr {
	public static boolean recharge(String userName, String cash, String platform, String environment, String status,
			String ip, String appId) {
		DBHelper dbHelper = null;
		boolean ret = false;
		try {
			dbHelper = DBHelper.GetInstance();
			ret = dbHelper.Insert("recharge",
					new String[] { "username", "cash", "platform", "environment", "status", "time", "ip", "appid" },
					new Object[] { userName, cash, platform, environment, status, Common.GetDateTime(), ip, appId });
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return ret;
	}
}
