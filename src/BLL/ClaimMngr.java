package BLL;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class ClaimMngr {

	public static int getClaimCount(String username, String txtStartTime, String txtEndTime) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}

		if (!Common.IsNullOrEmpty(username)) {
			sbCondition.append("username").append("='").append(dbHelper.FilterSpecialCharacter(username))
					.append("' AND ");
		}
		if (!Common.IsNullOrEmpty(txtStartTime)) {
			sbCondition.append("time").append(">='")
					.append(dbHelper.FilterSpecialCharacter(txtStartTime + " 00:00:00")).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(txtEndTime)) {
			sbCondition.append("time").append("<'")
					.append(dbHelper.FilterSpecialCharacter(
							Common.ConvertToDateTime(txtEndTime, "yyyy-MM-dd", 24 * 3600 * 1000) + " 00:00:00"))
					.append("' AND ");
		}

		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}

		int count = 0;
		try {
			count = dbHelper.GetCount("expertclaim", sbCondition.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	public static List<Map<String, Object>> getClaimList(String username, String txtStartTime, String txtEndTime,
			int start, int length) {
		StringBuilder sbCondition = new StringBuilder(" SELECT c.id, c.username, c.expcode, c.realname, c.workunit, c.phone, c.email, c.time, a.cardnum FROM `expertclaim` c left outer join expertauth a on c.username = a.username where 1=1 ");
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (!Common.IsNullOrEmpty(username)) {
			sbCondition.append(" AND ");
			sbCondition.append("c.username").append("='").append(dbHelper.FilterSpecialCharacter(username))
					.append("' ");
		}
		if (!Common.IsNullOrEmpty(txtStartTime)) {
			sbCondition.append(" AND ");
			sbCondition.append("c.time").append(">='")
					.append(dbHelper.FilterSpecialCharacter(txtStartTime + " 00:00:00")).append("' ");
		}
		if (!Common.IsNullOrEmpty(txtEndTime)) {
			sbCondition.append(" AND ");
			sbCondition.append("c.time").append("<'")
					.append(dbHelper.FilterSpecialCharacter(
							Common.ConvertToDateTime(txtEndTime, "yyyy-MM-dd", 24 * 3600 * 1000) + " 00:00:00"))
					.append("'");
		}

		sbCondition.append(" order by c.id desc ");
		sbCondition.append(" limit ").append(start - 1).append(" , ").append(length);
		List<Map<String, Object>> lstComment = null;
		try {
			lstComment = dbHelper.ExecuteQuery(sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstComment == null) {
			lstComment = Collections.emptyList();
		}
		return lstComment;
	}

	public static List<Map<String, Object>> getById(String id) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			StringBuffer sb = new StringBuffer();
			sb.append("select * from expertclaim where id=");
			sb.append(id);
			List<Map<String, Object>> list = dbHelper.ExecuteQuery(sb.toString());
			return list;
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return null;
	}

}
