package BLL;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import DAL.DBHelper;
import Model.EPubFileBean;
import Model.ODataFileBean;
import Util.Common;
import Util.XmlReader;

public class ODataMngr {

	public static boolean existsFileInDB(String TypeID, String FileID) {
		int iCount = 0;
		DBHelper dbHelper;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(getFileTable(TypeID, FileID), "fileid='".concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据TypeID和FileID获取表名称
	 * 
	 * @param TypeID
	 * @param FileID
	 * @return
	 */
	public static String getFileTable(String TypeID, String FileID) {
		return TypeID.concat(String.valueOf(Math.abs(FileID.hashCode())).substring(0, 1)).toLowerCase();
	}

	public static boolean existsFileTable(String TableName) {
		int iCount = 0;
		DBHelper dbHelper;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("cnkifiletable", "tablename='".concat(TableName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (iCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean createTable(String TableName) {
		boolean bResult = true;
		DBHelper dbHelper;
		try {
			dbHelper = DBHelper.GetInstance();
			List<String> lstSql = new ArrayList<String>();
			lstSql.add("create table ".concat(TableName).concat(" like cnkifiletemplate"));
			lstSql.add("insert into cnkifiletable(tablename,time) values('".concat(TableName).concat("','" + Common.GetDateTime() + "')"));
			bResult = dbHelper.ExecuteSql(lstSql);
			lstSql = null;
		} catch (Exception e) {
			Logger.WriteException(e);
			bResult = false;
		}
		return bResult;
	}

	public static boolean saveODataInfo(ODataFileBean FileInfo, String TableName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(TableName, new String[] { "fileid", "filename", "filesize", "typename", "filemd5", "time" }, new Object[] { FileInfo.getFileID(), FileInfo.getFileName(), FileInfo.getFileSize(), FileInfo.getTypeName(), FileInfo.getFileMd5(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean saveODataEpubInfo(EPubFileBean FileInfo, String TableName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert(TableName, new String[] { "fileid", "filename", "typename", "filesize", "time" }, new Object[] { FileInfo.getFileID(), FileInfo.getFileName(), FileInfo.getTypeName(), FileInfo.getFileSize(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean isExistEpub(String TypeID, String FileID) {
		String strTableName = TypeID.concat(String.valueOf(Math.abs(FileID.hashCode())).substring(0, 1)).toLowerCase();
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("ishasepub", strTableName, "fileid='".concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if ("1".equals(String.valueOf(lstInfo.get(0).get("ishasepub")))) {
			return true;
		}
		return false;
	}

	/**
	 * 获取ODataToken
	 * 
	 * @param UserName
	 *            用户名
	 * @param PassWord
	 *            密码
	 * @return JSONObject
	 */
	public static JSONObject getUserToken(String UserName, String PassWord) {
		String strUrl = Common.GetConfig("AuthUrl");
		String strAppID = Common.GetConfig("AppID");
		String strAppSecret = Common.GetConfig("AppSecret");
		URL url;
		StringBuilder sbToken = new StringBuilder();
		try {
			url = new URL(strUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			String timeSign = String.valueOf(System.currentTimeMillis());
			String strParam = "grant_type=password&username=".concat(UserName).concat("&password=").concat(Common.getBase64Password(PassWord, "jds)(#&dsa7SDNJ32hwbds%u32j33edjdu2@**@3w")).concat("&client_id=").concat(strAppID).concat("&client_secret=").concat(Common.SHA1(timeSign.concat(strAppSecret))).concat("&sign=").concat(timeSign);
			http.getOutputStream().write(strParam.getBytes("utf-8"));
			http.getOutputStream().flush();
			http.getOutputStream().close();
			if (http.getResponseCode() != 200) {
				Logger.WriteException(new Exception("获取OataToken失败:" + String.valueOf(http.getResponseCode())));
				return null;
			}
			InputStream input = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				sbToken.append(line);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		} finally {

		}
		return JSONObject.fromObject(sbToken.toString());
	}

	/**
	 * 在web服务器上处理pdf转epub时应用(暂时注释掉。epub转换独立出该项目)
	 * 
	 * @param Token
	 *            OData系统Token
	 * @param TypeID
	 *            文件所属类别：期刊：journals，博士论文：doctortheses，硕士论文：mastertheses，会议：
	 *            conferences，报纸：newspapers
	 * @param FileID
	 *            文件的fileID:期刊的如：xwll20101129002
	 * @param FileType
	 *            文件类型：caj，pdf等
	 * @param TableName
	 *            临时目录的名称
	 * @return
	 */
	// public static boolean downloadFile(String Token, String TypeID, String
	// FileID, String FileType, String TableName) {
	// String strUrl = getODataDownUrl(Token, TypeID, FileID);
	// if ("pdf".equalsIgnoreCase(FileType)) {
	// strUrl = strUrl.concat("&rt=pdf");
	// }
	// URL url = null;
	// HttpURLConnection urlconn = null;
	// byte[] bRet = null;
	// try {
	// url = new URL(strUrl);
	// urlconn = (HttpURLConnection) url.openConnection();
	// if (!"pdf".equalsIgnoreCase(FileType)) {
	// urlconn.setRequestProperty("ACCEPT-Range", "bytes=0-");
	// }
	// urlconn.setDoInput(true);
	// urlconn.connect();
	// bRet = new byte[urlconn.getContentLength()];
	// DataInputStream dataInput = new
	// DataInputStream(urlconn.getInputStream());
	// dataInput.readFully(bRet);
	// urlconn.getInputStream().close();
	// dataInput.close();
	// FileOutputStream fsInput = new
	// FileOutputStream(UploadMngr.getTempFilePath(TableName) +
	// FileID.concat(".").concat(FileType));
	// fsInput.write(bRet, 0, bRet.length);
	// fsInput.close();
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// return false;
	// } finally {
	// urlconn.disconnect();
	// url = null;
	// urlconn = null;
	// }
	// return true;
	// }

	/**
	 * 在web服务器上处理pdf转epub时应用
	 * 
	 * @param FileID
	 *            文件的FIld
	 * @param FileType
	 *            文件的类型caj or pdf等等
	 * @param TableName
	 *            临时目录位置
	 * @return
	 */
	public static ODataFileBean getDownFileInfo(String FileID, String FileName, String FileType, String TableName) {
		File file = new File(UploadMngr.getTempFilePath(TableName) + FileID.concat(".").concat(FileType));
		if (!file.exists())
			return null;
		ODataFileBean odataFileBean = new ODataFileBean();
		odataFileBean.setFileID(FileID);
		odataFileBean.setFileName(FileName);
		odataFileBean.setFileSize(String.valueOf(file.length()));
		odataFileBean.setTypeName(FileType);
		return odataFileBean;
	}

	/***
	 * 获取用户类别
	 * 
	 * @param Token
	 * @param FileType
	 * @param FileID
	 * @return
	 */
	public static String getUserCategory(String Token, String FileType, String FileID) {
		String strServer = Common.GetConfig("DownloadServer");
		String strUrl = strServer.concat("/file/").concat(FileType).concat("/").concat(FileID).concat("/price");
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
			if (200 == urlconn.getResponseCode()) {

			} else if (401 == urlconn.getResponseCode()) {
				return "";
			} else if (402 == urlconn.getResponseCode()) {
				return "";
			} else {

			}
			bufReader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
			while ((strLine = bufReader.readLine()) != null) {
				strResult = strResult.concat(strLine);
			}
			bufReader.close();
		} catch (Exception e) {
			java.io.BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getErrorStream()));
			StringBuilder sbException = new StringBuilder();
			String s = null;
			try {
				while ((s = in.readLine()) != null) {
					sbException.append(s);
				}
			} catch (IOException e1) {
				return null;
			}
			return sbException.toString();

		} finally {
			urlconn.disconnect();
			url = null;
			urlconn = null;
		}
		if (!"".equals(strResult)) {
			strRet = "http".concat(strResult.substring(strResult.indexOf(':')));
			strRet = strRet.replace("&amp;", "&");
			if (strRet.endsWith("\"")) {
				strRet = strRet.substring(0, strRet.length() - 1);
			}
		}
		return strRet;
	}

	/**
	 * 获取OData下载数据的URL
	 * 
	 * @param Token
	 *            ODataToken
	 * @param FileType
	 *            如：TypeID：journals
	 * @param FileID
	 *            文件id
	 * @return
	 */
	public static String getODataDownUrl(String Token, String TypeID, String FileID) {
		String strServer = Common.GetConfig("DownloadServer");
		String strUrl = strServer.concat("/file/").concat(TypeID).concat("/").concat(FileID).concat("/download");
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
			java.io.BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getErrorStream()));
			StringBuilder sbException = new StringBuilder();
			String s = null;
			try {
				while ((s = in.readLine()) != null) {
					sbException.append(s);
				}
			} catch (IOException e1) {
				Logger.WriteException(e);
				return null;
			}
			// System.out.println(sbException.toString());
			// JSONObject jo = JSONObject.fromObject(sbException.toString());
			// Logger.WriteException(e);
			// return jo.get("error").toString();
			return sbException.toString();
		} finally {
			urlconn.disconnect();
			url = null;
			urlconn = null;
		}
		if (!"".equals(strResult)) {
			strRet = "http".concat(strResult.substring(strResult.indexOf(':')));
			strRet = strRet.replace("&amp;", "&");
			if (strRet.endsWith("\"")) {
				strRet = strRet.substring(0, strRet.length() - 1);
			}
		}
		return strRet;
	}

	/***
	 * 根据Url和Range，获取OData上的数据段
	 * 
	 * @param Url
	 * @param Range
	 * @return
	 */
	public static byte[] getRequestData(String Url, String Range) {
		URL url = null;
		HttpURLConnection urlconn = null;
		byte[] bRet = null;
		try {
			url = new URL(Url);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestProperty("ACCEPT-Range", "bytes=".concat(Range));
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

	public static Map<String, Object> getFileXMLInfo(String strUrl) {
		XmlReader xmlReader = null;
		try {
			xmlReader = XmlReader.Read(getFileInfo(strUrl));
		} catch (Exception e) {
			Logger.WriteException(new Exception("获取FileInfo失败".concat(e.getMessage() == null ? "。" : ":" + e.getMessage())));
			return null;
		}
		if (xmlReader == null) {
			Logger.WriteException(new Exception("获取FileInfo失败"));
			return null;
		}
		String strLength = xmlReader.GetXmlValue("root/document/length");
		String strFileName = xmlReader.GetXmlValue("root/document/filename");
		String strDocInfo = xmlReader.GetXmlValue("root/document/docInfo");
		Common.base64DecodeStr(strDocInfo);
		XmlReader xmlReader2 = XmlReader.Read(Common.base64DecodeStr(strDocInfo));

		String strTitle = xmlReader2.GetXmlValue("root/docInfo/title");
		String strFileMd5 = xmlReader2.GetXmlValue("root/docInfo/filemd5");
		Map<String, Object> mapFileXml = new HashMap<String, Object>();
		mapFileXml.put("typename", strFileName.contains(".") ? strFileName.substring(strFileName.lastIndexOf(".") + 1, strFileName.length()) : "caj");
		mapFileXml.put("filesize", strLength);
		mapFileXml.put("filename", strTitle);
		mapFileXml.put("filemd5", strFileMd5);
		return mapFileXml;
	}

	/***
	 * 2014.04.23 修改：将filename改为title+filetype的形式.如果取出来的filename没有扩展名，
	 * 
	 * @param Token
	 * @param FileType
	 * @param FileID
	 * @return
	 */
	public static Map<String, Object> getFileXMLInfo(String FileID, String TypeID, byte[] bXml) {
		XmlReader xmlReader = null;
		try {
			xmlReader = XmlReader.Read(bXml);
		} catch (Exception e) {
			Logger.WriteException(new Exception("获取FileInfo失败:".concat(e.getMessage())));
			return null;
		}
		if (xmlReader == null) {
			Logger.WriteException(new Exception("获取FileInfo失败"));
			return null;
		}
		String strLength = xmlReader.GetXmlValue("root/document/length");
		String strFileName = xmlReader.GetXmlValue("root/document/filename");
		String strDocInfo = xmlReader.GetXmlValue("root/document/docInfo");
		Common.base64DecodeStr(strDocInfo);
		XmlReader xmlReader2 = XmlReader.Read(Common.base64DecodeStr(strDocInfo));

		String strTitle = xmlReader2.GetXmlValue("root/docInfo/title");
		String strFileMd5 = xmlReader2.GetXmlValue("root/docInfo/filemd5");
		Map<String, Object> mapFileXml = new HashMap<String, Object>();
		mapFileXml.put("fileid", FileID);
		mapFileXml.put("typeid", TypeID);
		mapFileXml.put("typename", strFileName.contains(".") ? strFileName.substring(strFileName.lastIndexOf(".") + 1, strFileName.length()) : "caj");
		mapFileXml.put("filesize", strLength);
		mapFileXml.put("filename", strTitle);
		mapFileXml.put("filemd5", strFileMd5);
		return mapFileXml;
	}

	/**
	 * 根据Url获取xml的数据
	 * 
	 * @param Url
	 * @return
	 */
	public static byte[] getFileInfo(String Url) {
		URL url = null;
		HttpURLConnection urlconn = null;
		byte[] arrUrlContent = null;
		try {
			url = new URL(Url);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestProperty("REQUEST-ACTION", "fileinfo");
			urlconn.connect();
			int iReqContet = urlconn.getContentLength();
			if (iReqContet > 0) {
				arrUrlContent = new byte[urlconn.getContentLength()];
				DataInputStream dataInput = new DataInputStream(urlconn.getInputStream());
				dataInput.readFully(arrUrlContent);
			} else {
				Logger.WriteException(new Exception("获取FileInfo失败"));
				return null;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		} finally {
			urlconn.disconnect();
			url = null;
			urlconn = null;
		}
		return arrUrlContent;
	}

	public static List<Map<String, Object>> getFileEpubInfo(Map<String, String> mapFileInfo) {
		StringBuilder sbSql = new StringBuilder();
		String strTable = "";
		for (Entry<String, String> entry : mapFileInfo.entrySet()) {
			strTable = entry.getKey().concat(String.valueOf(Math.abs(entry.getValue().hashCode())).substring(0, 1)).toLowerCase();
			sbSql.append("(select '").append(entry.getKey()).append("' as typeid,").append("fileid,ishasepub,filename,filesize from ").append(strTable).append(" where fileid='").append(entry.getValue()).append("') union all ");
		}
		if (sbSql.length() > 0) {
			sbSql.delete(sbSql.length() - 11, sbSql.length());
		} else {
			return null;
		}
		List<Map<String, Object>> lstFileEpubInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstFileEpubInfo = dbHelper.ExecuteQuery(sbSql.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstFileEpubInfo;
	}

	/************************* 0917新加 ***********************************/
	/**
	 * 0917 新
	 * 
	 * @param FileID
	 * @param TableName
	 * @return
	 */
	public static Map<String, Object> getFileInfo(String TypeID, String FileID) {
		List<Map<String, Object>> lstFileInfo = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstFileInfo = dbHelper.ExecuteQuery("filename,filesize,typename,filemd5,ishasepub", getFileTable(TypeID, FileID), "fileid='" + FileID + "'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstFileInfo == null) {
			return null;
		} else {
			return lstFileInfo.get(0);
		}
	}

	public static boolean getODataProperties(Map<String, String> mapProperties, String TypeID) {
		String strServer = Common.GetConfig("DownloadServer");
		String strUrl = strServer.concat("/model/types/").concat(TypeID).concat("/properties");
		URL searchUrl;
		StringBuilder sbData = null;
		try {
			searchUrl = new URL(strUrl);
			HttpURLConnection http = (HttpURLConnection) searchUrl.openConnection();
			http.setRequestMethod("GET");

			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setUseCaches(false);
			http.setDoInput(true);
			if (http.getResponseCode() != 200) {
				return false;
			}
			InputStream input = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

			String line;
			sbData = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sbData.append(line);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		int iFlag = mapProperties.size();
		if (sbData != null && sbData.length() > 0) {
			JSONArray recordArray = JSONArray.fromObject(sbData.toString());
			JSONObject jsonObject = null;
			for (int i = 0; i < recordArray.size(); i++) {
				jsonObject = recordArray.getJSONObject(i);
				for (Entry<String, String> en : mapProperties.entrySet()) {
					if (en.getKey().equals(jsonObject.get("label"))) {
						mapProperties.put(en.getKey(), String.valueOf(jsonObject.get("uri")));
						iFlag--;

					}
				}
				if (iFlag == 0) {
					break;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public static boolean isSecrecyFile(String AppToken, String TypeID, String FileID) {
		boolean bResult = false;
		String strServer = Common.GetConfig("DownloadServer");
		String strUrl = strServer.concat("/file/").concat(TypeID).concat("/").concat(FileID).concat("/download");
		URL url = null;
		HttpURLConnection urlconn = null;
		try {
			url = new URL(strUrl);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestMethod("GET");
			urlconn.setUseCaches(false);
			urlconn.setDoInput(true);
			urlconn.addRequestProperty("authorization", "Bearer ".concat(AppToken));
			urlconn.connect();
			if (403 == urlconn.getResponseCode()) {
				bResult = true;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		} finally {
			urlconn.disconnect();
			url = null;
			urlconn = null;
		}
		return bResult;
	}
}
