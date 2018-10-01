package BLL;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.cnki.hfs.FileClient;
import net.cnki.hfs.HFSOutputStream;
import net.cnki.hfs.HFS_OPEN_FILE;
import DAL.DBHelper;
import Model.EPubFileBean;
import Model.HttpContext;
import Model.UploadInfoBean;
import Util.Common;

public class UploadMngr {
	private static Map<String, Object[]> mFileHandler = new HashMap<String, Object[]>();

	private static FileClient fc = new FileClient(Common.GetConfig("HfsServer"));
	private static Map<String, Object[]> mFileOutStream = new HashMap<String, Object[]>();

	public static boolean addUploadInfo(UploadInfoBean FileInfo) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(getUploadTable(FileInfo.getUserName()), new String[] { "fileid", "filename", "username", "typename", "filesize", "ranges", "iscompleted", "ishasepub", "client", "address", "filemd5", "filetable", "dskfilename", "isdelete", "deletetime", "typeid", "time" }, new Object[] { FileInfo.getFileID(), FileInfo.getFileName(), FileInfo.getUserName(), FileInfo.getTypeName(), FileInfo.getFileLength(), FileInfo.getRange(), FileInfo.getIsCompleted(), FileInfo.getIsHahepub(), FileInfo.getClient(), FileInfo.getAddress(), FileInfo.getFileMd5(), FileInfo.getFileTable(), FileInfo.getDskFileName(), FileInfo.getIsDelete(), FileInfo.getDeleteTime(), FileInfo.getTypeid(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/**
	 * 
	 * @param FileID
	 * @param FileName
	 * @param UserName
	 * @param FileType
	 * @param FileSize
	 * @param FileMD5
	 * @return
	 */
	public static boolean addUploadInfo(String FileID, String FileName, String UserName, String FileType, String FileSize, String FileMD5, String strAgent, String strClientAddr) {
		boolean bolRet = false;
		// HttpServletRequest request = HttpContext.GetRequest();
		// String strAgent = request.getHeader("User-Agent");
		// String strClientAddr = request.getRemoteAddr();
		String strTableName = getUploadTable(UserName);
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(getUploadTable(UserName), new String[] { "fileid", "filename", "username", "typename", "filesize", "iscompleted", "ishasepub", "client", "address", "filemd5", "filetable", "dskfilename", "isdelete", "typeid", "time" }, new Object[] { FileID, FileName, UserName, FileType, FileSize, 0, 0, strAgent, strClientAddr, FileMD5, strTableName, FileID.concat(".").concat(FileType), 0, "cajcloud", Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	// public static boolean addUploadInfo(){
	//
	// }

	/**
	 * 将用户通过OData下载的数据记录到自己uplad表，同步数据时应用
	 * 
	 * @param FileID
	 * @param FileName
	 * @param UserName
	 * @param FileType
	 * @param FileSize
	 * @param Range
	 * @param IsCompleted
	 * @param TypeID
	 * @return
	 */
	public static boolean setOdataToUploadInfo(String FileID, String FileName, String UserName, String FileType, String FileSize, String Range, String IsCompleted, String TypeID) {
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		String strTableName = getUploadTable(UserName);
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(getUploadTable(UserName), new String[] { "fileid", "filename", "username", "typename", "filesize", "range", "iscompleted", "ishasepub", "client", "address", "filetable", "dskfilename", "isdelete", "typeid", "time" }, new Object[] { FileID, FileName, UserName, FileType, FileSize, Range, IsCompleted, 0, strAgent, strClientAddr, strTableName, FileID.concat(".").concat(FileType), 0, TypeID, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/**
	 * 根据用户名找到用户上传信息表
	 * 
	 * @param UserName
	 * @return
	 */
	public static String getUploadTable(String UserName) {
		String strTableName = "uploadinfo".concat(String.valueOf(Math.abs(UserName.hashCode())).substring(0, 1));
		return strTableName;
	}

	private static boolean deleteUploadInfo(String FileID, String UserName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			// bolRet = dbHelper.ExecuteSql(Sql, Param)("uploadinfo",
			// "fileid='".concat(dbHelper.FilterSpecialCharacter(FileID)).concat("'"));
			bolRet = dbHelper.Update(getUploadTable(UserName), "fileid='".concat(dbHelper.FilterSpecialCharacter(FileID)).concat("'"), new String[] { "ISDELETE", "DELETETIME" }, new String[] { "1", Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/**
	 * UserName是否下载过FileID
	 * 
	 * @param FileID
	 * @param UserName
	 * @return
	 */
	public static boolean isExistUploadInfo(String FileID, String UserName) {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(getUploadTable(UserName), "username ='" + UserName + "' and fileid='".concat(FileID).concat("' and isdelete = 0"));
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
	 * 
	 * @param FileID
	 * @param UserName
	 * @return
	 */
	private static Map<String, Object> getUploadInfo(String FileID, String UserName) {
		List<Map<String, Object>> lstUserInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstUserInfo = dbHelper.ExecuteQuery("filename,typename,filesize,ranges,dskfilename", getUploadTable(UserName), "fileid='".concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstUserInfo != null) {
			return lstUserInfo.get(0);
		}
		return null;
	}

	/**
	 * 
	 * @param FileID
	 * @param UserName
	 * @return
	 */
	public static String getDskFileName(String FileID, String UserName) {

		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("dskfilename", getUploadTable(UserName), "fileid='".concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		if (lstInfo == null) {
			return null;
		}
		return (String) lstInfo.get(0).get("dskfilename");
	}

	private static String getUploadInfoRanges(String FileID, String UserName) {

		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("ranges", getUploadTable(UserName), "fileid='".concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		if (lstInfo == null) {
			return null;
		}
		return (String) lstInfo.get(0).get("ranges");
	}

	private static boolean updateUploadInfoRanges(String FileID, String UserName, String Ranges) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update(getUploadTable(UserName), "fileid='".concat(FileID).concat("'"), new String[] { "ranges" }, new Object[] { Ranges });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static boolean updateUploadCompletedStatus(String FileID, String UserName, boolean IsCompleted) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update(getUploadTable(UserName), "fileid='".concat(FileID).concat("'"), new String[] { "iscompleted" }, new Object[] { IsCompleted ? 1 : 0 });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static boolean updateUploadIsHasEpub(String FileID, String UserName, boolean IsHasEpub) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update(getUploadTable(UserName), "fileid='".concat(FileID).concat("'"), new String[] { "ishasepub" }, new Object[] { IsHasEpub ? 1 : 0 });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean getUploadCompletedStatus(String FileID, String UserName) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("iscompleted", getUploadTable(UserName), "fileid='".concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		if (lstInfo == null) {
			return false;
		}
		int iStatus = (int) lstInfo.get(0).get("iscompleted");
		return iStatus == 1 ? true : false;
	}

	// private static boolean getFileEpubStatus(String FileID) {
	// List<Map<String, Object>> lstInfo = null;
	// try {
	// DBHelper dbHelper = DBHelper.GetInstance();
	// lstInfo = dbHelper.ExecuteQuery("ishasepub", "uploadinfo",
	// "fileid='".concat(FileID).concat("'"));
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// }
	//
	// if (lstInfo == null) {
	// return false;
	// }
	// int iStatus = (int) lstInfo.get(0).get("iscompleted");
	// return iStatus == 1 ? true : false;
	// }

	private static long getRangeLength(String Ranges) {
		String[] arrRange = Ranges.split(";");
		long lStart, lEnd, lLength = 0;
		String[] arrPos = null;
		for (String strRange : arrRange) {
			arrPos = strRange.split("-");
			lStart = Long.valueOf(Common.Trim(arrPos[0], " "));
			lEnd = Long.valueOf(Common.Trim(arrPos[1], " "));
			lLength += lEnd - lStart + 1;
		}
		return lLength;
	}

	private static String mergeRanges(String arg1, String arg2) {
		long lStartTemp, lEndTemp, lStartTemp1, lEndTemp1, lStart, lEnd;
		String[] arrPos = arg2.split("-");
		lStart = Long.valueOf(Common.Trim(arrPos[0], " "));
		lEnd = Long.valueOf(Common.Trim(arrPos[1], " "));
		List<long[]> lstRanges = new ArrayList<long[]>();
		String[] arrRanges = arg1.split(";");
		for (String strRange : arrRanges) {
			arrPos = strRange.split("-");
			lStartTemp = Long.valueOf(Common.Trim(arrPos[0], " "));
			lEndTemp = Long.valueOf(Common.Trim(arrPos[1], " "));
			lstRanges.add(new long[] { lStartTemp, lEndTemp });
		}

		int iSize = lstRanges.size();
		for (int i = 0; i < iSize; i++) {
			lStartTemp = lstRanges.get(i)[0];
			lEndTemp = lstRanges.get(i)[1];

			if (lStartTemp > lStart) {
				lstRanges.add(i, new long[] { lStart, lEnd });
				break;
			}
			if (i == iSize - 1) {
				lstRanges.add(new long[] { lStart, lEnd });
			}

		}

		for (int i = 0; i < lstRanges.size() - 1; i++) {
			lStartTemp = lstRanges.get(i)[0];
			lEndTemp = lstRanges.get(i)[1];
			lStartTemp1 = lstRanges.get(i + 1)[0];
			lEndTemp1 = lstRanges.get(i + 1)[1];

			if (lStartTemp1 - lEndTemp > 1) {
				continue;
			}

			lStart = lStartTemp > lStartTemp1 ? lStartTemp1 : lStartTemp;
			lEnd = lEndTemp > lEndTemp1 ? lEndTemp : lEndTemp1;

			lstRanges.get(i + 1)[0] = lStart;
			lstRanges.get(i + 1)[1] = lEnd;
			lstRanges.remove(i);
			i--;
		}

		StringBuilder sbRanges = new StringBuilder();
		for (long[] arrRangeTemp : lstRanges) {
			sbRanges.append(arrRangeTemp[0]).append("-").append(arrRangeTemp[1]).append(";");
		}
		if (sbRanges.length() > 0) {
			sbRanges.deleteCharAt(sbRanges.length() - 1);
		}
		return sbRanges.toString();
	}

	private static boolean writeFile(FileChannel FileHandler, String Range, byte[] FileContent) {
		String[] arrRange = Range.split("-");
		if (arrRange.length != 2) {
			return false;
		}

		long lStart = Long.valueOf(Common.Trim(arrRange[0], " "));
		long lEnd = Long.valueOf(Common.Trim(arrRange[1], " "));

		MappedByteBuffer mapBuf = null;
		try {
			mapBuf = FileHandler.map(FileChannel.MapMode.READ_WRITE, lStart, lEnd - lStart + 1);
			mapBuf.position(0);
			mapBuf.put(FileContent);
			mapBuf.force();
			mapBuf.clear();
		} catch (Exception e) {
			Logger.WriteException(e);
			return false;
		} finally {
			try {
				Method getCleanerMethod = mapBuf.getClass().getMethod("cleaner", new Class[0]);
				getCleanerMethod.setAccessible(true);
				/*sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(mapBuf, new Object[0]);
				cleaner.clean();*/
				mapBuf = null;
			} catch (Exception e1) {
				Logger.WriteException(e1);
			}
		}
		return true;
	}

	public static long personalUpload(UploadInfoBean FileInfo, String Ranges, byte[] FileContent) {
		String strFileID = FileInfo.getFileID();
		String strUserName = FileInfo.getUserName();
		Map<String, Object> mapFileInfo = getUploadInfo(strFileID, strUserName);
		if (mapFileInfo == null) {
			return SysConfigMngr.ERROR_CODE.ERROR_FILEIDORUSERNAME.code;
		}
		FileInfo.setFileLength(Long.parseLong(String.valueOf(mapFileInfo.get("filesize"))));
		FileInfo.setTypeName(String.valueOf(mapFileInfo.get("typename")));
		FileInfo.setTypeid("cajcloud");
		String strRemoteRootPath = "CajCloud";
		if (Common.GetConfig("HfsFileFolder") != null) {
			strRemoteRootPath = Common.GetConfig("HfsFileFolder");
		}
		String strRemotePath = strRemoteRootPath.concat("\\").concat("cajcloud\\".concat(FileInfo.getFileID().substring(0, 2))).concat("\\");
		long upLength = uploadFile1(FileInfo, strRemotePath, Ranges, FileContent);
		if (upLength >= FileInfo.getFileLength()) {
			if ("pdf".equalsIgnoreCase(FileInfo.getTypeName())) {
				pdf2Epub(FileInfo.getTypeid(), FileInfo.getFileID(), strUserName, "");
			}
			BehaviourMngr.addUploadInfo(strUserName, String.valueOf(mapFileInfo.get("filename")), String.valueOf(mapFileInfo.get("typename")));
		}
		return upLength;
	}

	public static synchronized long uploadFile(final UploadInfoBean FileInfo, byte[] FileContent) {

		boolean bolRet = true;

		final String strFileID = FileInfo.getFileID();
		final String strUserName = FileInfo.getUserName();

		if (!isExistUploadInfo(strFileID, strUserName)) {
			if (!addUploadInfo(FileInfo)) {
				return SysConfigMngr.ERROR_CODE.ERROR_ADDUPLOADINFO.code;
			}
		} else {
			if (getUploadCompletedStatus(strFileID, strUserName)) {
				return SysConfigMngr.ERROR_CODE.ERROR_CHECKUPLOADSTATUS.code;
			}
		}
		Map<String, Object> mapFileInfo = getUploadInfo(strFileID, strUserName);
		if (mapFileInfo == null) {
			return SysConfigMngr.ERROR_CODE.ERROR_FILEIDORUSERNAME.code;
		}
		final String strFileName = String.valueOf(mapFileInfo.get("filename"));
		String strRanges = mapFileInfo.get("ranges") == null ? null : String.valueOf(mapFileInfo.get("ranges"));
		final String strFileLength = String.valueOf(mapFileInfo.get("filesize"));
		String strFileType = String.valueOf(mapFileInfo.get("typename"));
		long lFileLength;
		try {
			lFileLength = Long.parseLong(strFileLength);
		} catch (Exception e) {
			Logger.WriteException(e);
			return SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code;
		}

		Object[] arrFileHandler = mFileHandler.get(strFileID);
		if (arrFileHandler == null) {
			String strFilePath = UploadMngr.getTempFilePath(getPersonalFilePath(strFileID));
			File file = new File(strFilePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			try {
				RandomAccessFile randomFile = new RandomAccessFile(strFilePath.concat("\\").concat(strFileID).concat(".").concat(strFileType), "rw");
				randomFile.setLength(lFileLength);
				FileChannel fileChannel = randomFile.getChannel();
				arrFileHandler = new Object[] { randomFile, fileChannel };
				mFileHandler.put(strFileID, arrFileHandler);
			} catch (Exception e) {
				Logger.WriteException(e);
				deleteUploadInfo(strFileID, strUserName);
				return SysConfigMngr.ERROR_CODE.ERROR_UPLOADFILE.code;
			}
		}

		String strBlockRange = FileInfo.getRange();
		String strNewRanges = strBlockRange;
		if (strRanges != null) {
			strNewRanges = mergeRanges(strRanges, strNewRanges);
		}

		if (strNewRanges.equals(strRanges)) {
			// 已经存在
			return getRangeLength(strNewRanges);
		}

		if (updateUploadInfoRanges(strFileID, strUserName, strNewRanges)) {
			bolRet = writeFile((FileChannel) arrFileHandler[1], strBlockRange, FileContent);
		}
		if (!bolRet) {
			updateUploadInfoRanges(strFileID, strUserName, strRanges);
			return SysConfigMngr.ERROR_CODE.ERROR_WRITEUPLOADFILE.code;
		}
		long lUploadedLength = getRangeLength(strNewRanges);
		if (lUploadedLength >= lFileLength) {
			try {
				((RandomAccessFile) arrFileHandler[0]).close();
				((FileChannel) arrFileHandler[1]).close();
				mFileHandler.remove(strFileID);
				if (HfmsMngr.uploadFile(strFileID, strFileType, getPersonalFilePath(strFileID))) { // 没有成功咋整
					updateUploadCompletedStatus(strFileID, strUserName, true);
					BehaviourMngr.addUploadInfo(strUserName, strFileName, strFileType);
				} else {
					Logger.WriteException(new Exception(strFileID.concat("上传到hfms失败")));
				}
				if ("pdf".equalsIgnoreCase(strFileType)) {
					new Thread() {
						public void run() {
							EPubFileBean epubFileBean = Pdf2Epub.startPdf2Epub(strFileID, strFileName, Integer.parseInt(strFileLength) / (1024 * 1024) + 1, getPersonalFilePath(strFileID));
							if (epubFileBean != null) {
								if (HfmsMngr.uploadFile(epubFileBean.getFileID(), epubFileBean.getTypeName(), getPersonalFilePath(strFileID))) {
									if (EPubFileMngr.addUploadEPubInfo(epubFileBean, strUserName)) {
										updateUploadIsHasEpub(epubFileBean.getFileID(), strUserName, true);
									}
									deleteTempFile(getPersonalFilePath(strFileID), epubFileBean.getFileID().concat(".epub"));
								}
							}
						}
					}.start();
				}
				deleteTempFile(getPersonalFilePath(strFileID), strFileID.concat(".").concat(strFileName));
			} catch (Exception e) {
				Logger.WriteException(e);
				return SysConfigMngr.ERROR_CODE.ERROR_UPLOADFILE.code;
			}
		}
		return lUploadedLength;
	}

	/**
	 * 直接将接收到的字节流发送给全文
	 * 
	 * @param FileInfo
	 * @param RemoteFilePath
	 * @param Ranges
	 * @param FileContent
	 * @return
	 */
	public static long uploadFile1(UploadInfoBean FileInfo, String RemoteFilePath, String Ranges, byte[] FileContent) {
		String strFileID = FileInfo.getFileID();
		String strUserName = FileInfo.getUserName();

		if (!isExistUploadInfo(strFileID, strUserName)) {
			if (!addUploadInfo(FileInfo)) {
				return SysConfigMngr.ERROR_CODE.ERROR_ADDUPLOADINFO.code;
			}
		} else {
			if (getUploadCompletedStatus(strFileID, strUserName)) {
				return SysConfigMngr.ERROR_CODE.ERROR_CHECKUPLOADSTATUS.code;
			}
		}
		HFSOutputStream fileHandler = null;
		long lFileRet = 0;
		Object[] arrFileHandler = mFileOutStream.get(strFileID);
		if (arrFileHandler == null) {
			// 先创建文件夹

			if (!creatUploadDir(RemoteFilePath)) {
				return SysConfigMngr.ERROR_CODE.ERROR_CREATEFILEFOLDER.code;
			}
			HFS_OPEN_FILE hof = null;
			try {
				hof = fc.OpenFile(RemoteFilePath.concat(FileInfo.getFileID()).concat(".").concat(FileInfo.getTypeName()), "wb+");
			} catch (Exception e) {
				Logger.WriteException(e);
				return SysConfigMngr.ERROR_CODE.ERROR_OPENFILE.code;
			}
			if (hof == null) {
				return SysConfigMngr.ERROR_CODE.ERROR_OPENFILENULL.code;
			}

			long lRet = hof.Handle;

			fileHandler = new HFSOutputStream(fc, lRet);
			arrFileHandler = new Object[] { fileHandler, lRet };
			mFileOutStream.put(strFileID, arrFileHandler);
		}
		fileHandler = (HFSOutputStream) arrFileHandler[0];
		lFileRet = (long) arrFileHandler[1];

		String strBlockRange = Ranges;
		String strRangeEnd = String.valueOf(FileInfo.getFileLength() - 1);
		if (!Common.IsNullOrEmpty(strBlockRange)) {
			if (strBlockRange.indexOf("-") == strBlockRange.length() - 1) {
				strBlockRange = strBlockRange.concat(strRangeEnd);
			} else {
				if (Integer.parseInt(strBlockRange.split("-")[1]) >= FileInfo.getFileLength()) {
					strBlockRange = strBlockRange.split("-")[0].concat("-").concat(strRangeEnd);
				}
			}
		} else {
			strBlockRange = "0-".concat(strRangeEnd);
		}
		String strRanges = getUploadInfoRanges(strFileID, strUserName);
		String strNewRanges = strBlockRange;
		if (!Common.IsNullOrEmpty(strRanges)) {
			strNewRanges = mergeRanges(strRanges, strNewRanges);
		}
		if (strNewRanges.equals(strRanges)) {
			return getRangeLength(strNewRanges);
		}
		int iResult = 0;
		try {
			iResult = fileHandler.write(FileContent, FileContent.length, Integer.parseInt(strBlockRange.split("-")[0]));
		} catch (Exception e) {
			Logger.WriteException(e);
			return SysConfigMngr.ERROR_CODE.ERROR_WRITEFILE.code;
		}
		if (iResult == 0) {
			return SysConfigMngr.ERROR_CODE.ERROR_WRITEFILE.code;
		} else {
			if (!updateUploadInfoRanges(strFileID, strUserName, strNewRanges)) {
				return SysConfigMngr.ERROR_CODE.ERROR_UPDATERANGE.code;
			}
		}

		long lUploadedLength = getRangeLength(strNewRanges);
		if (lUploadedLength >= FileInfo.getFileLength()) {
			try {
				updateUploadCompletedStatus(strFileID, strUserName, true);
				mFileOutStream.remove(strFileID);
				fileHandler.close();
				try {
					fc.CloseFile(lFileRet);
				} catch (Exception e) {
					Logger.WriteException(e);
				}
			} catch (Exception e) {
				Logger.WriteException(e);
				return SysConfigMngr.ERROR_CODE.ERROR_UPLOADFILE.code;
			}
		}
		return lUploadedLength;
	}

	private static boolean creatUploadDir(String RemotePath) {
		String strFilePath = RemotePath;
		int isExist = 0;
		try {
			isExist = fc.IsDirExist(strFilePath);
		} catch (Exception e) {
			Logger.WriteException(e);
			return false;
		}
		if (isExist > 0) {
			return true;
		} else {
			try {
				if (fc.DirCreate(strFilePath) > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				Logger.WriteException(e);
				return false;
			}
		}
	}

	public static int getFileCount(String FileName, String UserName, String FileType, String IsDelete) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (FileName != null) {
			sbCondition.append("filename like '%").append(dbHelper.FilterSpecialCharacter(FileName)).append("%' and ");
		}
		if (UserName != null) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
		}
		if (FileType != null) {
			sbCondition.append("typename = '").append(dbHelper.FilterSpecialCharacter(FileType)).append("' and ");
		}
		if (IsDelete != null) {
			sbCondition.append("isdelete =").append(dbHelper.FilterSpecialCharacter(IsDelete)).append(" and ");
		}

		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[3];
		arrParam[0] = "uploadinfo";
		arrParam[1] = sbCondition.toString();
		arrParam[2] = "cloudtable";

		List<Map<String, Object>> lstDownloadList = null;
		try {
			lstDownloadList = dbHelper.ExecuteQueryProc("sp_getDataCount", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			return 0;
		}
		arrParam = null;
		if (lstDownloadList == null) {
			return 0;
		}
		return Integer.valueOf(lstDownloadList.get(0).get("totalcount").toString());
	}

	public static List<Map<String, Object>> getFileList(String FileName, String UserName, String FileType, String IsDelete, int Start, int Length) {
		List<Map<String, Object>> lstFile = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (FileName != null) {
			sbCondition.append("filename like '%").append(dbHelper.FilterSpecialCharacter(FileName)).append("%' and ");
		}
		if (UserName != null) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
		}
		if (FileType != null) {
			sbCondition.append("typename = '").append(dbHelper.FilterSpecialCharacter(FileType)).append("' and ");
		}
		if (IsDelete != null) {
			sbCondition.append("isdelete =").append(dbHelper.FilterSpecialCharacter(IsDelete)).append(" and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		// lstFile =
		// dbHelper.ExecuteQuery("fileid,filename,username,typename,filesize,ranges,client,address,iscompleted,time",
		// "uploadinfo", sbCondition.toString(), "time desc", Start, Length);

		Object[] arrParam = new Object[6];
		arrParam[0] = "uploadinfo";
		arrParam[1] = "id,filename,typename,filesize,username,iscompleted,ishasepub,isdelete,fileid,typeid,client,address,time";
		arrParam[2] = sbCondition.toString();
		arrParam[3] = "cloudtable";
		arrParam[4] = Start;
		arrParam[5] = Length;
		try {
			lstFile = dbHelper.ExecuteQueryProc("sp_getDataRecord", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		arrParam = null;
		return lstFile;
	}

	public static List<Map<String, Object>> getFileList(Map<String, Object> FileInfo, String UserName) {
		List<Map<String, Object>> lstFile = null;
		StringBuilder sbCondition = new StringBuilder();
		sbCondition.append("username='").append(UserName).append("' and ");
		String strFileName = (String) FileInfo.get("filename");
		if (!Common.IsNullOrEmpty(strFileName)) {
			if (strFileName.endsWith("?")) {
				sbCondition.append("filename like '").append(strFileName.substring(0, strFileName.length() - 1)).append("%' and ");
			} else {
				sbCondition.append("filename = '").append(strFileName).append("' and ");
			}
		}

		String strTime = (String) FileInfo.get("time");
		if (!Common.IsNullOrEmpty(strTime)) {
			strTime = strTime.trim();
			String strFix1 = strTime.substring(0, 1);
			String strFix2 = strTime.substring(0, 2);
			if (">=".equals(strFix2) || "<=".equals(strFix2)) {
				sbCondition.append("time").append(strFix2).append(" '").append(strTime.substring(2, strTime.length())).append("' and ");
			} else if (">".equals(strFix1) || "<".equals(strFix1) || "=".equals(strFix1)) {
				sbCondition.append("time").append(strFix1).append(" '").append(strTime.substring(1, strTime.length())).append("' and ");
			} else {
				sbCondition.append("time").append(" = '").append(strTime).append("' and ");
			}
		}
		sbCondition.append("isdelete = 0 and ");
		int iConditionLen = sbCondition.length();
		if (iConditionLen > 0) {
			sbCondition.delete(iConditionLen - 4, iConditionLen);
		}
		int iStart = 1;
		int iLen = 20;
		String strOrder = "";
		if (FileInfo.containsKey("start") && FileInfo.get("start") != null) {
			iStart = Integer.valueOf(FileInfo.get("start").toString());
		}
		if (FileInfo.containsKey("length") && FileInfo.get("length") != null) {
			iLen = Integer.valueOf(FileInfo.get("length").toString());
		}
		if (FileInfo.containsKey("order")) {
			strOrder = FileInfo.get("order").toString();
		}

		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstFile = dbHelper.ExecuteQuery("fileid,filename,username,typename,filesize,ranges,client,address,iscompleted,ishasepub,typeid,time", getUploadTable(UserName), sbCondition.toString(), strOrder, iStart, iLen);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstFile;
	}

	public static boolean deleteTempFile(String FilePath, String FileName) {
		boolean bRet = true;
		String strFileName = getTempFilePath(FilePath).concat(FileName);
		File file = new File(strFileName);
		if (file.exists()) {
			bRet = file.delete();
		}
		return bRet;
	}

	public static boolean deleteFile(String FileID, String UserName) {
		if (mFileHandler.containsKey(FileID)) {
			mFileHandler.remove(FileID);
		}
		return deleteUploadInfo(FileID, UserName);
	}

	public static boolean deleteFile2(String FileID, String UserName) {
		if (mFileOutStream.containsKey(FileID)) {
			mFileOutStream.remove(FileID);
		}
		String strFileName = Common.GetConfig("HfsFileFolder").concat("\\").concat(FileID.substring(0, 2)).concat("\\").concat(FileID);
		long lRet = 0;
		try {
			lRet = fc.DeleteFile(strFileName);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lRet < 0) {
			return false;
		}
		return deleteUploadInfo(FileID, UserName);
	}

	public static String getFileFullName(String FileID) {
		return Common.GetConfig("UploadFileFolder").concat("\\").concat(FileID.substring(0, 2)).concat("\\").concat(FileID);
	}

	public static String getTempFilePath(String FilePath) {
		String strFilePath = Common.GetConfig("UploadFileFolder").concat("\\").concat(FilePath);
		File file = new File(strFilePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return strFilePath.concat("\\");
	}

	public static String getPersonalFilePath(String FileID) {
		return "cajcloud\\".concat(FileID.substring(0, 2));
	}

	public static UploadInfoBean getUploadFileInfo(String FileID) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("filename,username,typename,filesize,ranges", "uploadinfo", "fileid='".concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		if (lstInfo == null) {
			return null;
		}

		UploadInfoBean uploadInfo = new UploadInfoBean();
		uploadInfo.setFileID(FileID);
		uploadInfo.setFileName((String) lstInfo.get(0).get("filename"));
		uploadInfo.setTypeName((String) lstInfo.get(0).get("typename"));
		uploadInfo.setFileLength(Long.valueOf((String) lstInfo.get(0).get("filesize")));
		uploadInfo.setUserName((String) lstInfo.get(0).get("username"));
		uploadInfo.setRange((String) lstInfo.get(0).get("ranges"));
		return uploadInfo;
	}

	public static boolean isExist(String UserName, String FileName) {
		boolean bolRet = false;
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(getUploadTable(UserName), "username='".concat(UserName).concat("' and filename='").concat(FileName).concat("' and isdelete=0"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount > 0) {
			bolRet = true;
		}
		return bolRet;
	}

	public static Map<String, Object> getExistMd5(String FileMD5) {
		Map<String, Object> mapFileInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			Object[] Param = new Object[2];
			Param[0] = "uploadinfo";
			Param[1] = FileMD5;
			List<Map<String, Object>> lstFileInfo = dbHelper.ExecuteQueryProc("sp_getFileID", Param);
			if (lstFileInfo != null) {
				mapFileInfo = lstFileInfo.get(0);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return mapFileInfo;
	}

	/**
	 * 
	 * @param FileID
	 * @param FileName
	 * @param FileType
	 * @param UserName
	 * @param FileMD5
	 * @param TableName
	 * @return
	 */
	public static boolean setFileInfo(String FileID, String FileName, String FileType, String UserName, String FileMD5, String TableName) {
		boolean bResult = true;
		String strTableName = getUploadTable(UserName);
		String strSql = "INSERT into ".concat(strTableName).concat("(fileid, filename, username, typename, filesize, ranges, iscompleted, ishasepub, client, address,dskfilename,filetable,filemd5,isdelete, time) select '").concat(FileID).concat("','").concat(FileName).concat("','").concat(UserName).concat("','").concat(FileType).concat("',").concat("filesize,ranges,iscompleted,ishasepub,client,address,dskfilename,filetable,filemd5,'0','").concat(Common.GetDateTime()).concat("' from ").concat(TableName).concat(" where iscompleted=1 and filemd5='").concat(FileMD5).concat("' limit 1");
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.ExecuteSql(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/**
	 * 
	 * @param TypeID
	 * @param FileID
	 * @param UserName
	 */
	public static void pdf2Epub(String TypeID, String FileID, String UserName, String Url) {
		final String strCmd = "convert ".concat(TypeID).concat(" ").concat(FileID).concat(" ").concat(UserName).concat(" ").concat(Url);
		if (!"cajcloud".equalsIgnoreCase(TypeID)) {
			if (ODataMngr.isExistEpub(TypeID, FileID)) {
				setExistEpub(TypeID, FileID, UserName);
				return;
			}
		}
		String strServerAddr = Pdf2Epub.getServerAddr();
		if (!Common.IsNullOrEmpty(strServerAddr)) {
			SocketMngr.sendSocketDataNoWait(strServerAddr, strCmd);
		} else {
			Logger.WriteException(new Exception("epub服务器为空"));
		}
	}

	/**
	 * OData下载下来的数据应用
	 * 
	 * @param FileID
	 * @param UserName
	 * @return
	 */
	public static boolean setExistEpub(String TypeID, String FileID, String UserName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update(getUploadTable(UserName), "typeid='".concat(TypeID).concat("' and fileid='").concat(FileID).concat("'").concat(" and username='").concat(UserName).concat("'"), new String[] { "ishasepub" }, new Object[] { 1 });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}
}
