package Model;

import java.util.Map;

//import Util.Common;


import net.sf.json.JSONObject;

//ͼ����û���Ϣ
public class UserInfoBean {
	private String UserName;
	private String PassWord;
	private String EMail;
	private String PlatForm;
	private String ClientID;
	private String address;
	private String version;
	private String qqopenid;
	private String sinaopenid;
	private String mobile;
	private String appid;
	private String longitude;
	private String latitude;
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getQqopenid() {
		return qqopenid;
	}

	public void setQqopenid(String qqopenid) {
		this.qqopenid = qqopenid;
	}

	public String getSinaopenid() {
		return sinaopenid;
	}

	public void setSinaopenid(String sinaopenid) {
		this.sinaopenid = sinaopenid;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPassWord() {
		return PassWord;
	}

	public void setPassWord(String passWord) {
		this.PassWord = passWord;
	}

	public String getEMail() {
		return EMail;
	}

	public void setEMail(String eMail) {
		EMail = eMail;
	}

	public String getPlatForm() {
		return PlatForm;
	}

	public void setPlatForm(String platForm) {
		PlatForm = platForm;
	}

	public String getClientID() {
		return ClientID;
	}

	public void setClientID(String clientID) {
		ClientID = clientID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public UserInfoBean() {
	}

	public UserInfoBean(Map<String, String> UserInfo) {
		this.UserName = UserInfo.get("username");
		this.PassWord = UserInfo.get("password");//Common.getPasswordFromBase64(UserInfo.get("password"),"caj)(#&dsa7SDNJ32hwbds%u32j33edjdu2@**@3w");//UserInfo.get("password");//
		this.EMail = UserInfo.get("email");
		this.PlatForm = UserInfo.get("platform") == null ? "" : UserInfo.get("platform");
		this.ClientID = UserInfo.get("clientid")== null ? "" : UserInfo.get("clientid");;
		this.address = UserInfo.get("ip");
		this.qqopenid = UserInfo.get("qqopenid") == null ? "" : UserInfo.get("qqopenid");
		this.sinaopenid = UserInfo.get("sinaopenid") == null ? "" : UserInfo.get("sinaopenid");
		this.version = UserInfo.get("version") == null ? "" : UserInfo.get("version");
		this.appid = UserInfo.get("appid") == null ? "" : UserInfo.get("appid");
		this.longitude = UserInfo.get("longitude") == null ? "": UserInfo.get("longitude");
		this.latitude = UserInfo.get("latitude") == null ? "": UserInfo.get("latitude");
	}

	public static UserInfoBean getUserInfoBean(String json) {
		JSONObject joUserInfo = JSONObject.fromObject(json);
		return (UserInfoBean) JSONObject.toBean(joUserInfo, UserInfoBean.class);
	}

}
