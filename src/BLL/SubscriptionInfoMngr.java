package BLL;

import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Model.SubscriptionInfoBean;
import Util.Common;

public class SubscriptionInfoMngr {
	public static boolean addFavoriteInfo(SubscriptionInfoBean SubscriptionInfo) {
		boolean bolRet = false;
		String strSubscriptionType = SubscriptionInfo.getSubscriptionType();
		String strTableName = getSubscriptionTable(strSubscriptionType);
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(strTableName, new String[] { "subscriptionname", "username", "content", "time" }, new Object[] { SubscriptionInfo.getSubscriptionName(), SubscriptionInfo.getUserName(), SubscriptionInfo.getSubscriptionContent(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean delSubscriptionInfo(SubscriptionInfoBean SubscriptionInfo) {
		boolean bolRet = false;
		String strTableName = getSubscriptionTable(SubscriptionInfo.getSubscriptionType());
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete(strTableName, "id=".concat(String.valueOf(SubscriptionInfo.getID())));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean isExistSubscription(SubscriptionInfoBean SubscriptionInfo) {
		int iCount = 0;
		String strCondition = "subscriptionname='".concat(SubscriptionInfo.getSubscriptionName()).concat("' and username='").concat(SubscriptionInfo.getUserName()).concat("'");
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("subscription", strCondition);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount > 0) {
			return true;
		}
		return false;
	}

	public static List<Map<String, Object>> getSubscriptionList(String UserName, String SubscriptionType) {
		List<Map<String, Object>> lstSubscription = null;
		String strTableName = "Subscription_".concat(SubscriptionType).toLowerCase();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSubscription = dbHelper.ExecuteQuery("id,subscriptionname,time", strTableName, "username='".concat(UserName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSubscription;
	}

	private static boolean isExistSubscriptionType(String SubscriptionType) {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("subscriptiontype", "typename='".concat(SubscriptionType).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount > 0) {
			return true;
		}
		return false;
	}

	private static boolean addSubscriptionType(String SubscriptionType) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("subscriptiontype", new String[] { "typename", "time" }, new Object[] { SubscriptionType, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static boolean createSubscriptionTable(String TableName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql("create table if not exists ".concat(TableName).concat(" like subscription"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static String getSubscriptionTable(String SubscriptionType) {
		String strTableName = "subscription_".concat(SubscriptionType).toLowerCase();
		if (isExistSubscriptionType(SubscriptionType)) {
			return strTableName;
		}

		if (!createSubscriptionTable(strTableName)) {
			return null;
		}

		if (!addSubscriptionType(SubscriptionType)) {
			return null;
		}

		return strTableName;
	}
}
