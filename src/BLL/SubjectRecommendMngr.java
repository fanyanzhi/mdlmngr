package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Model.SubjectRecommendInfoBean;
import Util.Common;

public class SubjectRecommendMngr {

	public static boolean addSubjectRecommend(SubjectRecommendInfoBean subject) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("subjectrecommend",
					new String[] { "subjectid","title", "keyword", "type", "istop", "isadv", "isrecomd", "openclass", "linktype", "summary", "simageid",
							"bimageid", "time", "orgname" },
					new Object[] { subject.getSubjectid(), subject.getTitle(), subject.getKeyword(), subject.getType(), subject.getIstop(),subject.getIsadv(),
							subject.getIsrecomd(), subject.getOpenclass(), subject.getLinktype(), subject.getSummary(), subject.getSimageid(),
							subject.getBimageid(), Common.GetDateTime(), "cnki" });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static int getSubjectCount() {
		int result = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			result = dbHelper.GetCount("subjectrecommend", "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return result;
	}

	public static List<Map<String, Object>> getSubjectList(int start, int length) {
		List<Map<String, Object>> lstSubjectInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubjectInfo = dbHelper.ExecuteQuery(
					"id,title,keyword,type,istop,openclass,isrecomd,linktype,summary,simageid,bimageid,time", "subjectrecommend", "orgname='cnki'", "",
					start, length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstSubjectInfo == null) {
			return null;
		} else {
			return lstSubjectInfo;
		}
	}

	public static boolean delSubjectInfo(String id, String smallId, String bigId) {
		boolean result = false;
		List<String> sqls = new ArrayList<String>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			String sql1 = "delete from subjectrecommend where id =" + id;
			sqls.add(sql1);
			if (!Common.IsNullOrEmpty(smallId)) {
				String sql2 = "delete from imageinfo where id=" + smallId;
				sqls.add(sql2);
			}
			if (!Common.IsNullOrEmpty(bigId)) {
				String sql3 = "delete from imageinfo where id=" + bigId;
				sqls.add(sql3);
			}
			result = dbHelper.ExecuteSql(sqls);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return result;
	}

	public static List<Map<String, Object>> getSubjectInfo(String id) {
		List<Map<String, Object>> lstSubjectInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSubjectInfo = dbHelper.ExecuteQuery(
					"subjectid,title,keyword,type,istop,isadv,isrecomd,openclass,linktype,summary,simageid,bimageid", "subjectrecommend",
					"id=" + id);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSubjectInfo;
	}

	public static boolean editSubjectInfo(SubjectRecommendInfoBean subject, String id) {
		boolean result = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			result = dbHelper.Update("subjectrecommend", "id=" + id,
					new String[] { "title", "keyword", "type", "istop", "isadv", "isrecomd", "openclass", "linktype", "summary", "simageid",
							"bimageid", "time" },
					new Object[] { subject.getTitle(), subject.getKeyword(), subject.getType(), subject.getIstop(), subject.getIsadv(),
							subject.getIsrecomd(), subject.getOpenclass(), subject.getLinktype(), subject.getSummary(), subject.getSimageid(),
							subject.getBimageid(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return result;
	}

	public static int getSubjectRecommendCount(String condition) {
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("subjectrecommend",
					Common.IsNullOrEmpty(condition) ? "orgname='cnki'" : "orgname='cnki' and istop=" + dbHelper.FilterSpecialCharacter(condition));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getSubjectRecommendList(String condition, String order, int iStart,
			int iLength) {
		List<Map<String, Object>> lstSubject = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubject = dbHelper.ExecuteQuery("title,keyword,type,istop,linktype,summary,simageid,bimageid",
					"subjectrecommend",
					Common.IsNullOrEmpty(condition) ? "orgname='cnki'"
							: "orgname='cnki' and openclass=0 and isrecomd=1 and istop = " + dbHelper.FilterSpecialCharacter(condition),
					Common.IsNullOrEmpty(order) ? "" : order, iStart, iLength);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSubject;
	}

	public static List<Map<String, Object>> getIndexSubject() {
		List<Map<String, Object>> lstSubject = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubject = dbHelper.ExecuteQuery(
					"title,keyword,type,istop,linktype,summary,simageid,bimageid,isrecomd,time", "subjectrecommend",
					"isrecomd=1 and openclass=0 and orgname='cnki'", "istop desc,time desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSubject;
	}
	
	public static List<Map<String, Object>> getAdvSubject(){
		List<Map<String, Object>> lstSubject = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubject = dbHelper.ExecuteQuery(
					"title,keyword,type,istop,linktype,summary,simageid,bimageid,isrecomd,time", "subjectrecommend",
					"isadv=1 and orgname='cnki'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSubject;
	}
	
	public static List<Map<String, Object>> getRemdSubject(){
		List<Map<String, Object>> lstSubject = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubject = dbHelper.ExecuteQuery(
					"title,keyword,type,istop,linktype,summary,simageid,bimageid,isrecomd,time", "subjectrecommend",
					"isrecomd=1 and openclass=0 and isadv=0 and orgname='cnki'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSubject;
	}

	public static List<Map<String, Object>> getSubjectIdss(String orgname) {
		List<Map<String, Object>> lstResult = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstResult = dbHelper.ExecuteQuery("subjectid", "orgsubject",
					"orgname = '" + dbHelper.FilterSpecialCharacter(orgname) + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstResult == null || lstResult.size() == 0)
			return null;
		return lstResult;
	}

	public static List<Map<String, Object>> getOrgSubject(String subjectid) {
		List<Map<String, Object>> lstResult = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstResult = dbHelper.ExecuteQuery(
					"title,keyword,type,istop,linktype,summary,simageid,bimageid,isrecomd,time", "subjectrecommend",
					"isrecomd=1 and subjectid in('" + subjectid.replace(",", "','") + "')", "istop desc,time desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstResult;
	}

	/** 后台管理页面应用 **/

	public static int getSubjectOrgCount(String subjectId) {
		int count = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			count = dbHelper.GetCount("orgsubject", "subjectid='".concat(subjectId).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return count;
	}

	public static List<Map<String, Object>> getSubjectOrgList(String subjectId, int start, int length) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lstSubjectOrg = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstSubjectOrg = dbHelper.ExecuteQuery("orgname,unitname,time", "orgsubject",
					"subjectid='".concat(subjectId).concat("'"), "time desc", start, length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstSubjectOrg;
	}
	
	public static boolean delSubjectOrg(String orgname, String subjectid){
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("orgsubject", "subjectid = '"+dbHelper.FilterSpecialCharacter(subjectid)+"' and orgname in('" + Common.Trim(orgname, ",").replace(",","','") + "')");

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean exportSubjectOrg(String subjectId, Map<String, String> orgs) {
		DBHelper dbHelper = null;
		boolean result = false;
		try {
			dbHelper = DBHelper.GetInstance();
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("insert into orgsubject(subjectid,orgname,unitname,time)values");
			for (Map.Entry<String, String> entry : orgs.entrySet()) {
				sbSql.append("('").append(subjectId).append("','").append(entry.getKey()).append("','")
						.append(entry.getValue()).append("','").append(Common.GetDateTime()).append("'),");
			}
			sbSql.delete(sbSql.length()-1, sbSql.length());
			result = dbHelper.ExecuteSql(sbSql.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return result;
	}
	public static void main(String[] args) {
		System.out.println(Common.GetDateTime("yyyyMMddhhmmss"));
	}
}
