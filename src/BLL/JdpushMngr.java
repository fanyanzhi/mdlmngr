package BLL;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.Devices;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import org.json.JSONException;
import org.json.JSONObject;

public class JdpushMngr {
	protected static final Logger LOG = LoggerFactory.getLogger(JdpushMngr.class);

	// demo App defined in resources/jpush-api.conf EXTRA_EXTRA

	public static final String TITLE = "全球学术快报";
	public static final String ALERT = "欢迎使用全球学术快报";
	public static final String MSG_CONTENT = "欢迎使用全球学术快报";
	public static final String REGISTRATION_ID = "0900e8d85ef";
	public static final String TAG = "tag_api";

	private static final String appKey = "79d0d55cdaec17c6facf3106";
	private static final String masterSecret = "6ff63385202e4e2ada345153";

	public static JPushClient jpushClient = null;

	public static void testSendPush(String appKey, String masterSecret) {

		jpushClient = new JPushClient(masterSecret, appKey, 3);
		PushPayload payload = buildPushObject_ios_tag_alertWithTitle2();
		try {
			System.out.println(payload.toString());
			PushResult result = jpushClient.sendPush(payload);
			System.out.println(result + "................................");

			LOG.info("Got result - " + result);

		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);

		} catch (APIRequestException e) {
			LOG.error("Error response from JPush server. Should review and fix it. ", e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
			LOG.info("Msg ID: " + e.getMsgId());
		}
	}

	/**
	 * 发送广播到ios和安卓 所有人的
	 */
	public static void pushAll(String title, Map<String, String> message) {

	}

	/**
	 * 以别名方式发送
	 * 
	 * @param alias
	 * @param title
	 * @param message
	 */
	public static void pushWithAlias(String alias, String title, Map<String, String> message) {

	}

	/*
	 * public static PushPayload buildPushObject_all_all_alert() { return
	 * PushPayload.alertAll(ALERT); }
	 */

	public static PushPayload buildPushObject_android_tag_alertWithTitle() {
		return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.all())
				.setNotification(Notification.android(ALERT, TITLE, null)).build();
	}

	public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage() {
		return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.tag_and("tag1", "tag_all"))
				.setNotification(Notification.newBuilder()
						.addPlatformNotification(IosNotification.newBuilder().setAlert(ALERT).setBadge(5)
								.setSound("happy").addExtra("from", "JPush").build())
						.build())
				.setMessage(Message.content(MSG_CONTENT))
				.setOptions(Options.newBuilder().setApnsProduction(true).build()).build();
	}

	public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
		return PushPayload.newBuilder().setPlatform(Platform.android_ios())
				.setAudience(Audience.newBuilder().addAudienceTarget(AudienceTarget.tag("tag1", "tag2"))
						.addAudienceTarget(AudienceTarget.alias("alias1", "alias2")).build())
				.setMessage(Message.newBuilder().setMsgContent(MSG_CONTENT).addExtra("from", "JPush").build()).build();
	}

	/***
	 */
	public static PushPayload send_N(String registrationId, String alert) {
		return PushPayload.newBuilder().setPlatform(Platform.android_ios())// 必填
																			// 推送平台设置
				.setAudience(Audience.registrationId(registrationId)).setNotification(Notification.alert(alert))
				/**
				 * 如果目标平台为 iOS 平台 需要在 options 中通过 apns_production 字段来制定推送环境。
				 * True 表示推送生产环境，False 表示要推送开发环境； 如 果不指定则为推送生产环境
				 */
				.setOptions(Options.newBuilder().setApnsProduction(false).build()).build();
	}

	public static PushPayload buildPushObject_all_alert2() {
		return PushPayload.newBuilder().setPlatform(Platform.all())// 设置接受的平台
				.setAudience(Audience.all())// Audience设置为all，说明采用广播方式推送，所有用户都可以接收到
				.setNotification(Notification.alert(ALERT)).build();
	}

	/**
	 * 推送改特定用户，加类别的 可以,
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_all_alias_alert() {
		return PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.alias("lfk1010"))
				.setNotification(Notification.alert(ALERT)).setMessage(Message.newBuilder().setMsgContent(MSG_CONTENT)
						.addExtra("notice", "1DBC5EB26B154EDDE050007F010042FD").build())
				.build();
	}

	/**************************************/
	// 1)
	/**
	 * 推送给所有人，所有平台-->可以
	 * {"platform":"all","audience":"all","notification":{"alert":
	 * "国家图书馆闭馆通知1019"},"options":{"sendno":1939330627,"apns_production":false}}
	 * {"msg_id":3280722559,"sendno":1939330627}................................
	 * 2016-10-19 13:49:11 INFO cn.gov.nlc.util.Jdpush:60 - Got result -
	 * {"msg_id":3280722559,"sendno":1939330627} sucess
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_all_alert() {
		return PushPayload.newBuilder().setPlatform(Platform.all())// 设置接受的平台
				.setAudience(Audience.all())// Audience设置为all，说明采用广播方式推送，所有用户都可以接收到
				.setNotification(Notification.alert(ALERT)).build();
	}

	// 2)
	/**
	 * {"platform":["android","ios"],"audience":"all","notification":{"alert":
	 * "国家图书馆闭馆通知1019","android":{"alert":"国家图书馆闭馆通知1019","title":"国家图书馆"},"ios"
	 * :{"alert":"国家图书馆闭馆通知1019","extras":{"notice":
	 * "1DBC5EB26B154EDDE050007F010042FD"},"badge":"+1","sound":""}},"options":{
	 * "sendno":628845487,"apns_production":false}} 2016-10-19 13:59:57 INFO
	 * cn.gov.nlc.util.Jdpush:60 - Got result -
	 * {"msg_id":3250998535,"sendno":628845487}
	 * {"msg_id":3250998535,"sendno":628845487}................................
	 * sucess
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_android_and_ios() {
		return PushPayload.newBuilder().setPlatform(Platform.android_ios()).setAudience(Audience.all())
				.setNotification(
						Notification.newBuilder().setAlert(ALERT)
								.addPlatformNotification(AndroidNotification.newBuilder().setTitle(TITLE).build())
								.addPlatformNotification(IosNotification.newBuilder().incrBadge(1)
										.addExtra("notice", "1DBC5EB26B154EDDE050007F010042FD").build())
								.build())
				.build();
	}

	// 3
	/**
	 * android,客户端可以,特定的人
	 * 推送内容包括{"platform":["android"],"audience":{"alias":["lfk1010"]},
	 * "notification":{"android":{"alert":"国家图书馆闭馆通知1019","extras":{"news":
	 * "4aeafd24574f5cda0157b138e49e7079"},"title":"国家图书馆"}},"options":{"sendno"
	 * :676106587,"apns_production":false}}
	 * {"msg_id":2336928913,"sendno":676106587}................................
	 * 2016-10-19 09:40:37 INFO cn.gov.nlc.util.Jdpush:55 - Got result -
	 * {"msg_id":2336928913,"sendno":676106587}
	 * 
	 * 
	 * 
	 * {"platform":["android"],"audience":{"alias":["lfk1010"]},"notification":{
	 * "android":{"alert":"国家图书馆闭馆通知1019","extras":{"notice":
	 * "1DBC5EB26B154EDDE050007F010042FD"},"title":"国家图书馆"}},"options":{"sendno"
	 * :928548734,"apns_production":false}}
	 * {"msg_id":3284570772,"sendno":928548734}................................
	 * 2016-10-19 14:04:09 INFO cn.gov.nlc.util.Jdpush:64 - Got result -
	 * {"msg_id":3284570772,"sendno":928548734} sucess
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_android_tag_alertWithTitle2() {
		Map<String, String> news = new HashMap<String, String>();
		news.put("message", "大家好，系统测试");
		return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.alias("lfk1010"))
				.setNotification(Notification.android(ALERT, TITLE, news)).build();
	}

	//////////////// ios
	public static PushPayload buildPushObject_all_all_alert() {
		return PushPayload.alertAll(ALERT);
	}

	public static PushPayload buildPushObject_all_alias_alert3() {
		return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.all())
				.setNotification(Notification.alert(ALERT)).build();
	}

	public static PushPayload buildPushObject_android_tag_alertWithTitle3() {
		return PushPayload.newBuilder().setPlatform(Platform.ios())
				// .setAudience(Audience.tag("tag1"))
				.setNotification(Notification.android(ALERT, TITLE, null)).build();
	}

	public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage3() {
		return PushPayload.newBuilder().setPlatform(Platform.ios())
				// .setAudience(Audience.tag_and("tag1", "tag_all"))
				.setNotification(Notification.newBuilder()
						.addPlatformNotification(IosNotification.newBuilder().setAlert(ALERT).setBadge(5)
								.setSound("happy").addExtra("from", "JPush").build())
						.build())
				.setMessage(Message.content(MSG_CONTENT))
				.setOptions(Options.newBuilder().setApnsProduction(true).build()).build();
	}

	public static PushPayload buildPushObject_ios_tag_alertWithTitle2() {
		Map<String, String> news = new HashMap<String, String>();
		news.put("message", "大家好，系统测试");
		return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.alias("cnki_wsy"))
				.setNotification(Notification.ios(ALERT, news)).build();
	}

	/*******************************************
	 * 2016-11-06,可以写一个定时任务，在容器启动的时候一直循环去表里面读数据，
	 ***************************************************/

	public static boolean pushMessage(String alias, String alert, String title, String type, String id) {
		jpushClient = new JPushClient(masterSecret, appKey, 3);

		PushPayload payload = null;
		if (alias == null || alias.length() == 0) {
			payload = buildPushObject_to_all(alert, title, type, id);
		} else {
			payload = buildPushObject_all_alias_alert(alias, alert, title, type, id);
		}

		try {
			System.out.println(payload.toString());
			PushResult result = jpushClient.sendPush(payload);
			System.out.println(result + "................................");

			LOG.info("Got result - " + result);

		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);

		} catch (APIRequestException e) {
			LOG.error("Error response from JPush server. Should review and fix it. ", e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
			LOG.info("Msg ID: " + e.getMsgId());
		}
		return true;
	}

	/**
	 * 针对新闻、公告、专题、文津
	 * 
	 * @param alert
	 * @param title
	 * @param type
	 * @param id
	 * @return
	 */
	public static PushPayload buildPushObject_to_all(String alert, String title, String type, String id) {
		return PushPayload.newBuilder().setPlatform(Platform.android_ios()).setAudience(Audience.all())
				.setNotification(Notification.newBuilder().setAlert(alert)
						.addPlatformNotification(
								AndroidNotification.newBuilder().setTitle(title).addExtra(type, id).build())
						.addPlatformNotification(IosNotification.newBuilder().incrBadge(1).addExtra(type, id).build())
						.build()).setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(90).build())
				.build();

	}

	/**
	 * 推送特定用户，加类别的 可以, 周一需要试试这个好用吗，这个给android好用，需要给ios试试
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_all_alias_alert(String alias, String alert, String title, String type,
			String id) {
		Map<String, String> extra = new HashMap<String, String>();
		if(type.equals("count")){
			extra.put("type", "1");
			extra.put("code", id);
		}else{
			extra.put("type", "2");
			extra.put("odatatype", type);
			extra.put("fileid", id);
		}
		return PushPayload.newBuilder().setPlatform(Platform.android_ios()).setAudience(Audience.alias(alias))
				.setNotification(Notification.newBuilder().setAlert(alert)
						.addPlatformNotification(
								AndroidNotification.newBuilder().setTitle(title).addExtras(extra).build())
						.addPlatformNotification(IosNotification.newBuilder().incrBadge(1).addExtras(extra).build())
						.build()).setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(90).build())
				.build();

		/*
		 * return PushPayload.newBuilder().setPlatform(Platform.android_ios()).
		 * setAudience(Audience.alias(alias))
		 * .setNotification(Notification.alert(alert))
		 * .setMessage(Message.newBuilder().setMsgContent(msg_content).addExtra(
		 * type, id).build()).build();
		 */

	}
	
	public static void main(String[] args){
		System.out.println(JdpushMngr.class.getClassLoader().getResource("").getPath());
		//pushMessage("cofan7608", "您定制的'软件工程'今日更新27篇文献", "全球学术快报", "CJFD", "SIJI201406015");
	}
	
	/**
	 * 推送今日更新数
	 */
//	public static void iosPushKuaiBao(List<Device> devices, String message, String type, String code){	
//		writeLog("message",message);
//		try {
//			PushNotificationPayload payload = new PushNotificationPayload();
//			payload.addCustomAlertBody(message);
//			payload.addBadge(1);
//			payload.addSound("default");
//			payload.addCustomDictionary("type", type);    //跳转参数
//			payload.addCustomDictionary("code", code); //跳转参数
//			//System.out.println(IosPush.class.getClassLoader().getResource("").getPath());
////			Push.payload(payload, "D:/WebRoot/mdl/WebContent/WEB-INF/classes/aps_kuaibao_product.p12", "123456", true, devices);
//			Push.payload(payload, "D:/aps_kuaibao_product.p12", "123456", true, devices);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	public static void iosPushKuaiBao(List<String> tokens, String message, String type, String code){	
		writeLog("start",message);
        PushNotificationManager pushManager = new PushNotificationManager();
		try
        {
			final String sound = "default";
			PushNotificationPayload payLoad = new PushNotificationPayload(message,0,sound);
			/*PushNotificationPayload payLoad = new PushNotificationPayload();
	        payLoad.addBadge(0); // iphone应用图标上小红圈上的数值
	        payLoad.addSound("default");//铃音
*/	        payLoad.addCustomDictionary("type", type);    //跳转参数
			payLoad.addCustomDictionary("code", code);
	        //payLoad.addAlert(message);
            //true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl("D:/aps_kuaibao_product.p12", "123456", true));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            List<Device> devices = new ArrayList<Device>();
            StringBuilder sbToken = new StringBuilder();
            for (String token : tokens)
            {
            	sbToken.append(token).append("###");
                devices.add(new BasicDevice(token));
            }
            notifications = pushManager.sendNotifications(payLoad, devices);
            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
            int failed = failedNotifications.size();
            
            int successful = successfulNotifications.size();
            writeLog("result",message+"-->failed:"+failed+"-->success:"+successful+"-->token:"+sbToken);
            if(failedNotifications.size()>0)
            	writeLog("result",message+"-->"+failedNotifications.get(0).getException().getMessage());
            pushManager.stopConnection();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	writeLog("error",message+"-->"+e.getMessage());
        }finally{
        	try {
				pushManager.stopConnection();
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeystoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		

		
		
		
//		try {
//			PushNotificationPayload payload = new PushNotificationPayload();
//			payload.addCustomAlertBody(message);
//			payload.addBadge(0);
//			payload.addSound("default");
//			payload.addCustomDictionary("type", type);    //跳转参数
//			payload.addCustomDictionary("code", code); //跳转参数
////			Push.payload(payload, "D:/WebRoot/mdl/WebContent/WEB-INF/classes/aps_kuaibao_product.p12", "123456", true, devices);
//			Push.payload(payload, "D:/aps_kuaibao_product.p12", "123456", true, Devices.asDevices(tokens));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public static void iosPushKuaiBao(List<String> tokens, String message, String type, Map<String,String> map){	
		writeLog("start",message);
        PushNotificationManager pushManager = new PushNotificationManager();
		try
        {
			final String sound = "default";
			PushNotificationPayload payLoad = new PushNotificationPayload(message,0,sound);
			/*PushNotificationPayload payLoad = new PushNotificationPayload();
	        payLoad.addBadge(0); // iphone应用图标上小红圈上的数值
	        payLoad.addSound("default");//铃音
*/	        payLoad.addCustomDictionary("type", type);    //跳转参数
	        if("1".equals(type)){
	        	payLoad.addCustomDictionary("code", map.get("code"));
	        }
	        if("6".equals(type)){
	        	payLoad.addCustomDictionary("fromuser", map.get("fromuser"));
	        	payLoad.addCustomDictionary("_type", map.get("type"));
	        	payLoad.addCustomDictionary("id", map.get("id"));
	        	payLoad.addCustomDictionary("message", message);
	        	payLoad.addCustomDictionary("time", map.get("time"));
	        }
			
	        payLoad.addAlert(message);
            //true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl("D:/aps_kuaibao_product.p12", "123456", true));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            List<Device> devices = new ArrayList<Device>();
            StringBuilder sbToken = new StringBuilder();
            for (String token : tokens)
            {
            	sbToken.append(token).append("###");
                devices.add(new BasicDevice(token));
            }
            notifications = pushManager.sendNotifications(payLoad, devices);
            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
            int failed = failedNotifications.size();
            
            int successful = successfulNotifications.size();
            writeLog("result",message+"-->failed:"+failed+"-->success:"+successful+"-->token:"+sbToken);
            if(failedNotifications.size()>0)
            	writeLog("result",message+"-->"+failedNotifications.get(0).getException().getMessage());
            pushManager.stopConnection();
        }
        catch (Exception e)
        {
        	writeLog("error",message+"-->"+e.getMessage());
        }finally{
        	try {
				pushManager.stopConnection();
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeystoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	public static void iosPushKuaiBao2(List<String> tokens, final String message, String type, Map<String,String> map){	
		writeLog("start",message);
        PushNotificationManager pushManager = new PushNotificationManager();
		try
        {
			final String sound = "default";
			PushNotificationPayload payLoad = new PushNotificationPayload(message,0,sound);
	        payLoad.addCustomDictionary("type", type);    //跳转参数
	        if("1".equals(type)){
	        	payLoad.addCustomDictionary("code", map.get("code"));
	        }
	        if("6".equals(type)){
	        	payLoad.addCustomDictionary("fromuser", map.get("fromuser"));
	        	payLoad.addCustomDictionary("_type", map.get("type"));
	        	payLoad.addCustomDictionary("id", map.get("id"));
	        	payLoad.addCustomDictionary("message", message);
	        	payLoad.addCustomDictionary("time", map.get("time"));
	        }
			
	        payLoad.addAlert(message);
            //true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl("D:/aps_kuaibao_product.p12", "123456", true));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            List<Device> devices = new ArrayList<Device>();
            StringBuilder sbToken = new StringBuilder();
            for (String token : tokens)
            {
            	sbToken.append(token).append("###");
                devices.add(new BasicDevice(token));
            }
            notifications = pushManager.sendNotifications(payLoad, devices);
            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
            int failed = failedNotifications.size();
            
            int successful = successfulNotifications.size();
            writeLog("result",message+"-->failed:"+failed+"-->success:"+successful+"-->token:"+sbToken);
            if(failedNotifications.size()>0)
            	writeLog("result",message+"-->"+failedNotifications.get(0).getException().getMessage());
            pushManager.stopConnection();
        }
        catch (Exception e)
        {
        	writeLog("error",message+"-->"+e.getMessage());
        }finally{
        	try {
				pushManager.stopConnection();
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeystoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	
	public static void writeLog(String folder,String data) {
		File file = new File("d:\\log\\"+folder+".txt");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter sucsessFile = new FileWriter(file, true);
			sucsessFile.write(data + "\r\n");
			sucsessFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}
}
