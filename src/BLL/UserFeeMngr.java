package BLL;

import java.util.List;
import java.util.Map;

import Util.Common;
import DAL.DBHelper;

public class UserFeeMngr {
	public static void setUserFee(String UserName, String TypeID, String FileID, float Price) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (!dbHelper.Insert(getUserFeeTable(), new String[] { "username", "typeid", "fileid", "price", "time" }, new Object[] { UserName, TypeID, FileID, Price, Common.GetDateTime() })) {
				Logger.WriteException(new Exception("insert user fee record failed"));
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}

	private static String getUserFeeTable() {
		String strNow = Common.GetDateTime("yyMMdd");
		String strYear = strNow.substring(0, 2);
		String strMonth = strNow.substring(2, 4);
		String strTablePost;

		switch (strMonth) {
		case "01":
		case "02":
		case "03":
			strTablePost = "0103";
			break;
		case "04":
		case "05":
		case "06":
			strTablePost = "0406";
			break;
		case "07":
		case "08":
		case "09":
			strTablePost = "0709";
			break;
		case "10":
		case "11":
		case "12":
			strTablePost = "1012";
			break;
		default:
			return "commentinfo";
		}

		return "userfee".concat(strYear).concat(strTablePost);
	}
	
	public static int getUserFreeCount(String UserName,String StartDate,String EndDate){
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (UserName != null && UserName.length() > 0) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' AND ");
		}
		if (StartDate != null && StartDate.length() > 0) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (EndDate != null && EndDate.length() > 0) {
			sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000))).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] objParam = new Object[2];
		objParam[0] = "userfee";
		objParam[1] = sbCondition.toString();
		List<Map<String, Object>> lstComment = null;
		try {
			lstComment = dbHelper.ExecuteQueryProc("up_GetViewCount", objParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstComment == null) {
			return 0;
		}
		return Integer.valueOf(lstComment.get(0).get("totalcount").toString());
	}
	public static float getUserFreeSum(String UserName,String StartDate,String EndDate){
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (UserName != null && UserName.length() > 0) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' AND ");
		}
		if (StartDate != null && StartDate.length() > 0) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (EndDate != null && EndDate.length() > 0) {
			sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000))).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] objParam = new Object[2];
		objParam[0] = "userfee";
		objParam[1] = sbCondition.toString();
		List<Map<String, Object>> lstComment = null;
		try {
			lstComment = dbHelper.ExecuteQueryProc("up_GetViewSum", objParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstComment == null) {
			return 0;
		}
		return Float.parseFloat(lstComment.get(0).get("totalsum").toString());
	}
	public static List<Map<String, Object>> getUserFreeList(String UserName, String StartDate, String EndDate,int Start, int Length) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (UserName != null && UserName.length() > 0) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' AND ");
		}
		if (StartDate != null && StartDate.length() > 0) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (EndDate != null && EndDate.length() > 0) {
			sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000))).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] objParam = new Object[5];
		objParam[0] = "userfee";
		objParam[1] = "id,username,typeid,fileid,price,time,comment";
		objParam[2] = sbCondition.toString();
		objParam[3] = Start;
		objParam[4] = Length;
		List<Map<String, Object>> lstComment = null;
		try {
			lstComment = dbHelper.ExecuteQueryProc("up_GetViewData", objParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstComment;
	}
	
	public static float getUserFreePriceSum(String txtUserName,String startDate,String endDate){
		StringBuilder sbCondition = new StringBuilder();
		List<Map<String, Object>> list = null;
		float userFreeSum = 0;
		float sum = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			sbCondition.append("modeltable='userfee' ");
			if (startDate != null && startDate.length() > 0) {
				sbCondition.append("and ").append("time >='").append(dbHelper.FilterSpecialCharacter(startDate)).append("' AND ");
			}
			if (endDate != null && endDate.length() > 0) {
				sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(endDate, "yyyy-MM-dd", 24 * 3600 * 1000))).append("'");
			}
				list = dbHelper.ExecuteQuery("tablename", "tableindex", sbCondition.toString());
			if(list != null){
				for(Map<String, Object> map : list){
					String tablename = (String)map.get("tablename");
					String sql = "SELECT SUM(price) as sum FROM "+tablename;
					List<Map<String, Object>>  sumlist = dbHelper.ExecuteQuery(sql);
					if(sumlist != null&&sumlist.get(0).get("sum")!=null){
						String a = String.valueOf(sumlist.get(0).get("sum"));
						
						sum =  Float.parseFloat(a);
					}else{
						sum =0;
					}
					userFreeSum = userFreeSum+sum;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return userFreeSum;
	}
}
