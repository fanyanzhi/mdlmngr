package BLL;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class OrgHomePageMngr {

	public static int getOrgHomePageCount(String txtOrg, String txtStartTime, String txtEndTime) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}

		if (!Common.IsNullOrEmpty(txtOrg)) {
			sbCondition.append("orgname").append("='").append(dbHelper.FilterSpecialCharacter(txtOrg)).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(txtStartTime)) {
			sbCondition.append("time").append(">='").append(dbHelper.FilterSpecialCharacter(txtStartTime + " 00:00:00"))
					.append("' AND ");
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
			count = dbHelper.GetCount("orgweb", sbCondition.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	public static List<Map<String, Object>> getOrgHomePageList(String txtOrg, String txtStartTime, String txtEndTime,
			int start, int length) {
		StringBuilder sbCondition = new StringBuilder(" select * from orgweb where 1=1 ");
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (!Common.IsNullOrEmpty(txtOrg)) {
			sbCondition.append("and ").append("orgname").append("='").append(dbHelper.FilterSpecialCharacter(txtOrg))
					.append("'");
		}
		if (!Common.IsNullOrEmpty(txtStartTime)) {
			sbCondition.append(" AND ");
			sbCondition.append("time").append(">='").append(dbHelper.FilterSpecialCharacter(txtStartTime + " 00:00:00"))
					.append("'");
		}
		if (!Common.IsNullOrEmpty(txtEndTime)) {
			sbCondition.append(" AND ");
			sbCondition.append("time").append("<'")
					.append(dbHelper.FilterSpecialCharacter(
							Common.ConvertToDateTime(txtEndTime, "yyyy-MM-dd", 24 * 3600 * 1000) + " 00:00:00"))
					.append("'");
		}

		sbCondition.append(" order by id desc ");
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
			sb.append("select * from orgweb where id=");
			sb.append(id);
			List<Map<String, Object>> list = dbHelper.ExecuteQuery(sb.toString());
			return list;
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return null;
	}

	public static boolean updateUrlById(String id, String unitname, String orgname, String weburl) {
		DBHelper dbHelper = null;
		boolean bRet = false;

		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("orgweb", "id=" + id, new String[] { "unitname", "orgname", "weburl", "updatetime" },
					new Object[] { unitname, orgname, weburl, Common.GetDateTime() });
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Logger.WriteException(e);
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.WriteException(e);
		} catch (InstantiationException e) {
			e.printStackTrace();
			Logger.WriteException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			Logger.WriteException(e);
		}
		return bRet;

	}

	public static boolean add(String unitname, String orgname, String weburl) {

		DBHelper dbHelper = null;
		Boolean insert = false;
		try {
			dbHelper = DBHelper.GetInstance();
			insert = dbHelper.Insert("orgweb", new String[] { "unitname", "orgname", "weburl", "time", "updatetime" },
					new Object[] { unitname, orgname, weburl, Common.GetDateTime(), Common.GetDateTime() });
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return insert;

	}

	public static boolean delete(String id) {
		boolean bResult = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("orgweb", "id=" + id);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bResult;
	}

	public static String getOrgWeb(String orgname) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("weburl", "orgweb",
					"orgname ='" + dbHelper.FilterSpecialCharacter(orgname) + "'");
		} catch (Exception e) {

		}
		if (lst == null || lst.size() == 0)
			return "";
		return lst.get(0).get("weburl").toString();
	}

}
