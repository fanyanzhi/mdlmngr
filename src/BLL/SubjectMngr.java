package BLL;

import java.util.List;
import java.util.Map;

import Util.Common;

import DAL.DBHelper;

public class SubjectMngr {

	public static int getSubjectCount(String condition) {
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("zjcls", Common.IsNullOrEmpty(condition) ? "" : "name like '%" + dbHelper.FilterSpecialCharacter(condition) + "%'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getSubjectList(String condition, String order, int iStart, int iLength) {
		List<Map<String, Object>> lstSubject = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubject = dbHelper.ExecuteQuery("id,code,name,ishavechild,grade,parentcode,pathcode,pathname", "zjcls", Common.IsNullOrEmpty(condition) ? "" : "name like '%" + dbHelper.FilterSpecialCharacter(condition) + "%'", Common.IsNullOrEmpty(order) ? "" : order, iStart, iLength);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSubject;
	}
}
