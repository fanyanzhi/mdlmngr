package BLL;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class RechargeMngr {

	public static float getRechargeSum(String UserName, String StartDate, String EndDate, String txtPlatForm, String txtIsSus) {
		StringBuilder sbCondition = new StringBuilder(" select sum(cash) as total from recharge where 1=1 ");
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if(!Common.IsNullOrEmpty(txtPlatForm)){
			sbCondition.append(" and ");
			sbCondition.append(" platform = '").append(dbHelper.FilterSpecialCharacter(txtPlatForm)).append("'");
		}
		if(Common.IsNullOrEmpty(txtIsSus)){
			sbCondition.append(" and ");
			sbCondition.append(" (status = 0 or status = 1)");
		}else{
			sbCondition.append(" and ");
			sbCondition.append(" status = ").append(txtIsSus);
		}
		if (UserName != null && UserName.length() > 0) {
			sbCondition.append(" and ");
			sbCondition.append(" username = '").append(dbHelper.FilterSpecialCharacter(UserName)).append("'");
		}
		if (StartDate != null && StartDate.length() > 0) {
			sbCondition.append(" and ");
			sbCondition.append(" time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("'");
		}
		if (EndDate != null && EndDate.length() > 0) {
			sbCondition.append(" and ");
			sbCondition.append(" time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("'");
		}

		List<Map<String, Object>> lstComment = null;
		try {
			lstComment = dbHelper.ExecuteQuery(sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstComment == null) {
			return 0;
		}
		Object object = lstComment.get(0).get("total");
		if (null == object) {
			return 0;
		} else {

			return Float.parseFloat(object.toString());
		}
	}

	/**
	 * 
	 * @param UserName
	 * @param StartDate
	 * @param EndDate
	 * @param Start
	 * @param Length
	 * @return
	 * @description 获取recharge记录列表
	 */
	public static List<Map<String, Object>> getRechargeList(String UserName, String StartDate, String EndDate, String txtPlatForm, String txtIsSus,
			int Start, int Length) {
		StringBuilder sbCondition = new StringBuilder(" select * from recharge where 1=1 ");
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if(!Common.IsNullOrEmpty(txtPlatForm)){
			sbCondition.append(" and ");
			sbCondition.append(" platform = '").append(dbHelper.FilterSpecialCharacter(txtPlatForm)).append("'");
		}
		if(Common.IsNullOrEmpty(txtIsSus)){
			sbCondition.append(" and ");
			sbCondition.append(" (status = 0 or status = 1) ");
		}else{
			sbCondition.append(" and ");
			sbCondition.append(" status = ").append(txtIsSus).append(" ");
		}
		if (UserName != null && UserName.length() > 0) {
			sbCondition.append(" and ");
			sbCondition.append(" username = '").append(dbHelper.FilterSpecialCharacter(UserName)).append("'").append(" ");
		}
		if (StartDate != null && StartDate.length() > 0) {
			sbCondition.append(" and ");
			sbCondition.append(" time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("'").append(" ");
		}
		if (EndDate != null && EndDate.length() > 0) {
			sbCondition.append(" and ");
			sbCondition.append(" time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("'").append(" ");
		}
		sbCondition.append("order by id desc ");
		sbCondition.append(" limit ").append(Start - 1).append(" , ").append(Length);

		List<Map<String, Object>> lstComment = null;
		try {
			lstComment = dbHelper.ExecuteQuery(sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstComment;
	}

	/**
	 * 
	 * @param UserName
	 * @param StartDate
	 * @param EndDate
	 * @return 记录条数
	 */
	public static int getRechargeCount(String UserName, String StartDate, String EndDate, String txtPlatForm, String txtIsSus) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if(!Common.IsNullOrEmpty(txtPlatForm)){
			sbCondition.append(" platform = '").append(dbHelper.FilterSpecialCharacter(txtPlatForm)).append("' and ");
		}
		if(Common.IsNullOrEmpty(txtIsSus)){
			sbCondition.append(" (status = 0 or status = 1) and ");
		}else{
			sbCondition.append(" status = ").append(txtIsSus).append(" and ");
		}
		if (UserName != null && UserName.length() > 0) {
			sbCondition.append("username = '").append(dbHelper.FilterSpecialCharacter(UserName)).append("' AND ");
		}
		if (StartDate != null && StartDate.length() > 0) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (EndDate != null && EndDate.length() > 0) {
			sbCondition.append("time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}

		int count = 0;
		try {
			count = dbHelper.GetCount("recharge", sbCondition.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

}
