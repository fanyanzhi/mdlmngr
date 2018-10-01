package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.NoticeInfoBean;
import Util.Common;

import DAL.DBHelper;

public class NoticeMngr {

	public static int getNoticeCount() {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("noticeinfo", "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getNoticeList(int Start, int Length) {
		List<Map<String, Object>> lstNotice = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstNotice = dbHelper.ExecuteQuery("id,title,content,time,ispublic", "noticeinfo", "", "time desc", Start,
					Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstNotice;
	}

	public static List<Map<String, Object>> getPublicNoticeList(String orgname) {
		List<Map<String, Object>> lstNotice = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstNotice = dbHelper.ExecuteQuery("title,content,type,time,ispublic,noticeid", "noticeinfo",
					"time>'".concat(Common.ConvertToDateTime(Common.GetDate(), "yyyy-MM-dd", -7 * 24 * 3600 * 1000))
							.concat("'"),
					"time desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstNotice;
	}

	public static List<Map<String, Object>> getUserNoticeList(String UserName) {
		List<Map<String, Object>> lstNotice = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstNotice = dbHelper.ExecuteQuery("title,content,time", "noticeinfo",
					"ispublic=0 and id in (select noticeid from usernotice where userid=(select id from userinfo where username='"
							.concat(UserName).concat("'))"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstNotice;
	}

	public static boolean delNotice(String NoticeID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("noticeinfo",
					"id in(".concat(dbHelper.FilterSpecialCharacter(NoticeID)).concat(")"));

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static Map<String, Object> getNoticeInfo(String NoticeID) {
		List<Map<String, Object>> NoticeInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			NoticeInfo = dbHelper.ExecuteQuery("id,noticeid,title,content,ispublic,type,time", "noticeinfo",
					"id='".concat(dbHelper.FilterSpecialCharacter(NoticeID)).concat("'"));

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (NoticeInfo == null) {
			return null;
		}
		return NoticeInfo.get(0);

	}

	public static boolean publishNoticeInfo(NoticeInfoBean NoticeInfo, String UserID) {
		String strID = "";
		List<Map<String, Object>> lstResult = null;

		// if(UserID==null||UserID.length()==0){
		// strSql="insert into noticeinfo(title,content,ispublic,time)
		// values('".concat(Title).concat("','").concat(NoticeInfo).concat("',1,'").concat(Common.GetDateTime()).concat("')");
		// }else{
		// strSql="insert into noticeinfo(title,content,ispublic,time)
		// values('".concat(Title).concat("','").concat(NoticeInfo).concat("',0,'").concat(Common.GetDateTime()).concat("')");
		// }
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.Insert("noticeinfo",
					new String[] { "noticeid", "title", "content", "ispublic", "type", "time" },
					new String[] { NoticeInfo.getNoticeid(), NoticeInfo.getTitle(), NoticeInfo.getContent(),
							String.valueOf(NoticeInfo.getIsPublic()), String.valueOf(NoticeInfo.getType()),
							Common.GetDateTime() })) {
				lstResult = dbHelper.ExecuteQuery("select max(id) as id from noticeinfo");
			} else {
				return false;
			}
			strID = String.valueOf(lstResult.get(0).get("id"));
			if (0 == NoticeInfo.getIsPublic()) {
				String[] arrID = Common.Trim(UserID, ",").split(",");
				int iCount = arrID.length;

				List<String> lstSql = new ArrayList<String>(16);
				String strPre = "INSERT INTO usernotice (username,noticeid) VALUES ";
				StringBuilder sbIDs = new StringBuilder();
				for (int i = 1; i <= iCount; i++) {
					sbIDs.append("('").append(arrID[i - 1]).append("',").append(strID).append("),");
					if (i % 15 == 0 || i == iCount) {
						String strSQL = strPre.concat(sbIDs.toString());
						strSQL = strSQL.substring(0, strSQL.length() - 1);
						lstSql.add(strSQL);
						sbIDs = new StringBuilder();
					}
				}
				if (!dbHelper.ExecuteSql(lstSql)) {
					dbHelper.ExecuteSql("delete from usernotice where noticeid='"
							.concat(dbHelper.FilterSpecialCharacter(strID)).concat("'"));
					return false;
				}
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return true;
	}

	public static boolean updateNoticeInfo(NoticeInfoBean NoticeInfo, String UserID) {
		String NoticeID = String.valueOf(NoticeInfo.getId());
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (!dbHelper.Update("noticeinfo", "id='".concat(dbHelper.FilterSpecialCharacter(NoticeID)).concat("'"),
					new String[] { "title", "content", "ispublic", "type" },
					new String[] { NoticeInfo.getTitle(), NoticeInfo.getContent(),
							String.valueOf(NoticeInfo.getIsPublic()), String.valueOf(NoticeInfo.getType()) })) {
				return false;
			}
			dbHelper.Delete("usernotice", "noticeid='".concat(dbHelper.FilterSpecialCharacter(NoticeID)).concat("'"));
			if (0 == NoticeInfo.getIsPublic()) {
				String[] arrID = Common.Trim(UserID, ",").split(",");
				int iCount = arrID.length;

				List<String> lstSql = new ArrayList<String>(16);
				String strPre = "INSERT INTO usernotice (username,noticeid) VALUES ";
				StringBuilder sbIDs = new StringBuilder();
				for (int i = 1; i <= iCount; i++) {
					sbIDs.append("('").append(arrID[i - 1]).append("',").append(NoticeID).append("),");
					if (i % 15 == 0 || i == iCount) {
						String strSQL = strPre.concat(sbIDs.toString());
						strSQL = strSQL.substring(0, strSQL.length() - 1);
						lstSql.add(strSQL);
						sbIDs = new StringBuilder();
					}
				}
				if (!dbHelper.ExecuteSql(lstSql)) {
					dbHelper.ExecuteSql("delete from usernotice where noticeid='"
							.concat(dbHelper.FilterSpecialCharacter(NoticeID)).concat("'"));
					return false;
				}
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return true;
	}

	public static boolean delNoticeRelationship(String NoticeID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("usernotice",
					"noticeid in(".concat(dbHelper.FilterSpecialCharacter(NoticeID)).concat(")"));

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static List<Map<String, Object>> getRelationUserID(String NoticeID) {
		List<Map<String, Object>> lstUserID = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstUserID = dbHelper.ExecuteQuery("id,username", "userinfo",
					"username in (select username from usernotice where noticeid='"
							.concat(dbHelper.FilterSpecialCharacter(NoticeID)).concat("')"));

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstUserID;
	}

	public static int getOrgNoticeCount(String orgName, String time) {
		int count = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if(Common.IsNullOrEmpty(time))
				count = dbHelper.GetCount("notice", "orgName='" + dbHelper.FilterSpecialCharacter(orgName) + "' and isSend=1 and editTime>'"+Common.GetDateTime(-7 * 24 * 3600 * 1000)+"'");//一周
			else
				count = dbHelper.GetCount("notice", "orgName='" + dbHelper.FilterSpecialCharacter(orgName) + "' and isSend=1 and editTime > '" + dbHelper.FilterSpecialCharacter(time) + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return count;
	}

	public static List<Map<String, Object>> getOrgNotice(String orgName, String time) {
		List<Map<String, Object>> lstNotice = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if(Common.IsNullOrEmpty(time))
				lstNotice = dbHelper.ExecuteQuery("name,content,editTime,isSend,sender,imageId,type,orgName", "notice",
					"orgName='" + dbHelper.FilterSpecialCharacter(orgName) + "' and isSend=1 and editTime>'"+Common.GetDateTime(-7 * 24 * 3600 * 1000)+"'", "editTime desc");
			else
				lstNotice = dbHelper.ExecuteQuery("name,content,editTime,isSend,sender,imageId,type,orgName", "notice",
						"orgName='" + dbHelper.FilterSpecialCharacter(orgName) + "' and isSend=1 and editTime > '" + dbHelper.FilterSpecialCharacter(time) + "'", "editTime desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstNotice;
	}

	/**
	 * 0为获取机构消息标志
	 * @param username
	 * @param flag
	 * @return
	 */
	public static String getUserNoticeFlag(String username, int flag) {
		List<Map<String, Object>> lstNoticeFlag = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstNoticeFlag = dbHelper.ExecuteQuery("time", "usernoticeflag",
					"username = '" + username + "' and flag=" + flag);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstNoticeFlag == null || lstNoticeFlag.size() == 0)
			return "";
		return lstNoticeFlag.get(0).get("time").toString();
	}

	public static boolean setUserNoticeFlag(String username, int flag) {
		boolean ret = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("usernoticeflag", "username = '" + username + "' and flag=" + flag) > 0) {
				ret = dbHelper.Update("usernoticeflag", "username = '" + username + "' and flag=" + flag,
						new String[] { "time" }, new Object[] { Common.GetDateTime() });
			} else {
				ret = dbHelper.Insert("usernoticeflag", new String[] { "username", "flag", "time" },
						new Object[] { username, flag, Common.GetDateTime() });
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return ret;
	}
}
