package BLL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class RecommendationInfoMngr {

	public static List<Map<String, Object>> getRecommendationInfo(String recommnedID) {
		List<Map<String, Object>> lstRecommendationInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRecommendationInfo = dbHelper.ExecuteQuery("id,title,important,description", "recommendationinfo", "id='".concat(dbHelper.FilterSpecialCharacter(recommnedID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRecommendationInfo;
	}

	public static boolean updateRecdationDescription(String recommnedID, String strDescrption, String Important, String OldImportant) {
		boolean bolRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		List<String> lstSql = new ArrayList<String>();
		StringBuilder sbSql1 = new StringBuilder();
		StringBuilder sbSql2 = new StringBuilder();

		sbSql1.append("update recommendationinfo set updatetime='").append(Common.GetDateTime()).append("' where important=1");
		sbSql2.append("update recommendationinfo set updatetime='").append(Common.GetDateTime()).append("' where typeid =(select tabb.typeid from (select typeid from recommendationinfo where id='").append(dbHelper.FilterSpecialCharacter(recommnedID)).append("')tabb) and important = 0");

		if (!Important.equals(OldImportant)) {
			lstSql.add(sbSql1.toString());
			lstSql.add(sbSql2.toString());
		} else {
			if ("1".equals(Important)) {
				lstSql.add(sbSql1.toString());
			} else {
				lstSql.add(sbSql2.toString());
			}
		}
		lstSql.add("update recommendationinfo set description='".concat(strDescrption).concat("',important=").concat(Important).concat(",time='").concat(Common.GetDateTime()).concat("' where id=").concat(dbHelper.FilterSpecialCharacter(recommnedID)));
		try {
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static List<Map<String, Object>> getSearchField(String TypeID) {
		List<Map<String, Object>> lstSearchField = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSearchField = dbHelper.ExecuteQuery("name_ch,name_en", "searchfieldinfo", "typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"), "showorder asc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSearchField;
	}

	public static boolean recommendArticles(String appid, String TypeID, String FileID, List<Map<String, String>> ListArticle) {
		boolean bResult = false;
		List<String> lstSql = new ArrayList<String>();
		StringBuilder sbSql1 = new StringBuilder();
		sbSql1.append("delete from  recommendationinfo where appid='").append(appid).append("' and typeid=").append(TypeID).append(" and fileid in ('").append(FileID.replace(",", "','")).append("')");
		lstSql.add(sbSql1.toString());
		StringBuilder sbSql2 = new StringBuilder();
		sbSql2.append("insert into recommendationinfo(appid,typeid,fileid,title,tablename,important,time) values");
		Iterator<Map<String, String>> iterator = ListArticle.iterator();
		Map<String, String> mapArticle = null;

		while (iterator.hasNext()) {
			mapArticle = iterator.next();
			sbSql2.append("('").append(appid).append("',").append(TypeID).append(",'").append(mapArticle.get("rid")).append("','").append(mapArticle.get("rval")).append("','").append(mapArticle.get("rtab")).append("',0,'").append(Common.GetDateTime()).append("'),");
		}
		sbSql2.delete(sbSql2.length() - 1, sbSql2.length());

		// }
		// else {
		// sbSql2.append("(").append(TypeID).append(",'").append(FileID).append("','").append(Common.GetDateTime()).append("')");
		// }
		lstSql.add(sbSql2.toString());
		lstSql.add("update recommendationinfo set updatetime='".concat(Common.GetDateTime()).concat("' where appid='").concat(appid).concat("' and important = 0 and typeid=").concat(TypeID));
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static List<String> getRecommendationFileID(String TypeID) {
		List<Map<String, Object>> lstRecommendInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRecommendInfo = dbHelper.ExecuteQuery("fileid", "recommendationinfo", "typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstRecommendInfo == null) {
			return null;
		}
		List<String> lstRecomdInfo = new ArrayList<String>();
		for (Map<String, Object> map : lstRecommendInfo) {
			lstRecomdInfo.add(String.valueOf(map.get("fileid")));
		}

		return lstRecomdInfo;
	}

	public static boolean disRecommendArticles(String appid, String TypeID, String FileID) {
		boolean bResult = false;
		StringBuilder sbSql1 = new StringBuilder();
		sbSql1.append("update recommendationinfo set updatetime='").append(Common.GetDateTime()).append("' where appid='").append(appid).append("' and typeid=").append(TypeID).append(" and important in (select tabb.important from(select important from recommendationinfo where fileid in ('").append(FileID.replace(",", "','")).append("'))tabb)");
		StringBuilder sbSql2 = new StringBuilder();
		sbSql2.append("delete from  imageinfo where foreignid in (select id from recommendationinfo where appid='").append(appid).append("' and typeid=").append(TypeID).append(" and fileid in ('").append(FileID.replace(",", "','")).append("'))");
		StringBuilder sbSql3 = new StringBuilder();
		sbSql3.append("delete from  recommendationinfo where appid='").append(appid).append("' and typeid=").append(TypeID).append(" and fileid in ('").append(FileID.replace(",", "','")).append("')");
		List<String> lstSql = new ArrayList<String>();
		lstSql.add(sbSql1.toString());
		lstSql.add(sbSql2.toString());
		lstSql.add(sbSql3.toString());
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean delRecommendArticles(String appid, String FileID) {
		boolean bResult = false;
		StringBuilder sbSql1 = new StringBuilder();
		sbSql1.append("update recommendationinfo set updatetime='").append(Common.GetDateTime()).append("' where EXISTS (select tabb.* from (select * from  recommendationinfo  where id in ('").append(FileID.replace(",", "','")).append("') and appid='").append(appid).append("' and important = 1)tabb) and important = 1 ");
		StringBuilder sbSql2 = new StringBuilder();
		sbSql2.append("update recommendationinfo set updatetime='").append(Common.GetDateTime()).append("' where typeid in (select tabc.typeid from(select typeid from recommendationinfo where id in ('").append(FileID.replace(",", "','")).append("'))tabc) and appid='").append(appid).append("' and ").append(" important = 0 ");
		StringBuilder sbSql3 = new StringBuilder();
		sbSql3.append("delete from  recommendationinfo where appid='").append(appid).append("' and id in ('").append(FileID.replace(",", "','")).append("')");
		StringBuilder sbSql4 = new StringBuilder();
		sbSql4.append("delete from  imageinfo where foreignid in ('").append(FileID.replace(",", "','")).append("')");
		List<String> lstSql = new ArrayList<String>();
		lstSql.add(sbSql1.toString());
		lstSql.add(sbSql2.toString());
		lstSql.add(sbSql3.toString());
		lstSql.add(sbSql4.toString());
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static int getRecommendCount(String appid, String KeyWord, String TypeID) {
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		StringBuilder sbCondition = new StringBuilder();
		sbCondition.append("appid ='").append(appid).append("' AND ");
		if (KeyWord != null && KeyWord.length() > 0) {
			sbCondition.append("title like '%").append(dbHelper.FilterSpecialCharacter(KeyWord)).append("%'  AND ");
		}
		if (TypeID != null && TypeID.length() > 0) {
			sbCondition.append("typeid ='").append(dbHelper.FilterSpecialCharacter(TypeID)).append("'").append(" AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		try {
			iCount = dbHelper.GetCount("recommendationinfo", sbCondition.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iCount;
	}

	public static List<Map<String, Object>> getRecommendList(String appid, String KeyWord, String TypeID, int Start, int Length) {
		List<Map<String, Object>> lstRet = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		StringBuilder sbCondition = new StringBuilder();
		sbCondition.append("appid ='").append(appid).append("' AND ");
		if (KeyWord != null && KeyWord.length() > 0) {
			sbCondition.append("title like '%").append(dbHelper.FilterSpecialCharacter(KeyWord)).append("%' AND ");
		}
		if (TypeID != null && TypeID.length() > 0) {
			sbCondition.append("typeid ='").append(dbHelper.FilterSpecialCharacter(TypeID)).append("'").append(" AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		try {
			lstRet = dbHelper.ExecuteQuery("id,title,typeid,fileid,tablename,important,time", "recommendationinfo", sbCondition.toString(), "important desc,time desc", Start, Length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lstRet;
	}

	public static List<Map<String, Object>> getRecommendTypeID(String appid) {
		List<Map<String, Object>> lstRecommendTypeID = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRecommendTypeID = dbHelper.ExecuteQuery("sourcedb", "recommendationtype","appid='".concat(appid).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRecommendTypeID;
	}

	public static List<Map<String, Object>> getRecommendationTypeList(String appid) {
		List<Map<String, Object>> lstRecommendationType = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRecommendationType = dbHelper.ExecuteQuery("select tabb.id,tabb.name_ch,tabb.name_en from recommendationtype taba LEFT join sourcedatabase tabb on taba.sourcedb=tabb.id where taba.appid ='".concat(appid).concat("' ORDER BY showorder"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRecommendationType;
	}

	public static boolean saveRecomTypeOrder(String appid, List<Map<String, String>> lstRecomType) {
		boolean bResult = false;
		List<String> lstSql = new ArrayList<String>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSql.add("delete from recommendationtype where appid='".concat(appid).concat("'"));

			if (lstRecomType != null) {
				for (Map<String, String> imap : lstRecomType) {
					lstSql.add("insert into recommendationtype(sourcedb,appid,showorder,time) values(".concat(imap.get("sourcedb")).concat(",'").concat(appid).concat("',").concat(imap.get("showorder")).concat(",'").concat(Common.GetDateTime()).concat("')"));
				}
			}
			bResult = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/*
	 * 针对页面没有分页的请况，如果每页了，就不成立。
	 */
	public static boolean remmendSourceType(String appid, String TypeID) {
		boolean bResult = false;
		List<String> lstSql = new ArrayList<String>();
		if (Common.IsNullOrEmpty(TypeID)) {
			lstSql.add("delete from recommendationtype where appid='".concat(appid).concat("'"));
		} else {
			lstSql.add("delete from recommendationtype where appid='".concat(appid).concat("' and sourcedb not in(".concat(TypeID).concat(")")));
			int iOrder = getMaxOrder();
			String[] arrTypeID = TypeID.split(",");
			for (String str : arrTypeID) {
				++iOrder;
				lstSql.add("insert into recommendationtype(sourcedb,appid,showorder,time) select ".concat(str).concat(",'").concat(appid).concat("',").concat(String.valueOf(iOrder)).concat(",'").concat(Common.GetDateTime()).concat("' from DUAL where not EXISTS (SELECT id FROM recommendationtype WHERE sourcedb=").concat(str).concat(")"));
			}
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	private static int getMaxOrder() {
		List<Map<String, Object>> lstMaxOrder = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstMaxOrder = dbHelper.ExecuteQuery("select MAX(showorder) as maxval from recommendationtype");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstMaxOrder == null || lstMaxOrder.get(0).get("maxval") == null) {
			return 0;
		} else {
			return Integer.parseInt(String.valueOf(lstMaxOrder.get(0).get("maxval")));
		}

	}

	public static String getLatestTime(String appid, String Type) {
		List<Map<String, Object>> lstRet = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRet = dbHelper.ExecuteQuery("updatetime", "recommendationinfo", "appid='".concat(appid).concat("' and typeid=(select id from sourcedatabase where name_en='").concat(Type).concat("') and important=0"), "time desc", 1, 1);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstRet == null) {
			return "";
		}
		return String.valueOf(lstRet.get(0).get("updatetime"));
	}

	public static String getLatestTime(String appid) {
		List<Map<String, Object>> lstRet = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRet = dbHelper.ExecuteQuery("updatetime", "recommendationinfo", "appid = '".concat(appid).concat("' and important=1"), "", 1, 1);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstRet == null) {
			return "";
		}
		return String.valueOf(lstRet.get(0).get("updatetime"));
	}

	public static List<Map<String, Object>> getRecommendList(String appid, String Type, int Start, int Length) {
		List<Map<String, Object>> lstRet = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRet = dbHelper.ExecuteQuery("title,fileid,description", "recommendationinfo", "appid='"+appid+"' and typeid=(select id from sourcedatabase where name_en='".concat(Type).concat("')").concat(" and important=0"), "id desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRet;
	}

	public static boolean isHasRecommend(String TypeID, String FileID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("recommendationinfo", "typeid=".concat(TypeID).concat(" and fileid='").concat(FileID).concat("'")) > 0) {
				bResult = true;
			} else {
				bResult = false;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static String getLatestUpdateImportant() {
		List<Map<String, Object>> lstRet = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRet = dbHelper.ExecuteQuery("taba.id,if(taba.time>tabc.time,taba.time,tabc.time) as time, tabb.id imageid,title,tabc.name_en,fileid,description", "(recommendationinfo taba left join imageinfo tabb on taba.id=tabb.foreignid) left JOIN sourcedatabase tabc on taba.typeid =tabc.id", "taba.important=1 and tabb.module=1", "time desc", 1, 1);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstRet == null) {
			return "";
		}
		return String.valueOf(lstRet.get(0).get("time"));
	}

	public static List<Map<String, Object>> getImportantRecommendList(String appid) {
		List<Map<String, Object>> lstRet = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRet = dbHelper.ExecuteQuery("taba.id, tabb.id imageid,title,tabc.name_en,fileid,description", "(recommendationinfo taba left join imageinfo tabb on taba.id=tabb.foreignid) left JOIN sourcedatabase tabc on taba.typeid =tabc.id", "taba.appid='".concat(appid).concat("' and ").concat("taba.important=1 and tabb.module=1"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRet;
	}
}
