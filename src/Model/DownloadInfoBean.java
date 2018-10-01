package Model;

import java.util.Map;

public class DownloadInfoBean {
	private String appID;
	private String userName;
	private String typeID;
	private String fileID;
	private String fileName;
	private String fileType;
	private String client;
	private String address;
	private int isorg;
	private String orgName;
	
	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTypeID() {
		return typeID;
	}

	public void setTypeID(String typeID) {
		this.typeID = typeID;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getIsorg() {
		return isorg;
	}

	public void setIsorg(int isorg) {
		this.isorg = isorg;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public DownloadInfoBean(){
		
	}
	
	public DownloadInfoBean(Map<String, Object> mapData) {
		userName = (String) mapData.get("username");
		fileName = (String) mapData.get("filename");
		fileID = (String) mapData.get("fileid");
	}
}
