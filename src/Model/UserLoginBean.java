package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;

import net.sf.json.JSONObject;

//��̨����Ա�û���Ϣ��
public class UserLoginBean {
	private int id;
	private String userName;
	private String password;
	private int role;
	private String appid;
	
	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public UserLoginBean(){
		
	}

	public UserLoginBean(Map<String, Object> mapData) {
		//this.userName = (String) UserInfo.get("username");
		//this.admin = "1".equals(String.valueOf(UserInfo.get("role"))) ? true : false;
		Field[] fileds = this.getClass().getDeclaredFields();
		for (int i = 0; i < fileds.length; i++) {
			try {
				Object objData=mapData.get(fileds[i].getName().toLowerCase());
				if(objData!=null&&String.valueOf(fileds).length()>0){
					fileds[i].set(this, Common.CovertToObject(mapData.get(fileds[i].getName().toLowerCase()), fileds[i].getType()));
				}
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}
	}

	public static UserLoginBean getUserLoginBean(String json) {
		JSONObject joUserLogin = JSONObject.fromObject(json);
		return (UserLoginBean) JSONObject.toBean(joUserLogin, UserLoginBean.class);
	}

}
