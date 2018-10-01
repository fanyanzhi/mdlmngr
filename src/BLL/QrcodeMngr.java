package BLL;

import java.util.List;
import java.util.Map;

import DAL.DBHelper;
import Util.Common;

public class QrcodeMngr {
	public static boolean addQrcode(String qrcode){
		boolean ret = false;
		DBHelper dbHelper = null;
		try{
			dbHelper = DBHelper.GetInstance();
			ret = dbHelper.Insert("qrcode", new String[]{"code","time","updatetime"}, new Object[]{qrcode,Common.GetDateTime(),Common.GetDateTime()});
		}catch(Exception e){
			
		}
		return ret;
	}
	
	public static boolean updateQrcode(String qrcode, String userName){
		boolean ret = false;
		DBHelper dbHelper = null;
		try{
			dbHelper = DBHelper.GetInstance();
			ret = dbHelper.Update("qrcode", "code='".concat(qrcode).concat("'"),new String[]{"username","updatetime"}, new Object[]{userName,Common.GetDateTime()});
		}catch(Exception e){
			
		}
		return ret;
	}
	
	public static boolean chkQrcode(String qrcode){
		DBHelper dbHelper = null;
		List<Map<String, Object>> lst= null;
		try{
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("username", "qrcode", "code='".concat(qrcode).concat("'"));
		}catch(Exception e){
			
		}
		if(lst==null || lst.size()==0)
			return false;
		
		if(!Common.IsNullOrEmpty((String)lst.get(0).get("username")))
			return false;
		return true;
	}
	
	public static List<Map<String, Object>> getQrcode(String qrcode){
		List<Map<String, Object>> lst= null;
		DBHelper dbHelper = null;
		try{
			dbHelper = DBHelper.GetInstance();
			lst = dbHelper.ExecuteQuery("username", "qrcode", "code='".concat(qrcode).concat("'"));
		}catch(Exception e){
			
		}
		return lst;
	}
	
	
	
}
