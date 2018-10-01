package BLL;

import java.util.List;
import java.util.Map;

import DAL.DBHelper;

public class SubscriptionMngr {
	
	
	
	public static List<Map<String,Object>> getSubscription(String SubptName){
		StringBuilder sbCondition=new StringBuilder();
		sbCondition.append("typename='").append(SubptName).append("'");
		List<Map<String,Object>> lstSubscript=null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubscript = dbHelper.ExecuteQuery("id", "subscriptiontype", sbCondition.toString());
			if(lstSubscript==null){
				dbHelper.Insert("subscriptiontype", new String[] { "typename" }, new Object[] { SubptName });
				lstSubscript = dbHelper.ExecuteQuery("id", "subscriptiontype", "typename='".concat(SubptName).concat("'"));
				CreateTables("subscription",SubptName);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		} finally {
			sbCondition = null;
			dbHelper = null;
		}
		return lstSubscript;
	}
	
//	/*
//	 * ���붩�ı�
//	 */
//	public boolean SetSubscription(String TabName,SubscriptionBean Subscription){
//		DBHelper dbHelper = null;
//		boolean bResult=false;
//		try {
//			dbHelper = DBHelper.GetInstance();
//			bResult=dbHelper.Insert(TabName, new String[] { "subscriptionname", "username", "content","type","time" }, new Object[] { Subscription.getSubscriptionName(), Subscription.getUserName(),Subscription.getContent(),Subscription.getType(), Common.GetDateTime() });
//		}catch(Exception e){
//			Logger.WriteException(e);
//		}
//		return bResult;
//	} 
	
	
	/*
	 * ����
	 * oldtabname��newtabname
	 */
	private static boolean CreateTables(String OldTabName,String NewTabName){
		StringBuilder sbSQL=new StringBuilder();
		sbSQL.append("CREATE table IF NOT EXISTS ").append(NewTabName).append(" like ").append(OldTabName);
		DBHelper dbHelper = null;
		boolean bResult=false;
		try {
			dbHelper = DBHelper.GetInstance();
			bResult=dbHelper.ExecuteSql(sbSQL.toString());
		}catch(Exception e){
			Logger.WriteException(e);
		}
		return bResult;
	}
	
}
