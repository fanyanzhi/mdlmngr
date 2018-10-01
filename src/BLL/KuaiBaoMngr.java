package BLL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

import Util.Common;
import Util.HuaWeiPush;
import Util.UmengSender;
import Util.XiaoMiPush;
import javapns.devices.Devices;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import DAL.DBHelper;

public class KuaiBaoMngr {

	public static String checkAttention(String userName, String code) {
		String strRet = "0";
		DBHelper dbHelper = null;
		String strTableName = getTableName("userkuaibao", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount(strTableName,
					"username = '".concat(userName).concat("' and sortcode='").concat(code).concat("'")) > 0) {
				strRet = "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strRet;
	}

	public static boolean addAttention(String userName, String code) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userkuaibao", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			if (checkAttention(userName, code).equals("0")) {
				bRet = dbHelper.Insert(strTableName, new String[] { "username", "sortcode", "time" },
						new Object[] { userName, code, Common.GetDateTime() });
			} else {
				bRet = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static boolean concalAttention(String userName, String code) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userkuaibao", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(strTableName, "username='" + dbHelper.FilterSpecialCharacter(userName)
					+ "' and sortcode='" + dbHelper.FilterSpecialCharacter(code) + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static boolean delAttention(String userName, String id) {
		Boolean bRet = false;
		DBHelper dbHelper = null;
		String strTableName = getTableName("userkuaibao", userName);
		try {
			dbHelper = DBHelper.GetInstance();
			bRet = dbHelper.Delete(strTableName, "username='" + dbHelper.FilterSpecialCharacter(userName)
					+ "' and id in('" + id.replace(",", "','") + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	public static int getAtionSubjectCount(String userName) {
		int iCount = 0;
		String strTableName = getTableName("userkuaibao", userName);
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount(strTableName, "username ='" + dbHelper.FilterSpecialCharacter(userName) + "'");
		} catch (Exception e) {
			// Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getAtionSubjectList(String userName) {
		String strTableName = getTableName("userkuaibao", userName);
		List<Map<String, Object>> lstAtionSubject = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			lstAtionSubject = dbHelper.ExecuteQuery("id,sortcode,name,pathname,time",
					"(select tab1.id,tab1.sortcode,tab1.time,tab2.`name`,tab2.pathname from " + strTableName
							+ " tab1 LEFT JOIN zjcls tab2 ON tab1.sortcode=tab2.`code`  where tab1.username='"
							+ userName + "')tab",
					"", "time desc");
		} catch (Exception e) {
			// Logger.WriteException(e);
		}
		return lstAtionSubject;
	}

	private static String getTableName(String SourceTable, String UserName) {
		return SourceTable.concat(String.valueOf(Math.abs(Common.EnCodeMD5(UserName).hashCode())).substring(0, 1));

	}

	public static int getKuaiBaoCount(String sortcode, String UserName, String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (!Common.IsNullOrEmpty(sortcode)) {
			sbCondition.append("sortcode = '").append(dbHelper.FilterSpecialCharacter(sortcode)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
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
		Object[] arrParam = new Object[3];
		arrParam[0] = "userkuaibao";
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

	public static List<Map<String, Object>> getKuaiBaoList(String sortcode, String UserName, String StartDate,
			String EndDate, int Start, int Length) {
		List<Map<String, Object>> lstFile = null;
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (!Common.IsNullOrEmpty(sortcode)) {
			sbCondition.append("sortcode = '").append(dbHelper.FilterSpecialCharacter(sortcode)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
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
		arrParam[0] = "userkuaibao";
		arrParam[1] = "id,username,sortcode,time";
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

	public static String getKuaiBaoUserCount(String sortcode, String UserName, String StartDate, String EndDate) {
		StringBuilder sbCondition = new StringBuilder();
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (!Common.IsNullOrEmpty(sortcode)) {
			sbCondition.append("sortcode = '").append(dbHelper.FilterSpecialCharacter(sortcode)).append("' and ");
		}
		if (!Common.IsNullOrEmpty(UserName)) {
			sbCondition.append("username like '%").append(dbHelper.FilterSpecialCharacter(UserName)).append("%' and ");
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
		int allCount = 0;

		List<Map<String, Object>> lstCount = null;
		for (int i = 0; i < 10; i++) {
			try {
				lstCount = dbHelper.ExecuteQuery("count(DISTINCT username) cut", "userkuaibao" + i,
						sbCondition.toString());
			} catch (Exception e) {

			}
			allCount = allCount + Integer.parseInt(lstCount.get(0).get("cut").toString());
		}
		return String.valueOf(allCount);
	}

	public static void main(String[] args) {
		List<String> devicef = new ArrayList<String>();
		devicef.add("2e7ae65e450d3d861d8c989371cbcaac02432b8f6e086d1791e194196361552a");
		devicef.add("92208c54bded9b53b8db435b0104f25f19b380e8a1100339007f8156c6960050");
		devicef.add("8869679ece84b01af09462d9dce5e7c462c30e69219a230ab77653cc3230ad14");
		devicef.add("1d76f31e6390afa8d519017017132666225fe05459d61ea334391d7920b3cb03");
		devicef.add("0e1007d93b13bf25980eac6571c79ad75762d908dcc9c3d32fc8f072835282b3");
		devicef.add("4afd0440705edc2e709e0dcaff8ed312d57c616ab391e226dc8dbc0aad06b610");
		devicef.add("dbcae8c040f50f601bd61ded92c5a368424ea57d8c3d388a3022f44e0bfe0be0");
		devicef.add("b3c4dde0031de97324183291b69f96930ef2eb19ea6e2cc5c8401ccefe8087bc");
		devicef.add("9f56baf3413dc62a98f9f6ab1581b43fe7baa141d008464f3a71e6c881d56abe");
		devicef.add("9a82eb9dd25c399b031cf6f1afc738ef299c12665a733656e31704a80ae84013");
		devicef.add("e570ef5388bf8542cb3fa72095566e57626b3fcde737c09f2d23bef3d4f08509");
		devicef.add("ce5e05fa2f05fbf543cbc7f65a5858fc204bde9af70b472a9a9d0f51bb3ba383");
		devicef.add("0e7bdd039f3b90a8983e79c44451a867a8bf241fea31364c91bc53d4cf1f1539");
		devicef.add("1d0d0c1324839ce355f472207603a87c1b78462989a0514cd69ad0591695cd4d");
		devicef.add("b22bcd82299bf7b1e30025afe7d5bc7fc6d00d861e463465e2996d6348d354f3");
		devicef.add("233e89cb00caef90e610d7bf0ec8200780c322e44876d173cff7689a4b11d550");
		devicef.add("990f9d5ef94015a2875bb38064e96e0979e2d00f4d0084c4dbeff7ed00a630aa");
		devicef.add("ed75567b5c1fc269846207dd79ccfd2a6214c88bce2c8020ae23242de8d7b75e");
		devicef.add("9070149da684b37bcab229452d3763507dfd769d5bc75fb046deff40838f0c79");
		devicef.add("6f74f3b4a6bed347ee7bd4a701de9310dbbed678366405632771e8585670729b");
		devicef.add("3acd4be95d9b5b49dfedc3a7d504c776a1cced20e6daec3756a9ad5494ad6c9a");
		devicef.add("912bf8b2e03e2c078aa3578ce399904691e4908c36509a5293940e171d22aacc");
		devicef.add("08f5a0c23c08b4ac965769290f22720fca768ef1c376171c9d129036973a592c");
		devicef.add("2ea6ef1f1d0bbd07eedd43c2ba0acc8044f1f5233977afd7a7337e4749a469f4");
		devicef.add("42fe8450b74b49d8e9bbc75ee40f67db51536e67b2ddd1b078c620975c2358fc");
		devicef.add("3839a49568334a646810d3f7bb22c873c4e071f64321afe72af54ed1d3e78b1a");
		devicef.add("2e9066d2de70b12f22c54c5751d2e841a15a5cbbcc6cd6488d32cc366d3bdf9b");
		devicef.add("1ddf82bc5889a83fabe65d1f33386c1e0d8f8838a0509136099c6cb094ae2d45");
		devicef.add("20b2bc94621f741acdfe4c4b54310bffff137ebebba9982259f0ab1ff4b74769");
		devicef.add("fd4d5ed847cabe22b17275e1df48d7634cea03cdefc42559422acfb3c3a0faad");
		devicef.add("8b108da5acb492e7a3cc96025a248c50f9db166a50046b3c682cfa27bb70a2b8");
		devicef.add("e3dd80db028845a1447bef19bef46575c14e67fac2fd6bd016ac57c45d390e28");
		devicef.add("ed8ecd98e7e95b84a610a748c1c8fd8eb3f75551714f43c5b7646fd2e98f9843");
		devicef.add("ded92fdd9ee6cd3c3108dae341609668469f68c42f2883c69ee6cb6d7d686cb1");
		devicef.add("000c058287db1df66cafed328d68d6d7fb78be2029c1576cd1e021fabab97116");
		devicef.add("730750c9ab74c027140423ff4a651360d15cff7738245c3610d6977d8bf91ff0");
		devicef.add("d7b35af8f8b37e01d2f73992c84082c21b153fb234ac553123d4f30289ce58b0");
		devicef.add("19439d936dfece329906c22f1c76cf272dc60ca6f85d1f440422136870a2fcc0");
		devicef.add("9d9947b8f9849c88464c5638b6d5a1b65fbb8830e0d13bc37aec371943be4719");
		devicef.add("59ee9129da00946da0f281987518d0a861d3e3f23923771ff4fe8264b3ae8114");
		devicef.add("da5d27cf1460a6570531b50fb700de98b1208aea7401600ee238f6ec11d0cddb");
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("code", "128010122");
		JdpushMngr.iosPushKuaiBao(devicef, "您定制的'几何拓扑'今日更新1篇文献", "1", "128010122");
		
		try {
			Thread.currentThread().sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// jPushKuaiBao();
		// for(int i=0;i<100;i++){
		// new Thread() {
		// public void run() {
		List<String> devices = new ArrayList<String>();
		 //devices.add("aae17e2423952b7e112d81fff2bc626ad293291cb3c407d0defea69362f3f0fa");
		// devices.add("9064f2708b65b1a8ee863486df7582ca8f7605ee2be9d9a27f775b4f7c5d9c4e");
		// // -->孟正式的
		devices.add("da5d27cf1460a6570531b50fb700de98b1208aea7401600ee238f6ec11d0cddb");//iphoe6
		devices.add("aae17e2423952b7e112d81fff2bc626ad293291cb3c407d0defea69362f3f0fa");
		/*devices.add("fe13745484299294a168f165d8deb35805ac96673e658f5dd5120c30e1e1f448");
		devices.add("f902488836b01d689d0fb088f7521fc6b5d1af0a3ce6291a107d8076984bd6c4");
		devices.add("f97381c16452143a2d1b9ecc74111e6ccf210650790c99c83ff35076032525f5");
		devices.add("030e967cffe5122ac759c870d4b97f0557e3279d46f5bd3a4a643108afc6fb52");
		devices.add("c4f7114fe0945344645c8394b0e839f8ff2a85d96ea6c5d366a8d084342d0acc");
		devices.add("b13c8888ee40f71cddd8db3f4ac6eb831164ed716c114a8b4df61ceb8eec6791");
		devices.add("9225bb46e4ca578a5b67bef84cb467a15c9bb914c32aee33dcccb7bde43180c3");
		devices.add("2cd8e0f1fe38ccd525512aa46c387ca813fa5db7ed7fc91fe689a75784b824f3");*/
		Map<String, String> map = new HashMap<String, String>();
		map.put("code", "128010122");
		JdpushMngr.iosPushKuaiBao(devices, "您定制的'几何拓扑'今日更新1篇文献", "1", "128010122");
		// }
		// }.start();
		// }
		// jPushKuaiBao();

		/*
		 * List<String> devices = new ArrayList<String>();
		 * devices.add("EOdG88KZZ604S8n1VFeAahtM6jZ8KMTqe1uFRTtIBac="); String
		 * content = "您定制的'声学'今日更新3篇文献"; Map<String,String> map = new
		 * HashMap<String,String>(); map.put("code", "128010202"); try {
		 * XiaoMiPush.sendKuaiBaoMessage("全球学术快报", content, "", devices, "1",
		 * map); } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (ParseException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		
		
		/*String content = "您定制的'放射化学'今日更新1篇文献";
		List<String> devices = new ArrayList<String>();
		devices.add("0869394028846274300000647900CN01");
		Map<String, String> map = new HashMap<String, String>();
		map.put("code", "128014908");
		try {
			HuaWeiPush.pushKuaiBaoMsg("全球学术快报", content, devices, "1", map);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*推送单篇*/
		/*List<String> devices = new ArrayList<String>();
		devices.add("0869394028846274300000647900CN01");
		Map<String, String> map = new HashMap<String, String>();
		map.put("odatatype", "CJFD");
		map.put("fileid", "gdjr201305023");
		try {
			HuaWeiPush.pushKuaiBaoMsg("全球学术快报", "互联网金融模式及对传统银行业的影响", devices, "2", map);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*推送整刊*/
		/*List<String> devices = new ArrayList<String>();
		devices.add("0869394028846274300000647900CN01");
		Map<String, String> map = new HashMap<String, String>();
		map.put("titlepy", "SHWL");
		map.put("_type", "JournalInfo");
		map.put("year", "2016");
		map.put("dbcode", "");
		map.put("issue", "10");
		try {
			HuaWeiPush.pushKuaiBaoMsg("全球学术快报", "生物化学与生物物理学报", devices, "3", map);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//jPushXkcls();
	}

	public static void jPushXkcls() {
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst = null;
		try {
			dbHelper = DBHelper.GetInstance("Xkcls");
			lst = dbHelper.ExecuteQuery("CODE,NAME,UPDATECOUNT", "xkcls", "GRADE=3 and UPDATECOUNT>0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String code = "";
		String updatecount = "";
		String name = "";
		if (lst != null && lst.size() > 0) {
			Iterator<Map<String, Object>> it = lst.iterator();
			Map<String, Object> map = null;

			while (it.hasNext()) {
				map = it.next();
				code = map.get("code").toString();
				updatecount = map.get("updatecount").toString();
				name = map.get("name").toString();
				if("ios".equals(Common.GetConfig("Brand"))){
					iosPush(code, name, updatecount); // 169 07:00
				}
				if("huawei".equals(Common.GetConfig("Brand"))){
					huaWeiPush(code, name, updatecount); // 171 07:20
				}
				if("xiaomi".equals(Common.GetConfig("Brand"))){
					xiaoMiPush(code, name, updatecount); //173 07:40
				}
				if("other".equals(Common.GetConfig("Brand"))){
					uMengPush(code, name, updatecount); //175 08:00
				}
			}
		}
	}

	public static void jPushKuaiBao() {
		int count = 0;
		int iStart = 0;
		do {
			JSONObject jsonData = ODataHelper.GetObjDataLists("XKCLS",
					"ALLCOUNT,NAME,CODE,GRADE,UPDATECOUNT,UPDATETIME", "UPDATECOUNT>0 and GRADE=3", "CODE desc", "",
					iStart, 128);
			if (jsonData.containsKey("Count")) {
				count = Integer.parseInt(jsonData.get("Count").toString());
				JSONArray rowArray = JSONArray.fromObject(jsonData.get("Rows"));
				iStart = iStart + rowArray.size();
				for (int i = 0; i < rowArray.size(); i++) {
					JSONObject rowObj = JSONObject.fromObject(rowArray.get(i));
					JSONArray cellArray = JSONArray.fromObject(rowObj.get("Cells"));
					String code = "";
					String updatecount = "";
					String name = "";
					for (int j = 0; j < cellArray.size(); j++) {
						JSONObject cellObj = JSONObject.fromObject(cellArray.get(j));
						if (cellObj.getString("Name").equalsIgnoreCase("CODE")) {
							code = cellObj.getString("Value");
						}
						if (cellObj.getString("Name").equalsIgnoreCase("UPDATECOUNT")) {
							updatecount = cellObj.getString("Value");
						}
						if (cellObj.getString("Name").equalsIgnoreCase("NAME")) {
							name = cellObj.getString("Value");
						}
					}
					// writeLog2("code="+code+"-->updatecount="+updatecount+"-->name="+name+"-->iStart="+iStart);
					// iosPush(code, name, updatecount); // 169 07:00
					// huaWeiPush(code, name, updatecount); //171 07:20
					// xiaoMiPush(code, name, updatecount); //173 07:40
					// uMengPush(code, name, updatecount); //175 08:00
				}
			}
		} while (iStart < count);
	}

	public static void writeLog(String folder, String data) {
		String time = Common.GetDate();
		File file = new File("d:\\" + folder + time + ".txt");
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

	public static void writeLog2(String data) {
		File file = new File("d:\\huawei2.txt");
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

	public static String ListToString(List<?> list) {
		StringBuffer sb = new StringBuffer();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == null || list.get(i) == "") {
					continue;
				}
				// 如果值是list类型则调用自己
				if (list.get(i) instanceof List) {
					sb.append(ListToString((List<?>) list.get(i)));
					sb.append("-->");
				} else {
					sb.append(list.get(i));
					sb.append("-->");
				}
			}
		}
		return sb.toString();
	}

	public static void iosPush(final String code, final String name, final String upCount) {
		new Thread() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					List<Map<String, Object>> lst = null;
					final DBHelper dbHelper = DBHelper.GetInstance();
					List<String> devices = new ArrayList<String>();
					for (int n = 0; n < 10; n++) {
						/*
						 * String sql =
						 * "select devicetoken from userdevice where manu='iphone' and username in (select username from userkuaibao"
						 * + n + " where sortcode='" + code + "')";
						 */
						String sql = "select devicetoken from userdevice ud INNER join userkuaibao" + n
								+ " uk on ud.username=uk.username where ud.manu='iphone' and uk.sortcode='" + code
								+ "'";
						lst = dbHelper.ExecuteQuery(sql);
						if (lst != null && lst.size() > 0) {

							StringBuilder sb = new StringBuilder();
							for (Map<String, Object> map : lst) {
								devices.add(map.get("devicetoken").toString());
								sb.append(map.get("devicetoken").toString() + "-->");
								// JdpushMngr.pushMessage(map.get("username").toString(),
								// "您定制的'"+name+"'今日更新"+updatecount+"篇文献",
								// "全球学术快报", "count", code);
							}
							sb.append("您定制的'" + name + "'今日更新" + upCount + "篇文献");
							writeLog("IPHONE",sb.toString()+"-->n="+n);
							/*
							 * if("声学".equals(name)){
							 * writeLog("IPHONE2",sb.toString()+"-->n="+n);
							 */
						}
					}
					String content = "您定制的'" + name + "'今日更新" + upCount + "篇文献";
					this.currentThread().sleep(200);
					JdpushMngr.iosPushKuaiBao(devices, content, "1", code);

				} catch (Exception e) {
					System.out.println(e.getMessage());
					Logger.WriteException(e);
				}

			};
		}.start();
	}

	public static void huaWeiPush(final String code, final String name, final String upCount) {
		new Thread() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					List<Map<String, Object>> lst = null;
					final DBHelper dbHelper = DBHelper.GetInstance();
					for (int n = 0; n < 10; n++) {
						/*
						 * String sql =
						 * "select devicetoken from userdevice where manu='huawei' and username in (select username from userkuaibao"
						 * + n + " where sortcode='" + code + "')";
						 */

						String sql = "select devicetoken from userdevice ud INNER join userkuaibao" + n
								+ " uk on ud.username=uk.username where ud.manu='huawei' and uk.sortcode='" + code
								+ "'";
						//System.out.println(sql);
						//writeLog("huawei", sql);
						lst = dbHelper.ExecuteQuery(sql);
						if (lst != null && lst.size() > 0) {
							List<String> devices = new ArrayList<String>();
							StringBuilder sb = new StringBuilder();
							for (Map<String, Object> map : lst) {
								sb.append(map.get("devicetoken").toString() + "-->");
								devices.add(map.get("devicetoken").toString());
							}
							String content = "您定制的'" + name + "'今日更新" + upCount + "篇文献";
							sb.append(content);
							writeLog("huawei", sb.toString() + "-->n=" + n);
							Map<String, String> map = new HashMap<String, String>();
							map.put("code", code);
							this.currentThread().sleep(200);
							HuaWeiPush.pushKuaiBaoMsg("全球学术快报", content, devices, "1", map);

						}
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					Logger.WriteException(e);
				}

			};
		}.start();
	}

	public static void xiaoMiPush(final String code, final String name, final String upCount) {
		new Thread() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					List<Map<String, Object>> lst = null;
					final DBHelper dbHelper = DBHelper.GetInstance();
					for (int n = 0; n < 10; n++) {
						/*
						 * String sql =
						 * "select devicetoken from userdevice where manu='xiaomi' and username in (select username from userkuaibao"
						 * + n + " where sortcode='" + code + "')";
						 */
						String sql = "select devicetoken from userdevice ud INNER join userkuaibao" + n
								+ " uk on ud.username=uk.username where ud.manu='xiaomi' and uk.sortcode='" + code
								+ "'";
						lst = dbHelper.ExecuteQuery(sql);
						if (lst != null && lst.size() > 0) {
							List<String> devices = new ArrayList<String>();
							StringBuilder sb = new StringBuilder();
							for (Map<String, Object> map : lst) {
								sb.append(map.get("devicetoken").toString() + "-->");
								devices.add(map.get("devicetoken").toString());
							}
							String content = "您定制的'" + name + "'今日更新" + upCount + "篇文献";
							sb.append(content);
							writeLog("xiaomi", sb.toString() + "-->n=" + n);
							Map<String, String> map = new HashMap<String, String>();
							map.put("code", code);
							this.currentThread().sleep(200);
							XiaoMiPush.sendKuaiBaoMessage("全球学术快报", content, "", devices, "1", map);
						}
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					Logger.WriteException(e);
				}
			};
		}.start();
	}

	public static void uMengPush(final String code, final String name, final String upCount) {
		new Thread() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					List<Map<String, Object>> lst = null;
					List<String> devices = new ArrayList<String>();
					final DBHelper dbHelper = DBHelper.GetInstance();
					StringBuilder sb = new StringBuilder();
					for (int n = 0; n < 10; n++) {
						/*
						 * String sql =
						 * "select devicetoken from userdevice where manu='other' and username in (select username from userkuaibao"
						 * + n + " where sortcode='" + code + "')";
						 */
						String sql = "select devicetoken from userdevice ud INNER join userkuaibao" + n
								+ " uk on ud.username=uk.username where ud.manu='other' and uk.sortcode='" + code + "'";
						lst = dbHelper.ExecuteQuery(sql);
						
						if (lst != null && lst.size() > 0) {
							for (Map<String, Object> map : lst) {
								devices.add(map.get("devicetoken").toString());
								sb.append(map.get("devicetoken").toString() + "-->");
							}
							sb.append("您定制的'" + name + "'今日更新" + upCount + "篇文献");
							// String content = "您定制的'" + name + "'今日更新" +
							// upCount + "篇文献";
							//
						}
						// writeLog("umeng",sb.toString()+"-->n="+n);
					}

					String content = "您定制的'" + name + "'今日更新" + upCount + "篇文献";
					sb.append(content);
					writeLog("umeng", sb.toString());
					Map<String, String> map = new HashMap<String, String>();
					map.put("code", code);
					this.currentThread().sleep(200);
					UmengSender.sendKuaiBaoMessage("全球学术快报", "11", content, devices, "1", map);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					Logger.WriteException(e);
				}

			};
		}.start();
	}

}
