package BLL;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class IdentityAuthMngr {

	public static int getIdentityAuthCount(String status, String txtStartTime, String txtEndTime) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		String timeCol = "time";
		if (null == status) {
		} else if (status.equals("0")) {
			sbCondition.append(" status =").append(status);
			sbCondition.append(" AND ");
		} else if (status.equals("1")) {
			sbCondition.append(" status =").append(status);
			sbCondition.append(" AND ");
			timeCol = "updatetime";
		} else if (status.equals("-1")) {
			sbCondition.append(" status =").append(status);
			sbCondition.append(" AND ");
			timeCol = "updatetime";
		}

		if (!Common.IsNullOrEmpty(txtStartTime)) {
			sbCondition.append(timeCol).append(">='")
					.append(dbHelper.FilterSpecialCharacter(txtStartTime + " 00:00:00")).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(txtEndTime)) {
			sbCondition.append(timeCol).append("<'")
					.append(dbHelper.FilterSpecialCharacter(
							Common.ConvertToDateTime(txtEndTime, "yyyy-MM-dd", 24 * 3600 * 1000) + " 00:00:00"))
					.append("' AND ");
		}

		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}

		int count = 0;
		try {
			count = dbHelper.GetCount("expertauth", sbCondition.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	public static List<Map<String, Object>> getIdentityAuthList(String status, String txtStartTime, String txtEndTime,
			int start, int length) {
		StringBuilder sbCondition = new StringBuilder(
				" select * from(select auth.id,auth.username,auth.cardnum,auth.front,auth.back,auth.status,auth.cause,auth.updatetime,auth.time,claim.workunit,claim.phone,claim.email,claim.realname from mdlmngr.expertauth as auth left join mdlmngr.expertclaim as claim on auth.username=claim.username )as t1 where 1=1 ");
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		// t1.xxx=''
		String timeCol = "t1.time";
		if (null == status) {
		} else if (status.equals("0")) {
			sbCondition.append(" AND ");
			sbCondition.append(" t1.status =").append(status);
		} else if (status.equals("1")) {
			sbCondition.append(" AND ");
			sbCondition.append(" t1.status =").append(status);
			timeCol = "t1.updatetime";
		} else if (status.equals("-1")) {
			sbCondition.append(" AND ");
			sbCondition.append(" t1.status =").append(status);
			timeCol = "t1.updatetime";
		}

		if (!Common.IsNullOrEmpty(txtStartTime)) {
			sbCondition.append(" AND ");
			sbCondition.append(timeCol).append(">='")
					.append(dbHelper.FilterSpecialCharacter(txtStartTime + " 00:00:00")).append("'");
		}
		if (!Common.IsNullOrEmpty(txtEndTime)) {
			sbCondition.append(" AND ");
			sbCondition.append(timeCol).append("<'")
					.append(dbHelper.FilterSpecialCharacter(
							Common.ConvertToDateTime(txtEndTime, "yyyy-MM-dd", 24 * 3600 * 1000) + " 00:00:00"))
					.append("'");
		}

		sbCondition.append(" order by time desc ");
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
			sb.append(
					"select * from(select auth.id,auth.username,auth.cardnum,auth.front,auth.back,auth.status,auth.cause,auth.updatetime,auth.time,claim.workunit,claim.phone,claim.email,claim.realname from expertauth as auth left join expertclaim as claim on auth.username=claim.username )as t1 where t1.id=");
			sb.append(id);
			List<Map<String, Object>> list = dbHelper.ExecuteQuery(sb.toString());
			return list;
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return null;
	}

}
