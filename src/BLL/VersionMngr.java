package BLL;

import java.util.List;
import java.util.Map;

import Model.VersionBean;
import Util.Common;

import DAL.DBHelper;

public class VersionMngr {
	public static VersionBean getForceVersion(String ClientName) {
		VersionBean verBean = null;
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("client,version, versionname, apkurl", "version",  "type=1 and client='".concat(ClientName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return null;
		}
		verBean = new VersionBean(lstInfo.get(0));
		return verBean;
	}

	public static boolean addForceVersion(VersionBean verBean) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("version", new String[] { "client", "version", "versionname", "apkurl", "type", "time" }, new Object[] { verBean.getClient(), verBean.getVersion(), verBean.getVersionName(), verBean.getApkUrl(), verBean.getType(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean updateForceVersion(VersionBean verBean) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update("version", "client='".concat(verBean.getClient()).concat("'"), new String[] { "version", "versionname", "apkurl", "type", "time" }, new Object[] { verBean.getVersion(), verBean.getVersionName(), verBean.getApkUrl(), verBean.getType(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean updateForceVersion(String Version, String ClientName, int ID) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update("version", "id=".concat(String.valueOf(ID)), new String[] { "version", "client", "time" }, new Object[] { Version, ClientName, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean deleteForceVersion(int ID) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("version", "id=".concat(String.valueOf(ID)));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static List<Map<String, Object>> getForceVersionList() {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("id,client,version,versionname, apkurl,time", "version", "type=1");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstInfo;
	}
}
