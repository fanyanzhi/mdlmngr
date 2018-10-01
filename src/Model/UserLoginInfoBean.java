package Model;


//用户登陆日志信息，记录每次用户详细的登陆，在登录成功后记录
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
