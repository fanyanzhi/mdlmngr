package Request;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Holder;

import net.cnki.hfs.FileClient;
import net.cnki.hfs.HFSInputStream;
import net.cnki.hfs.HFS_OPEN_FILE;
import net.cnki.sso.UserStruct;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import BLL.AppInfoMngr;
import BLL.BehaviourMngr;
import BLL.CnkiMngr;
import BLL.DownloadMngr;
import BLL.FreeDownMngr;
import BLL.HfmsMngr;
import BLL.Logger;
import BLL.ODataMngr;
import BLL.SignMngr;
import BLL.SysConfigMngr;
import BLL.UploadMngr;
import BLL.UserFeeMngr;
import BLL.UserInfoMngr;
import BLL.UserOrgMngr;
import Model.DownloadInfoBean;
import Model.MobileRightStatus;
//import Model.UploadInfoBean;
import Model.UserLoginBean;
import Util.Common;
import Util.KCMSKey;
import Util.LoggerFile;

/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/download/*")
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static FileClient fc = new FileClient(Common.GetConfig("HfsServer"));

	int mDownFileNum = Integer.parseInt(Common.GetConfig("FileQueueSize"));
	private List<String> mFileList = new ArrayList<String>();
	private static Map<String, Object[]> mFileInStream = new HashMap<String, Object[]>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");

		String strFileInfo = request.getHeader("request-action"); // 判断是否是在线阅读
		String strAction = request.getPathInfo().replace("/", "").toLowerCase(); // 操作

		if (!"getfile".equals(strAction)) {
			if (Common.IsNullOrEmpty(strFileInfo)) {
				sendResponseData(response, "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
				return;
			}
		}
		String strRange = request.getHeader("accept-range");
		if (Common.IsNullOrEmpty(strRange)) {
			strRange = request.getHeader("Range");
		}

		String strToken = request.getParameter("usertoken");
		String strUserName = request.getParameter("username");
		String strFileName = request.getParameter("filename").trim();
		String strFileID = request.getParameter("fileid").trim();
		String strFileType = request.getParameter("filetype").trim();
		String strTypeID = request.getParameter("typeid").trim();
		String strSign = request.getParameter("sign");
		String strExpire = request.getParameter("expire");
		// 变量
		String strRet = ""; // 返回值
		boolean bolAddInfo = true; // 是否增加下载信息
		boolean bolChekTime = true; // 判断时间戳

		if (strToken == null) {
			bolAddInfo = false;
			bolChekTime = false;
			UserLoginBean userInfo = (UserLoginBean) request.getSession().getAttribute("LoginObj");
			if (userInfo == null) {
				sendResponseData(response, "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("}"));
				return;
			}
		} else {
			strUserName = UserInfoMngr.UserLogin(strToken);
			if (strUserName.startsWith("@-")) {
				response.setStatus(500);
				strRet = "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
						.concat(strUserName.substring(1)).concat("}");
				response.setHeader("error", strRet);
				sendResponseData(response, strRet);
				return;
			}
		}

		Map<String, Object> mapFile = new HashMap<String, Object>();
		mapFile.put("fileid", strFileID);
		mapFile.put("range", strRange);
		mapFile.put("filetype", strFileType);
		mapFile.put("typeid", strTypeID);
		mapFile.put("usertoken", strToken);
		mapFile.put("filename", strFileName);
		if (bolChekTime && !"epub".equals(strFileType) && !"cajcloud".equalsIgnoreCase(strTypeID)) {
			if (strSign == null || strExpire == null) {
				sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
						.concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
				return;
			}
			long lCurTime = System.currentTimeMillis();
			if (Long.parseLong(strExpire) < (lCurTime - 60000L * 60 * 12)
					|| Long.parseLong(strExpire) > (lCurTime + 60000L * 60 * 12)
					|| !strSign.equals(Common.SHA1(strExpire.concat("cajcloud")))) {
				response.setStatus(500);
				strRet = "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_URLTIME.code)).concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_URLTIME.code)).concat("}");
				response.setHeader("error", strRet);
				sendResponseData(response, strRet);
				return;
			}
			boolean bExistFile = false;
			int iTime = 360;
			while (!bExistFile && iTime > 0) {
				bExistFile = ODataMngr.existsFileInDB(strTypeID, strFileID);
				if (!bExistFile) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (iTime % 10 == 0) {
						if (CnkiMngr.isErrorFile(strTypeID, strFileID)) {
							Logger.WriteDownTraceLog(strUserName, strTypeID, strFileID, 1, "cnki error file", 0);
							break;
						}
					}
				}
				iTime--;
			}
			if (!bExistFile) {
				Logger.WriteDownTraceLog(strUserName, strTypeID, strFileID, 1,
						"after 3 minute file still not exists:cnki error file or file is downloading very slowly", 0);
				response.setStatus(500);
				strRet = "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_SOURCEFILE.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_SOURCEFILE.code)).concat("}");
				response.setHeader("error", strRet);
				sendResponseData(response, strRet);
				return;
			}
			if (!Common.IsNullOrEmpty(strFileInfo) && "fileinfo".equalsIgnoreCase(strFileInfo)) {
				Logger.WriteDownTraceLog(strUserName, strTypeID, strFileID, 1, "start get xml for online read", 1);
				sendResponseData(response, getFileXML(strUserName, mapFile, request, true));
			} else if (!Common.IsNullOrEmpty(strFileInfo) && "client-quit".equalsIgnoreCase(strFileInfo)) {
				return;
			} else if ("1".equals(Common.GetConfig("DirectDown") == null ? "0" : Common.GetConfig("DirectDown"))) { // caj或者全文后台出错，直接从原文下载
				getSourceFile(mapFile, strUserName, request, response, bolAddInfo);
			} else {
				Logger.WriteDownTraceLog(strUserName, strTypeID, strFileID, 1,
						"get download file data from getFileData", 1);
				strRet = getFileData(mapFile, strUserName, request, response, bolAddInfo);
			}
		} else {
			Logger.WriteDownTraceLog(strUserName, strTypeID, strFileID, 1,
					"get download file data from getFileFromHfms", 1);
			strRet = getFileFromHfms(mapFile, strUserName, request, response, bolAddInfo);
		}

		if (Common.IsNullOrEmpty(strRet)) {
			return;
		}
		if (strRet != null && strRet.startsWith("{\"result\":false")) {
			response.setStatus(500);
			response.setHeader("error", strRet);
		}
		sendResponseData(response, strRet);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		long start = System.currentTimeMillis();
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"),start);
			return;
		}

		byte[] arrReq = new byte[request.getContentLength()];
		DataInputStream dataInput = new DataInputStream(request.getInputStream());
		dataInput.readFully(arrReq);
		String strReq = new String(arrReq, "utf-8");
		arrReq = null;
		String strUserName = "";
		JSONObject jo = null;
		Map<String, Object> mapInfo = null;
		JSONArray jsonArray = null;
		if ("getepubstatus".equalsIgnoreCase(strAction.replace("/", ""))) { // 可能是在没有登录的情况下
			jsonArray = JSONArray.fromObject(strReq);
		} else if ("existsepub".equalsIgnoreCase(strAction.replace("/", ""))) {
			jo = JSONObject.fromObject(strReq);
			mapInfo = (Map<String, Object>) jo;
		} else {
			jo = JSONObject.fromObject(strReq);
			mapInfo = (Map<String, Object>) jo;
			String strToken = (String) mapInfo.get("usertoken");
			strUserName = UserInfoMngr.UserLogin(strToken);
			if (strUserName.startsWith("@-")) {
				if ("getfile".equals(strAction)) {
					response.setStatus(500);
					response.setHeader("error",
							"{\"result\":false,\"message\":\"".concat(strUserName.substring(1))
									.concat("\",\"errorcode\":").concat(strUserName.substring(1))
									.concat(",\"errcode\":").concat(strUserName.substring(1)).concat("}"));
				}
				sendResponseData(response,
						"{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":")
								.concat(strUserName.substring(1)).concat(",\"errcode\":")
								.concat(strUserName.substring(1)).concat("}"),start);
				return;
			}
		}
		if ("getfile".equals(strAction.replace("/", "").toLowerCase())) {
			if (!"cajcloud".equals((String) mapInfo.get("typeid"))) {
				if (!"epub".equals(String.valueOf(mapInfo.get("filetype")))) {
					sendResponseData(response, "{\"result\":false,\"message\":\""
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
							.concat("\",\"errorcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
							.concat(",\"errcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"),start);
					return;
				}
			}
		}
		if (mapInfo != null && mapInfo.containsKey("typeid")) {
			if ("CAPJ".equalsIgnoreCase((String) mapInfo.get("typeid"))) {
				mapInfo.put("typeid", "cjfd");
			}
			String[] arrType = CnkiMngr.getTypes((String) mapInfo.get("typeid"));
			if (arrType == null) {
				sendResponseData(response, "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"),start);
				return;
			}
			mapInfo.put("typeid", arrType[0]);
			mapInfo.put("newtypeid", arrType[1]);
			mapInfo.put("odatatype", arrType[2]);
		}

		JSONArray newJsonArray = null;
		JSONObject recordObj = null;
		if (jsonArray != null && jsonArray.size() > 0) {
			newJsonArray = new JSONArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				recordObj = JSONObject.fromObject(jsonArray.get(i));
				if (recordObj.containsKey("typeid")) {
					String[] arrType = CnkiMngr.getTypes((String) recordObj.get("typeid"));
					if (arrType == null) {
						sendResponseData(response, "{\"result\":false,\"message\":\""
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
								.concat("\",\"errorcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
								.concat(",\"errcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"),start);
						return;
					}
					recordObj.put("typeid", arrType[0]);
					recordObj.put("newtypeid", arrType[1]);
				}
				newJsonArray.add(recordObj);
			}
			if (newJsonArray.size() > 0) {
				jsonArray = JSONArray.fromObject(newJsonArray.toString());

			}
		}

		String strRet = null;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "chkauthority":
			strRet = chkUserAuthorityNew(mapInfo, strUserName, request);
			break;
		case "feefile":
			strRet = feeForFile(mapInfo, strUserName, request);
			break;
		case "pointexpense":
			strRet = scoreForFile(mapInfo, strUserName, request);
			break;
		case "getfile":
			strRet = getFileData(mapInfo, strUserName, request, response, true);
			if (strRet != null && strRet.startsWith("{\"result\":false")) {
				response.setStatus(500);
				response.setHeader("error", strRet);
			}
			break;
		case "getfilelist":
			strRet = getFileList(mapInfo, strUserName);
			break;
		case "getepubstatus":
			strRet = getEpubStatus(jsonArray);
			break;
		case "existsepub":
			strRet = isExistsEpub(mapInfo, request);
			break;
		case "accessright":
			strRet = accessRight(strUserName, mapInfo, request);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}

		if (Common.IsNullOrEmpty(strRet)) {
			return;
		}
		sendResponseData(response, strRet,start);
	}

	private void sendResponseData(HttpServletResponse response, String Data, long start) throws IOException {
		long end = System.currentTimeMillis();
		long timestmp = end - start;
		if(Data.startsWith("{")){
			JSONObject json = JSONObject.fromObject(Data);
			String ip = Common.GetConfig("ServerIp");
			json.put("ip", ip);
			json.put("ProcessingTime", timestmp);
			//System.out.println(json.toString());
			response.getOutputStream().write(json.toString().getBytes("utf-8"));
			response.getOutputStream().close();
		}else{
			response.getOutputStream().write(Data.getBytes("utf-8"));
			response.getOutputStream().close();
		}
	}
	
	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	private void sendResponseData(HttpServletResponse response, byte[] DataBytes) throws IOException {
		response.getOutputStream().write(DataBytes);
		response.getOutputStream().close();
	}

	private String getFileList(Map<String, Object> arg, String UserName) {
		List<Map<String, Object>> lstResult = UploadMngr.getFileList(arg, UserName);
		JSONArray jsonArray = new JSONArray();
		if (lstResult != null) {
			for (Map<String, Object> map : lstResult) {
				JSONObject jsonObj = new JSONObject();
				for (Entry<String, Object> entry : map.entrySet()) {
					jsonObj.put(entry.getKey(), entry.getValue());
				}
				jsonArray.add(jsonObj);
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	/**
	 * 直接从全文上下载 会有并发问题
	 * 
	 * @param arg
	 * @param UserName
	 * @param TempFilePath
	 * @param request
	 * @param response
	 * @param isAddInfo
	 * @return
	 */
	private String getFileFromHfms(Map<String, Object> arg, String UserName, HttpServletRequest request,
			HttpServletResponse response, boolean isAddInfo) {
		String appId = String.valueOf(request.getAttribute("app_id"));
		String strFileName = (String) arg.get("filename");
		String strRange = (String) arg.get("range");
		String strFileID = (String) arg.get("fileid");
		String strFileType = (String) arg.get("filetype");
		String strTypeID = (String) arg.get("typeid");
		String strMapFileKey = strFileID.concat(strFileType.toLowerCase());
		if (Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String strFileFullName;
		// Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 2,
		// "come in downfrmhfms", 1);
		if ("cajcloud".equalsIgnoreCase(strTypeID)) {
			String strDskFile = UploadMngr.getDskFileName(strFileID, UserName);
			if (!"epub".equalsIgnoreCase(strFileType)) {
				strFileFullName = HfmsMngr.getHfsFilePath(UploadMngr.getPersonalFilePath(strDskFile))
						.concat(strDskFile);
			} else {
				strFileFullName = HfmsMngr.getHfsFilePath(UploadMngr.getPersonalFilePath(strDskFile))
						.concat(strDskFile.substring(0, strDskFile.indexOf("."))).concat(".epub");
			}
		} else {
			String TempFilePath = strTypeID.concat(String.valueOf(Math.abs(strFileID.hashCode())).substring(0, 1))
					.toLowerCase();
			strFileFullName = HfmsMngr.getHfsFilePath(TempFilePath).concat(strFileID).concat(".").concat(strFileType);
		}

		String strFileExtName = "octet-stream";

		Object[] arrFileHandler = null;
		arrFileHandler = mFileInStream.get(strMapFileKey);
		if (arrFileHandler == null) {
			HFS_OPEN_FILE hof = null;
			try {
				hof = fc.OpenFile(strFileFullName, "rb");
			} catch (Exception e) {
				Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 2, "openfile failed", 0);
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILENOTEXIST.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILENOTEXIST.code)).concat("}");// 打开文件出错
			}
			if (hof == null) {
				Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 2, "openfile is null", 0);
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILENOTEXIST.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILENOTEXIST.code)).concat("}");
			}
			long lRet = hof.Handle;
			long lFileSize = hof.File.FileSize;
			HFSInputStream fileHandler = new HFSInputStream(fc, lRet);
			if (mFileList.size() > mDownFileNum) {
				String strTempFile = mFileList.get(0);
				closeFileHandler(strTempFile);
				mFileList.remove(0);
			}
			Lock lock = new ReentrantLock();
			lock.lock();
			try {
				if (!mFileList.contains(strMapFileKey)) {
					mFileList.add(strMapFileKey);
				}
			} finally {
				lock.unlock();
			}
			arrFileHandler = new Object[] { fileHandler, lRet, lFileSize };
			mFileInStream.put(strMapFileKey, arrFileHandler);
		} else {
			arrFileHandler = mFileInStream.get(strMapFileKey);
		}
		// if (mFileList.contains(strMapFileKey)) {
		// System.out.println(Common.GetDateTime()+":"+strMapFileKey+"句柄存在");
		// arrFileHandler = mFileInStream.get(strMapFileKey);
		// } else {
		// System.out.println(Common.GetDateTime()+":"+strMapFileKey+"不存在句柄");
		// HFS_OPEN_FILE hof = null;
		// try {
		// hof = fc.OpenFile(strFileFullName, "rb");
		// } catch (Exception e) {
		// Logger.WriteException(e);
		// return
		// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILENOTEXIST.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILENOTEXIST.code)).concat("}");//
		// 打开文件出错
		// }
		// if (hof == null) {
		// return
		// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILENOTEXIST.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILENOTEXIST.code)).concat("}");
		// }
		// long lRet = hof.Handle;
		// long lFileSize = hof.File.FileSize;
		// HFSInputStream fileHandler = new HFSInputStream(fc, lRet);
		// if (mFileList.size() > mDownFileNum) {
		// String strTempFile = mFileList.get(0);
		// closeFileHandler(strTempFile);
		// mFileList.remove(0);
		// }
		// Lock lock = new ReentrantLock();
		// lock.lock();
		// try {
		// if (!mFileList.contains(strMapFileKey)) {
		// mFileList.add(strMapFileKey);
		// }
		// } finally {
		// lock.unlock();
		// }
		// arrFileHandler = new Object[] { fileHandler, lRet, lFileSize };
		// mFileInStream.put(strMapFileKey, arrFileHandler);
		// }
		HFSInputStream fileHandler = (HFSInputStream) arrFileHandler[0];
		Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 2, "get file handler success", 1);
		long lStart = 0;
		long lEnd = 0;
		long lLength = (long) arrFileHandler[2];

		if (strFileName.lastIndexOf('.') > 0) {
			strFileExtName = strFileName.substring(strFileName.lastIndexOf('.'));
		}
		if (Common.IsNullOrEmpty(strRange) && request.getHeader("Range") != null) {
			strRange = request.getHeader("Range");
		}
		if (!Common.IsNullOrEmpty(strRange)) {
			strRange = strRange.replace("bytes=", "");
		}
		if (!Common.IsNullOrEmpty(strRange)) {
			if (strRange.indexOf("-") == strRange.length() - 1) {
				lStart = Long.valueOf(strRange.split("-")[0]);
				lEnd = lLength;
			} else {
				String[] arrRange = strRange.split("-");
				if (arrRange.length == 2) {
					lStart = Long.valueOf(Common.Trim(arrRange[0], " "));
					String strEnd = Common.Trim(arrRange[1], " ");
					lEnd = Long.valueOf(strEnd) > (lLength) ? lLength : Long.valueOf(strEnd);
				}
			}
		} else {
			lStart = 0;
			lEnd = lLength;
		}
		response.reset();
		response.setHeader("Accept-Ranges", "bytes");
		response.setHeader("Content-Length", new Long(lEnd - lStart).toString());
		if (lStart != 0) {
			response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
			response.setHeader("Content-Range", "bytes ".concat(String.valueOf(lStart)).concat("-")
					.concat(new Long(lEnd - 1).toString()).concat("/").concat(String.valueOf(lEnd)));
		}
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/".concat(strFileExtName));
		String strTempFileName = null;
		try {
			strTempFileName = URLEncoder.encode(strFileName, "utf-8");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		response.addHeader("Content-Disposition",
				"attachment;filename=".concat(strTempFileName).concat(".").concat(strFileType));

		int iBlockSize = 512;
		if (Common.GetConfig("BlockSize") != null) {
			iBlockSize = Integer.parseInt(Common.GetConfig("BlockSize"));
		}
		byte[] bData = new byte[iBlockSize];
		OutputStream out = null;
		try {
			long lLenData = lEnd - lStart + 1;
			int iRead = 0;
			out = response.getOutputStream();
			Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 2, "start read from hfms and write to client", 1);
			while (lLenData > 0) {
				Thread.sleep(200);
				iRead = fileHandler.read(bData, lStart);
				lStart = lStart + iRead;
				lLenData = lLenData - iRead;
				out.write(bData, 0, iRead);
				out.flush();
			}
		} catch (Exception e) {
			// Logger.WriteException(e);//并发大了，或者说执行间隔多了，会出现很多io或其他问题，所以注掉
			// return
			// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DOWNLOADFILE.code)).concat("\"}");
		} finally {
			try {
				out.close();
				bData = null;
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}

		if (isAddInfo && "epub".equals(strFileType)) {
			DownloadInfoBean downloadInfo = new DownloadInfoBean();
			downloadInfo.setFileID(strFileID);
			downloadInfo.setTypeID(strTypeID);
			downloadInfo.setFileName(strFileName);
			downloadInfo.setFileType(strFileType);
			downloadInfo.setUserName(UserName);
			DownloadMngr.addDownloadInfo(downloadInfo);
			BehaviourMngr.addDownloadInfo(appId, UserName, strFileName, strFileType, strTypeID, strFileID);
		}
		return null;
	}

	private String getDocInfo(String FileName, String FileMd5) {
		Document doc = null;
		Element root = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Logger.WriteException(e);
		}
		doc = builder.newDocument();
		doc.setXmlStandalone(true);
		root = doc.createElement("root");
		doc.appendChild(root);

		Element docInfo = doc.createElement("docInfo");
		Element title = doc.createElement("title");
		title.setTextContent(FileName);
		docInfo.appendChild(title);
		Element author = doc.createElement("author");
		docInfo.appendChild(author);
		Element publisher = doc.createElement("publisher");
		docInfo.appendChild(publisher);
		Element filemd5 = doc.createElement("filemd5"); // 此时filemd5未赋值
		filemd5.setTextContent(FileMd5);
		docInfo.appendChild(filemd5);
		root.appendChild(docInfo);
		Element display = doc.createElement("display");
		Element locate = doc.createElement("locate");
		locate.setTextContent("1");
		display.appendChild(locate);
		Element pageMode = doc.createElement("pageMode");
		pageMode.setTextContent("0");
		display.appendChild(pageMode);
		Element IsZoomIn = doc.createElement("IsZoomIn");
		IsZoomIn.setTextContent("1");
		display.appendChild(IsZoomIn);
		root.appendChild(display);

		return prettyPrint(doc, "utf-8");
	}

	private static String prettyPrint(Document xml, String strEncoding) {

		Transformer tf = null;
		try {
			tf = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			Logger.WriteException(e);
		}
		tf.setOutputProperty(OutputKeys.ENCODING, strEncoding);
		tf.setOutputProperty(OutputKeys.INDENT, "no");
		Writer out = new StringWriter();
		try {
			tf.transform(new DOMSource(xml), new StreamResult(out));
		} catch (TransformerException e) {
			Logger.WriteException(e);
		}
		return out.toString();
	}

	private void closeFileHandler(String strFileID) {
		Object[] objs = mFileInStream.get(strFileID);
		if (objs == null) {
			return;
		}
		HFSInputStream fileHandler = (HFSInputStream) objs[0];
		long iRet = (long) objs[1];
		mFileInStream.remove(strFileID);
		try {
			fileHandler.close();
			fc.CloseFile(iRet);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}

	public static String getEpubStatus(JSONArray JsonArray) throws UnsupportedEncodingException {
		if (JsonArray.size() == 0) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		Map<String, Map<String, Object>> mapFileInfo = new HashMap<String, Map<String, Object>>();
		String[] arrParam = new String[JsonArray.size()];
		Map<String, String> mapParam = new IdentityHashMap<String, String>();
		JSONObject recordObj = null;
		for (int i = 0; i < JsonArray.size(); i++) {
			recordObj = JSONObject.fromObject(JsonArray.get(i));
			if (Common.IsNullOrEmpty((String) recordObj.get("typeid"))
					|| Common.IsNullOrEmpty((String) recordObj.get("fileid"))) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
			}
			arrParam[i] = String.valueOf(recordObj.get("fileid"));
			mapParam.put(String.valueOf(recordObj.get("typeid")), String.valueOf(recordObj.get("fileid")));
		}
		List<Map<String, Object>> lstEpubInfo = ODataMngr.getFileEpubInfo(mapParam);
		if (lstEpubInfo == null) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		for (Map<String, Object> mapepub : lstEpubInfo) {
			mapFileInfo.put(String.valueOf(mapepub.get("fileid")), mapepub);
		}

		JSONArray jsonArray = new JSONArray();
		String strDownServer = Common.GetConfig("DownServer");
		String strUrl = "";
		for (int i = 0; i < arrParam.length; i++) {
			JSONObject epubObj = new JSONObject();
			if (mapFileInfo.get(arrParam[i]) == null) {
				epubObj.put("status", -1);
			} else {
				if ("1".equals(String.valueOf(mapFileInfo.get(arrParam[i]).get("ishasepub")))) {
					epubObj.put("status", 1);
					strUrl = strDownServer.concat("/download/getfile?op=download&fileid=")
							.concat(String.valueOf(mapFileInfo.get(arrParam[i]).get("fileid"))).concat("&typeid=")
							.concat(String.valueOf(mapFileInfo.get(arrParam[i]).get("typeid")))
							.concat("&filename=").concat(URLEncoder
									.encode(String.valueOf(mapFileInfo.get(arrParam[i]).get("filename")), "utf-8"))
							.concat("&filetype=epub");
					epubObj.put("epuburl", strUrl);
				} else {
					epubObj.put("status", 0);
				}
			}
			jsonArray.add(epubObj);
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	public static String isExistsEpub(Map<String, Object> arg, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String physicalTable = "";
		String fileId = (String) arg.get("fileid");
		String title = (String) arg.get("title");
		String strTypeID = arg.containsKey("typeid") ? (String) arg.get("typeid") : "";
		String strNewTypeID = arg.containsKey("newtypeid") ? (String) arg.get("newtypeid") : "";
		// String TypeID =
		if (Common.IsNullOrEmpty(strNewTypeID)) {
			physicalTable = (String) arg.get("ptablename");// 物理表名
		} else {
			CnkiMngr cnkiMngr = new CnkiMngr();
			cnkiMngr.setFileInfo(strTypeID, strNewTypeID, fileId, null);
			physicalTable = cnkiMngr.getPhysicalTableName();
		}
		if(Common.IsNullOrEmpty(physicalTable)){
			//LoggerFile.appendMethod("/home/dbcodeisnull.txt", "strNewTypeID:"+strNewTypeID+"-->strTypeID:"+strTypeID+"-->physicalTable:"+physicalTable+"-->fileId:"+fileId);
			return "{\"result\":false,\"exists\":" + false + "}";
		}
		String epubUrl = getExistsEpubUrl(fileId, physicalTable, title, true);
		// System.out.println(epubUrl);
		String str = "";
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);// 连接时间
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000);// 数据传输时间
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(epubUrl);
			HttpResponse hr = client.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.abort();
			client.getConnectionManager().shutdown();
		}
		if (str.length() > 0 && str.indexOf("{") > -1) {
			JSONObject result = JSONObject.fromObject(str.toLowerCase().indexOf("null") > -1
					? str.toLowerCase().replaceAll("null", "true") : str.toLowerCase());

			return "{\"result\":true,\"exists\":" + result.get("istrue").toString() + "}";
		} else {
			return "{\"result\":false,\"exists\":" + false + "}";
		}

	}

	/**************************
	 * 2014-09-17新修改
	 ******************************************/

	/**
	 * 在线阅读xml
	 * 
	 * @param UserName
	 * @param arg
	 * @param request
	 * @param isAddInfo
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private byte[] getFileXML(String UserName, Map<String, Object> arg, HttpServletRequest request, boolean isAddInfo)
			throws UnsupportedEncodingException {
		String strFileID = (String) arg.get("fileid");
		String strTypeID = (String) arg.get("typeid");
		String strToken = (String) arg.get("usertoken");
		String strIsBind = (String) arg.get("isband");
		if (strIsBind == null || strIsBind.length() == 0) {
			strIsBind = "0";
		}

		Map<String, Object> mapFileInfo = ODataMngr.getFileInfo(strTypeID, strFileID);
		String strFileSize = String.valueOf(mapFileInfo.get("filesize"));
		String strTitle = String.valueOf(mapFileInfo.get("filename"));
		String strFileMd5 = String.valueOf(mapFileInfo.get("filemd5"));
		String strFileType = String.valueOf(mapFileInfo.get("typename"));
		String strFileName = strTitle;// .concat(".").concat(String.valueOf(strFileType));
		Document doc = null;
		Element root = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Logger.WriteException(e);
		}
		doc = builder.newDocument();
		doc.setXmlStandalone(true);
		root = doc.createElement("root");
		root.setAttribute("version", "2.0");
		doc.appendChild(root);

		Element document = doc.createElement("document");
		Element docInfo = doc.createElement("docInfo");
		CDATASection cdatadocInfo = null;
		try {
			cdatadocInfo = doc.createCDATASection(Common.base64Encode(getDocInfo(strTitle, strFileMd5)));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		docInfo.appendChild(cdatadocInfo);
		document.appendChild(docInfo);
		Element length = doc.createElement("length");
		length.setTextContent(String.valueOf(strFileSize));
		document.appendChild(length);
		Element filename = doc.createElement("filename");
		filename.setTextContent(strFileID.concat(".").concat(strFileType));
		document.appendChild(filename);
		root.appendChild(document);

		Element server = doc.createElement("server");
		Element cache = doc.createElement("cache");
		Element type = doc.createElement("type");
		type.setAttribute("validPeriod", "longtime");
		type.setTextContent("file");
		cache.appendChild(type);
		Element preparse = doc.createElement("preparse");
		String strPreparse = Common.GetConfig("Preparse");
		if (strPreparse == null || strPreparse == "")
			strPreparse = "1";
		preparse.setTextContent(strPreparse);
		cache.appendChild(preparse);
		server.appendChild(cache);

		Element cluster = doc.createElement("cluster");
		String strThreadsCount = Common.GetConfig("Threads");
		if (strThreadsCount == null) {
			strThreadsCount = "2";
		}
		Element threads = doc.createElement("threads");
		threads.setTextContent(strThreadsCount);
		cluster.appendChild(threads);

		String strDownServer = Common.GetConfig("DownServer");
		String curTime = String.valueOf(System.currentTimeMillis());
		String strUrl = strDownServer.concat("/download/getfile?op=downloads&usertoken=")
				.concat(URLEncoder.encode(strToken, "utf-8")).concat("&fileid=").concat(strFileID).concat("&typeid=")
				.concat(strTypeID).concat("&filename=").concat(URLEncoder.encode(strFileName, "utf-8"))
				.concat("&filetype=").concat(strFileType).concat("&expire=").concat(curTime).concat("&sign=")
				.concat(Common.SHA1(curTime.concat("cajcloud")));
		Element url = doc.createElement("url");
		url.setAttribute("pri", "10");
		CDATASection cdataurl = doc.createCDATASection(strUrl);
		url.appendChild(cdataurl);
		cluster.appendChild(url);

		Element urlbak = doc.createElement("url");
		urlbak.setAttribute("pri", "9");
		CDATASection cdataurlbak = doc.createCDATASection(strUrl);
		urlbak.appendChild(cdataurlbak);
		cluster.appendChild(urlbak);

		server.appendChild(cluster);
		root.appendChild(server);

		return prettyPrint(doc, "utf-8").getBytes("utf-8");
	}

	private String getFileData(Map<String, Object> arg, String UserName, HttpServletRequest request,
			HttpServletResponse response, boolean isAddInfo) {
		// addUploadInfo(arg, UserName, request, response, isAddInfo);
		return getFileFromHfms(arg, UserName, request, response, isAddInfo);
	}

	/**
	 * 
	 * @param arg
	 *            参数
	 * @param UserName
	 *            用户名
	 * @param request
	 * @param bIPLogin
	 *            是否ip登陆
	 * @return 结果
	 */
	/*
	 * private String chkUserAuthority(Map<String, Object> arg, final String
	 * UserName, HttpServletRequest request) { String appId =
	 * String.valueOf(request.getAttribute("app_id"));
	 * 
	 * final String strTypeID = ((String) arg.get("typeid")).trim(); String
	 * strNewTypeID = ((String) arg.get("newtypeid")).trim(); String odatatype =
	 * ((String) arg.get("odatatype")).trim(); final String strFileID =
	 * ((String) arg.get("fileid")).trim(); final String strFileType = ((String)
	 * arg.get("filetype")).trim(); final String strFileName = ((String)
	 * arg.get("filename")).trim(); String strOrgName = arg.get("orgname") ==
	 * null ? null : (String) arg.get("orgname"); String strOrgPwd =
	 * arg.get("orgpwd") == null ? null : (String) arg.get("orgpwd"); String
	 * strIsBind = (String) arg.get("isband"); if (strIsBind == null ||
	 * strIsBind.length() == 0) { strIsBind = "0"; } String strIP = ""; if
	 * (arg.containsKey("ip")) { strIP = ((String) arg.get("ip")).trim(); } if
	 * (Common.IsNullOrEmpty(strIP) || "null".equals(strIP.toLowerCase())) {
	 * strIP = Common.getClientIP(request); } //strIP="59.64.113.205";////
	 * String strIP = Common.getClientIP(request); // strIP =
	 * "121.17.160.181";// boolean bOrgBind = "1".equals(strIsBind) ? true :
	 * false;
	 * 
	 * if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
	 * return
	 * "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr
	 * .ERROR_CODE
	 * .ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String
	 * .valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}"); }
	 * 
	 * if (!AppInfoMngr.isFeeApp(appId)) { String strDownUrl =
	 * orgLibDownloadUrl(appId, UserName, strTypeID, strFileID, strFileName,
	 * strFileType); return
	 * "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}"); }
	 * 
	 * // 判断是否是异常文件 if (CnkiMngr.isErrorFile(strTypeID, strFileID)) { return
	 * "{\"result\":false,\"message\":\""
	 * .concat(String.valueOf(SysConfigMngr.ERROR_CODE
	 * .ERROR_SOURCEFILE.code)).concat
	 * ("\",\"errorcode\":").concat(String.valueOf
	 * (SysConfigMngr.ERROR_CODE.ERROR_SOURCEFILE.code)).concat("}"); }
	 * 
	 * // 判断是否为不允许下载的文件 if (ODataMngr.isSecrecyFile(AppToken.getAppToken(),
	 * strTypeID, strFileID)) { return
	 * "{\"result\":false,\"message\":\"".concat(
	 * String.valueOf(SysConfigMngr.ERROR_CODE
	 * .ERROR_SECRECYFILE.code)).concat("\",\"errorcode\":"
	 * ).concat(String.valueOf
	 * (SysConfigMngr.ERROR_CODE.ERROR_SECRECYFILE.code)).concat("}"); }
	 *//**************************************************/
	/*
	 * // boolean bolCheckAuthority = //
	 * !UploadMngr.isExistUploadInfo(strFileID,UserName); boolean
	 * bolCheckAuthority = !UserInfoMngr.isExistsCnkiFile(UserName, strTypeID,
	 * strFileID); // 判断该用户是否已经下载过该资源,需要走下面的内容； if (bolCheckAuthority) { final
	 * CnkiMngr cnkiMngr = new CnkiMngr(); boolean bLogined = false; int[]
	 * iResult = new int[2]; if (bOrgBind) { if (strOrgName == null ||
	 * strOrgName.length() == 0) { bLogined = cnkiMngr.cnkiUserLogin(strIP); }
	 * else { bLogined = cnkiMngr.cnkiUserLogin(strOrgName, strOrgPwd, strIP,
	 * iResult); } } else { bLogined = cnkiMngr.cnkiUserLogin(UserName, strIP);
	 * } if (!bLogined) { Logger.WriteDownTraceLog(UserName, strTypeID,
	 * strFileID, 7, "chkuserauthority login failed", 0); return
	 * "{\"result\":true,\"passed\":false,\"message\":\" login failure\",\"msgcode\":-1}"
	 * ; } boolean bExistsFileInfo = ODataMngr.existsFileInDB(strTypeID,
	 * strFileID); if (!bExistsFileInfo) { String[] errResult = new String[] {
	 * "" };
	 * 
	 * 启用OData2.0时需要调用的方法if (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID,
	 * strFileID, errResult)) {
	 * 
	 * if (odatatype.equals("1")) { if (!cnkiMngr.setFileInfo(strTypeID,
	 * strFileID)) { // Logger.WriteDownTraceLog(UserName, strTypeID, //
	 * strFileID, 7, // "chkuserauthority get file info from odata error:" + //
	 * errResult[0], 0); return
	 * "{\"result\":false,\"message\":\"".concat(String.
	 * valueOf(SysConfigMngr.ERROR_CODE
	 * .ERROR_FULLFILE.code)).concat("\",\"errorcode\":"
	 * ).concat(String.valueOf(SysConfigMngr
	 * .ERROR_CODE.ERROR_FULLFILE.code)).concat("}"); } } else { if
	 * (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID, strFileID, errResult)) {
	 * // Logger.WriteDownTraceLog(UserName, strTypeID, // strFileID, 7, //
	 * "chkuserauthority get file info from odata error:" + // errResult[0], 0);
	 * return
	 * "{\"result\":false,\"message\":\"".concat(String.valueOf(errResult[
	 * 0])).concat
	 * ("\",\"errorcode\":").concat(String.valueOf(errResult[0])).concat("}"); }
	 * } long[] arrDownRet = new long[1]; boolean bOk =
	 * cnkiMngr.downSourceFile(strTypeID, strFileID, strFileType, UserName,
	 * true, arrDownRet); if (!bOk) { if (arrDownRet[0] == -404) {
	 * CnkiMngr.setCnkiErrorFile(UserName, strTypeID, strFileID, 1); } return
	 * "{\"result\":false,\"message\":\""
	 * .concat(String.valueOf(SysConfigMngr.ERROR_CODE
	 * .ERROR_SOURCEFILE.code)).concat
	 * ("\",\"errorcode\":").concat(String.valueOf
	 * (SysConfigMngr.ERROR_CODE.ERROR_SOURCEFILE.code)).concat("}"); } } else {
	 * cnkiMngr.getFileInfo(strTypeID, strFileID); } // 特殊处理的用户(目前是给苹果商店审核用的)
	 * String strSpUser = Common.GetConfig("SpecialUser"); if
	 * (UserName.toLowerCase().equals(strSpUser)) { Map<String, Object> mapInfo
	 * = SysConfigMngr.getConfigValueAndTime("specialuser"); if (mapInfo != null
	 * && "1".equals(String.valueOf(mapInfo.get("value")))) { String strDownUrl
	 * = getDownLoadUrl(arg); addDownloadRec(arg, appId, UserName, request,
	 * Integer.parseInt(strIsBind)); return
	 * "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}"); } }
	 * String[] arrAuthRet = new String[6]; int iAuthRet =
	 * cnkiMngr.getUserAuthority(strTypeID, strFileID, arrAuthRet); if (iAuthRet
	 * == -1) { if ("-5".equals(arrAuthRet[1])) { return
	 * "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] +
	 * "\",\"msgcode\":" + arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] +
	 * ",\"userticket\":" + arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] +
	 * ",\"pagecount\":" + arrAuthRet[5] + "}"; } else { return
	 * "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] +
	 * "\",\"msgcode\":" + arrAuthRet[1] + "}"; } } else if (iAuthRet == 0) { if
	 * (bOrgBind && iResult[1] == 1) { return
	 * "{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0] +
	 * "\",\"msgcode\":" + arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] +
	 * ",\"userticket\":" + arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] +
	 * ",\"pagecount\":" + arrAuthRet[5] + ",\"feeuser\":\"org\"}"; } else {
	 * return "{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0]
	 * + "\",\"msgcode\":" + arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2]
	 * + ",\"userticket\":" + arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] +
	 * ",\"pagecount\":" + arrAuthRet[5] + "}"; } } } String strDownUrl =
	 * getDownLoadUrl(arg); if (bolCheckAuthority) {
	 * UserInfoMngr.saveUserCnkiFile(UserName, strTypeID, strFileID); }
	 * addDownloadRec(arg, appId, UserName, request,
	 * Integer.parseInt(strIsBind)); return
	 * "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}"); }
	 */

	/**
	 * 
	 * @param arg
	 *            参数
	 * @param UserName
	 *            用户名
	 * @param request
	 * @param bIPLogin
	 *            是否ip登陆
	 * @return 结果
	 */
	private String chkUserAuthority(Map<String, Object> arg, final String UserName, HttpServletRequest request) {
		String appId = String.valueOf(request.getAttribute("app_id"));
		final String strTypeID = ((String) arg.get("typeid")).trim();
		String strNewTypeID = ((String) arg.get("newtypeid")).trim();
		String odatatype = ((String) arg.get("odatatype")).trim();
		// String type = arg.containsKey("t") ? (String) arg.get("t") : "";
		final String strFileID = ((String) arg.get("fileid")).trim();
		final String strFileType = ((String) arg.get("filetype")).trim();
		final String strFileName = ((String) arg.get("filename")).trim();
		final String strCreator = arg.containsKey("creator") ? arg.get("creator").toString().trim() : "";
		String strOrgName = arg.get("orgname") == null ? null : (String) arg.get("orgname");
		String strOrgPwd = arg.get("orgpwd") == null ? null : (String) arg.get("orgpwd");
		String strIsBind = (String) arg.get("isband");
		String organizationName = "";
		if (strIsBind == null || strIsBind.length() == 0) {
			strIsBind = "0";
		}
		String strIP = "";
		if (arg.containsKey("ip")) {
			strIP = ((String) arg.get("ip")).trim();
		}
		if (Common.IsNullOrEmpty(strIP) || "null".equals(strIP.toLowerCase())) {
			strIP = Common.getClientIP(request);
		}
		/*
		 * LoggerFile.appendMethod("D:\\DownLoad", "download log:appid:" + appId
		 * + ";username:" + UserName + ";type" + strTypeID + ";fileid:" +
		 * strFileID + ";IP:" + strIP);
		 */
		// System.out.println("isband:"+strIsBind+"-->orgname:"+strOrgName+"-->orgpwd:"+strOrgPwd+"-->ip:"+strIP);
		// System.out.println("isband:"+strIsBind+"-->orgname:"+strOrgName+"-->orgpwd:"+strOrgPwd+"-->ip:"+strIP+"-->username"+UserName);
		// strIP="59.64.113.205";//strIP="59.64.113.205";//// String strIP =
		// Common.getClientIP(request); // strIP = "121.17.160.181";//
		// strIP = "192.168.26.81";
		// boolean bOrgBind = "1".equals(strIsBind) ? true : false;

		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}

		// 机构馆用户处理
		// if (!AppInfoMngr.isFeeApp(appId)) {
		if (strTypeID.toLowerCase().equals("ynkx_cacb") || strTypeID.toLowerCase().equals("ynkx_pic")
				|| strTypeID.toLowerCase().equals("ynkx_bookinfo") || strTypeID.toLowerCase().equals("ynkx_cacm")
				|| strTypeID.toLowerCase().equals("ynkx_cacv")) {

			String strDownUrl = orgLibDownloadUrl(appId, UserName, strTypeID, strFileID, strFileName, strFileType);
			return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
		}
		// }

		// 判断是否是异常文件，可以暂时不要了
		/*
		 * if (CnkiMngr.isErrorFile(strTypeID, strFileID)) { return
		 * "{\"result\":false,\"message\":\""
		 * .concat(String.valueOf(SysConfigMngr
		 * .ERROR_CODE.ERROR_SOURCEFILE.code)
		 * ).concat("\",\"errorcode\":").concat
		 * (String.valueOf(SysConfigMngr.ERROR_CODE
		 * .ERROR_SOURCEFILE.code)).concat("}"); }
		 */

		// 判断是否为不允许下载的文件
		/*
		 * if (ODataMngr.isSecrecyFile(AppToken.getAppToken(), strTypeID,
		 * strFileID)) { return
		 * "{\"result\":false,\"message\":\"".concat(String.
		 * valueOf(SysConfigMngr
		 * .ERROR_CODE.ERROR_SECRECYFILE.code)).concat("\",\"errorcode\":"
		 * ).concat
		 * (String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_SECRECYFILE.code
		 * )).concat("}"); }
		 */

		/********************************* 活动修改 *****************/
		boolean bolCheckAuthority = true;
		boolean validUser = false;
		if (Common.GetConfig("expand") != null && "1".equals(Common.GetConfig("expand"))) {
			validUser = UserOrgMngr.isValid(UserName);
			if (validUser) {
				int downCount = DownloadMngr.getDownloadFileCount("", "", "", "", "", Common.GetDateTime("yyyy-MM-dd"),
						Common.ConvertToDateTime(Common.GetDateTime("yyyy-MM-dd"), "yyyy-MM-dd", 24 * 3600 * 1000));
				if (downCount > Integer.parseInt(Common.GetConfig("downlimit"))) {
					return "{\"result\":true,\"download\":false,\"message\":\"今日已达到下载上限\",\"errorcode\":"
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
				}
			}
		}
		if (!validUser) {
			bolCheckAuthority = !UserInfoMngr.isExistsCnkiFile(UserName, strTypeID, strFileID); // 判断该用户是否已经下载过该资源,需要走下面的内容；
		}
		// 不存在
		String physicalTable = "";// 物理表名，下载的时候需要物理表名
		if (bolCheckAuthority) {
			String unitname = "";
			String orgname = "";
			String orgpwd = "";
			String longitude = "";
			String latitude = "";

			final CnkiMngr cnkiMngr = new CnkiMngr();
			boolean bLogined = false;
			int[] iResult = new int[2];
			Holder<Integer> errorCode = new Holder<Integer>();
			if ("1".equals(strIsBind) || "2".equals(strIsBind)) {
				// if (strOrgName == null || strOrgName.length() == 0) {
				bLogined = cnkiMngr.cnkiUserLogin(strIP, errorCode);
				// } else {
				if (!bLogined) {
					String[] arrOrgName = strOrgName.split(";");
					String[] arrOrgPwd = strOrgPwd.split(";");
					String[] tmpOrgName = arrOrgName.length > 0 ? arrOrgName[0].split(",") : null;
					String[] tmpOrgPwd = arrOrgPwd.length > 0 ? arrOrgPwd[0].split(",") : null;
					String tmpLongitude = "";
					String tmpLatitude = "";
					if (tmpOrgName != null && tmpOrgName.length > 0) {
						for (int i = 0; i < tmpOrgName.length; i++) {
							if (!Common.IsNullOrEmpty(tmpOrgName[i])) {
								orgname = tmpOrgName[i];
								orgpwd = tmpOrgPwd[i];
								int[] tmpResult = new int[2];
								bLogined = cnkiMngr.cnkiUserLogin(tmpOrgName[i], tmpOrgPwd[i], strIP, tmpResult);
								if (bLogined) {
									if (tmpResult[1] == 0) {
										break;
									} else {
										bLogined = false;
										continue;
									}
								}
							}

						}
					}
					if (!bLogined) {
						if (arrOrgName.length > 1) {
							tmpLongitude = arrOrgName[1];
							tmpLatitude = arrOrgPwd[1];
							if (!Common.IsNullOrEmpty(tmpLongitude)) {
								longitude = tmpLongitude;
								latitude = tmpLatitude;
								orgpwd = "";
								Holder<String> userName = new Holder<String>();
								Holder<String> unitName = new Holder<String>();

								bLogined = cnkiMngr.cnkiLbsUserLogin(strIP, Double.parseDouble(tmpLongitude),
										Double.parseDouble(tmpLatitude), userName, unitName, errorCode);
							}
						}
					}
				}
				// }
				if (bLogined && !Common.IsNullOrEmpty(UserName)) {
					UserStruct us = cnkiMngr.getUserInfo(errorCode);
					unitname = us.getComInfo().getValue().getUnitName().getValue();
					orgname = us.getBaseInfo().getValue().getUserName().getValue();
					int tdays = UserOrgMngr.existUserInfo(UserName, orgname);
					if (tdays == -1) {
						UserOrgMngr.updateUserOrg(UserName, strIP, unitname, orgname, orgpwd, longitude, latitude,
								tdays);
					} else {
						UserOrgMngr.insertUserOrg(UserName, strIP, unitname, orgname, orgpwd, longitude, latitude);
					}
				}
				if (bLogined && MobileRightStatus.getMobileRight()) {
					Holder<Boolean> hmr = new Holder<Boolean>();
					if (!cnkiMngr.haveMobileRight(hmr, errorCode)) {
						return "{\"result\":true,\"passed\":false,\"message\":\" have no MobileRight\",\"msgcode\":-1}";
					}
				}

			}
			/*
			 * else if("2".equals(strIsBind)){ Holder<String> userName = new
			 * Holder<String>(); Holder<String> unitName = new Holder<String>();
			 * Holder<Integer> errorCode = new Holder<Integer>(); bLogined =
			 * cnkiMngr.cnkiLbsUserLogin(strIP, Double.parseDouble(strOrgName),
			 * Double.parseDouble(strOrgPwd), userName, unitName, errorCode);
			 * if(!bLogined){ Logger.WriteDownTraceLog(UserName, strTypeID,
			 * strFileID, 7, "chkuserauthority login failed", 0); return
			 * "{\"result\":true,\"passed\":false,\"message\":\"lbs login failure\",\"msgcode\":"
			 * +errorCode.value+"}"; } }
			 */
			else {
				bLogined = cnkiMngr.cnkiUserLogin(UserName, strIP, errorCode);
			}
			if (!bLogined) {
				Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 7, UserName + "chkuserauthority login failed",
						0);
				return "{\"result\":true,\"passed\":false,\"message\":\" login failure\",\"msgcode\":-1,\"errcode\":"
						+ errorCode.value + "}";
			}
			String[] errResult = new String[] { "-15506" };

			// if(!cnkiMngr.getODataFileInfo(strTypeID, strFileID)){
			if (odatatype.equals("1")) { // odata1
				if (!cnkiMngr.setFileInfo(strTypeID, strFileID)) {
					return "{\"result\":false,\"message\":\""
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
							.concat("\",\"errorcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
							.concat("\",\"errcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code)).concat("}");
				}
			} else {
				if (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID, strFileID, errResult)) {
					return "{\"result\":false,\"message\":\"".concat(String.valueOf(errResult[0]))
							.concat("\",\"errorcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
							.concat("\",\"errcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code)).concat("}");
				}
			}
			// }
			// 临时添加，针对预出版和优先出版 start
			/*
			 * if(cnkiMngr.getLogicTableName().toLowerCase().contains("prep")){
			 * return
			 * "{\"result\":false,\"message\":\"暂时无法下载\",\"errorcode\":-5}"; }
			 */
			// end

			physicalTable = cnkiMngr.getPhysicalTableName();
			// 特殊处理的用户(目前是给苹果商店审核用的)
			String strSpUser = Common.GetConfig("SpecialUser");
			if (UserName.toLowerCase().equals(strSpUser)) {
				Map<String, Object> mapInfo = SysConfigMngr.getConfigValueAndTime("specialuser");
				if (mapInfo != null && "1".equals(String.valueOf(mapInfo.get("value")))) {
					String strDownUrl = "";
					if (strFileType.equalsIgnoreCase("epub"))
						strDownUrl = getExistsEpubUrl(strFileID, physicalTable, strFileName, false);
					else
						strDownUrl = getDownLoadUrl(strFileID, physicalTable, strFileName, strCreator);
					addDownloadRec(arg, appId, UserName, request, Integer.parseInt(strIsBind), organizationName);
					return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
				}
			}
			if (strNewTypeID.equalsIgnoreCase("CDFD") || strNewTypeID.equalsIgnoreCase("CMFD")
					|| FreeDownMngr.getFreeDownCount(UserName) > 500) {
				String[] arrAuthRet = new String[6];
				int iAuthRet = cnkiMngr.getUserAuthority(strNewTypeID, strFileID, arrAuthRet);
				if (iAuthRet == -1) {
					if ("-5".equals(arrAuthRet[1])) {
						return "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
								+ arrAuthRet[1] + ",\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_BALANCE.code
								+ ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
								+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + "}";
					} else {
						return "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
								+ arrAuthRet[1] + ",\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_ACCESSRIGHT.code
								+ "}";
					}
				} else if (iAuthRet == 0) {
					if (("1".equals(strIsBind) || "2".equals(strIsBind)) && iResult[1] == 1) {
						return "{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
								+ arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":"
								+ arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5]
								+ ",\"feeuser\":\"org\"}";
					} else {
						return "{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
								+ arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":"
								+ arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5]
								+ "}";
					}
				}
			} else {
				FreeDownMngr.addFreeDownInfo(UserName, strNewTypeID, strFileID);
			}
		} else {
			// physicalTable = CnkiMngr.getPhysicalTable(strTypeID, strFileID);
			// if (Common.IsNullOrEmpty(physicalTable)) {
			if (odatatype.equals("1")) {
				physicalTable = CnkiMngr.getFirstODataTableName(strTypeID, strFileID);
			} else {
				physicalTable = CnkiMngr.getSecondODataTableName(strTypeID, strNewTypeID, strFileID);
			}
			if (Common.IsNullOrEmpty(physicalTable)) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DBCODE.code)).concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DBCODE.code)).concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DBCODE.code)).concat("}");
			}
			// }
		}
		String strDownUrl = "";
		if (strFileType.equalsIgnoreCase("epub"))
			strDownUrl = getExistsEpubUrl(strFileID, physicalTable, strFileName, false);
		else
			strDownUrl = getDownLoadUrl(strFileID, physicalTable, strFileName, strCreator);
		// System.out.println(strDownUrl);
		if (bolCheckAuthority) {
			UserInfoMngr.saveUserCnkiFile(UserName, strTypeID, strFileID);
		}
		addDownloadRec(arg, appId, UserName, request, Integer.parseInt(strIsBind), organizationName);
		return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
	}

	/**
	 * 扣费
	 * 
	 * @param arg
	 * @param UserName
	 * @param request
	 * @param bIPLogin
	 * @return
	 */
	private String feeForFile(Map<String, Object> arg, String UserName, HttpServletRequest request) {
		String appId = String.valueOf(request.getAttribute("app_id"));
		String strTypeID = ((String) arg.get("typeid")).trim();
		String type = arg.containsKey("filetype") ? (String) arg.get("filetype") : "";
		String strNewTypeID = ((String) arg.get("newtypeid")).trim();
		String odatatype = ((String) arg.get("odatatype")).trim();
		String strFileID = ((String) arg.get("fileid")).trim();
		final String strCreator = arg.containsKey("creator") ? arg.get("creator").toString().trim() : "";
		String strOrgName = arg.get("orgname") == null ? null : (String) arg.get("orgname");
		String strOrgPwd = arg.get("orgpwd") == null ? null : (String) arg.get("orgpwd");
		String strIsBind = (String) arg.get("isband");
		String organizationName = "";
		if (strIsBind == null || strIsBind.length() == 0) {
			strIsBind = "0";
		}
		// boolean bOrgBind = "1".equals(strIsBind) ? true : false;
		String strIP = "";
		if (arg.containsKey("ip")) {
			strIP = ((String) arg.get("ip")).trim();
		}
		if (Common.IsNullOrEmpty(strIP) || "null".equals(strIP.toLowerCase())) {
			strIP = Common.getClientIP(request);
		}
		// String strIP = Common.getClientIP(request); // "208.187.128.27";
		// strIP="59.64.113.205"; //
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String[] arrOrg = new String[] { "", "", "", "" };
		int roamDay = AppInfoMngr.getAppRoam(appId);
		//boolean roam = false;
		int validOrg = 0;  
		if (roamDay > 0) {
			validOrg = UserOrgMngr.getUserOrgInfo(UserName, roamDay, arrOrg);
		}
		if(roamDay>0&&validOrg==0){
			strIsBind = "0";
		}
		CnkiMngr cnkiMngr = new CnkiMngr();
		boolean bLogined = false;
		int[] iResult = new int[2];
		Holder<Integer> errorCode = new Holder<Integer>();
		if ("1".equals(strIsBind) || "2".equals(strIsBind)) {
			if (validOrg == 1) {
				if ("1".equals(arrOrg[0])) {
					Holder<String> userName = new Holder<String>();
					Holder<String> unitName = new Holder<String>();
					bLogined = cnkiMngr.cnkiLbsUserLogin(arrOrg[1], Double.parseDouble(arrOrg[2]),
							Double.parseDouble(arrOrg[3]), userName, unitName, errorCode);
				} else if ("2".equals(arrOrg[0])) {
					bLogined = cnkiMngr.cnkiUserLogin(arrOrg[2], arrOrg[3], arrOrg[1], iResult);
				} else {
					bLogined = cnkiMngr.cnkiUserLogin(arrOrg[1], errorCode);
				}
			} else {
				if (roamDay > 0) { // 打开了开关，但是没有点立即关联。所有通过快报app的用户，如果想使用机构权限，必须关联，也就是所有移动app的都要先关联
					if(validOrg == -1){ //0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，目前只在快报中才会出现1，或者-1，也就是app是允许漫游的
					return "{\"result\":true,\"passed\":false,\"message\":\"机构关联错误\",\"msgcode\":"
							+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + ",\"errcode\":"
							+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + "}";
					}
				}
				if ("1".equals(strIsBind)) {
					if (strOrgName == null || strOrgName.length() == 0) {
						bLogined = cnkiMngr.cnkiUserLogin(strIP, errorCode);
					} else {
						bLogined = cnkiMngr.cnkiUserLogin(strOrgName, strOrgPwd, strIP, iResult);
					}
				} else if ("2".equals(strIsBind)) {
					Holder<String> userName = new Holder<String>();
					Holder<String> unitName = new Holder<String>();
					bLogined = cnkiMngr.cnkiLbsUserLogin(strIP, Double.parseDouble(strOrgName),
							Double.parseDouble(strOrgPwd), userName, unitName, errorCode);
					if (!bLogined) {
						Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 7, "lbs login failed", 0);
						return "{\"result\":true,\"passed\":false,\"message\":\"lbs login failure\",\"msgcode\":"
								+ errorCode.value + ",\"errcode\":" + errorCode.value + "}";
					}
				}
			}
			if (bLogined) {
				UserStruct us = cnkiMngr.getUserInfo(errorCode);
				organizationName = us.getBaseInfo().getValue().getUserName().getValue();
			}
		} else {
			bLogined = cnkiMngr.cnkiUserLogin(UserName, strIP, errorCode);
		}
		if (!bLogined) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(errorCode.value))
					.concat("\",\"errorcode\":").concat(String.valueOf(errorCode.value)).concat(",\"errcode\":")
					.concat(String.valueOf(errorCode.value)).concat("}");
		}
		String[] errResult = new String[] { "-1506" };
		if (odatatype.equals("1")) { // odata1
			if (!cnkiMngr.setFileInfo(strTypeID, strFileID)) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
						.concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code)).concat("}");
			}
		} else {
			if (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID, strFileID, errResult)) {
				return "{\"result\":false,\"message\":\"".concat(String.valueOf(errResult[0]))
						.concat("\",\"errorcode\":").concat(String.valueOf(errResult[0])).concat(",\"errcode\":")
						.concat(String.valueOf(errResult[0])).concat("}");
			}
		}
		/*
		 * if (!cnkiMngr.getODataFileInfo(strTypeID, strFileID)) { return
		 * "{\"result\":false,\"message\":\"".concat(String.valueOf(
		 * SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code)).concat(
		 * "\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.
		 * ERROR_ODATAFILEINFO.code)).concat("}"); }
		 */

		String[] arrAuthRet = new String[2];
		if (!cnkiMngr.getPermision(strNewTypeID, strFileID, arrAuthRet, errorCode)) {
			return "{\"result\":true,\"pay\":0,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":-1,\"errcode\":"
					+ errorCode.value + "}";
		} else {
			UserFeeMngr.setUserFee(UserName, strTypeID, strFileID, Float.parseFloat(arrAuthRet[0]));
		}
		UserInfoMngr.saveUserCnkiFile(UserName, strTypeID, strFileID);
		addDownloadRec(arg, appId, UserName, request, Integer.parseInt(strIsBind), organizationName);
		// String strDownUrl = getDownLoadUrl(arg);
		// String strDownUrl = getDownLoadUrl(strFileID,
		// CnkiMngr.getPhysicalTable(strTypeID, strFileID), strFileID);
		String strDownUrl = "";
		if (type.equalsIgnoreCase("epub"))
			strDownUrl = getExistsEpubUrl(strFileID, cnkiMngr.getPhysicalTableName(), strFileID, false);
		else{
			if(!strNewTypeID.equalsIgnoreCase("CRFD"))
			strDownUrl = getDownLoadUrl(strFileID, cnkiMngr.getPhysicalTableName(), strFileID, strCreator);
			
		}
		return "{\"result\":true,\"pay\":1,\"downurl\":\"".concat(strDownUrl).concat("\"}");
	}

	
	private String scoreForFile(Map<String, Object> arg, String UserName, HttpServletRequest request) {
		String appId = String.valueOf(request.getAttribute("app_id"));
		String strTypeID = ((String) arg.get("typeid")).trim();
		String type = arg.containsKey("filetype") ? (String) arg.get("filetype") : "";
		String strNewTypeID = ((String) arg.get("newtypeid")).trim();
		String odatatype = ((String) arg.get("odatatype")).trim();
		String strFileID = ((String) arg.get("fileid")).trim();
		final String strCreator = arg.containsKey("creator") ? arg.get("creator").toString().trim() : "";
		String strIP = "";
		if (arg.containsKey("ip")) {
			strIP = ((String) arg.get("ip")).trim();
		}
		if (Common.IsNullOrEmpty(strIP) || "null".equals(strIP.toLowerCase())) {
			strIP = Common.getClientIP(request);
		}
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		CnkiMngr cnkiMngr = new CnkiMngr();
		
		Holder<Integer> errorCode = new Holder<Integer>();
		boolean bLogined = cnkiMngr.cnkiUserLogin(UserName, strIP, errorCode);
		if (!bLogined) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(errorCode.value))
					.concat("\",\"errorcode\":").concat(String.valueOf(errorCode.value)).concat(",\"errcode\":")
					.concat(String.valueOf(errorCode.value)).concat("}");
		}
		String[] errResult = new String[] { "-1506" };
		if (odatatype.equals("1")) {
			if (!cnkiMngr.setFileInfo(strTypeID, strFileID)) {
				return "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
						.concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
						.concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code)).concat("}");
			}
		} else {
			if (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID, strFileID, errResult)) {
				return "{\"result\":false,\"message\":\"".concat(String.valueOf(errResult[0]))
						.concat("\",\"errorcode\":").concat(String.valueOf(errResult[0])).concat(",\"errcode\":")
						.concat(String.valueOf(errResult[0])).concat("}");
			}
		}
		String[] arrAuthRet = new String[6];
		int iAuthRet = cnkiMngr.getUserAuthority(strNewTypeID, strFileID, arrAuthRet);
		boolean bSuccess = false;
		String price = "";
		if(iAuthRet == 0){
			price = arrAuthRet[4];
		}else if(iAuthRet == -1){
			if ("-5".equals(arrAuthRet[1])) { // 余额不够
				price = arrAuthRet[4];
			} else { // 没权限或获取不到余额
				return "{\"result\":false,\"message\":\"".concat(String.valueOf(errResult[0]))
						.concat("\",\"errorcode\":").concat(String.valueOf(errResult[0])).concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSRIGHT.code)).concat("}");
			}
		} else {
			bSuccess = true;
		}
		if(!bSuccess){
			int score = SignMngr.signScore(UserName);
			int pscore = (int)(Float.parseFloat(price)*15);
			if(score>pscore){
				score = score - pscore;
				if(SignMngr.setSignScore(UserName, score)){
					if(!SignMngr.usersignexpense(UserName, pscore, 1)){
						LoggerFile.appendMethod("d:\\usersocre", "用户名："+UserName+",添加积分消费记录失败："+score);
					}
				}else{
					LoggerFile.appendMethod("d:\\usersocre", "用户名："+UserName+",扣除积分失败："+score);
				}
			}else{
				return "{\"result\":false,\"message\":\"".concat(String.valueOf(errResult[0]))
						.concat("\",\"errorcode\":").concat(String.valueOf(errResult[0])).concat(",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSRIGHT.code)).concat("}");
			}
		}
		UserInfoMngr.saveUserCnkiFile(UserName, strTypeID, strFileID);
		addDownloadRec(arg, appId, UserName, request, 0, "");
		String strDownUrl = "";
		if (type.equalsIgnoreCase("epub"))
			strDownUrl = getExistsEpubUrl(strFileID, cnkiMngr.getPhysicalTableName(), strFileID, false);
		else if (type.equalsIgnoreCase("pdf")) {
			try {
				strDownUrl = cnkiMngr.getDownloadUrl(strTypeID, strFileID, UserName)
						.replace("t=cajdown", "t=pdfdown").replaceAll("dbcode=CCNDTEMP", "dbcode=CCNDTOTAL").replaceAll("db=CCNDTEMP", "db=CCNDTOTAL");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else{
			if(!strNewTypeID.equalsIgnoreCase("CRFD"))
			strDownUrl = getDownLoadUrl(strFileID, cnkiMngr.getPhysicalTableName(), strFileID, strCreator);
			
		}
		return "{\"result\":true,\"pay\":1,\"downurl\":\"".concat(strDownUrl).concat("\"}");
	}
	/**
	 * 获取下载地址
	 * 
	 * @param arg
	 * @return
	 */
	private String getDownLoadUrl(Map<String, Object> arg) {
		String strToken = ((String) arg.get("usertoken")).trim();
		String strTypeID = ((String) arg.get("typeid")).trim();
		String strFileID = ((String) arg.get("fileid")).trim();
		String strFileName = ((String) arg.get("filename")).trim();
		String strFileType = ((String) arg.get("filetype")).trim();

		String strDownServer = Common.GetConfig("DownServer");
		String curTime = String.valueOf(System.currentTimeMillis());
		String strUrl = null;
		try {
			strUrl = strDownServer.concat("/download/getfile?op=download&usertoken=")
					.concat(URLEncoder.encode(strToken, "utf-8")).concat("&fileid=").concat(strFileID)
					.concat("&typeid=").concat(strTypeID).concat("&filename=")
					.concat(URLEncoder.encode(strFileName, "utf-8")).concat("&filetype=").concat(strFileType)
					.concat("&expire=").concat(curTime).concat("&sign=")
					.concat(Common.SHA1(curTime.concat("cajcloud")));
		} catch (UnsupportedEncodingException e) {
			Logger.WriteException(e);
			e.printStackTrace();
		}
		return strUrl;
	}

	private void addDownloadRec(Map<String, Object> arg, String appID, String UserName, HttpServletRequest request,
			int isOrg, String orgName) {
		DownloadInfoBean downloadInfo = new DownloadInfoBean();
		downloadInfo.setUserName(UserName);
		downloadInfo.setAppID(appID);
		downloadInfo.setTypeID((String) arg.get("typeid"));
		downloadInfo.setFileID((String) arg.get("fileid"));
		downloadInfo.setFileName((String) arg.get("filename"));
		downloadInfo.setFileType((String) arg.get("filetype"));
		downloadInfo.setIsorg(isOrg);
		// if (isOrg == 1) {
		downloadInfo.setOrgName(orgName);
		// }
		DownloadMngr.addTodayDown(downloadInfo);
		DownloadMngr.addDownloadInfo(downloadInfo);
		BehaviourMngr.addDownloadInfo(appID, UserName, downloadInfo.getFileName(), downloadInfo.getFileType(),
				downloadInfo.getTypeID(), downloadInfo.getFileID());
	}

	private String getSourceFile(Map<String, Object> arg, String UserName, HttpServletRequest request,
			HttpServletResponse response, boolean isAddInfo) {
		String strFileName = (String) arg.get("filename");
		String strFileID = (String) arg.get("fileid");
		String strFileType = (String) arg.get("filetype");
		String strTypeID = (String) arg.get("typeid");
		String strIP = Common.getClientIP(request);

		Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 5, "Direct from the original download, start", 0);
		CnkiMngr cnkiMngr = null;
		try {
			cnkiMngr = new CnkiMngr();
		} catch (Exception e) {
			Logger.WriteException(e);
		}

		// 此处的ip不会出错，该ip来至客户端
		Holder<Integer> errorCode = new Holder<Integer>();
		if (!cnkiMngr.cnkiUserLogin(UserName, strIP, errorCode)) {
			Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 5,
					"Direct from the original download,login failed", 0);
			return "-404";
		}
		if (!cnkiMngr.setFileInfo(strTypeID, strFileID)) {
			Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 5,
					"Direct from the original download,get fileinfo from odata error", 0);
			return "-404";
		}
		String Url = "";
		try {
			Url = cnkiMngr.getDownloadUrl(strTypeID, strFileID, UserName);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		URL url = null;
		HttpURLConnection urlconn = null;
		OutputStream out = null;
		byte[] bRet = null;
		try {
			url = new URL(Url);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setDefaultUseCaches(false);
			urlconn.setDoInput(true);
			urlconn.setDoOutput(true);
			urlconn.connect();
			String strContentLength = urlconn.getHeaderField("Content-Length");
			if (strContentLength == null || strContentLength.length() == 0) {
				Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 5,
						"Direct from the original download,Download Source File Content-Length is zero", 0);
				return "-404";
			}
			long lLength = Long.parseLong(strContentLength);
			if (lLength == 15 || lLength == 51) {
				Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 5,
						"Direct from the original download,Download Source File search error or download url overtime",
						0);
				return "-404";
			}
			int iDataBlock = 10240;
			bRet = new byte[iDataBlock];
			DataInputStream dataInput = new DataInputStream(urlconn.getInputStream());
			int iRead = 0;
			response.addHeader("Content-Range", "bytes ".concat(String.valueOf(0)).concat("-")
					.concat(String.valueOf(strContentLength)).concat("/").concat(String.valueOf(strContentLength)));
			response.addHeader("Content-Length", String.valueOf(strContentLength));
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/octet-stream");

			String strTempFileName = null;
			try {
				strTempFileName = URLEncoder.encode(strFileName, "utf-8");
			} catch (Exception e) {
				Logger.WriteException(e);
			}
			response.addHeader("Content-Disposition",
					"attachment;filename=".concat(strTempFileName).concat(".").concat(strFileType));
			out = response.getOutputStream();
			while ((iRead = dataInput.read(bRet)) > 0) {
				if (lLength < 3072) {
					byte[] bCheck = Arrays.copyOf(bRet, 6);
					if ("<html>".equals(new String(bCheck))) {
						Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 5,
								"Direct from the original download,source file is error page,contains html,for example:no download",
								0);
						return "-404";
					}
				}
				out.write(bRet, 0, iRead);
				out.flush();
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		} finally {
			try {
				out.close();
				urlconn.disconnect();
				bRet = null;
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param appID
	 * @param userName
	 * @param odataType
	 * @param fileID
	 * @param fileName
	 * @param fileFormat
	 * @return
	 */
	private String orgLibDownloadUrl(String appID, String userName, String odataType, String fileID, String fileName,
			String fileFormat) {
		String timeStamp = String.valueOf(System.currentTimeMillis());
		String txt = "appid=" + appID + "&type=" + odataType + "&fileid=" + fileID + "&timestamp=" + timeStamp;
		String sign = Common.SHA1(txt).toLowerCase();

		DownloadInfoBean downloadInfo = new DownloadInfoBean();
		downloadInfo.setUserName(userName);
		downloadInfo.setTypeID(odataType);
		downloadInfo.setFileID(fileID);
		downloadInfo.setFileName(fileName);
		downloadInfo.setAppID(appID);
		downloadInfo.setFileType(fileFormat);
		DownloadMngr.addDownloadInfo(downloadInfo);
		// return
		// "http://localhost:16050/filedown.ashx?rt=pdf&type=cacb&fileid=B0400060";Common.GetConfig("orgliburl")
		return Common.GetConfig("orgliburl") + "?timestamp=" + timeStamp + "&type=" + odataType + "&appid=" + appID
				+ "&fileid=" + fileID + "&sign=" + sign + "&rt=" + fileFormat;
	}

	/**
	 * 
	 * @param fn
	 * @param dbcode
	 * @param title
	 * @return
	 */
	private String getDownLoadUrl(String fn, String dbcode, String title, String creator) {
		// downUrl =
		// "http://caj.unicom.d.cnki.net/dms/view.aspx?act=local&t=&title=C%E8%AF%AD%E8%A8%80&pages=&fn=JSJX201501001&dbcode=CJFD2015&v="+KCMSKey.EnCode("JSJX201501001");
		String downLoadUrl = "";// 通过光盘号为CAPJ_PUB或者物理表名前四位是不是capj来判断文献是不是优先出版
		if (dbcode.toLowerCase().substring(0, 4).equals("capj")) {
			// if(dbcode.toLowerCase().contains("capjlast")||dbcode.toLowerCase().contains("capj_pub")||dbcode.toLowerCase().contains("capjday")){
			downLoadUrl = Common.GetConfig("capjDownLoadUrl");
		} else {
			downLoadUrl = Common.GetConfig("downLoadUrl");
		}
		String downUrl = null;
		try {
			downUrl = downLoadUrl + "?act=local&t=&title=" + URLEncoder.encode(title, "utf-8") + "&pages=&fn=" + fn
					+ "&dbcode=" + dbcode + "&v=" + KCMSKey.EnCode(fn);
			if (!Common.IsNullOrEmpty(creator)) {
				downUrl = downUrl + "&creator=" + URLEncoder.encode(creator, "utf-8");
			}
			downUrl = downUrl.replaceAll("\\+", "%20");
			// System.out.println("url="+downUrl);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return downUrl.replaceAll("dbcode=CPFDTEMP", "dbcode=CPFDTOTAL").replaceAll("dbcode=CCNDTEMP",
				"dbcode=CCNDTOTAL");
	}

	/**
	 * 同上面方法getDownLoadUrl，
	 * 需要先判断epub是否存在,用的还是这个地址，只不过参数是t=epub和act=exlocal，2如果存在，就可以在线预览，或者说下载，地址不变，
	 * 参数t=epub,act=local
	 * 
	 * @param fn
	 * @param dbcode
	 * @param title
	 * @param flag
	 *            标志位，如果为true获取的为判断epub是否存在的url，如果为false获取的为epub在线浏览的地址
	 * @return
	 */
	private static String getExistsEpubUrl(String fn, String dbcode, String title, boolean flag) {
		String downLoadUrl = "";// 通过光盘号为CAPJ_PUB或者物理表名前四位是不是capj来判断文献是不是优先出版
		String downUrl = null;
		/*
		 * if (dbcode.toLowerCase().substring(0, 4).equals("capj")) {
		 * downLoadUrl = Common.GetConfig("capjDownLoadUrl"); } else {
		 * downLoadUrl = Common.GetConfig("downLoadUrl"); }
		 */
		if (dbcode.toLowerCase().substring(0, 4).equals("capj")) {
			try {
				if (flag) {
					downLoadUrl = Common.GetConfig("incapjDownLoadUrl");
					downUrl = downLoadUrl + "?act=exlocal&t=epub&title=" + URLEncoder.encode(title, "utf-8")
							+ "&pages=&fn=" + fn + "&dbcode=" + dbcode + "&v=" + KCMSKey.EnCode(fn);
				} else {
					downLoadUrl = Common.GetConfig("capjDownLoadUrl");
					downUrl = downLoadUrl + "?act=local&t=epub&title=" + URLEncoder.encode(title, "utf-8")
							+ "&pages=&fn=" + fn + "&dbcode=" + dbcode + "&v=" + KCMSKey.EnCode(fn);
				}
				downUrl = downUrl.replaceAll("\\+", "%20");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			downLoadUrl = Common.GetConfig("inepubDownUrl");
			try {
				if (flag) {
					downLoadUrl = Common.GetConfig("inepubDownUrl");
					downUrl = downLoadUrl + "?act=exlocal&t=epub&title=" + URLEncoder.encode(title, "utf-8")
							+ "&pages=&fn=" + fn + "&dbcode=" + dbcode + "&v=" + KCMSKey.EnCode(fn);
				} else {
					downLoadUrl = Common.GetConfig("epubDownUrl");
					downUrl = downLoadUrl + "?act=local&t=epub&title=" + URLEncoder.encode(title, "utf-8")
							+ "&pages=&fn=" + fn + "&dbcode=" + dbcode + "&v=" + KCMSKey.EnCode(fn);
				}
				downUrl = downUrl.replaceAll("\\+", "%20");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return downUrl;
	}

	/**
	 * 
	 * @param arg
	 *            参数
	 * @param UserName
	 *            用户名
	 * @param request
	 * @param bIPLogin
	 *            是否ip登陆
	 * @return 结果
	 */
	private String chkUserAuthorityNew(Map<String, Object> arg, final String UserName, HttpServletRequest request) {
		// 获取参数
		String appId = String.valueOf(request.getAttribute("app_id"));
		final String strTypeID = ((String) arg.get("typeid")).trim();
		String strNewTypeID = ((String) arg.get("newtypeid")).trim();
		String odatatype = ((String) arg.get("odatatype")).trim();
		final String strFileID = ((String) arg.get("fileid")).trim();
		final String strFileType = arg.containsKey("filetype") ? ((String) arg.get("filetype")).trim() : "";
		final String strFileName = ((String) arg.get("filename")).trim();
		final String strCreator = arg.containsKey("creator") ? arg.get("creator").toString().trim() : "";
		String strOrgName = arg.containsKey("orgname") ? arg.get("orgname") == null ? null : (String) arg.get("orgname")
				: "";
		String strOrgPwd = arg.containsKey("orgpwd") ? arg.get("orgpwd") == null ? null : (String) arg.get("orgpwd")
				: "";
		String strIsBind = (String) arg.get("isband");
		String organizationName = "";
		if (strIsBind == null || strIsBind.length() == 0) {
			strIsBind = "0";
		}

		String strIP = "";
		if (arg.containsKey("ip")) {
			strIP = ((String) arg.get("ip")).trim();
		}
		if (Common.IsNullOrEmpty(strIP) || "null".equals(strIP.toLowerCase())) {
			strIP = Common.getClientIP(request);
		}
		// strIP="59.64.113.205";
		if (Common.IsNullOrEmpty(strTypeID) || Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}

		// 特殊对象
		if (strTypeID.toLowerCase().equals("ynkx_cacb") || strTypeID.toLowerCase().equals("ynkx_pic")
				|| strTypeID.toLowerCase().equals("ynkx_bookinfo") || strTypeID.toLowerCase().equals("ynkx_cacm")
				|| strTypeID.toLowerCase().equals("ynkx_cacv")) {
			String strDownUrl = orgLibDownloadUrl(appId, UserName, strTypeID, strFileID, strFileName, strFileType);
			return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
		}
		// 活动修改
		boolean bolCheckAuthority = true;
		boolean validUser = false;
		if (Common.GetConfig("expand") != null && "1".equals(Common.GetConfig("expand"))) {
			validUser = UserOrgMngr.isValid(UserName);
			if (validUser) {
				int downCount = DownloadMngr.getDownloadFileCount("", "", "", "", "", Common.GetDateTime("yyyy-MM-dd"),
						Common.ConvertToDateTime(Common.GetDateTime("yyyy-MM-dd"), "yyyy-MM-dd", 24 * 3600 * 1000));
				if (downCount > Integer.parseInt(Common.GetConfig("downlimit"))) {
					return "{\"result\":true,\"download\":false,\"message\":\"今日已达到下载上限\",\"errorcode\":"
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code))
							.concat("\"errcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
				}
			}
		}
		if (!validUser) {
			bolCheckAuthority = !UserInfoMngr.isExistsCnkiFile(UserName, strTypeID, strFileID); // 判断该用户是否已经下载过该资源,需要走下面的内容；
		}
		String todaydown = Common.GetConfig("todaydown");
		int tdowncount =Integer.parseInt(todaydown);
		if(DownloadMngr.getTodayDownloadCount(UserName) > tdowncount){
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.Max_DownLoad.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.Max_DownLoad.code)).concat(",\"errcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.Max_DownLoad.code)).concat("}");
		}

		// boolean bSummers = UserOrgMngr.existInSummerUser(UserName);
		// if (bSummers) {
		// if (DownloadMngr.getTodayDownloadCount(UserName) > 19) {
		// return "{\"result\":false,\"message\":\""
		// .concat(String.valueOf(SysConfigMngr.ERROR_CODE.Max_DownLoad.code)).concat("\",\"errorcode\":")
		// .concat(String.valueOf(SysConfigMngr.ERROR_CODE.Max_DownLoad.code)).concat(",\"errcode\":")
		// .concat(String.valueOf(SysConfigMngr.ERROR_CODE.Max_DownLoad.code)).concat("}");
		// }
		// bolCheckAuthority = false;
		// }
		// 用户没有下载过需要验证权限
		String physicalTable = ""; // 物理表名，下载的时候需要物理表名
		if (bolCheckAuthority) {
			String[] arrOrg = new String[] { "", "", "", "" };
			int roamDay = AppInfoMngr.getAppRoam(appId);
			int validOrg = 0;  //0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，
			if (roamDay > 0) {
				validOrg = UserOrgMngr.getUserOrgInfo(UserName, roamDay, arrOrg);
			}
			if(roamDay>0&&validOrg==0){
				strIsBind = "0";
			}
			if ("1".equals(strIsBind) || "2".equals(strIsBind)) {
				List<Map<String, String>> orgList = null;
				if (validOrg == 1) {
					/*
					 * if("lwztest".equals(UserName)||"cofan7608".equals(
					 * UserName)){ LoggerFile.appendMethod("d:\\orginfo",
					 * UserName+"-->leiixng: " + arrOrg[0] + ";ip:" + arrOrg[1]
					 * +"-->"+ ";username:" +arrOrg[2]+";pwd:"+arrOrg[3]); }
					 */
					orgList = initOrgList_New(arrOrg);
					strIP = arrOrg[1];
				} else {
					if (roamDay > 0) { // 打开了开关，但是没有点立即关联。所有通过快报app的用户，如果想使用机构权限，必须关联，也就是所有移动app的都要先关联
						if(validOrg == -1){ //0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，目前只在快报中才会出现1，或者-1，也就是app是允许漫游的
						return "{\"result\":true,\"passed\":false,\"message\":\"机构关联错误\",\"msgcode\":"
								+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + ",\"errcode\":"
								+ SysConfigMngr.ERROR_CODE.ERROR_BINDINFO.code + "}";
						}
					} else {
						orgList = initOrgList(strIP, strOrgName, strOrgPwd);
					}
				}
				String[] result = new String[orgList.size()];
				boolean bSuccess = false;
				String returnRsult = "";
				for (int i = 0; i < orgList.size(); i++) {
					CnkiMngr cnkiMngr = new CnkiMngr();
					boolean bLogined = false;
					String longitude = "";
					String latitude = "";
					String orgname = "";
					String orgpwd = "";
					int[] iResult = new int[2];
					Holder<Integer> errorCode = new Holder<Integer>();
					if (orgList.get(i).get("type").equals("ip")) {
						bLogined = cnkiMngr.cnkiUserLogin(orgList.get(i).get("ip"), errorCode);
					} else if (orgList.get(i).get("type").equals("orgname")) {
						orgname = orgList.get(i).get("orgname");
						orgpwd = orgList.get(i).get("orgpwd");
						bLogined = cnkiMngr.cnkiUserLogin(orgname, orgpwd, orgList.get(i).get("ip"), iResult);
						if (bLogined) {
							if (iResult[1] == 1)// 1为个人账号，不允许绑定下载
								bLogined = false;
						}
					} else if (orgList.get(i).get("type").equals("lbs")) {
						Holder<String> userName = new Holder<String>();
						Holder<String> unitName = new Holder<String>();
						longitude = orgList.get(i).get("longitude");
						latitude = orgList.get(i).get("latitude");
						bLogined = cnkiMngr.cnkiLbsUserLogin(orgList.get(i).get("ip"), Double.parseDouble(longitude),
								Double.parseDouble(latitude), userName, unitName, errorCode);
					}
					if (!bLogined) {
						returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"下载时机构账号登录失败!\",\"msgcode\":-1,\"errcode\":"
								+ SysConfigMngr.ERROR_CODE.ERROR_ORGLOGIN.code + "}";
						result[i] = "login failed";
						continue;
					}

					Holder<Integer> errorCodea = new Holder<Integer>();
					UserStruct us = cnkiMngr.getUserInfo(errorCodea);
					organizationName = us.getBaseInfo().getValue().getUserName().getValue();
					/*
					 * if (bLogined && !Common.IsNullOrEmpty(UserName)) { //
					 * 这里应该加上除了漫游的账号 Holder<Integer> errorCode = new
					 * Holder<Integer>(); UserStruct us =
					 * cnkiMngr.getUserInfo(errorCode); String unitname =
					 * us.getComInfo().getValue().getUnitName().getValue();
					 * orgname =
					 * us.getBaseInfo().getValue().getUserName().getValue(); if
					 * (!UserOrgMngr.existUserInfo(UserName, orgname)) {
					 * UserOrgMngr.insertUserOrg(UserName, strIP, unitname,
					 * orgname, orgpwd, longitude, latitude); } else {
					 * UserOrgMngr.updateUserOrg(UserName, strIP, unitname,
					 * orgname, orgpwd, longitude, latitude); } }
					 */
					// 判断权限-->判断下载权限
					if (bLogined && MobileRightStatus.getMobileRight()) {
						Holder<Boolean> hmr = new Holder<Boolean>();
						if (!cnkiMngr.haveMobileRight(hmr, errorCode)) {
							returnRsult = "{\"result\":true,\"passed\":false,\"message\":\" have no MobileRight\",\"msgcode\":-1,\"errcode\":"
									+ errorCode.value + "}";
						}
					}
					String[] errResult = new String[] { "-15506" };
					if (odatatype.equals("1")) { // odata1
						if (!cnkiMngr.setFileInfo(strTypeID, strFileID)) {
							return "{\"result\":false,\"message\":\""
									.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FULLFILE.code))
									.concat("\",\"errorcode\":")
									.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FULLFILE.code))
									.concat(",\"errcode\":")
									.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
									.concat("}");
						}
					} else {
						if (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID, strFileID, errResult)) {
							return "{\"result\":false,\"message\":\"".concat(String.valueOf(errResult[0]))
									.concat("\",\"errorcode\":").concat(String.valueOf(errResult[0]))
									.concat(",\"errcode\":").concat(String.valueOf(errResult[0])).concat("}");
						}
					}
					physicalTable = cnkiMngr.getPhysicalTableName();
					int score = SignMngr.signScore(UserName);
					if (!AppInfoMngr.isActivityApp(appId)) {
						String[] arrAuthRet = new String[6];
						int iAuthRet = cnkiMngr.getUserAuthority(strNewTypeID, strFileID, arrAuthRet);
						if (iAuthRet == -1) {
							if ("-5".equals(arrAuthRet[1])) { // 余额不够
								returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"余额不足.\""
										+ ",\"msgcode\":-7,\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_BALANCE.code
										+ ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
										+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + ",\"allscore\":" + score + ",\"curscore\":" + (int)(Float.parseFloat(arrAuthRet[4])*15) + "}";
							} else { // 没权限或获取不到余额
								returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0]
										+ "\",\"msgcode\":-5,\"errcode\":"
										+ SysConfigMngr.ERROR_CODE.ERROR_ACCESSRIGHT.code + "}";
							}
						} else if (iAuthRet == 0) {
							returnRsult = "{\"result\":true,\"passed\":true,\"message\":\"机构账户余额.\"" + ",\"msgcode\":"
									+ arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":"
									+ arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5]
									+ ",\"allscore\":" + score + ",\"curscore\":" + (int)(Float.parseFloat(arrAuthRet[4])*15) + ",\"feeuser\":\"org\"}";
						} else {
							bSuccess = true;
							break;
						}
					} else {
						if (strNewTypeID.equalsIgnoreCase("CDFD") || strNewTypeID.equalsIgnoreCase("CMFD")
								|| FreeDownMngr.getFreeDownCount(UserName) > 200) {
							String[] arrAuthRet = new String[6];
							int iAuthRet = cnkiMngr.getUserAuthority(strNewTypeID, strFileID, arrAuthRet);
							if (iAuthRet == -1) {
								if ("-5".equals(arrAuthRet[1])) { // 余额不够
									returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"机构账户余额不足.\""
											+ ",\"msgcode\":-7,\"errcode\":"
											+ SysConfigMngr.ERROR_CODE.ERROR_BALANCE.code + ",\"userbalance\":"
											+ arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3] + ",\"price\":"
											+ arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + ",\"allscore\":" + score + ",\"curscore\":" + (int)(Float.parseFloat(arrAuthRet[4])*15) + "}";
								} else { // 没权限或获取不到余额
									returnRsult = "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0]
											+ "\",\"msgcode\":-5,\"errcode\":"
											+ SysConfigMngr.ERROR_CODE.ERROR_ACCESSRIGHT.code + "}";
								}
							} else if (iAuthRet == 0) {
								returnRsult = "{\"result\":true,\"passed\":true,\"message\":\"机构账户余额为" + arrAuthRet[2]
										+ "\",\"msgcode\":" + arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2]
										+ ",\"userticket\":" + arrAuthRet[3] + ",\"price\":" + arrAuthRet[4]
										+ ",\"pagecount\":" + arrAuthRet[5] + ",\"allscore\":" + score + ",\"curscore\":" + (int)(Float.parseFloat(arrAuthRet[4])*15) + ",\"feeuser\":\"org\"}";
							} else {
								bSuccess = true;
								break;
							}
						} else {
							FreeDownMngr.addFreeDownInfo(UserName, strNewTypeID, strFileID);
							bSuccess = true;
							break;
						}
					}
				}
				if (!bSuccess) {
					return returnRsult;
				}
			} else {
				CnkiMngr cnkiMngr = new CnkiMngr();
				Holder<Integer> errorCode = new Holder<Integer>();
				if (!cnkiMngr.cnkiUserLogin(UserName, strIP, errorCode)) {
					Logger.WriteDownTraceLog(UserName, strTypeID, strFileID, 7,
							UserName + "chkuserauthority login failed", 0);
					return "{\"result\":true,\"passed\":false,\"message\":\"login failure\",\"msgcode\":-1,\"errcode\":"
							+ errorCode.value + "}";
				}
				UserStruct us = cnkiMngr.getUserInfo(errorCode);
				try{
					organizationName = us.getBaseInfo().getValue().getParentName().getValue();
				}catch(Exception e){
					try{
					organizationName = us.getComInfo().getValue().getUnitName().getValue();
					}catch(Exception ex){
						organizationName = "";
					}
				}
				String[] errResult = new String[] { "-15506" };
				if (odatatype.equals("1")) { // odata1
					if (!cnkiMngr.setFileInfo(strTypeID, strFileID)) {
						return "{\"result\":false,\"message\":\""
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FULLFILE.code))
								.concat("\",\"errorcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FULLFILE.code))
								.concat(",\"errcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code)).concat("}");
					}
				} else {
					if (!cnkiMngr.setFileInfo(strTypeID, strNewTypeID, strFileID, errResult)) {
						return "{\"result\":false,\"message\":\""
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
								.concat("\",\"errorcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code))
								.concat(",\"errcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ODATAFILEINFO.code)).concat("}");
					}
				}
				physicalTable = cnkiMngr.getPhysicalTableName();
				// 特殊处理的用户(目前是给苹果商店审核用的)
				String strSpUser = Common.GetConfig("SpecialUser");
				if (UserName.toLowerCase().equals(strSpUser)) {
					Map<String, Object> mapInfo = SysConfigMngr.getConfigValueAndTime("specialuser");
					if (mapInfo != null && "1".equals(String.valueOf(mapInfo.get("value")))) {
						String strDownUrl = "";
						if (strFileType.equalsIgnoreCase("epub"))
							strDownUrl = getExistsEpubUrl(strFileID, physicalTable, strFileName, false);
						else if (strFileType.equalsIgnoreCase("pdf")) {
							try {
								strDownUrl = cnkiMngr.getDownloadUrl(strTypeID, strFileID, UserName)
										.replace("t=cajdown", "t=pdfdown").replaceAll("dbcode=CCNDTEMP", "dbcode=CCNDTOTAL").replaceAll("db=CCNDTEMP", "db=CCNDTOTAL");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else{
							if(!strNewTypeID.equalsIgnoreCase("CRFD"))
							strDownUrl = getDownLoadUrl(strFileID, physicalTable, strFileName, strCreator);
						}
						addDownloadRec(arg, appId, UserName, request, Integer.parseInt(strIsBind), organizationName);
						return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
					}
				}
				int score = SignMngr.signScore(UserName);
				if (!AppInfoMngr.isActivityApp(appId)) {
					String[] arrAuthRet = new String[6];
					int iAuthRet = cnkiMngr.getUserAuthority(strNewTypeID, strFileID, arrAuthRet);
					if (iAuthRet == -1) {
						if ("-5".equals(arrAuthRet[1])) {
							return "{\"result\":true,\"passed\":false,\"message\":\" 余额不足. \"" + ",\"msgcode\":"
									+ arrAuthRet[1] + ",\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_BALANCE.code
									+ ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
									+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + ",\"allscore\":" + score + ",\"curscore\":" + (int)(Float.parseFloat(arrAuthRet[4])*15) + "}";
						} else {
							return "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0]
									+ "\",\"msgcode\":" + arrAuthRet[1] + ",\"errcode\":" + arrAuthRet[1] + "}";
						}
					} else if (iAuthRet == 0) {
						return "{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
								+ arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":"
								+ arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5]
								+ ",\"allscore\":" + score + ",\"curscore\":" + (int)(Float.parseFloat(arrAuthRet[4])*15) + "}";
					}
				} else {
					if (strNewTypeID.equalsIgnoreCase("CDFD") || strNewTypeID.equalsIgnoreCase("CMFD")
							|| FreeDownMngr.getFreeDownCount(UserName) > 200) {
						String[] arrAuthRet = new String[6];
						int iAuthRet = cnkiMngr.getUserAuthority(strNewTypeID, strFileID, arrAuthRet);
						if (iAuthRet == -1) {
							if ("-5".equals(arrAuthRet[1])) {
								return "{\"result\":true,\"passed\":false,\"message\":\" 余额不足.\"" + ",\"msgcode\":"
										+ arrAuthRet[1] + ",\"errcode\":" + SysConfigMngr.ERROR_CODE.ERROR_BALANCE.code
										+ ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3]
										+ ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + ",\"allscore\":" + score + ",\"curscore\":" + (int)(Float.parseFloat(arrAuthRet[4])*15) + "}";
							} else {
								return "{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0]
										+ "\",\"msgcode\":" + arrAuthRet[1] + ",\"errcode\":" + arrAuthRet[1] + "}";
							}
						} else if (iAuthRet == 0) {
							return "{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":"
									+ arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":"
									+ arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5]
									+ ",\"allscore\":" + score + ",\"curscore\":" + (int)(Float.parseFloat(arrAuthRet[4])*15) + "}";
						}
					} else {
						FreeDownMngr.addFreeDownInfo(UserName, strNewTypeID, strFileID);
					}
				}
			}
		} else {
			if(!strNewTypeID.equalsIgnoreCase("CRFD")){
				if (odatatype.equals("1")) {
					physicalTable = CnkiMngr.getFirstODataTableName(strTypeID, strFileID);
				} else {
					physicalTable = CnkiMngr.getSecondODataTableName(strTypeID, strNewTypeID, strFileID);
				}
				if (Common.IsNullOrEmpty(physicalTable)) {
					if (odatatype.equals("1")) {
						physicalTable = CnkiMngr.getSecondODataTableName(strTypeID, strNewTypeID, strFileID);
					}
					if (Common.IsNullOrEmpty(physicalTable)) {
						return "{\"result\":false,\"message\":\""
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DBCODE.code))
								.concat("\",\"errorcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DBCODE.code)).concat(",\"errcode\":")
								.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DBCODE.code)).concat("}");
					}
				}
			}
		}
		String strDownUrl = "";
		if (strFileType.equalsIgnoreCase("epub"))
			strDownUrl = getExistsEpubUrl(strFileID, physicalTable, strFileName, false);
		else if (strFileType.equalsIgnoreCase("pdf")) {
			try {
				strDownUrl = CnkiMngr.getDownloadUrl(strTypeID, strFileID, UserName, physicalTable).replace("t=cajdown",
						"t=pdfdown").replaceAll("dbcode=CCNDTEMP","dbcode=CCNDTOTAL").replaceAll("db=CCNDTEMP", "db=CCNDTOTAL");
				;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if(!strNewTypeID.equalsIgnoreCase("CRFD"))
				strDownUrl = getDownLoadUrl(strFileID, physicalTable, strFileName, strCreator);
		}
		if (bolCheckAuthority) {
			UserInfoMngr.saveUserCnkiFile(UserName, strTypeID, strFileID);
		}
		addDownloadRec(arg, appId, UserName, request, Integer.parseInt(strIsBind), organizationName);
		return "{\"result\":true,\"downurl\":\"".concat(strDownUrl).concat("\"}");
	}

	/**
	 * 初始化机构信息，适用于多个机构的情况
	 * 
	 * @param ip
	 * @param orgName
	 * @param orgPassword
	 * @return
	 */
	private List<Map<String, String>> initOrgList(String ip, String orgName, String orgPassword) {
		List<Map<String, String>> orgList = new ArrayList<Map<String, String>>();
		Map<String, String> orgIpMap = new HashMap<String, String>();
		orgIpMap.put("type", "ip");
		orgIpMap.put("ip", ip);
		orgList.add(orgIpMap);
		String[] arrOrgName = orgName.split(";");
		String[] arrOrgPwd = orgPassword.split(";");
		String[] tmpOrgName = arrOrgName.length > 0 ? arrOrgName[0].split(",") : null;
		String[] tmpOrgPwd = arrOrgPwd.length > 0 ? arrOrgPwd[0].split(",") : null;
		String tmpLongitude = "";
		String tmpLatitude = "";
		if (tmpOrgName != null && tmpOrgName.length > 0) {
			for (int i = 0; i < tmpOrgName.length; i++) {
				if (!Common.IsNullOrEmpty(tmpOrgName[i])) {
					Map<String, String> orgOrgMap = new HashMap<String, String>();
					orgOrgMap.put("type", "orgname");
					orgOrgMap.put("orgname", tmpOrgName[i]);
					orgOrgMap.put("orgpwd", tmpOrgPwd[i]);
					orgOrgMap.put("ip", ip);
					orgList.add(orgOrgMap);
				}
			}
		}
		if (arrOrgName.length > 1) {
			tmpLongitude = arrOrgName[1];
			tmpLatitude = arrOrgPwd[1];
			if (!Common.IsNullOrEmpty(tmpLongitude)) {
				Map<String, String> orgLbsMap = new HashMap<String, String>();
				orgLbsMap.put("type", "lbs");
				orgLbsMap.put("longitude", tmpLongitude);
				orgLbsMap.put("latitude", tmpLatitude);
				orgLbsMap.put("ip", ip);
				orgList.add(orgLbsMap);
			}
		}
		return orgList;
	}

	private List<Map<String, String>> initOrgList_New(String[] orgInfo) {
		List<Map<String, String>> orgList = new ArrayList<Map<String, String>>();
		Map<String, String> orgIpMap = new HashMap<String, String>();
		if ("2".equalsIgnoreCase(orgInfo[0])) {
			orgIpMap.put("type", "orgname");
			orgIpMap.put("ip", orgInfo[1]);
			orgIpMap.put("orgname", orgInfo[2]);
			orgIpMap.put("orgpwd", orgInfo[3]);
			orgList.add(orgIpMap);
		} else if ("1".equalsIgnoreCase(orgInfo[0])) {
			orgIpMap.put("type", "lbs");
			orgIpMap.put("ip", orgInfo[1]);
			orgIpMap.put("longitude", orgInfo[2]);
			orgIpMap.put("latitude", orgInfo[3]);
			orgList.add(orgIpMap);
		} else {
			orgIpMap.put("type", "ip");
			orgIpMap.put("ip", orgInfo[1]);
			orgList.add(orgIpMap);
		}
		return orgList;
	}

	private Map<String, String> initOrgListNew(String[] orgInfo) {
		Map<String, String> orgIpMap = new HashMap<String, String>();
		if ("2".equalsIgnoreCase(orgInfo[0])) {
			orgIpMap.put("type", "orgname");
			orgIpMap.put("ip", orgInfo[1]);
			orgIpMap.put("orgname", orgInfo[2]);
			orgIpMap.put("orgpwd", orgInfo[3]);
		} else if ("1".equalsIgnoreCase(orgInfo[0])) {
			orgIpMap.put("type", "lbs");
			orgIpMap.put("ip", orgInfo[1]);
			orgIpMap.put("longitude", orgInfo[2]);
			orgIpMap.put("latitude", orgInfo[3]);
		} else {
			orgIpMap.put("type", "ip");
			orgIpMap.put("ip", orgInfo[1]);
		}
		return orgIpMap;
	}

	/*
	 * private String[] getFilePrice(CnkiMngr cnkiMngr) { String[] result = new
	 * String[] { "", "" }; }
	 */

	/**
	 * 获取访问权限：如果是个人，并且没有绑定机构，直接返回result=true，org=0,data=[],如果关联了机构
	 * result=true，org=1，data=[1，1，1，] 云文档、暑期活动、机构权限
	 * 
	 * @param arg
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String accessRight(String userName, Map<String, Object> arg, HttpServletRequest request)
			throws UnsupportedEncodingException {
		JSONArray jsonArray = JSONArray.fromObject(arg.get("data"));
		if (Common.IsNullOrEmpty(userName) || jsonArray.size() == 0) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat(",\"errcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String appId = String.valueOf(request.getAttribute("app_id"));
		String strIsBind = (String) arg.get("isbind");
		if (strIsBind == null || strIsBind.length() == 0) {
			strIsBind = "0";
		}

		String strIP = "";
		if (arg.containsKey("ip")) {
			strIP = ((String) arg.get("ip")).trim();
		}
		if (Common.IsNullOrEmpty(strIP) || "null".equals(strIP.toLowerCase())) {
			strIP = Common.getClientIP(null);
		}
		String[] arrOrg = new String[] { "", "", "", "" };
		int roamDay = AppInfoMngr.getAppRoam(appId);
		int validOrg = 0;  //0默认的话认为数据库表中不存在机构信息，兼容云阅读（云阅读都是带着机构信息的）。1为数据库表中有数据，
		if (roamDay > 0) {
			validOrg = UserOrgMngr.getUserOrgInfo(userName, roamDay, arrOrg);
		}
		if(roamDay>0&&validOrg==0){
			strIsBind = "0";
		}
		if (validOrg<1) {
			return "";
		}
		strIP = arrOrg[1];
		Map<String, String> orgMap = initOrgListNew(arrOrg);
		CnkiMngr cnkiMngr = new CnkiMngr();
		boolean bLogined = false;
		String longitude = "";
		String latitude = "";
		String orgname = "";
		String orgpwd = "";
		int[] iResult = new int[2];
		Holder<Integer> errorCode = new Holder<Integer>();
		if (orgMap.get("type").equals("ip")) {
			bLogined = cnkiMngr.cnkiUserLogin(orgMap.get("ip"), errorCode);
		} else if (orgMap.get("type").equals("orgname")) {
			orgname = orgMap.get("orgname");
			orgpwd = orgMap.get("orgpwd");
			bLogined = cnkiMngr.cnkiUserLogin(orgname, orgpwd, orgMap.get("ip"), iResult);
			if (bLogined) {
				if (iResult[1] == 1)// 1为个人账号，不允许绑定下载
					bLogined = false;
			}
		} else if (orgMap.get("type").equals("lbs")) {
			Holder<String> userNames = new Holder<String>();
			Holder<String> unitName = new Holder<String>();
			longitude = orgMap.get("longitude");
			latitude = orgMap.get("latitude");
			bLogined = cnkiMngr.cnkiLbsUserLogin(orgMap.get("ip"), Double.parseDouble(longitude),
					Double.parseDouble(latitude), userNames, unitName, errorCode);
		}
		if (!bLogined) {
			return "{\"result\":true,\"passed\":false,\"message\":\"机构账号登录失败!\",\"msgcode\":-1,\"errcode\":"
					+ errorCode.value + "}";
		}
		Holder<Integer> errorCodea = new Holder<Integer>();
		List<Integer> result = cnkiMngr.chkRightAccess(jsonArray, errorCodea);
		if (result == null) {
			return "{\"result\":false,\"errorcode\":" + errorCodea.value + ",\"errcode\":" + errorCodea.value + "}";
		} else {
			return "{\"result\":true,\"data\":\"" + result.toString() + "\"}";
		}
	}
}
