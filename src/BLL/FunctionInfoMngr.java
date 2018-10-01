package BLL;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class FunctionInfoMngr {

	public static List<Map<String, Object>> getFunctionInfoList(String type, String startDate, String endDate) {
		StringBuilder sbCondition = new StringBuilder(
				" select *, DATE_FORMAT(spottime,'%Y-%m-%d')as ymd from appmodulestatis where 1=1 ");
		// select *, DATE_FORMAT(spottime,'%Y-%m-%d')as
		// ymd,date_format(spottime,'%c') as m,date_format(spottime,'%e') as d
		// from appmodulestatis order by spottime asc,type asc;

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}

		if (!Common.IsNullOrEmpty(type)) {
			sbCondition.append(" and ");
			sbCondition.append("type='").append(type).append("'");
		}
		if (!Common.IsNullOrEmpty(startDate)) {
			sbCondition.append(" and ");
			sbCondition.append("spottime>='").append(startDate).append("'");
		}
		if (!Common.IsNullOrEmpty(endDate)) {
			sbCondition.append(" and ");
			sbCondition.append("spottime<='").append(endDate).append("'");
		}
		sbCondition.append(" order by spottime asc");

		List<Map<String, Object>> list = null;
		try {
			list = dbHelper.ExecuteQuery(sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return list;
	}

	public static int getFunctionInfoCount(String txtStartDate, String txtEndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}

		if (!Common.IsNullOrEmpty(txtStartDate)) {
			sbCondition.append("spottime >='").append(dbHelper.FilterSpecialCharacter(txtStartDate)).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(txtEndDate)) {
			sbCondition.append("spottime <='").append(dbHelper.FilterSpecialCharacter(txtEndDate)).append("' AND ");
		}
		System.out.println("presql:" + sbCondition.toString());
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		System.out.println("afgersql:" + sbCondition.toString());

		int count = 0;
		try {
			count = dbHelper.GetCount("appmodulestatis", sbCondition.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

}
