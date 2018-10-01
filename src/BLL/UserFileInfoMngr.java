package BLL;

import DAL.DBHelper;
import Model.UserDocInfoBean;
import Model.UserFileInfoBean;
import Util.Common;

public class UserFileInfoMngr {

	/****************** 临时应用 ***********************/
	public static boolean addMyFavorites(String RecID, String UserName) {
		String strTableName = "myfavorites".concat(String.valueOf(Math.abs(UserName.hashCode())).substring(0, 1));
		boolean bResult = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Insert(strTableName, new String[] { "recid", "username", "docinfo", "readstatus", "deleted", "time" }, new Object[] { RecID, UserName, "", "", 0, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/**
	 * UserName是否下载过FileID
	 * 
	 * @param FileID
	 * @param UserName
	 * @return
	 */
	public static boolean ExistsMyFavorite(String RecID, String UserName) {
		String strTableName = "myfavorites".concat(String.valueOf(Math.abs(UserName.hashCode())).substring(0, 1));
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(strTableName, "username ='" + UserName + "' and recid='".concat(RecID).concat("' and deleted = 0"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount == 0) {
			return false;
		} else {
			return true;
		}
	}

	/****************** 二期 **************************/
	/**
	 * 添加用户下载数据的记录
	 * 
	 * @param UserFileInfo
	 * @return
	 */
	public static boolean addUserFileInfo(UserFileInfoBean UserFileInfo) {
		boolean bResult = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Insert(getUserFileTable(UserFileInfo.getUserName()), new String[] { "username", "typeid", "fileid", "isdelete", "updatetime", "inserttime" }, new Object[] { UserFileInfo.getUserName(), UserFileInfo.getTypeID(), UserFileInfo.getFileID(), UserFileInfo.getIsDelete(), Common.GetDateTime(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/**
	 * UserName是否下载过FileID
	 * 
	 * @param FileID
	 * @param UserName
	 * @return
	 */
	public static boolean isExistUserFileInfo(String FileID, String UserName) {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(getUserFileTable(UserName), "username ='" + UserName + "' and fileid='".concat(FileID).concat("' and isdelete = 0"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 根据用户名找到表信息
	 * 
	 * @param UserName
	 * @return
	 */
	private static String getUserFileTable(String UserName) {
		return "userfileinfo".concat(String.valueOf(Math.abs(UserName.hashCode())).substring(0, 1));
	}

	/********************* UserDocInfoMngr ******************************/

	/**
	 * 
	 * @param UserDocInfo
	 * @return
	 */
	public static boolean addUserDocInfo(UserDocInfoBean UserDocInfo) {
		boolean bResult = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Insert(getUserDocInfoTable(UserDocInfo.getUserName()), new String[] { "recid", "username", "docinfo", "readstatus", "deleted", "updatetime", "time" }, new Object[] { UserDocInfo.getRecID(), UserDocInfo.getUserName(), UserDocInfo.getDocInfo(), UserDocInfo.getReadStatus(), UserDocInfo.getDeleted(), Common.GetDateTime(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/**
	 * 根据用户名找到表信息
	 * 
	 * @param UserName
	 * @return
	 */
	private static String getUserDocInfoTable(String UserName) {
		return "userdocinfo".concat(String.valueOf(Math.abs(UserName.hashCode())).substring(0, 1));
	}

	/********************* UserNodeInfoMngr ******************************/
}
