package BLL;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Model.AppealInfoBean;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ScholarMngr {
	/**
	 * @param userName
	 * @param code
	 * @return
	 */
	public static boolean addAtionScholar(String userName, Map<String, String> arg, String appId) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = "userscholar";
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert(strTableName,
					new String[] { "username", "code", "name", "contributor", "investigation", "time", "appid" },
					new Object[] { userName, arg.get("code"), arg.get("name"), arg.get("contributor"),
							arg.get("investigation"), Common.GetDateTime(), appId });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static boolean cancelAtionScholar(String userName, String code) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = "userscholar";
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(strTableName, "username='" + dbHelper.FilterSpecialCharacter(userName)
					+ "' and code in('" + dbHelper.FilterSpecialCharacter(code).replace(",", "','") + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static int checkAtionScholar(String userName, String code) {
		int iRet = -1;
		DBHelper dbHelper = null;
		String strTableName = "userscholar";
		try {
			dbHelper = DBHelper.GetInstance();
			iRet = dbHelper.GetCount(strTableName, "username='" + dbHelper.FilterSpecialCharacter(userName)
					+ "' and code ='" + dbHelper.FilterSpecialCharacter(code) + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iRet;
	}

	public static int getAtionScholarCount(String userName) {
		String strTableName = "userscholar";
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(strTableName, "username='" + dbHelper.FilterSpecialCharacter(userName) + "'");
		} catch (Exception e) {
			// Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getAtionScholarList(String userName) {
		String strTableName = "userscholar";
		List<Map<String, Object>> lstAtionScholar = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAtionScholar = dbHelper.ExecuteQuery("code, name, contributor, investigation, time", strTableName,
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'", "time desc");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAtionScholar;
	}

	// ------------------------------------------------
	/**
	 * 获取作者关注数目（后台作者关注管理）
	 * 
	 * @param userName
	 * @param code
	 *            作者ID
	 * @param strStartDate
	 * @param strEndDate
	 * @return
	 */
	public int getAtionScholarCount2(String userName, String code, String strStartDate, String strEndDate) {
		String strTableName = "userscholar";
		StringBuilder sbCondition = new StringBuilder();
		int iCount = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(userName)) {
				sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName))
						.append("%' and ");
			}
			if (!Common.IsNullOrEmpty(code)) {
				sbCondition.append("code = '").append(dbHelper.FilterSpecialCharacter(code)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(strStartDate)) {
				sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(strStartDate)).append("' AND ");
			}
			if (!Common.IsNullOrEmpty(strEndDate)) {
				sbCondition.append("time <'").append(dbHelper
						.FilterSpecialCharacter(Common.ConvertToDateTime(strEndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
						.append("' AND ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			}
			iCount = dbHelper.GetCount(strTableName, sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public String getScholarUserCount(String userName, String code, String strStartDate, String strEndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (!Common.IsNullOrEmpty(userName)) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(code)) {
			sbCondition.append("code = '").append(dbHelper.FilterSpecialCharacter(code)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(strStartDate)) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(strStartDate)).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(strEndDate)) {
			sbCondition.append("time <'").append(dbHelper
					.FilterSpecialCharacter(Common.ConvertToDateTime(strEndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}

		List<Map<String, Object>> lstCount = null;
		try {
			lstCount = dbHelper.ExecuteQuery("count(DISTINCT username) cut", "userscholar", sbCondition.toString());
		} catch (Exception e) {

		}
		if (lstCount != null && lstCount.size() > 0) {
			return lstCount.get(0).get("cut").toString();
		}
		return "0";
	}

	public List<Map<String, Object>> getAtionScholarList2(String userName, String code, String strStartDate,
			String strEndDate, int iStart, int iLength) {
		String strTableName = "userscholar";
		List<Map<String, Object>> lstAtionScholar = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (!Common.IsNullOrEmpty(userName)) {
				sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName))
						.append("%' and ");
			}
			if (!Common.IsNullOrEmpty(code)) {
				sbCondition.append("code = '").append(dbHelper.FilterSpecialCharacter(code)).append("' and ");
			}
			if (!Common.IsNullOrEmpty(strStartDate)) {
				sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(strStartDate)).append("' AND ");
			}
			if (!Common.IsNullOrEmpty(strEndDate)) {
				sbCondition.append("time <'").append(dbHelper
						.FilterSpecialCharacter(Common.ConvertToDateTime(strEndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
						.append("' AND ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			}

			lstAtionScholar = dbHelper.ExecuteQuery("id,username,code, name, contributor, investigation, time",
					strTableName, sbCondition.toString(), "time DESC ", iStart, iLength);

		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAtionScholar;
	}

	public boolean delAttention(String userName, String id) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete("userscholar", "username='" + dbHelper.FilterSpecialCharacter(userName)
					+ "' and id in('" + id.replace(",", "','") + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static int getScholarActionNum(String code) {
		int count = 0;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			count = dbHelper.GetCount("userscholar", "code = '" + dbHelper.FilterSpecialCharacter(code) + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/*********************************************
	 * 认领相关方法
	 ***************************/
	/**
	 * status为认领状态，1为认领成功的，-1为自己手动解除认领关系的，默认认领完成就是1，不存在人工审核
	 * 
	 * @param userName
	 * @return
	 */
	public static List<Map<String, Object>> getExpertClaim(String userName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			/*
			 * lst = dbHelper.ExecuteQuery(
			 * "expcode,realname,workunit,lastunit,phone,email", "expertclaim", "username='"
			 * + dbHelper.FilterSpecialCharacter(userName) + "' and status = 1");
			 */
			lst = dbHelper.ExecuteQuery("expcode,realname,workunit,lastunit,phone,email", "expertclaim",
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lst;
	}

	public static boolean cancelClaim(String userName, String expcode) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete("expertclaim", "username='" + userName + "' and expcode='" + expcode + "'");
			
			List<Map<String, Object>> authList = dbHelper.ExecuteQuery("select * from expertauth where username='" + userName + "'");	//查询以前的实名认证
			if(null != authList && authList.size() > 0) {
				dbHelper.Delete("expertauth", "username='"+userName+"'");	//删除以前的实名认证
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static boolean existUserClaim(String userName, String expcode) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("expertclaim", "username='" + userName + "' or expcode='" + expcode + "'") > 0)
				return true;
		} catch (Exception e) {

		}
		return false;
	}

	public static boolean saveClaim(String userName, String expcode, String realname, String workunit, String lastunit,
			String phone, String email) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("expertclaim", "username='" + userName + "' and expcode='" + expcode + "'") > 0) {
				bRet = dbHelper.Update("expertclaim", "username='" + userName + "' and expcode='" + expcode + "'",
						new String[] { "realname", "workunit", "lastunit", "phone", "email", "status", "updatetime" },
						new Object[] { realname, workunit, lastunit, phone, email, 1, Common.GetDateTime() });
			} else {
				bRet = dbHelper.Insert("expertclaim",
						new String[] { "username", "expcode", "realname", "workunit", "lastunit", "phone", "email",
								"status", "updatetime", "time" },
						new Object[] { userName, expcode, realname, workunit, lastunit, phone, email, 1,
								Common.GetDateTime(), Common.GetDateTime() });
				
				if (dbHelper.GetCount("expertappeal", "username='" + userName + "'") > 0) {
					dbHelper.Delete("expertappeal", "username='" + userName + "'");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static boolean updateClaim(String userName, String realname, String workunit, String lastunit, String phone,
			String email) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("expertclaim", "username='" + userName + "'",
					new String[] { "realname", "workunit", "lastunit", "phone", "email", "updatetime" },
					new Object[] { realname, workunit, lastunit, phone, email, Common.GetDateTime() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static List<Map<String, Object>> expertstatus(JSONArray jsonArray) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		StringBuilder sbSql = new StringBuilder();
		try {
			dbHelper = DBHelper.GetInstance();
			@SuppressWarnings("unchecked")
			Iterator<Object> it = jsonArray.iterator();
			while (it.hasNext()) {
				JSONObject ob = (JSONObject) it.next();
				String excode = ob.getString("expertcode");
				if (Common.IsNullOrEmpty(excode))
					continue;
				sbSql.append("(select expcode, username from expertclaim where expcode='"
						+ dbHelper.FilterSpecialCharacter(excode) + "' and status = 1) union all ");
			}
			if (sbSql.length() > 0) {
				sbSql.delete(sbSql.length() - 11, sbSql.length());
				lst = dbHelper.ExecuteQuery(sbSql.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lst;
	}

	public static List<Map<String, Object>> expertstatus(JSONArray jsonArray, String milliSeconds) {
		String time = "";
		if (!Common.IsNullOrEmpty(milliSeconds)) {
			try {
				time = Common.getDateTime(Long.parseLong(milliSeconds));
			} catch (Exception e) {

			}
		}
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		StringBuilder sbSql = new StringBuilder();
		try {
			dbHelper = DBHelper.GetInstance();
			@SuppressWarnings("unchecked")
			Iterator<Object> it = jsonArray.iterator();
			while (it.hasNext()) {
				JSONObject ob = (JSONObject) it.next();
				String excode = ob.getString("expertcode");
				if (Common.IsNullOrEmpty(excode))
					continue;
				if (Common.IsNullOrEmpty(time))
					sbSql.append("(select expcode,realname,time from expertclaim where expcode='"
							+ dbHelper.FilterSpecialCharacter(excode) + "' and status = 1) union all ");
				else
					sbSql.append("(select expcode,realname,time from expertclaim where expcode='"
							+ dbHelper.FilterSpecialCharacter(excode) + "' and status = 1 and time>'" + time
							+ "') union all ");
			}
			if (sbSql.length() > 0) {
				sbSql.delete(sbSql.length() - 11, sbSql.length());
				lst = dbHelper.ExecuteQuery(sbSql.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lst;
	}

	public static List<Map<String, Object>> getExpertNews(String begintime, String endtime) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (Common.IsNullOrEmpty(begintime) || Common.IsNullOrEmpty(endtime))
				lst = dbHelper.ExecuteQuery("username, expcode, realname, time", "expertclaim", "status = 1");
			else
				lst = dbHelper.ExecuteQuery("username, expcode, realname, time", "expertclaim", "time > '"+ begintime +"' and time<'" + endtime + "' and status = 1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lst;
	}

	/*********************************************
	 * 认证相关方法
	 **************************/
	/*
	 * 认证相关信息，只有认证不通过的也就是status为-1的时候可以获取认证的信息
	 */
	public static List<Map<String, Object>> getAuthMsg(String userName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("cardnum, front, back, status, cause", "expertauth",
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "' and status = -1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lst;
	}

	/**
	 * 获取认证状态，-2位没有认证过，-1为认证未通过，0为已提交认证未处理，1位认证通过
	 * 
	 * @param userName
	 * @return
	 */
	public static String getAuthStatus(String userName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("status", "expertauth",
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (lst == null || lst.size() == 0)
			return "-2";
		return lst.get(0).get("status").toString();
	}

	public static boolean addAuth(String userName, String cardNum, String front, String back) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("expertauth", "username='" + userName + "'") > 0) {
				bRet = dbHelper.Update("expertauth", "username='" + userName + "'",
						new String[] { "cardnum", "front", "back", "time", "status", "updatetime" },
						new Object[] { cardNum, front, back, Common.GetDateTime(), 0, null });
			} else {
				bRet = dbHelper.Insert("expertauth",
						new String[] { "username", "cardnum", "front", "back", "status", "updatetime", "time", },
						new Object[] { userName, cardNum, front, back, 0, null, Common.GetDateTime() });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static boolean updateAuth(String userName, String cardNum, String front, String back) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("expertauth", "username='" + userName + "'",
					new String[] { "cardnum", "front", "back", "updatetime" },
					new Object[] { cardNum, front, back, Common.GetDateTime() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	/**
	 * 申诉目前只支持对一个的申诉，不能同时多个申诉，申诉入口
	 * 
	 * @param userName
	 * @param expcode
	 * @param realname
	 * @param workunit
	 * @param phone
	 * @param email
	 * @param cause
	 * @param cardNum
	 * @param front
	 * @param back
	 * @return
	 */
	public static boolean addAuthAppeal(String userName, String expcode, String realname, String workunit, String phone,
			String email, String cause, String cardNum, String front, String back) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("expertappeal", "username='" + userName + "'") > 0) {
				bRet = dbHelper.Update("expertappeal", "username='" + userName + "'",
						new String[] { "expcode", "realname", "workunit", "cause", "phone", "email", "time",
								"cardnum", "front", "back", "status", "updatetime", "remark"},
						new Object[] { expcode, realname, workunit, cause, phone, email, Common.GetDateTime(), cardNum,
								front, back, 0, null, null });
			} else {
				bRet = dbHelper.Insert("expertappeal",
						new String[] { "username", "expcode", "realname", "workunit", "phone", "email", "cause",
								"cardnum", "front", "back", "status", "updatetime", "time", },
						new Object[] { userName, expcode, realname, workunit, phone, email, cause, cardNum, front, back,
								0, null, Common.GetDateTime() });
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	/**
	 * -2的时候是可以申诉的，-1的时候也允许申诉，也就是申诉被驳回了，可以继续申诉。1或者0的时候不允许再有申诉，每个用户只允许一个在申诉中的状态
	 * 
	 * @param userName
	 * @return
	 */
	public static String getAuthAppealStatus(String userName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("status", "expertappeal",
					"username='" + dbHelper.FilterSpecialCharacter(userName) + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (lst == null || lst.size() == 0)
			return "-2";
		return lst.get(0).get("status").toString();
	}

	public static List<Map<String, Object>> getAuthAppeal(String userName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("expcode,realname,workunit,phone,email,cause,cardnum,front,back",
					"expertappeal", "username='" + dbHelper.FilterSpecialCharacter(userName) + "' and status = -1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lst;
	}

	public static boolean updateAuthAppeal(String userName, String realname, String workunit, String phone,
			String email, String cause, String front, String back, String cardNum) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("expertappeal", "username='" + userName + "'",
					new String[] { "realname", "workunit", "cause", "phone", "email", "updatetime", "cardnum", "front",
							"back", "status" },
					new Object[] { realname, workunit, cause, phone, email, Common.GetDateTime(), cardNum, front, back,
							0 });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static int updateAppealStatusById(String id, String status, String remark) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			List<Map<String, Object>> list = AppealMngr.getById(id);

			if (null != list) {

				// appealInfo
				Map<String, Object> appealMap = list.get(0);
				AppealInfoBean appealInfoBean = new AppealInfoBean(appealMap);

				String process = appealInfoBean.getProcess() == null ? "" : appealInfoBean.getProcess();
				StringBuilder sb = new StringBuilder(process);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date appealTime = appealInfoBean.getTime();	//申诉时间
				if(null != appealTime) {
					String appealTimeStr = format.format(appealTime);
					sb.append("申诉时间:"+appealTimeStr+",");
				}
				sb.append("处理时间:"+Common.GetDateTime(0) + ",");
				if (status.equals("1")) {
					sb.append("通过,");
				} else if (status.equals("-1")) {
					sb.append("驳回,");
				} else {
					sb.append("待处理,");
				}
				sb.append("备注:"+remark+";");
				
				if (!dbHelper.Update("expertappeal", "id=" + id,
						new String[] { "status", "updatetime", "remark", "process" },
						new Object[] { status, Common.GetDateTime(), remark, sb.toString() }))
					return -1;

				// 拆分expertauth,expertclaim
				if (status.equals("1")) {

					List<Map<String, Object>> claimList = dbHelper
							.ExecuteQuery("select * from expertclaim where username='" + appealInfoBean.getUsername()
									+ "' or expcode='" + appealInfoBean.getExpcode() + "'");
					if (claimList != null && claimList.size() > 0) {	//清除以前的认领成功的相应的数据
						for (Map<String, Object> map : claimList) {
							String username = (String)map.get("username");
							String usernameNow = appealInfoBean.getUsername();
							if(!usernameNow.equalsIgnoreCase(username)) {	//不相等，则此条信息表示的是以前占用expcode的那个人
								List<Map<String, Object>> authList = dbHelper.ExecuteQuery(
										"select * from expertauth where username='" + username + "'");	//查询以前那个人的实名认证
								if(null != authList && authList.size() > 0) {
									dbHelper.Delete("expertauth", "username='"+username+"'");	//删除以前那个人的实名认证
								}
							}
						}
						
						dbHelper.Delete("expertclaim", "username='" + appealInfoBean.getUsername() + "' or expcode='"
								+ appealInfoBean.getExpcode() + "'");
					}
					dbHelper.Insert("expertclaim",	//插入认领成功的数据
							new String[] { "username", "expcode", "realname", "workunit", "phone", "email", "time",
									"updatetime" },
							new Object[] { appealInfoBean.getUsername(), appealInfoBean.getExpcode(),
									appealInfoBean.getRealname(), appealInfoBean.getWorkunit(),
									appealInfoBean.getPhone(), appealInfoBean.getEmail(), Common.GetDateTime(),
									null });
					// expertauth，如果没有身份证照片就不插入			实名认证表
					List<Map<String, Object>> authList = dbHelper.ExecuteQuery(
							"select * from expertauth where username='" + appealInfoBean.getUsername() + "'");
					if (authList == null) {
						if (!Common.IsNullOrEmpty(appealInfoBean.getFront())
								&& !Common.IsNullOrEmpty(appealInfoBean.getBack())) {
							dbHelper.Insert("expertauth",
									new String[] { "username", "cardnum", "front", "back", "cause", "status", "time", "updatetime" },
									new Object[] { appealInfoBean.getUsername(), appealInfoBean.getCardnum(),
											appealInfoBean.getFront(), appealInfoBean.getBack(),
											appealInfoBean.getCause(), 1, Common.GetDateTime(), Common.GetDateTime() });
						}
					} else {
						if (!Common.IsNullOrEmpty(appealInfoBean.getFront())
								&& !Common.IsNullOrEmpty(appealInfoBean.getBack())) {
							dbHelper.Update("expertauth", "username='" + appealInfoBean.getUsername() + "'",
									new String[] { "cardnum", "front", "back", "cause", "status", "updatetime" },
									new Object[] { appealInfoBean.getCardnum(), appealInfoBean.getFront(),
											appealInfoBean.getBack(), appealInfoBean.getCause(), 1,
											Common.GetDateTime() });
						}
					}
					
					if (!Common.IsNullOrEmpty(appealInfoBean.getFront()) && !Common.IsNullOrEmpty(appealInfoBean.getBack())) {
						dbHelper.Insert("expertauthbp",
								new String[] { "username", "cardnum", "front", "back", "time", "realname" },
								new Object[] { appealInfoBean.getUsername(), appealInfoBean.getCardnum(),
										appealInfoBean.getFront(), appealInfoBean.getBack(), Common.GetDateTime(), appealInfoBean.getRealname() });
					}
					
					
				}
			} else {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -2;
		}
		return 1;
	}

	public static boolean updateAppealAuthById(String id, String status) {
		DBHelper dbHelper = null;
		boolean bRet = false;
		try {
			dbHelper = DBHelper.GetInstance();
			StringBuffer sb = new StringBuffer();
			sb.append("select * from expertauth where id=");
			sb.append(id);
			List<Map<String, Object>> list = dbHelper.ExecuteQuery(sb.toString());
			if (list != null && list.size() > 0) {
				String process = list.get(0).get("process") == null ? "" : list.get(0).get("process").toString();
				StringBuilder sb2 = new StringBuilder(process);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Object obj = list.get(0).get("time");	//申请时间
				if(obj != null) {
					sb2.append("申请时间:"+obj.toString()+",");
				}
				sb2.append("处理时间:"+Common.GetDateTime(0) + ",");
				
				if (status.equals("1")) {
					sb2.append("通过;");
				} else if (status.equals("-1")) {
					sb2.append("驳回;");
				} else {
					sb2.append("异常操作;");
				}
	
				bRet = dbHelper.Update("expertauth", "id=" + id, new String[] { "status", "updatetime", "process" },
						new Object[] { status, Common.GetDateTime(), sb2.toString() });
				bRet = true;
				
				if("1".equals(status)) {	//通过则备份身份信息
					String username = list.get(0).get("username") == null ? "" : list.get(0).get("username").toString();
					String cardnum = list.get(0).get("cardnum") == null ? "" : list.get(0).get("cardnum").toString();
					String front = list.get(0).get("front") == null ? "" : list.get(0).get("front").toString();
					String back = list.get(0).get("back") == null ? "" : list.get(0).get("back").toString();
					
					List<Map<String, Object>> claimList = dbHelper.ExecuteQuery("select * from expertclaim where username='"+username+"'");
					String realname = "";
					if (claimList != null && claimList.size() > 0) {
						realname = (String)claimList.get(0).get("realname");
						
					}
					
					dbHelper.Insert("expertauthbp",
							new String[] { "username", "cardnum", "front", "back", "time", "realname" },
							new Object[] { username, cardnum, front, back, Common.GetDateTime(), realname });
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	/**
	 * 只有被认领以后才可以调用该设置接口，type为1位设置关闭。没有该条记录表示打开
	 * 
	 * @param code
	 * @param openStatus
	 * @return
	 */
	public static int setSchloarMsg(String code, String openStatus) {
		int ret = 1;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if ("0".equals(openStatus)) {
				if (dbHelper.Insert("scholarset", new String[] { "code", "type", "time" },
						new Object[] { code, 1, Common.GetDateTime() })) {
					ret = 0;
				}
			} else {
				if (dbHelper.Delete("scholarset", "code='" + code + "' and type=1"))
					ret = 1;
			}
		} catch (Exception e) {
			ret = -1;
		}
		return ret;
	}

	public static int getSchloarMsg(String code) {
		int ret = 1;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			ret = dbHelper.GetCount("scholarset", "code='" + code + "' and type=1") > 0 ? 0 : 1;
		} catch (Exception e) {
		}
		return ret;
	}

}
