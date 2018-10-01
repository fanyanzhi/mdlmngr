package BLL;

import java.util.List;
import java.util.Map;

import Util.Common;

import DAL.DBHelper;
import Model.MobileRightStatus;

public class SysConfigMngr {
	public enum ERROR_CODE {
		NO_ERROR(0), ERROR_APPID(-1000), ERROR_TOKEN(-1001), ERROR_CONNECTIONAUTHSERVER(-1002), 
		ERROR_USERNAMEORPASSWORD(-1003), ERROR_ACCESSDATEBASE(-1004), ERROR_ACTION(-1005), ERROR_PARAMETERS(-1006),
		ERROR_NO_DATA(-1007), ERROR_USEREXIST(-1008), ERROR_DELETEDATA(-1009), ERROR_UPDATEDATA(-1010),
		ERROR_CLIENTOVERFULL(-1011), ERROR_REGISTER(-1012), ERROR_DUPPRIMKEYS(-1014),
		ERROR_ORGLOGIN(-1015), ERROR_DBCODE(-1016), ERROR_BINDPHONE(-1017), ERROR_BINDPERSONALUSER(-1018),
		ERROR_TIMEOUT(-1019), ERROR_ACCESSTOKEN(-1020), ERROR_COMMONMODULEFIELDS(-1101), ERROR_PRIMKEYSNOTEXIST(-1102),
		ERROR_ADDUPLOADINFO(-1201), ERROR_CHECKUPLOADSTATUS(-1202), ERROR_UPLOADFILE(-1203), ERROR_WRITEUPLOADFILE(-1204),
		ERROR_DELETEUPLOADINFO(-1205), ERROR_FILENOTEXIST(-1206), ERROR_DOWNLOADFILE(-1207), ERROR_UPDATERANGE(-1208), 
		ERROR_FILEHASEXIST(-1209), ERROR_CREATEFILEFOLDER(-1301), ERROR_OPENFILE(-1302), ERROR_OPENFILENULL(-1303),
		ERROR_WRITEFILE(-1304), ERROR_ODATATOKEN(-1401), ERROR_ODATAFILEDOWN(-1402), ERROR_CHANGEEPUB(-1403),
		ERROR_INTEGRITY(-1404), ERROR_FILEIDORUSERNAME(-1405), ERROR_ODATADOWNURL(-1406), ERROR_ODATAXML(-1407),
		ERROR_ODATAFILEINFO(-1408), ERROR_URLTIME(-1501), ERROR_FULLFILE(-1502), ERROR_SOURCEFILE(-1503),
		ERROR_FEEFILE(-1504), ERROR_SECRECYFILE(-1505), ERROR_ODATAFILE(-1506), ERROR_IPLOGIN(-1601),
		ERROR_ACCESSRIGHT(-1701), ERROR_BALANCE(-1702), ERROR_NOUSING(-1703), NO_DEVICETOKEN(-1801),
		ERROR_CONNECT(-2001), Max_DownLoad(-2002), ERROR_BINDINFO(-2003),ERROR_BINDPERSONAL(-2004),
		ERROR_OUTMAXSIZE(-2101),ERROR_IMAGEMAXCOUNT(-2102),ERROR_CLAIMED(2201),
		ERROR_VALIDATECODE(-3001),ERROR_INCORRECTCHARACTERS(-3002),ERROR_INCONSISTENT(-3003);//重置密码
		public int code;

		ERROR_CODE(int arg) {
			code = arg;
		}
	};

	public static Map<String, Object> getConfigValueAndTime(String Key) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("value,time", "sysconfig", "name='".concat(Key).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return null;
		}
		return lstInfo.get(0);
	}

	public static int getConfigCount() {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("sysconfig", "");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}
	
	public static int getMobileRight(){
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("value,time", "sysconfig", "name='mobileright'");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null || lstInfo.size() == 0) {
			return 0;
		}
		return Integer.parseInt(String.valueOf(lstInfo.get(0).get("value")));
	}

	public static List<Map<String, Object>> getConfigList(int Start, int Length) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("id,name,value,time", "sysconfig", "", "time desc", Start, Length);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstInfo;
	}

	public static Map<String, Object> getConfigInfo(String ID) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("name,value,time", "sysconfig", "id='".concat(dbHelper.FilterSpecialCharacter(ID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return null;
		}
		return lstInfo.get(0);
	}

	public static boolean addConfigInfo(String Name, String Value) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("sysconfig", new String[] { "name", "value", "time" }, new Object[] { Name, Value, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean updateConfigInfo(String ID, String Name, String Value) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update("sysconfig", "id='".concat(dbHelper.FilterSpecialCharacter(ID)).concat("'"), new String[] { "name", "value", "time" }, new Object[] { Name, Value, Common.GetDateTime() });
			if("mobileright".equalsIgnoreCase(Name)){
				MobileRightStatus.updateMobileRight(Value);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean deleteConfigInfo(String IDs) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("sysconfig", "id in (".concat(IDs).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}
}
