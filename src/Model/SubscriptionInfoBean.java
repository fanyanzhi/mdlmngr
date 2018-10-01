package Model;

import net.sf.json.JSONObject;

public class SubscriptionInfoBean {
	private int ID;
	private String SubscriptionName;
	private String SubscriptionContent;
	private String SubscriptionType;
	private String UserName;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getSubscriptionName() {
		return SubscriptionName;
	}

	public void setSubscriptionName(String subscriptionName) {
		SubscriptionName = subscriptionName;
	}

	public String getSubscriptionContent() {
		return SubscriptionContent;
	}

	public void setSubscriptionContent(String subscriptionContent) {
		SubscriptionContent = subscriptionContent;
	}

	public String getSubscriptionType() {
		return SubscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		SubscriptionType = subscriptionType;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public static SubscriptionInfoBean getSubscriptionInfoBean(String json) {
		JSONObject joSubscriptionInfo = JSONObject.fromObject(json);
		return (SubscriptionInfoBean) JSONObject.toBean(joSubscriptionInfo, SubscriptionInfoBean.class);
	}
}
