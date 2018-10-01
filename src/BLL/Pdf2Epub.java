package BLL;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Model.EPubFileBean;
import Util.Common;

public class Pdf2Epub {
	// private String fileName;
	// private int factor; // 等待系数
	//
	// public Pdf2Epub(String FileName, int Factor) {
	// this.fileName = FileName;
	// this.factor = Factor;
	// }
	//
	// public void run() {
	// if(StartPdf2Epub(fileName, factor)
	// }

	/**
	 * 
	 * @param FileID
	 *            文件id
	 * @param FileName
	 *            文件名称
	 * @param iFactor
	 *            执行时间系数
	 * @param TempFilePath
	 *            文件目录
	 * @return
	 */
	public static synchronized EPubFileBean startPdf2Epub(String FileID, String FileName, int iFactor, String TempFilePath) {
		int iResult = -1;
		String strFilePath = UploadMngr.getTempFilePath(TempFilePath);
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		String strPdf2EpubExe = Common.GetConfig("PdfToEpub");
		try {
			process = runtime.exec(strPdf2EpubExe.concat("PDF2EPub ") + strFilePath + FileID + ".pdf" + " " + strFilePath + FileID + ".epub");
		} catch (IOException e) {
			return null;
		}
		Worker worker = new Worker(process);
		worker.start();
		try {
			worker.join(iFactor * 60 * 1000);
			if (worker.exit != null)
				iResult = worker.exit;
		} catch (InterruptedException ex) {
			worker.interrupt();
			Thread.currentThread().interrupt();
			return null;
		} finally {
			process.destroy();
		}
		if (iResult == 0) {
			File file = new File(strFilePath + FileID + ".epub");
			EPubFileBean FileInfo = new EPubFileBean();
			FileInfo.setFileName(FileName.contains(".") ? FileName.substring(0, FileName.indexOf(".")).concat(".epub") : FileName);
			FileInfo.setFileID(FileID);
			FileInfo.setTypeName("epub");
			FileInfo.setFileSize(file.length());
			file = null;
			return FileInfo;
		} else {
			return null;
		}

	}

	public static List<Map<String, Object>> getServerList() {
		List<Map<String, Object>> lstServer = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstServer = dbHelper.ExecuteQuery("id,host,cmdport,statusport,status,time", "pdf2epubsvrinfo");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstServer;
	}

	public static String getServerAddr() {
		List<Map<String, Object>> lstHost = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstHost = dbHelper.ExecuteQuery("host,cmdport,statusport", "pdf2epubsvrinfo", "status=1");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstHost == null) {
			return null;
		}
		String strHost = null;
		String strCmdPort = null;
		String strStatusPort = null;
		String strServer = null;
		String strStatus;
		int iStatus = 0;
		int iStatusTemp = 0;

		for (Map<String, Object> mapTemp : lstHost) {
			strHost = String.valueOf(mapTemp.get("host"));
			strCmdPort = String.valueOf(mapTemp.get("cmdport"));
			strStatusPort = String.valueOf(mapTemp.get("statusport"));
			strStatus = SocketMngr.sendSocketData(strHost.concat(":").concat(strStatusPort), "status", 1000);
			if (Common.IsNullOrEmpty(strStatus)) {
				continue;
			}
			iStatusTemp = Integer.valueOf(strStatus.replace("\r", "").replace("\n", ""));
			if (iStatusTemp > iStatus) {
				iStatus = iStatusTemp;
				strServer = strHost.concat(":").concat(strCmdPort);
			}
		}
		return strServer;
	}

	public static Map<String, Object> getEpubServerInfo(String ID) {
		List<Map<String, Object>> lstServer = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstServer = dbHelper.ExecuteQuery("host,cmdport,statusport,status", "pdf2epubsvrinfo", "id=".concat(ID));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstServer != null)
			return lstServer.get(0);
		return null;
	}

	public static boolean addEpubServerInfo(String ServerHost, String StatusPort, String CmdPort, String Status) {
		boolean bRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Insert("pdf2epubsvrinfo", new String[] { "host", "cmdport", "statusport", "status", "time" }, new String[] { ServerHost, CmdPort, StatusPort, Status, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	public static boolean updateEpubServerInfo(String ServerID, String ServerHost, String StatusPort, String CmdPort, String Status) {
		boolean bRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Update("pdf2epubsvrinfo", "id=".concat(ServerID), new String[] { "host", "cmdport", "statusport", "status", "time" }, new String[] { ServerHost, CmdPort, StatusPort, Status, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bRet;
	}

	public static boolean delEpubServer(int ID) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("pdf2epubsvrinfo", "id=".concat(String.valueOf(ID)));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static void pdfTransEpub() {
		String strUserName = "ttod";
		String strPassword = "ttod";
		String strIP = "211.151.93.226";
		if (Common.GetConfig("epubusername") != null) {
			strUserName = Common.GetConfig("epubusername");
		}
		final String UserName = strUserName;
		if (Common.GetConfig("epubuserpwd") != null) {
			strPassword = Common.GetConfig("epubuserpwd");
		}
		if (Common.GetConfig("epubip") != null) {
			strIP = Common.GetConfig("epubip");
		}
		CnkiMngr cnkiMngr = null;
		try {
			cnkiMngr = new CnkiMngr();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		int[] iResult = new int[2];
		cnkiMngr.cnkiUserLogin(strUserName, strPassword, strIP, iResult);
		List<Map<String, Object>> listType = SourceMngr.getSourceType();
		Iterator<Map<String, Object>> iterator = listType.iterator();
		Map<String, Object> mapType = null;
		while (iterator.hasNext()) {
			mapType = iterator.next();
			List<Map<String, Object>> listTableName = EpubTransMngr.getTableNameList(String.valueOf(mapType.get("id")), 0);
			final String TypeID = String.valueOf(mapType.get("name_en"));
			if (listTableName == null)
				continue;
			Iterator<Map<String, Object>> iTableName = listTableName.iterator();
			Map<String, Object> mapTableName = null;
			while (iTableName.hasNext()) {
				mapTableName = iTableName.next();
				List<Map<String, Object>> listNoEpubFile = getNoEpubInfoList(String.valueOf(mapTableName.get("tablename")));
				if (listNoEpubFile == null)
					continue;
				Iterator<Map<String, Object>> iFile = listNoEpubFile.iterator();
				Map<String, Object> mapFile = null;
				while (iFile.hasNext()) {
					mapFile = iFile.next();
					final String strFileID = String.valueOf(mapFile.get("fileid"));
					if (!cnkiMngr.setFileInfo(TypeID, strFileID)) {
						CnkiMngr.setCnkiErrorFile(UserName, TypeID, strFileID, 2);
						continue;
					}

					String tempUrl = "";
					try {
						tempUrl = cnkiMngr.getDownloadUrl(TypeID, strFileID, strUserName);
					} catch (Exception e) {
						Logger.WriteException(e);
					}
					final String Url = tempUrl;
					if (Url != null && Url.length() > 0) {
						new Thread() {
							public void run() {
								UploadMngr.pdf2Epub(TypeID, strFileID, UserName, Url.replace("t=cajdown", "t=pdfdown"));
							}
						}.start();

					}
				}
			}
		}
	}

	public static String pdfTransEpub(String TypeID, List<Map<String, Object>> listFile) {
		String strUserName = "ttod";
		String strPassword = "ttod";
		String strIP = "211.151.93.226";
		if (Common.GetConfig("epubusername") != null) {
			strUserName = Common.GetConfig("epubusername");
		}
		if (Common.GetConfig("epubuserpwd") != null) {
			strPassword = Common.GetConfig("epubuserpwd");
		}
		if (Common.GetConfig("epubip") != null) {
			strIP = Common.GetConfig("epubip");
		}
		CnkiMngr cnkiMngr = null;
		try {
			cnkiMngr = new CnkiMngr();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		int[] iResult = new int[2];
		cnkiMngr.cnkiUserLogin(strUserName, strPassword, strIP, iResult);
		int iRecord = 0;
		Iterator<Map<String, Object>> iFile = listFile.iterator();
		Map<String, Object> mapFile = null;
		while (iFile.hasNext()) {
			mapFile = iFile.next();
			final String strFileID = String.valueOf(mapFile.get("fileid"));
			if (!cnkiMngr.setFileInfo(TypeID, strFileID)) {
				CnkiMngr.setCnkiErrorFile(strUserName, TypeID, strFileID, 2);
				continue;
			}
			String tempUrl = "";
			try {
				tempUrl = cnkiMngr.getDownloadUrl(TypeID, strFileID, strUserName);
			} catch (Exception e) {
				Logger.WriteException(e);
			}
			final String Url = tempUrl;
			if (Url != null && Url.length() > 0) {
				Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 6, "Come pdf2epub function", 1);
				UploadMngr.pdf2Epub(TypeID, strFileID, strUserName, Url.replace("t=cajdown", "t=pdfdown"));
				iRecord = iRecord + 1;
			}
		}
		return String.valueOf(iRecord);
	}

	private static List<Map<String, Object>> getNoEpubInfoList(String TableName) {
		List<Map<String, Object>> listNoEpubFile = null;
		DBHelper dbHelper;
		try {
			dbHelper = DBHelper.GetInstance();
			listNoEpubFile = dbHelper.ExecuteQuery("fileid", TableName, "ishasepub is null");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return listNoEpubFile;
	}
}

class Worker extends Thread {
	private final Process process;
	public Integer exit;

	public Worker(Process process) {
		this.process = process;
	}

	public void run() {
		// String line = null;
		try {
			BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((stdout.readLine()) != null) {
			}
			exit = process.waitFor();
		} catch (Exception ignore) {
			return;
		}
	}
}