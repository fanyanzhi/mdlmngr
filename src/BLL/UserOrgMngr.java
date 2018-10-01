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
import javapns.devices.Devices;

public class UserOrgMngr {

	private static String TokenPassWord = "jdiu$98JH-03H-;@a3k9#~kpb59akj8j";

	public static List<Map<String, Object>> getOrgInfo() {
		List<Map<String, Object>> lstorginfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstorginfo = dbHelper.ExecuteQuery("orgname,type,expirydate,firstq,firsta,secondq,seconda,thirdq,thirda",
					"userorgset");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstorginfo;
	}

	public static void mains(String[] args) {
		// System.out.println(ecpLogin(getEcpToken(), "zwxtest",
		// "123456","202.96.31.95"));
		System.out.println(Common.DecryptData("e+OGEshd/fY=", TokenPassWord));
		// System.out.println(Common.EncryptData("ttod", TokenPassWord));
		/*
		 * System.out.println(ecpBindUser("o16EX1dEcS8meAgun4LNPD8FovBA",
		 * "lwztest", "weixin", "ios", "202.96.31.95"));
		 */
		// System.out.println(ecpThirdLogin("o16EX1dEcS8meAgun4LNPD8FovBA","weixin","202.96.31.95"));
	}

	// 判断是否可用-->判断当天下载次数
	public static boolean isValid(String userName) {
		boolean ret = false;
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("ip,orgname,orgpwd,longitude,latitude,endtime", "userorg", "username='"
					+ dbHelper.FilterSpecialCharacter(userName) + "' and endtime>'" + Common.GetDateTime() + "'");
		} catch (Exception e) {

		}
		if (lst != null && lst.size() > 0) {
			ret = true;
		}
		return ret;
	}

	/**
	 * 判断是否存在有效的机构名和用户名信息
	 * 
	 * @param userName
	 * @param orgName
	 * @return
	 */
	/*
	 * public static boolean existUserInfos(String userName, String orgName) {
	 * boolean bRet = false; DBHelper dbHelper = null; try { dbHelper =
	 * DBHelper.GetInstance(); if (dbHelper.GetCount("userorg", "username='" +
	 * userName + "' and orgname='" + orgName + "' and status=1") > 0) bRet =
	 * true; } catch (Exception E) { System.out.println(E.getMessage()); }
	 * return bRet; }
	 */

	public static int existValidUserInfo(String userName, String orgName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lstRet = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstRet = dbHelper.ExecuteQuery("days,updatetime", "userorg",
					"username='" + userName + "' and orgname='" + orgName + "' and status=1");
			if (lstRet != null && lstRet.size() > 0) {
				String utime = lstRet.get(0).get("updatetime").toString();
				int roamDay = Integer.parseInt(lstRet.get(0).get("days").toString());
				DateTimeFormatter format1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
				DateTime tmptime = DateTime.parse(utime, format1);
				LocalDate updateDate = new LocalDate(tmptime);
				LocalDate curDate = new LocalDate();
				int days = Days.daysBetween(updateDate, curDate).getDays();
				if (days == 0) {
					return 1;
				}
				if (days > roamDay) {
					return 0;
				}
				return roamDay;
			}
		} catch (Exception E) {
			System.out.println(E.getMessage());
		}
		return -1;
	}

	/*
	 * public static boolean existUserInfoTwo(String userName, String orgName) {
	 * boolean bRet = false; DBHelper dbHelper = null; try { dbHelper =
	 * DBHelper.GetInstance(); if (dbHelper.GetCount("userorg", "username='" +
	 * userName + "' and orgname='" + orgName + "'") > 0) bRet = true; } catch
	 * (Exception E) { System.out.println(E.getMessage()); } return bRet; }
	 */
	/**
	 * 存在返回-1，不存在返回0
	 * 
	 * @param userName
	 * @param orgName
	 * @return
	 */
	public static int existUserInfo(String userName, String orgName) {
		int ret = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("userorg", "username='" + userName + "' and orgname='" + orgName + "'") > 0)
				ret = -1;
		} catch (Exception E) {
			System.out.println(E.getMessage());
		}
		return ret;
	}

	public static boolean insertUserOrg(String userName, String ip, String unitname, String orgname, String orgpwd,
			String longitude, String latitude) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {

			dbHelper = DBHelper.GetInstance();
			// LoggerFile.appendMethod("/root/userogr_ExecuteInsertUserOrg",
			// LoggerFile.appendMethod("D:\\userorg_ExecuteInsertUserOrg",
			// "userName " + userName + ";strIP:" + ip +"-->unitname:"+unitname+
			// ";orgname:" +
			// orgname+";longitude:"+longitude+"-->"+System.currentTimeMillis());
			bRet = dbHelper.Insert("userorg",
					new String[] { "username", "ip", "unitname", "orgname", "orgpwd", "longitude", "latitude", "time",
							"status", "updatetime", "days" },
					new Object[] { userName, ip, unitname, orgname,
							Common.IsNullOrEmpty(orgpwd) ? "" : Common.DESEncrypt(orgpwd, TokenPassWord), longitude,
							latitude, Common.GetDateTime(), 1, Common.GetDateTime(), 15 });
		} catch (Exception e) {
			System.out.println("insert:" + e.getMessage());
		}
		return bRet;
	}
	// 更新到用户机构表

	public static boolean updateUserOrg(String userName, String ip, String unitname, String orgname, String orgpwd,
			String longitude, String latitude, int days) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		int tempdays = 0;
		if (days == 1) {
			try {
				dbHelper = DBHelper.GetInstance();
				bRet = dbHelper.Update("userorg", "username='" + userName + "' and orgname='" + orgname + "'",
						new String[] { "ip", "unitname", "orgname", "orgpwd", "longitude", "latitude", "status",
								"updatetime" },
						new Object[] { ip, unitname, orgname,
								Common.IsNullOrEmpty(orgpwd) ? "" : Common.DESEncrypt(orgpwd, TokenPassWord), longitude,
								latitude, 1, Common.GetDateTime() });
			} catch (Exception e) {
				System.out.println("update:" + e.getMessage());
			}
		} else {
			if (days == -1 || days == 0) {
				tempdays = 15;
			} else {
				tempdays = (days + 7) > 60 ? 60 : (days + 7);
			}
			try {
				dbHelper = DBHelper.GetInstance();
				bRet = dbHelper.Update("userorg", "username='" + userName + "' and orgname='" + orgname + "'",
						new String[] { "ip", "unitname", "orgname", "orgpwd", "longitude", "latitude", "status",
								"updatetime", "days" },
						new Object[] { ip, unitname, orgname,
								Common.IsNullOrEmpty(orgpwd) ? "" : Common.DESEncrypt(orgpwd, TokenPassWord), longitude,
								latitude, 1, Common.GetDateTime(), tempdays });
			} catch (Exception e) {
				System.out.println("update:" + e.getMessage());
			}
		}
		return bRet;
	}

	/**
	 * 
	 * @param UserName
	 * @param UnitName
	 * @param strOrg
	 * @return
	 */
	public static int getUserOrgCount(String UserName, String UnitName, String OrgName) {
		int iCount = 0;

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();

		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '%").append(UserName).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(UnitName)) {
			sbCondition.append("unitname like '%").append(UnitName).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(OrgName)) {
			sbCondition.append("orgname like '%").append(OrgName).append("%' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		try {
			iCount = dbHelper.GetCount("userorg", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getUserOrgList(String UserName, String UnitName, String OrgName, int Start,
			int Length) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '%").append(UserName).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(UnitName)) {
			sbCondition.append("unitname like '%").append(UnitName).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(OrgName)) {
			sbCondition.append("orgname like '%").append(OrgName).append("%' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		List<Map<String, Object>> lstRet = null;
		try {
			lstRet = dbHelper.ExecuteQuery("id,username,ip,unitname,orgname,orgpwd,longitude,latitude,time,updatetime,days", "userorg",
					sbCondition.toString(), "updatetime desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstRet;
	}

	public static boolean delUserOrg(String recID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("userorg", "id in (".concat(recID).concat(")"));
			/*
			 * if (bResult) { updateLoginCount(UserName); }
			 */
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	// userorg日志表
	public static boolean insertUserOrgLog(String userName, String ip, String unitname, String orgname, String orgpwd,
			String longitude, String latitude) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("userorglog",
					new String[] { "username", "ip", "unitname", "orgname", "orgpwd", "longitude", "latitude", "time" },
					new Object[] { userName, ip, unitname, orgname,
							Common.IsNullOrEmpty(orgpwd) ? "" : Common.DESEncrypt(orgpwd, TokenPassWord), longitude,
							latitude, Common.GetDateTime() });
		} catch (Exception e) {
			System.out.println("orglog:" + e.getMessage());
		}
		return bRet;
	}

	// userorgtoday 用户当天关联机构的记录信息
	public static boolean insertUserOrgToday(String userName, String ip, String unitname, String orgname, String orgpwd,
			String longitude, String latitude) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("userorgtoday",
					new String[] { "username", "ip", "unitname", "orgname", "orgpwd", "longitude", "latitude", "time" },
					new Object[] { userName, ip, unitname, orgname,
							Common.IsNullOrEmpty(orgpwd) ? "" : Common.DESEncrypt(orgpwd, TokenPassWord), longitude,
							latitude, Common.GetDateTime() });
		} catch (Exception e) {

		}
		return bRet;
	}

	// 获取最新的用户机构信息信息
	public static Map<String, Object> getLatestUserOrg(String userName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("username, ip, unitname, orgname, orgpwd, longitude, latitude, time", "",
					userName = "'" + dbHelper.FilterSpecialCharacter(userName) + "'", "time desc", 0, 1);
		} catch (Exception e) {

		}
		if (lst == null || lst.size() == 0)
			return null;
		Map<String, Object> map = lst.get(0);
		map.put("orgpwd", Common.DESDecrypt(map.get("orgpwd").toString(), TokenPassWord));
		return map;
	}

	public static String getOrgNameByUserName(String userName) {
		List<Map<String, Object>> lstResult = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstResult = dbHelper.ExecuteQuery("orgname", "userorg",
					"username = '" + dbHelper.FilterSpecialCharacter(userName) + "' and status=1", "updatetime desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstResult == null || lstResult.size() == 0)
			return "";
		return lstResult.get(0).get("orgname").toString();
	}

	// 清除昨天的信息
	public static boolean delUserOrgToday() {
		DBHelper dbHelper = null;
		boolean success = false;
		try {
			dbHelper = DBHelper.GetInstance();
			success = dbHelper.Delete("userorgtoday", "time <'" + Common.GetDate() + "'");
		} catch (Exception e) {

		}
		return success;
	}

	public static boolean getOrgSet(String[] result) {
		return true;
	}

	/**
	 * 
	 * @param userName
	 * @param roamDay
	 * @param result
	 * @return -1表示过期，0表示不存在，1表示有效
	 */
	public static int getUserOrgInfo(String userName, int roamDay, String[] result) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lstUserOrg = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUserOrg = dbHelper.ExecuteQuery(
					"ip, unitname, orgname, orgpwd, longitude, latitude, time, updatetime, days, status", "userorg",
					"username='" + userName + "' and status=1", "updatetime desc");
		} catch (Exception e) {
		}
		if (lstUserOrg == null || lstUserOrg.size() == 0) {
			return 0;
		}
		/*
		 * if(!"1".equals(lstUserOrg.get(0).get("status").toString())){ return
		 * false; }
		 */
		String ltime = lstUserOrg.get(0).get("updatetime").toString();
		roamDay = Integer.parseInt(lstUserOrg.get(0).get("days").toString());
		DateTimeFormatter format1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTime tmptime = DateTime.parse(ltime, format1);
		LocalDate lastDate = new LocalDate(tmptime);
		LocalDate curDate = new LocalDate();
		int days = Days.daysBetween(lastDate, curDate).getDays();
		if (days > roamDay) {
			return -1;
		}
		if (Common.IsNullOrEmpty(lstUserOrg.get(0).get("orgpwd").toString())
				|| "null".equalsIgnoreCase(lstUserOrg.get(0).get("orgpwd").toString())) {
			if (Common.IsNullOrEmpty(lstUserOrg.get(0).get("longitude").toString())
					|| "null".equalsIgnoreCase(lstUserOrg.get(0).get("longitude").toString())) {
				result[0] = "0";
				result[1] = lstUserOrg.get(0).get("ip").toString();
			} else {
				result[0] = "1";
				result[1] = lstUserOrg.get(0).get("ip").toString();
				result[2] = lstUserOrg.get(0).get("longitude").toString();
				result[3] = lstUserOrg.get(0).get("latitude").toString();
			}
		} else {
			result[0] = "2";
			result[1] = lstUserOrg.get(0).get("ip").toString();
			result[2] = lstUserOrg.get(0).get("orgname").toString();
			result[3] = lstUserOrg.get(0).get("orgpwd").toString().endsWith("=")
					? Common.DESDecrypt(lstUserOrg.get(0).get("orgpwd").toString(), TokenPassWord)
					: lstUserOrg.get(0).get("orgpwd").toString();
		}
		return 1;

	}

	public static boolean isBlackUser(String userName, String orgName) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.GetCount("orgblacklist",
					"orgname='".concat(orgName).concat("' and username='").concat(userName).concat("'")) > 0;
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	public static boolean isWhiteUser(String userName, String orgName) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.GetCount("orgwhitelist",
					"orgname='".concat(orgName).concat("' and username='").concat(userName).concat("'")) > 0;
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	public static Map<String, Object> getOrgRelevanceSet(String orgName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("sort,validtime,question,answer,iplimit,ip", "orgrelevanceset",
					"orgname='" + orgName + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lst != null && lst.size() > 0)
			return lst.get(0);
		return null;
	}

	public static boolean addUserOrgAudit(String userName, String ip, String unitname, String orgname, String orgpwd,
			String longitude, String latitude) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("userorgaudit",
					new String[] { "username", "ip", "unitname", "orgname", "orgpwd", "longitude", "latitude",
							"updatetime", "time" },
					new Object[] { userName, ip, unitname, orgname,
							Common.IsNullOrEmpty(orgpwd) ? "" : Common.DESEncrypt(orgpwd, TokenPassWord), longitude,
							latitude, Common.GetDateTime(), Common.GetDateTime() });
		} catch (Exception e) {
			System.out.println("insert:" + e.getMessage());
		}
		return bRet;
	}

	/**
	 * 判断是否有待处理的申请
	 * 
	 * @param userName
	 * @param orgname
	 * @return
	 */
	public static boolean existUserOrgAudit(String userName, String orgname) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("userorgaudit",
					"orgname='" + orgname + "' and username='" + userName + "' and status=0") > 0)
				bRet = true;
		} catch (Exception e) {
			System.out.println("count:" + e.getMessage());
		}
		return bRet;
	}

	/**
	 * 如果关联成功已经过去很久了，等了好久不行，然后又申请了另一个，这个反而可以了,只提醒后面一个，还是都提醒，目前是只提醒最后申请的
	 * 
	 * @param userName
	 * @return
	 */
	public static boolean chkOrgAudit_bak(String userName) {
		List<Map<String, Object>> lst = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("status,isalert", "userorgaudit", "username='" + userName + "'", "time desc", 1,
					1);
			if (lst == null || lst.size() == 0)
				return false;
			if ("0".equals(lst.get(0).get("status"))) {
				return true;
			}
			if (!"0".equals(lst.get(0).get("status")) && "0".equals(lst.get(0).get("isalert")))
				return true;
		} catch (Exception e) {
			System.out.println("count:" + e.getMessage());
		}
		return false;
	}

	public static List<Map<String, Object>> chkOrgAuditing(String userName) {
		List<Map<String, Object>> lst = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("unitname,orgname", "userorgaudit",
					"username='" + userName + "' and status =0");
		} catch (Exception e) {
		}
		return lst;
	}

	public static String getOrgAuditNoticeTime(String userName) {
		String strTime = "";
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("time", "userorgaudittime", "username='" + userName + "'");
			if (lst == null || lst.size() == 0) {
				strTime = "";
				dbHelper.Insert("userorgaudittime", new String[] { "username", "time" },
						new Object[] { userName, Common.GetDateTime() });
			} else {
				strTime = lst.get(0).get("time").toString();
				dbHelper.Update("userorgaudittime", "username='" + userName + "'", new String[] { "time" },
						new Object[] { Common.GetDateTime() });
			}
		} catch (Exception e) {

		}
		return strTime;
	}

	public static List<Map<String, Object>> getOrgAuditNotice(String userName, String strTime) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (Common.IsNullOrEmpty(strTime)) {
				lst = dbHelper.ExecuteQuery("unitname,orgname,status,updatetime", "userorgaudit",
						"username='" + userName + "' and status !=0");
			} else {
				lst = dbHelper.ExecuteQuery("unitname,orgname,status,updatetime", "userorgaudit",
						"username='" + userName + "' and status!=0 and time>'" + strTime + "'");
			}
		} catch (Exception e) {

		}
		return lst;
	}

	public static Map<String, Object> getOrgStatus(String userName, int roamDays) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lstOrg = null;
		Map<String, Object> map = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstOrg = dbHelper.ExecuteQuery("unitname,orgname,updatetime,days", "userorg",
					"username='".concat(userName).concat("' and status = 1"), "updatetime desc", 1, 1);
		} catch (Exception e) {

		}
		if (lstOrg != null && lstOrg.size() > 0) {
			String ltime = lstOrg.get(0).get("updatetime").toString();
			int period = Integer.parseInt(lstOrg.get(0).get("days").toString());
			DateTimeFormatter format1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			DateTime tmptime = DateTime.parse(ltime, format1);
			LocalDate lastDate = new LocalDate(tmptime);
			LocalDate curDate = new LocalDate();
			int days = Days.daysBetween(lastDate, curDate).getDays();
			if (days > period) {
				return null;
			}
			map = lstOrg.get(0);
			map.put("days", period - days);
			return map;
		}
		return null;
	}

	/**
	 * 返回为提示过的请求
	 * 
	 * @param userName
	 * @return
	 */
	public static List<Map<String, Object>> orgAuditStatus(String userName) {
		List<Map<String, Object>> lst = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("id,orgname,status,unitname,isalert", "userorgaudit",
					"username='" + userName + "' and status !=0 and isalert=0", "updatetime desc");
		} catch (Exception e) {
			System.out.println("count:" + e.getMessage());
		}
		if (lst == null || lst.size() == 0)
			return null;
		return lst;
	}

	/**
	 * 当前关联的有效的机构信息
	 * 
	 * @param id
	 * @return
	 */
	public static Boolean updOrgAuditStatus(String id) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("userorgaudit", "id in('" + id.replaceAll(",", "','") + "')",
					new String[] { "isalert" }, new Object[] { 1 });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	/**
	 * 是否是暑期活动用户
	 * 
	 * @param args
	 */
	public static boolean existInSummerUser(String userName) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("summeruser", "username='" + userName + "'") > 0)
				return true;
		} catch (Exception e) {

		}
		return false;

	}

	public static void main(String[] args) {
		String ltime = "2017-10-11 22:00:00";
		int roamDay = 15;
		DateTimeFormatter format1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTime tmptime = DateTime.parse(ltime, format1);
		LocalDate lastDate = new LocalDate(tmptime);
		LocalDate curDate = new LocalDate();
		int days = Days.daysBetween(lastDate, curDate).getDays();
		System.out.println(days);
		/*
		 * System.out.println(UserOrgMngr.class.getClassLoader().getResource("")
		 * .getPath()); System.out.println(Common.DESEncrypt("",
		 * TokenPassWord));
		 */
		System.out.println(Common.DESEncrypt("zhangwenxiangbk", TokenPassWord));
		// System.out.println(Common.DESDecrypt("ILyGw9HngcbLog1G1cg3GQ==",
		// TokenPassWord));
		// System.out.println(UserOrgMngr.class.getClassLoader().getResource("").getPath());
		List<String> devices = new ArrayList<String>();
		devices.add("d1b35ee0ef457b1bae8b600b2b164548ba3f9c67ee043c91d9ae95cdd06711f4");
		// devices.add("86a96153c2ac4a3fc4d1e1b3baaee49779ae748cf609c17e91ec93b9e42154ce");
		// JdpushMngr.iospush(Devices.asDevices(devices),
		// "true,您定制的今日更新888篇文献");
	}
}
