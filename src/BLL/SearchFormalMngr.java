package BLL;

import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;
import net.sf.json.JSONObject;

public class SearchFormalMngr {

	/**
	 * @param userName
	 * @param code
	 * @return
	 */
	public static boolean addAtionSeaFormal(String userName, Map<String, String> arg, String appId) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = "searchformal";
		JSONObject jsonObj = JSONObject.fromObject(arg.get("content"));
		String content = jsonObj.toString();
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert(strTableName, new String[] { "username", "formal", "content", "time", "appid" },
					new Object[] { userName, arg.get("formal"), content, Common.GetDateTime(), appId });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static Map<String, Object> getLastRecord(String userName) {
		DBHelper dbHelper = null;
		String strTableName = "searchformal";
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("id, time", strTableName,
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'", "id desc", 1, 1);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (lst != null && lst.size() > 0)
			return lst.get(0);
		return null;
	}

	public static boolean updateAtionSeaFormal(String userName, Map<String, String> arg, String appId) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = "searchformal";
		JSONObject jsonObj = JSONObject.fromObject(arg.get("content"));
		String content = jsonObj.toString();
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update(strTableName,
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "' and id="
							+ dbHelper.FilterSpecialCharacter(arg.get("id")),
					new String[] { "formal", "content", "time" },
					new Object[] { arg.get("formal"), content, Common.GetDateTime() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static boolean cancelAtionSeaFormal(String userName, String id) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = "searchformal";
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(strTableName, "username='" + dbHelper.FilterSpecialCharacter(userName)
					+ "' and id in('" + dbHelper.FilterSpecialCharacter(id).replace(",", "','") + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static int getSeaFormalCount(String userName) {
		String strTableName = "searchformal";
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(strTableName, "username='" + dbHelper.FilterSpecialCharacter(userName) + "'");
		} catch (Exception e) {
			// Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getSeaFormalList(String userName) {
		String strTableName = "searchformal";
		List<Map<String, Object>> lstAtionSeaFormal = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAtionSeaFormal = dbHelper.ExecuteQuery("id,formal,content,time", strTableName,
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'", "time desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAtionSeaFormal;
	}

	/**
	 * 获取检索式数量（用于后台检索式管理）
	 * @param userName
	 * @param formal 检索式名
	 * @param appid
	 * @param strStartDate
	 * @param strEndDate
	 * @return
	 */
	public int getSeaFormalCount2(String userName, String formal,String appid,String strStartDate, String strEndDate) {
		String strTableName = "searchformal";
		StringBuilder sbCondition = new StringBuilder();
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(userName)) {
				sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' and ");
			}
			if (!Common.IsNullOrEmpty(formal)) {
				sbCondition.append("formal = '").append(dbHelper.FilterSpecialCharacter(formal)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(appid)) {
				sbCondition.append("appid = '").append(dbHelper.FilterSpecialCharacter(appid)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(strStartDate)) {
				sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(strStartDate)).append("' AND ");
			}
			if (!Common.IsNullOrEmpty(strEndDate)) {
				sbCondition.append("time <'").append(dbHelper
						.FilterSpecialCharacter(Common.ConvertToDateTime(strEndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
				.append("' AND ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			}
			iCount = dbHelper.GetCount(strTableName, sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}
	/**
	 * 获取检索式（用于后台检索式管理）
	 * @param userName
	 * @param formal 检索式名
	 * @param appid
	 * @param strStartDate
	 * @param strEndDate
	 * @return
	 */
	public List<Map<String, Object>> getSeaFormalList2(String userName, String formal, String appid, String strStartDate, String strEndDate, int iStart, int iLength) {
		String strTableName = "searchformal";
		List<Map<String, Object>> lstAtionScholar = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(userName)) {
				sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' and ");
			}
			if (!Common.IsNullOrEmpty(formal)) {
				sbCondition.append("formal = '").append(dbHelper.FilterSpecialCharacter(formal)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(appid)) {
				sbCondition.append("appid = '").append(dbHelper.FilterSpecialCharacter(appid)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(strStartDate)) {
				sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(strStartDate)).append("' AND ");
			}
			if (!Common.IsNullOrEmpty(strEndDate)) {
				sbCondition.append("time <'").append(dbHelper
						.FilterSpecialCharacter(Common.ConvertToDateTime(strEndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
				.append("' AND ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			}

			lstAtionScholar = dbHelper.ExecuteQuery("id,username,formal,content,appid,time", strTableName,
					sbCondition.toString(),"time DESC ",iStart,iLength);

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAtionScholar;
	}
	/**
	 * 获取定制检索式用户数量
	 * @param strUserName
	 * @param formal
	 * @param appid
	 * @param strStartDate
	 * @param strEndDate
	 * @return
	 */
	public String getSeaFormalUserCount(String userName, String formal, String appid, String strStartDate,
			String strEndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		List<Map<String, Object>> lstCount = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(userName)) {
				sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' and ");
			}
			if (!Common.IsNullOrEmpty(formal)) {
				sbCondition.append("formal = '").append(dbHelper.FilterSpecialCharacter(formal)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(appid)) {
				sbCondition.append("appid = '").append(dbHelper.FilterSpecialCharacter(appid)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(strStartDate)) {
				sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(strStartDate)).append("' AND ");
			}
			if (!Common.IsNullOrEmpty(strEndDate)) {
				sbCondition.append("time <'").append(dbHelper
						.FilterSpecialCharacter(Common.ConvertToDateTime(strEndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
				.append("' AND ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			}
	
			lstCount = dbHelper.ExecuteQuery("count(DISTINCT username) cut", "searchformal" ,
					sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if(lstCount!=null&&lstCount.size()>0){
			return lstCount.get(0).get("cut").toString();
		}
		return "0";
	}

	public boolean delAttention(String userName, String id) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete("searchformal", "username='" + dbHelper.FilterSpecialCharacter(userName)
					+ "' and id in('" + id.replace(",", "','") + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}
}
