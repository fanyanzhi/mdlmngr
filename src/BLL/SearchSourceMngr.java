package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class SearchSourceMngr {

	

	public static List<Map<String, Object>> getSearchSourceTypeList() {
		List<Map<String, Object>> lstRecommendationType = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstRecommendationType = dbHelper.ExecuteQuery("select tabb.id,tabb.name_ch,tabb.name_en,tabb.nodataname from searchsourcetype taba LEFT join sourcedatabase tabb on taba.sourcedb=tabb.id ORDER BY showorder");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRecommendationType;
	}

	public static boolean saveSearchSourceTypeOrder(List<Map<String, String>> lstRecomType) {
		boolean bResult = false;
		List<String> lstSql = new ArrayList<String>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSql.add("delete from searchsourcetype");

			if (lstRecomType != null) {
				for (Map<String, String> imap : lstRecomType) {
					lstSql.add("insert into searchsourcetype(sourcedb,showorder,time) values(".concat(imap.get("sourcedb")).concat(",").concat(imap.get("showorder")).concat(",'").concat(Common.GetDateTime()).concat("')"));
				}
			}
			bResult = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}
	public static boolean SearchSourceType(String TypeID) {
		boolean bResult = false;
		List<String> lstSql = new ArrayList<String>();
		if (Common.IsNullOrEmpty(TypeID)) {
			lstSql.add("delete from searchsourcetype");
		} else {
			lstSql.add("delete from searchsourcetype where sourcedb not in(".concat(TypeID).concat(")"));
			int iOrder = getMaxOrder();
			String[] arrTypeID = TypeID.split(",");
			for (String str : arrTypeID) {
				++iOrder;
				lstSql.add("insert into searchsourcetype(sourcedb,showorder,time) select ".concat(str).concat(",").concat(String.valueOf(iOrder)).concat(",'").concat(Common.GetDateTime()).concat("' from DUAL where not EXISTS (SELECT id FROM searchsourcetype WHERE sourcedb=").concat(str).concat(")"));
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
			lstMaxOrder = dbHelper.ExecuteQuery("select MAX(showorder) as maxval from searchsourcetype");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstMaxOrder == null || lstMaxOrder.get(0).get("maxval") == null) {
			return 0;
		} else {
			return Integer.parseInt(String.valueOf(lstMaxOrder.get(0).get("maxval")));
		}

	}
	public static List<Map<String, Object>> getSearchSourceTypeID() {
		List<Map<String, Object>> lstSearchSourceTypeID = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSearchSourceTypeID = dbHelper.ExecuteQuery("sourcedb", "searchsourcetype");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSearchSourceTypeID;
	}
}
