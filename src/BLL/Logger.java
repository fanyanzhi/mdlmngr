package BLL;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import DAL.DBHelper;
import Model.HttpContext;
import Util.Common;

public class Logger {
	
	public static  enum BaseOsNames{
		
		IPHONE("iOS-iPhone"),
		IPAD("iOS-iPad"),
		IPOD("iOS-iPod"),
		ANDROID("android"),
		WINDOWS("windows"),
		MACOS("macos"),
		OTHER("other");
		public String value;
		BaseOsNames(String value){
			this.value = value;
		}
		public String toString() {
			return super.toString();
		}
	}

	public enum DownloadTraceStatus {

		/** 通过下载链接进入下载 */
		getdownload(1),
		/** 进入全文下载 */
		hfmsdownload(2),
		/** 后台对错误的原文重新下载 */
		backstage(3),
		/** 上传全文hfms */
		hfmsupload(4),
		/** 从中心网站下载原文 */
		sourcefile(5),
		/** pdf转epub记录 */
		pdf2epub(6),
		/** 验证下载权限 */
		chkuserauthority(7),
		/** 后台处理数据*/
		redowncnkifile(8),
		/** 付费*/
		feefile(9);
		
		public int value;

		DownloadTraceStatus(int value) {
			this.value = value;
		}

		public String toString() {
			return super.toString();
		}
	}

	public static void WriteException(Exception arg) {
		StringBuilder sbTrace = new StringBuilder();
		StackTraceElement[] arrSTE = arg.getStackTrace();
		for (int i = 0; i < 20 && i < arrSTE.length; i++) {
			sbTrace.append(arrSTE[i]).append("\r\n");
		}
		int iCount = sbTrace.length();
		if (iCount > 0) {
			sbTrace.delete(iCount - 2, iCount);
		}
		WriteException(arg.toString(), sbTrace.toString());
		arrSTE = null;
		sbTrace = null;
	}

	public static void WriteException(String Message, String StackTrace) {
		DBHelper dbHelper;
		HttpServletRequest request = HttpContext.GetRequest();
		try {
			dbHelper = DBHelper.GetInstance();
			dbHelper.Insert("exception", new String[] { "MESSAGE", "STACKTRACE", "ADDRESS", "TIME" }, new Object[] { Message, StackTrace, Common.getClientIP(request), Common.GetDateTime() });
		} catch (Exception e) {
			System.err.println("WriteException ".concat(Common.GetDateTime()));
			e.printStackTrace();
		}
	}

	public static int GetExceptionCount(String StartTime, String EndTime) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			WriteException(e1);
		}
		if (dbHelper == null) {
			return 0;
		}
		StartTime = dbHelper.FilterSpecialCharacter(StartTime);
		EndTime = dbHelper.FilterSpecialCharacter(EndTime);
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(StartTime)) {
			sbCondition.append("TIME >= '").append(StartTime).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(EndTime)) {
			sbCondition.append("TIME <= '").append(EndTime).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		int iRet = 0;
		try {
			iRet = dbHelper.GetCount("exception", sbCondition.toString());
		} catch (Exception e) {
			WriteException(e);
		} finally {
			sbCondition = null;
		}
		return iRet;
	}

	public static List<Map<String, Object>> GetExceptionList(String StartTime, String EndTime, int Start, int Length) {
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e1) {
			WriteException(e1);
		}
		if (dbHelper == null) {
			return null;
		}
		StartTime = dbHelper.FilterSpecialCharacter(StartTime);
		EndTime = dbHelper.FilterSpecialCharacter(EndTime);
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(StartTime)) {
			sbCondition.append("TIME >= '").append(StartTime).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(EndTime)) {
			sbCondition.append("TIME <= '").append(EndTime).append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		List<Map<String, Object>> lstRet = null;
		try {
			lstRet = dbHelper.ExecuteQuery("*", "exception", sbCondition.toString(), "ID DESC", Start, Length);
		} catch (Exception e) {
			WriteException(e);
		} finally {
			sbCondition = null;
		}
		return lstRet;
	}

	public static boolean DeleteException(String IDs) {
		boolean bolRet = false;
		String strCondition = null;
		if (!Common.IsNullOrEmpty(IDs)) {
			strCondition = "ID IN ('".concat(Common.Trim(IDs, ",").replace(",", "','")).concat("')");
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("exception", strCondition);
		} catch (Exception e) {
			WriteException(e);
			return bolRet;
		}
		return bolRet;
	}

	public static List<Map<String, Object>> getOperatorSystem() {
		List<Map<String, Object>> lstOpSystem = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOpSystem = dbHelper.ExecuteQuery("OSNAME", "operationsystem");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOpSystem;
	}

	public static List<Map<String, Object>> getOperatorSystemID() {
		List<Map<String, Object>> lstOpSystemID = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOpSystemID = dbHelper.ExecuteQuery("ID,OSNAME", "operationsystem");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOpSystemID;
	}

	public static List<Map<String, Object>> getOperatorSystemName(String OSID) {
		List<Map<String, Object>> lstOpSystemName = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOpSystemName = dbHelper.ExecuteQuery("OSNAME", "operationsystem", "id in(".concat(OSID).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOpSystemName;
	}

	public static boolean setOperatorSystem(String Name) {
		boolean bolRet = true;
		String baseOsName="other";
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("operationsystem", "osname='".concat(Name).concat("'")) == 0) {
				BaseOsNames[] baseOsNames = BaseOsNames.values ();
			       for (BaseOsNames base : baseOsNames) {
			    	  int index = Name.indexOf(base.value);
			    	  if(index >=0){
			    		  baseOsName = base.value;
			    		  break;
			    	  }
			       }
				bolRet = dbHelper.Insert("operationsystem", new String[] { "osname","baseosname"}, new Object[] { Name,baseOsName});
			}
		} catch (Exception e) {
			WriteException(e);
			bolRet = false;
		}
		return bolRet;
	}
	
	

	/**
	 * 添加下载追踪信息
	 * 
	 * @param UserName
	 * @param TypeID
	 * @param FileID
	 * @param Operation
	 * @param TraceData
	 * @param Status
	 */
	public static void WriteDownTraceLog(String UserName, String TypeID, String FileID, int Operation, String TraceData, int Status) {
		HttpServletRequest request = HttpContext.GetRequest();
		String strClient = request==null?"1":request.getHeader("User-Agent")==null?"2":request.getHeader("User-Agent");
		String strAddress = Common.getClientIP(request);
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			dbHelper.Insert("downloadtrace", new String[] { "username", "typeid", "fileid", "operation", "tracedata", "opstatus", "address", "client", "TIME" }, new Object[] { UserName, TypeID, FileID, Operation, TraceData, Status, strAddress, strClient, Common.GetDateTime() });
		} catch (Exception ex) {
			WriteException(ex);
		}
	}
	
	public static Integer getDownloadTraceCount(String userName,String fileId,String operation,String opstatus){
		int count = 0;
		StringBuilder sbCondition = new StringBuilder();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (userName != null && !"".equals(userName)) {
				sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' and ");
			}
			if (fileId != null && !"".equals(fileId)) {
				sbCondition.append("fileId = '").append(dbHelper.FilterSpecialCharacter(fileId)).append("' and ");
			}
			if (operation != null && !"".equals(operation)) {
				sbCondition.append("operation = '").append(dbHelper.FilterSpecialCharacter(operation)).append("' and ");
			}
			if (opstatus != null && !"".equals(opstatus)) {
				sbCondition.append("opstatus = '").append(dbHelper.FilterSpecialCharacter(opstatus)).append("' and ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			}
			count = dbHelper.GetCount("downloadtrace", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return count;
	}

	public static List<Map<String, Object>> getDownloadTraceList(String userName,String fileId,String operation,String opstatus,int iStart,int iLength){
		List<Map<String, Object>> lstDownloadTraceInfo = null;
		StringBuilder sbCondition = new StringBuilder();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (userName != null && !"".equals(userName)) {
				sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(userName)).append("%' and ");
			}
			if (fileId != null && !"".equals(fileId)) {
				sbCondition.append("fileId = '").append(dbHelper.FilterSpecialCharacter(fileId)).append("' and ");
			}
			if (operation != null && !"".equals(operation)) {
				sbCondition.append("operation = '").append(dbHelper.FilterSpecialCharacter(operation)).append("' and ");
			}
			if (opstatus != null && !"".equals(opstatus)) {
				sbCondition.append("opstatus = '").append(dbHelper.FilterSpecialCharacter(opstatus)).append("' and ");
			}
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
			}
			lstDownloadTraceInfo = dbHelper.ExecuteQuery("id,username,typeid,fileid,operation,tracedata,opstatus,address,client,time", "downloadtrace", sbCondition.toString(),"time desc",iStart,iLength);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstDownloadTraceInfo;
	}
	
	public static boolean delDownloadTraceInfo(String id) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("downloadtrace", "id in(".concat(dbHelper.FilterSpecialCharacter(id)).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}
	public static boolean delDownloadTraceAllInfo() {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("downloadtrace", "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}
	
	
}
