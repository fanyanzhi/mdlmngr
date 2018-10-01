package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import DAL.DBHelper;
import Util.Common;
import Util.LoggerFile;
import net.sf.json.JSONObject;

public class SignMngr {

	static int sign = Integer.parseInt(Common.GetConfig("sign"));

	/****************************** app接口 ********************************/
	/**
	 * 根据time值判断积分数和累计签到次数
	 *type 1.签到 2.邀请好友 3.评论 4.分享 5.充值 6.完善资料
	 * @param userName
	 * @param ip
	 * @param version
	 * @return
	 */
	public static String userSign(String userName, String ip, String version, int type) {
		JSONObject result = new JSONObject();
		DBHelper dbHelper = null;
		try {
			int scount = 0;
			int ssum = 0;
			int score = 0;
			int tempscore = 5;
			dbHelper = DBHelper.GetInstance();
			List<String> sql = new ArrayList<String>();
			
			List<Map<String, Object>> lst = dbHelper.ExecuteQuery("scount,ssum,score,time", "usersign",
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'");
			if (lst != null && lst.size() > 0) {
				scount = Integer.parseInt(lst.get(0).get("scount").toString());
				ssum = Integer.parseInt(lst.get(0).get("ssum").toString());
				score = Integer.parseInt(lst.get(0).get("score").toString());
				String ltime = lst.get(0).get("time").toString();
				DateTimeFormatter format1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
				DateTime tmptime = DateTime.parse(ltime, format1);
				LocalDate lastDate = new LocalDate(tmptime);
				LocalDate curDate = new LocalDate();
				int days = Days.daysBetween(lastDate, curDate).getDays();
				if (days == 0) {
					result.put("result", true);
					result.put("status", 1);
					result.put("scount", scount);
					result.put("score", score);
					return result.toString();
				} else if (days == 1) { // 判断是不是已经签到，是不是昨天，是不是前天等
					scount = scount + 1;
					ssum = ssum + 1;
					tempscore = getScore(scount);
					score = score + getScore(scount);
					result.put("result", true);
					result.put("status", 1);
					result.put("scount", scount);
					result.put("score", score);
				} else if (days > 1) {
					scount = 1;
					ssum = ssum + 1;
					tempscore = getScore(scount);
					score = score + getScore(scount);
					result.put("result", true);
					result.put("status", 1);
					result.put("scount", scount);
					result.put("score", score);
				}
				//暑期活动添加score
				/*if (scount > 2) {
					if (!UserOrgMngr.existInSummerUser(userName)) {
						sql.add("insert into summeruser(username,orgname,password,ip,time)values('" + userName
								+ "','ttod','" + Common.DESEncrypt("ttod", "jdiu$98JH-03H-;@a3k9#~kpb59akj8j")
								+ "','118.194.175.254','" + Common.GetDateTime() + "')");
					}
				}*/
				sql.add("update usersign set scount=" + scount + ",ssum=" + ssum + ",score=" + score + ",time='"
						+ Common.GetDateTime() + "' where username='" + dbHelper.FilterSpecialCharacter(userName)
						+ "'");
			} else {
				score = getScore(1);
				result.put("result", true);
				result.put("status", 1);
				result.put("scount", 1);
				result.put("score", score);
				sql.add("insert into usersign(username,scount,ssum,score,time)values('" + userName + "',1,1," + score
						+ ",'" + Common.GetDateTime() + "')");
			}
			String table = getTableName("usersignlog", userName);
			sql.add("insert into " + table + "(username,ip,version,type,score,time) values('" + userName + "','" + ip + "','"
					+ version + "'," + type + ","+ tempscore +",'" + Common.GetDateTime() + "')");
			if (dbHelper.ExecuteSql(sql)) {
				return result.toString();
			}
		} catch (Exception e) {
			LoggerFile.appendMethod("/root/sing.txt", e.getMessage());
		}
		result.put("result", false);
		result.put("message", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		result.put("errorcode", String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		return result.toString();

	}

	/**
	 * 根据time值判断积分数和累计签到次数
	 * 
	 * @param userName
	 * @return
	 */
	public static Map<String, Object> userStatus(String userName) {
		DBHelper dbHelper = null;
		Map<String, Object> map = null;
		try {
			dbHelper = DBHelper.GetInstance();
			List<Map<String, Object>> lst = dbHelper.ExecuteQuery("scount,ssum,score,time", "usersign",
					"username='".concat(dbHelper.FilterSpecialCharacter(userName).concat("'")));
			if (lst != null && lst.size() > 0)
				map = lst.get(0);
		} catch (Exception e) {
			// 失败
		}
		return map;
	}

	/**
	 * 获取积分
	 * 
	 * @param userName
	 * @return
	 */
	public static int signScore(String userName) {
		DBHelper dbHelper = null;
		Map<String, Object> map = null;
		try {
			dbHelper = DBHelper.GetInstance();
			List<Map<String, Object>> lst = dbHelper.ExecuteQuery("score", "usersign",
					"username='".concat(dbHelper.FilterSpecialCharacter(userName).concat("'")));
			if (lst != null && lst.size() > 0)
				map = lst.get(0);
			else
				return 0;
		} catch (Exception e) {
			// 失败
		}
		return Integer.parseInt(map.get("score").toString());
	}

	public static boolean setSignScore(String username, int score) {
		boolean bret = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bret = dbHelper.Update("usersign",
					"username='".concat(dbHelper.FilterSpecialCharacter(username)).concat("'"),
					new String[] { "score" }, new Object[] { score });
		} catch (Exception e) {
		}
		return bret;
	}

	/**
	 * 添加用户积分消费记录
	 * 
	 * @param username
	 * @param score
	 * @param type：默认为1，1为文献下载
	 * @return
	 */
	public static boolean usersignexpense(String username, int score, int type) {
		boolean bret = false;
		DBHelper dbHelper = null;
		String table = getTableName("usersignexpense", username);
		try {
			dbHelper = DBHelper.GetInstance();
			bret = dbHelper.Insert(table, new String[] { "username", "score", "type", "time" },
					new Object[] { username, score, type, Common.GetDateTime() });
		} catch (Exception e) {
		}
		return bret;
	}

	public static List<Map<String, Object>> signExpense(String userName, int iStart, int iLength) {
		List<Map<String, Object>> lstResult = null;
		DBHelper dbHelper = null;
		String table = getTableName("usersignexpense", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			lstResult = dbHelper.ExecuteQuery("type,score,time", table,
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'", "time desc", iStart, iLength);
		} catch (Exception e) {
		}
		return lstResult;
	}
	
	/**
	 * 获取积分列表
	 * @param userName
	 * @param iStart
	 * @param iLength
	 * @return
	 */
	public static List<Map<String, Object>> obtainScore(String userName, int iStart, int iLength) {
		List<Map<String, Object>> lstResult = null;
		DBHelper dbHelper = null;
		String table = getTableName("usersignlog", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			lstResult = dbHelper.ExecuteQuery("type,score,time", table,
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'", "time desc", iStart, iLength);
		} catch (Exception e) {
		}
		return lstResult;
	}

	public static int getScore(int signCount) {
		/*
		 * if (signCount < 8) { return 1; } else if (signCount < 15) { return 5;
		 * } else { return 10; }
		 */
		if (signCount < 6)
			return sign * signCount;
		return sign * 6;
	}

	private static String getTableName(String tablename, String UserName) {
		return tablename.concat(String.valueOf(Math.abs(Common.EnCodeMD5(UserName).hashCode())).substring(0, 1));
	}

	/**************************** 后台页面 *************************************/

	public static int getUserSignCount(String UserName, String StartDate, String EndDate, String txtscount,
			String txtssum) {
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
			sbCondition.append("time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("' AND ");
		}
		if (!Common.IsNullOrEmpty(txtscount)) {
			sbCondition.append("scount >" + txtscount + " AND ");
		}
		if (!Common.IsNullOrEmpty(txtssum)) {
			sbCondition.append("ssum >" + txtssum + " AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		int count = 0;
		try {
			count = dbHelper.GetCount("usersign", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return count;
	}

	public static List<Map<String, Object>> getUserSignList(String userName, String startDate, String endDate,
			String txtscount, String txtssum, int start, int length) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (userName != null && userName.length() > 0) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' AND ");
		}
		if (startDate != null && startDate.length() > 0) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(startDate)).append("' AND ");
		}
		if (endDate != null && endDate.length() > 0) {
			sbCondition.append("time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(endDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("' AND ");
		}
		if (!Common.IsNullOrEmpty(txtscount)) {
			sbCondition.append("scount >" + txtscount + " AND ");
		}
		if (!Common.IsNullOrEmpty(txtssum)) {
			sbCondition.append("ssum >" + txtssum + " AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		List<Map<String, Object>> lstUserSign = null;
		try {
			lstUserSign = dbHelper.ExecuteQuery("username,scount,ssum,score,time", "usersign", sbCondition.toString(),
					"time desc", start, length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstUserSign;
	}

	public static List<Map<String, Object>> exportUserSignList(String userName, String startDate, String endDate,
			String txtscount, String txtssum) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		if (userName != null && userName.length() > 0) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' AND ");
		}
		if (startDate != null && startDate.length() > 0) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(startDate)).append("' AND ");
		}
		if (endDate != null && endDate.length() > 0) {
			sbCondition.append("time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(endDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("' AND ");
		}
		if (!Common.IsNullOrEmpty(txtscount)) {
			sbCondition.append("scount >" + txtscount + " AND ");
		}
		if (!Common.IsNullOrEmpty(txtssum)) {
			sbCondition.append("ssum >" + txtssum + " AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		List<Map<String, Object>> lstUserSign = null;
		try {
			lstUserSign = dbHelper.ExecuteQuery("username,scount,ssum,score,time", "usersign", sbCondition.toString(),
					"time desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstUserSign;
	}

	/***************** 签到日志 *********************/
	public static int getUserSignDetailCount(String userName, String startDate, String endDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		sbCondition.append("type = 1 AND ");
		if (userName != null && userName.length() > 0) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' AND ");
		}
		if (startDate != null && startDate.length() > 0) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(startDate)).append("' AND ");
		}
		if (endDate != null && endDate.length() > 0) {
			sbCondition.append("time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(endDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		String table = getTableName("usersignlog", userName);
		int count = 0;
		try {
			count = dbHelper.GetCount(table, sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return count;
	}

	public static List<Map<String, Object>> getUserSignLogList(String userName, String startDate, String endDate,
			int start, int length) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		sbCondition.append("type = 1 AND ");
		if (userName != null && userName.length() > 0) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' AND ");
		}
		if (startDate != null && startDate.length() > 0) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(startDate)).append("' AND ");
		}
		if (endDate != null && endDate.length() > 0) {
			sbCondition.append("time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(endDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		String table = getTableName("usersignlog", userName);
		List<Map<String, Object>> lstUserSignLog = null;
		try {
			lstUserSignLog = dbHelper.ExecuteQuery("username,ip,version,time", table, sbCondition.toString(),
					"time desc", start, length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstUserSignLog;
	}

	public static void main(String[] args) {
		// DateTime nowTime = new DateTime();
		LocalDate curDate = new LocalDate();
		DateTimeFormatter format1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTime tmptime = DateTime.parse("2017-03-20 14:47:15", format1);
		LocalDate start = new LocalDate(tmptime);
		int days = Days.daysBetween(start, curDate).getDays();
		System.out.println(days);

	}
}
