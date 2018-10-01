package Model;

public class UploadInfoBean {
	private String userName;
	private String fileName;
	private String fileID;
	private String typeName;
	private long fileLength;
	private String typeid;
	private String range;
	private String client;
	private String address;
	private String fileMd5;
	private int isCompleted = 0;
	private int isHahepub = 0;
	private String fileTable;
	private String dskFileName;
	private int isDelete = 0;
	private String deleteTime;
	
	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
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
	
	public int getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(int isCompleted) {
		this.isCompleted = isCompleted;
	}

	public int getIsHahepub() {
		return isHahepub;
	}

	public void setIsHahepub(int isHahepub) {
		this.isHahepub = isHahepub;
	}

	public String getFileTable() {
		return fileTable;
	}

	public void setFileTable(String fileTable) {
		this.fileTable = fileTable;
	}

	public String getDskFileName() {
		return dskFileName;
	}

	public void setDskFileName(String dskFileName) {
		this.dskFileName = dskFileName;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public String getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(String deleteTime) {
		this.deleteTime = deleteTime;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}
}
