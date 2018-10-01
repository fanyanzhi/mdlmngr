package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.CommentMngr;
import BLL.Logger;
import BLL.SourceMngr;
import BLL.UserInfoMngr;
import Util.Common;

/**
 * Servlet implementation class CommentSearchListHandlerServlet
 */
@WebServlet("/CommentSearchListHandler.do")
public class CommentSearchListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommentSearchListHandlerServlet() {
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
		} else if ("close".equals(request.getParameter("do"))) {
			strResult = closeArticlesComment(request);
		} else if ("open".equals(request.getParameter("do"))) {
			strResult = openArticlesComment(request);
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

		String strUrl = "http://api.cnki.net/data/";
		int iStart = Integer.parseInt(request.getParameter("start"));
		String strTypeID = request.getParameter("typeid");

		List<Map<String, Object>> lstType = SourceMngr.getSourceType(strTypeID);
		if (lstType == null) {
			return "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
		}
		String strTypeEN = String.valueOf(lstType.get(0).get("name_en"));

		strUrl = strUrl.concat(strTypeEN).concat("?");

		StringBuilder sbSeaFilter = new StringBuilder();

		String strSeaVal = request.getParameter("keyword");

		if (!Common.IsNullOrEmpty(strSeaVal)) {
			String strFiltField = request.getParameter("filter");
			sbSeaFilter.append("filter=").append(strFiltField).append("%20like%20").append(strSeaVal);
		}

		// 如果不存在，返回为空，没办法显示
		List<Map<String, Object>> lstDisplayField = SourceMngr.getDisplayField(strTypeID);
		if (lstDisplayField == null) {
			return "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
		}

		StringBuilder sbSeaField = new StringBuilder();
		List<String> arrSeaField = new ArrayList<String>();
		Map<String, String> mapSeaField = new HashMap<String, String>();
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<table width=\"100%\" name=\"tabarticle\" id=\"tabarticle\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbSeaField.append("fields=");
		for (Map<String, Object> imap : lstDisplayField) {
			sbSeaField.append(String.valueOf(imap.get("name_en"))).append(",");
			arrSeaField.add(String.valueOf(imap.get("name_en")));
			mapSeaField.put(String.valueOf(imap.get("name_en")), "");
			sbHtml.append("<th>").append(String.valueOf(imap.get("name_ch"))).append("</th>");
		}
		sbHtml.append("<th width=\"50\">操作</th>");
		sbHtml.append("</tr>");
		if (sbSeaField.length() > 0) {
			sbSeaField.delete(sbSeaField.length() - 1, sbSeaField.length());
		}
		if (sbSeaField.length() > 0) {
			strUrl = strUrl.concat(sbSeaField.toString()).concat(",cnki:tablename&");
		}
		if (sbSeaFilter.length() > 0) {
			strUrl = strUrl.concat(sbSeaFilter.toString()).concat("&");
		}
		strUrl = strUrl.concat("start=").concat(String.valueOf((iStart - 1)));

		// 获取用户Token
		String strSysToken = null;
		int iNum = 10;
		while (strSysToken == null) {
			strSysToken = UserInfoMngr.getUserToken();
			--iNum;
			if(iNum<0){
				Logger.WriteException(new Exception("获取ODataToken失败"));
				return "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
			}
		}

		JSONObject jsonSeaData = Common.getSearchData(strUrl, strSysToken);
		if ("0".equals(String.valueOf(jsonSeaData.get("recordCount")))) {
			return "<input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"0\" />";
		}

		boolean bTypeClosed = CommentMngr.getTypeClosedStatus(strTypeID);

		List<String> lstDocuControl = null;
		if (!bTypeClosed) {
			lstDocuControl = CommentMngr.getDocuControlInfo(strTypeEN);
		}
		String strRecordCount = String.valueOf(jsonSeaData.get("recordCount"));

		JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("store"));
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject recordObj = JSONObject.fromObject(jsonArray.get(i));
			String strInstance = String.valueOf(recordObj.get("instance"));
			strInstance = strInstance.substring(strInstance.indexOf(":") + 1);

			boolean bFlag = false;

			if (!bTypeClosed) {
				if (lstDocuControl != null) {
					bFlag = lstDocuControl.contains(strInstance);
				}
			}
			JSONArray recordArray = JSONArray.fromObject(recordObj.get("data"));

			for (int j = 0; j < recordArray.size(); j++) {
				JSONObject detailObj = recordArray.getJSONObject(j);
				mapSeaField.put(String.valueOf(detailObj.get("rdfProperty")), String.valueOf(detailObj.get("value")));
			}
			String tablename = mapSeaField.get("cnki:tablename");

			String strDetailUrl = Common.GetConfig("RecommendDetailUrl");
			if (strDetailUrl == null) {
				strDetailUrl = "http://192.168.100.122/KCMS/detail/detail.aspx";
			}
			if (strTypeEN.equals("journalinfo")) {
				strDetailUrl = "JournalInfo.do?jpy=".concat(strInstance).concat("&vtypeid=").concat(strTypeID);
			} else {
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
							if (bTypeClosed) {
								sbHtml.append("<td><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已关闭</em></td>");
							} else {
								if (bFlag) {
									sbHtml.append("<td><a href=\"javascript:void(0);\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已关闭</em></a></td>");
								} else {
									sbHtml.append("<td><a href=\"javascript:void(0);\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span></a></td>");
								}
							}
						} else {
							if (bTypeClosed) {
								sbHtml.append("<td><a target=\"_blank\" href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已关闭</em></a></td>");
							} else {
								if (bFlag) {
									sbHtml.append("<td><a target=\"_blank\" href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span><em class=\"fine\">已关闭</em></a></td>");
								} else {
									sbHtml.append("<td><a target=\"_blank\" href=\"").append(strDetailUrl).append("\"><span name='").append(strFieldName.substring(strFieldName.indexOf(":") + 1)).append("'>").append(mapSeaField.get(strFieldName)).append("</span></a></td>");
								}
							}
						}
					} else {
						sbHtml.append("<td>").append(mapSeaField.get(strFieldName)).append("</td>");
					}
				}
			}
			if (bTypeClosed) {
				sbHtml.append("<td class=\"tabopt\"><a class=\"turnon\" title=\"类别已关闭，无法打开评论\" href=\"javascript:void(0);\"></a></td>");
			} else {
				if (bFlag) {
					sbHtml.append("<td class=\"tabopt\"><a class=\"turnon\" title=\"打开评论\" href=\"javascript:void(0);\" onclick=\"OpenArticleComment(this,'").append(strTypeEN).append("','").append(strInstance).append("',0)\"></a></td>");
				} else {
					sbHtml.append("<td class=\"tabopt\"><a title=\"关闭评论\" class=\"turnoff	\" href=\"javascript:void(0);\" onclick=\"CloseArticleComment(this,'").append(strTypeEN).append("','").append(strInstance).append("',0)\"></a></td>");
				}
			}
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table><input type=\"hidden\" id=\"hidcount\" name=\"hidcount\" value=\"").append(strRecordCount).append("\" /><input type=\"hidden\" id=\"hidTypeEn\" name=\"hidTypeEn\" value=\"").append(strTypeEN).append("\" />");// <input
																																			// type=\"hidden\" id=\"hidtypech\" name=\"hidtypech\" value=\"".concat(strTypeCH).concat("\" />"));

		return sbHtml.toString();
	}

	private String closeArticlesComment(HttpServletRequest request) throws ServletException {
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

		if (CommentMngr.closeArticlesComment(strType, sbFileID.delete(sbFileID.length() - 1, sbFileID.length()).toString(), lstArticle)) {
			return "1";
		} else {
			return "0";
		}
	}

	private String openArticlesComment(HttpServletRequest request) throws ServletException {
		String strType = request.getParameter("type");
		String strFileID = request.getParameter("fileid");
		
		if (Common.IsNullOrEmpty(strType) || Common.IsNullOrEmpty(strFileID)) {
			return "0";
		}
		strFileID = Common.Trim(strFileID, ",");
		if (CommentMngr.openArticlesComment(strType, strFileID)) {
			return "1";
		} else {
			return "0";
		}
	}

}
