package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.CnkiMngr;
import BLL.RecommendationInfoMngr;
import BLL.SourceMngr;
import Model.UserLoginBean;
import Util.Common;
import Util.SHA1;

/**
 * Servlet implementation class RecommendSearchListHandlerServlet
 */
@WebServlet("/RecommendSearchListHandler.do")
public class RecommendSearchListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static String ODataUrl = Common.GetConfig("newODataUrl");// "http://192.168.100.198:8010/";
	static String appId = Common.GetConfig("newAppId");
	static String appKey = Common.GetConfig("newAppKey");
	static String did = "{123456}";
	static String mobile = "";
	static String location = "0,0";
	static String ip = "127.0.0.1";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RecommendSearchListHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSearchData(request);
		} else if ("commend".equals(request.getParameter("do"))) {
			strResult = recommendArticles(request);
		} else if ("discommend".equals(request.getParameter("do"))) {
			strResult = disRecommendArticles(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected String getSearchData(HttpServletRequest request) throws ServletException, IOException {

		int iStart = Integer.parseInt(request.getParameter("start"));
		String strTypeID = request.getParameter("typeid");

		List<Map<String, Object>> lstType = SourceMngr.getSourceType(strTypeID);
		if (lstType == null) {
			return "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
		}
		
		String strNewODataName = String.valueOf(lstType.get(0).get("nodataname"));
		String strTypeEN = String.valueOf(lstType.get(0).get("name_en"));

		StringBuilder sbSeaFilter = new StringBuilder();
		String strSeaVal = request.getParameter("keyword");
		if (!Common.IsNullOrEmpty(strSeaVal)) {
			String strFiltField = request.getParameter("filter");
			sbSeaFilter.append(strFiltField).append(" eq '").append(strSeaVal).append("'");
		}
		String query = sbSeaFilter.toString();
		sbSeaFilter = null;
		// 如果不存在，返回为空，没办法显示
		List<Map<String, Object>> lstDisplayField = SourceMngr.getDisplayField(strTypeID);
		if (lstDisplayField == null) {
			return "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
		}
		String reqUrl = ODataUrl + "api/db/" + URLEncoder.encode(strNewODataName, "utf-8");
		StringBuilder sbSeaField = new StringBuilder();
		List<String> arrSeaField = new ArrayList<String>();
		Map<String, String> mapSeaField = new HashMap<String, String>();
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<table width=\"100%\" name=\"tabarticle\" id=\"tabarticle\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		//sbSeaField.append("fields=");
		for (Map<String, Object> imap : lstDisplayField) {
			sbSeaField.append(String.valueOf(imap.get("name_en"))).append(",");
			arrSeaField.add(String.valueOf(imap.get("name_en")));
			mapSeaField.put(String.valueOf(imap.get("name_en")), "");
			sbHtml.append("<th>").append(String.valueOf(imap.get("name_ch"))).append("</th>");
		}
		sbHtml.append("<th width=\"50\">推荐</th>");
		sbHtml.append("</tr>");
		if (sbSeaField.length() > 0) {
			sbSeaField.delete(sbSeaField.length() - 1, sbSeaField.length());
		}
		if (sbSeaField.length() > 0) {
			sbSeaField.append(",tablename,FileName");
			reqUrl = reqUrl + "?fields=" + sbSeaField.toString();
		}
		String group = "";
		/*if("YNKX_CACB".equals(strTypeEN)){
			group="BookNo";
		}*/
		String order = "";
		reqUrl = reqUrl + "&query=" + URLEncoder.encode(query, "utf-8") + "&group="+group+"&order=&start=" + String.valueOf(iStart - 1) + "&length=" + String.valueOf(20);
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String txt = "timestamp=" + timestamp + "&appid=" + appId + "&appkey=" + appKey + "&ip=" + ip + "&location=" + location + "&mobile=" + mobile + "&did=" + did + "&op=data_gets&type=" + strNewODataName + "&fields=" + sbSeaField.toString() + "&query=" + query + "&group=" + group + "&order=" + order;
		SHA1 sha1 = new SHA1();
		String sign = sha1.Digest(txt, "UTF-8");
		Map<String, String> mapHeader = new TreeMap<String, String>();
		mapHeader.put("ip", ip);
		mapHeader.put("app_id", appId);
		mapHeader.put("did", did);
		mapHeader.put("mobile", mobile);
		mapHeader.put("location", location);
		mapHeader.put("timestamp", timestamp);
		mapHeader.put("sign", sign.toLowerCase());
		
		JSONObject JsonSeaData = CnkiMngr.sendGet(reqUrl, mapHeader);

		if (JsonSeaData == null || JsonSeaData.get("Count") == null || (Integer) JsonSeaData.get("Count") == 0) {
			return "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
		}
		List<String> lstRecomdInfo = RecommendationInfoMngr.getRecommendationFileID(strTypeID);
		String strRecordCount = String.valueOf(JsonSeaData.get("Count"));
		JSONArray jsonArray = JSONArray.fromObject(JsonSeaData.get("Rows"));
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject recordObj = JSONObject.fromObject(jsonArray.get(i));	
			String rowsId= (String) recordObj.get("Id");
			JSONArray subJsonAry = JSONArray.fromObject(recordObj.get("Cells"));
			String strInstance = "";
			boolean bFlag = false;
			for (int j = 0; j < subJsonAry.size(); j++) {
				JSONObject subRecObj = JSONObject.fromObject(subJsonAry.get(j));
				mapSeaField.put(String.valueOf(subRecObj.get("Name")), String.valueOf(subRecObj.get("Value")).replace("#", "").replace("$", ""));
				if(strTypeEN.equals("journalinfo")){
					if(String.valueOf(subRecObj.get("Name")).toLowerCase().equals("id"))
						strInstance = String.valueOf(subRecObj.get("Value"));
				}else if(strTypeEN.equals("YNKX_CACM")||strTypeEN.equals("YNKX_CACV")||strTypeEN.equals("YNKX_BOOKINFO")||strTypeEN.equals("YNKX_PICINDEX")){
					strInstance=rowsId;
				}else{
					if(String.valueOf(subRecObj.get("Name")).toLowerCase().equals("filename"))
						strInstance = String.valueOf(subRecObj.get("Value"));
				}
			}
			if (lstRecomdInfo != null) {
				bFlag = lstRecomdInfo.contains(strInstance);
				}
			String tablename = mapSeaField.get("TableName");

			String strDetailUrl = Common.GetConfig("RecommendDetailUrl");
			if (strDetailUrl == null) {
				strDetailUrl = "http://192.168.100.122/KCMS/detail/detail.aspx";
			}
			if (strTypeEN.equals("journalinfo")) {
				strDetailUrl = "JournalInfo.do?jpy=".concat(strInstance).concat("&vtypeid=").concat(strTypeID);
			}else if(strTypeEN.equals("YNKX_CACM")||strTypeEN.equals("YNKX_CACV")||strTypeEN.equals("YNKX_BOOKINFO")||strTypeEN.equals("YNKX_PICINDEX")){
				strDetailUrl = "OrgInfo.do?opy=".concat(rowsId).concat("&vtypeid=").concat(strTypeID).concat("&sourceType=").concat(strTypeEN);
			}/*else if(strTypeEN.equals("YNKX_BOOKINFO")){
				strDetailUrl = "OrgInfo.do?opy=".concat(rowsId).concat("&vtypeid=").concat(strTypeID).concat("&sourceType=").concat(strTypeEN).concat("&bookNo=").concat(strInstance);
			}*/else {
				strDetailUrl = strDetailUrl.concat("?dbcode=".concat(tablename.substring(0, 4)).concat("&dbname=").concat(tablename).concat("&filename=").concat(strInstance));// "ODataDetail.do?type=".concat("journals").concat("&fileid=");
			}
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chkfileid\" type=\"checkbox\" value=\"").append(strInstance).append("\"></td>");
			sbHtml.append("<td class=\"num\">").append(iStart++).append("<input type=\"hidden\" name=\"hidtablename\" value=\"").append(tablename == null ? "" : tablename).append("\"/></td>");
			if (arrSeaField.size() > 0) {
				for (int k = 0; k < arrSeaField.size(); k++) {
					String strFieldName = arrSeaField.get(k);
					if (k == 0) {
						if (strTypeEN.equals("journalinfo")) {
							if (bFlag) {
								sbHtml.append("<td><a href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.toLowerCase()).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已推荐</em></a></td>");
							} else {
								sbHtml.append("<td><a href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.toLowerCase()).append("'>").append(mapSeaField.get(strFieldName)).append("</span></a></td>");
							}
						}else if(strTypeEN.equals("YNKX_CACM")||strTypeEN.equals("YNKX_CACV")||strTypeEN.equals("YNKX_BOOKINFO")||strTypeEN.equals("YNKX_PICINDEX")){
							if (bFlag) {
								sbHtml.append("<td><a href=\"").append(strDetailUrl).append("\"><span name='title'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已推荐</em></a></td>");
							} else {
								sbHtml.append("<td><a href=\"").append(strDetailUrl).append("\"><span name='title'>").append(mapSeaField.get(strFieldName)).append("</span></a></td>");
							}
						} else {
							if (bFlag) {
								sbHtml.append("<td><a target=\"_blank\" href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.toLowerCase()).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已推荐</em></a></td>");
							} else {
								sbHtml.append("<td><a target=\"_blank\" href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.toLowerCase()).append("'>").append(mapSeaField.get(strFieldName)).append("</span></a></td>");
							}
						}
					} else {
						// sbHtml.append("<td><span name='").append(strFieldName.substring(strFieldName.indexOf(":")
						// +
						// 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span></td>");
						sbHtml.append("<td>").append(mapSeaField.get(strFieldName)).append("</td>");
					}
				}
			}
			if (bFlag) {
				sbHtml.append("<td class=\"tabopt\"><a class=\"discommend\" title=\"取消推荐\" href=\"javascript:void(0);\" onclick=\"DisRemdArticle(this,'").append(strTypeID).append("','").append(strInstance).append("',0)\"></a></td>");
			} else {
				sbHtml.append("<td class=\"tabopt\"><a title=\"推荐\" class=\"commend\" href=\"javascript:void(0);\" onclick=\"RemdArticle(this,'").append(strTypeID).append("','").append(strInstance).append("',0)\"></a></td>");
			}
			sbHtml.append("</tr>");
		}
		
		
		

//		for (int i = 0; i < jsonArray.size(); i++) {
//			JSONObject recordObj = JSONObject.fromObject(jsonArray.get(i));
//			String strInstance = String.valueOf(recordObj.get("instance"));
//			strInstance = strInstance.substring(strInstance.indexOf(":") + 1);
//			boolean bFlag = false;
//			if (lstRecomdInfo != null) {
//				bFlag = lstRecomdInfo.contains(strInstance);
//			}
//			JSONArray recordArray = JSONArray.fromObject(recordObj.get("data"));
//
//			for (int j = 0; j < recordArray.size(); j++) {
//				JSONObject detailObj = recordArray.getJSONObject(j);
//
//				// String strKey=String.valueOf(detailObj.get("rdfProperty"));
//				// String strVal=String.valueOf(detailObj.get("value"));
//				//
//				// if(mapSeaField.containsKey(strKey)){
//				// if(!Common.IsNullOrEmpty(strVal)){
//				// mapSeaField.put(strKey,mapSeaField.get(strKey)+","+strVal);
//				// }
//				// }else{
//				// mapSeaField.put(strKey,strVal);
//				// }
//
//				// if(mapSeaField.containsKey(String.valueOf(detailObj.get("rdfProperty")))){
//				// mapSeaField.put(String.valueOf(detailObj.get("rdfProperty")),mapSeaField.get(String.valueOf(detailObj.get("value")))+"~~"+String.valueOf(detailObj.get("value")));
//				// }else{
//				// mapSeaField.put(String.valueOf(detailObj.get("rdfProperty")),String.valueOf(detailObj.get("value")));
//				// }
//				mapSeaField.put(String.valueOf(detailObj.get("rdfProperty")), String.valueOf(detailObj.get("value")));
//			}
//			String tablename = mapSeaField.get("cnki:tablename");
//
//			String strDetailUrl = Common.GetConfig("RecommendDetailUrl");
//			if (strDetailUrl == null) {
//				strDetailUrl = "http://192.168.100.122/KCMS/detail/detail.aspx";
//			}
//			if (strTypeEN.equals("journalinfo")) {
//				strDetailUrl = "JournalInfo.do?jpy=".concat(strInstance).concat("&vtypeid=").concat(strTypeID);
//			} else {
//				strDetailUrl = strDetailUrl.concat("?dbcode=".concat(tablename.substring(0, 4)).concat("&dbname=").concat(tablename).concat("&filename=").concat(strInstance));// "ODataDetail.do?type=".concat("journals").concat("&fileid=");
//			}
//			sbHtml.append("<tr>");
//			sbHtml.append("<td><input name=\"chkfileid\" type=\"checkbox\" value=\"").append(strInstance).append("\"></td>");
//			sbHtml.append("<td class=\"num\">").append(iStart++).append("<input type=\"hidden\" name=\"hidtablename\" value=\"").append(tablename == null ? "" : tablename).append("\"/></td>");
//			if (arrSeaField.size() > 0) {
//				for (int k = 0; k < arrSeaField.size(); k++) {
//					String strFieldName = arrSeaField.get(k);
//					if (k == 0) {
//						if (strTypeEN.equals("journalinfo")) {
//							if (bFlag) {
//								sbHtml.append("<td><a href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已推荐</em></a></td>");
//							} else {
//								sbHtml.append("<td><a href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span></a></td>");
//							}
//						} else {
//							if (bFlag) {
//								sbHtml.append("<td><a target=\"_blank\" href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已推荐</em></a></td>");
//							} else {
//								sbHtml.append("<td><a target=\"_blank\" href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span></a></td>");
//							}
//						}
//					} else {
//						// sbHtml.append("<td><span name='").append(strFieldName.substring(strFieldName.indexOf(":")
//						// +
//						// 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span></td>");
//						sbHtml.append("<td>").append(mapSeaField.get(strFieldName)).append("</td>");
//					}
//				}
//			}
//			if (bFlag) {
//				sbHtml.append("<td class=\"tabopt\"><a class=\"discommend\" title=\"取消推荐\" href=\"javascript:void(0);\" onclick=\"DisRemdArticle(this,'").append(strTypeID).append("','").append(strInstance).append("',0)\"></a></td>");
//			} else {
//				sbHtml.append("<td class=\"tabopt\"><a title=\"推荐\" class=\"commend\" href=\"javascript:void(0);\" onclick=\"RemdArticle(this,'").append(strTypeID).append("','").append(strInstance).append("',0)\"></a></td>");
//			}
//			sbHtml.append("</tr>");
//		}
		sbHtml.append("</table><input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"").append(strRecordCount).append("\" />");// <input
																																			// type=\"hidden\" id=\"hidtypech\" name=\"hidtypech\" value=\"".concat(strTypeCH).concat("\" />"));

		return sbHtml.toString();
	}

	// protected String getSearchData(HttpServletRequest request) throws
	// ServletException, IOException {
	//
	// String strUrl = Common.GetConfig("ODataServer");
	// //"http://api.cnki.net/data/";
	// int iStart = Integer.parseInt(request.getParameter("start"));
	// String strTypeID = request.getParameter("typeid");
	//
	// // String strType=request.getParameter("type");
	// List<Map<String, Object>> lstType = SourceMngr.getSourceType(strTypeID);
	// if (lstType == null) {
	// return
	// "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
	// }
	// String strTypeEN = String.valueOf(lstType.get(0).get("name_en"));
	// // String strTypeCH = String.valueOf(lstType.get(0).get("name_ch"));
	//
	// strUrl = strUrl.concat("/data/").concat(strTypeEN).concat("?");
	// //strUrl = strUrl.concat("literatures").concat("?");
	//
	// StringBuilder sbSeaFilter = new StringBuilder();
	//
	// String strSeaVal = request.getParameter("keyword");
	//
	// if (!Common.IsNullOrEmpty(strSeaVal)) {
	// String strFiltField = request.getParameter("filter");
	// strSeaVal = URLEncoder.encode(strSeaVal.toString(), "utf-8");
	// sbSeaFilter.append("filter=").append(strFiltField).append("%20like%20").append(strSeaVal);
	// }
	//
	// // 如果不存在，返回为空，没办法显示
	// List<Map<String, Object>> lstDisplayField =
	// SourceMngr.getDisplayField(strTypeID);
	// if (lstDisplayField == null) {
	// return
	// "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
	// }
	//
	// StringBuilder sbSeaField = new StringBuilder();
	// List<String> arrSeaField = new ArrayList<String>();
	// Map<String, String> mapSeaField = new HashMap<String, String>();
	// StringBuilder sbHtml = new StringBuilder();
	// sbHtml.append("<table width=\"100%\" name=\"tabarticle\" id=\"tabarticle\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
	// sbHtml.append("<tr>");
	// sbHtml.append("<th width=\"20\" >&nbsp;</th>");
	// sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
	// sbSeaField.append("fields=");
	// for (Map<String, Object> imap : lstDisplayField) {
	// sbSeaField.append(String.valueOf(imap.get("name_en"))).append(",");
	// arrSeaField.add(String.valueOf(imap.get("name_en")));
	// mapSeaField.put(String.valueOf(imap.get("name_en")), "");
	// sbHtml.append("<th>").append(String.valueOf(imap.get("name_ch"))).append("</th>");
	// }
	// sbHtml.append("<th width=\"50\">推荐</th>");
	// sbHtml.append("</tr>");
	// if (sbSeaField.length() > 0) {
	// sbSeaField.delete(sbSeaField.length() - 1, sbSeaField.length());
	// }
	// if (sbSeaField.length() > 0) {
	// strUrl = strUrl.concat(sbSeaField.toString()).concat(",cnki:tablename&");
	// }
	// if (sbSeaFilter.length() > 0) {
	// strUrl = strUrl.concat(sbSeaFilter.toString()).concat("&");
	// }
	// strUrl = strUrl.concat("start=").concat(String.valueOf((iStart - 1)));
	//
	// // 获取用户Token
	// JSONObject jsonSeaData = Common.getSearchData(strUrl,
	// AppToken.getAppToken());
	// if ("0".equals(String.valueOf(jsonSeaData.get("recordCount")))) {
	// return
	// "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
	// }
	//
	// List<String> lstRecomdInfo =
	// RecommendationInfoMngr.getRecommendationFileID(strTypeID);
	//
	// String strRecordCount = String.valueOf(jsonSeaData.get("recordCount"));
	//
	// JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("store"));
	// for (int i = 0; i < jsonArray.size(); i++) {
	// JSONObject recordObj = JSONObject.fromObject(jsonArray.get(i));
	// String strInstance = String.valueOf(recordObj.get("instance"));
	// strInstance = strInstance.substring(strInstance.indexOf(":") + 1);
	// boolean bFlag = false;
	// if (lstRecomdInfo != null) {
	// bFlag = lstRecomdInfo.contains(strInstance);
	// }
	// JSONArray recordArray = JSONArray.fromObject(recordObj.get("data"));
	//
	// for (int j = 0; j < recordArray.size(); j++) {
	// JSONObject detailObj = recordArray.getJSONObject(j);
	//
	// // String strKey=String.valueOf(detailObj.get("rdfProperty"));
	// // String strVal=String.valueOf(detailObj.get("value"));
	// //
	// // if(mapSeaField.containsKey(strKey)){
	// // if(!Common.IsNullOrEmpty(strVal)){
	// // mapSeaField.put(strKey,mapSeaField.get(strKey)+","+strVal);
	// // }
	// // }else{
	// // mapSeaField.put(strKey,strVal);
	// // }
	//
	// //
	// if(mapSeaField.containsKey(String.valueOf(detailObj.get("rdfProperty")))){
	// //
	// mapSeaField.put(String.valueOf(detailObj.get("rdfProperty")),mapSeaField.get(String.valueOf(detailObj.get("value")))+"~~"+String.valueOf(detailObj.get("value")));
	// // }else{
	// //
	// mapSeaField.put(String.valueOf(detailObj.get("rdfProperty")),String.valueOf(detailObj.get("value")));
	// // }
	// mapSeaField.put(String.valueOf(detailObj.get("rdfProperty")),
	// String.valueOf(detailObj.get("value")));
	// }
	// String tablename = mapSeaField.get("cnki:tablename");
	//
	// String strDetailUrl = Common.GetConfig("RecommendDetailUrl");
	// if (strDetailUrl == null) {
	// strDetailUrl = "http://192.168.100.122/KCMS/detail/detail.aspx";
	// }
	// if (strTypeEN.equals("journalinfo")) {
	// strDetailUrl =
	// "JournalInfo.do?jpy=".concat(strInstance).concat("&vtypeid=").concat(strTypeID);
	// } else {
	// strDetailUrl =
	// strDetailUrl.concat("?dbcode=".concat(tablename.substring(0,
	// 4)).concat("&dbname=").concat(tablename).concat("&filename=").concat(strInstance));//
	// "ODataDetail.do?type=".concat("journals").concat("&fileid=");
	// }
	// sbHtml.append("<tr>");
	// sbHtml.append("<td><input name=\"chkfileid\" type=\"checkbox\" value=\"").append(strInstance).append("\"></td>");
	// sbHtml.append("<td class=\"num\">").append(iStart++).append("<input type=\"hidden\" name=\"hidtablename\" value=\"").append(tablename
	// == null ? "" : tablename).append("\"/></td>");
	// if (arrSeaField.size() > 0) {
	// for (int k = 0; k < arrSeaField.size(); k++) {
	// String strFieldName = arrSeaField.get(k);
	// if (k == 0) {
	// if (strTypeEN.equals("journalinfo")) {
	// if (bFlag) {
	// sbHtml.append("<td><a href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":")
	// +
	// 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已推荐</em></a></td>");
	// } else {
	// sbHtml.append("<td><a href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":")
	// +
	// 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span></a></td>");
	// }
	// } else {
	// if (bFlag) {
	// sbHtml.append("<td><a target=\"_blank\" href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":")
	// +
	// 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已推荐</em></a></td>");
	// } else {
	// sbHtml.append("<td><a target=\"_blank\" href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":")
	// +
	// 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span></a></td>");
	// }
	// }
	// } else {
	// //
	// sbHtml.append("<td><span name='").append(strFieldName.substring(strFieldName.indexOf(":")
	// // +
	// //
	// 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span></td>");
	// sbHtml.append("<td>").append(mapSeaField.get(strFieldName)).append("</td>");
	// }
	// }
	// }
	// if (bFlag) {
	// sbHtml.append("<td class=\"tabopt\"><a class=\"discommend\" title=\"取消推荐\" href=\"javascript:void(0);\" onclick=\"DisRemdArticle(this,'").append(strTypeID).append("','").append(strInstance).append("',0)\"></a></td>");
	// } else {
	// sbHtml.append("<td class=\"tabopt\"><a title=\"推荐\" class=\"commend\" href=\"javascript:void(0);\" onclick=\"RemdArticle(this,'").append(strTypeID).append("','").append(strInstance).append("',0)\"></a></td>");
	// }
	// sbHtml.append("</tr>");
	// }
	// sbHtml.append("</table><input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"").append(strRecordCount).append("\" />");//
	// <input
	// //
	// type=\"hidden\" id=\"hidtypech\" name=\"hidtypech\" value=\"".concat(strTypeCH).concat("\" />"));
	//
	// return sbHtml.toString();
	// }

	private String recommendArticles(HttpServletRequest request) throws ServletException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strType = request.getParameter("type");
		String strFileID = request.getParameter("fileid");
		StringBuilder sbFileID = new StringBuilder();
		JSONObject jsonObj = JSONObject.fromObject(strFileID);
		JSONArray recordArray = JSONArray.fromObject(jsonObj.get("jsondata"));
		List<Map<String, String>> lstArticle = new ArrayList<Map<String, String>>();
		Map<String, String> mapArticle = null;
		for (int i = 0; i < recordArray.size(); i++) {
			mapArticle = new HashMap<String, String>();
			mapArticle.put("rid", String.valueOf(recordArray.getJSONObject(i).get("rid")));
			mapArticle.put("rval", String.valueOf(recordArray.getJSONObject(i).get("rval")));
			mapArticle.put("rtab", String.valueOf(recordArray.getJSONObject(i).get("rtab")));
			lstArticle.add(mapArticle);
			sbFileID.append(String.valueOf(recordArray.getJSONObject(i).get("rid"))).append(",");

		}
		if (Common.IsNullOrEmpty(strType) || sbFileID.length() == 0) {
			return "0";
		}

		if (RecommendationInfoMngr.recommendArticles(appid, strType, sbFileID.delete(sbFileID.length() - 1, sbFileID.length()).toString(), lstArticle)) {
			return "1";
		} else {
			return "0";
		}
	}

	private String disRecommendArticles(HttpServletRequest request) throws ServletException {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strType = request.getParameter("type");
		String strFileID = request.getParameter("fileid");

		if (Common.IsNullOrEmpty(strType) || Common.IsNullOrEmpty(strFileID)) {
			return "0";
		}
		strFileID = Common.Trim(strFileID, ",");
		if (RecommendationInfoMngr.disRecommendArticles(appid, strType, strFileID)) {
			return "1";
		} else {
			return "0";
		}
	}

}
