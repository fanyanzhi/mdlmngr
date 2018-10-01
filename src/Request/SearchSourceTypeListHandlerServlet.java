package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.SearchSourceMngr;
import BLL.SourceMngr;
import Util.Common;

@WebServlet("/SearchSourceTypeListHandler.do")
public class SearchSourceTypeListHandlerServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	public SearchSourceTypeListHandlerServlet() {
		super();
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
			strResult = getSourceTypeList(request);
		} else if ("addremmendtype".equals(request.getParameter("do"))) {
			strResult = SearchSourceType(request);
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

	protected String getSourceTypeList(HttpServletRequest request) throws ServletException, IOException {

		List<Map<String, Object>> lstSourceType = SourceMngr.getSourceType();
		if (lstSourceType == null) {
			return "";
		}
		Iterator<Map<String, Object>> iMap = lstSourceType.iterator();
		Map<String, Object> mapData = null;
		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"30\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"100\">分类名称</th>");
		sbHtml.append("<th width=\"40%\">搜索属性</th>");
		sbHtml.append("<th width=\"35%\">显示属性</th>");
		sbHtml.append(" </tr>");
		
		List<String> lstSearchTypdID=getSearchSouceTypeList();
		boolean bContains=false;
		
		int iNum = 1;
		while (iMap.hasNext()) {
			mapData = iMap.next();
			String  strTypeID = String.valueOf(mapData.get("id"));
			if(lstSearchTypdID!=null){
				bContains=lstSearchTypdID.contains(strTypeID);
			}
			
			sbHtml.append("<tr>");
			sbHtml.append("<td><input type='checkbox' name='stypechk' value=\"").append(strTypeID).append("\"").append(bContains?"checked='true'":"").append(" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(mapData.get("name_ch"))).append("</td>");
			sbHtml.append("<td>").append(getSearchFieldHtml(strTypeID)).append("</td>");
			sbHtml.append("<td>").append(getDisplayFieldHtml(strTypeID)).append("</td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	protected String SearchSourceType(HttpServletRequest request) throws ServletException, IOException {
		String strTypeID = request.getParameter("tid");
		if (!Common.IsNullOrEmpty(strTypeID)) {
			strTypeID = Common.Trim(strTypeID, ",");
		}
		if (SearchSourceMngr.SearchSourceType(strTypeID)) {
			return "1";
		} else {
			return "0";
		}
	}

	private String getSearchFieldHtml(String TypeID) {
		List<Map<String, Object>> lstSearchField = SourceMngr.getSearchField(TypeID);
		if (lstSearchField == null) {
			return "&nbsp;";
		}
		StringBuilder sbSechFieldHtml = new StringBuilder();
		Iterator<Map<String, Object>> iterator = lstSearchField.iterator();
		Map<String, Object> imap = null;
		while (iterator.hasNext()) {
			imap = iterator.next();
			sbSechFieldHtml.append("<dfn>");
			sbSechFieldHtml.append(String.valueOf(imap.get("name_ch")));
			sbSechFieldHtml.append("</dfn>");
		}
		return sbSechFieldHtml.toString();
	}

	private String getDisplayFieldHtml(String TypeID) {
		List<Map<String, Object>> lstDisplayField = SourceMngr.getDisplayField(TypeID);
		if (lstDisplayField == null) {
			return "&nbsp;";
		}
		StringBuilder sbDisplayFieldHtml = new StringBuilder();
		Iterator<Map<String, Object>> iterator = lstDisplayField.iterator();
		Map<String, Object> imap = null;
		while (iterator.hasNext()) {
			imap = iterator.next();
			sbDisplayFieldHtml.append("<dfn>");
			sbDisplayFieldHtml.append(String.valueOf(imap.get("name_ch")));
			sbDisplayFieldHtml.append("</dfn>");
		}
		return sbDisplayFieldHtml.toString();
	}

	private List<String> getSearchSouceTypeList() {
		List<String> lstReTypdID=new ArrayList<String>();
		List<Map<String, Object>> lstSearchSouceTypeID = SearchSourceMngr.getSearchSourceTypeID();
		if(lstSearchSouceTypeID==null){
			return null;
		}
		Iterator<Map<String,Object>> iterator=lstSearchSouceTypeID.iterator();
		Map<String,Object> imap=null;
		while(iterator.hasNext()){
			imap=iterator.next();
			lstReTypdID.add(String.valueOf(imap.get("sourcedb")));
		}
		return lstReTypdID;
	}

}
