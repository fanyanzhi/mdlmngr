package Model;


//�û���½��־��Ϣ����¼ÿ���û���ϸ�ĵ�½���ڵ�¼�ɹ����¼
public class UserLoginInfoBean {
	private int id;
	private String userName;
	private String address;
	private String client;
	private String time;
	
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public UserLoginInfoBean()
	{
		
	}
		
}
