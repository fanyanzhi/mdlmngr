package Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import BLL.Logger;
import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Util.Common;
import Util.RedisUtil;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class KFDReadExDownloadServlet
 */
@WebServlet("/kfdreadexdownload/*")
public class KFDReadExDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public KFDReadExDownloadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");

		String fileInfo = request.getHeader("request-action"); // 判断是否是在线阅读
		String strAction = request.getPathInfo().replace("/", "").toLowerCase(); // 操作
		if (!"getfile".equals(strAction)) {
			if (Common.IsNullOrEmpty(fileInfo)) {
				sendResponseData(response, "{\"result\":false,\"message\":\""
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errcode\":")
						.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
				return;
			}
		}
		String range = request.getHeader("accept-range");
		System.out.println("range1=" + range);
		if (Common.IsNullOrEmpty(range)) {
			System.out.println("range2=" + range);
			range = request.getHeader("Range");
		}
		System.out.println("range3=" + range);
		// 参数
		String fileid = request.getParameter("fileid");
		String filetype = request.getParameter("filetype");
		String typeid = request.getParameter("typeid");
		String token = request.getParameter("usertoken");
		String sign = request.getParameter("sign");
		String expire = request.getParameter("expire");
		String path = request.getParameter("path");
		String discno = request.getParameter("discno");
		boolean prior = "1".equals(request.getParameter("prior"));

		String userName = "";
		// 返回值
		String strRet = ""; // 返回值
		// boolean bolChekTime = true; // 判断时间戳

		if (Common.IsNullOrEmpty(token)) {
			sendResponseData(response,
					"{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code))
							.concat("\",\"errorcode\":")
							.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_TOKEN.code)).concat("}"));
			return;
		} else {
			userName = UserInfoMngr.UserLogin(token);
			if (userName.startsWith("@-")) {
				response.setStatus(500);
				strRet = "{\"result\":false,\"message\":\"".concat(userName.substring(1)).concat("\",\"errorcode\":")
						.concat(userName.substring(1)).concat("}");
				response.setHeader("error", strRet);
				sendResponseData(response, strRet);
				return;
			}
		}
		/*
		 * long lCurTime = System.currentTimeMillis(); if
		 * (Long.parseLong(expire) < (lCurTime - 60000L * 60 * 12) ||
		 * Long.parseLong(expire) > (lCurTime + 60000L * 60 * 12) ||
		 * !sign.equals(Common.SHA1(expire.concat("cnkiexpress")))) {
		 * response.setStatus(500); strRet = "{\"result\":false,\"message\":\""
		 * .concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_URLTIME.code)).
		 * concat("\",\"errorcode\":")
		 * .concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_URLTIME.code)).
		 * concat("}"); response.setHeader("error", strRet);
		 * sendResponseData(response, strRet); return; }
		 */
		Map<String, Object> mapFile = new HashMap<String, Object>();
		mapFile.put("range", range);
		mapFile.put("usertoken", token);
		mapFile.put("path", path);
		mapFile.put("fileid", fileid);
		mapFile.put("typeid", typeid);
		mapFile.put("filetype", filetype);
		mapFile.put("discno", discno);
		mapFile.put("prior", prior);
		
		if (!Common.IsNullOrEmpty(fileInfo) && "fileinfo".equalsIgnoreCase(fileInfo)) {
			sendResponseData(response, getFileXML(userName, mapFile, request, true));
		} else if (!Common.IsNullOrEmpty(fileInfo) && "client-quit".equalsIgnoreCase(fileInfo)) {
			return;
		} else { // caj或者全文后台出错，直接从原文下载
			kfdReadexDownload(mapFile, userName, request, response);
		}

		if (Common.IsNullOrEmpty(strRet)) {
			return;
		}
		if (strRet != null && strRet.startsWith("{\"result\":false")) {
			response.setStatus(500);
			response.setHeader("error", strRet);
		}
		sendResponseData(response, strRet);
	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	private void sendResponseData(HttpServletResponse response, byte[] DataBytes) throws IOException {
		response.getOutputStream().write(DataBytes);
		response.getOutputStream().close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * 在线阅读xml
	 * http://192.168.51.40:8080/mdlmngr/kfdreadexdownload/getfile?op=download&usertoken=7620abed81e05380fb5ee13004f04f2ch2Rme1xUidUlqMzKmbA7b2Fz93BJ64uylZj4ksgE6o7Yev1CWvrDjA==&fileid=FJKS201504003&typeid=cjfd&filename=FJKS201504003&filetype=caj&expire=1429594399507&sign=9541ebd299cc9c82d1cf512de1e0bf77a41462e1&discno=SCTB1505
	 * @param UserName
	 * @param arg
	 * @param request
	 * @param isAddInfo
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private byte[] getFileXML(String UserName, Map<String, Object> arg, HttpServletRequest request, boolean isAddInfo)
			throws UnsupportedEncodingException {
		String strFileID = (String) arg.get("fileid");
		String discNo = (String) arg.get("discno");
		String typeID = (String) arg.get("typeid");
		String fileType = (String) arg.get("filetype");
		String strToken = (String) arg.get("usertoken");
		boolean prior = (boolean) arg.get("prior");

		Map<String, Object> mapFileInfo = null;
		try {
			// mapFileInfo = kfdresReaderExfileinfo("FJKS201504003",
			// "caj","SCTB1505", "cjfd", "");
			mapFileInfo = kfdresReaderExfileinfo(strFileID, fileType, discNo, typeID, "", prior);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String strFileSize = String.valueOf(mapFileInfo.containsKey("length") ? mapFileInfo.get("length") : "");
		String strTitle = String.valueOf(mapFileInfo.containsKey("filename") ? mapFileInfo.get("filename") : "");
		String strFileMd5 = String.valueOf(mapFileInfo.containsKey("filemd5") ? mapFileInfo.get("filemd5") : "");
		String path = String.valueOf(mapFileInfo.containsKey("url") ? mapFileInfo.get("url") : "");
		String strFileName = strTitle;
		Document doc = null;
		Element root = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Logger.WriteException(e);
		}
		doc = builder.newDocument();
		doc.setXmlStandalone(true);
		root = doc.createElement("root");
		root.setAttribute("version", "2.0");
		doc.appendChild(root);

		Element document = doc.createElement("document");
		Element docInfo = doc.createElement("docInfo");
		CDATASection cdatadocInfo = null;
		try {
			cdatadocInfo = doc.createCDATASection(Common.base64Encode(getDocInfo(strTitle, strFileMd5)));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		docInfo.appendChild(cdatadocInfo);
		document.appendChild(docInfo);
		Element length = doc.createElement("length");
		length.setTextContent(String.valueOf(strFileSize));
		document.appendChild(length);
		Element filename = doc.createElement("filename");
		// filename.setTextContent(strFileID.concat(".").concat(strFileType));
		filename.setTextContent(strTitle);
		document.appendChild(filename);
		root.appendChild(document);

		Element server = doc.createElement("server");
		Element cache = doc.createElement("cache");
		Element type = doc.createElement("type");
		type.setAttribute("validPeriod", "longtime");
		type.setTextContent("file");
		cache.appendChild(type);
		Element preparse = doc.createElement("preparse");
		String strPreparse = Common.GetConfig("Preparse");
		if (strPreparse == null || strPreparse == "")
			strPreparse = "1";
		preparse.setTextContent(strPreparse);
		cache.appendChild(preparse);
		server.appendChild(cache);

		Element cluster = doc.createElement("cluster");
		String strThreadsCount = Common.GetConfig("Threads");
		if (strThreadsCount == null) {
			strThreadsCount = "2";
		}
		Element threads = doc.createElement("threads");
		threads.setTextContent(strThreadsCount);
		cluster.appendChild(threads);

		String strDownServer = Common.GetConfig("DownServer");
		String curTime = String.valueOf(System.currentTimeMillis());
		String strUrl = strDownServer.concat("/kfdreadexdownload/getfile?op=downloads&usertoken=")
				.concat(URLEncoder.encode(strToken, "utf-8")).concat("&fileid=").concat(strFileID).concat("&typeid=")
				.concat(typeID).concat("&filename=").concat(URLEncoder.encode(strFileName, "utf-8"))
				.concat("&filetype=").concat(fileType).concat("&path=").concat(URLEncoder.encode(path, "utf-8"))
				.concat("&expire=").concat(curTime).concat("&sign=").concat(Common.SHA1(curTime.concat("cnkiexpress")));
		Element url = doc.createElement("url");
		url.setAttribute("pri", "10");
		CDATASection cdataurl = doc.createCDATASection(strUrl);
		url.appendChild(cdataurl);
		cluster.appendChild(url);

		Element urlbak = doc.createElement("url");
		urlbak.setAttribute("pri", "9");
		CDATASection cdataurlbak = doc.createCDATASection(strUrl);
		urlbak.appendChild(cdataurlbak);
		cluster.appendChild(urlbak);

		server.appendChild(cluster);
		root.appendChild(server);

		return prettyPrint(doc, "utf-8").getBytes("utf-8");
	}

	private String getDocInfo(String FileName, String FileMd5) {
		Document doc = null;
		Element root = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Logger.WriteException(e);
		}
		doc = builder.newDocument();
		doc.setXmlStandalone(true);
		root = doc.createElement("root");
		doc.appendChild(root);

		Element docInfo = doc.createElement("docInfo");
		Element title = doc.createElement("title");
		title.setTextContent(FileName);
		docInfo.appendChild(title);
		Element author = doc.createElement("author");
		docInfo.appendChild(author);
		Element publisher = doc.createElement("publisher");
		docInfo.appendChild(publisher);
		Element filemd5 = doc.createElement("filemd5"); // 此时filemd5未赋值
		filemd5.setTextContent(FileMd5);
		docInfo.appendChild(filemd5);
		root.appendChild(docInfo);
		Element display = doc.createElement("display");
		Element locate = doc.createElement("locate");
		locate.setTextContent("1");
		display.appendChild(locate);
		Element pageMode = doc.createElement("pageMode");
		pageMode.setTextContent("0");
		display.appendChild(pageMode);
		Element IsZoomIn = doc.createElement("IsZoomIn");
		IsZoomIn.setTextContent("1");
		display.appendChild(IsZoomIn);
		root.appendChild(display);

		return prettyPrint(doc, "utf-8");
	}

	private static String prettyPrint(Document xml, String strEncoding) {

		Transformer tf = null;
		try {
			tf = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			Logger.WriteException(e);
		}
		tf.setOutputProperty(OutputKeys.ENCODING, strEncoding);
		tf.setOutputProperty(OutputKeys.INDENT, "no");
		Writer out = new StringWriter();
		try {
			tf.transform(new DOMSource(xml), new StreamResult(out));
		} catch (TransformerException e) {
			Logger.WriteException(e);
		}
		return out.toString();
	}

	private String kfdReadexDownload(Map<String, Object> arg, String UserName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String range = (String) arg.get("range");
		String reqUrl = (String) arg.get("path");
		if (Common.IsNullOrEmpty(range) && request.getHeader("range") != null) {
			range = request.getHeader("Range");

		}
		String token = getRedisEcpToken();
		// String reqUrl =
		// "http://192.168.106.70/KFDRes/api/readerEx_download?filePath=D:\\KFD\\CacheFiles\\CAJ\\CJFD\\2015\\F\\FJKS\\FJKS201504003_1-5.caj";

		URL url = new URL(reqUrl);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setRequestProperty("Authorization", "Bearer ".concat(token));
		http.setRequestProperty("Range", range);
		http.setRequestMethod("GET");
		http.setDefaultUseCaches(false);
		http.setDoInput(true);
		http.setDoOutput(true);
		http.connect();
		if (http.getResponseCode() != 200) {
			System.out.println(http.getResponseCode());
		}
		OutputStream os = response.getOutputStream();
		InputStream input = http.getInputStream();
		// long length = http.getContentLength();
		byte[] inputData = new byte[10240];
		int iread = 0;
		while ((iread = input.read(inputData)) > 0) {
			os.write(inputData, 0, iread);
			os.flush();
		}
		os.close();
		return null;
	}

	/**
	 * 
	 * @param fileName
	 *            fn
	 * @param fileType
	 *            类型 caj;pdf;epub
	 * @param discNo
	 *            光盘号
	 * @param resouceType
	 *            资源类型：CJFD
	 * @param page
	 *            页码范围
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private Map<String, Object> kfdresReaderExfileinfo(String fileName, String fileType, String discNo,
			String resouceType, String page, boolean prior) throws IOException, SAXException, ParserConfigurationException {
		String token = getRedisEcpToken();
		Map<String, Object> xmlMap = new HashMap<String, Object>();
		String kfdresUrl="";
		if(prior){ 
			kfdresUrl=Common.GetConfig("capjKfdresUrl"); //优先地址
		}else{
			kfdresUrl=Common.GetConfig("kfdresUrl");  //普通服务器地址
		}
		String reqUrl = kfdresUrl+"/KFDRes/api/readerEx_getfileinfo?";
		String param = "fileName=" + fileName + "&discNo=" + discNo + "&resourceType=" + resouceType + "&filetype="
				+ fileType + "&page=" + page;
		URL url = new URL(reqUrl + param);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setRequestProperty("Authorization", "Bearer ".concat(token));
		http.setRequestMethod("GET");
		http.setDefaultUseCaches(false);
		http.setDoInput(true);
		http.setDoOutput(true);

		http.connect();
		if (http.getResponseCode() != 200) {
			System.out.println(http.getResponseCode());
		}
		InputStream input = http.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(input, "GBK"));
		StringBuilder content = new StringBuilder();
		String sline = "";
		while ((sline = br.readLine()) != null) {
			content.append(sline);
		}

		// 把要解析的xml文档读入DOM解析器
		org.dom4j.Document doc = null;
		try {
			doc = DocumentHelper.parseText(content.toString());
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.dom4j.Element root = doc.getRootElement();
		iterator(xmlMap, root);
		return xmlMap;

	}

	public static void iterator(Map<String, Object> map, org.dom4j.Element ele) {
		Iterator<org.dom4j.Element> iterator = ele.elementIterator();
		while (iterator.hasNext()) {
			org.dom4j.Element next = iterator.next();
			String nextName = next.getName();
			Iterator iterator2 = next.elementIterator();
			if (iterator2.hasNext()) {
				// Map<String, Object> map2 = new HashMap<String, Object>();
				iterator(map, next);
				// map.put(nextName, map2);
			} else {
				String text = next.getText();
				map.put(nextName, text);
				/*
				 * Iterator<org.dom4j.Attribute> attributeIterator =
				 * next.attributeIterator(); while(attributeIterator.hasNext())
				 * { org.dom4j.Attribute attribute = attributeIterator.next();
				 * String name = attribute.getName(); String value =
				 * attribute.getValue(); //map.put(name, value); }
				 */
			}
		}
	}

	private static String getRedisEcpToken() {
		RedisUtil redisUtil = new RedisUtil();
		String token = (String) redisUtil.getObject("kfdres");
		if (Common.IsNullOrEmpty(token)) {
			token = getEcpToken();
			redisUtil.setObject("kfdres", token, 30);
		}
		return token;
	}

	public static String getEcpToken() {
		// 内网测试服务器
		String kfdUrl = "http://192.168.106.70/KFDAuth/token";
		String client_id = "80908";
		String client_secret = "3dVXdCpCO7NE1gZl1xTnNtKP9rqFQS-SqSh8OfjexrU";
		// 中心网站服务器
		// String ecpUrl = "http://10.1.112.21/KFDAuth/token";
		// String client_id = "92227";
		// String client_secret = "frSAP0a_nZcaSnl0TdqT1mecn5RqCm9Sb3tftnYU40o";
		String strParam = "grant_type=client_credentials&client_id=".concat(client_id).concat("&client_secret=")
				.concat(client_secret);
		JSONObject json = getServerResult(kfdUrl, null, strParam, null, null);
		return json.getString("access_token");
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

}
