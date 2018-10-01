package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Model.AuthImageInfoBean;
import Model.ImageInfoBean;
import Util.Common;

public class ImageMngr {
	public static int addImageInfo(ImageInfoBean ImageInfo) {
		int iRet = -1;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (1 == ImageInfo.getModule()) {
				List<String> lstSql = new ArrayList<String>();
				StringBuilder sbSql1 = new StringBuilder();
				sbSql1.append("update recommendationinfo set updatetime='").append(Common.GetDateTime())
						.append("' where important=1");
				StringBuilder sbSql2 = new StringBuilder();
				sbSql2.append("delete from imageinfo where foreignid='").append(ImageInfo.getForeignID()).append("'");
				lstSql.add(sbSql1.toString());
				lstSql.add(sbSql2.toString());
				dbHelper.ExecuteSql(lstSql);
			}
			if (dbHelper.Insert("imageinfo",
					new String[] { "name", "appid", "content", "extname", "size", "width", "height", "module",
							"foreignid", "time" },
					new Object[] { ImageInfo.getName(), ImageInfo.getAppid(), ImageInfo.getContent(),
							ImageInfo.getExtName(), ImageInfo.getSize(), ImageInfo.getWidth(), ImageInfo.getHeight(),
							ImageInfo.getModule(), ImageInfo.getForeignID(), Common.GetDateTime() })) {
				if (1 == ImageInfo.getModule()) {
					iRet = getImageID(ImageInfo.getForeignID());
				} else if (3 == ImageInfo.getModule() || 4 == ImageInfo.getModule()) {
					iRet = getMaxImageID();
				} else {
					return 1;
				}
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
			DBHelper dbHelper = DBHelper.GetInstance();
			lstImageInfo = dbHelper.ExecuteQuery("max(id) id", "imageinfo");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstImageInfo == null) {
			return -1;
		}
		return Integer.parseInt(String.valueOf(lstImageInfo.get(0).get("id")));
	}

	public static int getImageID(String ForeignID) {
		List<Map<String, Object>> lstImageInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstImageInfo = dbHelper.ExecuteQuery("id", "imageinfo", "foreignid='".concat(ForeignID).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstImageInfo == null) {
			return -1;
		}
		return Integer.parseInt(String.valueOf(lstImageInfo.get(0).get("id")));
	}

	public static List<ImageInfoBean> getimageList(String appID, int Module) {
		List<ImageInfoBean> lstRet = null;
		List<Map<String, Object>> lstImageInfo = null;
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(appID)) {
			sbCondition.append("appid = '").append(appID).append("' and ");
		}
		if (!Common.IsNullOrEmpty(appID)) {
			sbCondition.append("module=").append(String.valueOf(Module));
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstImageInfo = dbHelper.ExecuteQuery("id,width,height,name,extname,size,foreignid,time", "imageinfo",
					sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstImageInfo != null) {
			lstRet = new ArrayList<ImageInfoBean>(lstImageInfo.size());
			for (Map<String, Object> mapTemp : lstImageInfo) {
				ImageInfoBean imageInfo = new ImageInfoBean();
				imageInfo.setId((int) mapTemp.get("id"));
				imageInfo.setWidth((int) mapTemp.get("width"));
				imageInfo.setHeight((int) mapTemp.get("height"));
				imageInfo.setName((String) mapTemp.get("name"));
				imageInfo.setExtName((String) mapTemp.get("extname"));
				imageInfo.setSize((long) mapTemp.get("size"));
				imageInfo.setModule(Module);
				imageInfo.setTime((String) mapTemp.get("time"));
				lstRet.add(imageInfo);
			}
		}
		return lstRet;
	}

	public static List<ImageInfoBean> getimageList(String appID, int Module, int Width, int Height) {
		List<ImageInfoBean> lstRet = null;
		List<Map<String, Object>> lstImageInfo = null;
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(appID)) {
			sbCondition.append("appid = '").append(appID).append("' and ");
		}
		sbCondition.append("module=").append(String.valueOf(Module)).append(" and width>=")
				.append(String.valueOf(Width)).append(" and height>=").append(String.valueOf(Height));
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstImageInfo = dbHelper.ExecuteQuery("id,width,height,name,extname,size,foreignid,time", "imageinfo",
					sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstImageInfo != null) {
			lstRet = new ArrayList<ImageInfoBean>(lstImageInfo.size());
			for (Map<String, Object> mapTemp : lstImageInfo) {
				ImageInfoBean imageInfo = new ImageInfoBean();
				imageInfo.setId((int) mapTemp.get("id"));
				imageInfo.setWidth((int) mapTemp.get("width"));
				imageInfo.setHeight((int) mapTemp.get("height"));
				imageInfo.setName((String) mapTemp.get("name"));
				imageInfo.setExtName((String) mapTemp.get("extname"));
				imageInfo.setSize((long) mapTemp.get("size"));
				imageInfo.setModule(Module);
				imageInfo.setTime((String) mapTemp.get("time"));
				lstRet.add(imageInfo);
			}
		}
		return lstRet;
	}

	public static boolean deleteImageInfo(int ImageID) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete("imageinfo", "id=".concat(String.valueOf(ImageID)));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static byte[] getImageContent(int ImageID) {
		List<Map<String, Object>> lstImageInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstImageInfo = dbHelper.ExecuteQuery("content", "imageinfo", "id=".concat(String.valueOf(ImageID)));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		byte[] arrRet = null;
		if (lstImageInfo != null) {
			arrRet = (byte[]) lstImageInfo.get(0).get("content");
		}
		return arrRet;
	}

	public static String addAuthImageInfo(AuthImageInfoBean ImageInfo) {
		String ret = "";
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.Insert("authimageinfo",
					new String[] { "imageid", "name", "appid", "content", "extname", "size", "width", "height",
							"module", "foreignid", "time" },
					new Object[] { ImageInfo.getImageid(), ImageInfo.getName(), ImageInfo.getAppid(),
							ImageInfo.getContent(), ImageInfo.getExtName(), ImageInfo.getSize(), ImageInfo.getWidth(),
							ImageInfo.getHeight(), ImageInfo.getModule(), ImageInfo.getForeignID(),
							Common.GetDateTime() })) {
				ret = ImageInfo.getImageid();
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return ret;
	}
	
	public static byte[] getAuthImageContent(String ImageID) {
		List<Map<String, Object>> lstImageInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstImageInfo = dbHelper.ExecuteQuery("content", "authimageinfo", "imageid='".concat(dbHelper.FilterSpecialCharacter(ImageID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		byte[] arrRet = null;
		if (lstImageInfo != null) {
			arrRet = (byte[]) lstImageInfo.get(0).get("content");
		}
		return arrRet;
	}
}
