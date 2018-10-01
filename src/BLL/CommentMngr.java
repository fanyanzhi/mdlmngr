package BLL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import DAL.DBHelper;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CommentMngr {

	/**
	 * 
	 * @param UserName
	 * @param TypeID
	 * @param FileID
	 * @param Title
	 * @param Score
	 * @param Content
	 * @return
	 */
	public static boolean addCommentInfo(String UserName, String TypeID, String FileID, String Title, String Score, String Content, String KeyWord, String AppID) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert(getCommentTable(), new String[] { "username", "typeid", "fileid", "title", "score", "display", "content", "keyword", "appid", "updatetime", "time" }, new Object[] { UserName, TypeID, FileID, Title, Score, filterWord(Content), Content, KeyWord, AppID, Common.GetDateTime(), Common.GetDateTime() });
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	private static int filterWord(String Content) {
		Map<String, Object> mapWord = getSensitiveWord();
		if (mapWord == null) {
			return 1;
		}
		String strSensitiveWord = String.valueOf(mapWord.get("words"));
		String[] arrWords = strSensitiveWord.split(";");
		for (int i = 0; i < arrWords.length; i++) {
			if (Content.replace(" ", "").contains(arrWords[i])) {
				return 0;
			}
		}
		return 1;
	}

	public static int getCommentCount(String UserName, String StartDate, String EndDate, String DocuName, String Content, String IsDisplay) {
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
		if (DocuName != null && DocuName.length() > 0) {
			sbCondition.append("title like '%").append(dbHelper.FilterSpecialCharacter(DocuName)).append("%' AND ");
		}
		if (Content != null && Content.length() > 0) {
			sbCondition.append("Content like '%").append(dbHelper.FilterSpecialCharacter(Content)).append("%' AND ");
		}
		if (IsDisplay != null && IsDisplay.length() > 0) {
			sbCondition.append("display =").append(dbHelper.FilterSpecialCharacter(IsDisplay)).append(" AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] objParam = new Object[2];
		objParam[0] = "commentinfo";
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

	public static List<Map<String, Object>> getCommentList(String UserName, String StartDate, String EndDate, String DocuName, String Content, String IsDisplay, int Start, int Length) {
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
		if (DocuName != null && DocuName.length() > 0) {
			sbCondition.append("title like '%").append(dbHelper.FilterSpecialCharacter(DocuName)).append("%' AND ");
		}
		if (Content != null && Content.length() > 0) {
			sbCondition.append("content like '%").append(dbHelper.FilterSpecialCharacter(Content)).append("%' AND ");
		}
		if (IsDisplay != null && IsDisplay.length() > 0) {
			sbCondition.append("display =").append(dbHelper.FilterSpecialCharacter(IsDisplay)).append(" AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] objParam = new Object[5];
		objParam[0] = "commentinfo";
		objParam[1] = "id,username,title,content,time,score";
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

	public static int getUserCommentCount(String UserName) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		String strCondition = "username = '".concat(dbHelper.FilterSpecialCharacter(UserName)).concat("'");
		Object[] objParam = new Object[2];
		objParam[0] = "commentinfo";
		objParam[1] = strCondition;
		List<Map<String, Object>> lstComment = null;
		try {
			lstComment = dbHelper.ExecuteQueryProc("up_GetViewCount", objParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstComment == null) {
			return 0;
		}
		return Integer.parseInt(lstComment.get(0).get("totalcount").toString());
	}

	public static List<Map<String, Object>> getUserCommentList(String UserName, int Start, int Length) {
		List<Map<String, Object>> lstComment = null;
		DBHelper dbHelper = null;
		Object[] objParam = new Object[5];
		try {
			dbHelper = DBHelper.GetInstance();
			objParam[0] = "commentinfo";
			objParam[1] = "id,typeid,fileid,title,content,time,score";
			objParam[2] = "username = '".concat(dbHelper.FilterSpecialCharacter(UserName)).concat("'");
			objParam[3] = Start;
			objParam[4] = Length;
			lstComment = dbHelper.ExecuteQueryProc("up_GetViewData", objParam);
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return lstComment;
	}

	public static boolean delComment(Map<String, List<String>> mapCommentID) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		List<String> lstSql = new ArrayList<String>();
		for (Map.Entry<String, List<String>> entry : mapCommentID.entrySet()) {
			StringBuilder sbSql = new StringBuilder();
			sbSql.append(" delete from ").append(entry.getKey()).append(" where id in(");
			for (int i = 0; i < entry.getValue().size(); i++) {
				sbSql.append(entry.getValue().get(i)).append(",");
			}
			sbSql.delete(sbSql.length() - 1, sbSql.length());
			sbSql.append(")");
			lstSql.add(sbSql.toString());
		}
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	public static boolean delComment(String ID, String UserName, String Time) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(getCommentTable(Time), "id=".concat(dbHelper.FilterSpecialCharacter(ID)).concat(" and username='").concat(UserName).concat("'"));
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}
	public static boolean delComment(String UserName, JSONArray jsonArray) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			List<String> lstSql = new ArrayList<String>();
			for(Object object:jsonArray){
				StringBuilder sbSql = new StringBuilder();
				JSONObject jsonObject = (JSONObject)object;
				Iterator keyIter =jsonObject.keys();
				while(keyIter.hasNext()){
					String key = (String)keyIter.next();
					String value = (String) jsonObject.get(key);
					sbSql.append(" delete from ").append(value).append(" where id =").append(key);
				}
				lstSql.add(sbSql.toString());
			}
			bRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	/***
	 * 
	 * @param ID
	 * @param UserName
	 * @param Time
	 * @param Score
	 * @param Content
	 * @return
	 */
	public static boolean updateComment(String ID, String UserName, String Time, String Score, String Content) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update(getCommentTable(Time), "id=".concat(ID).concat(" and username='").concat(UserName).concat("'"), new String[] { "content", "display", "score", "updatetime" }, new Object[] { Content, filterWord(Content), Score, Common.GetDateTime() });
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	public static boolean insertSensitiveWord(String SensitiveWord) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("sensitivewordinfo", new String[] { "words", "module", "time" }, new Object[] { dbHelper.FilterSpecialCharacter(SensitiveWord).replace("；", ";").replace("；", ";").replace("；", ";"), 1, Common.GetDateTime() });
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	public static boolean updateSensitiveWord(String ID, String SensitiveWord) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (SensitiveWord == null || SensitiveWord.isEmpty()) {
				bRet = dbHelper.Delete("sensitivewordinfo", "id=".concat(ID));
			} else {
				bRet = dbHelper.Update("sensitivewordinfo", "id=".concat(ID), new String[] { "words", "module", "time" }, new Object[] { dbHelper.FilterSpecialCharacter(SensitiveWord).replace("；", ";").replace("；", ";").replace("；", ";"), 1, Common.GetDateTime() });
			}
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	public static Map<String, Object> getSensitiveWord() {
		List<Map<String, Object>> lstWord = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstWord = dbHelper.ExecuteQuery("id,words", "sensitivewordinfo");
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (lstWord == null) {
			return null;
		}
		return lstWord.get(0);
	}

	public static List<Map<String, Object>> getFrontCommentList(String TypeID, String FileID, int Start, int Length) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (TypeID != null && TypeID.length() > 0) {
			sbCondition.append("typeid = '").append(dbHelper.FilterSpecialCharacter(TypeID)).append("' AND ");
		}
		if (FileID != null && FileID.length() > 0) {
			sbCondition.append("fileid = '").append(dbHelper.FilterSpecialCharacter(FileID)).append("' AND ");
		}
		sbCondition.append("display = 1");
		Object[] objParam = new Object[5];
		objParam[0] = "commentinfo";
		objParam[1] = "id,username,title,content,time,score";
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

	private static String getCommentTable() {
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

		return "commentinfo".concat(strYear).concat(strTablePost);
	}

	private static String getCommentTable(String DateTime) {
		String strNow = Common.ConvertToDateTime(DateTime, "yyyy-MM-dd HH:mm:ss").replace("-", "");
		String strYear = strNow.substring(2, 4);
		String strMonth = strNow.substring(4, 6);
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

		return "commentinfo".concat(strYear).concat(strTablePost);
	}

	public static boolean getTypeClosedStatus(String TypeID) {
		boolean bolRet = false;
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("isclose", "commentcontrol", "type=1 or (type=2 and typeid='".concat(TypeID).concat("')"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return bolRet;
		}
		for (Map<String, Object> mapTemp : lstInfo) {
			if (1 == (int) mapTemp.get("isclose")) {
				bolRet = true;
				break;
			}
		}
		return bolRet;
	}

	public static List<String> getDocuControlInfo(String TypeID) {
		List<Map<String, Object>> lstRecommendInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRecommendInfo = dbHelper.ExecuteQuery("fileid", "commentcontrol", "type=3 and typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));
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

	public static boolean getAllClostStatus() {
		boolean bolRet = false;
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("isclose", "commentcontrol", "type=1");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return bolRet;
		}
		for (Map<String, Object> mapTemp : lstInfo) {
			if (1 == (int) mapTemp.get("isclose")) {
				bolRet = true;
				break;
			}
		}
		return bolRet;
	}

	public static List<String> getCommentSort() {
		List<Map<String, Object>> lstCommentSort = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstCommentSort = dbHelper.ExecuteQuery("typeid", "commentcontrol", "type=2 and isclose = 1");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstCommentSort == null) {
			return null;
		}
		List<String> lstSort = new ArrayList<String>();
		for (Map<String, Object> map : lstCommentSort) {
			lstSort.add(String.valueOf(map.get("typeid")));
		}

		return lstSort;
	}

	public static boolean openAllComment() {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("commentcontrol", "type=1", new String[] { "isclose", "time" }, new Object[] { 0, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	public static boolean closeAllComment() {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("commentcontrol", "type=1") > 0) {
				bRet = dbHelper.Update("commentcontrol", "type=1", new String[] { "isclose", "time" }, new Object[] { 1, Common.GetDateTime() });
			} else {
				bRet = dbHelper.Insert("commentcontrol", new String[] { "type", "isclose", "time" }, new Object[] { 1, 1, Common.GetDateTime() });
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	public static boolean openSortComment(String TypeID) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("commentcontrol", "type=2 and typeid='".concat(TypeID).concat("'"), new String[] { "isclose", "time" }, new Object[] { 0, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	public static boolean closeSortComment(String TypeID) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("commentcontrol", "type=2 and typeid='".concat(TypeID).concat("'")) > 0) {
				bRet = dbHelper.Update("commentcontrol", "type=2 and typeid='".concat(TypeID).concat("'"), new String[] { "isclose", "time" }, new Object[] { 1, Common.GetDateTime() });
			} else {
				bRet = dbHelper.Insert("commentcontrol", new String[] { "type", "typeid", "isclose", "time" }, new Object[] { 2, TypeID, 1, Common.GetDateTime() });
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	/***
	 * 关闭文献评论
	 * 
	 * @param TypeID
	 * @param FileID
	 * @param ListArticle
	 * @return
	 */
	public static boolean closeArticlesComment(String TypeID, String FileID, List<Map<String, String>> ListArticle) {
		boolean bResult = false;
		List<String> lstSql = new ArrayList<String>();
		StringBuilder sbSql1 = new StringBuilder();
		sbSql1.append("delete from  commentcontrol where typeid='").append(TypeID).append("' and fileid in ('").append(FileID.replace(",", "','")).append("')");
		lstSql.add(sbSql1.toString());
		StringBuilder sbSql2 = new StringBuilder();
		sbSql2.append("insert into commentcontrol(type,typeid,fileid,title,tablename,isclose,time) values");
		Iterator<Map<String, String>> iterator = ListArticle.iterator();
		Map<String, String> mapArticle = null;

		while (iterator.hasNext()) {
			mapArticle = iterator.next();
			sbSql2.append("(3,'").append(TypeID).append("','").append(mapArticle.get("rid")).append("','").append(mapArticle.get("rval")).append("','").append(mapArticle.get("rtab")).append("',1,'").append(Common.GetDateTime()).append("'),");
		}
		sbSql2.delete(sbSql2.length() - 1, sbSql2.length());

		lstSql.add(sbSql2.toString());
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/**
	 * 打开文献评论
	 * 
	 * @param TypeID
	 * @param FileID
	 * @return
	 */
	public static boolean openArticlesComment(String TypeID, String FileID) {
		boolean bResult = false;
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("delete from  commentcontrol where typeid='").append(TypeID).append("' and fileid in ('").append(FileID.replace(",", "','")).append("')");
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.ExecuteSql(sbSql.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean openArticlesComment(String ID) {
		boolean bResult = false;
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("delete from  commentcontrol where id in ('").append(ID.replace(",", "','")).append("')");
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.ExecuteSql(sbSql.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static int getArticlesCommentCount() {
		int iRet = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			// iRet = dbHelper.GetCount("commentcontrol",
			// "typeid is not NULL and fileid is not NULL");
			iRet = dbHelper.GetCount("commentcontrol", "type = 3");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iRet;
	}

	public static List<Map<String, Object>> getArticlesCommentList(int Start, int Length) {
		List<Map<String, Object>> lstDecuComment = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			// lstDecuComment =
			// dbHelper.ExecuteQuery("id,title,typeid,fileid,time,type",
			// "commentcontrol", "typeid is not NULL and fileid is not NULL",
			// "time desc", Start, Length);
			lstDecuComment = dbHelper.ExecuteQuery("taba.id,taba.title,taba.typeid,taba.fileid,taba.time,taba.type,tabb.name_ch", "commentcontrol taba left join sourcedatabase tabb on taba.typeid = tabb.name_en ", "type = 3", "time desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstDecuComment;
	}

	public static boolean isShow(String TypeID, String FileID) {
		boolean bolRet = true;
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("isclose", "commentcontrol", "type=1 or (type=2 and typeid='".concat(TypeID).concat("') or (type=3 and typeid='").concat(TypeID).concat("' and fileid='").concat(FileID).concat("')"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return bolRet;
		}
		for (Map<String, Object> mapTemp : lstInfo) {
			if (1 == (int) mapTemp.get("isclose")) {
				bolRet = false;
				break;
			}
		}
		return bolRet;
	}

	/***
	 * 类表中评论总条数
	 * 
	 * @param MapParam
	 * @return
	 */
	public static Map<String, Integer> getCommentCount(Map<String, String> MapParam) {
		Map<String, Integer> mapCount = new HashMap<String, Integer>();
		List<String> lstComTab = getExistsCommentTable();
		StringBuilder sbCondition = new StringBuilder();
		for (Entry<String, String> entry : MapParam.entrySet()) {
			sbCondition.append("(typeid = '").append(entry.getKey()).append("' and fileid ='").append(entry.getValue()).append("' and display = 1) or ");
			mapCount.put(entry.getKey().concat("_").concat(entry.getValue()), 0);
		}
		sbCondition.delete(sbCondition.length() - 4, sbCondition.length());

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		List<Map<String, Object>> lstCount = null;
		for (String strTab : lstComTab) {
			try {
				lstCount = dbHelper.ExecuteQuery("typeid,fileid ,count(*) as count", strTab, sbCondition.toString().concat(" GROUP BY typeid,fileid"));
				if (lstCount == null) {
					continue;
				}
				Iterator<Map<String, Object>> iterator = lstCount.iterator();
				Map<String, Object> mapData = null;
				while (iterator.hasNext()) {
					mapData = iterator.next();
					String strKey = String.valueOf(mapData.get("typeid")).concat("_").concat(String.valueOf(mapData.get("fileid")));
					mapCount.put(strKey, mapCount.get(strKey) + Integer.parseInt(String.valueOf(mapData.get("count"))));
				}
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}
		return mapCount;
	}

	public static List<String> getExistsCommentTable() {
		List<Map<String, Object>> lstCommentTable = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstCommentTable = dbHelper.ExecuteQuery("tablename", "tableindex", "modeltable='commentinfo'", "tablename desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstCommentTable == null) {
			return null;
		}
		List<String> lstCommentTabInfo = new ArrayList<String>();
		for (Map<String, Object> map : lstCommentTable) {
			lstCommentTabInfo.add(String.valueOf(map.get("tablename")));
		}

		return lstCommentTabInfo;
	}

	/******** PraiseMngr *******************/
	/**
	 * 
	 * @param UserName
	 * @param TypeID
	 * @param FileID
	 * @return
	 */
	public static boolean isExistsUserPraise(String UserName, String TypeID, String FileID) {
		String strTableName = getFilePraiseTable(TypeID, FileID);
		List<Map<String, Object>> lstUser = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUser = dbHelper.ExecuteQuery("username", strTableName, "TypeID = '".concat(TypeID).concat("' and FileID ='").concat(FileID).concat("'"));
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (lstUser == null) {
			return false;
		}
		String strUser = String.valueOf(lstUser.get(0).get("username"));
		String[] arrUser = strUser.split(";");
		List<String> listUser = Arrays.asList(arrUser);
		if (listUser.contains(UserName)) {
			return true;
		}
		return false;
	}

	/**
	 * 添加点赞信息
	 * 
	 * @param UserName
	 * @param TypeID
	 * @param FileID
	 * @param Title
	 * @param Title
	 * @param AppID
	 * @return
	 */
	public static boolean addPraiseInfo(String UserName, String TypeID, String FileID, String Title, String KeyWord, String AppID) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		String fileTable = getFilePraiseTable(TypeID, FileID);
		String userTable = getUserPraiseTable(UserName);
		String strCondition = "TypeID = '".concat(TypeID).concat("' and FileID ='").concat(FileID).concat("'");
		List<String> lstSql = new ArrayList<String>();
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount(fileTable, strCondition) > 0) {
				lstSql.add("update ".concat(fileTable).concat(" SET username = concat(username,'").concat(UserName).concat(";'),count = count + 1 where ").concat(strCondition));
			} else {
				lstSql.add("insert into ".concat(fileTable).concat("(username, typeid, fileid, title, count, time)values(';").concat(UserName).concat(";','").concat(TypeID).concat("','").concat(FileID).concat("','").concat(Title).concat("',1,'").concat(Common.GetDateTime()).concat("')"));
			}
			lstSql.add("insert into ".concat(userTable).concat("(username, typeid, fileid, title, keyword, appid, inserttime)values('").concat(UserName).concat("','").concat(TypeID).concat("','").concat(FileID).concat("','").concat(Title).concat("','").concat(Common.IsNullOrEmpty(KeyWord)?"":KeyWord).concat("','").concat(AppID).concat("','").concat(Common.GetDateTime()).concat("')"));
			bRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	/**
	 * 
	 * @param UserName
	 * @param TypeID
	 * @param FileID
	 * @param Title
	 * @return
	 */
	public static boolean cancelPraise(String UserName, String TypeID, String FileID) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		String fileTable = getFilePraiseTable(TypeID, FileID);
		String userTable = getUserPraiseTable(UserName);
		String strCondition = "TypeID = '".concat(TypeID).concat("' and FileID ='").concat(FileID).concat("'");
		List<String> lstSql = new ArrayList<String>();
		try {
			dbHelper = DBHelper.GetInstance();
			lstSql.add("update ".concat(fileTable).concat(" SET username = replace(username,';").concat(UserName).concat(";',';'),count = count - 1 where ").concat(strCondition));
			lstSql.add("delete from ".concat(userTable).concat(" where username='").concat(UserName).concat("' and ".concat(strCondition)));
			bRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	/**
	 * 
	 * @param TypeID
	 * @param FileID
	 * @return
	 */
	public static int getPraiseCount(String TypeID, String FileID) {
		String strTableName = getFilePraiseTable(TypeID, FileID);
		List<Map<String, Object>> lstCount = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstCount = dbHelper.ExecuteQuery("count", strTableName, "TypeID = '".concat(TypeID).concat("' and FileID ='").concat(FileID).concat("'"));
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (lstCount == null) {
			return 0;
		}
		return Integer.parseInt(String.valueOf((lstCount.get(0).get("count"))));
	}

	public static Map<String, Integer> getPraiseCount(Map<String, String> MapParam) {
		Map<String, Integer> mapCount = new HashMap<String, Integer>();

		Map<String, String> mapTabCon = new HashMap<String, String>();

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		for (Entry<String, String> entry : MapParam.entrySet()) {
			mapCount.put(entry.getKey().concat("_").concat(entry.getValue()), 0);
			String strTab = getFilePraiseTable(entry.getKey(), entry.getValue());
			if (mapTabCon.containsKey(strTab)) {
				mapTabCon.put(strTab, mapTabCon.get(strTab).concat("(typeid = '").concat(dbHelper.FilterSpecialCharacter(entry.getKey())).concat("' and fileid ='").concat(dbHelper.FilterSpecialCharacter(entry.getValue())).concat("') or "));
			} else {
				mapTabCon.put(strTab, "(typeid = '".concat(dbHelper.FilterSpecialCharacter(entry.getKey())).concat("' and fileid ='").concat(dbHelper.FilterSpecialCharacter(entry.getValue())).concat("') or "));
			}
		}

		List<Map<String, Object>> lstCount = null;
		StringBuilder sbSql = new StringBuilder();
		for (Entry<String, String> entry : mapTabCon.entrySet()) {
			sbSql.append("(select typeid,fileid,count from ").append(entry.getKey()).append(" where ").append(entry.getValue().substring(0, entry.getValue().length() - 4)).append(") union all ");
		}
		if (sbSql.length() > 0) {
			sbSql.delete(sbSql.length() - 11, sbSql.length());
		}
		try {
			lstCount = dbHelper.ExecuteQuery(sbSql.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		if (lstCount != null) {
			Iterator<Map<String, Object>> iterator = lstCount.iterator();
			Map<String, Object> mapData = null;
			while (iterator.hasNext()) {
				mapData = iterator.next();
				String strKey = String.valueOf(mapData.get("typeid")).concat("_").concat(String.valueOf(mapData.get("fileid")));
				mapCount.put(strKey, mapCount.get(strKey) + Integer.parseInt(String.valueOf(mapData.get("count"))));
			}
		}
		return mapCount;
	}
	public static int getUserPraiseCount(String UserName) {
		String strTableName = getUserPraiseTable(UserName);
		int count=0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			count=dbHelper.GetCount(strTableName, "username='" + UserName + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return count;
	}
	/**
	 * 
	 * @param TypeID
	 * @param FileID
	 * @return
	 */
	public static String getPraiseUsers(String TypeID, String FileID) {
		String strTableName = getFilePraiseTable(TypeID, FileID);
		List<Map<String, Object>> lstUser = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUser = dbHelper.ExecuteQuery("username", strTableName, "TypeID = '".concat(TypeID).concat("' and FileID ='").concat(FileID).concat("'"));
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (lstUser == null) {
			return "";
		}
		return Common.Trim(String.valueOf(lstUser.get(0).get("username")), ";");
	}

	public static List<Map<String, Object>> getUserPraises(String UserName, int Start, int Length) {
		String strTableName = getUserPraiseTable(UserName);
		List<Map<String, Object>> lstRet = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRet = dbHelper.ExecuteQuery("typeid,fileid,title,inserttime", strTableName, "username='" + UserName + "'", "id desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRet;
	}

	/**
	 * 根据typeid和fileid获取对应的推荐表，推荐表为praiseinfo，在cajcloud索引表中有相应的记录
	 * 
	 * @param TypeID
	 * @param FileID
	 * @return
	 */
	private static String getFilePraiseTable(String TypeID, String FileID) {
		return "praiseinfo".concat(String.valueOf(Math.abs(Common.EnCodeMD5(TypeID.concat(FileID)).hashCode())).substring(0, 1));
	}

	/**
	 * 
	 * @param TypeID
	 * @param FileID
	 * @return
	 */
	private static String getUserPraiseTable(String UserName) {
		return "userpraise".concat(String.valueOf(Math.abs(Common.EnCodeMD5(UserName).hashCode())).substring(0, 1));
	}

	public static int getPraiseCount(String Title) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (Title != null) {
			sbCondition.append("title like '%").append(dbHelper.FilterSpecialCharacter(Title)).append("%' ");
		}
		Object[] arrParam = new Object[3];
		arrParam[0] = "praiseinfo";
		arrParam[1] = sbCondition.toString();
		arrParam[2] = "cloudtable";
		List<Map<String, Object>> lstPraise = null;
		try {
			lstPraise = dbHelper.ExecuteQueryProc("sp_getDataCount", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			return 0;
		}
		arrParam = null;
		if (lstPraise == null) {
			return 0;
		}
		return Integer.valueOf(lstPraise.get(0).get("totalcount").toString());
	}

	public static List<Map<String, Object>> getPraiseList(String Title, int Start, int Length) {
		List<Map<String, Object>> lstFile = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (Title != null) {
			sbCondition.append("title like '%").append(dbHelper.FilterSpecialCharacter(Title)).append("%' ");
		}
		Object[] arrParam = new Object[6];
		arrParam[0] = "praiseinfo";
		arrParam[1] = "id,title,username,count";
		arrParam[2] = sbCondition.toString();
		arrParam[3] = "cloudtable";
		arrParam[4] = Start;
		arrParam[5] = Length;
		try {
			lstFile = dbHelper.ExecuteQueryProc("sp_getDataRecord", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		arrParam = null;
		return lstFile;
	}

	public static boolean delPraise(Map<String, List<String>> MapPraise) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		List<String> lstSql = new ArrayList<String>();
		for (Map.Entry<String, List<String>> entry : MapPraise.entrySet()) {
			StringBuilder sbSql = new StringBuilder();
			sbSql.append(" delete from ").append(entry.getKey()).append(" where id in(");
			for (int i = 0; i < entry.getValue().size(); i++) {
				sbSql.append(entry.getValue().get(i)).append(",");
			}
			sbSql.delete(sbSql.length() - 1, sbSql.length());
			sbSql.append(")");
			lstSql.add(sbSql.toString());
		}
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		return bRet;
	}

	public static List<String> getExistsPraiseTable() {
		List<Map<String, Object>> lstPraiseTable = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstPraiseTable = dbHelper.ExecuteQuery("tablename", "cloudtable", "tablename like 'praise%'", "tablename desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstPraiseTable == null) {
			return null;
		}
		List<String> lstPraiseTabInfo = new ArrayList<String>();
		for (Map<String, Object> map : lstPraiseTable) {
			lstPraiseTabInfo.add(String.valueOf(map.get("tablename")));
		}

		return lstPraiseTabInfo;
	}

}
