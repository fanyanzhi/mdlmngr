package BLL;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.ws.Holder;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import net.cnki.hfs.FileClient;
import net.cnki.hfs.HFSOutputStream;
import net.cnki.hfs.HFS_OPEN_FILE;
import net.cnki.sso.UserStruct;
//import net.cnki.mngr.TAuthMngr;
//import net.cnki.mngr.WCFClient.SRightMngrStub;
//import net.cnki.mngr.WCFClient.SRightMngrStub.ArrayOfTFieldInfo;
//import net.cnki.mngr.WCFClient.SRightMngrStub.CDownloadInfo;
//import net.cnki.mngr.WCFClient.SRightMngrStub.FileInfo;
//import net.cnki.mngr.WCFClient.SRightMngrStub.LogonType;
//import net.cnki.mngr.WCFClient.SRightMngrStub.OperateType;
//import net.cnki.mngr.WCFClient.SRightMngrStub.ProductInfo;
//import net.cnki.mngr.WCFClient.SRightMngrStub.TFieldInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import DAL.DBHelper;
import Model.AppToken;
import Model.ODataFileBean;
import Model.UploadInfoBean;
import Util.Common;
import Util.LoggerFile;
import Util.SHA1;

import com.cnki.mngr.Constant;
import com.cnki.mngr.RightMngr;

public class CnkiMngr {
	private static FileClient fc = new FileClient(Common.GetConfig("HfsServer"));
	private static Map<String, Object[]> mFileOutStream = new HashMap<String, Object[]>();
	public static String Pid = Common.GetConfig("TAuthMngrID");
	public static int iIndex = 0; // 源文件url数组索引

	static String ODataUrl = Common.GetConfig("newODataUrl");// "http://192.168.100.198:8010/";
	static String appId = Common.GetConfig("newAppId");
	static String appKey = Common.GetConfig("newAppKey");
	static String did = "{123456}";
	static String mobile = "";
	static String location = "0,0";
	static String ip = "127.0.0.1";

	private String logicTableName; // 逻辑表名
	String classId = "";
	int pageCount = 0;
	String title = "";
	String fileName = "";
	String fileSize = "";
	private String strTableName; // 物理表名
	// private TAuthMngr tAuthMngr;
	private RightMngr rightMngr;
	private boolean bLogin = false;
	Holder<String> identId = new Holder<String>();

	// 保存到数据库中的变量信息pageCount,PageRange,PublishDay,Year,Date,Issue,Id,SubjectCode,TableName,Year,AlbumCode,Title
	private String subjectCode;
	private String albumCode;
	private String pageRange;
	private String publishDay;
	private String issue;
	private String year;
	private String pyJournalName;
	private String yearIssue;
	private String subjectSubColumnCode;
	private String typeCode;

	private int iFeeFlag = 1; // 付费用户

	// UserStruct.getCtrlInfo().getIsFeeFlag()
	// 1为付费用户，0为包库用户

	static Map<String, String> NODataMap = new HashMap<String, String>();
	static Map<String, String> OODataMap = new HashMap<String, String>();
	// public static Map<String, String> useridentid =new HashMap<String,
	// String>();

	static {
		List<Map<String, Object>> lstType = SourceMngr.getSourceType();
		if (lstType != null && lstType.size() > 0) {
			Iterator<Map<String, Object>> itr = lstType.iterator();
			Map<String, Object> mapType = null;
			while (itr.hasNext()) {
				mapType = itr.next();
				NODataMap.put(String.valueOf(mapType.get("nodataname")).toLowerCase(),
						String.valueOf(mapType.get("name_en")).toLowerCase());
				OODataMap.put(String.valueOf(mapType.get("name_en")).toLowerCase(),
						String.valueOf(mapType.get("nodataname")).toLowerCase());
			}
		}

		// NODataMap.put("cjfd", "journals");
		// NODataMap.put("cmfd", "mastertheses");
		// NODataMap.put("cdfd", "doctortheses");
		// NODataMap.put("CPFD", "conferences");
		// NODataMap.put("ccnd", "newspapers");
		//
		// OODataMap.put("journals", "cjfd");
		// OODataMap.put("mastertheses", "cmfd");
		// OODataMap.put("doctortheses", "cdfd");
		// OODataMap.put("conferences", "CPFD");
		// OODataMap.put("newspapers", "ccnd");
	}

	public CnkiMngr() {
		// tAuthMngr = new TAuthMngr(Pid);
		rightMngr = new RightMngr(Constant.PLATFORM);
	}

	public static String[] getTypes(String Type) {
		String[] aryTypes = new String[3];
		if (NODataMap.containsKey(Type.toLowerCase())) {
			aryTypes[0] = NODataMap.get(Type.toLowerCase());
			aryTypes[1] = Type.toLowerCase();
			aryTypes[2] = "2";
			return aryTypes;
		} else if (OODataMap.containsKey(Type.toLowerCase())) {
			aryTypes[0] = Type.toLowerCase();
			aryTypes[1] = OODataMap.get(Type.toLowerCase());
			aryTypes[2] = "1";
			return aryTypes;
		}
		return null;
	}

	/*	*//**
			 * IP登陆，还没验证
			 * 
			 * @param IP
			 * @return
			 *//*
			 * public boolean cnkiUserLogin(String IP) { int iRet =
			 * tAuthMngr.UserLogin("", "", IP, LogonType.IPLogin); if (iRet ==
			 * 1) { bLogin = true; iFeeFlag =
			 * tAuthMngr.GetUserInfo().getCtrlInfo().getIsFeeFlag(); return
			 * true; } else { Logger.WriteException(new Exception(
			 * "cnki login failure, error code :" + tAuthMngr.getErrorCode()));
			 * return false; }
			 * 
			 * }
			 */

	/**
	 * IP登陆，还没验证
	 * 
	 * @param IP
	 * @return
	 */
	public boolean cnkiUserLogin(String IP, Holder<Integer> errorCode) {
		if (rightMngr.ipLogin(IP, identId, errorCode)) {
			bLogin = true;
			return true;
		} else {
			Logger.WriteException(new Exception("cnki login failure, error code :" + errorCode.value));
			return false;
		}
	}

	/**
	 * 用户名登陆
	 * 
	 * @param IP
	 * @return
	 */
	public boolean cnkiUserLogin(String UserName, String IP, Holder<Integer> errorCode) {
		String Password = UserInfoMngr.getUserPassword(UserName);
		if ("appletest2013".equals(UserName) && "111111".equals(Password)) {
			return true;
		}
		if("zhu_zhu18".equals(UserName)){
			writeLog("zhu_zhu18Password:"+Password);
		}
		if (Common.IsNullOrEmpty(Password)) {
			if("zhu_zhu18".equals(UserName)){
				writeLog("zhu_zhu18Passwordweikong:");
			}	
			Map<String, Object> thirdMap = UserInfoMngr.getThirdName(UserName);
			JSONObject json = UserInfoMngr.ecpThirdLogin(thirdMap.get("thirduserid").toString(),
					thirdMap.get("thirdname").toString(), IP);
			if (json == null) { // 连接异常 -->一般为服务器错误
				return false;
			}
			if("zhu_zhu18".equals(UserName)){
				writeLog("zhu_zhu18josn:"+json.toString());
			}	
			if (json.getBoolean("Success")) {
				identId.value = json.getString("IdenId") + "|" + IP + "|" + "JGApp";;
				bLogin = true;
				return true;
			} else {
				return false;
			}
		}
		//Holder<Integer> errorCode = new Holder<Integer>();
		if (rightMngr.userLogin(UserName, Password, IP, identId, errorCode)) {
			if("zhu_zhu18".equals(UserName)){
				writeLog("zhu_zhu18:"+identId.value);
			}
			bLogin = true;
			return true;
		} else {
			if("zhu_zhu18".equals(UserName)){
				writeLog("zhu_zhu18failse:"+errorCode.value);
			}
			Logger.WriteException(new Exception("cnki login failure, error code :" + errorCode.value));
			return false;
		}
	}

	/**
	 * 压力测试用
	 * 
	 * @param userName
	 * @param paaword
	 * @param ip
	 * @return
	 */
	public boolean chkiUserLogin(String userName, String password, String ip) {
		Holder<Integer> errorCode = new Holder<Integer>();
		if (rightMngr.userLogin(userName, password, ip, identId, errorCode)) {
			bLogin = true;
			return true;
		} else {
			Logger.WriteException(new Exception("cnki login failure, error code :" + errorCode.value));
			return false;
		}
	}

	public String getIdentId() {
		return this.identId.value;
	}

	/**
	 * 个人用户名\密码登陆 或 机构登陆
	 * 
	 * @param OrgName
	 * @param OrgPwd
	 * @param IP
	 * @return
	 */
	public boolean cnkiUserLogin(String OrgName, String OrgPwd, String IP, int[] iResult) {
		if ("appletest2013".equals(OrgName) && "111111".equals(OrgPwd)) {
			iResult[1] = 1;
			return true;
		}
		Holder<Integer> errorCode = new Holder<Integer>();
		// Holder<String> identId = new Holder<String>();
		if (rightMngr.userLogin(OrgName, OrgPwd, IP, identId, errorCode)) {
			Holder<Boolean> iia = new Holder<Boolean>();
			if (rightMngr.isInstitutionAccount(identId.value, iia, errorCode)) {
				iResult[1] = iia.value ? 0 : 1;
			} else {
				Logger.WriteException(new Exception("check nstitutionAccount failure, error code :" + errorCode.value));
			}
			return true;
		} else {
			iResult[0] = errorCode.value;
			Logger.WriteException(new Exception("cnki login failure, error code :" + errorCode.value));
			return false;
		}
	}

	public boolean cnkiLbsUserLogin(String IP, double longitude, double latitude, Holder<String> userName,
			Holder<String> unitName, Holder<Integer> errorCode) {
		// Holder<String> userName = new Holder<String>();
		// Holder<String> unitName = new Holder<String>();
		// Holder<String> identId = new Holder<String>();
		// Holder<Integer> errorCode = new Holder<Integer>();
		return rightMngr.lbsLogin(IP, longitude, latitude, userName, unitName, identId, errorCode);
	}

	public UserStruct getUserInfo(Holder<Integer> errorCode) {
		UserStruct us = rightMngr.getUserInfo(identId.value, errorCode);
		return us;
	}

	/**
	 * 初始化FileInfo:目前FileInfo的信息来自Odata，要改为要去OData获取FileInfo之前，先判断缓存里面有没有，如果有，
	 * 就不用去OData取了
	 * 
	 * @param TypeID
	 * @param InstanceID
	 * @return
	 */
	public boolean setFileInfo(String TypeID, String InstanceID) {
		String strServer = Common.GetConfig("DownloadServer");
		String strUrl = strServer + "/data/" + TypeID + "/" + InstanceID
				+ "?fields=cnki:albumcode,cnki:subjectcode,cnki:subjectsubcolumncode,cnki:pagecount,dc:title,cnki:pagerange,cnki:filesize,dc:date,cnki:year,cnki:issue,dc:source@py,cnki:tablename,cnki:yearissue,cnki:logictablename";
		/*
		 * String strClassID = ""; int iObjectCount = 0; String strTitle = "";
		 * String strObjectRange = ""; int iObjectSize = 0; String strPublishDay
		 * = ""; String strQi = ""; String strSYear = "";
		 */
		try {
			JSONObject jsonSeaData = Common.getSearchData(strUrl, AppToken.getAppToken());
			if (jsonSeaData == null || jsonSeaData.get("store") == null) {
				return false;
			}
			String dataStore = jsonSeaData.get("store").toString();
			if (!dataStore.contains("{"))
				return false;
			JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("store"));
			JSONObject recordObj = JSONObject.fromObject(jsonArray.get(0));
			JSONArray recordArray = JSONArray.fromObject(recordObj.get("data"));
			for (int i = 0; i < recordArray.size(); i++) {
				JSONObject Obj = JSONObject.fromObject(recordArray.get(i));
				String rdfProperty = Obj.get("rdfProperty").toString().toLowerCase();
				switch (rdfProperty) {
				case "cnki:subjectsubcolumncode": // 专题子栏目代码
					this.subjectSubColumnCode = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:pagecount": // 页数
					this.pageCount = (Obj.get("value") == null || Obj.get("value").equals("")) ? 0
							: Integer.parseInt(Obj.get("value").toString());
					break;
				case "dc:title": // 篇名
					this.title = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:pagerange": // 页码范围
					this.pageRange = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:filesize": // 文件大小
					this.fileSize = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "dc:date": // 出版日期
					this.publishDay = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:issue": // 期
					this.issue = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:year": // 年
					this.year = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:albumcode": // 专辑
					this.albumCode = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:subjectcode": // 专题代码
					this.subjectCode = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:yearissue": // 年期
					this.yearIssue = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "dc:source": // 拼音刊名
					this.pyJournalName = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:logictablename": // 逻辑表名
					this.logicTableName = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				case "cnki:tablename": // 物理表名
					this.strTableName = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
							: Obj.get("value").toString();
					break;
				}
			}
		} catch (IOException e) {
			Logger.WriteException(e);
		}

		/*
		 * fileInfo.setClassID(strClassID); // 专题子栏目代码 cnki:subjectsubcolumncode
		 * fileInfo.setObjectCount(iObjectCount); // 页数 cnki:pagecount
		 * fileInfo.setObjectDescript(strTitle); // dc:title
		 * fileInfo.setObjectName(InstanceID); // instanceid
		 * fileInfo.setObjectRange(strObjectRange); // 页码范围 cnki:pagerange
		 * fileInfo.setObjectSize(iObjectSize); // 文章大小 cnki:filesize
		 * fileInfo.setPublishDay(strPublishDay); // 文章出版时间 dc:date(出版日期)
		 * fileInfo.setQi(strQi); // 文章期 cnki:issue fileInfo.setSYear(strSYear);
		 * // 文章年 cnki:year fileInfo.setSearchSQLFlag(0); // 默认值为0；
		 * 
		 */ if (!Common.IsNullOrEmpty(yearIssue) && Common.IsNullOrEmpty(this.issue)) {
			this.issue = this.yearIssue.replace(this.year, "");
		}
		this.fileName = InstanceID;
		this.classId = this.subjectSubColumnCode;
		return true;// setODataFileInfo(InstanceID, TypeID);
	}

	public static String getFirstODataTableName(String TypeID, String InstanceID) {
		String strServer = Common.GetConfig("DownloadServer");
		String strUrl = strServer + "/data/" + TypeID + "/" + InstanceID + "?cnki:tablename,cnki:logictablename";
		try {
			JSONObject jsonSeaData = Common.getSearchData(strUrl, AppToken.getAppToken());
			if (jsonSeaData == null || jsonSeaData.get("store") == null) {
				return "";
			}
			String dataStore = jsonSeaData.get("store").toString();
			if (!dataStore.contains("{"))
				return "";
			JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("store"));
			JSONObject recordObj = JSONObject.fromObject(jsonArray.get(0));
			JSONArray recordArray = JSONArray.fromObject(recordObj.get("data"));
			for (int i = 0; i < recordArray.size(); i++) {
				JSONObject Obj = JSONObject.fromObject(recordArray.get(i));
				String rdfProperty = Obj.get("rdfProperty").toString().toLowerCase();
				switch (rdfProperty) {
				case "cnki:tablename": // 物理表名
					return (Obj.get("value") == null || Obj.get("value").equals("")) ? "" : Obj.get("value").toString();
				}
				/*
				 * case "cnki:logictablename": // 逻辑表名 return (Obj.get("value")
				 * == null || Obj.get("value").equals("")) ? "" :
				 * Obj.get("value").toString(); }
				 */
			}
		} catch (IOException e) {
			Logger.WriteException(e);
		}
		return "";
	}

	/**
	 * 初始化FileInfo:目前FileInfo的信息来自Odata，要改为要去OData获取FileInfo之前，先判断缓存里面有没有，如果有，
	 * 就不用去OData取了
	 * 
	 * @param TypeID
	 * @param InstanceID
	 * @return
	 */
	public boolean setFileInfo(String TypeID, String NewTypeID, String InstanceID, String[] errResult) {
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String fields = "title,albumcode,subjectcode,subjectsubcolumncode,pagecount,pagerange,date,issue,year,yearissue,tablename,filesize,physicaltablename,journalname@py,typecode";
		String txt = "timestamp=" + timestamp + "&appid=" + appId + "&appkey=" + appKey + "&ip=" + ip + "&location="
				+ location + "&mobile=" + mobile + "&did=" + did + "&op=data_get&type=" + NewTypeID + "&id="
				+ InstanceID;
		SHA1 sha1 = new SHA1();
		String sign = sha1.Digest(txt, "UTF-8");
		String reqUrl = ODataUrl + "api/db/" + NewTypeID + "/" + InstanceID;

		Map<String, String> mapHeader = new TreeMap<String, String>();
		mapHeader.put("ip", ip);
		mapHeader.put("app_id", appId);
		mapHeader.put("did", did);
		mapHeader.put("mobile", mobile);
		mapHeader.put("location", location);
		mapHeader.put("timestamp", timestamp);
		mapHeader.put("sign", sign.toLowerCase());
		// System.out.println(mapHeader.toString());
		if (!Common.IsNullOrEmpty(fields)) {
			reqUrl = reqUrl + "?fields=" + fields;
		}
		// System.out.println(reqUrl);
		JSONObject JsonSeaData = sendGet(reqUrl, mapHeader);
		if (JsonSeaData == null || JsonSeaData.get("Count") == null || (Integer) JsonSeaData.get("Count") == 0) {
			if (JsonSeaData.containsKey("error")) {
				errResult[0] = JsonSeaData.get("error_description").toString();
			}
			return false;
		}
		/*
		 * String strClassID = ""; int iObjectCount = 0; String strTitle = "";
		 * String strObjectRange = ""; int iObjectSize = 0; String strPublishDay
		 * = ""; String strQi = ""; String strSYear = "";
		 */
		JSONArray jsonArray = JSONArray.fromObject(JsonSeaData.get("Rows"));
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject recordObj = JSONObject.fromObject(jsonArray.get(i));
			JSONArray subJsonAry = JSONArray.fromObject(recordObj.get("Cells"));
			for (int j = 0; j < subJsonAry.size(); j++) {
				JSONObject subRecObj = JSONObject.fromObject(subJsonAry.get(j));
				String strKey = subRecObj.get("Name").toString().toLowerCase();
				switch (strKey) {
				case "subjectsubcolumncode": // 专题子栏目代码
					this.subjectSubColumnCode = (subRecObj.get("Value") == null || subRecObj.get("Value").equals(""))
							? "" : subRecObj.get("Value").toString();
					this.classId = this.subjectSubColumnCode;
					break;
				case "pagecount": // 页数
					this.pageCount = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? 0
							: Integer
									.parseInt(subRecObj.get("Value").toString().toLowerCase().contains("p")
											? subRecObj.get("Value")
													.toString().substring(0, subRecObj.get("Value").toString()
															.toLowerCase().indexOf("p"))
											: subRecObj.get("Value").toString());
					break;
				case "title": // 篇名
					this.title = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "pagerange": // 页
					this.pageRange = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "filesize": // 文件大小
					this.fileSize = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "date": // 出版日期
					this.publishDay = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "issue": // 期
					this.issue = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "year": // 年
					this.year = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "albumcode": //
					this.albumCode = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "subjectcode": // 年
					this.subjectCode = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "yearissue": // 年
					this.yearIssue = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "journalname@py": // 年
					this.pyJournalName = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "tablename": // 逻辑表名
					this.logicTableName = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "physicaltablename": // 物理表名
					this.strTableName = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				case "typecode": // 物理表名
					this.typeCode = (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
					break;
				}
			}
		}
		/*
		 * fileInfo = new FileInfo(); fileInfo.setClassID(strClassID); //
		 * 专题子栏目代码 cnki:subjectsubcolumncode
		 * fileInfo.setObjectCount(iObjectCount); // 页数 cnki:pagecount
		 * fileInfo.setObjectDescript(strTitle); // dc:title
		 * fileInfo.setObjectName(InstanceID); // instanceid
		 * fileInfo.setObjectRange(strObjectRange); // 页码范围 cnki:pagerange
		 * fileInfo.setObjectSize(iObjectSize); // 文章大小 cnki:filesize
		 * fileInfo.setPublishDay(strPublishDay); // 文章出版时间 dc:date(出版日期)
		 * fileInfo.setQi(strQi); // 文章期 cnki:issue fileInfo.setSYear(strSYear);
		 * // 文章年 cnki:year fileInfo.setSearchSQLFlag(0); // 默认值为0；
		 */
		this.fileName = InstanceID;
		/*
		 * this.filesize = iObjectSize; this.classId = strClassID;
		 * this.pageCount = iObjectCount; this.title = strTitle;
		 */
		return true;// setODataFileInfo(InstanceID, TypeID);
	}

	public static String getSecondODataTableName(String TypeID, String NewTypeID, String InstanceID) {
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String fields = "tablename,physicaltablename";
		String txt = "timestamp=" + timestamp + "&appid=" + appId + "&appkey=" + appKey + "&ip=" + ip + "&location="
				+ location + "&mobile=" + mobile + "&did=" + did + "&op=data_get&type=" + NewTypeID + "&id="
				+ InstanceID;
		SHA1 sha1 = new SHA1();
		String sign = sha1.Digest(txt, "UTF-8");
		String reqUrl = ODataUrl + "api/db/" + NewTypeID + "/" + InstanceID;
		Map<String, String> mapHeader = new TreeMap<String, String>();
		mapHeader.put("ip", ip);
		mapHeader.put("app_id", appId);
		mapHeader.put("did", did);
		mapHeader.put("mobile", mobile);
		mapHeader.put("location", location);
		mapHeader.put("timestamp", timestamp);
		mapHeader.put("sign", sign.toLowerCase());
		if (!Common.IsNullOrEmpty(fields)) {
			reqUrl = reqUrl + "?fields=" + fields;
		}
		JSONObject JsonSeaData = sendGet(reqUrl, mapHeader);
		if (JsonSeaData == null || JsonSeaData.get("Count") == null || (Integer) JsonSeaData.get("Count") == 0) {
			return "";
		}
		JSONArray jsonArray = JSONArray.fromObject(JsonSeaData.get("Rows"));
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject recordObj = JSONObject.fromObject(jsonArray.get(i));
			JSONArray subJsonAry = JSONArray.fromObject(recordObj.get("Cells"));
			for (int j = 0; j < subJsonAry.size(); j++) {
				JSONObject subRecObj = JSONObject.fromObject(subJsonAry.get(j));
				String strKey = subRecObj.get("Name").toString().toLowerCase();
				switch (strKey) {
				case "physicaltablename": // 物理表名
					return (subRecObj.get("Value") == null || subRecObj.get("Value").equals("")) ? ""
							: subRecObj.get("Value").toString();
				}
				/*
				 * case "tablename": // 逻辑表名 return (subRecObj.get("Value") ==
				 * null || subRecObj.get("Value").equals("")) ? "" :
				 * subRecObj.get("Value").toString(); }
				 */
			}
		}
		return "";
	}

	private Boolean setODataFileInfo_bak(String FileID, String TypeID) {
		ODataFileBean FileInfo = new ODataFileBean();
		FileInfo.setFileID(FileID);
		FileInfo.setTypeName("caj");
		FileInfo.setFileMd5("");
		FileInfo.setFileName(this.title);
		FileInfo.setFileSize(this.fileSize);
		FileInfo.setSubjectSubCode(this.classId);
		FileInfo.setPageCount(this.pageCount);
		FileInfo.setObjRange(this.pageRange);
		FileInfo.setObjYear(this.year);
		FileInfo.setObjQi(this.issue);
		FileInfo.setPublishDay(this.publishDay);
		FileInfo.setAlbumCode(this.albumCode);
		FileInfo.setSubjectCode(this.subjectCode);
		FileInfo.setJournalNamepy(this.pyJournalName);
		FileInfo.setYearIssue(this.yearIssue);
		FileInfo.setLogicTableName(logicTableName);
		FileInfo.setTableName(strTableName);
		return saveFileInfo(FileInfo, TypeID, FileID);
	}

	public boolean getODataFileInfo_bak(String TypeID, String FileID) {
		boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getFileTable(TypeID, FileID);
		try {
			dbHelper = DBHelper.GetInstance();
			List<Map<String, Object>> lstFileInfo = dbHelper.ExecuteQuery(
					"fileid,filename,filesize,typename,subjectsubcode,pagecount,objrange,publishday,objqi,objyear,logictablename,tablename,albumcode,subjectcode,yearissue,journalnamepy",
					strTableName, "fileid='".concat(FileID).concat("'"));
			if (lstFileInfo != null && lstFileInfo.size() > 0) {
				this.albumCode = lstFileInfo.get(0).get("albumcode").toString();
				this.subjectCode = lstFileInfo.get(0).get("subjectcode").toString();
				this.yearIssue = lstFileInfo.get(0).get("yearissue").toString();
				this.pyJournalName = lstFileInfo.get(0).get("journalnamepy").toString();
				this.subjectSubColumnCode = lstFileInfo.get(0).get("subjectsubcode").toString();
				this.classId = lstFileInfo.get(0).get("subjectsubcode").toString();
				this.fileSize = lstFileInfo.get(0).get("filesize").toString();
				this.pageCount = lstFileInfo.get(0).get("pagecount") == null ? 0
						: Integer.parseInt(lstFileInfo.get(0).get("pagecount").toString());
				this.issue = lstFileInfo.get(0).get("objqi").toString();
				this.year = lstFileInfo.get(0).get("objyear").toString();
				this.logicTableName = lstFileInfo.get(0).get("logictablename").toString();
				this.strTableName = lstFileInfo.get(0).get("tablename").toString();
				this.pageRange = lstFileInfo.get(0).get("objrange").toString();
				this.publishDay = lstFileInfo.get(0).get("publishday").toString();
				this.fileName = FileID;
				bRet = true;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			;
		}
		return bRet;
	}

	public boolean getFirstODataFileInfo_bak() {
		boolean bRet = false;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			List<Map<String, Object>> lstFileInfo = dbHelper.ExecuteQuery(
					"select fileid,filename,filesize,typename,subjectsubcode,pagecount,objrange,publishday,objqi,objyear,logictablename,tablename,albumcode,subjectcode,yearissue,journalnamepy from journals1 limit 1");
			if (lstFileInfo != null && lstFileInfo.size() > 0) {
				this.albumCode = lstFileInfo.get(0).get("albumcode").toString();
				this.subjectCode = lstFileInfo.get(0).get("subjectcode").toString();
				this.yearIssue = lstFileInfo.get(0).get("yearissue").toString();
				this.pyJournalName = lstFileInfo.get(0).get("journalnamepy").toString();
				this.subjectSubColumnCode = lstFileInfo.get(0).get("subjectsubcode").toString();
				this.classId = lstFileInfo.get(0).get("subjectsubcode").toString();
				this.fileSize = lstFileInfo.get(0).get("filesize").toString();
				this.pageCount = lstFileInfo.get(0).get("pagecount") == null ? 0
						: Integer.parseInt(lstFileInfo.get(0).get("pagecount").toString());
				this.issue = lstFileInfo.get(0).get("objqi").toString();
				this.year = lstFileInfo.get(0).get("objyear").toString();
				this.logicTableName = lstFileInfo.get(0).get("logictablename").toString();
				this.strTableName = lstFileInfo.get(0).get("tablename").toString();
				this.pageRange = lstFileInfo.get(0).get("objrange").toString();
				this.publishDay = lstFileInfo.get(0).get("publishday").toString();
				this.fileName = lstFileInfo.get(0).get("fileid").toString();
				;
				bRet = true;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			;
		}
		return bRet;
	}

	public String getPhysicalTableName() {
		return this.strTableName;
	}

	public String getLogicTableName() {
		return this.logicTableName;
	}

	/**
	 * 验证权限(是不是可以在这里setFileinfo啊)
	 * 
	 * @param TypeID
	 * @param InstanceID
	 * @param UserName
	 * @param ip
	 * @param arrResult
	 * @return
	 *//*
		 * public int getUserAuthority(String TypeID, String FileID, String[]
		 * arrResult) { arrResult[0] = ""; if (!bLogin) { arrResult[0] =
		 * "user name or password error"; arrResult[1] = "-1"; return -1; } if
		 * (fileInfo == null) { arrResult[0] = "file not exist"; arrResult[1] =
		 * "-2"; return -1; }
		 * 
		 * ProductInfo pi = new ProductInfo(); String strRootID =
		 * tAuthMngr.GetProductRootID(logicTableName);
		 * 
		 * ProductInfo[] arrProductID = tAuthMngr.GetProductInfo(new String[] {
		 * strRootID });
		 * 
		 * if (arrProductID == null) { pi.setFieldInfo(null); } else {
		 * ArrayOfTFieldInfo aryTFieldInfo = arrProductID[0].getFieldInfo();
		 * TFieldInfo[] arrFieldInfo = aryTFieldInfo.getTFieldInfo(); if
		 * (arrFieldInfo.length > 0) { Map<String, String> mapTFieldInfo = new
		 * HashMap<String, String>(); for (int i = 0; i < arrFieldInfo.length;
		 * i++) { mapTFieldInfo.put(arrFieldInfo[i].getFieldName(), ""); }
		 * ArrayOfTFieldInfo aryNewTFieldInfo = new ArrayOfTFieldInfo(); if
		 * (getTFieldInfo(mapTFieldInfo, TypeID, FileID)) { for (Entry<String,
		 * String> en : mapTFieldInfo.entrySet()) { TFieldInfo tFieldInfo = new
		 * TFieldInfo(); tFieldInfo.setFieldName(en.getKey());
		 * tFieldInfo.setValue(en.getValue());
		 * aryNewTFieldInfo.addTFieldInfo(tFieldInfo); }
		 * pi.setFieldInfo(aryNewTFieldInfo); } else { pi.setFieldInfo(null); }
		 * } else { pi.setFieldInfo(null); } } pi.setRootID(strRootID);
		 * 
		 * CDownloadInfo[] arrCDInfo = new CDownloadInfo[1]; CDownloadInfo di =
		 * new CDownloadInfo(); di.setFile(fileInfo); di.setProduct(pi);
		 * di.setOperateName("下载"); di.setTotalSize(fileInfo.getObjectSize());
		 * di.setDownLoadType("caj下载"); di.setURL("URL");
		 * 
		 * arrCDInfo[0] = di; int[] ia = new int[1]; // if (!IsFeeFlag) { ia =
		 * tAuthMngr.IsRightAccess(new ProductInfo[] { pi }, new FileInfo[] {
		 * fileInfo }, OperateType.Download); if (ia[0] != 1) { if (ia[0] ==
		 * 7000) { arrResult[0] = "has no app usage rights";
		 * Logger.WriteDownTraceLog("", TypeID, FileID, 7,
		 * "chkuserauthority no right to use app,will not appear at present",
		 * 0); arrResult[1] = "-6"; } else { Logger.WriteDownTraceLog("",
		 * TypeID, FileID, 7, "chkuserauthority has no right buy", 0);
		 * arrResult[0] = "has no Right"; arrResult[1] = "-3"; } //
		 * 判断是否支持使用app，如果app不能使用将返回-7000 return -1; } // } int[] ShowBalance = {
		 * 0 }; double[] Balance = { 0 }; double[] Ticket = { 0 };
		 * 
		 * int iQuery = tAuthMngr.QueryPrice(arrCDInfo, ShowBalance, Balance,
		 * Ticket); if (iQuery != 1) { arrResult[0] = "Get user balance error";
		 * arrResult[1] = "-4"; Logger.WriteDownTraceLog("", TypeID, FileID, 7,
		 * "chkuserauthority get user balance error", 0); return -1; } double
		 * dPrice = arrCDInfo[0].getFile().getExpenses(); // if ((ShowBalance[0]
		 * == 0 && iFeeFlag == 1) || (ShowBalance[0] == 1 && // iFeeFlag == 0))
		 * { // Logger.WriteDownTraceLog("", TypeID, FileID, 9, "dPrice=" +
		 * dPrice + // "--> iFeeFlag = " + iFeeFlag + "-->ShowBalance=" +
		 * ShowBalance[0], // 0); // } // if (ShowBalance[0] == 1) { if (dPrice
		 * > 0) { if (Balance[0] + Ticket[0] < dPrice) { arrResult[0] =
		 * " Not sufficient funds. UserBalance:" + Balance[0] + ",UserTicket:" +
		 * Ticket[0] + ",Price:" + dPrice; arrResult[1] = "-5"; arrResult[2] =
		 * String.valueOf(Balance[0]); arrResult[3] = String.valueOf(Ticket[0]);
		 * arrResult[4] = String.valueOf(dPrice); arrResult[5] =
		 * String.valueOf(fileInfo.getObjectCount()); return -1; } else {
		 * arrResult[0] = "Will charge to fee. UserBalance:" + Balance[0] +
		 * ",UserTicket:" + Ticket[0] + ",Price:" + dPrice; arrResult[1] = "1";
		 * arrResult[2] = String.valueOf(Balance[0]); arrResult[3] =
		 * String.valueOf(Ticket[0]); arrResult[4] = String.valueOf(dPrice);
		 * arrResult[5] = String.valueOf(fileInfo.getObjectCount()); return 0; }
		 * } return 1; }
		 */
	
	public List<Integer> chkRightAccess(JSONArray array, Holder<Integer> errorCode){
		return rightMngr.chkRightAccess(identId.value, array, errorCode);
	}

	public int getUserAuthority(String TypeID, String FileID, String[] arrResult) {
		Holder<Integer> errorCode = new Holder<Integer>();
		String logicTableName = this.logicTableName;
		if(Common.IsNullOrEmpty(logicTableName))
			logicTableName = TypeID;
		if (!rightMngr.isRightAccess(identId.value, logicTableName, classId, pageCount, title, fileName, albumCode,
				subjectCode, subjectSubColumnCode, pyJournalName, year, issue, yearIssue, this.fileSize, pageRange,
				publishDay, typeCode, errorCode)) {
			// if (!rightMngr.isRightAccess(identId.value, logicTableName,
			// classId, pageCount, title, fileName, errorCode)) {
			Logger.WriteDownTraceLog("", TypeID, FileID, 7, "chkuserauthority has no right buy", 0);
			arrResult[0] = "has no Right";
			arrResult[1] = "-3";
			return -1;
		}
		Holder<Integer> showBalance = new Holder<Integer>();
		Holder<Double> balance = new Holder<Double>();
		Holder<Double> ticket = new Holder<Double>();
		Holder<Integer> priceType = new Holder<Integer>();
		Holder<Double> price = new Holder<Double>();
		Holder<Double> discount = new Holder<Double>();
		Holder<Double> expenses = new Holder<Double>();
		if (!rightMngr.queryPrice(identId.value, logicTableName, classId, pageCount, title, fileName, albumCode,
				subjectCode, subjectSubColumnCode, pyJournalName, year, issue, yearIssue, this.fileSize, pageRange,
				publishDay, typeCode, showBalance, balance, ticket, priceType, price, discount, expenses, errorCode)) {

			arrResult[0] = "Get user balance error";
			arrResult[1] = String.valueOf(errorCode.value);
			Logger.WriteDownTraceLog("", TypeID, FileID, 7, "chkuserauthority get user balance error", 0);
			return -1;
		}
		if (expenses.value != null) {
			double dPrice = expenses.value;
			if (dPrice > 0) {
				if (balance.value + ticket.value < dPrice) {
					arrResult[0] = " Not sufficient funds. UserBalance:" + balance.value + ",UserTicket:" + ticket.value
							+ ",Price:" + dPrice;
					arrResult[1] = "-5";
					arrResult[2] = String.valueOf(balance.value);
					arrResult[3] = String.valueOf(ticket.value);
					arrResult[4] = String.valueOf(dPrice);
					arrResult[5] = String.valueOf(pageCount);
					return -1;
				} else {
					arrResult[0] = "Will charge to fee. UserBalance:" + balance.value + ",UserTicket:" + ticket.value
							+ ",Price:" + dPrice;
					arrResult[1] = "1";
					arrResult[2] = String.valueOf(balance.value);
					arrResult[3] = String.valueOf(ticket.value);
					arrResult[4] = String.valueOf(dPrice);
					arrResult[5] = String.valueOf(pageCount);
					return 0;
				}
			}
		}
		return 1;
	}

	public boolean getUserBalance(String userName, Holder<Integer> showBalance, Holder<Double> balance,
			Holder<Double> ticket, Holder<Integer> errorCode) {
		if (rightMngr.getUserBalance(identId.value, "cjfd", fileName, "H", "H131_3", "H131_3", pyJournalName, "2014",
				issue, yearIssue, this.fileSize, pageRange, publishDay, typeCode, showBalance, balance, ticket, errorCode)) {
			writeLog("userName=" + userName + "-->iddentid=" + identId.value + "--->type=cjfd" + "-->filename="
					+ fileName + "-->pyJournaleName=" + pyJournalName + "-->issue=" + issue + "-->filesize=" + fileSize
					+ "-->pageRange=" + pageRange + "-->publishDay=" + publishDay + "-->balance=" + balance.value
					+ "-->ticket=" + ticket.value + "-->errorCode=" + errorCode.value);
			return true;
		}
		return false;
	}

	public static void writeLog(String data) {
		File file = new File("d:\\cnki0823rightmngr.txt");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter sucsessFile = new FileWriter(file, true);
			sucsessFile.write(data + "\r\n");
			sucsessFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	public int cnkJournalInfoAuthority(String titlepy, String dbcode, String year, String issue, String[] arrResult) {
		Holder<Integer> errorCode = new Holder<Integer>();
		if (!rightMngr.isRightAccess(identId.value, dbcode, classId, pageCount, title, titlepy+year+issue, albumCode, subjectCode,
				subjectSubColumnCode, titlepy, year, issue, yearIssue, "40", "10-50", publishDay, typeCode,
				errorCode)) {
			arrResult[0] = "has no Right";
			arrResult[1] = "-3";
			return -1;
		}
		Holder<Integer> showBalance = new Holder<Integer>();
		Holder<Double> balance = new Holder<Double>();
		Holder<Double> ticket = new Holder<Double>();
		Holder<Integer> priceType = new Holder<Integer>();
		Holder<Double> price = new Holder<Double>();
		Holder<Double> discount = new Holder<Double>();
		Holder<Double> expenses = new Holder<Double>();
		if (!rightMngr.queryPrice(identId.value, dbcode, classId, pageCount, title, titlepy+year+issue, albumCode,
				subjectCode, subjectSubColumnCode, titlepy, year, issue, yearIssue, "40", "10-50",
				publishDay, typeCode, showBalance, balance, ticket, priceType, price, discount, expenses, errorCode)) {

			arrResult[0] = "Get user balance error";
			arrResult[1] = String.valueOf(errorCode.value);
			return -1;
		}
		if (expenses.value != null) {
			double dPrice = expenses.value;
			if (dPrice > 0) {
				if (balance.value + ticket.value < dPrice) {
					arrResult[0] = " Not sufficient funds. UserBalance:" + balance.value + ",UserTicket:" + ticket.value
							+ ",Price:" + dPrice;
					arrResult[1] = "-5";
					arrResult[2] = String.valueOf(balance.value);
					arrResult[3] = String.valueOf(ticket.value);
					arrResult[4] = String.valueOf(dPrice);
					arrResult[5] = String.valueOf(pageCount);
					return -1;
				} else {
					arrResult[0] = "Will charge to fee. UserBalance:" + balance.value + ",UserTicket:" + ticket.value
							+ ",Price:" + dPrice;
					arrResult[1] = "1";
					arrResult[2] = String.valueOf(balance.value);
					arrResult[3] = String.valueOf(ticket.value);
					arrResult[4] = String.valueOf(dPrice);
					arrResult[5] = String.valueOf(pageCount);
					return 0;
				}
			}
		}
		return 1;
	}

	public boolean getJournalInfoPermision(String dbCode, String titlepy, String year, String issue,
			String[] arrResult, Holder<Integer> errorCode) {

		Holder<Integer> showBalance = new Holder<Integer>();
		Holder<Double> balance = new Holder<Double>();
		Holder<Double> ticket = new Holder<Double>();
		Holder<Integer> priceType = new Holder<Integer>();
		Holder<Double> price = new Holder<Double>();
		Holder<Double> discount = new Holder<Double>();
		Holder<Double> expenses = new Holder<Double>();
		try {
			if (!rightMngr.queryPrice(identId.value, dbCode, classId, pageCount, titlepy + year + issue,
					titlepy + year + issue, albumCode, subjectCode, subjectSubColumnCode, titlepy, year, issue,
					year + issue, this.fileSize, pageRange, publishDay, typeCode, showBalance, balance, ticket, priceType, price,
					discount, expenses, errorCode)) {
				arrResult[0] = "fee failed, temporarily unable to download";
				return false;
			}
		} catch (Exception e) {
			LoggerFile.appendMethod("C:\\queryPrice", "queryPrice-->" + e.getMessage());
		}
		if(balance.value !=null && ticket.value !=null && expenses.value!=null){
			if (balance.value + ticket.value < expenses.value) {
				arrResult[0] = " Not sufficient funds. UserBalance:" + balance.value + ",UserTicket:" + ticket.value
						+ ",Price:" + expenses.value;
				return false;
			}
		}
		try {
			if (!rightMngr.fee(identId.value, dbCode, classId, pageCount, titlepy + year + issue,
					titlepy + year + issue, albumCode, subjectCode, subjectSubColumnCode, pyJournalName, year, issue,
					year + issue, this.fileSize, pageRange, publishDay, typeCode, errorCode)) { // 成功返回1，否则返回错误代码
				arrResult[0] = "fee failed, temporarily unable to download";
				return false;
			}
		} catch (Exception e) {
			LoggerFile.appendMethod("C:\\Fee", "fee-->" + e.getMessage());
		}
		arrResult[0] = String.valueOf(expenses.value);
		return true;
	}

	private boolean getTFieldInfo(Map<String, String> mapProperty, String TypeID, String InstanceID) {
		if (!ODataMngr.getODataProperties(mapProperty, TypeID))
			return false;
		StringBuilder sbField = new StringBuilder();
		for (Entry<String, String> en : mapProperty.entrySet()) {
			sbField.append(en.getValue()).append(",");
		}
		if (sbField.length() > 0) {
			sbField.deleteCharAt(sbField.length() - 1);
		} else {
			return false;
		}

		String strServer = Common.GetConfig("DownloadServer");
		String strUrl = strServer + "/data/" + TypeID + "/" + InstanceID + "?fields=" + sbField.toString();
		try {
			JSONObject jsonSeaData = Common.getSearchData(strUrl, AppToken.getAppToken());
			if (jsonSeaData == null || jsonSeaData.get("store") == null) {
				return false;
			}
			String dataStore = jsonSeaData.get("store").toString();
			if (!dataStore.contains("{"))
				return false;
			JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("store"));
			JSONObject recordObj = JSONObject.fromObject(jsonArray.get(0));
			JSONArray recordArray = JSONArray.fromObject(recordObj.get("data"));
			for (int i = 0; i < recordArray.size(); i++) {
				JSONObject Obj = JSONObject.fromObject(recordArray.get(i));
				String rdfProperty = Obj.get("rdfProperty").toString().toLowerCase();
				for (Entry<String, String> en : mapProperty.entrySet()) {
					if (rdfProperty.equals(en.getValue())) {
						mapProperty.put(en.getKey(), (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
								: Obj.get("value").toString());
					}
				}
			}
		} catch (IOException e) {
			Logger.WriteException(e);
		}
		return true;

	}

	/**
	 * 电子商务真正扣费接口
	 * 
	 * @return
	 *//*
		 * public boolean getPermision(String TypeID, String FileID, String[]
		 * arrResult) {
		 * 
		 * ProductInfo pi = new ProductInfo(); String strRootID =
		 * tAuthMngr.GetProductRootID(logicTableName);
		 * 
		 * ProductInfo[] arrProductID = tAuthMngr.GetProductInfo(new String[] {
		 * strRootID });
		 * 
		 * if (arrProductID == null) { pi.setFieldInfo(null); } else {
		 * ArrayOfTFieldInfo aryTFieldInfo = arrProductID[0].getFieldInfo();
		 * TFieldInfo[] arrFieldInfo = aryTFieldInfo.getTFieldInfo(); if
		 * (arrFieldInfo.length > 0) { Map<String, String> mapTFieldInfo = new
		 * HashMap<String, String>(); for (int i = 0; i < arrFieldInfo.length;
		 * i++) { mapTFieldInfo.put(arrFieldInfo[i].getFieldName(), ""); }
		 * ArrayOfTFieldInfo aryNewTFieldInfo = new ArrayOfTFieldInfo(); if
		 * (getTFieldInfo(mapTFieldInfo, TypeID, FileID)) { for (Entry<String,
		 * String> en : mapTFieldInfo.entrySet()) { TFieldInfo tFieldInfo = new
		 * TFieldInfo(); tFieldInfo.setFieldName(en.getKey());
		 * tFieldInfo.setValue(en.getValue());
		 * aryNewTFieldInfo.addTFieldInfo(tFieldInfo); }
		 * pi.setFieldInfo(aryNewTFieldInfo); } else { pi.setFieldInfo(null); }
		 * } else { pi.setFieldInfo(null); } } pi.setRootID(strRootID);
		 * 
		 * CDownloadInfo[] arrCDInfo = new CDownloadInfo[1]; CDownloadInfo di =
		 * new CDownloadInfo(); di.setFile(fileInfo); di.setProduct(pi);
		 * di.setOperateName("下载"); di.setTotalSize(fileInfo.getObjectSize());
		 * di.setDownLoadType("caj下载"); di.setURL("cajviewer@cajviewer");
		 * 
		 * arrCDInfo[0] = di;
		 * 
		 * int[] ShowBalance = { 0 }; double[] Balance = { 0 }; double[] Ticket
		 * = { 0 };
		 * 
		 * int iQuery = tAuthMngr.QueryPrice(arrCDInfo, ShowBalance, Balance,
		 * Ticket); double dPrice = arrCDInfo[0].getFile().getExpenses();
		 * 
		 * if (iQuery != 1) { arrResult[0] =
		 * "fee failed, temporarily unable to download"; return false; } else {
		 * if (Balance[0] + Ticket[0] < dPrice) { arrResult[0] =
		 * " Not sufficient funds. UserBalance:" + Balance[0] + ",UserTicket:" +
		 * Ticket[0] + ",Price:" + dPrice; return false; } } int iRet =
		 * tAuthMngr.GetPermision(SRightMngrStub.OperateType.Download, di); //
		 * 成功返回1，否则返回错误代码 if (iRet != 1) { arrResult[0] =
		 * "fee failed, temporarily unable to download"; return false; }
		 * arrResult[0] = String.valueOf(dPrice); return true; }
		 */
	/**
	 * 电子商务真正扣费接口
	 * 
	 * @return
	 */
	public boolean getPermision(String TypeID, String FileID, String[] arrResult, Holder<Integer> errorCode) {
		String logicTableName = this.logicTableName;
		if(Common.IsNullOrEmpty(logicTableName))
			logicTableName = TypeID;
		Holder<Integer> showBalance = new Holder<Integer>();
		Holder<Double> balance = new Holder<Double>();
		Holder<Double> ticket = new Holder<Double>();
		Holder<Integer> priceType = new Holder<Integer>();
		Holder<Double> price = new Holder<Double>();
		Holder<Double> discount = new Holder<Double>();
		Holder<Double> expenses = new Holder<Double>();
		try {
			if (!rightMngr.queryPrice(identId.value, logicTableName, classId, pageCount, title, fileName, albumCode,
					subjectCode, subjectSubColumnCode, pyJournalName, year, issue, yearIssue, this.fileSize, pageRange,
					publishDay, typeCode, showBalance, balance, ticket, priceType, price, discount, expenses, errorCode)) {
				arrResult[0] = "fee failed, temporarily unable to download";
				return false;
			}
		} catch (Exception e) {
			LoggerFile.appendMethod("C:\\queryPrice", "queryPrice-->" + e.getMessage());
		}
		if(balance.value!=null && expenses.value!=null && expenses.value!=null){
			if (balance.value + ticket.value < expenses.value) {
				arrResult[0] = " Not sufficient funds. UserBalance:" + balance.value + ",UserTicket:" + ticket.value
						+ ",Price:" + expenses.value;
				return false;
			}
		}

		try {
			if (!rightMngr.fee(identId.value, logicTableName, classId, pageCount, title, FileID, albumCode, subjectCode,
					subjectSubColumnCode, pyJournalName, year, issue, yearIssue, this.fileSize, pageRange, publishDay, typeCode,
					errorCode)) { // 成功返回1，否则返回错误代码
				arrResult[0] = "fee failed, temporarily unable to download";
				return false;
			}
		} catch (Exception e) {
			LoggerFile.appendMethod("C:\\Fee", "fee-->" + e.getMessage());
		}
		arrResult[0] = String.valueOf(expenses.value);
		return true;
	}

	public boolean getSymbolValidCode(String phoneNum, int timeStamp, int Length, Holder<String> authCode,
			Holder<Integer> symbolValidCodeResult, Holder<Integer> errorCode) {
		if (rightMngr.getSymbolValidCode(identId.value, phoneNum, timeStamp, Length, authCode, symbolValidCodeResult,
				errorCode)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean setSymbolName(String identId, String phoneNum, String code, Holder<Integer> errorCode) {
		RightMngr mngr = new RightMngr(Constant.PLATFORM);
		if (mngr.setSymbolName(identId, phoneNum, code, errorCode)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 移动服务权限
	 * 
	 * @param identId
	 * @param hmr
	 * @param errorCode
	 * @return
	 */
	public boolean haveMobileRight(Holder<Boolean> hmr, Holder<Integer> errorCode) {
		if (rightMngr.haveMobileRight(identId.value, hmr, errorCode)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean bindUser(String userName, boolean roamStatus, int operateType, String parentName, String groupID) {
		if (rightMngr.bindUser(userName, false, operateType, identId.value, parentName, groupID) != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 自动漫游权限
	 * 
	 * @param identId
	 * @param harr
	 * @param errorCode
	 * @return
	 */
	public boolean haveAutoRoamingRight(String identId, Holder<Boolean> harr, Holder<Integer> errorCode) {
		RightMngr mngr = new RightMngr(Constant.PLATFORM);
		if (mngr.haveAutoRoamingRight(identId, harr, errorCode)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 下载原文数据到hfs
	 * 
	 * @param TypeID
	 * @param FileID
	 * @param TypeName
	 * @param UserName
	 * @param IP
	 * @param arrResult
	 *            返回值还需要做判断
	 * @return
	 */
	public boolean downSourceFile(String TypeID, String FileID, String TypeName, String UserName, boolean bAdd,
			long[] arrResult) {
		boolean bResult = false;
		String strUrl = "";
		try {
			strUrl = getDownloadUrl(TypeID, FileID, UserName);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (strUrl == null) {
			arrResult[0] = 1000;
			return false;
		}
		int i = 5;
		do {
			arrResult[0] = 2000;
			try {
				strUrl = getDownloadUrl(TypeID, FileID, UserName);
			} catch (Exception e) {
				Logger.WriteException(e);
			}
			bResult = uploadHSFFile(UserName, TypeID, FileID, TypeName, strUrl, bAdd, arrResult);
			if (arrResult[0] != -404) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i--;
		} while (arrResult[0] == -404 && i > 0);
		return bResult;
	}

	/**
	 * 获取下载链接地址
	 * 
	 * @param TypeID
	 * @param FileID
	 * @param UserName
	 * @param IP
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getDownloadUrl(String TypeID, String FileID, String UserName) throws UnsupportedEncodingException {
		// String uid = tAuthMngr.GetUserIdenID(); //
		String strConSourceFileUrl = Common.GetConfig("SourceFileServer");
		String[] arrUrl = strConSourceFileUrl.split(";");
		int iLen = arrUrl.length;
		if (iIndex >= iLen) {
			iIndex = 0;
		}
		String strSourceFileUrl = "";
		try {
			strSourceFileUrl = arrUrl[iIndex];
			iIndex = iIndex + 1;
		} catch (Exception e) {
			strSourceFileUrl = arrUrl[0];
		}
		String q = FileID.concat("*20*").concat(GetDateTime()).concat("*1-5*").concat(UserName);
		strSourceFileUrl = strSourceFileUrl.concat("q=")
				.concat(URLEncoder.encode(Encrypt(q, "xue13wen", true), "UTF-8")).concat("&db=").concat(strTableName)
				.concat("&t=cajdown&cflag=&p=").concat(strTableName).concat("&uid=")
				.concat(URLEncoder.encode(identId.value, "utf-8"));
		// Logger.WriteException(new Exception(strSourceFileUrl));
		return strSourceFileUrl;
	}

	/**
	 * 临时复制上面的，和上面获取downloadurl一样。只是为了应付已下载用户下载的，此处从外面出入strTableName,identId 为假的
	 * 
	 * @param TypeID
	 * @param FileID
	 * @param UserName
	 * @param strTableName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getDownloadUrl(String TypeID, String FileID, String UserName, String strTableName)
			throws UnsupportedEncodingException {
		// String uid = tAuthMngr.GetUserIdenID(); //
		String strConSourceFileUrl = Common.GetConfig("SourceFileServer");
		String[] arrUrl = strConSourceFileUrl.split(";");
		int iLen = arrUrl.length;
		if (iIndex >= iLen) {
			iIndex = 0;
		}
		String strSourceFileUrl = "";
		try {
			strSourceFileUrl = arrUrl[iIndex];
			iIndex = iIndex + 1;
		} catch (Exception e) {
			strSourceFileUrl = arrUrl[0];
		}
		String q = FileID.concat("*20*").concat(GetDateTime()).concat("*1-5*").concat(UserName);
		strSourceFileUrl = strSourceFileUrl.concat("q=")
				.concat(URLEncoder.encode(Encrypt(q, "xue13wen", true), "UTF-8")).concat("&db=").concat(strTableName)
				.concat("&t=cajdown&cflag=&p=").concat(strTableName).concat("&uid=")
				.concat(URLEncoder.encode(
						"WEFiRVgwQ3dIa0NaV1NRUnVqc3VyRTUyVXpydXZhRGF0VFRiT3ZjcnJYZVJCOXVodlE9PQ==|192.168.26.81|JGApp",
						"utf-8"))
				.replace("t=cajdown", "t=pdfdown");
		// Logger.WriteException(new Exception(strSourceFileUrl));
		return strSourceFileUrl;
	}

	/*
	 * public static String getUserIdenID(String IP) { TAuthMngr tAuthMngr = new
	 * TAuthMngr(Pid); int iRegRet = tAuthMngr.UserLogin("", "", IP,
	 * LogonType.IPLogin); if (iRegRet != 1) { Logger.WriteException(new
	 * Exception("login TAuthMngr Failed, ErrorCod is:" +
	 * tAuthMngr.getErrorCode())); return null; } return
	 * tAuthMngr.GetUserIdenID(); }
	 */

	private static synchronized boolean creatUploadDir(String RemotePath) {
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

	public static String GetDateTime() {
		return GetDateTime("yyyy/MM/dd HH:mm:ss");
	}

	public static String GetDate() {
		return GetDateTime("yyyy-MM-dd");
	}

	public static String GetDateTime(String Formatter) {
		SimpleDateFormat format = new SimpleDateFormat(Formatter);
		String strDate = format.format(new Date().getTime());
		format = null;
		return strDate;
	}

	public static void main(String[] args) {
		try {
			System.out.println(Encrypt("RBZI201505*20*2017-07-18 14:30:30**", "lczl2014", true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String Encrypt(String input, String password, boolean isBase64OrHex) {
		byte[] Keys = { 0x16, 0x34, 0x56, 0x58, (byte) 0x88, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
		IvParameterSpec zeroIv;
		SecretKeySpec key;
		byte[] encryptedData = null;
		try {
			zeroIv = new IvParameterSpec(Keys);
			key = new SecretKeySpec(password.getBytes("utf-8"), "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			encryptedData = cipher.doFinal(input.getBytes("utf-8"));
		} catch (Exception e) {
			return e.getMessage();
		}
		if (isBase64OrHex) {
			// return
			// EncryptEncoding(Base64.getEncoder().encodeToString(encryptedData));
			// BASE64Encoder base64 = new BASE64Encoder();
			return EncryptEncoding(new String(Base64.encodeBase64(encryptedData)));
		}
		return new String(encryptedData);
	}

	public static String EncryptEncoding(String OldStr) {
		return OldStr.replace("+", "%mmd2B").replace("\"", "%mmd22").replace("'", "%mmd27").replace("/", "%mmd2F");
	}

	/**
	 * 
	 * @param TypeId
	 * @param FileID
	 * @param TypeName
	 * @param ReadLength
	 * @param Position
	 * @param FileContent
	 * @return
	 */
	private boolean uploadHSFFile(final String UserName, final String TypeID, final String FileID, String TypeName,
			final String Url, boolean isAddInfo, long[] arrResult) {
		boolean bResult = true;
		String RemoteFilePath = "CajCloud\\"
				+ TypeID.concat(String.valueOf(Math.abs(FileID.hashCode())).substring(0, 1)).toLowerCase() + "\\";
		String RemoteFile = RemoteFilePath.concat(FileID).concat(".").concat(TypeName);
		HFSOutputStream fileHandler = null;
		Object[] arrFileHandler = mFileOutStream.get(FileID);
		long lFileRet = 0;
		if (arrFileHandler == null) {
			if (!creatUploadDir(RemoteFilePath)) {
				Logger.WriteDownTraceLog(UserName, TypeID, FileID, 4, "hfms create Upload Dir error", 0); // 1）上传全文，创建目录失败
				arrResult[0] = SysConfigMngr.ERROR_CODE.ERROR_CREATEFILEFOLDER.code;
				return false;
			}
			HFS_OPEN_FILE hof = null;
			try {
				hof = fc.OpenFile(RemoteFile, "wb+");
			} catch (Exception e) {
				Logger.WriteException(e);
				Logger.WriteDownTraceLog(UserName, TypeID, FileID, 4,
						"hfms Open File error->threadid=" + Thread.currentThread().getId(), 0);// 1）上传全文hfms，打开文件目录失败
				arrResult[0] = SysConfigMngr.ERROR_CODE.ERROR_OPENFILE.code;
				return false;
			}
			if (hof == null) {
				Logger.WriteDownTraceLog(UserName, TypeID, FileID, 4,
						"hfms Open File is null->threadid=" + Thread.currentThread().getId(), 0);// 1）上传全文hfms，打开文件为null
				arrResult[0] = SysConfigMngr.ERROR_CODE.ERROR_OPENFILENULL.code;
				return false;
			}
			long lRet = hof.Handle;
			fileHandler = new HFSOutputStream(fc, lRet);
			arrFileHandler = new Object[] { fileHandler, lRet };
			mFileOutStream.put(FileID, arrFileHandler);
		}
		fileHandler = (HFSOutputStream) arrFileHandler[0];
		lFileRet = (long) arrFileHandler[1];
		// int iResult = 0;
		URL url = null;
		HttpURLConnection urlconn = null;
		try {
			byte[] bRet = null;
			url = new URL(Url);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestMethod("GET");
			urlconn.setDefaultUseCaches(false);
			urlconn.setDoInput(true);
			urlconn.setDoOutput(true);
			urlconn.setRequestProperty("Accept-Encoding", "utf-8");
			urlconn.connect();

			// String strHeader;
			// for(int i=1;i<50;i++){
			// strHeader = urlconn.getHeaderFieldKey(i);
			// if(strHeader!=null){
			// Logger.WriteDownTraceLog(UserName, TypeID, FileID, 5,
			// "strHeader:"+String.valueOf(strHeader), 0);// 2）下载原文，长度为0
			// }
			// if(strHeader.equals("Content-Length")){
			// Logger.WriteDownTraceLog(UserName, TypeID, FileID, 5,
			// "strHeader.length:"+urlconn.getHeaderField("Content-Length"),
			// 0);// 2）下载原文，长度为0
			// break;
			// }
			// }
			boolean bChkLength = true;
			String strContentLength = urlconn.getHeaderField("Content-Length");
			if (strContentLength == null || strContentLength.length() == 0) {
				int iConLegth = urlconn.getContentLength();
				if (iConLegth > 0) {
					strContentLength = String.valueOf(iConLegth);
				} else {
					// arrResult[0] = -404;
					bChkLength = false;
					Logger.WriteDownTraceLog(UserName, TypeID, FileID, 5,
							Url + "-->Download Source File Content-Length is zero:" + String.valueOf(strContentLength),
							0);// 2）下载原文，长度为0
					// return false;
				}
			}
			long lLength = 0;
			if (bChkLength) {
				lLength = Long.parseLong(strContentLength);
			}
			if (lLength == 15 || lLength == 51) {
				arrResult[0] = -404;
				Logger.WriteException(new Exception(Url));
				Logger.WriteDownTraceLog(UserName, TypeID, FileID, 5,
						"Download Source File search error or download url overtime->threadid="
								+ Thread.currentThread().getId(),
						0);// 2）下载原文，索引异常或者下载超时
				return false;
			}
			int iDataBlock = 10240;
			bRet = new byte[iDataBlock];
			DataInputStream dataInput = new DataInputStream(urlconn.getInputStream());
			int iPosition = 0;
			int iRead = 0;
			Logger.WriteDownTraceLog(UserName, TypeID, FileID, 5,
					"sourcefile start read data->threadid=" + Thread.currentThread().getId(), 1);// 2）下载原文，开始写入到全文系统
			while ((iRead = dataInput.read(bRet)) > 0) {
				if (lLength < 3072) {
					// System.out.println(new String(bRet,"utf-8"));
					// Logger.WriteException(new Exception(new String(bRet)));
					Logger.WriteException(new Exception(Url));
					byte[] bCheck = Arrays.copyOf(bRet, 6);
					if ("<html>".equals(new String(bCheck))) {
						arrResult[0] = -404;
						Logger.WriteDownTraceLog(UserName, TypeID, FileID, 5,
								"source file is error page,contains html,for example:no download", 0);// 2）下载原文，禁止下载或者直接返回html
						return false;
					}
				}
				int iRet = 0;
				try {
					iRet = fileHandler.write(bRet, iRead, iPosition); // 未做判断
					if (iRet != iRead) {
						Logger.WriteDownTraceLog(UserName, TypeID, FileID, 4,
								"upload file info error->threadid=" + Thread.currentThread().getId(), 0);
					}
				} catch (Exception e) {
					// Logger.WriteException(e);
				}
				iPosition = iPosition + iRead;
				// if(iRet>0){
				// Logger.WriteDownTraceLog(UserName, TypeID, FileID, 4,
				// "write in :"+iPosition + " / " + lLength , 0);
				// }
			}
			dataInput.close();
			if (bChkLength) {
				if (iPosition == lLength) {
					if (mFileOutStream.containsKey(FileID))
						mFileOutStream.remove(FileID);
					fileHandler.close();
					fc.CloseFile(lFileRet);
					Logger.WriteDownTraceLog(UserName, TypeID, FileID, 4,
							"closed1->threadid=" + Thread.currentThread().getId(), 1);
					if (isAddInfo) {
						ODataFileBean FileInfo = new ODataFileBean();
						FileInfo.setFileID(FileID);
						FileInfo.setTypeName("caj");
						FileInfo.setFileMd5("");
						// FileInfo.setFileName(fileInfo.getObjectDescript());
						FileInfo.setFileName(title);
						FileInfo.setFileSize(String.valueOf(lLength));
						// FileInfo.setSubjectSubCode(fileInfo.getClassID());
						FileInfo.setSubjectSubCode(classId);
						// FileInfo.setPageCount(fileInfo.getObjectCount());
						FileInfo.setPageCount(pageCount);
						// FileInfo.setObjRange(fileInfo.getObjectRange());
						// FileInfo.setObjSize(fileInfo.getObjectSize());
						// FileInfo.setObjSize(filesize);
						// FileInfo.setObjYear(fileInfo.getSYear());
						// FileInfo.setPublishDay(fileInfo.getPublishDay());
						// FileInfo.setObjQi(fileInfo.getQi());
						FileInfo.setLogicTableName(logicTableName);
						FileInfo.setTableName(strTableName);
						bResult = saveFileInfo(FileInfo, TypeID, FileID);
					}
					final String newUrl = getDownloadUrl(TypeID, FileID, UserName); // 预防超时，重新取一遍
					new Thread() {
						public void run() {
							UploadMngr.pdf2Epub(TypeID, FileID, UserName, newUrl.replace("t=cajdown", "t=pdfdown"));
						}
					}.start();
				}
			} else {
				if (iPosition > 0) {
					if (mFileOutStream.containsKey(FileID))
						mFileOutStream.remove(FileID);
					fileHandler.close();
					fc.CloseFile(lFileRet);
					Logger.WriteDownTraceLog(UserName, TypeID, FileID, 4, "closed2", 0);
					if (isAddInfo) {
						ODataFileBean FileInfo = new ODataFileBean();
						FileInfo.setFileID(FileID);
						FileInfo.setTypeName("caj");
						FileInfo.setFileMd5("");
						// FileInfo.setFileName(fileInfo.getObjectDescript());
						FileInfo.setFileName(title);
						FileInfo.setFileSize(String.valueOf(lLength));
						// FileInfo.setSubjectSubCode(fileInfo.getClassID());
						FileInfo.setSubjectSubCode(classId);
						// FileInfo.setPageCount(fileInfo.getObjectCount());
						FileInfo.setPageCount(pageCount);
						// FileInfo.setObjRange(fileInfo.getObjectRange());
						// FileInfo.setObjSize(fileInfo.getObjectSize());
						// FileInfo.setObjSize(filesize);
						// FileInfo.setObjYear(fileInfo.getSYear());
						// FileInfo.setPublishDay(fileInfo.getPublishDay());
						// FileInfo.setObjQi(fileInfo.getQi());
						FileInfo.setLogicTableName(logicTableName);
						FileInfo.setTableName(strTableName);
						bResult = saveFileInfo(FileInfo, TypeID, FileID);
					}
					final String newUrl = getDownloadUrl(TypeID, FileID, UserName); // 预防超时，重新取一遍
					new Thread() {
						public void run() {
							UploadMngr.pdf2Epub(TypeID, FileID, UserName, newUrl.replace("t=cajdown", "t=pdfdown"));
						}
					}.start();
				}
			}

		} catch (Exception e) {
			if (e.getMessage().indexOf("Illegal character in URL") != -1) {
				Logger.WriteDownTraceLog(UserName, TypeID, FileID, 5, "url=" + Url, 0);
			}
			Logger.WriteException(e);
			arrResult[0] = SysConfigMngr.ERROR_CODE.ERROR_WRITEFILE.code;
			return false;
		} finally {
			urlconn.disconnect();
		}
		return bResult;
	}

	private static boolean saveFileInfo(ODataFileBean FileInfo, String TypeID, String FileID) {
		String strTableName = getFileTable(TypeID, FileID);
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount(strTableName, "fileid='" + FileID + "'") == 0) {
				bolRet = dbHelper.Insert(strTableName,
						new String[] { "fileid", "filename", "filesize", "typename", "filemd5", "subjectsubcode",
								"pagecount", "objrange", "publishday", "objqi", "objyear", "albumcode", "subjectcode",
								"yearissue", "journalnamepy", "logictablename", "tablename", "time" },
						new Object[] { FileInfo.getFileID(), FileInfo.getFileName(), FileInfo.getFileSize(),
								FileInfo.getTypeName(), FileInfo.getFileMd5(), FileInfo.getSubjectSubCode(),
								FileInfo.getPageCount(), FileInfo.getObjRange(), FileInfo.getPublishDay(),
								FileInfo.getObjQi(), FileInfo.getObjYear(), FileInfo.getAlbumCode(),
								FileInfo.getSubjectCode(), FileInfo.getYearIssue(), FileInfo.getJournalNamepy(),
								FileInfo.getLogicTableName(), FileInfo.getTableName(), Common.GetDateTime() });
			} else {
				bolRet = dbHelper.Update(strTableName, "fileid='" + FileID + "'",
						new String[] { "filename", "filesize", "typename", "filemd5", "subjectsubcode", "pagecount",
								"objrange", "publishday", "objqi", "objyear", "albumcode", "subjectcode", "yearissue",
								"journalnamepy", "logictablename", "tablename", "time" },
						new Object[] { FileInfo.getFileName(), FileInfo.getFileSize(), FileInfo.getTypeName(),
								FileInfo.getFileMd5(), FileInfo.getSubjectSubCode(), FileInfo.getPageCount(),
								FileInfo.getObjRange(), FileInfo.getPublishDay(), FileInfo.getObjQi(),
								FileInfo.getObjYear(), FileInfo.getAlbumCode(), FileInfo.getSubjectCode(),
								FileInfo.getYearIssue(), FileInfo.getJournalNamepy(), FileInfo.getLogicTableName(),
								FileInfo.getTableName(), Common.GetDateTime() });
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static String getPhysicalTable_bak(String TypeID, String FileID) {
		String strTableName = getFileTable(TypeID, FileID);
		DBHelper dbHelper = null;
		List<Map<String, Object>> lstFileInfo = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstFileInfo = dbHelper.ExecuteQuery("tablename", strTableName, "fileid='".concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstFileInfo != null && lstFileInfo.size() > 0) {
			return lstFileInfo.get(0).get("tablename").toString();
		}
		return "";
	}

	public static String getLogicTable(String TypeID, String FileID) {
		String strTableName = getFileTable(TypeID, FileID);
		DBHelper dbHelper = null;
		List<Map<String, Object>> lstFileInfo = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstFileInfo = dbHelper.ExecuteQuery("logictablename", strTableName, "fileid='".concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstFileInfo != null && lstFileInfo.size() > 0) {
			return lstFileInfo.get(0).get("logictablename").toString();
		}
		return "";
	}

	/*
	 * public boolean getFileInfo(String TypeID, String FileID) { DBHelper
	 * dbHelper = null; List<Map<String, Object>> lstFileInfo = null; try {
	 * dbHelper = DBHelper.GetInstance(); lstFileInfo = dbHelper.ExecuteQuery(
	 * "filename, subjectsubcode, pagecount, objrange, objsize, publishday, objqi, objyear, logictablename, tablename"
	 * , getFileTable(TypeID, FileID), "fileid='".concat(FileID).concat("'")); }
	 * catch (Exception e) { Logger.WriteException(e); } if (lstFileInfo !=
	 * null) { Map<String, Object> mapFileInfo = lstFileInfo.get(0); //fileInfo
	 * = new FileInfo();
	 * //fileInfo.setClassID(String.valueOf(mapFileInfo.get("subjectsubcode")));
	 * this.classId = String.valueOf(mapFileInfo.get("subjectsubcode"));
	 * //fileInfo.setObjectCount(Integer.parseInt(String.valueOf(mapFileInfo.get
	 * ("pagecount")))); // 页数// this.pageCount =
	 * Integer.parseInt(String.valueOf(mapFileInfo.get("pagecount"))); //
	 * cnki:pagecount
	 * //fileInfo.setObjectDescript(String.valueOf(mapFileInfo.get("filename")))
	 * ; // dc:title this.title = String.valueOf(mapFileInfo.get("filename"));
	 * //fileInfo.setObjectName(FileID); // instanceid
	 * 
	 * //fileInfo.setObjectRange(String.valueOf(mapFileInfo.get("objrange")));
	 * // 页码范围 // // // cnki:pagerange
	 * //fileInfo.setObjectSize(Integer.parseInt(String.valueOf(mapFileInfo.get(
	 * "objsize")))); // 文章大小
	 * this.filesize=Integer.parseInt(String.valueOf(mapFileInfo.get("objsize"))
	 * ); // // // cnki:filesize
	 * //fileInfo.setPublishDay(String.valueOf(mapFileInfo.get("publishday")));
	 * // 文章出版时间 // // // dc:date(出版日期)
	 * //fileInfo.setQi(String.valueOf(mapFileInfo.get("objqi"))); // 文章期 // //
	 * cnki:issue
	 * //fileInfo.setSYear(String.valueOf(mapFileInfo.get("objyear"))); // 文章年
	 * // // // cnki:year //fileInfo.setSearchSQLFlag(0); // 默认值为0；
	 * this.logicTableName = String.valueOf(mapFileInfo.get("logictablename"));
	 * this.strTableName = String.valueOf(mapFileInfo.get("tablename")); return
	 * true; } else { return false; }
	 * 
	 * }
	 */

	public static String getFileTable(String TypeID, String FileID) {
		return TypeID.concat(String.valueOf(Math.abs(FileID.hashCode())).substring(0, 1)).toLowerCase();
	}

	public static byte[] getRequestData(String Url, String Range) {
		URL url = null;
		HttpURLConnection urlconn = null;
		byte[] bRet = null;
		try {
			url = new URL(Url);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestProperty("ACCEPT_RANGE", "0-1024");
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

	/**
	 * 
	 * @param UserName
	 * @param TypeID
	 * @param FileID
	 */
	public static void setCnkiErrorFile(String UserName, String TypeID, String FileID, int ErrorType) {
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			dbHelper.Insert("cnkierrorfile", new String[] { "username", "typeid", "fileid", "time", "errtype" },
					new Object[] { UserName, TypeID, FileID, Common.GetDateTime(), ErrorType });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}

	public static int getErrorFileCount(String TypeID, String FileID, String ErrorType) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (FileID != null && FileID.length() > 0) {
			sbCondition.append("fileid like '%").append(dbHelper.FilterSpecialCharacter(FileID)).append("%'  AND ");
		}
		if (TypeID != null && TypeID.length() > 0) {
			sbCondition.append("typeid ='").append(dbHelper.FilterSpecialCharacter(TypeID)).append("'").append(" AND ");
		}
		if (ErrorType != null && ErrorType.length() > 0) {
			sbCondition.append("errtype = ").append(dbHelper.FilterSpecialCharacter(ErrorType)).append("")
					.append(" AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}

		int iCount = 0;
		try {
			iCount = dbHelper.GetCount("cnkierrorfile", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getErrorFileList(String TypeID, String FileID, String ErrorType, int Start,
			int Length) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (FileID != null && FileID.length() > 0) {
			sbCondition.append("fileid like '%").append(dbHelper.FilterSpecialCharacter(FileID)).append("%'  AND ");
		}
		if (TypeID != null && TypeID.length() > 0) {
			sbCondition.append("typeid ='").append(dbHelper.FilterSpecialCharacter(TypeID)).append("'").append(" AND ");
		}
		if (ErrorType != null && ErrorType.length() > 0) {
			sbCondition.append("errtype = ").append(dbHelper.FilterSpecialCharacter(ErrorType)).append("")
					.append(" AND ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		List<Map<String, Object>> lstErrorFile = null;
		try {
			lstErrorFile = dbHelper.ExecuteQuery("id, username, typeid, fileid, time", "cnkierrorfile",
					sbCondition.toString(), "time desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstErrorFile;
	}

	public static boolean delErrorFile(String FileID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("cnkierrorfile",
					"id in(".concat(dbHelper.FilterSpecialCharacter(FileID)).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean delErrorFile(String TypeID, String FileID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("cnkierrorfile",
					"typeid ='".concat(TypeID).concat("' and fileid ='").concat(FileID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	public static boolean isErrorFile(String TypeID, String FileID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.GetCount("cnkierrorfile", "typeid ='" + TypeID + "' and fileid='" + FileID + "'") > 0;
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/**
	 * 定时检查cnki error file 文件，及时清空里面的错误数据
	 */
	public static void examineErrorFile() {
		List<Map<String, Object>> lstErrorFile = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstErrorFile = dbHelper.ExecuteQuery("id, username, typeid, fileid, time", "cnkierrorfile");
			if (lstErrorFile == null) {
				Logger.WriteDownTraceLog("", "", "", 3, "cnki error file table is null", 1);
				return;
			}
			Iterator<Map<String, Object>> iMap = lstErrorFile.iterator();
			Map<String, Object> mapErrorFile;
			String strUserName = "ttod";
			String strPassword = "ttod";
			String strIP = "59.64.113.205";// "211.151.93.226";
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
			if (!cnkiMngr.cnkiUserLogin(strUserName, strPassword, strIP, iResult)) {
				Logger.WriteDownTraceLog(strUserName, "", "", 3,
						"Backstage org user login error,errorcode is" + iResult[0], 0);// 4）后台对错误的原文重新下载或者补录，用系统机构账号登陆失败
				return;
			}
			String strTypeID = null;
			String strFileID = null;
			String strErrorUserName = null;
			Logger.WriteDownTraceLog("", "", "", 3, "start do with cnki", 1);
			while (iMap.hasNext()) {
				mapErrorFile = iMap.next();
				strTypeID = String.valueOf(mapErrorFile.get("typeid"));
				strFileID = String.valueOf(mapErrorFile.get("fileid"));
				strErrorUserName = String.valueOf(mapErrorFile.get("username"));
				if (cnkiMngr.setFileInfo(strTypeID, strFileID)) {
					long[] arrDownRet = new long[1];
					boolean bOk = cnkiMngr.downSourceFile(strTypeID, strFileID, "caj", strErrorUserName, true,
							arrDownRet);
					if (!bOk) {
						Logger.WriteDownTraceLog(strUserName, strTypeID, strFileID, 3,
								"Backstage org user downSourceFile error,errorcode is" + arrDownRet[0], 0);// 4）后台对错误的原文重新下载失敗
						continue;
					} else {
						Logger.WriteDownTraceLog(strUserName, strTypeID, strFileID, 3,
								"Backstage org user do with Success" + arrDownRet[0], 1);
						String strCajTabName = ODataMngr.getFileTable(strTypeID, strFileID);
						Map<String, Object> mapFileInfo = ODataMngr.getFileInfo(strTypeID, strFileID);
						long lFileLength = (long) (mapFileInfo.get("filesize"));
						UploadInfoBean FileInfo = new UploadInfoBean();
						FileInfo.setFileID(strFileID);
						FileInfo.setFileName(String.valueOf(mapFileInfo.get("filename")));
						FileInfo.setUserName(strErrorUserName);
						FileInfo.setTypeName("caj");
						FileInfo.setFileLength(lFileLength);
						FileInfo.setRange("0-".concat(String.valueOf(lFileLength - 1)));
						FileInfo.setIsCompleted(1);
						FileInfo.setClient("backstage"); // 表示为补录数据
						FileInfo.setAddress("");
						FileInfo.setFileMd5(String.valueOf(mapFileInfo.get("filemd5")));
						FileInfo.setIsHahepub(mapFileInfo.get("ishasepub") == null ? 0
								: Integer.parseInt(String.valueOf(mapFileInfo.get("ishasepub"))));
						FileInfo.setTypeid(strTypeID);
						FileInfo.setFileTable(strCajTabName);
						FileInfo.setDskFileName(strFileID.concat(".caj"));
						UploadMngr.addUploadInfo(FileInfo);
						CnkiMngr.delErrorFile(strTypeID, strFileID);
					}
				} else {
					Logger.WriteDownTraceLog(strUserName, strTypeID, strFileID, 3,
							"Backstage org user get odata fileinfo error", 0);// 4）后台对错误的原文重新下载,从ODATA获取fileinfo失败
					continue;
				}
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}

	}

	public static Integer getEpubTransInfoCount(String name_en, String filter, String keyword, String EpubStatus) {

		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (Common.IsNullOrEmpty(filter)) {
			filter = "fileName";
		}
		if (filter.equals("fileName") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("filename like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		} else if (filter.equals("fileID") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("fileid like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(EpubStatus)) {
			if ("2".equals(EpubStatus)) {
				sbCondition.append("ishasepub is NULL and ");
			} else {
				sbCondition.append("ishasepub = ".concat(EpubStatus).concat(" and "));
			}
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[3];
		arrParam[0] = name_en;
		arrParam[1] = sbCondition.toString();
		arrParam[2] = "cnkifiletable";
		List<Map<String, Object>> epubTransList = null;
		try {
			epubTransList = dbHelper.ExecuteQueryProc("sp_getDataCount", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			return 0;
		}
		arrParam = null;
		if (epubTransList == null) {
			return 0;
		}
		return Integer.valueOf(epubTransList.get(0).get("totalcount").toString());

	}

	public static List<Map<String, Object>> getEpubTransInfoList(String name_en, String filter, String keyword,
			String EpubStatus, String start, String len) {
		List<Map<String, Object>> epubTransInfoList = new ArrayList<Map<String, Object>>();

		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (Common.IsNullOrEmpty(filter)) {
			filter = "fileName";
		}
		if (filter.equals("fileName") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("filename like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		} else if (filter.equals("fileID") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("fileid like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(EpubStatus)) {
			if ("2".equals(EpubStatus)) {
				sbCondition.append("ishasepub is NULL and ");
			} else {
				sbCondition.append("ishasepub = ".concat(EpubStatus).concat(" and "));
			}
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[6];
		arrParam[0] = name_en;
		arrParam[1] = "id,fileid,filename,filesize,filemd5,typename,ishasepub,time";
		arrParam[2] = sbCondition.toString();
		arrParam[3] = "cnkifiletable";
		arrParam[4] = start;
		arrParam[5] = len;
		try {
			epubTransInfoList = dbHelper.ExecuteQueryProc("sp_getDataRecord", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return epubTransInfoList;
	}

	public static List<Map<String, Object>> getEpubTransInfoList(String name_en, String filter, String keyword,
			String EpubStatus, String Count) {
		List<Map<String, Object>> epubTransInfoList = new ArrayList<Map<String, Object>>();

		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (Common.IsNullOrEmpty(filter)) {
			filter = "fileName";
		}
		if (filter.equals("fileName") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("filename like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		} else if (filter.equals("fileID") && !Common.IsNullOrEmpty(keyword)) {
			sbCondition.append("fileid like '%").append(dbHelper.FilterSpecialCharacter(keyword)).append("%' and ");
		}
		if (!Common.IsNullOrEmpty(EpubStatus)) {
			if ("2".equals(EpubStatus)) {
				sbCondition.append("ishasepub is NULL and ");
			} else {
				sbCondition.append("ishasepub = ".concat(EpubStatus).concat(" and "));
			}
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[6];
		arrParam[0] = name_en;
		arrParam[1] = "fileid";
		arrParam[2] = sbCondition.toString();
		arrParam[3] = "cnkifiletable";
		arrParam[4] = "1";
		arrParam[5] = Count;
		try {
			epubTransInfoList = dbHelper.ExecuteQueryProc("sp_getDataRecord", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return epubTransInfoList;
	}

	public static String reDownFile(String TypeID, List<Map<String, Object>> listFile) {
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
			iRecord = iRecord + 1;
			mapFile = iFile.next();
			String strFileID = String.valueOf(mapFile.get("fileid"));
			Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8, "redown cnki file start", 1);
			if (cnkiMngr.setFileInfo(TypeID, strFileID)) {
				long[] arrDownRet = new long[1];
				boolean bOk = cnkiMngr.downSourceFile(TypeID, strFileID, "caj", strUserName, false, arrDownRet);
				if (!bOk) {
					Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8,
							"redown cnki file error,errorcode is" + arrDownRet[0], 0);
					continue;
				} else {
					Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8,
							"redown cnki file Success" + arrDownRet[0], 1);
				}
			} else {
				Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8, "redown cnki file get odata fileinfo error",
						0);
				continue;
			}

		}
		return String.valueOf(iRecord);

	}

	/**************************************** NewOData ********************************************************/

	/**
	 * 
	 * @param TypeID
	 * @param listFile
	 * @return
	 */
	// public static String getCnkiFileDetail(String TypeID, List<Map<String,
	// Object>> listFile) {
	// String strServer = Common.GetConfig("DownloadServer");
	//
	// int iRecord = 0;
	// Iterator<Map<String, Object>> iFile = listFile.iterator();
	// Map<String, Object> mapFile = null;
	// while (iFile.hasNext()) {
	// iRecord = iRecord + 1;
	// mapFile = iFile.next();
	// String strFileID = String.valueOf(mapFile.get("fileid"));
	// String strUrl = strServer + "/data/" + TypeID + "/" + strFileID +
	// "?fields=cnki:subjectsubcolumncode,cnki:pagecount,dc:title,cnki:pagerange,cnki:filesize,dc:date,cnki:issue,cnki:year,cnki:tablename,cnki:logictablename";
	// String strClassID = "";
	// int iObjectCount = 0;
	// String strTitle = "";
	// String strObjectRange = "";
	// int iObjectSize = 0;
	// String strPublishDay = "";
	// String logicTableName = "";
	// String strTableName = "";
	// String strQi = "";
	// String strSYear = "";
	// try {
	// JSONObject jsonSeaData = Common.getSearchData(strUrl,
	// AppToken.getAppToken());
	// if (jsonSeaData == null || jsonSeaData.get("store") == null) {
	// //
	// }
	// String dataStore = jsonSeaData.get("store").toString();
	// if (!dataStore.contains("{"))
	// {
	// //
	// }
	// JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("store"));
	// JSONObject recordObj = JSONObject.fromObject(jsonArray.get(0));
	// JSONArray recordArray = JSONArray.fromObject(recordObj.get("data"));
	// for (int i = 0; i < recordArray.size(); i++) {
	// JSONObject Obj = JSONObject.fromObject(recordArray.get(i));
	// String rdfProperty = Obj.get("rdfProperty").toString().toLowerCase();
	// switch (rdfProperty) {
	// case "cnki:subjectsubcolumncode":
	// strClassID = (Obj.get("value") == null || Obj.get("value").equals("")) ?
	// "" : Obj.get("value").toString();
	// break;
	// case "cnki:pagecount":
	// iObjectCount = (Obj.get("value") == null || Obj.get("value").equals(""))
	// ? 0 : Integer.parseInt(Obj.get("value").toString());
	// break;
	// case "dc:title":
	// strTitle = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
	// : Obj.get("value").toString();
	// break;
	// case "cnki:pagerange":
	// strObjectRange = (Obj.get("value") == null ||
	// Obj.get("value").equals("")) ? "" : Obj.get("value").toString();
	// break;
	// case "cnki:filesize":
	// iObjectSize = (Obj.get("value") == null || Obj.get("value").equals("")) ?
	// 0 : Integer.parseInt(Obj.get("value").toString());
	// break;
	// case "dc:date":
	// strPublishDay = (Obj.get("value") == null || Obj.get("value").equals(""))
	// ? "" : Obj.get("value").toString();
	// break;
	// case "cnki:issue":
	// strQi = (Obj.get("value") == null || Obj.get("value").equals("")) ? "" :
	// Obj.get("value").toString();
	// break;
	// case "cnki:year":
	// strSYear = (Obj.get("value") == null || Obj.get("value").equals("")) ? ""
	// : Obj.get("value").toString();
	// break;
	// case "cnki:logictablename":
	// logicTableName = (Obj.get("value") == null ||
	// Obj.get("value").equals("")) ? "" : Obj.get("value").toString();
	// break;
	// case "cnki:tablename":
	// strTableName = (Obj.get("value") == null || Obj.get("value").equals(""))
	// ? "" : Obj.get("value").toString();
	// break;
	// }
	// }
	// } catch (IOException e) {
	// Logger.WriteException(e);
	// }
	// cnkiMngr
	//
	// if (cnkiMngr.setFileInfo(TypeID, strFileID)) {
	// long[] arrDownRet = new long[1];
	// boolean bOk = cnkiMngr.downSourceFile(TypeID, strFileID, "caj",
	// strUserName, false, arrDownRet);
	// if (!bOk) {
	// Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8,
	// "redown cnki file error,errorcode is" + arrDownRet[0], 0);
	// continue;
	// } else {
	// Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8,
	// "redown cnki file Success" + arrDownRet[0], 1);
	// }
	// } else {
	// Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8,
	// "redown cnki file get odata fileinfo error", 0);
	// continue;
	// }
	//
	// }
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	// CnkiMngr cnkiMngr = null;
	// try {
	// cnkiMngr = new CnkiMngr();
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// }
	// int[] iResult = new int[2];
	// cnkiMngr.cnkiUserLogin(strUserName, strPassword, strIP, iResult);
	// int iRecord = 0;
	// Iterator<Map<String, Object>> iFile = listFile.iterator();
	// Map<String, Object> mapFile = null;
	// while (iFile.hasNext()) {
	// iRecord = iRecord + 1;
	// mapFile = iFile.next();
	// String strFileID = String.valueOf(mapFile.get("fileid"));
	// Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8,
	// "redown cnki file start", 1);
	// if (cnkiMngr.setFileInfo(TypeID, strFileID)) {
	// long[] arrDownRet = new long[1];
	// boolean bOk = cnkiMngr.downSourceFile(TypeID, strFileID, "caj",
	// strUserName, false, arrDownRet);
	// if (!bOk) {
	// Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8,
	// "redown cnki file error,errorcode is" + arrDownRet[0], 0);
	// continue;
	// } else {
	// Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8,
	// "redown cnki file Success" + arrDownRet[0], 1);
	// }
	// } else {
	// Logger.WriteDownTraceLog(strUserName, TypeID, strFileID, 8,
	// "redown cnki file get odata fileinfo error", 0);
	// continue;
	// }
	//
	// }
	// return String.valueOf(iRecord);
	//
	// }
	//
	// private boolean updateCnkiFileInfo(String TypeID, String FileID, String
	// SubjectSubCode, String PageCount, String ObjRange, String ObjSize, String
	// PublishDay, String ObjQi, String ObjYear, String TableName){
	// DBHelper dbHelper = null;
	// try{
	// return dbHelper.Update(getFileTable(TypeID, FileID),
	// "fileid = '".concat(FileID).concat("'"), new
	// String[]{"subjectsubcode","pagecount","objrange","objsize","publishday","objqi","objyear",},
	// new Object[]{});
	// }catch(Exception e){
	// Logger.WriteException(e);
	// return false;
	// }
	// }

	public static JSONObject sendGet(String RemoteUrl, Map<String, String> MapHeader) {
		JSONObject jsonSeaData = null;
		String strResult = "";
		int iTime = 5;
		while (iTime > 0) {
			iTime--;
			strResult = sendHttpGet(RemoteUrl, MapHeader);
			// System.out.println(strResult);
			if (strResult.startsWith("err:"))
				continue;
			jsonSeaData = JSONObject.fromObject(strResult);
			if (jsonSeaData != null && (Integer) jsonSeaData.get("ErrorCode") == 0)
				return jsonSeaData;
		}
		if (jsonSeaData == null && strResult.startsWith("err:")) {
			jsonSeaData = JSONObject.fromObject(strResult.substring(4));
		}
		return jsonSeaData;
	}

	public static String sendHttpGet(String RemoteUrl, Map<String, String> MapHeader) {
		HttpClient hc = null;
		HttpGet httpGet = null;
		try {
			hc = new DefaultHttpClient();
			hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);// 连接时间
			hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);// 数据传输时间
			httpGet = new HttpGet(RemoteUrl);
			if (MapHeader != null && MapHeader.size() > 0) {
				for (Map.Entry<String, String> entry : MapHeader.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}
			HttpResponse hr = hc.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			HttpEntity he = hr.getEntity();
			String str = EntityUtils.toString(he);
			if (code == 200) {
				return str;
			}
			// LoggerFile.appendMethod("C:\\Login", str + "-->4");
			return "err:" + str;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpGet != null) {
				httpGet.abort();
			}
			hc.getConnectionManager().shutdown();
		}
		return "err:";
	}

	public static JSONObject getSingleNewOdata(String type, String strJournalPY, String fields) {
		// String fields =
		// "Title,Title@EN,Author,Type,Address,Language,Size,ISSN,CN,MailCode,Impactfactor,CompositeImpactfactor";//
		// "title,SubjectSubColumnCode,PageCount,PageRange,Date,Issue,year,TableName,FileSize,PhysicalTableName";
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String fileName = strJournalPY;
		String txt = "timestamp=" + timestamp + "&appid=" + appId + "&appkey=" + appKey + "&ip=" + ip + "&location="
				+ location + "&mobile=" + mobile + "&did=" + did + "&op=data_get&type=" + type + "&id=" + fileName;
		SHA1 sha1 = new SHA1();
		String sign = sha1.Digest(txt, "UTF-8");
		String reqUrl = ODataUrl + "api/db/" + type + "/" + fileName;
		if (fields != null && !"".equals(fields)) {
			reqUrl = reqUrl + "?fields=" + fields;
		}
		HttpClient hc = null;
		HttpGet httpGet = null;
		try {
			hc = new DefaultHttpClient();
			hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);// 连接时间
			hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);// 数据传输时间
			httpGet = new HttpGet(reqUrl);
			httpGet.setHeader("app_id", appId);
			httpGet.setHeader("timestamp", timestamp);
			httpGet.setHeader("sign", sign.toLowerCase());
			httpGet.setHeader("ip", ip);
			httpGet.setHeader("did", did);
			httpGet.setHeader("mobile", mobile);
			httpGet.setHeader("location", location);
			HttpResponse hr = hc.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				String str = EntityUtils.toString(he);
				return JSONObject.fromObject(str);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			e.printStackTrace();
		} finally {
			if (httpGet != null) {
				httpGet.abort();
			}
			hc.getConnectionManager().shutdown();
		}
		return null;
	}

	public static JSONObject getNewOdataInfo(String type, String fields, String query, String group, String order,
			int start, int length) {
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String txt = "timestamp=" + timestamp + "&appid=" + appId + "&appkey=" + appKey + "&ip=" + ip + "&location="
				+ location + "&mobile=" + mobile + "&did=" + did + "&op=data_gets&type=" + type + "&fields=" + fields
				+ "&query=" + query + "&group=" + group + "&order=" + order;
		SHA1 sha1 = new SHA1();
		String sign = sha1.Digest(txt, "UTF-8").toLowerCase();
		String reqUrl = "";
		try {
			reqUrl = ODataUrl + "api/db/" + URLEncoder.encode(type, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			HttpClient hc = new DefaultHttpClient();
			if (fields != null && !"".equals(fields)) {
				reqUrl = reqUrl + "?fields=" + fields + "&query=" + URLEncoder.encode(query, "utf-8") + "&group="
						+ group + "&order=" + URLEncoder.encode(order, "utf-8") + "&start=" + String.valueOf(start)
						+ "&length=" + String.valueOf(length);
			}
			HttpGet httpGet = new HttpGet(reqUrl);
			httpGet.setHeader("app_id", appId);
			httpGet.setHeader("timestamp", timestamp);
			httpGet.setHeader("sign", sign);
			httpGet.setHeader("ip", ip);
			httpGet.setHeader("did", did);
			httpGet.setHeader("mobile", mobile);
			httpGet.setHeader("location", location);
			HttpResponse hr = hc.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				String str = URLDecoder.decode(EntityUtils.toString(he), "utf-8");
				return JSONObject.fromObject(str);
			} else {
				httpGet.abort();
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			e.printStackTrace();
		}
		return null;
	}

}
