package BLL;

import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class OrgNoticeMngr {

	/** 后台管理页面应用 **/

	public static int getOrgNoticeCount(String noticeId) {
		int count = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			count = dbHelper.GetCount("orgnotice", "noticeid='".concat(noticeId).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return count;
	}

	public static List<Map<String, Object>> getOrgNoticeList(String subjectId, int start, int length) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lstSubjectOrg = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubjectOrg = dbHelper.ExecuteQuery("orgname,unitname,time", "orgnotice",
					"noticeid='".concat(subjectId).concat("'"), "time desc", start, length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSubjectOrg;
	}

	public static boolean delOrgNotice(String orgname, String subjectid) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("orgnotice", "noticeid = '" + dbHelper.FilterSpecialCharacter(subjectid)
					+ "' and orgname in('" + Common.Trim(orgname, ",").replace(",", "','") + "')");

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean importOrgNotice(String subjectId, Map<String, String> orgs) {
		DBHelper dbHelper = null;
		boolean result = false;
		try {
			dbHelper = DBHelper.GetInstance();
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("insert into orgnotice(noticeid,orgname,unitname,time)values");
			for (Map.Entry<String, String> entry : orgs.entrySet()) {
				sbSql.append("('").append(subjectId).append("','").append(entry.getKey()).append("','")
						.append(entry.getValue()).append("','").append(Common.GetDateTime()).append("'),");
			}
			sbSql.delete(sbSql.length() - 1, sbSql.length());
			result = dbHelper.ExecuteSql(sbSql.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return result;
	}

	public static boolean existOrgNotice(String noticeid, String orgName){
		DBHelper dbHelper = null;
		boolean exist = false;
		try{
			dbHelper = DBHelper.GetInstance();
			if(dbHelper.GetCount("orgnotice", "noticeid='"+noticeid +"' and orgname = '"+orgName+"'")>0)
				exist = true;
		}catch(Exception e){
			
		}
		return exist;
	}
	
	public static void main(String[] args) {
		System.out.println(Common.GetDateTime("yyyyMMddhhmmss"));
	}
}
