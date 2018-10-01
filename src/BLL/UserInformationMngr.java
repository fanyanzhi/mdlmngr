package BLL;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;

/**
 * 
 * 用户资料
 */
public class UserInformationMngr {

	public static int getUserInformationCount(String txtUserName, String txtMobile) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (txtUserName != null && txtUserName.length() > 0) {
			sbCondition.append("username = '").append(dbHelper.FilterSpecialCharacter(txtUserName)).append("' AND ");
		}
		if (txtMobile != null && txtMobile.length() > 0) {
			sbCondition.append("mobile ='").append(dbHelper.FilterSpecialCharacter(txtMobile)).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		int count = 0;
		try {
			count = dbHelper.GetCount("cnkiuser", sbCondition.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	public static List<Map<String, Object>> getUserInformationList(String txtUserName, String txtMobile, int Start,
			int Length) {
		StringBuilder sbCondition = new StringBuilder(" select * from cnkiuser where 1=1 ");
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (txtUserName != null && txtUserName.length() > 0) {
			sbCondition.append(" and ");
			sbCondition.append(" username = '").append(dbHelper.FilterSpecialCharacter(txtUserName)).append("'");
		}
		if (txtMobile != null && txtMobile.length() > 0) {
			sbCondition.append(" and ");
			sbCondition.append(" mobile ='").append(dbHelper.FilterSpecialCharacter(txtMobile)).append("'");
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

}
