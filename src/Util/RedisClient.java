/**
 *  Copyright (c) 2015 Neusoft.com corporation All Rights Reserved.
 */

package Util;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 *  此处进行功能描述。
 *
 *  @author Hp
 *  @version 1.0
 *
 *  <pre>
 *  使用范例：
 *  创建时间:2015-1-23 下午12:37:51
 *  修改历史：
 *     ver     修改日期     修改人  修改内容
 * ──────────────────────────────────
 *   V1.00   2015-1-23   Hp  初版
 *
 *  </pre>
 *
 */
public class RedisClient {
	//http://www.cnblogs.com/linjiqin/archive/2013/06/14/3135248.html
    /*private static JedisPool jedisPool;//非切片连接池
    
    static{
    	initialPool();
    }
    
    *//**
	 * 获取Jedis实例
	 * @return
	 *//*
     public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                 Jedis jedis = jedisPool.getResource();
                 return jedis;
             } else {
                 return null;
             }
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
     }

     *//**
      * 释放jedis资源
      * @param jedis
      *//*
      public static void returnResource(final Jedis jedis) {
         if (jedis != null) {
             jedisPool.returnResource(jedis);
         }
      }
    *//**
     * 初始化非切片池
     *//*
	private static void initialPool() {
		// 池基本配置
		JedisPoolConfig config = new JedisPoolConfig();
		// config.setMaxActive(1024);
		config.setMaxIdle(200);
		// config.setMaxWait(1000l);
		config.setTestOnBorrow(true);
		jedisPool = new JedisPool(config, "127.0.0.1", 6379);
	}*/
	
	private static JedisPool pool;
	private static Jedis jedis;
	static {
		pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
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
	
	public static void push(String key, String value){
		//http://blog.kainaodong.com/?p=53
		//http://www.2cto.com/kf/201504/395403.html  jedispool 连 redis 高并发卡死
		//http://www.chongchonggou.com/g_425292966.html
		//http://www.sohu.com/a/112701466_468627
		//https://caorong.github.io/2015/01/13/about-dynamic-proxy/
		//https://www.bbsmax.com/A/KE5QLOXkJL/
		//http://shiguanghui.iteye.com/blog/1995390
			//http://blog.csdn.net/chenxiaodan_danny/article/details/41485581
				//google 静态类型的Jedis需要returnResource
		//异常最诡异第二期之 Jedis pool.returnResource(jedis) 
		//http://blog.csdn.net/coral0212/article/details/42261623
		//http://www.codeweblog.com/jedis-returnresource%E4%BD%BF%E7%94%A8%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9/
		//http://www.cnblogs.com/wangxin37/p/6397783.html
	}
	
	/**
	 * 测试使用
	 * @param args
	 */
	public static void main(String[] args) {
		 /*String key = "test_key";
	     List<String> list = null;
	        String value;
	        while (true) {
	        	try {
	                value = System.currentTimeMillis() + "";
	                System.out.println("push:" + value + ", return:"
	                        + JedisUtil.push(key, value));
	                System.out.println("push:" + value + ", return:"
	                        + JedisUtil.push(key, value));

	                // batch pop
	                try {
	                    // String str = JedisUtil.pop(key);
	                    // System.out.println("pop:" + str);
	                    // 使用redis管道批量lpop
	                    list = JedisUtil.batchPop(key, 3);
	                    if (list == null || list.isEmpty()) {
	                        System.out.println("empty list");
	                    } else {
	                        for (String str : list) {
	                            System.out.println("pop:" + str);
	                        }
	                    }
	                } catch (Exception e) {
	                    System.out.println(ExceptionUtil.parseString(e));
	                }

	                System.out.println("length of " + key + ":"
	                        + JedisUtil.llen(key));
	            } catch (Exception e) {
	                System.out.println(ExceptionUtil.parseString(e));
	            }
	            try {
	                Thread.sleep(1000);
	            } catch (InterruptedException e) {
	                System.out.println(ExceptionUtil.parseString(e));
	            }
	        }*/
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

