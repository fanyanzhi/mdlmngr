package Util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import Util.umengpush.AndroidListcast;
import Util.umengpush.AndroidNotification;
import Util.umengpush.PushClient;

/*
 * 1.type=1 为推送关注的快报信息更新信息，跳转到我的图书馆，其他参数：code为对应的行业分类代码
 * 2.type=2 为推荐的单篇信息，跳转到文献详情页，其他参数：odatatype为文献类别，fileid为文献id
 * 3.type=3 为推送的整刊信息，跳转到整刊详情页，其他参数：titlepy 为拼音刊名，_type 期刊类型，dbcode，year 期刊年份，issue 期号（示例见main方法）
 * 4.type=4 为文本类型，跳转到消息详情页，其他参数：id为消息id
 * 5.type=5 为链接，直接打开链接，其他参数：url为链接地址
 */
public class UmengSender {
	private static String APP_KEY = "598d20bd310c93692c00001e";
	private static String APP_MASTER_SECRET = "dqyzgdp601gplai4q1scovgsyjsj0dr0";
	private static PushClient client = new PushClient();

	/**
	 * 友盟消息推送    Android 多设备Token推送
	 * @param title		通知标题
	 * @param ticker	通知栏提示文字
	 * @param text		通知文字描述
	 * @param deviceTokens	設備ID
	 * @param type		1 表示 我的图书馆定制更新   2 为推荐的单篇信息
	 * @param map		其他参数
	 * @throws Exception
	 */
	public static void sendKuaiBaoMessage(String title,String ticker,String text,List<String> deviceTokens,String type,Map<String,String> map) throws Exception {
		AndroidListcast listcast = new AndroidListcast(APP_KEY,APP_MASTER_SECRET);
		StringBuilder sb = new StringBuilder();
		for (String deviceToken : deviceTokens) {
			sb.append(deviceToken+",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		listcast.setDeviceTokens(sb.toString());
		listcast.setTicker(ticker);
		listcast.setTitle(title);
		listcast.setText(text);
		JSONObject custom = new JSONObject();
		custom.put("type", type);
		
		 switch (type) {
	   		case "1":custom.put("code", map.get("code"));;break;
	   		case "2":custom.put("odatatype", map.get("odatatype"));
	   				custom.put("fileid", map.get("fileid"));
	   				break;
	   		case "3":custom.put("_type", map.get("_type"));
	   				custom.put("titlepy", map.get("titlepy"));
	   				custom.put("dbcode", map.get("dbcode"));
	   				custom.put("year", map.get("year"));
	   				custom.put("issue", map.get("issue"));
	   				break;
	   		case "4":custom.put("id", map.get("id"));break;
	   		case "5":custom.put("url", map.get("url"));break;
	   		case "6":custom.put("fromuser", map.get("fromuser"));
	   				custom.put("_type", map.get("type"));
	   				custom.put("id", map.get("id"));
	   				custom.put("message", text);
					custom.put("time", map.get("time"));
					break;
	   		default:break;
	   }
		listcast.goCustomAfterOpen(custom);
		listcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		//listcast.setTestMode();
		writeLog("emengsend","type:"+type+"");
		listcast.setProductionMode();
		client.send(listcast);
	}
	
	public static void writeLog(String folder,String data) {
		File file = new File("d:\\"+folder+".txt");
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
	
	public static void main(String[] args) {
		try {
			List<String> list = new ArrayList<String>(); 
		//	list.add("Aj0wNuGGiIWJqTGLVHnTUAj4X9-4mxNeJ2uYflrafKKK");
//			list.add("ApsLao29BxLXQ53eWrqpLJpdf8pu3TU0bQkZztaetaW8");
			//list.add("AubIOJm0t6Ytat6TBNS_OGpEZyugG48Dx85aU04GYTC-");
			//list.add("ApsLao29BxLXQ53eWrqpLJpGN5PUHOAcgnFZYkrSjjGh");
			list.add("AnoXJFB1fL6XH0JnP72_5ZDpgoPoZ1Wqpuu4CtPzamle");
			
			Map<String,String> map = new HashMap<String,String>();
			
			map.put("code", "128011001");
			sendKuaiBaoMessage("全球学术快报", "11", "我的图书馆", list, "1", map);
			
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

}
