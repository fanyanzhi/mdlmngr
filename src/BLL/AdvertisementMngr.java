package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Model.AdvertisementInfoBean;
import Util.Common;

public class AdvertisementMngr {

	public static boolean addAdvertisement(AdvertisementInfoBean AdvermentBean) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("advertisement", new String[] { "type", "appid", "content", "startdate", "enddate", "imageid", "isdelete", "updatetime", "time" }, new Object[] { AdvermentBean.getType(), AdvermentBean.getAppid(), AdvermentBean.getContent(), AdvermentBean.getStartDate(), AdvermentBean.getEndDate(), AdvermentBean.getImageId(), 0, Common.GetDateTime(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static List<Map<String, Object>> getAdvertisementInfo(String appid, String AdvID) {
		List<Map<String, Object>> lstAdvertisementInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstAdvertisementInfo = dbHelper.ExecuteQuery("id,type,content,startdate,enddate,imageid", "advertisement", "appid = '".concat(appid).concat("' and id='").concat(dbHelper.FilterSpecialCharacter(AdvID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstAdvertisementInfo;
	}

	public static boolean updateAdvertisement(AdvertisementInfoBean AdvermentBean) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update("advertisement", "id=".concat(String.valueOf(AdvermentBean.getId()).concat(" and appid='").concat(AdvermentBean.getAppid()).concat("'")), new String[] { "type", "content", "startdate", "enddate", "imageid", "updatetime" }, new Object[] { AdvermentBean.getType(), AdvermentBean.getContent(), AdvermentBean.getStartDate(), AdvermentBean.getEndDate(), AdvermentBean.getImageId(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean delAdvertisement(String appid, int ID) {
		boolean bolRet = false;
		try {
			// DBHelper dbHelper = DBHelper.GetInstance();
			// List<String> lstSql = new ArrayList<String>();
			// String sql1 =
			// "DELETE advertisement,imageinfo from advertisement LEFT JOIN imageinfo ON advertisement.imageid=imageinfo.id WHERE advertisement.id=".concat(String.valueOf(ID));
			// lstSql.add(sql1);
			// String sql2 =
			// "update advertisement set updatetime='".concat(Common.GetDateTime()).concat("'");
			// lstSql.add(sql2);
			// bolRet = dbHelper.ExecuteSql(lstSql);
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update("advertisement", "advertisement.id=".concat(String.valueOf(ID).concat(" and advertisement.appid='").concat(appid).concat("'")), new String[] { "isdelete", "updatetime" }, new Object[] { 1, Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static List<Map<String, Object>> getAdvertisementList(String appid) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("advertisement.id, advertisement.type, advertisement.content, advertisement.startdate, advertisement.enddate, imageinfo.id imageid, advertisement.updatetime as time", "advertisement left JOIN imageinfo on advertisement.imageid = imageinfo.id where advertisement.appid ='".concat(appid).concat("' and advertisement.isdelete = 0 order by advertisement.showorder"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstInfo;
	}

	public static String getLatestUpTime(String appid) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("max(updatetime) updatetime", "advertisement", "appid='".concat(appid).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstInfo == null) {
			return "";
		} else {
			return String.valueOf(lstInfo.get(0).get("updatetime"));
		}
	}

	public static List<Map<String, Object>> getValidAdvertisement(String AppID) {
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstInfo = dbHelper.ExecuteQuery("advertisement.type, advertisement.content, advertisement.startdate, advertisement.enddate, advertisement.imageid, advertisement.time", "advertisement left JOIN imageinfo on advertisement.imageid = imageinfo.id", "advertisement.appid='".concat(AppID).concat("' and (imageinfo.id is not null) and  isdelete = 0 and if(startdate is null,1=1,'").concat(Common.GetDate()).concat("'>=startdate) and if(enddate is null, 1=1,'").concat(Common.GetDate()).concat("'<= enddate)"),"advertisement.showorder");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstInfo;
	}
	
	public static boolean saveAdVertisementOrder(String AppID, List<Map<String, String>> adList){
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			List<String> lstSql = new ArrayList<String>();
			
			for(Map<String, String> map:adList){
				StringBuilder sbSql = new StringBuilder();
				sbSql.append("update advertisement set showorder=").append(map.get("showorder")).append(",updatetime='").append(Common.GetDateTime()).append("' where id=").append(map.get("id")).append(" and appid='".concat(AppID).concat("'"));
				lstSql.add(sbSql.toString());
			}
			return dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
}
