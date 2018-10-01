package BLL;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.cnki.mngr.UserMngr;

import Util.Common;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MobilePhoneMngr {
	private static JedisPool pool;
	private static Jedis jedis;
	static {
		pool = new JedisPool(new JedisPoolConfig(), Common.GetConfig("RedisServer"));
		jedis = pool.getResource();
	}

	public static String createValidateCode(String phoneNum) {
		StringBuilder sbCode = new StringBuilder();
		Random random = new Random();
		for (int i = 6; i > 0; i--) {
			sbCode.append(random.nextInt(10));
		}
		Map<String, String> mapMsg = new HashMap<String, String>();
		mapMsg.put("code", sbCode.toString());
		mapMsg.put("phonenum", phoneNum);
		try{
			jedis.hmset("authcode_"+phoneNum, mapMsg);
		}catch(Exception e){
			//System.out.println(e.getMessage());
			pool = new JedisPool(new JedisPoolConfig(), Common.GetConfig("RedisServer"));
			jedis = pool.getResource();
			jedis.hmset("authcode_"+phoneNum, mapMsg);
		}
		jedis.expire("authcode_"+phoneNum, 180);
		return sbCode.toString();
	}

	public static boolean checkValidateCode(String phoneNum, String Code) {
		boolean bRet = false;
		if (jedis.exists("authcode_"+phoneNum)) {
			if (Code.equals(jedis.hmget("authcode_"+phoneNum, "code").get(0))){
				bRet = true;
			}
		}
		return bRet;
	}
	
	/**
	 * 测试使用
	 * @param args
	 */
	public static void main(String[] args) {
		Long time = System.currentTimeMillis() + 600000;
		System.out.println(time);
			//UserMngr.SendSMS("18210233647","12223456");
		writeLog(jedis.hmget("authcode_18210011924", "code").get(0));
	}
	
	public static void writeLog(String data) {
		File file = new File("d:\\redistxt.txt");
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


/*public class MobilePhoneMngr {
	private static Timer timer = null;
	private static Map<String, Map<String, String>> userCode = new HashMap<String, Map<String, String>>();
	static {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (userCode.size() > 0) {
					for (Map.Entry<String, Map<String, String>> entry : userCode.entrySet()) {
						if (System.currentTimeMillis() > Long.parseLong(entry.getValue().get("timestamp"))) {
							userCode.remove(entry.getKey());
						}
					}
				}
			}
			}, 0, 180000);
		}
	}

	public static String createValidateCode(String phoneNum) {
		StringBuilder sbCode = new StringBuilder();
		Random random = new Random();
		for (int i = 6; i > 0; i--) {
			sbCode.append(random.nextInt(10));
		}
		Map<String, String> mapMsg = new HashMap<String, String>();
		mapMsg.put("code", sbCode.toString());
		mapMsg.put("timestamp", String.valueOf(System.currentTimeMillis() + 600000));
		userCode.put(phoneNum, mapMsg);
		return sbCode.toString();
	}

	public static boolean checkValidateCode(String phoneNum, String Code) {
		boolean bRet = false;
		if (userCode.containsKey(phoneNum)) {
			if (Code.equals(userCode.get(phoneNum).get("code"))){
				bRet = true;
			}
		}
		return bRet;
	}
	
	*//**
	 * 测试使用
	 * @param args
	 *//*
	public static void main(String[] args) {
		Long time = System.currentTimeMillis() + 600000;
		System.out.println(time);
			UserMngr.SendSMS("18210233647","12223456");
	}
}*/

