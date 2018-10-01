package BLL;


import DAL.DBHelper;
import Model.EPubFileBean;
import Util.Common;

public class EPubFileMngr {

	public static boolean addUploadEPubInfo(EPubFileBean FileInfo,String UserName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(getUploadEPubTable(UserName), new String[] { "fileid", "filename", "typename", "filesize", "time" }, new Object[] { FileInfo.getFileID(), FileInfo.getFileName(), FileInfo.getTypeName(), FileInfo.getFileSize(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}
	
	private static String getUploadEPubTable(String UserName){
		String strTableName = "uploadinfoepub".concat(String.valueOf(Math.abs(UserName.hashCode())).substring(0, 1));
		/*DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("cloudtable", "tablename='".concat(strTableName).concat("'")) == 0) {
				List<String> lstSql = new ArrayList<String>();
				lstSql.add("create table ".concat(strTableName).concat(" like uploadinfoepub"));
				lstSql.add("insert into cloudtable(tablename,time) values('".concat(strTableName).concat("','" + Common.GetDateTime() + "')"));
				dbHelper.ExecuteSql(lstSql);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			return "";
		}*/
		return strTableName;
	}
	
	/**
	 * OData下载文件转换为pdf时应用
	 * @param FileInfo
	 * @param TableName
	 * @return
	 */
	public static boolean addDownloadEPubInfo(EPubFileBean FileInfo, String TableName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(TableName, new String[] { "fileid", "filename", "filesize", "typename", "time" }, new Object[] { FileInfo.getFileID(), FileInfo.getFileName(), FileInfo.getFileSize(), FileInfo.getTypeName(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

//	public static boolean addODataEPubInfo(EPubFileBean FileInfo) {
//		boolean bolRet = false;
//		try {
//			DBHelper dbHelper = DBHelper.GetInstance();
//			bolRet = dbHelper.Insert("odataepubinfo", new String[] { "fileid", "filename", "typename", "filesize", "time" }, new Object[] { FileInfo.getFileID(), FileInfo.getFileName(), FileInfo.getTypeName(), FileInfo.getFileSize(), Common.GetDateTime() });
//		} catch (Exception e) {
//			Logger.WriteException(e);
//		}
//		return bolRet;
//	}

//	public static boolean isExistODataEPubInfo(String FileID) {
//		boolean bolRet = false;
//		try {
//			DBHelper dbHelper = DBHelper.GetInstance();
//			int iCount = 0;
//			try {
//				iCount = dbHelper.GetCount("odataepubinfo", "fileid='".concat(FileID).concat("'"));
//			} catch (Exception e) {
//				Logger.WriteException(e);
//			}
//			if (iCount == 0) {
//				return false;
//			} else {
//				return true;
//			}
//		} catch (Exception e) {
//			Logger.WriteException(e);
//		}
//		return bolRet;
//	}

}
