package Util.umengpush;

public class AndroidListcast extends AndroidNotification {
	public AndroidListcast(String appkey,String appMasterSecret) throws Exception {
			setAppMasterSecret(appMasterSecret);
			setPredefinedKeyValue("appkey", appkey);
			this.setPredefinedKeyValue("type", "listcast");	
	}
	
	public void setDeviceTokens(String token) throws Exception {
    	setPredefinedKeyValue("device_tokens", token);
    }

}