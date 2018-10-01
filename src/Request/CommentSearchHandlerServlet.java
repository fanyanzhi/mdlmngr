package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.RecommendationInfoMngr;
import BLL.SourceMngr;
import Model.HttpContext;
import Util.Common;

/**
 * Servlet implementation class CommentSearchHandlerServlet
 */
@WebServlet("/CommentSearchHandler.do")
public class CommentSearchHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommentSearchHandlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("setsearchmenu".equals(request.getParameter("do"))) {
			strResult = getSearchMenu(request);
		} else if ("getsearchfield".equals(request.getParameter("do"))) {
			strResult = getSearchField(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private String getSearchField(HttpServletRequest request) throws ServletException, IOException {

		Map<String, String> typeMap = new LinkedHashMap<String, String>();
		String strSeaField = request.getParameter("seafield");
		String strTypeID = request.getParameter("typeid");
		if (Common.IsNullOrEmpty(strTypeID)) {
			typeMap.put("暂无数据", "");
			return initDropDownList(strTypeID, typeMap, null);
		}

		List<Map<String, Object>> lstSearchField = RecommendationInfoMngr.getSearchField(strTypeID);
		if (lstSearchField == null) {
			typeMap.put("暂无数据", "");
			return initDropDownList(strTypeID, typeMap, null);
		}

		Iterator<Map<String, Object>> iterator = lstSearchField.iterator();
		Map<String, Object> iMap = null;
		while (iterator.hasNext()) {
			iMap = iterator.next();
			typeMap.put(String.valueOf(iMap.get("name_ch")), String.valueOf(iMap.get("name_en")));
		}
		return initDropDownList(strTypeID, typeMap, strSeaField);

		
	}

	private String initDropDownList(String TypeID, Map<String, String> EntryMap, String SeaField) {
		StringBuilder sbSearchField = new StringBuilder();

		Iterator<Entry<String, String>> iEntry = EntryMap.entrySet().iterator();

		sbSearchField.append("<a id=\"").append("SeachField").append("_ddl\" tille=\"").append("\" class=\"select\" ").append(">").append("</a>\r\n");
		sbSearchField.append("<span id='").append("SeachField").append("_ddlul' tabindex='0' hidefocus='true' class='hideoption'").append(">\r\n");
		String strSelectedText = "";
		String strSelectedValue = "";
		boolean bContains = false;
		if (!Common.IsNullOrEmpty(SeaField)) {
			bContains = EntryMap.containsValue(SeaField);
		}
		while (iEntry.hasNext()) {
			Entry<String, String> entryData = iEntry.next();
			String strText = entryData.getKey();
			String strValue = entryData.getValue();

			if (strSelectedText.length() == 0) {
				if (bContains) {
					if (SeaField.equals(strValue)) {
						strSelectedText = strText;
						strSelectedValue = strValue;
					}
				} else {
					strSelectedText = strText;
					strSelectedValue = strValue;
				}
			}
			sbSearchField.append("<em title=\"").append(strText).append("\"  onclick=\"drpItemSelected('").append("SeachField").append("','").append(strText).append("','").append(strValue).append("');\">").append(strText).append("</em>\r\n");
		}

		sbSearchField.append("</span>\r\n");
		sbSearchField.append("<input type=\"hidden\" id=\"").append("SeachField").append("_SelectText\" />\r\n");
		sbSearchField.append("<input type=\"hidden\" id=\"").append("SeachField").append("_SelectValue\" />\r\n");
		sbSearchField.append("<script type=\"text/javascript\" language=\"javascript\" src=\"").append(HttpContext.GetRequest().getContextPath()).append("/js/dropdonwlisttag.js\" >").append("</script>\r\n");
		sbSearchField.append("<script type=\"text/javascript\" language=\"javascript\">\r\n");
		sbSearchField.append("DrpTagInit(\"").append("SeachField").append("\",\"").append(strSelectedText).append("\",\"").append(strSelectedValue).append("\");\r\n");
		sbSearchField.append("</script>\r\n");
		return sbSearchField.toString();
	}

	private String getSearchMenu(HttpServletRequest request) throws ServletException, IOException {
		String strDefType = request.getParameter("deftype");

		List<Map<String, Object>> SourceType = SourceMngr.getSourceType();
		if (SourceType == null) {
			return "";
		}
		Iterator<Map<String, Object>> iterator = SourceType.iterator();
		Map<String, Object> imap = null;

		StringBuilder sbTypeHtml = new StringBuilder();
		boolean bFlag = true;
		if (!Common.IsNullOrEmpty(strDefType)) {
			bFlag = false;
		}
		while (iterator.hasNext()) {
			imap = iterator.next();
			if (bFlag) {
				sbTypeHtml.append("<input type=\"hidden\" id=\"hidTypeid\" name=\"hidTypeid\" value=\"").append(imap.get("id")).append("\"/>");
				sbTypeHtml.append("<em class='current' onclick=\"CheckDBTag(this,'").append(imap.get("id")).append("');\">").append(imap.get("name_ch")).append("</em>");
				bFlag = false;
			} else {
				if (!Common.IsNullOrEmpty(strDefType)) {
					if (strDefType.equals(String.valueOf(imap.get("id")))) {
						sbTypeHtml.append("<em class='current' onclick=\"CheckDBTag(this,'").append(imap.get("id")).append("');\">").append(imap.get("name_ch")).append("</em>");

					} else {
						sbTypeHtml.append("<em onclick=\"CheckDBTag(this,'").append(imap.get("id")).append("');\">").append(imap.get("name_ch")).append("</em>");
					}
				} else {
					sbTypeHtml.append("<em onclick=\"CheckDBTag(this,'").append(imap.get("id")).append("');\">").append(imap.get("name_ch")).append("</em>");
				}
			}
		}
		return sbTypeHtml.toString();
	}

}
