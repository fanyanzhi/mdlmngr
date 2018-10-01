package BLL;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class EcpMngr {
	public static String ecpUrl = "http://oauth.cnki.net:8450";

	/**
	 * 获取token
	 * 
	 * @return
	 */
	public static String getEcpToken() {
		String ecpUrl = "http://oauth.cnki.net/Auth/oauth/token";
		String client_id = "96d4368cd99d496d";
		String client_secret = "54b0c4ba652c4b4ea99d802490edbf1c";
		String strParam = "grant_type=client_credentials&client_id=".concat(client_id).concat("&client_secret=")
				.concat(client_secret);
		JSONObject json = getServerResult(ecpUrl, null, strParam, null, null);
		return json.getString("access_token");
	}

	/**
	 * 用户名密码登录
	 * 
	 * @param userName
	 * @param password
	 * @param ip
	 * @param logonType
	 * @param longitude
	 * @param latitude
	 * @param expireDay
	 */
	public static JSONObject userLoginByPassword(String userName, String password, String ip, String logonType) {
		String reqUrl = ecpUrl + "/login/login?";
		String param = "username=" + userName + "&password=" + password + "&ip=" + ip;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, param, null, null);
		return json;
	}

	/**
	 * ip登录
	 * 
	 * @param userName
	 * @param password
	 * @param ip
	 * @param logonType
	 * @return
	 */
	public static JSONObject userLoginByIP(String userName, String password, String ip, String logonType) {
		String reqUrl = ecpUrl + "/login/login?";
		String param = "username=" + userName + "&password=" + password + "&ip=" + ip + "&logonType=" + logonType;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, param, null, null);
		return json;
	}

	/**
	 * LBS登录
	 * 
	 * @param userName
	 * @param password
	 * @param ip
	 * @param logonType
	 * @param longitude
	 * @param latitude
	 * @param expireDay
	 * @return
	 */
	public static JSONObject userLoginByLBS(String userName, String password, String ip, String logonType,
			double longitude, double latitude, int expireDay) {
		String reqUrl = ecpUrl + "/login/login?";
		String param = "username=" + userName + "&password=" + password + "&ip=" + ip + "&logonType=" + logonType
				+ "&longitude=" + longitude + "&latitude=" + latitude;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, param, null, null);
		return json;

	}

	/**
	 * uid 登录
	 * 
	 * @param idenId
	 * @param ip
	 * @return {"Message":"An error has occurred","ExceptionMessage":
	 *         "Object reference not set to an instance of an object."}
	 */
	public static JSONObject loginByUid(String idenId, String ip) {
		String reqUrl = ecpUrl + "/Login/login_by_uid";
		JSONObject param = new JSONObject();
		param.put("IdenId", idenId);
		param.put("Ip", ip);
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl, token, param.toString(), null, "json");
		return json;
	}

	/**
	 * 退出登录
	 * 
	 * @param idenId
	 * @param ip
	 * @return {"Success":true,"Code":1,"Message":""}
	 */
	public static JSONObject loginOut(String idenId, String ip) {
		String reqUrl = ecpUrl + "/Login/logout";
		JSONObject param = new JSONObject();
		param.put("IdenId", idenId);
		param.put("Ip", ip);
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl, token, param.toString(), null, "json");
		return json;
	}

	/**
	 * 第三方登录
	 * 
	 * @param thirdUserId
	 * @param thirdName
	 * @param ip
	 * @return {"isExist":true,"Success":true,"Code":0,"Message":null,"IdenId":
	 *         "WEEvREcwSlJHSldTTEYzVnB3aUF6VG0ybkJnTDNkRW5TeDNnOWxuMndYQT0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4ggI8Fm4gTkoUKaID8j8gFw!!"
	 *         ,"Username":"zhu_zhu18"}
	 *         {"isExist":false,"Success":false,"Code":0,"Message":null,"IdenId"
	 *         :null,"Username":null}
	 */
	public static JSONObject loginByThird(String thirdUserId, String thirdName, String ip) {
		String reqUrl = ecpUrl + "/Login/login_by_third?";
		String param = "thirdUserId=" + thirdUserId + "&thirdName=" + thirdName + "&userIp=" + ip;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}

	/**
	 * 第三方凭证绑定到用户 --》未测试
	 * 
	 * @param thirdUserId
	 *            第三方登录标识
	 * @param thirdName
	 *            第三方名称，有效的值：qq,sina,163,weixin,wx_gz
	 * @param userName
	 *            知网用户名
	 * @param clientType
	 *            客户端类型，有效的值：web,wap,ios,android
	 * @param ip
	 *            用户客户端ip
	 * @return {"Success":true,"Message":""}
	 */
	public static JSONObject bindToUser(String thirdUserId, String thirdName, String userName, String clientType,
			String ip) {
		// http://192.168.100.132/Apis/Login/bind_user?thirdUserId=dsffs&thirdName=sfsd&userName=sdfsd&clientType=sfd&userIp=sfsfd
		String reqUrl = ecpUrl + "/Login/bind_user?";
		String param = "thirdUserId=" + thirdUserId + "&thirdName=" + thirdName + "&userName=" + userName
				+ "&clientType=" + clientType + "&userIp=" + ip;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}

	/**
	 * 获取绑定到用户的第三方列表
	 * 
	 * @return [{"UserName":"zhu_zhu18","ThirdUserId":
	 *         "oaH1TxBvCFxgt6ZqAGWAq2Wx2xZ4","ThirdName":"weixin","PostTime":
	 *         "2017-08-14T15:17:10","Client":"ios"},{"UserName":"zhu_zhu18",
	 *         "ThirdUserId":"1ADD1FAB8B92BD23065F91C7C0190C09","ThirdName":"qq"
	 *         ,"PostTime":"2017-08-11T16:48:16","Client":"ios"}]
	 */
	public static String getBindList(String userName) {
		String reqUrl = ecpUrl + "/Login/get_bind_list?";
		String param = "username=" + userName;
		String token = getEcpToken();
		String result = sendGet(reqUrl + param, token, null);
		return result;
	}

	/**
	 * 第三方账号解绑
	 * 
	 * @param userName
	 * @param thirdUserId
	 * @return {"Success":true,"Code":1,"Message":"解绑成功"}
	 */
	public static JSONObject removeBind(String userName, String thirdUserId) {
		// http://192.168.100.132/Apis/Login/remove_bind
		String reqUrl = ecpUrl + "/Login/remove_bind?";
		JSONObject param = new JSONObject();
		param.put("UserName", userName);
		param.put("ThirdUserId", thirdUserId);
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl, token, param.toString(), null, "json");
		return json;

	}

	/**
	 * 注册个人账号,支持邮箱注册和手机注册
	 * 
	 * @param password
	 * @param ip
	 * @param userName
	 * @param regType
	 * @param email
	 *            email需要编码
	 * @param mobile
	 * @param verifyCode
	 * @param platform
	 *            platform需要设置静态变量
	 * @param superPassword
	 *            {"Success":true,"Code":0,"Message":null} tuguoguo
	 *            {"Success":false,"Code":0,"Message":"注册失败!用户名已经存在！"} guoguotu
	 *            platform {"Success":true,"Code":0,"Message":null} guotuguo
	 *            888888 platform
	 */
	public static JSONObject userCreate(String password, String ip, String userName, String regType, String email,
			String mobile, String verifyCode, String platform, String superPassword) {
		String reqUrl = ecpUrl + "/Register/create?";
		String param = "password=" + password + "&ip=" + ip + "&username=" + userName + "&regType=" + regType
				+ "&email=" + email + "&mobile=" + mobile + "&verifyCode=" + verifyCode + "&platform=" + platform
				+ "&superPassword=" + superPassword;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}

	/**
	 * 用户名是否存在
	 * 
	 * @param userName
	 * @return
	 */
	public static JSONObject isUserNameExist(String userName) {
		String reqUrl = ecpUrl + "/Register/is_userame_exist?";
		String param = "userName=" + userName;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}

	/**
	 * 发送手机验证码
	 * 
	 * @param mobile
	 * @param app
	 *            需要编码
	 * @return {"Success":true,"Code":"313841","Message":"发送成功","SendTimes":1}
	 */
	public static JSONObject sendVerifyCode(String mobile, String app) {
		String reqUrl = ecpUrl + "/Register/send_verify_code?";
		String param = "to=" + mobile + "&app=" + app;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}

	/**
	 * 修改密码
	 * 
	 * @param userName
	 * @param password
	 * @param newPassword
	 * @return
	 */
	public static JSONObject changePassword(String userName, String password, String newPassword) {
		String reqUrl = ecpUrl + "/User/change_password?";
		String param = "username=" + userName + "&password=" + password + "&newPassword=" + newPassword;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}

	/**
	 * 获取用户基本信息
	 * 
	 * @param userName
	 * @return
	 */
	public static String getUserBaseInfo(String userName) {
		String reqUrl = ecpUrl + "/User/get_user_base_info?";
		String param = "username=" + userName;
		String token = getEcpToken();
		String result = sendGet(reqUrl + param, token, null);
		return result;
	}

	public static String getUserInfo(String userName) {
		String reqUrl = ecpUrl + "/User/get_user_info?";
		String param = "username=" + userName;
		String token = getEcpToken();
		String result = sendGet(reqUrl + param, token, null);
		return result;
	}

	public static boolean isInstitutionAccount(String userName) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setAllowNonStringKeys(true);
		//jsonConfig.setIgnoreDefaultExcludes(true);
		String str = getUserInfo(userName);
		System.out.println(str);
		JSONObject json = JSONObject.fromObject(str,jsonConfig);
		//JSONObject jsonObject = JSONObject.fromObject(str.replaceAll("\r", "").replaceAll("\n", ""));
		String userTypeID = JSONObject.fromObject(json.get("BaseInfo")).getString("UserTypeID");
		System.out.println(userTypeID);
		String IsFeeFlag = JSONObject.fromObject(json.get("CtrlInfo")).getString("IsFeeFlag");
		System.out.println(IsFeeFlag);
		return "2".equals(IsFeeFlag) || "2".equals(userTypeID) || "13".equals(userTypeID) || "228".equals(userTypeID);
	}

	/**
	 * 用户绑定、解绑
	 * 
	 * @param userName
	 *            主绑定用户名（比如：个人用户）
	 * @param parentName
	 *            被绑定用户名（比如：机构用户）
	 * @param bindType
	 *            绑定类型：1，绑定；2，取消绑定；3，处理漫游
	 * @isAllowRoam 是否允许漫游，默认为false
	 * @return {"Success":true,"Code":1,"Message":""}
	 */
	public static JSONObject bindUser(String userName, String parentName, int bindType, String isAllowRoam) {
		String reqUrl = ecpUrl + "/User/bind_user?";
		String param = "username=" + userName + "&parentName=" + parentName + "&bindType=" + bindType + "&isAllowRoam="
				+ isAllowRoam;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}

	/**
	 * 之前注册的未验证手机的老用户绑定手机号--> 重新绑定--》中心网站绑定过，咱这边提示没绑定。周一来了跟一下
	 * 
	 * @param userName
	 * @param mobile
	 * @param verifyCode
	 * @return {"Success":false,"Code":0,"Message":"验证码不正确！"}
	 */
	public static JSONObject bindUserToMobile(String userName, String mobile, String verifyCode) {
		String reqUrl = ecpUrl + "/User/bind_user_to_mobile?";
		String param = "username=" + userName + "&mobile=" + mobile + "&verifyCode=" + verifyCode;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}

	/**
	 * 用户是否已绑定手机(已实名认证)
	 * 
	 * @param userName
	 * @return {"IsBind":true,"Mobile":null}
	 */
	public static JSONObject isBindToMobile(String userName) {
		String reqUrl = ecpUrl + "/User/is_bind_to_mobile?";
		String param = "username=" + userName;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}

	/**
	 * 获取用户账户余额信息
	 * 
	 * @param userName
	 * @param moneyType
	 *            这个怎么传，有点疑问
	 * @param rootId
	 * @return
	 */
	public static String getUserBalance(String userName, String moneyType, String rootId) {
		String reqUrl = ecpUrl + "/User/get_user_balance?";
		String param = "username=" + userName + "&moneyType=" + moneyType;
		if (rootId != null)
			param = param + "&rootId=" + rootId;
		String token = getEcpToken();
		String result = sendGet(reqUrl + param, token, null);
		return result;
	}

	public static String findIp(String ip) {
		String reqUrl = ecpUrl + "/IPHelper/Find?";
		String param = "ip=" + ip;
		String token = getEcpToken();
		long t1 = System.currentTimeMillis();
		String result = sendGet(reqUrl + param, token, null);
		long t2 = System.currentTimeMillis();
		long span = t2 - t1;
		System.out.println(span);
		return result;
	}

	public static String sendGet(String url, String token, Map<String, String> headers) {
		String str = "";
		HttpClient client = new DefaultHttpClient();
		try {
			HttpGet httpGet = new HttpGet(url);
			if (token != null)
				httpGet.addHeader("authorization", "Bearer ".concat(token));
			if (headers != null && headers.size() > 0) {
				Set<String> keys = headers.keySet();
				for (Iterator<String> i = keys.iterator(); i.hasNext();) {
					String key = (String) i.next();
					httpGet.addHeader(key, headers.get(key));
					httpGet.setHeader(key, headers.get(key));
				}
			}
			HttpResponse hr = client.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he, "utf-8");
			}
			httpGet.abort();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return str;
	}

	public static JSONObject getServerResult(String url, String token, String params, Map<String, String> headers,
			String type) {
		String jsonResult = sendPost(url, token, params, null, type);
		return JSONObject.fromObject(jsonResult.replaceAll("\r", "").replaceAll("\n", ""));
	}

	private static String sendPost(String url, String token, String params, Map<String, String> headers, String type) {
		String str = "";
		HttpClient hc = new DefaultHttpClient();
		try {
			HttpPost httpPost = new HttpPost(url);
			if (token != null)
				httpPost.addHeader("authorization", "Bearer ".concat(token));
			if ("json".equals(type)) {
				httpPost.addHeader("Content-type", "application/json; charset=utf-8");
				httpPost.setHeader("Accept", "application/json");
			}
			if (headers != null && headers.size() > 0) {
				Set<String> keys = headers.keySet();
				for (Iterator<String> i = keys.iterator(); i.hasNext();) {
					String key = (String) i.next();
					httpPost.addHeader(key, headers.get(key));
				}
			}
			if (params != null)
				httpPost.setEntity(new StringEntity(params.toString(), "utf-8"));
			HttpResponse hr = null;
			hr = hc.execute(httpPost);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he, "utf-8");
			} else {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he, "utf-8");
			}
			httpPost.abort();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hc.getConnectionManager().shutdown();
		}
		return str;
	}
	
	/**
	 * 根据手机号修改密码
	 * @param username
	 * @param mobile
	 * @param newPassword
	 * @return
	 */
	public static JSONObject resetpwdbymobile(String username, String mobile, String newPassword){
		String reqUrl = ecpUrl + "/User/reset_password_by_mobile?";
		String param = "username=" + username + "&mobile=" + mobile + "&newPassword=" + newPassword;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}
	
	/**
	 * 根据email重置密码
	 * @param username
	 * @param email
	 * @param newPassword
	 * @return
	 */
	public static JSONObject resetpwdbyemail(String username, String email, String newPassword){
		String reqUrl = ecpUrl + "/User/reset_password_by_email?";
		String param = "username=" + username + "&email=" + email + "&newPassword=" + newPassword;
		String token = getEcpToken();
		JSONObject json = getServerResult(reqUrl + param, token, null, null, null);
		return json;
	}
}
