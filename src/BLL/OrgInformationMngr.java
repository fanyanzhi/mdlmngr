package BLL;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class OrgInformationMngr {

	/**
	 * 
	 * @param txtOrg
	 * @param StartDate
	 * @param EndDate
	 * @param Start
	 * @param Length
	 * @return
	 * @description 获取orginfo记录列表
	 */
	public static List<Map<String, Object>> getOrgInformationList(String txtOrg, String StartDate, String EndDate,
			int Start, int Length) {
		StringBuilder sbCondition = new StringBuilder(" select id,unitname,orgname,orgpwd,ip,longitude,latitude,updatetime from orginfo where 1=1 ");
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (txtOrg != null && txtOrg.length() > 0) {
			sbCondition.append(" and ");
			sbCondition.append(" orgname = '").append(dbHelper.FilterSpecialCharacter(txtOrg)).append("'");
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
	 * @param txtOrg
	 * @param StartDate
	 * @param EndDate
	 * @return 记录条数
	 */
	public static int getRechargeCount(String txtOrg, String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (txtOrg != null && txtOrg.length() > 0) {
			sbCondition.append("orgname = '").append(dbHelper.FilterSpecialCharacter(txtOrg)).append("' AND ");
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
			count = dbHelper.GetCount("orginfo", sbCondition.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

}
