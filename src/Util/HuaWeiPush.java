package Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/*
 * 1.type=1 为推送关注的快报信息更新信息，跳转到我的图书馆，其他参数：code为对应的行业分类代码
 * 2.type=2 为推荐的单篇信息，跳转到文献详情页，其他参数：odatatype为文献类别，fileid为文献id
 * 3.type=3 为推送的整刊信息，跳转到整刊详情页，其他参数：titlepy 为拼音刊名，_type，dbcode，year，issue
 * 4.type=4 为文本类型，跳转到消息详情页，其他参数：id为消息id
 * 5.type=5 为链接，直接打开链接，其他参数：url为链接地址
 */
public class HuaWeiPush {
	protected static Logger log = Logger.getLogger(HuaWeiPush.class);
	
	 private static String appSecret = "f6df967ef73d7e3063489f223386279f";
	    private static  String appId = "10698974";//用户在华为开发者联盟申请的appId和appSecret（会员中心->应用管理，点击应用名称的链接）
	    private static  String tokenUrl = "https://login.vmall.com/oauth2/token"; //获取认证Token的URL
	    private static  String apiUrl = "https://api.push.hicloud.com/pushsend.do"; //应用级消息下发API
	    private static  String accessToken;//下发通知消息的认证Token
	    private static  long tokenExpiredTime;  //accessToken的过期时间
	    /**
	     * 华为消息推送   
	     * @param title 	消息标题
	     * @param content	消息内容
	     * @param deviceTokens	推送设备 单次最多只是1000个
	     * @param type		1表示 我的图书馆定制更新   2 为推荐的单篇信息
	     * @param map		其他参数 
	     * @throws IOException
	     */
	    public static void pushKuaiBaoMsg(String title, String content, List<String> devices,String type ,Map<String,String> map) throws IOException
	    {
	        refreshToken();
	        sendPushMessage(title,content,devices,type,map);
	    }
	    
	    //获取下发通知消息的认证Token
	    private static void refreshToken() throws IOException
	    {
	        String msgBody = MessageFormat.format(
	        		"grant_type=client_credentials&client_secret={0}&client_id={1}", 
	        		URLEncoder.encode(appSecret, "UTF-8"), appId);
	        String response = httpPost(tokenUrl, msgBody, 5000, 5000);
	        JSONObject obj = JSONObject.fromObject(response);
	        accessToken = obj.getString("access_token");
	        System.out.println(accessToken);
	        tokenExpiredTime = System.currentTimeMillis() + obj.getLong("expires_in") * 1000;
	    }
	    
	    //发送Push消息
	    private static  void sendPushMessage(String title, String content, List<String> devices,String type ,Map<String,String> map) throws IOException
	    {
	        if (tokenExpiredTime <= System.currentTimeMillis())
	        {
	            refreshToken();
	        }      
	        /*PushManager.requestToken为客户端申请token的方法，可以调用多次以防止申请token失败*/
	        /*PushToken不支持手动编写，需使用客户端的onToken方法获取*/
	        JSONArray deviceTokens = new JSONArray();//目标设备Token
	        for (String device : devices) {
	        	deviceTokens.add(device);
			}
	          
	        JSONObject body = new JSONObject();//仅通知栏消息需要设置标题和内容，透传消息key和value为用户自定义
	        body.put("title", title);//消息标题
	        body.put("content", content);//消息内容体
	        
	        JSONObject ext = new JSONObject();//扩展信息，含BI消息统计，特定展示风格，消息折叠。
	        ext.put("biTag", "Trump");//设置消息标签，如果带了这个标签，会在回执中推送给CP用于检测某种类型消息的到达率和状态
	        JSONArray js = new JSONArray();
	        JSONObject param0 = new JSONObject();
	        JSONObject param_intent = new JSONObject();
	        param0.put("type", type);
	        param_intent.put("type", type);
	        js.add(param0);
	        boolean typeError=false;
	        switch (type) {
		   		case "1":
			        JSONObject param11 = new JSONObject();
			        param11.put("code", map.get("code"));
			        param_intent.put("code", map.get("code"));
			        js.add(param11); 
		   			break;
		   		case "2":
		   			JSONObject param21 = new JSONObject();
			        param21.put("odatatype", map.get("odatatype"));
			        param_intent.put("odatatype", map.get("odatatype"));
			        JSONObject param22 = new JSONObject();
			        param22.put("fileid", map.get("fileid"));
			        param_intent.put("fileid", map.get("fileid"));
			        js.add(param21); 
			        js.add(param22); 
		   			break;
		   		case "3":
		   			JSONObject param31 = new JSONObject();
			        param31.put("_type", map.get("_type"));
			        param_intent.put("_type", map.get("_type"));
			        JSONObject param32 = new JSONObject();
			        param32.put("titlepy", map.get("titlepy"));
			        param_intent.put("titlepy", map.get("titlepy"));
			        JSONObject param33 = new JSONObject();
			        param33.put("year", map.get("year"));
			        param_intent.put("year", map.get("year"));
			        JSONObject param34 = new JSONObject();
			        param34.put("dbcode", map.get("dbcode"));
			        param_intent.put("dbcode", map.get("dbcode"));
			        JSONObject param35 = new JSONObject();
			        param35.put("issue", map.get("issue"));
			        param_intent.put("issue", map.get("issue"));
			        js.add(param31); 
			        js.add(param32);
			        js.add(param33);
			        js.add(param34);
			        js.add(param35);
		   			break;
		   		case "4":
		   			JSONObject param41 = new JSONObject();
			        param41.put("id", map.get("id"));
			        param_intent.put("id", map.get("id"));
			        js.add(param41); 
			        break;
		   		case "5":
		   			JSONObject param51 = new JSONObject();
			        param51.put("url", map.get("url"));
			        param_intent.put("url", map.get("url"));
			        js.add(param51); 
		   			break;
		   		case "6":
		   			JSONObject param61 = new JSONObject();
		   			param61.put("fromuser", map.get("fromuser"));
		   			param_intent.put("fromuser", map.get("fromuser"));
		   			JSONObject param62 = new JSONObject();
		   			param62.put("_type", map.get("type"));
		   			param_intent.put("_type", map.get("type"));
		   			JSONObject param63 = new JSONObject();
		   			param63.put("id", map.get("id"));
		   			param_intent.put("id", map.get("id"));
		   			JSONObject param64 = new JSONObject();
		   			param64.put("message", content);
		   			param_intent.put("message", content);
		   			JSONObject param65 = new JSONObject();
		   			param65.put("time", map.get("time"));
		   			param_intent.put("time", map.get("time"));
			        js.add(param61); 
			        js.add(param62);
			        js.add(param63);
			        js.add(param64);
			        js.add(param65);
		   			break;
		   		default:
		   			typeError = true;
		   			break;
	        }
	        
	        ext.put("customize" ,js);
	        //ext.put("icon", "http://m.cnki.net/mcnkidown/images/kuai-logo.png");//自定义推送消息在通知栏的图标,value为一个公网可以访问的URL
	        
	        JSONObject param = new JSONObject();
	        JSONObject action = new JSONObject();
	        if(typeError) {
	        	action.put("type", 3);
	   			param.put("appPkgName", "com.cnki.android.cnkimobile");//定义需要打开的appPkgName
	        }else{
	        	action.put("type", 1);//类型3为打开APP，其他行为请参考接口文档设置
	        	param.put("intent", "scheme_cnkimobile://com.cnki.mobile/notify_detail?"+param_intent.toString());
	        }
	        action.put("param", param);//消息点击动作参数
	        
	        JSONObject msg = new JSONObject();
	        msg.put("type", 3);//3: 通知栏消息，异步透传消息请根据接口文档设置
	        msg.put("action", action);//消息点击动作
	        msg.put("body", body);//通知栏消息body内容
	        
	        
	        JSONObject hps = new JSONObject();//华为PUSH消息总结构体
	        hps.put("msg", msg);
	        hps.put("ext", ext);
	        
	        JSONObject payload = new JSONObject();
	        payload.put("hps", hps);
	        
	        String postBody = MessageFormat.format(
	        	"access_token={0}&nsp_svc={1}&nsp_ts={2}&device_token_list={3}&payload={4}",
	            URLEncoder.encode(accessToken,"UTF-8"),
	            URLEncoder.encode("openpush.message.api.send","UTF-8"),
	            URLEncoder.encode(String.valueOf(System.currentTimeMillis() / 1000),"UTF-8"),
	            URLEncoder.encode(deviceTokens.toString(),"UTF-8"),
	            URLEncoder.encode(payload.toString(),"UTF-8"));
	        
	        String postUrl = apiUrl + "?nsp_ctx=" + URLEncoder.encode("{\"ver\":\"1\", \"appId\":\"" + appId + "\"}", "UTF-8");
	        String result = httpPost(postUrl, postBody, 5000, 5000);
	        if(!result.contains("80000000")){
	        	System.out.println("华为推送result："+result);
	        	log.info("华为推送result："+result);
	        }else{
	        	System.out.println(result);
	        }
	    }
	    
	    public static String httpPost(String httpUrl, String data, int connectTimeout, int readTimeout) throws IOException
	    {
	        OutputStream outPut = null;
	        HttpURLConnection urlConnection = null;
	        InputStream in = null;
	        
	        try
	        {
	            URL url = new URL(httpUrl);
	            urlConnection = (HttpURLConnection)url.openConnection();          
	            urlConnection.setRequestMethod("POST");
	            urlConnection.setDoOutput(true);
	            urlConnection.setDoInput(true);
	            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	            urlConnection.setConnectTimeout(connectTimeout);
	            urlConnection.setReadTimeout(readTimeout);
	            urlConnection.connect();
	            
	            // POST data
	            outPut = urlConnection.getOutputStream();
	            outPut.write(data.getBytes("UTF-8"));
	            outPut.flush();
	            
	            // read response
	            if (urlConnection.getResponseCode() < 400)
	            {
	                in = urlConnection.getInputStream();
	            }
	            else
	            {
	                in = urlConnection.getErrorStream();
	            }
	            
	            List<String> lines = IOUtils.readLines(in, urlConnection.getContentEncoding());
	            StringBuffer strBuf = new StringBuffer();
	            for (String line : lines)
	            {
	                strBuf.append(line);
	            }
	            return strBuf.toString();
	        }
	        finally
	        {
	            IOUtils.closeQuietly(outPut);
	            IOUtils.closeQuietly(in);
	            if (urlConnection != null)
	            {
	                urlConnection.disconnect();
	            }
	        }
	    }
	    
	    public static void main(String[] args) throws IOException {
	    	//scheme_cnkimobile://com.cnki.mobile/notify_detail?{"type":"1","code":"128010304"}//放射化学

	    	//scheme_cnkimobile://com.cnki.mobile/notify_detail?{"type":"1","code":"128020301"}//经济学理论
	    	List<String> list = new ArrayList<>();
			list.add("0867689025098908300000647900CN01");
			list.add("0867919027160277300000647900CN01");
			Map<String,String> map = new HashMap<String,String>();
			
			map.put("code", "128020301");
	    	pushKuaiBaoMsg("全球学术快报","我的图书馆",list,"1",map);
		}
	    
	    public static void writeLog(String folder, String data) {
			File file = new File("d:\\" + folder + ".txt");
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
