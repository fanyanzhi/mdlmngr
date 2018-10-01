package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Util.Common;

import DAL.DBHelper;

public class SourceMngr {

	public static int getSourceTypeCount() {
		int iRet = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iRet = dbHelper.GetCount("sourcedatabase", "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iRet;
	}

	public static List<Map<String, Object>> getSourceType() {
		List<Map<String, Object>> lstSourceType = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSourceType = dbHelper.ExecuteQuery("id,name_ch,name_en,nodataname", "sourcedatabase");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSourceType;
	}

	public static boolean delSourceType(String TypeID) {
		boolean bResult = false;
		List<String> lstSql = new ArrayList<String>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSql.add("delete from sourcedatabase where id='".concat(dbHelper.FilterSpecialCharacter(TypeID).concat("'")));
			lstSql.add("delete from searchfieldinfo where typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));
			lstSql.add("delete from displayfieldinfo where typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));
			lstSql.add("delete from recommendationtype where sourcedb='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));
			bResult = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static List<Map<String, Object>> getSourceType(String TypeID) {
		List<Map<String, Object>> lstSourceType = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSourceType = dbHelper.ExecuteQuery("name_ch,name_en,nodataname", "sourcedatabase", "id='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSourceType;
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

	public static List<Map<String, Object>> getDisplayField(String TypeID) {
		List<Map<String, Object>> lstDisplayField = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstDisplayField = dbHelper.ExecuteQuery("name_ch,name_en", "displayfieldinfo", "typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"), "showorder asc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstDisplayField;
	}
	
	public static List<Map<String, Object>> getOrderField(String TypeID) {
		List<Map<String, Object>> lstOrderField = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOrderField = dbHelper.ExecuteQuery("name_ch,name_en", "orderfieldinfo", "typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"), "showorder asc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOrderField;
	}
	

	public static boolean saveSourceTypeInfo(String TypeID, String FieldNameCH, String FieldNameEN, String strNewODataName, Map<String, List<Map<String, String>>> mapSourceVal) {
		boolean bResult = false;
		List<String> lstSql = new ArrayList<String>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(TypeID)) {
				lstSql.add("update sourcedatabase set name_ch='".concat(FieldNameCH).concat("',name_en='").concat(FieldNameEN).concat("',nodataname='").concat(strNewODataName).concat("',time='").concat(Common.GetDateTime()).concat("' where id='").concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));
				lstSql.add("delete from searchfieldinfo where typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));
				lstSql.add("delete from displayfieldinfo where typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));
				lstSql.add("delete from orderfieldinfo where typeid='".concat(dbHelper.FilterSpecialCharacter(TypeID)).concat("'"));

			} else {
				dbHelper.Insert("sourcedatabase", new String[] { "name_ch", "name_en", "nodataname", "time" }, new Object[] { FieldNameCH, FieldNameEN, strNewODataName, Common.GetDateTime() });
				List<Map<String, Object>> DataSourceVal = dbHelper.ExecuteQuery("id", "sourcedatabase", "name_ch='".concat(FieldNameCH).concat("' and name_en='").concat(FieldNameEN).concat("'"));
				TypeID = String.valueOf(DataSourceVal.get(0).get("id"));

			}
			List<Map<String, String>> lstSeaField = mapSourceVal.get("seafield");
			if (lstSeaField != null) {
				for (Map<String, String> imap : lstSeaField) {
					lstSql.add("insert into searchfieldinfo(typeid,name_ch,name_en,showorder,time) values(".concat(TypeID).concat(",'").concat(imap.get("name_ch")).concat("','").concat(imap.get("name_en")).concat("',").concat(imap.get("showorder")).concat(",'").concat(Common.GetDateTime()).concat("')"));
				}
			}
			List<Map<String, String>> lstDisField = mapSourceVal.get("displayfield");
			if (lstDisField != null) {
				for (Map<String, String> imap : lstDisField) {
					lstSql.add("insert into displayfieldinfo(typeid,name_ch,name_en,showorder,time) values(".concat(TypeID).concat(",'").concat(imap.get("name_ch")).concat("','").concat(imap.get("name_en")).concat("',").concat(imap.get("showorder")).concat(",'").concat(Common.GetDateTime()).concat("')"));
				}
			}
			List<Map<String, String>> lstOrderField = mapSourceVal.get("orderfield");
			if (lstOrderField != null) {
				for (Map<String, String> imap : lstOrderField) {
					lstSql.add("insert into orderfieldinfo(typeid,name_ch,name_en,showorder,time) values(".concat(TypeID).concat(",'").concat(imap.get("name_ch")).concat("','").concat(imap.get("name_en")).concat("',").concat(imap.get("showorder")).concat(",'").concat(Common.GetDateTime()).concat("')"));
				}
			}
			if (lstSql.size() == 0) {
				bResult = true;
			} else {
				bResult = dbHelper.ExecuteSql(lstSql);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean cretSourceTypeTab(String FieldNameEN) {
		boolean bRet = false;
		List<String> arrSql = new ArrayList<String>();
		StringBuilder sbInsertTab = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			List<Map<String, Object>> DataSourceVal = dbHelper.ExecuteQuery("id", "sourcedatabase", "name_en='".concat(FieldNameEN).concat("'"));
			Integer id = (Integer)DataSourceVal.get(0).get("id");
			sbInsertTab.append("insert into cnkifiletable(tablename,soudataid,epubsign,time) values ");
			for (int i = 1; i < 10; i++) {
				String strTab = FieldNameEN.concat(String.valueOf(i));
				String strTabEpub = FieldNameEN.concat("epub").concat(String.valueOf(i));
				arrSql.add(" create table if not exists ".concat(strTab).concat(" like cnkifiletemplate"));
				arrSql.add(" create table if not exists ".concat(strTabEpub).concat(" like cnkifiletemplate"));
				sbInsertTab.append("('").append(strTab).append("',").append(id).append(",").append(0).append(",'").append(Common.GetDateTime()).append("'),").append("('").append(strTabEpub).append("',").append(id).append(",").append(1).append(",'").append(Common.GetDateTime()).append("'),");
			}
			sbInsertTab.delete(sbInsertTab.toString().length() - 1, sbInsertTab.toString().length());
			arrSql.add(sbInsertTab.toString());
		
			bRet = dbHelper.ExecuteSql(arrSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	public static String getJournalID() {
		List<Map<String, Object>> lstJouernal = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstJouernal = dbHelper.ExecuteQuery("id", "sourcedatabase", "name_ch='期刊' and name_en='journals'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstJouernal == null) {
			return null;
		} else {
			return String.valueOf(lstJouernal.get(0).get("id"));
		}

	}
}
