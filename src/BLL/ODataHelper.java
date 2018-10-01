package BLL;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import Util.Common;
import Util.SHA1;
import net.sf.json.JSONObject;

public class ODataHelper {
	static String ODataUrl = "http://api2.cnki.net/v20/";
	static String appId = Common.GetConfig("newAppId");// "odata_us3";//
	static String appKey = Common.GetConfig("newAppKey");// "b8MhHQF1GbA31gze";//
	static String did = "{123456}";
	static String mobile = "";
	static String location = "0,0";
	static String ip = "";

	public static JSONObject GetObjDataLists(String type, String fields, String query, String order, String group,
			int start, int length) {
		/*
		 * if(type.equalsIgnoreCase("Literature")) type =
		 * "Literature{CJFD,CDFD,CMFD,CCND,CPFD}";
		 */
		// System.out.println("order:"+order);
		// 检索的条件（OData语法）
		// String query = "";// "Title eq '“盘活”农村宅基地的思考'";
		// 分组依据
		// String group = "";
		// 排序依据
		// String order = "Issue desc";
		// // 数据的开始位置（和下面的参数配合，构建分页的依据）
		// int start = start;
		// // 获取数据的条数
		// int length = length;
		// 时间戳
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		// 加密指纹（注意：指纹加密的数据都是未经过URL编码前的数据）
		String txt = "timestamp=" + timestamp + "&appid=" + appId + "&appkey=" + appKey + "&ip=" + ip + "&location="
				+ location + "&mobile=" + mobile + "&did=" + did + "&op=data_gets&type=" + type + "&fields=" + fields
				+ "&query=" + query + "&group=" + group + "&order=" + order;
		SHA1 sha1 = new SHA1();
		String sign = sha1.Digest(txt, "UTF-8").toLowerCase();
		// 请求地址 /api/db/{OData对象的名称}
		String remoteUrl = "";
		try {
			remoteUrl = ODataUrl + "api/db/" + URLEncoder.encode(type, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (!Common.IsNullOrEmpty(fields)) {
			try {
				remoteUrl = remoteUrl + "?fields=" + URLEncoder.encode(fields, "utf-8") + "&query="
						+ URLEncoder.encode(query, "utf-8") + "&group=" + group + "&order="
						+ URLEncoder.encode(order, "utf-8") + "&start=" + String.valueOf(start) + "&length="
						+ String.valueOf(length);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		Map<String, String> mapHeader = new TreeMap<String, String>();
		mapHeader.put("app_id", appId);
		mapHeader.put("timestamp", timestamp);
		mapHeader.put("sign", sign);
		mapHeader.put("ip", ip);
		mapHeader.put("did", did);
		mapHeader.put("mobile", mobile);
		mapHeader.put("location", location);
		// mapHeader.put("Content-Type", "application/xml");
		JSONObject JsonSeaData = sendGet(remoteUrl, mapHeader);
		return JsonSeaData;
	}

	public static JSONObject sendGet(String RemoteUrl, Map<String, String> MapHeader) {
		JSONObject jsonSeaData = null;
		String strResult = "";
		int iTime = 5;
		while (iTime > 0) {
			iTime--;
			strResult = sendHttpGet(RemoteUrl, MapHeader);
			if (strResult.startsWith("err:"))
				continue;
			jsonSeaData = JSONObject.fromObject(strResult);
			if (jsonSeaData != null && (Integer) jsonSeaData.get("ErrorCode") == 0)
				return jsonSeaData;
		}
		if (jsonSeaData == null && strResult.startsWith("err:")) {
			try {
				jsonSeaData = JSONObject.fromObject(strResult.substring(4));
			} catch (Exception e) {
				return null;
			}

		}
		return jsonSeaData;
	}

	public static String sendHttpGet(String RemoteUrl, Map<String, String> MapHeader) {
		try {
			HttpClient hc = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(RemoteUrl);
			if (MapHeader != null && MapHeader.size() > 0) {
				for (Map.Entry<String, String> entry : MapHeader.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// httpGet.setHeader("Content-Type", "application/xml");
			HttpResponse hr = hc.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			HttpEntity he = hr.getEntity();
			String str = EntityUtils.toString(he);
			if (code == 200) {
				return str;
			}else {
				httpGet.abort();
			}
			return "err:" + str;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "err:";
	}
}
