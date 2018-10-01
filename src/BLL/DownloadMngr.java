package BLL;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import DAL.DBHelper;
import Model.DownloadInfoBean;
import Model.HttpContext;
import Util.Common;
import Util.XmlReader;

public class DownloadMngr {

	public boolean isDownloaded(String UserName, String FileID) {
		boolean bolRet = false;
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(getDownloadTable(UserName),
					"username = '".concat(UserName).concat("' and fileid = '").concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount > 0) {
			bolRet = true;
		}
		return bolRet;
	}

	public static boolean addDownloadInfo(DownloadInfoBean DownloadInfo) {
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(getDownloadTable(DownloadInfo.getUserName()),
					new String[] { "username", "typeid", "filename", "filetype", "fileid", "client", "address", "appid",
							"isorg", "orgname", "time" },
					new Object[] { DownloadInfo.getUserName(), DownloadInfo.getTypeID(), DownloadInfo.getFileName(),
							DownloadInfo.getFileType(), DownloadInfo.getFileID(), strAgent, strClientAddr,
							DownloadInfo.getAppID(), DownloadInfo.getIsorg(), DownloadInfo.getOrgName(),
							Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static String getDownloadTable(String UserName) {
		String strTableName = "downloadinfo".concat(String.valueOf(Math.abs(UserName.hashCode())).substring(0, 1));
		return strTableName;
	}

	public DownloadInfoBean getDownloadInfo(String UserName, String FileID) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("username,filename,fileid", getDownloadTable(UserName),
					"username = '".concat(UserName).concat("' and fileid = '").concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return null;
		}
		return new DownloadInfoBean(lstInfo.get(0));
	}

	private static String getDownloadUrl(String Token, String FileType, String FileID) {
		String strServer = Common.GetConfig("DownloadServer");
		String strUrl = strServer.concat("/file/").concat(FileType).concat("/").concat(FileID).concat("/download");
		URL url = null;
		HttpURLConnection urlconn = null;
		BufferedReader bufReader = null;
		String strRet = null;
		String strLine = null;
		String strResult = "";
		try {
			url = new URL(strUrl);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.addRequestProperty("authorization", "Bearer ".concat(Token));
			urlconn.connect();
			bufReader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
			while ((strLine = bufReader.readLine()) != null) {
				strResult = strResult.concat(strLine);
			}
			bufReader.close();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		} finally {
			urlconn.disconnect();
			url = null;
			urlconn = null;
		}
		if (!"".equals(strResult)) {
			strRet = "http".concat(strResult.substring(strResult.indexOf(':')));
			if (strRet.endsWith("\"")) {
				strRet = strRet.substring(0, strRet.length() - 1);
			}
		}
		return strRet;
	}

	private static String getFileInfo(String Url) {
		URL url = null;
		HttpURLConnection urlconn = null;
		BufferedReader bufReader = null;
		String strLine = null;
		String strResult = "";
		try {
			url = new URL(Url);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestProperty("REQUEST-ACTION", "fileinfo");
			// urlconn.setRequestProperty("Range", "bytes=0-9999");

			urlconn.connect();
			bufReader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
			while ((strLine = bufReader.readLine()) != null) {
				strResult = strResult.concat(strLine);
			}
			bufReader.close();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		} finally {
			urlconn.disconnect();
			url = null;
			urlconn = null;
		}
		if ("".equals(strResult)) {
			return null;
		}
		return strResult;
	}

	private static byte[] getFile(String Url, String Range) {
		URL url = null;
		HttpURLConnection urlconn = null;
		byte[] bRet = null;
		try {
			url = new URL(Url);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestProperty("ACCEPT-Range", Range);
			urlconn.setDoInput(true);
			urlconn.connect();
			bRet = new byte[urlconn.getContentLength()];
			DataInputStream dataInput = new DataInputStream(urlconn.getInputStream());
			dataInput.readFully(bRet);
			urlconn.getInputStream().close();
			dataInput.close();
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		} finally {
			urlconn.disconnect();
			url = null;
			urlconn = null;
		}
		return bRet;
	}

	public static Map<String, Object> getFileIno(String Token, String FileType, String FileID) {
		String strUrl = getDownloadUrl(Token, FileType, FileID);
		strUrl = strUrl.replace("&amp;", "&");
		String strFileInfo = getFileInfo(strUrl);
		XmlReader xmlReader = XmlReader.Read(strFileInfo.getBytes());
		String strLength = xmlReader.GetXmlValue("root/document/length");
		String strFileName = xmlReader.GetXmlValue("root/document/filename");
		String strDocInfo = xmlReader.GetXmlValue("root/document/docInfo");
		strDocInfo = Common.base64Decode(strDocInfo);
		XmlReader xmlReader2 = XmlReader.Read(strDocInfo.getBytes());
		String strTitle = xmlReader2.GetXmlValue("root/docInfo/title");
		Map<String, Object> mapFileXml = new HashMap<String, Object>();
		mapFileXml.put("filename", strFileName);
		mapFileXml.put("filesize", strLength);
		mapFileXml.put("title", strTitle);
		return mapFileXml;
	}

	public static boolean downloadFile(String Token, String FileType, String FileID) {
		String strUrl = getDownloadUrl(Token, FileType, FileID);
		System.out.println(strUrl);
		strUrl = strUrl.replace("&amp;", "&");
		// String strUrl =
		// "http://dms.d.cnki.net/DocService/padDoc.ashx?op=download&db=journals&name=%e6%81%b0%e5%b8%83%e5%9d%8e%e5%8d%93%e5%ae%83%e9%87%91%e7%9f%bf%e7%9f%bf%e5%ba%8a%e6%88%90%e5%9b%a0%e4%b8%8e%e6%89%be%e7%9f%bf%e6%a0%87%e5%bf%97&fn=xjys201306010&file=adzbxYGcuJVT0gHNrQXM4UVanNWasNFc3hVehpEWjR1R10ma0F3ViFWZKZVVBVUNzx0aBp1M4AHMl92a5MlZ1hFVjREMoZzVm9UW30UbUBVRERlavlkUsFUWGxWQ5BHa90zZrIlQURnaOBjVNZFdN1GVMVVZqR2c5FVOltUN5AlQzdWcrIHOHFUay0mYipHZiNEd3smNpdUU05WZLxEaU10UDlXaal1biZ1d0gHSx80aapleXpnUhJHSRJkdqFFS";
		// String
		// strUrl="http://oversea.d.cnki.net/DocService/padDoc.ashx?op=download&db=journals&name=%e5%9f%ba%e4%ba%8eLinux%e7%8e%af%e5%a2%83%e7%bd%91%e4%b8%8a%e8%be%85%e5%8a%a9%e7%ad%94%e7%96%91%e7%bd%91%e7%ab%99%e7%9a%84%e8%ae%be%e8%ae%a1&fn=njyj200302093&file=2J0ayFzQIhnU5pUVrgldpF1SN9ENO50QXpncZtkVuRkW25EZ2oFdC9WZyJTMON3YONUMLJTbuFkTyUDaKlzN5FTOPhXMip1LyVzYrY0QXplNEhzKygzdHllUFRVWHVGS90TQD9mdQd2S4dnT1UWWzUFV44GNrIkWSJUaHFlY2NjbDtWMVNWU3VjWTlmNqlXS1J1RxwEdmJmQEJleXd1b4UWSY9GZHh0KElXVrUGNS1WYW9kZsBlWUtCVi9Wc2A3N";
		String strFileInfo = getFileInfo(strUrl);
		XmlReader xmlReader = XmlReader.Read(strFileInfo.getBytes());
		System.out.println(xmlReader);
		String strLength = xmlReader.GetXmlValue("root/document/length"); // 有filename
		// </docInfo> <length>124783</length>
		// <filename>njyj200302093.caj</filename> </document> <server> <cache>
		// <type validPeriod="longtime">file</type> <preparse>1</preparse>
		// </cache> <cluster> <threads>1</threads> <url
		// pri="10"><![CDATA[http://oversea.d.cnki.net/DocService/padDoc.ashx?op=download&db=journals&name=%e5%9f%ba%e4%ba%8eLinux%e7%8e%af%e5%a2%83%e7%bd%91%e4%b8%8a%e8%be%85%e5%8a%a9%e7%ad%94%e7%96%91%e7%bd%91%e7%ab%99%e7%9a%84%e8%ae%be%e8%ae%a1&fn=njyj200302093&file=lBlYExWeiJTcJB1br9mMONXZmx0coBXR2AjT6tUUFNEUHZja0kjc4JkN5g1QllGc350ZGhjV2U2ZaRnQ1cmUaxUcDpmSlVkMlF1NQRWbtFzS4MHNtdlM0JlYuR2dvBFd90TUlp3ZUV3UqhEWUdXavkXdUJmbKZFdU90Mx40StJUZ2kVSXdmcGdnYwZ0ShJmTLFnWXVmdyJEcoZnT4UFVs5ESRBHSU12LKN0dwpkQolVcroEdEFXVP12aCxmc1hla]]></url>
		// <url
		// pri="9"><![CDATA[http://oversea.d.cnki.net/DocService/padDoc.ashx?op=download&db=journals&name=%e5%9f%ba%e4%ba%8eLinux%e7%8e%af%e5%a2%83%e7%bd%91%e4%b8%8a%e8%be%85%e5%8a%a9%e7%ad%94%e7%96%91%e7%bd%91%e7%ab%99%e7%9a%84%e8%ae%be%e8%ae%a1&fn=njyj200302093&file=lBlYExWeiJTcJB1br9mMONXZmx0coBXR2AjT6tUUFNEUHZja0kjc4JkN5g1QllGc350ZGhjV2U2ZaRnQ1cmUaxUcDpmSlVkMlF1NQRWbtFzS4MHNtdlM0JlYuR2dvBFd90TUlp3ZUV3UqhEWUdXavkXdUJmbKZFdU90Mx40StJUZ2kVSXdmcGdnYwZ0ShJmTLFnWXVmdyJEcoZnT4UFVs5ESRBHSU12LKN0dwpkQolVcroEdEFXVP12aCxmc1hla]]></url>
		// </cluster> </server></root>
		System.out.println(strLength);
		if (strLength == null) {
			return false;
		}
		String strRange = "bytes=0-".concat(strLength);
		byte[] bFile = getFile(strUrl, strRange);
		try {
			// FileOutputStream fsInput = new
			// FileOutputStream(UploadMngr.getTempFilePath() + FileID + ".caj");
			FileOutputStream fsInput = new FileOutputStream("d:\\test04.caj");
			fsInput.write(bFile, 0, bFile.length);
			fsInput.close();
		} catch (Exception e) {
			Logger.WriteException(e);
			return false;
		}
		return true;
	}

	public static long getFileSize(String Token, String FileType, String FileID) {
		// String strUrl = getDownloadUrl(Token, FileType, FileID);
		String strUrl = "http://dms.d.cnki.net/DocService/padDoc.ashx?op=download&db=journals&name=%e6%81%b0%e5%b8%83%e5%9d%8e%e5%8d%93%e5%ae%83%e9%87%91%e7%9f%bf%e7%9f%bf%e5%ba%8a%e6%88%90%e5%9b%a0%e4%b8%8e%e6%89%be%e7%9f%bf%e6%a0%87%e5%bf%97&fn=xjys201306010&file=adzbxYGcuJVT0gHNrQXM4UVanNWasNFc3hVehpEWjR1R10ma0F3ViFWZKZVVBVUNzx0aBp1M4AHMl92a5MlZ1hFVjREMoZzVm9UW30UbUBVRERlavlkUsFUWGxWQ5BHa90zZrIlQURnaOBjVNZFdN1GVMVVZqR2c5FVOltUN5AlQzdWcrIHOHFUay0mYipHZiNEd3smNpdUU05WZLxEaU10UDlXaal1biZ1d0gHSx80aapleXpnUhJHSRJkdqFFS";
		String strFileInfo = getFileInfo(strUrl);
		XmlReader xmlReader = XmlReader.Read(strFileInfo.getBytes());
		String strLength = xmlReader.GetXmlValue("root/document/length");
		if (strLength == null) {
			return 0;
		} else {
			return Long.parseLong(strLength);
		}
	}

	public static byte[] downloadFileBytes(String Token, String FileType, String FileID, String Range) {
		// String strUrl = getDownloadUrl(Token, FileType, FileID);
		String strUrl = "http://dms.d.cnki.net/DocService/padDoc.ashx?op=download&db=journals&name=%e6%81%b0%e5%b8%83%e5%9d%8e%e5%8d%93%e5%ae%83%e9%87%91%e7%9f%bf%e7%9f%bf%e5%ba%8a%e6%88%90%e5%9b%a0%e4%b8%8e%e6%89%be%e7%9f%bf%e6%a0%87%e5%bf%97&fn=xjys201306010&file=adzbxYGcuJVT0gHNrQXM4UVanNWasNFc3hVehpEWjR1R10ma0F3ViFWZKZVVBVUNzx0aBp1M4AHMl92a5MlZ1hFVjREMoZzVm9UW30UbUBVRERlavlkUsFUWGxWQ5BHa90zZrIlQURnaOBjVNZFdN1GVMVVZqR2c5FVOltUN5AlQzdWcrIHOHFUay0mYipHZiNEd3smNpdUU05WZLxEaU10UDlXaal1biZ1d0gHSx80aapleXpnUhJHSRJkdqFFS";
		byte[] bFile = getFile(strUrl, Range);
		return bFile;
	}

	public static int getDownloadFileCount(String appID, String FileName, String UserName, String OrgName, String IsOrg,
			String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (!Common.IsNullOrEmpty(appID)) {
			sbCondition.append("appid = '").append(dbHelper.FilterSpecialCharacter(appID)).append("' and ");
		}
		if (FileName != null) {
			sbCondition.append("filename like '").append(dbHelper.FilterSpecialCharacter(FileName)).append("%' and ");
		}
		if (UserName != null) {
			sbCondition.append("username like '").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
		}
		if (OrgName != null) {
			sbCondition.append("orgname = '").append(dbHelper.FilterSpecialCharacter(OrgName)).append("' and ");
		}
		if (IsOrg != null) {
			sbCondition.append("isorg = ").append(dbHelper.FilterSpecialCharacter(IsOrg)).append(" and ");
		}
		if (!Common.IsNullOrEmpty(StartDate)) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(EndDate)) {
			sbCondition.append("time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		// iCount = dbHelper.GetCount("downloadinfo", sbCondition.toString());

		Object[] arrParam = new Object[3];
		arrParam[0] = "downloadinfo";
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

	public static List<Map<String, Object>> getDownloadFileList(String appID, String FileName, String UserName,
			String OrgName, String IsOrg, String StartDate, String EndDate, int Start, int Length) {
		List<Map<String, Object>> lstFile = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (!Common.IsNullOrEmpty(appID)) {
			sbCondition.append("appid = '").append(dbHelper.FilterSpecialCharacter(appID)).append("' and ");
		}
		if (FileName != null) {
			sbCondition.append("filename like '").append(dbHelper.FilterSpecialCharacter(FileName)).append("%' and ");
		}
		if (UserName != null) {
			sbCondition.append("username like '").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
		}
		if (OrgName != null) {
			sbCondition.append("orgname = '").append(dbHelper.FilterSpecialCharacter(OrgName)).append("' and ");
		}
		if (IsOrg != null) {
			sbCondition.append("isorg = ").append(dbHelper.FilterSpecialCharacter(IsOrg)).append(" and ");
		}
		if (!Common.IsNullOrEmpty(StartDate)) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' AND ");
		}
		if (!Common.IsNullOrEmpty(EndDate)) {
			sbCondition.append("time <'")
					.append(dbHelper
							.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000)))
					.append("' AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[6];
		arrParam[0] = "downloadinfo";
		arrParam[1] = "id,filename,filetype,username,client,address,isorg,orgname,time";
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
		// lstFile =
		// dbHelper.ExecuteQuery("id,username,filename,fileid,client,address,time",
		// "downloadinfo", sbCondition.toString(), "time desc", Start, Length);

		return lstFile;
	}

	public static boolean deleteDownloadFileInfo(String ID, String UserName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete(getDownloadTable(UserName),
					"id='".concat(dbHelper.FilterSpecialCharacter(ID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/**
	 * 
	 * @param FileID
	 * @param TableName
	 * @return
	 */
	public static Map<String, Object> getFileInfo(String FileID, String TableName) {
		List<Map<String, Object>> lstFileInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstFileInfo = dbHelper.ExecuteQuery("filename,filesize,typename,filemd5", TableName,
					"fileid='" + FileID + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstFileInfo == null) {
			return null;
		} else {
			return lstFileInfo.get(0);
		}
	}

	public static List<Map<String, Object>> getDownloadDayLog(String startDate, String endDate) {

		List<Map<String, Object>> lstLogInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstLogInfo = dbHelper.ExecuteQuery("count,spottime", "downloadloginfo",
					"spottime between '" + startDate + "' and '" + endDate + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstLogInfo == null) {
			return null;
		} else {
			return lstLogInfo;
		}
	}

	/*public static int getTodayDownloadCount(String userName) {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery(
					"select count(*) count from (select * from " + getDownloadTable(userName) + " where username='"
							+ userName + "' and time>'" + Common.GetDate() + "' GROUP BY typeid,fileid)t");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lst == null || lst.size() == 0)
			return 0;
		return Integer.parseInt(lst.get(0).get("count").toString());
	}*/
	
	public static boolean addTodayDown(DownloadInfoBean DownloadInfo){
		boolean bolRet = false;
		HttpServletRequest request = HttpContext.GetRequest();
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("downtoday",
					new String[] { "username", "typeid", "filename", "filetype", "fileid", "client", "address", "appid",
							"isorg", "orgname", "time" },
					new Object[] { DownloadInfo.getUserName(), DownloadInfo.getTypeID(), DownloadInfo.getFileName(),
							DownloadInfo.getFileType(), DownloadInfo.getFileID(), strAgent, strClientAddr,
							DownloadInfo.getAppID(), DownloadInfo.getIsorg(), DownloadInfo.getOrgName(),
							Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}
	
	public static int getTodayDownloadCount(String userName) {
		DBHelper dbHelper = null;
		int icount = 0;
		try {
			dbHelper = DBHelper.GetInstance();
			icount = dbHelper.GetCount("downtoday", "username='"
							+ userName + "' and time>'" + Common.GetDate() + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return icount;
	}

}
