package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Util.Common;
import DAL.DBHelper;

public class EpubTransMngr {

	public static List<Map<String, Object>> getEpubTransInfoList(String name_en, String filter, String keyword, String start, String len) {
		List<Map<String, Object>> epubTransInfoList = new ArrayList<Map<String, Object>>();

		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (Common.IsNullOrEmpty(filter)) {
			filter = "fileName";
		}
		if (filter.equals("fileName") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("filename like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		} else if (filter.equals("fileID") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("fileid like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[6];
		arrParam[0] = name_en;
		arrParam[1] = "id,fileid,filename,filesize,filemd5,typename,ishasepub,time";
		arrParam[2] = sbCondition.toString();
		arrParam[3] = "cnkifiletable";
		arrParam[4] = start;
		arrParam[5] = len;
		try {
			epubTransInfoList = dbHelper.ExecuteQueryProc("sp_getDataRecord", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return epubTransInfoList;
	}

	public static Integer getEpubTransInfoCount(String name_en, String filter, String keyword) {

		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (Common.IsNullOrEmpty(filter)) {
			filter = "fileName";
		}
		if (filter.equals("fileName") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("filename like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		} else if (filter.equals("fileID") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("fileid like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[3];
		arrParam[0] = name_en;
		arrParam[1] = sbCondition.toString();
		arrParam[2] = "cnkifiletable";
		List<Map<String, Object>> epubTransList = null;
		try {
			epubTransList = dbHelper.ExecuteQueryProc("sp_getDataCount", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			return 0;
		}
		arrParam = null;
		if (epubTransList == null) {
			return 0;
		}
		return Integer.valueOf(epubTransList.get(0).get("totalcount").toString());

	}

	public static List<Map<String, Object>> getTableNameList(String typeid, Integer epubsign) {

		List<Map<String, Object>> cnkiFileTables = null;
		DBHelper dbHelper;
		try {
			dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(typeid)) {
				cnkiFileTables = dbHelper.ExecuteQuery("tablename", "cnkifiletable", "soudataid='".concat(dbHelper.FilterSpecialCharacter(typeid)).concat("'").concat(" and epubsign=").concat(String.valueOf(epubsign)), "tablename");
			} else if (Common.IsNullOrEmpty(typeid)) {
				cnkiFileTables = dbHelper.ExecuteQuery("tablename", "cnkifiletable", "epubsign=".concat(String.valueOf(epubsign)), "tablename");
			}

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return cnkiFileTables;
	}

	public static List<Map<String, Object>> getNoEpubInfoList(String tableName, String epubTableName) {
		List<Map<String, Object>> epubTransInfo = null;
		DBHelper dbHelper;
		try {
			dbHelper = DBHelper.GetInstance();
			epubTransInfo = dbHelper.ExecuteQuery("id,fileid,filename,filesize,filemd5,typename,ishasepub", tableName, "ishasepub is null ".concat(tableName).concat(".fileid NOT IN (SELECT fileid from ").concat(epubTableName).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return epubTransInfo;
	}
	
	public static boolean delODataRecord(String odataType, String fileID){
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete(getFileTable(odataType,fileID), "fileid='".concat(dbHelper.FilterSpecialCharacter(fileID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}
	
	public static String getFileTable(String odataType, String fileID) {
		return odataType.concat(String.valueOf(Math.abs(fileID.hashCode())).substring(0, 1)).toLowerCase();
	}
}
