package BLL;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Model.OrgImageBean;
import Util.Common;

public class OrgImageMngr {

	public static int addImageInfo(OrgImageBean imageInfo) {
		int iRet = -1;
		try {
			DBHelper dbHelper = DBHelper.GetInstance("Orglib");
			if (dbHelper.Insert("orgactive", new String[] { "appid","title","type","content", "active", "time" }, new Object[] {imageInfo.getAppid(),imageInfo.getTitle(),imageInfo.getType(),imageInfo.getContent(),imageInfo.getActive(),new Timestamp(imageInfo.getTime().getTime())})) {
				iRet = getMaxImageID();
			} else {
				return -1;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iRet;
	}
	public static int getMaxImageID() {
		List<Map<String, Object>> lstImageInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance("Orglib");
			lstImageInfo = dbHelper.ExecuteQuery("max(id) id", "orgactive");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstImageInfo == null) {
			return -1;
		}
		return Integer.parseInt(String.valueOf(lstImageInfo.get(0).get("id")));
	}

	public static List<Map<String, Object>> getOrgActiveList(String appID) {
		List<Map<String, Object>> lstInfo = null;
		StringBuilder sbCondition= new StringBuilder();
		if(!Common.IsNullOrEmpty(appID)){
			sbCondition.append("appid='").append(appID).append("'");
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance("Orglib");
			lstInfo = dbHelper.ExecuteQuery("id,appid,type,title,content,active,time", "orgactive",sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstInfo;
	}
	public static List<Map<String, Object>> getActiveById(int id){
		List<Map<String, Object>> lstImageInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance("Orglib");
			lstImageInfo = dbHelper.ExecuteQuery("active,type,appid,title,content", "orgactive", "id=".concat(String.valueOf(id)));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstImageInfo;
	}
	public static boolean delOrgLogoInfo(String id){
		try {
			DBHelper dbHelper = DBHelper.GetInstance("Orglib");
			return dbHelper.Delete("orgactive", "id="+id);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return false;
		
	}
	
	public static int updateImageInfo(OrgImageBean imageInfo){
		int iRet = -1;
		try {
			DBHelper dbHelper = DBHelper.GetInstance("Orglib");
			boolean result = dbHelper.Update("orgactive", "id="+imageInfo.getId(), new String[] { "appid","title","type","content", "active", "time" }, new Object[] {imageInfo.getAppid(),imageInfo.getTitle(),imageInfo.getType(),imageInfo.getContent(),imageInfo.getActive(),new Timestamp(imageInfo.getTime().getTime())});
			if (result) {
				iRet = imageInfo.getId();
			} else {
				return -1;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iRet;
	}
}
