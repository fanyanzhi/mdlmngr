package Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Message.Builder;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
import com.xiaomi.xmpush.server.Sender.BROADCAST_TOPIC_OP;

/*
 * 1.type=1 为推送关注的快报信息更新信息，跳转到我的图书馆，其他参数：code为对应的行业分类代码
 * 2.type=2 为推荐的单篇信息，跳转到文献详情页，其他参数：odatatype为文献类别，fileid为文献id
 * 3.type=3 为推送的整刊信息，跳转到整刊详情页，其他参数：titlepy 为拼音刊名，_type，dbcode，year，issue
 * 4.type=4 为文本类型，跳转到消息详情页，其他参数：id为消息id
 * 5.type=5 为链接，直接打开链接，其他参数：url为链接地址
 */
public class XiaoMiPush {
	private static String APP_SECRET_KEY = "TTtW40lc1EWh1yscvPUKYw==";
	private static String MY_PACKAGE_NAME = "com.cnki.android.cnkimobile";
	private static Logger logger = Logger.getLogger(XiaoMiPush.class);

	/**
	 * 小米推送 	按设备ID推送
	 * @param title		在通知栏展示的通知的标题, 不允许全是空白字符, 长度小于16
	 * @param description	在通知栏展示的通知描述, 不允许全是空白字符, 长度小于128, 
	 * @param messagePayload	设置要发送的消息内容payload, 不允许全是空白字符, 长度小于4K
	 * @param regids		 regids的个数不得超过1000个
	 * @param type		1表示 我的图书馆定制更新   2 为推荐的单篇信息
	 * @param map	其他参数 
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void sendKuaiBaoMessage(String title,String description,String messagePayload,List<String> regids, String type, Map<String,String> map) throws IOException, ParseException  {
	    Constants.useOfficial();
	    Sender sender = new Sender(APP_SECRET_KEY);
	    int notifyId = new Random().nextInt(2147483647);
	    Builder build = new Message.Builder()
		        .title(title)
		        .description(description).payload(messagePayload)
		        .restrictedPackageName(MY_PACKAGE_NAME)
		        .passThrough(0)  //消息使用通知栏方式
		        .notifyType(1)// 使用默认提示音提示
	   			.notifyId(notifyId)//可选项,相同notifyId的通知栏消息会覆盖之前的
	   			.timeToLive(86400000);//离线保留时长
	   
	   Message message = null;
	   //.extra(Constants.EXTRA_PARAM_NOTIFY_EFFECT, Constants.NOTIFY_LAUNCHER_ACTIVITY)
	   switch (type) {
	   		case "1":message = build.extra("type",type).extra("code",map.get("code")).build();break;
	   		case "2":message = build.extra("type",type).extra("odatatype", map.get("odatatype")).extra("fileid", map.get("fileid")).build();break;
	   		case "3":message = build.extra("type",type)
	   				.extra("_type",map.get("_type"))
	   				.extra("titlepy",map.get("titlepy"))
	   				.extra("dbcode",map.get("dbcode"))
	   				.extra("year",map.get("year"))
	   				.extra("issue",map.get("issue"))
	   				.build();
	   				break;
	   		case "4":message = build.extra("type",type).extra("id",map.get("id")).build();break;
	   		case "5":message = build.extra("type",type)
	   					.extra("url", map.get("url"))
	   					.build();
	   					break;
	   		case "6":message = build.extra("type",type)
	   				.extra("fromuser",map.get("fromuser"))
	   				.extra("_type",map.get("type"))
	   				.extra("id",map.get("id"))
	   				.extra("message",description)
	   				.extra("time",map.get("time"))
	   				.build();
	   				break;
	   		default:message = build.extra(Constants.EXTRA_PARAM_NOTIFY_EFFECT, Constants.NOTIFY_LAUNCHER_ACTIVITY).build();break;//打开APP
	   }
	    Result result = sender.send(message, regids, 3);
	    if(!result.toString().contains("errorCode=0")){
	    	logger.info("小米推送notifyId:"+notifyId+" - Result:"+result.toString());
	    }
	}

	/**
	 * 小米推送 广播
	 * 
	 * @param title
	 *            在通知栏展示的通知的标题, 不允许全是空白字符, 长度小于16
	 * @param description
	 *            在通知栏展示的通知描述, 不允许全是空白字符, 长度小于128,
	 * @param messagePayload
	 *            设置要发送的消息内容payload, 不允许全是空白字符, 长度小于4K
	 * @throws Exception
	 */
	public void sendBroadcast(String title, String description, String messagePayload) throws Exception {
		Constants.useOfficial();
		Sender sender = new Sender(APP_SECRET_KEY);
		Message message = new Message.Builder().title(title).description(description).payload(messagePayload)
				.restrictedPackageName(MY_PACKAGE_NAME).notifyType(1) // 使用默认提示音提示
				.build();
		sender.broadcastAll(message, 3);
	}

	/**
	 * 小米推送 分组推送
	 * 
	 * @param title
	 * @param description
	 * @param messagePayload
	 * @param topicList
	 *            分组标识（取并集）
	 * @throws Exception
	 */
	public void sendMultiTopicBroadcast(String title, String description, String messagePayload, List<String> topicList)
			throws Exception {
		Constants.useOfficial();
		Sender sender = new Sender(APP_SECRET_KEY);
		// List<String> topicList = new ArrayList<>();
		// String topic1 = "testTopic1";
		// String topic2 = "testTopic2";
		// topicList.add(topic1);
		// topicList.add(topic2);
		Message message = new Message.Builder().title(title).description(description).payload(messagePayload)
				.restrictedPackageName(MY_PACKAGE_NAME).notifyType(1) // 使用默认提示音提示
				.build();
		// 根据topicList做并集运算, 发送消息到指定一组设备上
		sender.multiTopicBroadcast(message, topicList, BROADCAST_TOPIC_OP.UNION, 3);
	}

	public static void main(String[] args) throws IOException, ParseException {
		List<String> list = new ArrayList<>();
		list.add("xxd0QvSDCoRQuO0AuaLcoNqFGZr34ujFP7ultdCI9tk=");
		//list.add("Qw7MILqsNwI8Lt0NiUtXxLaapHG8z/OoP+VVeLdpoKo=");
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("code", "128010304");
		sendKuaiBaoMessage("全球学术快报", "TEST128010304", "", list, "1", map);
		
		map.put("code", "128020301");
		sendKuaiBaoMessage("全球学术快报", "TEST128020301", "", list, "1", map);
		
		map.put("odatatype", "CJFD");
		map.put("fileid", "LVKJ20170802000");
		//sendKuaiBaoMessage("全球学术快报", "文章详情", "", list, "2", map);
		
		// SIJI 21世纪  //dbcode=CFJD&type=journalinfo&id=RBZI&year=2015&issue=05
		map.put("titlepy", "RBZI");
		map.put("dbcode", "CFJD");
		map.put("_type", "journalinfo");
		map.put("year", "2015");
		map.put("issue", "05");
		//sendKuaiBaoMessage("全球学术快报", "整刊详情", "", list, "3", map);
		
		
		
		map.put("url", "https://www.baidu.com");
		//sendKuaiBaoMessage("全球学术快报", "打开URL", "", list, "5", map);
	}
}
